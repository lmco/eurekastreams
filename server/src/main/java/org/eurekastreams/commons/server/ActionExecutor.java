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
import org.eurekastreams.commons.server.service.ServiceActionController;
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
    private Log log = LogFactory.getLog(ActionExecutor.class);

    /**
     * Logger.
     */
    private Log actionPerformanceLog = LogFactory.getLog(ActionExecutor.class.getCanonicalName() + "-actionTimer");

    /**
     * The context from which this service can load action beans.
     */
    private ApplicationContext springContext = null;

    /**
     * The action object.
     */
    @SuppressWarnings("unchecked")
    private ActionRequest actionRequest;

    /**
     * The user details for this action request.
     */
    private UserDetails userDetails;

    /**
     * Persistent bean manager for serialization.
     */
    private PersistentBeanManager persistentBeanManager = null;

    /**
     * Principal Populator.
     */
    private PrincipalPopulator principalPopulator;

    /**
     * Instance of {@link ServiceActionController} used within this executor.
     */
    private ServiceActionController serviceActionController;

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
        this.userDetails = inUserDetails;
        this.actionRequest = inActionRequest;
        this.springContext = inSpringContext;

        persistentBeanManager = (PersistentBeanManager) springContext.getBean("persistentBeanManager");
        principalPopulator = (PrincipalPopulator) springContext.getBean("principalPopulator");
        serviceActionController = (ServiceActionController) springContext.getBean("serviceActionController");
    }

    /**
     * Execute method for the class.
     * 
     * @return The result as a serializable object.
     */
    @SuppressWarnings("unchecked")
    public ActionRequest execute()
    {
        Long start = null;
        if (actionPerformanceLog.isInfoEnabled())
        {
            start = System.currentTimeMillis();
        }

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

                ServiceActionContext actionContext = new ServiceActionContext(actionParameter, principalPopulator
                        .getPrincipal(userDetails.getUsername()));
                result = serviceActionController.execute(actionContext, action);
            }
            else if (springBean instanceof TaskHandlerServiceAction)
            {
                TaskHandlerServiceAction action = (TaskHandlerServiceAction) springBean;

                // grab serializable parameter object.
                Serializable actionParameter = actionRequest.getParam();

                ServiceActionContext actionContext = new ServiceActionContext(actionParameter, principalPopulator
                        .getPrincipal(userDetails.getUsername()));
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
            actionRequest.setResponse((Serializable) persistentBeanManager.clone(result));
        }
        catch (Exception ex)
        {
            // We are effectively throwing the exception to the client.
            actionRequest.setResponse(ex);
            log.error("Caught exception while running " + actionRequest.getActionKey() + " for user: " + userName
                    + ". Parameters: "
                    + ((actionRequest.getParam() == null) ? "null parameters" : actionRequest.getParam().toString())
                    + ". ", ex);
        }

        // discard the params, since the client already has them
        actionRequest.setParam(null);

        log.debug("Finished Action: " + actionRequest.getActionKey());

        if (actionPerformanceLog.isInfoEnabled())
        {
            String logMessage = userName + "\t" + actionRequest.getActionKey() + "\t"
                    + (System.currentTimeMillis() - start);
            actionPerformanceLog.info(logMessage);
        }

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
