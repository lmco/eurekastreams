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

import javax.servlet.Filter;
import javax.servlet.FilterConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.GetRootOrganizationIdAndShortName;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * Factory used to configure servlet filters.
 */
public final class FilterInitializer
{
    /** logger instance. */
    private static Log log = LogFactory.getLog(FilterInitializer.class);

    /**
     * Private constructor for utility class.
     */
    private FilterInitializer()
    {

    }

    /**
     * 
     * @param theFilter
     *            - the filter to configure
     * @param filterConfig
     *            - filter config params
     */
    public static void initializeFilter(final Filter theFilter, final FilterConfig filterConfig)
    {
        ApplicationContext context = (ApplicationContext) WebApplicationContextUtils
                .getWebApplicationContext(filterConfig.getServletContext());

        if (theFilter instanceof RootOrgCheckFilter)
        {
            RootOrgCheckFilter rootOrgFilter = (RootOrgCheckFilter) theFilter;

            GetRootOrganizationIdAndShortName getRootOrgIdMapper = (GetRootOrganizationIdAndShortName) context
                    .getBean("getRootOrganizationIdAndShortNameMapper");

            rootOrgFilter.setGetRootOrganizationIdMapper(getRootOrgIdMapper);
        }
    }

}
