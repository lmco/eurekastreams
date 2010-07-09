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

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetRecursiveParentOrgIds.
 */
public class GetRecursiveParentOrgIdsTest extends CachedMapperTest
{
    /**
     * System under test.
     */
    private GetRecursiveParentOrgIds sut;

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

        sut = new GetRecursiveParentOrgIds();
        sut.setEntityManager(getEntityManager());
        sut.setCache(getCache());

        otb = new OrgTreeBuilderTestHelper(getEntityManager());
        otb.buildOrgTree();
    }

    /**
     * Test execute without a warmed cache.
     */
    @Test
    public void testExecuteWithoutWarmedCacheAtTopLevel()
    {
        List<Long> parentOrgIds = sut.execute(5L);
        assertEquals(0, parentOrgIds.size());
    }

    /**
     * Test execute without a warmed cache.
     */
    @Test
    public void testExecuteWithoutWarmedCache()
    {
        List<Long> parentOrgIds = sut.execute(6L);
        assertEquals(1, parentOrgIds.size());
        assertTrue(parentOrgIds.contains(5L));
    }

    /**
     * Test execute stores in cache.
     */
    @Test
    public void testExecuteStoresInCache()
    {
        List<Long> results = sut.execute(6L);
        assertEquals(results.get(0), getCache().getList(CacheKeys.ORGANIZATION_PARENTS_RECURSIVE + 6L).get(0));
    }

    /**
     * Test execute without a warmed cache.
     */
    @Test
    public void testExecuteWithoutWarmedCache3()
    {
        List<Long> parentOrgIds = sut.execute(otb.getOrg6a().getId());
        assertEquals(2, parentOrgIds.size());
        assertTrue(parentOrgIds.contains(6L));
        assertTrue(parentOrgIds.contains(5L));
    }

    /**
     * Test execute without a warmed cache - leaf node.
     */
    @Test
    public void testExecuteWithoutWarmedCache4()
    {
        List<Long> parentOrgIds = sut.execute(otb.getOrg6a1().getId());
        assertEquals(3, parentOrgIds.size());
        assertTrue(parentOrgIds.contains(otb.getOrg6a().getId()));
        assertTrue(parentOrgIds.contains(6L));
        assertTrue(parentOrgIds.contains(5L));
    }

    /**
     * Test that we pull from cache when it's available.
     */
    @Test
    public void testExecuteWithWarmedCache()
    {
        List<Long> parentOrgIds = new ArrayList<Long>();
        parentOrgIds.add(4L);
        parentOrgIds.add(9L);
        getCache().setList(CacheKeys.ORGANIZATION_PARENTS_RECURSIVE + 5L, parentOrgIds);

        List<Long> returned = sut.execute(5L);

        for (Long parentOrgId : parentOrgIds)
        {
        	assertTrue(returned.contains(parentOrgId));
        	returned.remove(parentOrgId);
        }

       assertEquals(0, returned.size());
    }
}
