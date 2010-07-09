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
package org.eurekastreams.server.persistence.mappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test fixture for GetOrgCoordinatorsTest.
 */
public class GetOrgCoordinatorsTest extends CachedMapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetOrgCoordinators sut;

    /**
     * Test execute on 5 and 6 making sure no duplicates.
     */
    @Test
    public void testExecuteMultiOrgsDuplicateCoords()
    {
        // even with multiple orgs only one of each coord is returned.
        Set<Long> orgList = new HashSet<Long>();
        // one coord.
        orgList.add(5L);
        // two coords one duplicate.
        orgList.add(6L);
        Set<Long> orgCoordinatorIds = sut.execute(orgList);
        // should be no duplicated.
        assertEquals(3, orgCoordinatorIds.size());
    }

    /**
     * Test execute on 5 alone.
     */
    @Test
    public void testExecuteOrg5()
    {
        Set<Long> orgList = new HashSet<Long>();
        orgList.add(5L);
        Set<Long> parentOrgIds = sut.execute(orgList);
        assertEquals(2, parentOrgIds.size());
    }

    /**
     * Test execute on 6 alone.
     */
    @Test
    public void testExecuteOrg6()
    {
        Set<Long> orgList = new HashSet<Long>();
        orgList.add(6L);
        Set<Long> parentOrgIds = sut.execute(orgList);
        assertEquals(2, parentOrgIds.size());
    }

    /**
     * Test execute on 6 alone.
     */
    @Test
    public void testExecuteOrg6WithLongInput()
    {
        Set<Long> parentOrgIds = sut.execute(6L);
        assertEquals(2, parentOrgIds.size());
    }

    /**
     * Test execute on cache.
     */
    @Test
    public void testExecuteWithCache()
    {
        Long orgId = 8L;

        Set<Long> coordList = new HashSet<Long>();
        coordList.add(1L);
        coordList.add(6L);

        getCache().set(CacheKeys.ORGANIZATION_COORDINATORS_BY_ORG_ID + orgId, coordList);

        Set<Long> orgList = new HashSet<Long>();
        orgList.add(orgId);
        Set<Long> parentOrgIds = sut.execute(orgList);

        assertEquals(2, parentOrgIds.size());
    }

    /**
     * Test execute stores in cache.
     */
    @Test
    public void testExecuteStoresInCache()
    {
        Set<Long> orgList = new HashSet<Long>();
        orgList.add(6L);

        assertTrue(sut.execute(orgList).containsAll(sut.execute(orgList)));
        Set<Long> results = sut.execute(orgList);
        assertTrue(results.containsAll((Collection<?>) getCache().get(
                CacheKeys.ORGANIZATION_COORDINATORS_BY_ORG_ID + 6L)));

    }
}
