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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.client.ActionRPCService;
import org.eurekastreams.commons.client.ActionRequest;
import org.eurekastreams.commons.exceptions.SessionException;
import org.springframework.context.ApplicationContext;
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Server-side implementation of ActionRPCService.
 */
public class ActionRPCServiceImpl extends RemoteServiceServlet implements ActionRPCService
{
    /**
     * Serialization id.
     */
    private static final long serialVersionUID = -8268924093483774688L;

    /**
     * Logger.
     */
    private final Log log = LogFactory.getLog(ActionRPCServiceImpl.class);

    /**
     * The context from which this service can load action beans.
     */
    private ApplicationContext springContext = null;

    /**
     * Local instance of the {@link ActionExecutor}.
     */
    private ActionExecutor actionExecutor;

    /**
     * As a servlet, this class' init() method is called automatically. This is how we get context.
     * 
     * @param config
     *            the configuration describing the run-time environment.
     */
    @Override
    public void init(final ServletConfig config)
    {
        log.info("ActionRPCServiceImpl::init()");

        try
        {
            super.init(config);
        }
        catch (ServletException e)
        {
            log.error("Caught a ServletException during initialization: " + e.getMessage(), e);
        }

        springContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());

        actionExecutor = (ActionExecutor) springContext.getBean("actionExecutor");
    }

    /**
     * @return Spring context.
     */
    protected ApplicationContext getSpringContext()
    {
        return springContext;
    }

    /**
     * {@inheritDoc}
     */
    public String establishSession()
    {
        return getThreadLocalRequest().getSession().getId();
    }

    /**
     * Execute a single ActionRequest.
     * 
     * @param request
     *            the request specification to execute
     * @return the action response encapsulated with the request
     */
    @SuppressWarnings({ "rawtypes" })
    public final ActionRequest execute(final ActionRequest request)
    {
        UserDetails user = getUserDetails();
        return execute(request, user);
    }

    /**
     * Execute multiple ActionRequests. We don't specify a type for ActionRequst because the types will likely be
     * different for each request.
     * 
     * @param requests
     *            the request specifications to execute
     * @return the action response encapsulated with the request
     */
    @SuppressWarnings({ "rawtypes" })
    public final ActionRequest[] execute(final ActionRequest[] requests)
    {
        UserDetails user = getUserDetails();

        ActionRequest[] results = new ActionRequest[requests.length];
        for (int i = 0; i < requests.length; i++)
        {
            results[i] = execute(requests[i], user);
        }

        return results;
    }

    /**
     * Execute a single ActionRequest.
     * 
     * @param request
     *            the request specification to execute
     * @param user
     *            the user making the request
     * @return the action response encapsulated with the request
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private ActionRequest execute(final ActionRequest request, final UserDetails user)
    {
        // check that the session id is the session id stamped in the request
        if (!getThreadLocalRequest().getSession().getId().equals(request.getSessionId()))
        {
            request.setResponse(new SessionException("Session Expired"));
            return request;
        }
        else
        {
            return actionExecutor.execute(request, user);
        }
    }

    /**
     * Try to get the User information from the session.
     * 
     * @return represents the user currently in session
     */
    private UserDetails getUserDetails()
    {
        UserDetails user = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (null != auth)
        {
            Object obj = auth.getPrincipal();
            if (obj instanceof UserDetails)
            {
                user = (UserDetails) obj;
                log.debug("Found username: " + user.getUsername());
            }
        }

        if (null == user)
        {
            user = new NoCurrentUserDetails();
        }

        return user;
    }

    /**
     * Overridden checkPermutationStrongName - disabled to avoid failure due to firefox bug that prevents GWT XSRF
     * request headers.
     * 
     * @throws SecurityException
     *             will never be thrown.
     */
    @Override
    protected void checkPermutationStrongName() throws SecurityException
    {
        // do nothing - avoids failure due to firefox bug that prevents GWT XSRF request headers
    }
}
