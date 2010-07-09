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
package org.eurekastreams.commons.task;

import org.apache.log4j.Logger;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.server.UserActionRequest;

/**
 * This will, while implementing {@link TaskHandler}, executes the given task in a new thread.
 */
public class ThreadedTaskActionExecutor extends TaskActionExecutor implements Runnable
{
    /**
     * The logger.
     */
    private Logger logger = Logger.getLogger(ThreadedTaskActionExecutor.class);

    /**
     * the user action request to share with the thread.
     */
    private UserActionRequest userActionRequest = null;

    /**
     * the task executor to use in the threaded mode.
     */
    private TaskExecutor taskExecutor;

    /**
     * Constructor.
     * 
     * @param inTaskExecutor  the task executor to use.
     */
    public ThreadedTaskActionExecutor(final TaskExecutor inTaskExecutor)
    {
        taskExecutor = inTaskExecutor;
    }

    /**
     * Submit method taking in a UserActionRequest.
     * 
     * @param inUserActionRequest
     *            the user action request.
     */
    @Override
    public void execute(final UserActionRequest inUserActionRequest)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("threaded submit called...");
        }

        userActionRequest = inUserActionRequest;
        Thread thread = new Thread(this);
        thread.start();
        try
        {
            thread.join(); // block until thread is finished
        }
        catch (Exception ex)
        {
            throw new ExecutionException("Error trying to join back with the child thread.");
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("threaded submit finished (thread joined).");
        }
    }

    /*
     * execute action on another thread.
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run()
    {
        taskExecutor.execute(userActionRequest);
    }

}
