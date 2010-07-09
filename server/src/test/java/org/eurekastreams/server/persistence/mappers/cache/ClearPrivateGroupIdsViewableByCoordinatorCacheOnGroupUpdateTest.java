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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for ClearActivityStreamSearchStringForUsersCache.
 */
public class ClearPrivateGroupIdsViewableByCoordinatorCacheOnGroupUpdateTest
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
     * Mocked cache.
     */
    private final Cache cache = context.mock(Cache.class);

    /**
     * System under test.
     */
    private ClearPrivateGroupIdsViewableByCoordinatorCacheOnGroupUpdate sut;

    /**
     * Mocked entity manager.
     */
    private EntityManager em = context.mock(EntityManager.class);

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new ClearPrivateGroupIdsViewableByCoordinatorCacheOnGroupUpdate();
        sut.setCache(cache);
        sut.setEntityManager(em);
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        final long groupId = 1L;

        final long p1id = 1837L;
        final long p2id = 8847L;
        final long p3id = 8777L;

        final String p1Key = CacheKeys.PRIVATE_GROUP_IDS_VIEWABLE_BY_PERSON_AS_COORDINATOR
                + p1id;
        final String p2Key = CacheKeys.PRIVATE_GROUP_IDS_VIEWABLE_BY_PERSON_AS_COORDINATOR
                + p2id;
        final String p3Key = CacheKeys.PRIVATE_GROUP_IDS_VIEWABLE_BY_PERSON_AS_COORDINATOR
                + p3id;

        final Query query = context.mock(Query.class);
        final List<Long> ids = new ArrayList<Long>();
        ids.add(p1id);
        ids.add(p2id);
        ids.add(p3id);

        context.checking(new Expectations()
        {
            {
                oneOf(em).createQuery(
                        "SELECT p.id FROM Person p, DomainGroup g "
                                + "WHERE g.id = :groupId "
                                + "AND p MEMBER OF g.coordinators");
                will(returnValue(query));

                oneOf(query).setParameter("groupId", groupId);

                oneOf(query).getResultList();
                will(returnValue(ids));

                oneOf(cache).delete(p1Key);
                oneOf(cache).delete(p2Key);
                oneOf(cache).delete(p3Key);
            }
        });

        // perform sut
        sut.execute(groupId);

        // verify
        context.assertIsSatisfied();
    }
}
