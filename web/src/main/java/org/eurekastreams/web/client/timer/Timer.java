/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eurekastreams.web.client.model.Fetchable;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.TimerFactory;
import org.eurekastreams.web.client.ui.TimerHandler;

/**
 * Controls timer jobs going back to the server. Bundles HTTP requests for optimizations.
 * 
 */
public class Timer
{
    /**
     * There are 60,000 milliseconds in a minute due to the theory of time.
     */
    private static final int MS_IN_MIN = 60000;
    /**
     * The fetchable models.
     */
    private HashMap<String, Fetchable> fetchables = new HashMap<String, Fetchable>();
    /**
     * The jobs.
     */
    private HashMap<Integer, Set<String>> jobs = new HashMap<Integer, Set<String>>();

    /**
     * The requests.
     */
    private HashMap<String, Serializable> requests = new HashMap<String, Serializable>();

    /**
     * The list of paused jobs.
     */
    private Set<String> pausedJobs = new HashSet<String>();

    /**
     * Temp jobs. Delete these when the page changes.
     */
    private Set<String> tempJobs = new HashSet<String>();

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
            // Add the Fetchable to the array.
            jobs.put(numOfMinutes, new HashSet<String>());
            jobs.get(numOfMinutes).add(jobKey);
            requests.put(jobKey, request);
            fetchables.put(jobKey, fetchable);

            // Set up the Timer job
            new TimerFactory().runTimerRepeating(numOfMinutes * MS_IN_MIN, new TimerHandler()
            {
                private int mouseX = -1;
                private int mouseY = -1;

                public void run()
                {
                    // Do not run if the user is inactive.
                    if (mouseX != Session.getInstance().getMouseX() || mouseY != Session.getInstance().getMouseY())
                    {
                        Session.getInstance().getActionProcessor().setQueueRequests(true);
                        for (String job : jobs.get(numOfMinutes))
                        {
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
                        Session.getInstance().getActionProcessor().setQueueRequests(false);
                        Session.getInstance().getActionProcessor().fireQueuedRequests();
                    }
                }
            });
        }
        else
        {
            jobs.get(numOfMinutes).add(jobKey);
            requests.put(jobKey, request);
            fetchables.put(jobKey, fetchable);
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
