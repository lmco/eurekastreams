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
package org.eurekastreams.web.client.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eurekastreams.commons.client.ActionProcessor;

import com.google.gwt.user.client.Timer;

/**
 * Allows periodic events to be scheduled and run in batches, thus allowing messages to be sent in bundles.
 */
public class PeriodicEventManager
{
    /** Obvious to everyone but checkstyle. */
    private static final long MS_PER_S = 1000;

    /** Period of the internal timer in ms. */
    private static final int POLL_PERIOD = 5 * (int) MS_PER_S;

    /** Amount of time in ms without user input to consider the app idle. */
    private long idleTimeout;

    /** At what time the system will be considered idle without intervening user input. */
    private Date idleTime;

    /** Internal timer. */
    private Timer timer;

    /** If system is idle. */
    private boolean idle;

    /** User activity detected since last poll. */
    private boolean activity;

    /** Action processor (for batching requests). */
    private ActionProcessor actionProcessor;

    /** Periodic processing. */
    private TimerHandler timerHandler = new TimerHandler()
    {
        public void run()
        {
            Date now = timeNow();

            // shut off activity if app is idle
            if (activity)
            {
                idleTime = new Date(now.getTime() + idleTimeout);
                activity = false;
            }
            else if (now.compareTo(idleTime) >= 0)
            {
                idle = true;
                timer.cancel();
            }

            // invoke actions
            if (!idle)
            {
                boolean any = false;

                for (Registration reg : registrations)
                {
                    if (now.compareTo(reg.getNextTime()) >= 0)
                    {
                        if (!any)
                        {
                            any = true;

                            // Freeze queue
                            actionProcessor.setQueueRequests(true);

                        }
                        // invoke action
                        reg.getHandler().run();

                        // update next time
                        reg.computeNextTime(now);
                    }
                }

                if (any)
                {
                    // Flush and unfreeze queue
                    actionProcessor.fireQueuedRequests();
                    actionProcessor.setQueueRequests(false);
                }
            }
        }
    };

    /** List of registrations. */
    private List<Registration> registrations = new ArrayList<Registration>();

    /**
     * Constructor.
     *
     * @param inIdleTimeout
     *            Idle timeout in seconds.
     * @param inTimerFactory
     *            For creating the timer.
     * @param inActionProcessor
     *            Action processor.
     */
    public PeriodicEventManager(final long inIdleTimeout, final TimerFactory inTimerFactory,
            final ActionProcessor inActionProcessor)
    {
        idleTimeout = inIdleTimeout * MS_PER_S;
        timer = inTimerFactory.createTimer(timerHandler);
        actionProcessor = inActionProcessor;
    }

    /**
     * Registers an action to occur on a periodic basis.
     *
     * @param handler
     *            Handler that will perform the action.
     * @param period
     *            Period in seconds.
     */
    public void register(final TimerHandler handler, final int period)
    {
        Registration reg = new Registration(handler, period);
        reg.computeNextTime(timeNow());
        registrations.add(reg);
    }

    /**
     * Removes an action from being executed.
     *
     * @param handler
     *            Handler that will perform the action.
     */
    public void deregister(final TimerHandler handler)
    {
        Iterator<Registration> iter = registrations.iterator();
        while (iter.hasNext())
        {
            Registration reg = iter.next();
            if (reg.getHandler().equals(handler))
            {
                iter.remove();
            }
        }
    }

    /**
     * Begins processing.
     */
    public void start()
    {
        Date now = timeNow();
        for (Registration reg : registrations)
        {
            reg.computeNextTime(now);
        }
        idleTime = new Date(now.getTime() + idleTimeout);
        startTimer();
    }

    /**
     * Invoked by the app when user input is detected to indicate the app is not idle. Direct method call for
     * efficiency, since this could be called very often.
     */
    public void userActivityDetected()
    {
        activity = true;
        if (idle)
        {
            startTimer();
        }
    }

    /**
     * Starts the timer running.
     */
    private void startTimer()
    {
        idle = false;
        timer.scheduleRepeating(POLL_PERIOD);
    }

    /**
     * Seam for testing.
     *
     * @return Current time.
     */
    protected Date timeNow()
    {
        return new Date();
    }

    /**
     * Tracks registered actions.
     */
    private class Registration
    {
        /** Action. */
        private TimerHandler handler;

        /** Next time to invoke action. */
        private Date nextTime;

        /** Period (in seconds). */
        private int period;

        /**
         * Constructor.
         *
         * @param inHandler
         *            The action.
         * @param inPeriod
         *            Period.
         */
        public Registration(final TimerHandler inHandler, final int inPeriod)
        {
            handler = inHandler;
            period = inPeriod;
        }

        /**
         * @return the nextTime
         */
        public Date getNextTime()
        {
            return nextTime;
        }

        /**
         * @param inNextTime
         *            the nextTime to set
         */
        public void setNextTime(final Date inNextTime)
        {
            nextTime = inNextTime;
        }

        /**
         * Computes the next time the action should be invoked.
         *
         * @param now
         *            Current time.
         */
        public void computeNextTime(final Date now)
        {
            nextTime = new Date(now.getTime() + MS_PER_S * period);
        }

        /**
         * @return the handler
         */
        public TimerHandler getHandler()
        {
            return handler;
        }

        /**
         * @return the period
         */
        public int getPeriod()
        {
            return period;
        }
    }
}
