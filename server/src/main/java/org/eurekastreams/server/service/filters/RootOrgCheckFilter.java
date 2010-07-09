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
package org.eurekastreams.server.service.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.eurekastreams.server.persistence.mappers.GetRootOrganizationIdAndShortName;

/**
 * Filter that determines if the app has been setup, if not redirects to ./setup. It is assumed the app is not setup if
 * a root organization does not exist.
 */
public class RootOrgCheckFilter implements Filter
{
    /**
     * Mapper to get the root organization id.
     */
    private GetRootOrganizationIdAndShortName getRootOrganizationIdMapper;

    /**
     * Destroy the filter, does nothing here.
     */
    public void destroy()
    {
        // Purposely left blank
    }

    /**
     * @param request
     *            the incoming request.
     * @param response
     *            the outgoing response.
     * @param chain
     *            the filter chain.
     * @exception IOException
     *                not expected.
     * @exception ServletException
     *                not expected.
     */
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException
    {
        if (getRootOrganizationIdMapper.getRootOrganizationId() == null)
        {
            HttpServletResponse hResponse = (HttpServletResponse) response;
            hResponse.sendRedirect("./setup");
        }
        else
        {
            chain.doFilter(request, response);
        }
    }

    /**
     * Initialize the filter.
     * 
     * @param filterConfig
     *            the config.
     * @exception ServletException
     *                not expected.
     */
    public void init(final FilterConfig filterConfig) throws ServletException
    {
        FilterInitializer.initializeFilter(this, filterConfig);
    }

    /**
     * @param inGetRootOrganizationIdMapper
     *            the getRootOrganizationIdMapper to set
     */
    public void setGetRootOrganizationIdMapper(final GetRootOrganizationIdAndShortName inGetRootOrganizationIdMapper)
    {
        this.getRootOrganizationIdMapper = inGetRootOrganizationIdMapper;
    }
}
