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
package org.eurekastreams.server.aop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.util.StopWatch;

/**
 * AOP timer for profiling.
 * 
 */
public class PerformanceTimer
{

    /**
     * Method for logging timing data.
     * 
     * @param call
     *            {@link ProceedingJoinPoint}
     * @return result of wrapped method.
     * @throws Throwable
     *             on error.
     */
    public Object profile(final ProceedingJoinPoint call) throws Throwable
    {
        StopWatch clock = null;

        // get the perf log for target object.
        Log log = LogFactory.getLog("perf.timer." + call.getTarget().getClass().getCanonicalName());
        try
        {
            if (log.isInfoEnabled())
            {
                clock = new StopWatch();
                clock.start(call.toShortString());
            }
            return call.proceed();
        }
        finally
        {
            if (log.isInfoEnabled() && clock != null)
            {
                clock.stop();

                Object[] args = call.getArgs();
                StringBuffer params = new StringBuffer();
                for (Object obj : args)
                {
                    params.append("Param: " + ((obj == null) ? "null" : obj.toString()) + "\n\t");
                }

                log.info(clock.getTotalTimeMillis() + " (ms) - " + call.getTarget().getClass().getSimpleName() + "."
                        + call.getSignature().toShortString() + "\n\t" + params.toString());
            }

        }
    }
}
