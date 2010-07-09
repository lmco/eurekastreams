/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.commons.scheduling;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;


/**
 * This class uses reflection to call the specified object and method
 * from within a @Transactional environment.
 *
 * This allows us to call mapper methods (which need to be called from within
 * a transactional environment) from the spring scheduler.
 *
 */
public class TransactionalTaskRunner
{
    /**
     * logger.
     */
    private static Log log = LogFactory.getLog(TransactionalTaskRunner.class);

    /**
     * conversion constant.
     */
    public static final double MILLIS_TO_SECONDS = 1000;

    /**
     * The object to call.
     */
    private Object target;

    /**
     * the method which will be called that requires a transactional environment.
     */
    private Method method;

    /**
     * I think spring wants this here.
     */
    public TransactionalTaskRunner()
    {

    }

    /**
     *
     * @param inTarget the object to call.
     * @param methodName the method to run on the target, method must accept no arguments.
     */
    public TransactionalTaskRunner(final Object inTarget, final String methodName)
    {
        try
        {
            target = inTarget;
            method = inTarget.getClass().getMethod(methodName);
        }
        catch (SecurityException e)
        {
            log.error(e.toString());
        }
        catch (NoSuchMethodException e)
        {
            log.error(e.toString());
        }
    }

    /**
     * this method creates a transactional environment for calling mapper methods.
     */
    @Transactional
    public void runTransactionalTask()
    {
        Date start = new Date();
        try
        {
            long startTime = start.getTime();
            method.invoke(target);
            Date end = new Date();
            long endTime = end.getTime();
            String message = "Method " + method.getName()
                                + " started at " + start
                                + " and finished at " + end
                                + " taking " + ((endTime - startTime)) / MILLIS_TO_SECONDS
                                + " seconds";
            log.info(message);
        }
        catch (IllegalArgumentException e)
        {
            String message = "Method " + method.getName() + " started at " + start;
            log.error(message);
            log.error(e.toString());
        }
        catch (IllegalAccessException e)
        {
            String message = "Method " + method.getName() + " started at " + start;
            log.error(message);
            log.error(e.toString());
        }
        catch (InvocationTargetException e)
        {
            String message = "Method " + method.getName() + " started at " + start;
            log.error(message);
            log.error(e.toString());
        }

    }
}
