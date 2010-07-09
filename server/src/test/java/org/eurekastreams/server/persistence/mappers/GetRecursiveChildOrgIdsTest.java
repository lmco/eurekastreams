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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetRecursiveChildOrgIds.
 */
public class GetRecursiveChildOrgIdsTest extends CachedMapperTest
{
    /**
     * System under test.
     */
    private GetRecursiveChildOrgIds sut;

    /**
     * Org tree builder helper - creates nested orgs that we can't setup in DBUnit.
     */
    private OrgTreeBuilderTestHelper otb;

    /**
     * Setup.
     *
     * @throws Exception
     *             on error
     */
    @Before
    public void setup() throws Exception
    {
        super.setUp();

        sut = new GetRecursiveChildOrgIds();
        sut.setEntityManager(getEntityManager());
        sut.setCache(getCache());

        otb = new OrgTreeBuilderTestHelper(getEntityManager());
        otb.buildOrgTree();
    }

    /**
     * Test executes stores in cache.
     */
    @Test
    public void testExecuteStoresInCache()
    {
        Set<Long> results = sut.execute(5L);
        assertSame(results, sut.execute(5L));
        assertSame(getCache().get(CacheKeys.ORGANIZATION_RECURSIVE_CHILDREN + 5L), results);
    }

    /**
     * Test execute without a warmed cache.
     */
    @Test
    public void testExecuteWithoutWarmedCache1()
    {
        Set<Long> childOrgIds = sut.execute(5L);
        assertEquals(8, childOrgIds.size());
        assertTrue(childOrgIds.contains(6L));
        assertTrue(childOrgIds.contains(7L));
        assertTrue(childOrgIds.contains(otb.getOrg6a().getId()));
        assertTrue(childOrgIds.contains(otb.getOrg6a1().getId()));
        assertTrue(childOrgIds.contains(otb.getOrg6a2().getId()));
        assertTrue(childOrgIds.contains(otb.getOrg6b().getId()));
        assertTrue(childOrgIds.contains(otb.getOrg7a().getId()));
        assertTrue(childOrgIds.contains(otb.getOrg7a1().getId()));
    }

    /**
     * Test execute without a warmed cache.
     */
    @Test
    public void testExecuteWithoutWarmedCache2()
    {
        Set<Long> childOrgIds = sut.execute(6L);
        assertEquals(4, childOrgIds.size());
        assertTrue(childOrgIds.contains(otb.getOrg6a().getId()));
        assertTrue(childOrgIds.contains(otb.getOrg6a1().getId()));
        assertTrue(childOrgIds.contains(otb.getOrg6a2().getId()));
        assertTrue(childOrgIds.contains(otb.getOrg6b().getId()));
    }

    /**
     * Test execute without a warmed cache.
     */
    @Test
    public void testExecuteWithoutWarmedCache3()
    {
        Set<Long> childOrgIds = sut.execute(otb.getOrg6a().getId());
        assertEquals(2, childOrgIds.size());
        assertTrue(childOrgIds.contains(otb.getOrg6a1().getId()));
        assertTrue(childOrgIds.contains(otb.getOrg6a2().getId()));
    }

    /**
     * Test execute without a warmed cache - leaf node.
     */
    @Test
    public void testExecuteWithoutWarmedCache4()
    {
        Set<Long> childOrgIds = sut.execute(otb.getOrg6a1().getId());
        assertEquals(0, childOrgIds.size());
    }

    /**
     * Test that we pull from cache when it's available.
     */
    @Test
    public void testExecuteWithWarmedCache()
    {
        Set<Long> childOrgIds = new HashSet<Long>();
        childOrgIds.add(4L);
        childOrgIds.add(9L);
        getCache().set(CacheKeys.ORGANIZATION_RECURSIVE_CHILDREN + 5L, childOrgIds);
        assertSame(childOrgIds, sut.execute(5L));
    }
}
