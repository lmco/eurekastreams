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

import javax.servlet.http.HttpServletRequest;
import org.springframework.security.ui.FilterChainOrder;
import org.springframework.security.ui.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.ui.preauth.PreAuthenticatedCredentialsNotFoundException;

/**
 * A simple pre-authenticated filter which obtains the username from a request attribute, for use with systems
 * that have the ability to put request attributes in the request to pass along.
 * <p>
 * The property <tt>principalRequestAttribute</tt> is the name of the request header that contains the username. It 
 * defaults to "REMOTE_USER".
 * 
 */
public class RequestAttributePreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter
{
	/**
	 * Filter order chain value for filter execution.
	 */
	private static final int FILTER_CHAIN_ORDER = FilterChainOrder.PRE_AUTH_FILTER;
	
	/**
	 * The default attribute name to find in the request for the username.  This can be overridden in the context
	 * configuration file
	 */
	private String principalRequestAttribute = "REMOTE_USER";  //default request attribute name
	
	/**
	 * The credentials request header string.
	 */
	private String credentialsRequestHeader;				   //default header value is null = does not exist

	/**
	 * Read and returns the header named by <tt>principalRequestHeader</tt> from the request.
	 * 
	 * @param request  The Servlet request object.
	 * @return String  the principal.
	 */
	protected Object getPreAuthenticatedPrincipal(final HttpServletRequest request)
	{
		String principal = (String) request.getAttribute(principalRequestAttribute);
		
		if (principal == null)
		{
			throw new PreAuthenticatedCredentialsNotFoundException(principalRequestAttribute
					+ " attribute not found in request.");
		}

		return principal;
	}	
		
	/**
	 * A getter for the credentials.
	 * 
	 * @param request  The Servlet request object.
	 * @return Object  The credentials object as found in the request header.
	 */
	protected Object getPreAuthenticatedCredentials(final HttpServletRequest request)
	{
		if (credentialsRequestHeader != null)
		{
			String credentials = request.getHeader(credentialsRequestHeader);	
			if (credentials == null)
			{
				throw new PreAuthenticatedCredentialsNotFoundException(credentialsRequestHeader
						+ " header value not found in request.");
			}
			
			return credentials;
		}
		return "N/A";
	}
	
	/**
	 * A setter for the principal request attribute.
	 * 
	 * @param prinRequestAttribute  the username as the principal.
	 */
	public void setPrincipalRequestAttribute(final String prinRequestAttribute)
	{
		this.principalRequestAttribute = prinRequestAttribute;
	}

	/**
	 * A setter for the credentials request header.
	 * 
	 * @param credRequestHeader  The credentials request header.
	 */
	public void setCredentialsRequestHeader(final String credRequestHeader)
	{
		this.credentialsRequestHeader = credRequestHeader;
	}

	/**
	 * A getter for the order this filter should execute in the chain.
	 * 
	 * @return int  The order in the filter chain to execute.
	 */
	public int getOrder()
	{
		return FILTER_CHAIN_ORDER;
	}
}
