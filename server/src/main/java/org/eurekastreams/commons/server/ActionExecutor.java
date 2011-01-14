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
package org.eurekastreams.commons.server;

import java.io.Serializable;

import net.sf.gilead.core.PersistentBeanManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.context.PrincipalPopulator;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.actions.service.TaskHandlerServiceAction;
import org.eurekastreams.commons.client.ActionRequest;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.exceptions.GeneralException;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.commons.server.service.ActionController;
import org.springframework.context.ApplicationContext;
import org.springframework.security.userdetails.UserDetails;

/**
 *
 * The Action Executor class. This class will look up in Spring for the action key. Once the action is found, it will
 * confirm the action requires User Details and the User Details are present. Next, the params will be validated, the
 * user will be authorized, and the action executed.
 */
public class ActionExecutor
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.getLog(ActionExecutor.class);

    /**
     * The context from which this service can load action beans.
     */
    private ApplicationContext springContext = null;

    /**
     * The action object.
     */
    @SuppressWarnings("unchecked")
    private final ActionRequest actionRequest;

    /**
     * The user details for this action request.
     */
    private final UserDetails userDetails;

    /**
     * Persistent bean manager for serialization.
     */
    private final PersistentBeanManager persistentBeanManager;

    /**
     * Principal Populator.
     */
    private final PrincipalPopulator principalPopulator;

    /**
     * Instance of {@link ActionController} used within this executor.
     */
    private final ActionController serviceActionController;

    /**
     * Constructor for the executor class.
     *
     * @param inSpringContext
     *            the Spring application context.
     * @param inUserDetails
     *            the user details for this action request.
     * @param inActionRequest
     *            the action to execute.
     */
    @SuppressWarnings("unchecked")
    public ActionExecutor(final ApplicationContext inSpringContext, final UserDetails inUserDetails,
            final ActionRequest inActionRequest)
    {
        userDetails = inUserDetails;
        actionRequest = inActionRequest;
        springContext = inSpringContext;

        persistentBeanManager = (PersistentBeanManager) springContext.getBean("persistentBeanManager");
        principalPopulator = (PrincipalPopulator) springContext.getBean("principalPopulator");
        serviceActionController = (ActionController) springContext.getBean("serviceActionController");
    }

    /**
     * Execute method for the class.
     *
     * @return The result as a serializable object.
     */
    @SuppressWarnings("unchecked")
    public ActionRequest execute()
    {
        log.debug("Starting Action: " + actionRequest.getActionKey());

        String userName = getUserName();

        try
        {
            Object springBean = springContext.getBean(actionRequest.getActionKey());

            // ////////////////////////////////////////////////
            // actually perform the action

            Serializable result = null;

            // if you use "instanceof classname" where classname is a class and
            // not an interface,
            // the check and cast will fail
            // however, the check and cast works if you use an interface here
            if (springBean instanceof ServiceAction)
            {
                ServiceAction action = (ServiceAction) springBean;

                // grab serializable parameter object.
                Serializable actionParameter = actionRequest.getParam();

                ServiceActionContext actionContext = new ServiceActionContext(actionParameter,
                        principalPopulator.getPrincipal(userDetails.getUsername()));
                actionContext.setActionId(actionRequest.getActionKey());
                result = serviceActionController.execute(actionContext, action);
            }
            else if (springBean instanceof TaskHandlerServiceAction)
            {
                TaskHandlerServiceAction action = (TaskHandlerServiceAction) springBean;

                // grab serializable parameter object.
                Serializable actionParameter = actionRequest.getParam();

                ServiceActionContext actionContext = new ServiceActionContext(actionParameter,
                        principalPopulator.getPrincipal(userDetails.getUsername()));
                actionContext.setActionId(actionRequest.getActionKey());
                result = serviceActionController.execute(actionContext, action);
            }
            else
            {
                throw new IllegalArgumentException("Supplied bean is not an executable action.");
            }

            // //////////////////////////////////////////////
            // set the results to be passed back

            // cloning here ensures that gilead makes all the objects
            // serializable.
            long startClone = System.currentTimeMillis();
            actionRequest.setResponse((Serializable) persistentBeanManager.clone(result));
            log.debug(actionRequest.getActionKey() + " gilead serialization time: "
                    + (System.currentTimeMillis() - startClone) + " (ms)");
        }
        catch (Exception ex)
        {
            // log the exception
            String paramString = "null parameters";
            if (actionRequest.getParam() != null)
            {
                try
                {
                    paramString = actionRequest.getParam().toString();
                }
                catch (Exception pex)
                {
                    paramString = "<error retrieving parameters: " + pex.getMessage() + ">";
                }
            }
            log.error("Caught exception while running " + actionRequest.getActionKey() + " for user: " + userName
                    + ". Parameters: " + paramString + ". ", ex);

            // By setting an exception as the response, we are effectively throwing the exception to the client.
            // But insure only exceptions which are serializable are returned (otherwise no response will be returned to
            // the client)
            Throwable response;
            if (ex instanceof ValidationException)
            {
                response = ex;
            }
            else if (ex instanceof AuthorizationException)
            {
                // Remove any nested exceptions
                response = (ex.getCause() == null) ? ex : new AuthorizationException(ex.getMessage());
            }
            else if (ex instanceof GeneralException)
            {
                // Remove any nested exceptions (particularly want to insure no PersistenceExceptions get sent - they
                // are not serializable plus contain details that should not be exposed to users)
                response = (ex.getCause() == null) ? ex : new GeneralException(ex.getMessage());
            }
            else if (ex instanceof ExecutionException)
            {
                // Remove any nested exceptions
                response = (ex.getCause() == null) ? ex : new ExecutionException(ex.getMessage());
            }
            else
            {
                response = new GeneralException(ex.getMessage());
            }
            actionRequest.setResponse(response);
        }

        // discard the params, since the client already has them
        actionRequest.setParam(null);

        log.debug("Finished Action: " + actionRequest.getActionKey());

        return actionRequest;
    }

    /**
     * Helper for getting userName.
     *
     * @return user name.
     */
    private String getUserName()
    {
        String userName = "";
        try
        {
            userName = userDetails.getUsername();
        }
        catch (Exception e)
        {
            // necessary in case getUserName() throws exception
            userName = "unavailable";
        }
        return userName;
    }

    /**
     * @return Logger.
     */
    protected Log getLog()
    {
        return log;
    }

    /**
     * @return Spring context.
     */
    protected ApplicationContext getSpringContext()
    {
        return springContext;
    }

    /**
     * @return Requesting user.
     */
    protected UserDetails getUserDetails()
    {
        return userDetails;
    }
}
