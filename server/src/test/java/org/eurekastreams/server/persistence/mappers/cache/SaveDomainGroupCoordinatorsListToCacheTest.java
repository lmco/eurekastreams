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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.cache.testhelpers.SimpleMemoryCache;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GroupCoordinatorManager.
 */
public class SaveDomainGroupCoordinatorsListToCacheTest
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
    private SaveDomainGroupCoordinatorsListToCache sut = new SaveDomainGroupCoordinatorsListToCache();

    /**
     * Mocked cache.
     */
    private SimpleMemoryCache cache;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        cache = new SimpleMemoryCache();
        sut.setCache(cache);
    }

    /**
     * Test saving a coordinator list to cache.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecute()
    {
        final DomainGroup g = context.mock(DomainGroup.class);
        final Person p1 = context.mock(Person.class, "p1");
        final Person p2 = context.mock(Person.class, "p2");

        final Set<Person> coordinators = new HashSet<Person>();
        coordinators.add(p1);
        coordinators.add(p2);

        final Long gId = 3333L;
        final Long p1Id = 82737L;
        final Long p2Id = 37772L;

        context.checking(new Expectations()
        {
            {
                allowing(g).getCoordinators();
                will(returnValue(coordinators));

                allowing(g).getId();
                will(returnValue(gId));

                allowing(p1).getId();
                will(returnValue(p1Id));

                allowing(p2).getId();
                will(returnValue(p2Id));
            }
        });

        List<Long> returnValue = sut.execute(g);

        List<Long> cachedCoordinators = cache
                .getList(CacheKeys.COORDINATOR_PERSON_IDS_BY_GROUP_ID + gId);

        assertEquals(returnValue, cachedCoordinators);
        assertEquals(2, cachedCoordinators.size());
        assertTrue(cachedCoordinators.contains(p1Id));
        assertTrue(cachedCoordinators.contains(p2Id));

        context.assertIsSatisfied();
    }

}
