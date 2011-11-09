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
import org.eurekastreams.commons.server.service.ActionController;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.security.userdetails.UserDetails;

/**
 * The Action Executor class. This class will look up in Spring for the action key. Once the action is found, it will
 * confirm the action requires User Details and the User Details are present. Next, the params will be validated, the
 * user will be authorized, and the action executed.
 */
public class ActionExecutor
{
    /** Logger. */
    private final Log log = LogFactory.getLog(ActionExecutor.class);

    /** The context from which this service can load action beans. */
    private final BeanFactory beanFactory;

    /** Instance of {@link ActionController} used within this executor. */
    private final ActionController serviceActionController;

    /** Principal populator. */
    private final PrincipalPopulator principalPopulator;

    /** Persistent bean manager for serialization. */
    private final PersistentBeanManager persistentBeanManager;

    /** Prepares exceptions for returning to the client. */
    private final Transformer<Exception, Exception> exceptionSanitizer;

    /**
     * Constructor.
     * 
     * @param inBeanFactory
     *            The context from which this service can load action beans.
     * @param inServiceActionController
     *            Instance of {@link ActionController} used within this executor.
     * @param inPrincipalPopulator
     *            Principal populator.
     * @param inPersistentBeanManager
     *            Persistent bean manager for serialization.
     * @param inExceptionSanitizer
     *            Prepares exceptions for returning to the client.
     */
    public ActionExecutor(final BeanFactory inBeanFactory, final ActionController inServiceActionController,
            final PrincipalPopulator inPrincipalPopulator, final PersistentBeanManager inPersistentBeanManager,
            final Transformer<Exception, Exception> inExceptionSanitizer)
    {
        beanFactory = inBeanFactory;
        serviceActionController = inServiceActionController;
        principalPopulator = inPrincipalPopulator;
        persistentBeanManager = inPersistentBeanManager;
        exceptionSanitizer = inExceptionSanitizer;
    }

    /**
     * Execute method for the class.
     *
     * @param actionRequest
     *            the action to execute.
     * @param userDetails
     *            the user details for this action request.
     * @return The result as a serializable object.
     */
    @SuppressWarnings("unchecked")
    public ActionRequest execute(final ActionRequest actionRequest, final UserDetails userDetails)
    {
        log.debug("Starting Action: " + actionRequest.getActionKey());

        String userName = getUserName(userDetails);

        try
        {
            Object springBean = beanFactory.getBean(actionRequest.getActionKey());

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
                        principalPopulator.getPrincipal(userDetails.getUsername(), actionRequest.getSessionId()));
                actionContext.setActionId(actionRequest.getActionKey());
                result = serviceActionController.execute(actionContext, action);
            }
            else if (springBean instanceof TaskHandlerServiceAction)
            {
                TaskHandlerServiceAction action = (TaskHandlerServiceAction) springBean;

                // grab serializable parameter object.
                Serializable actionParameter = actionRequest.getParam();

                ServiceActionContext actionContext = new ServiceActionContext(actionParameter,
                        principalPopulator.getPrincipal(userDetails.getUsername(), actionRequest.getSessionId()));
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
            // But insure only exceptions which are GWT-serializable are returned (otherwise no response will be
            // returned to the client)
            actionRequest.setResponse(exceptionSanitizer.transform(ex));
        }

        // discard the params, since the client already has them
        actionRequest.setParam(null);

        log.debug("Finished Action: " + actionRequest.getActionKey());

        return actionRequest;
    }

    /**
     * Helper for getting userName.
     *
     * @param userDetails
     *            User details.
     * @return user name.
     */
    private String getUserName(final UserDetails userDetails)
    {
        try
        {
            return userDetails.getUsername();
        }
        catch (Exception e)
        {
            return "unavailable";
        }
    }

}
