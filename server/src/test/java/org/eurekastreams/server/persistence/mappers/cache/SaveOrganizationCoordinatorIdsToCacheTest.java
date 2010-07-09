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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eurekastreams.server.action.request.profile.OrganizationCacheUpdaterRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for SaveOrganizationCoordinatorIdsToCache class.
 * 
 */
public class SaveOrganizationCoordinatorIdsToCacheTest
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
     * {@link Cache}.
     */
    private Cache cache = context.mock(Cache.class);

    /**
     * Organization id used for test.
     */
    private Long orgId = 5L;

    /**
     * {@link OrganizationCacheUpdaterRequest}.
     */
    private OrganizationCacheUpdaterRequest request = context.mock(OrganizationCacheUpdaterRequest.class);

    /**
     * System under test.
     */
    private SaveOrganizationCoordinatorIdsToCacheTestHelper sut = new SaveOrganizationCoordinatorIdsToCacheTestHelper();

    /**
     * Test.
     */
    @Test
    public void testExecuteCreate()
    {
        final Long coordId = 9L;
        final Set<Long> coordIds = new HashSet<Long>(Arrays.asList(coordId));

        context.checking(new Expectations()
        {
            {
                allowing(request).getOrganizationId();
                will(returnValue(orgId));

                allowing(request).getCoordinatorIds();
                will(returnValue(coordIds));

                allowing(request).getOriginalCoordinatorIds();
                will(returnValue(null));

                allowing(cache).set(CacheKeys.ORGANIZATION_COORDINATORS_BY_ORG_ID + orgId, coordIds);

                allowing(cache).addToSet(CacheKeys.ORG_IDS_DIRECTLY_COORD_BY_PERSON + coordId, orgId);
            }
        });

        sut.execute(request);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testExecuteUpdate()
    {
        final Long coordId = 9L;
        final Set<Long> coordIds = new HashSet<Long>(Arrays.asList(coordId));

        final Long origCoordId = 8L;
        final Set<Long> origCoordIds = new HashSet<Long>(Arrays.asList(origCoordId));

        context.checking(new Expectations()
        {
            {
                allowing(request).getOrganizationId();
                will(returnValue(orgId));

                allowing(request).getCoordinatorIds();
                will(returnValue(coordIds));

                allowing(request).getOriginalCoordinatorIds();
                will(returnValue(origCoordIds));

                allowing(cache).set(CacheKeys.ORGANIZATION_COORDINATORS_BY_ORG_ID + orgId, coordIds);

                allowing(cache).addToSet(CacheKeys.ORG_IDS_DIRECTLY_COORD_BY_PERSON + coordId, orgId);

                allowing(cache).removeFromSet(CacheKeys.ORG_IDS_DIRECTLY_COORD_BY_PERSON + origCoordId, orgId);
            }
        });

        sut.execute(request);
        context.assertIsSatisfied();
    }

    /**
     * Helper class for unit testing.
     * 
     */
    public class SaveOrganizationCoordinatorIdsToCacheTestHelper extends SaveOrganizationCoordinatorIdsToCache
    {

        /**
         * Return mock cache object.
         * 
         * @return mocked Cache object.
         */
        @Override
        public Cache getCache()
        {
            return cache;
        }

    }

}
