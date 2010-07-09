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
package org.eurekastreams.commons.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.eurekastreams.commons.actions.async.AsyncAction;
import org.eurekastreams.commons.actions.async.TaskHandlerAsyncAction;
import org.eurekastreams.commons.actions.context.async.AsyncActionContext;
import org.eurekastreams.commons.server.ActionExecutor;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.server.async.AsyncActionController;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * This will, while implementing {@link TaskHandler}, executes the given task in a new thread.
 */
public class TaskActionExecutor implements TaskExecutor, ApplicationContextAware
{
    /**
     * The logger.
     */
    private Logger logger = Logger.getLogger(TaskActionExecutor.class);

    /**
     * Logger.
     */
    private Log actionPerformanceLog = LogFactory.getLog(ActionExecutor.class.getCanonicalName() + "-actionTimer");

    /**
     * The application context auto-set by Spring when defined as a bean.
     */
    private static ApplicationContext ctx;

    /**
     * Setter called by Spring because of implementing ApplicationContextAware.
     *
     * @param inApplicationContext
     *            the application context
     */
    public void setApplicationContext(final ApplicationContext inApplicationContext)
    {
        ctx = inApplicationContext;
    }

    /**
     * Handle the action, this means executing it real time on the same thread.
     * @param inUserActionRequest the users action request
     */
    @Override
    public void execute(final UserActionRequest inUserActionRequest)
    {
        AsyncActionController actionController = (AsyncActionController) ctx.getBean("asyncActionController");

        Object springBean = ctx.getBean(inUserActionRequest.getActionKey());

        Long start = null;
        if (actionPerformanceLog.isInfoEnabled())
        {
            start = System.currentTimeMillis();
        }

        logger.debug("RealTimeExecuter about to performAction...");

        try
        {
            if (springBean instanceof AsyncAction)
            {
                AsyncAction action = (AsyncAction) springBean;
                AsyncActionContext actionContext = new AsyncActionContext(inUserActionRequest.getParams());
                actionController.execute(actionContext, action);
            }
            else if (springBean instanceof TaskHandlerAsyncAction)
            {
                TaskHandlerAsyncAction action = (TaskHandlerAsyncAction) springBean;
                AsyncActionContext actionContext = new AsyncActionContext(inUserActionRequest.getParams());
                actionController.execute(actionContext, action);
            }
            else
            {
                throw new IllegalArgumentException("Supplied bean is not an executable async action.");
            }
        }
        catch (Exception e)
        {
            logger.error("exception invoking action " + inUserActionRequest.getActionKey() + "Exception is " + e);
        }

        if (actionPerformanceLog.isInfoEnabled())
        {
            String logMessage = "async executor\t" + inUserActionRequest.getActionKey() + "\t"
                    + (System.currentTimeMillis() - start);
            actionPerformanceLog.info(logMessage);
        }
    }

}
