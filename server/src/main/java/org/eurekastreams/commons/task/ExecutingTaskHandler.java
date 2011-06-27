/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
import org.eurekastreams.commons.actions.async.AsyncAction;
import org.eurekastreams.commons.actions.async.TaskHandlerAsyncAction;
import org.eurekastreams.commons.actions.context.async.AsyncActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.server.async.AsynchronousActionController;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * Executes the given task on the current thread.
 */
public class ExecutingTaskHandler implements TaskHandler, BeanFactoryAware
{
    /** The logger. */
    private final Logger logger = Logger.getLogger(ExecutingTaskHandler.class);

    /** Spring bean factory from which to retrieve action beans. */
    private BeanFactory beanFactory;

    /** Action controller to use to execute the actions. */
    private final AsynchronousActionController actionController;

    /**
     * Constructor.
     *
     * @param inActionController
     *            Action controller used to execute the actions.
     */
    public ExecutingTaskHandler(final AsynchronousActionController inActionController)
    {
        actionController = inActionController;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBeanFactory(final BeanFactory inBeanFactory) throws BeansException
    {
        beanFactory = inBeanFactory;
    }

    /**
     * Handle the action, this means executing it real time on the same thread.
     *
     * @param inUserActionRequest
     *            the users action request
     * @throws Exception
     *             Possibly.
     */
    @Override
    public void handleTask(final UserActionRequest inUserActionRequest) throws Exception
    {
        Object springBean = beanFactory.getBean(inUserActionRequest.getActionKey());

        logger.debug("RealTimeExecuter about to performAction...");

        try
        {
            if (springBean instanceof AsyncAction)
            {
                AsyncAction action = (AsyncAction) springBean;
                AsyncActionContext actionContext = new AsyncActionContext(inUserActionRequest.getParams());
                actionContext.setActionId(inUserActionRequest.getActionKey());
                actionController.execute(actionContext, action);
            }
            else if (springBean instanceof TaskHandlerAsyncAction)
            {
                TaskHandlerAsyncAction action = (TaskHandlerAsyncAction) springBean;
                AsyncActionContext actionContext = new AsyncActionContext(inUserActionRequest.getParams());
                actionContext.setActionId(inUserActionRequest.getActionKey());
                actionController.execute(actionContext, action);
            }
            else
            {
                throw new IllegalArgumentException("Supplied bean is not an executable async action.");
            }
        }
        catch (Exception ex)
        {
            logger.error("Exception invoking action " + inUserActionRequest.getActionKey(), ex);
        }
    }
}
