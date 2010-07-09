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
package org.eurekastreams.web.client.log;

/**
 * Client side logging.
 *
 */
public class Log
{
    /**
     * Info out to console.
     * @param message the message.
     */
    public void info(final String message)
    {
        debug("INFO: " + message);
    }

    /**
     * Warn out to console.
     * @param message warn message.
     */
    public void warn(final String message)
    {
        debug("WARN: " + message);
    }

    /**
     * Error out to console.
     * @param message message.
     */
    public void error(final String message)
    {
        debug("ERROR: " + message);
    }

    /**
     * Write to console.
     * @param msg the message.
     */
    private static native void debug(final String msg)/*-{
        if ($wnd.console != null && typeof($wnd.console.log) == 'function')
        {
            $wnd.console.log(msg);
        }
    }-*/;
}
