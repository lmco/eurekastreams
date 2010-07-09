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

import com.google.gwt.user.client.Timer;

/**
 * Creates timers. Allows unit testing, since timers are GWT objects.
 */
public class TimerFactory
{
    /**
     * Wraps GWT timer so users can pass an event instead of overriding.
     */
    static class EventedTimer extends Timer
    {
        /** Action to occur on timer expiration. */
        private TimerHandler handler;

        /**
         * Constructor.
         * @param inHandler Action to occur on timer expiration.
         */
        public EventedTimer(final TimerHandler inHandler)
        {
            handler = inHandler;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void run()
        {
            handler.run();
        }
    }

    /**
     * Starts a one-shot timer.
     * 
     * @param delayMillis
     *            Time until expiration.
     * @param handler
     *            Action to occur.
     */
    public void runTimer(final int delayMillis, final TimerHandler handler)
    {
        new EventedTimer(handler).schedule(delayMillis);
    }

    /**
     * Starts a repeating timer.
     * 
     * @param periodMillis
     *            Time between expirations.
     * @param handler
     *            Action to occur.
     */
    public void runTimerRepeating(final int periodMillis, final TimerHandler handler)
    {
        new EventedTimer(handler).scheduleRepeating(periodMillis);
    }

    /**
     * Creates (but does not start) a timer.
     * 
     * @param handler
     *            Action to occur.
     * @return The timer.
     */
    public Timer createTimer(final TimerHandler handler)
    {
        return new EventedTimer(handler);
    }
}
