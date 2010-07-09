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

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.eurekastreams.server.persistence.mappers.GetRootOrganizationIdAndShortName;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.context.WebApplicationContext;

/**
 * Test class for FilterInitializer.
 * 
 */
public class FilterInitializerTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Test setup (unused in this case).
     */
    @Before
    public final void setup()
    {
    }

    /**
     * Verify that RegisteredUserFilter initialization takes place.
     * 
     * @throws IOException
     *             input/output exception.
     * @throws ServletException
     *             servlet exception.
     */
    @Test
    public final void testInitializeFilterWithRegisteredUserFilter() throws IOException, ServletException
    {
        final RootOrgCheckFilter filter = context.mock(RootOrgCheckFilter.class);
        final FilterConfig filterConfig = context.mock(FilterConfig.class);
        final ServletContext servletContext = context.mock(ServletContext.class);
        final WebApplicationContext webContext = context.mock(WebApplicationContext.class);
        final GetRootOrganizationIdAndShortName rootOrgMapper = context.mock(GetRootOrganizationIdAndShortName.class);
        
        context.checking(new Expectations()
        {
            {
                oneOf(filterConfig).getServletContext();
                will(returnValue(servletContext));

                oneOf(servletContext).getAttribute("org.springframework.web.context.WebApplicationContext.ROOT");
                will(returnValue(webContext));

                oneOf(webContext).getBean("getRootOrganizationIdAndShortNameMapper");
                will(returnValue(rootOrgMapper));
                
                oneOf(filter).setGetRootOrganizationIdMapper(rootOrgMapper);
            }
        });

        FilterInitializer.initializeFilter(filter, filterConfig);
        context.assertIsSatisfied();
    }
}
