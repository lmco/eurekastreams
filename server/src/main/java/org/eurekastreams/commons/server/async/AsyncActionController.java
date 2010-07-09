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
package org.eurekastreams.commons.server.async;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerAction;
import org.eurekastreams.commons.actions.async.AsyncAction;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.actions.context.async.AsyncActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.exceptions.GeneralException;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.task.TaskHandler;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * This class provides the business logic that controls the execution of an {@link AsyncAction}.
 *
 * Transaction management, Validation, and Execution strategy sequences are also controlled in here.
 *
 * Note: Authorization strategies are explicitly excluded from execution. This helps draw a line between the Service and
 * Async sides of actions.
 *
 * In the case of a {@link TaskHandlerAction}, the List of UserActionRequest objects are collected and submitted through
 * the action. This is necessary, because UserActionRequest submissions to the queue through JMS is currently not
 * covered under the configured transaction strategy. When distributed transactions are available within EurekaStreams
 * this behavior will change.
 *
 * The order of execution for the strategies contained within the {@link AsyncAction} is controlled by this class and
 * executed in the following order:
 *
 * ValidationStrategy ExecutionStrategy
 *
 * Optionally:
 *
 * In the case of a {@link TaskHandlerAction}, UserActionRequests will be gathered from the TaskHandlerContext passed
 * to the TaskHandlerExecutionStrategy and submitted to the configured TaskHandler of the action.
 *
 * Exception handling also occurs within this controller. - {@link GeneralException} will be thrown when an unwrapped
 * exception is encountered. - {@link ValidationException} will be logged and passed to the client when encountered. -
 *  {@link ExecutionException}
 * will be logged and passed to the client when encountered.
 *
 */
public class AsyncActionController
{
    /**
     * Local instance of the logger.
     */
    private final Log logger = LogFactory.make();

    /**
     * Instance of the configured platform transaction manager.
     */
    private final PlatformTransactionManager transMgr;

    /**
     * Constructor for the AsyncActionController.
     *
     * @param inTransMgr
     *            - instance of the {@link PlatformTransactionManager} for this controller.
     */
    public AsyncActionController(final PlatformTransactionManager inTransMgr)
    {
        transMgr = inTransMgr;
    }

    /**
     * Execute the supplied {@link AsyncAction} with the given {@link AsyncActionContext}.
     *
     * @param inAsyncActionContext
     *            - instance of the {@link AsyncActionContext} with which to execution the {@link AsyncAction}.
     * @param inAsyncAction
     *            - instance of the {@link AsyncAction} to execute.
     * @return - results from the execution of the AsyncAction.
     *
     *         - GeneralException - when an unexpected error occurs. - ValidationException - when a
     *         {@link ValidationException} occurs. - ExecutionException - when an {@link ExecutionException} occurs.
     */
    public Serializable execute(final AsyncActionContext inAsyncActionContext, final AsyncAction inAsyncAction)
    {
        Serializable results = null;
        DefaultTransactionDefinition transDef = new DefaultTransactionDefinition();
        transDef.setName(inAsyncAction.toString());
        transDef.setReadOnly(inAsyncAction.isReadOnly());
        TransactionStatus transStatus = transMgr.getTransaction(transDef);
        try
        {
            inAsyncAction.getValidationStrategy().validate(inAsyncActionContext);
            results = inAsyncAction.getExecutionStrategy().execute(inAsyncActionContext);
            transMgr.commit(transStatus);
        }
        catch (ValidationException vex)
        {
            onException(transStatus);
            logger.warn("Validation failed for the current action.", vex);
            for (Entry<String, String> currentError : vex.getErrors().entrySet())
            {
                logger.warn("Validation key: " + currentError.getKey() + ", value: " + currentError.getValue());
            }
            throw vex;
        }
        catch (ExecutionException eex)
        {
            onException(transStatus);
            logger.error("Error occurred during execution.", eex);
            throw eex;
        }
        catch (Exception ex)
        {
            onException(transStatus);
            logger.error("Error occurred performing transaction.", ex);
            throw new GeneralException(ex);
        }

        return results;
    }

    /**
     * This method executes a {@link TaskHandlerAction} with the supplied {@link AsyncActionContext}.
     *
     * @param inAsyncActionContext
     *            - instance of the {@link AsyncActionContext} associated with this request.
     * @param inTaskHandlerAction
     *            - instance of the {@link TaskHandlerAction}.
     * @return - results of the execution.
     *
     *         - GeneralException - when an unexpected error occurs. - ValidationException - when a
     *         {@link ValidationException} occurs. - ExecutionException - when an {@link ExecutionException} occurs.
     */
    @SuppressWarnings("unchecked")
    public Serializable execute(final AsyncActionContext inAsyncActionContext,
            final TaskHandlerAction inTaskHandlerAction)
    {
        Serializable results = null;

        DefaultTransactionDefinition transDef = new DefaultTransactionDefinition();
        transDef.setName(inTaskHandlerAction.toString());
        transDef.setReadOnly(inTaskHandlerAction.isReadOnly());
        TransactionStatus transStatus = transMgr.getTransaction(transDef);

        // Assemble special context for TaskHandler actions.
        TaskHandlerActionContext<ActionContext> taskHandlerContext = new TaskHandlerActionContext<ActionContext>(
                inAsyncActionContext, new ArrayList<UserActionRequest>());
        try
        {
            inTaskHandlerAction.getValidationStrategy().validate(inAsyncActionContext);
            results = inTaskHandlerAction.getExecutionStrategy().execute(taskHandlerContext);
            transMgr.commit(transStatus);
        }
        catch (ValidationException vex)
        {
            onException(transStatus);
            logger.warn("Validation failed for the current action.", vex);
            for (Entry<String, String> currentError : vex.getErrors().entrySet())
            {
                logger.warn("Validation key: " + currentError.getKey() + ", value: " + currentError.getValue());
            }
            throw vex;
        }
        catch (ExecutionException eex)
        {
            onException(transStatus);
            logger.error("Error occurred during execution.", eex);
            throw eex;
        }
        catch (Exception ex)
        {
            onException(transStatus);
            logger.error("Error occurred performing transaction.", ex);
            throw new GeneralException(ex);
        }

        //Submit the TaskRequests gathered from the execution strategy into the TaskHandlerContext to the TaskHandler.
        try
        {
            TaskHandler currentTaskHandler = inTaskHandlerAction.getTaskHandler();
            for (UserActionRequest currentRequest : taskHandlerContext.getUserActionRequests())
            {
                currentTaskHandler.handleTask(currentRequest);
            }
        }
        catch (Exception ex)
        {
            logger.error("Error occurred posting UserActionRequests to the queue.", ex);
            throw (new GeneralException("Error occurred posting UserActionRequests to the queue.", ex));
        }

        return results;
    }

    /**
     * Helper class that encapsulates transaction rollback when an exception is encountered.
     *
     * @param inTransStatus
     *            - instance of the {@link TransactionStatus} to go along with the currect block.
     */
    private void onException(final TransactionStatus inTransStatus)
    {
        if (!inTransStatus.isCompleted())
        {
            transMgr.rollback(inTransStatus);
        }
        // TODO: Perform undo operations here.
    }
}
