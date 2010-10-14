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

import java.util.HashSet;
import java.util.Set;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for ClearActivityStreamSearchStringCacheOnOrgUpdate.
 * 
 */
public class ClearPrivateGroupIdsViewableByCoordinatorCacheOnOrgUpdateTest
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
     * Mapper to get organization coordinator ids straight from the database.
     */
    private DomainMapper<Long, Set<Long>> getOrgCoordinatorIdsFromDbMapper = context.mock(DomainMapper.class);

    /**
     * System under test.
     */
    private final ClearPrivateGroupIdsViewableByCoordinatorCacheOnOrgUpdate sut =
    // line break
    new ClearPrivateGroupIdsViewableByCoordinatorCacheOnOrgUpdate(getOrgCoordinatorIdsFromDbMapper);

    /**
     * Mocked cache.
     */
    private final Cache cache = context.mock(Cache.class);

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut.setCache(cache);
    }

    /**
     * Org id.
     */
    final Long orgId = 38277117L;

    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        final Long coord1 = 3872L;
        final Long coord2 = 33872L;
        final Long coord3 = 333872L;

        final Set<Long> coordinatorIds = new HashSet<Long>();
        coordinatorIds.add(coord1);
        coordinatorIds.add(coord2);
        coordinatorIds.add(coord3);

        context.checking(new Expectations()
        {
            {
                oneOf(getOrgCoordinatorIdsFromDbMapper).execute(orgId);
                will(returnValue(coordinatorIds));

                oneOf(cache).delete(CacheKeys.PRIVATE_GROUP_IDS_VIEWABLE_BY_PERSON_AS_COORDINATOR + coord1);
                oneOf(cache).delete(CacheKeys.PRIVATE_GROUP_IDS_VIEWABLE_BY_PERSON_AS_COORDINATOR + coord2);
                oneOf(cache).delete(CacheKeys.PRIVATE_GROUP_IDS_VIEWABLE_BY_PERSON_AS_COORDINATOR + coord3);
            }
        });

        // perform sut
        sut.execute(orgId);

        // verify
        context.assertIsSatisfied();
    }
}
