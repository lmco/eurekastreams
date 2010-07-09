/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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

package org.eurekastreams.server.service.security.preauth;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.Ordered;
import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.AuthenticationEntryPoint;
import org.springframework.security.util.RedirectUtils;

/**
 * <p>
 * In the pre-authenticated authentication case (unlike CAS, for example) the
 * user will already have been identified through some external mechanism and a
 * secure context established by the time the security-enforcement filter is
 * invoked.
 * <p>
 * Therefore this class isn't actually responsible for the commencement of
 * authentication, as it is in the case of other providers. It will be called if
 * the user is rejected by the AbstractPreAuthenticatedProcessingFilter,
 * resulting in a null authentication.
 * <p>
 *  Depending on the Exception The <code>commence</code> method will forward to
 *  configured value, or if a match is not found return an
 * <code>HttpServletResponse.SC_FORBIDDEN</code> (403 error).
 * <p>
 * To configure this filter to redirect to specific pages as the result of
 * specific {@link AuthenticationException}s you can do the following.
 * Configure the <code>exceptionMappings</code> property in your application
 * xml. This property is a java.util.Properties object that maps a
 * fully-qualified exception class name to a redirection url target. For
 * example:
 *
 * <pre>
 *  &lt;property name=&quot;exceptionMappings&quot;&gt;
 *    &lt;props&gt;
 *      &lt;prop&gt; key=&quot;org.springframework.security.DisabledException&quot;&gt;/disabled.jsp&lt;/prop&gt;
 *    &lt;/props&gt;
 *  &lt;/property&gt;
 * </pre>
 *
 * The example above would redirect all
 * {@link org.springframework.security.DisabledException}s thrown, to a page in the
 * web-application called /disabled.jsp.
 * <p>
 * Any {@link AuthenticationException} thrown that cannot be matched in the
 * <code>exceptionMappings</code> will be redirected to the
 * <code>authenticationFailureUrl</code>
 * <p>

 */
public class PreAuthenticationFilterEntryPoint implements AuthenticationEntryPoint, Ordered
{
    /**
     * Local log instance.
     */
    private static Log logger = LogFactory.getLog(PreAuthenticationFilterEntryPoint.class);

    /**
     * Order of filter in filter chain.
     */
    private int order = Integer.MAX_VALUE;

    /**
     * Exception mappings for determining where to go for specific exceptions.
     */
    private Properties exceptionMappings = new Properties();

    /**
     * Tells if we are to do a server side include of the error URL instead of a 302 redirect.
     */
    private boolean serverSideRedirect = false;
    
    /**
     * If true, causes any redirection URLs to be calculated minus the protocol
     * and context path (defaults to false).
     */
    private boolean useRelativeContext = false;

    /**
     * {@inheritDoc}
     */
    public void commence(final ServletRequest inRequest, final ServletResponse inResponse,
            final AuthenticationException inAuthenticationException) throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest) inRequest;
        HttpServletResponse response = (HttpServletResponse) inResponse;

        if (logger.isDebugEnabled())
        {
            logger.debug("Authentication request failed: " + inAuthenticationException.toString());
        }
        
        String failureUrl = determineFailureUrl(inAuthenticationException);

        if (failureUrl == null)
        {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied:"
                    + inAuthenticationException.getMessage());
        }
        else if (serverSideRedirect)
        {
            request.getRequestDispatcher(failureUrl).forward(request, response);
        }
        else
        {
            RedirectUtils.sendRedirect(request, response, failureUrl, useRelativeContext);
        }
    }

    /**
     * Return url to redirect to for a given {@link AuthenticationException}. If no mapping is present, null is
     * returned.
     * 
     * @param inAuthException
     *            The {@link AuthenticationException}.
     * @return url to redirect to for a given {@link AuthenticationException}. If no mapping is present, null is
     *         returned.
     */
    protected String determineFailureUrl(final AuthenticationException inAuthException)
    {
        return exceptionMappings.getProperty(inAuthException.getClass().getName());
    }
    
    /**
     * {@inheritDoc}
     */
    public int getOrder()
    {
        return order;
    }

    /**
     * Setter for filter order.
     * @param inOrder
     *            the order to set
     */
    public void setOrder(final int inOrder)
    {
        this.order = inOrder;
    }
    
    /**
     * Set value for useRelativeContext.
     * @param inUseRelativeContext If true, causes any redirection URLs to be calculated minus the protocol
     * and context path (defaults to false).
     */
    public void setUseRelativeContext(final boolean inUseRelativeContext)
    {
        this.useRelativeContext = inUseRelativeContext;
    }

    /**
     * Get value for useRelativeContext.
     * @return Value for useRelativeContext.
     */
    public Properties getExceptionMappings()
    {
        return new Properties(exceptionMappings);
    }

    /**
     * Set the exception mappings collection for this class.
     * @param inExceptionMappings Exception mappings collection for this class.
     */
    public void setExceptionMappings(final Properties inExceptionMappings)
    {
        this.exceptionMappings = inExceptionMappings;
    }

    /**
     * Tells if we are to do a server side include of the error URL instead of a 302 redirect.
     * 
     * @param inServerSideRedirect Value for serverSideRedirect.
     */
    public void setServerSideRedirect(final boolean inServerSideRedirect)
    {
        this.serverSideRedirect = inServerSideRedirect;
    }
}
