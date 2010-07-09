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

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.eurekastreams.server.persistence.mappers.GetRootOrganizationIdAndShortName;

/**
 * OrganizationHierarchyCache Tests the root org check filter.
 */
public class RootOrgCheckFilterTest
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
     * System under test.
     */
    private RootOrgCheckFilter sut;

    /**
     * root org id mapper mock.
     */
    private GetRootOrganizationIdAndShortName getRootOrgIdMapper = context
            .mock(GetRootOrganizationIdAndShortName.class);

    /**
     * Setup the test fixtures.
     */
    @Before
    public final void setUp()
    {
        sut = new RootOrgCheckFilter();
        sut.setGetRootOrganizationIdMapper(getRootOrgIdMapper);
    }

    /**
     * Tests destroy, does nothing.
     */
    @Test
    public final void destroyTest()
    {
        sut.destroy();
    }

    /**
     * Test executing the filter when a root org exists.
     * 
     * @throws ServletException
     *             unexpected.
     * @throws IOException
     *             unexpected.
     */
    @Test
    public final void doFilterRootOrgExists() throws IOException, ServletException
    {
        final ServletRequest request = context.mock(ServletRequest.class);
        final ServletResponse response = context.mock(ServletResponse.class);
        final FilterChain chain = context.mock(FilterChain.class);

        context.checking(new Expectations()
        {
            {
                oneOf(getRootOrgIdMapper).getRootOrganizationId();
                will(returnValue(1L));

                oneOf(chain).doFilter(request, response);
            }
        });

        sut.doFilter(request, response, chain);

        context.assertIsSatisfied();
    }

    /**
     * Test executing the filter when no root org exists.
     * 
     * @throws ServletException
     *             unexpected.
     * @throws IOException
     *             unexpected.
     */
    @Test
    public final void doFilterNoRootOrg() throws IOException, ServletException
    {
        final ServletRequest request = context.mock(ServletRequest.class);
        final ServletResponse response = context.mock(HttpServletResponse.class);
        final FilterChain chain = context.mock(FilterChain.class);

        context.checking(new Expectations()
        {
            {
                oneOf(getRootOrgIdMapper).getRootOrganizationId();
                will(returnValue(null));

                oneOf((HttpServletResponse) response).sendRedirect("./setup");
            }
        });

        sut.doFilter(request, response, chain);

        context.assertIsSatisfied();
    }
}
