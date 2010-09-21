/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution;

import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.persistence.mappers.cache.DomainGroupCacheLoader;
import org.eurekastreams.server.persistence.mappers.cache.OrganizationHierarchyCacheLoader;
import org.eurekastreams.server.persistence.mappers.cache.PersonCacheLoader;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link InitializeCacheExecution} class.
 *
 */
public class InitializeCacheExecutionTest
{
    /**
     * System under test.
     */
    private InitializeCacheExecution sut;

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
     * Mocked loader for domain groups.
     */
    private DomainGroupCacheLoader domainGroupCacheLoader = context.mock(DomainGroupCacheLoader.class);

    /**
     * Mocked loader for organizations.
     */
    private OrganizationHierarchyCacheLoader organizationCacheLoader = context
            .mock(OrganizationHierarchyCacheLoader.class);

    /**
     * Mocked loader for people.
     */
    private PersonCacheLoader personCacheLoader = context.mock(PersonCacheLoader.class);

    /**
     * Setup the test.
     */
    @Before
    public final void setUp()
    {
        sut = new InitializeCacheExecution(domainGroupCacheLoader, organizationCacheLoader, personCacheLoader);
    }

    /**
     * Testing the action.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void testPerformAction() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(domainGroupCacheLoader).getCache();
                oneOf(domainGroupCacheLoader).initialize();
                oneOf(organizationCacheLoader).initialize();
                oneOf(personCacheLoader).initialize();
            }
        });
        ServiceActionContext currentContext = new ServiceActionContext(null, null);
        sut.execute(currentContext);
        context.assertIsSatisfied();
    }

    /**
     * Testing the action.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void testPerformActionWithException() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(domainGroupCacheLoader).getCache();
                oneOf(domainGroupCacheLoader).initialize();
                oneOf(organizationCacheLoader).initialize();
                oneOf(personCacheLoader).initialize();
                will(throwException(new Exception()));
            }
        });
        ServiceActionContext currentContext = new ServiceActionContext(null, null);
        sut.execute(currentContext);
        context.assertIsSatisfied();
    }
}
