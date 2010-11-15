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
package org.eurekastreams.server.persistence.mappers.cache;

import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.task.TaskHandler;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.StarredActivity;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Test;

/**
 * Test fixture for CacheInitializer.
 */
public class CacheInitializerTest
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
     * Teardown method.
     */
    @After
    public void tearDown()
    {
        DomainGroup.setEntityCacheUpdater(null);
        Organization.setEntityCacheUpdater(null);
        Person.setEntityCacheUpdater(null);
        StarredActivity.setEntityCacheUpdater(null);
    }

    /**
     * Test wiring up the entity cache updaters.
     */
    public void testWireUpdaters()
    {
        final DomainGroupCacheLoader domainGroupCacheLoader = context.mock(DomainGroupCacheLoader.class);
        final OrganizationHierarchyCacheLoader orgHierarchyCacheLoader = context
                .mock(OrganizationHierarchyCacheLoader.class);
        final PersonCacheLoader personCacheLoader = context.mock(PersonCacheLoader.class);

        new CacheInitializer().wireCacheUpdaters(domainGroupCacheLoader, orgHierarchyCacheLoader, personCacheLoader);

        // can't really test b/c there's no getter - oh well.
    }

    /**
     * Tests cache initialization.
     * 
     * @throws Exception
     *             on error.
     */
    @Test
    public void testInitializeCache() throws Exception
    {
        final TaskHandler submitter = context.mock(TaskHandler.class);

        context.checking(new Expectations()
        {
            {
                exactly(2).of(submitter).handleTask(with(any(UserActionRequest.class)));
            }
        });

        // perform SUT
        new CacheInitializer().initializeCache(submitter);

        // assert
        context.assertIsSatisfied();
    }
}
