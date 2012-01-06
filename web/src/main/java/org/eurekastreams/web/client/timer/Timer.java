/*
 * Copyright (c) 2010-2012 Lockheed Martin Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eurekastreams.web.client.timer;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.web.client.model.Fetchable;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.TimerFactory;
import org.eurekastreams.web.client.ui.TimerHandler;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;

/**
 * Controls timer jobs going back to the server. Bundles HTTP requests for optimizations.
 * 
 */
public class Timer
{
    /** There are 60,000 milliseconds in a minute due to the theory of time. */
    private static final int MS_IN_MIN = 60000;

    /** Milliseconds in a second (according to the definitions in the metric system). */
    private static final long MS_IN_SEC = 1000;

    /** Seconds in a minute. */
    private static final long SEC_IN_MIN = 60;

    /** Poll period of the master driving timer. */
    private static final int MASTER_TIMER_POLL_MS = (int) (30 * MS_IN_SEC);

    /** Max time without user activity to consider system idle. */
    private static final long IDLE_TIMEOUT_MS = 5 * MS_IN_MIN;

    /** How close is close enough for letting a job batch run (so it doesn't have to wait for the next master timer). */
    private static final long TIMER_MERCY_SEC = 5;

    /**
     * The fetchable models.
     */
    private final HashMap<String, Fetchable> fetchables = new HashMap<String, Fetchable>();
    /**
     * The jobs.
     */
    private final HashMap<Integer, Set<String>> jobs = new HashMap<Integer, Set<String>>();

    /**
     * The requests.
     */
    private final HashMap<String, Serializable> requests = new HashMap<String, Serializable>();

    /**
     * The list of paused jobs.
     */
    private final Set<String> pausedJobs = new HashSet<String>();

    /**
     * Temp jobs. Delete these when the page changes.
     */
    private final Set<String> tempJobs = new HashSet<String>();

    /** Last time each set of jobs (i.e. 1 minute jobs, 2 minute jobs, etc.) was run. */
    private final HashMap<Integer, Date> lastRunTimes = new HashMap<Integer, Date>();

    /** Master driving timer. */
    private com.google.gwt.user.client.Timer masterTimer;

    /** If there has been activity since the last master timer poll. */
    private boolean activity = false;

    /** If the system is officially idle. */
    private boolean idle = false;

    /** Logger. */
    private Logger log;

    /**
     * Constructor.
     */
    public Timer()
    {
        // This setup is to handle unit testing. GWT Logging uses GWT.Create to set up the logging implementation,
        // however GWT.Create is not available when running unit tests. (It returns null if GWTMockUtilities.disarm is
        // called or throws an exception if not. Thus during unit tests calling LogConfiguration.loggingIsEnabled throws
        // a NullPointerException.
        try
        {
            if (LogConfiguration.loggingIsEnabled())
            {
                log = Logger.getLogger("org.eurekastreams.web.client.timer.Timer");
            }
        }
        catch (NullPointerException ex)
        {
            // redundant, but keeps checkstyle happy
            log = null;
        }
    }

    /**
     * Set up the timer and activity monitoring.
     */
    private void initialize()
    {
        masterTimer = new TimerFactory().createTimer(new TimerHandler()
        {
            private Date lastActivity = new Date();

            public void run()
            {
                try
                {
                    // idle checking
                    if (activity)
                    {
                        // there has been user activity since the last poll, so record the time
                        lastActivity = new Date();
                        activity = false;
                    }
                    else
                    {
                        // no activity since last poll, see if it's long enough to be considered idle
                        long lastActivityMs = lastActivity.getTime();
                        long nowMs = new Date().getTime();
                        if (nowMs - lastActivityMs > IDLE_TIMEOUT_MS)
                        {
                            // idle: shut down master timer and don't run the jobs
                            idle = true;
                            masterTimer.cancel();
                            return;
                        }
                    }

                    // run timers
                    runTimerJobs();
                }
                catch (Exception ex)
                {
                    if (log != null)
                    {
                        log.log(Level.SEVERE, "Exception thrown in master timer handler.", ex);
                    }
                }
            }
        });
        Event.addNativePreviewHandler(new NativePreviewHandler()
        {
            /*
             * This event will be called A LOT. Do not add anything non-trivial to it! It is used to determine
             * inactivity.
             */
            public void onPreviewNativeEvent(final NativePreviewEvent event)
            {
                if (event.getTypeInt() == Event.ONMOUSEMOVE || event.getTypeInt() == Event.ONKEYDOWN)
                {
                    // mark that there has been activity. let master timer record the time on its next poll.
                    activity = true;

                    // if system is idle, mark as not idle and get jobs restarted
                    if (idle)
                    {
                        idle = false;

                        // defer this to prevent making the user event slow
                        Scheduler.get().scheduleDeferred(new ScheduledCommand()
                        {
                            public void execute()
                            {
                                try
                                {
                                    runTimerJobs();
                                    masterTimer.scheduleRepeating(MASTER_TIMER_POLL_MS);
                                }
                                catch (Exception ex)
                                {
                                    if (log != null)
                                    {
                                        log.log(Level.SEVERE, "Exception thrown when resuming from idle.", ex);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });

        //
        masterTimer.scheduleRepeating(MASTER_TIMER_POLL_MS);
    }

    /**
     * Checks for all timer jobs (of any periodicity) that are eligible to run and runs them.
     */
    private void runTimerJobs()
    {
        Date now = new Date();
        long nowMs = now.getTime();
        boolean anyJobsRun = false;
        ActionProcessor actionProcessor = Session.getInstance().getActionProcessor();

        for (int numMinutes : jobs.keySet())
        {
            // has it been more that numMinutes since this batch last ran?
            Date lastRun = lastRunTimes.get(numMinutes);
            boolean okToRun = true;
            if (lastRun != null)
            {
                long deltaSec = (nowMs - lastRun.getTime()) / MS_IN_SEC;
                long requiredSec = numMinutes * SEC_IN_MIN - TIMER_MERCY_SEC;
                okToRun = deltaSec >= requiredSec;
            }

            if (okToRun)
            {
                for (String job : jobs.get(numMinutes))
                {
                    if (!anyJobsRun)
                    {
                        anyJobsRun = true;
                        actionProcessor.setQueueRequests(true);
                    }

                    try
                    {
                        if (fetchables.containsKey(job) && !pausedJobs.contains(job))
                        {
                            fetchables.get(job).fetch(requests.get(job), false);
                        }
                    }
                    catch (Exception ex)
                    {
                        // Just making sure ANYTHING that goes wrong doesn't hose the entire app.
                        int x = 0;
                    }
                }

                // mark this batch as having run (using the base
                lastRunTimes.put(numMinutes, now);
            }
        }

        if (anyJobsRun)
        {
            actionProcessor.setQueueRequests(false);
            actionProcessor.fireQueuedRequests();
        }
    }

    /**
     * Add a timer job.
     * 
     * @param jobKey
     *            the job key, used for lookup and modification. DON'T FORGET IT.
     * @param numOfMinutes
     *            the number of minutes to wait between executions.
     * @param fetchable
     *            the fetchable client model.
     * @param request
     *            the request.
     * @param permanant
     *            does this timer job persist.
     */
    public void addTimerJob(final String jobKey, final Integer numOfMinutes, final Fetchable fetchable,
            final Serializable request, final boolean permanant)
    {
        if (!permanant)
        {
            tempJobs.add(jobKey);
        }
        if (!jobs.containsKey(numOfMinutes))
        {
            jobs.put(numOfMinutes, new HashSet<String>());
        }
        jobs.get(numOfMinutes).add(jobKey);
        requests.put(jobKey, request);
        fetchables.put(jobKey, fetchable);

        if (masterTimer == null)
        {
            initialize();
        }
    }

    /**
     * Change a request object for a job.
     * 
     * @param jobKey
     *            the job key.
     * @param request
     *            the request.
     */
    public void changeRequest(final String jobKey, final Serializable request)
    {
        requests.put(jobKey, request);
    }

    /**
     * Change the fetchable for a job.
     * 
     * @param jobKey
     *            the job key.
     * @param fetchable
     *            the fetchable.
     */
    public void changeFetchable(final String jobKey, final Fetchable fetchable)
    {
        fetchables.put(jobKey, fetchable);
    }

    /**
     * Pause a job.
     * 
     * @param jobKey
     *            the job to pause.
     */
    public void pauseJob(final String jobKey)
    {
        pausedJobs.add(jobKey);
    }

    /**
     * Unpause a job.
     * 
     * @param jobKey
     *            the job to unpause.
     */
    public void unPauseJob(final String jobKey)
    {
        pausedJobs.remove(jobKey);
    }

    /**
     * Clean up the temporary jobs. Called each time the page changes.
     */
    public void clearTempJobs()
    {
        for (String job : tempJobs)
        {
            removeTimerJob(job);
        }
    }

    /**
     * Remove a job.
     * 
     * @param jobKey
     *            the job key.
     */
    public void removeTimerJob(final String jobKey)
    {
        for (Integer min : jobs.keySet())
        {
            for (String job : jobs.get(min))
            {
                if (job.equals(jobKey))
                {
                    jobs.get(min).remove(jobKey);
                    break;
                }
            }
        }

    }

}
