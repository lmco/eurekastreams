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
package org.eurekastreams.commons.logging;

import org.apache.commons.logging.Log;

/**
 * Factory to create a logger for the calling class.
 */
public final class LogFactory
{
    /**
     * Private constructor.
     */
    private LogFactory()
    {
        // no-op
    }

    /**
     * Make a log for the calling class.
     * 
     * @return a log for the calling class
     */
    public static Log make()
    {
        Throwable t = new Throwable();
        StackTraceElement directCaller = t.getStackTrace()[1];
        return org.apache.commons.logging.LogFactory.getLog(directCaller.getClassName());
    }
}
