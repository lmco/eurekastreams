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
package org.eurekastreams.server.persistence.mappers.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.OrgTreeBuilderTestHelper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for GetChildOrgIdsByOrgIdDbMapper.
 * 
 */
public class GetChildOrgIdsByOrgIdDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetChildOrgIdsByOrgIdDbMapper sut;

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

        otb = new OrgTreeBuilderTestHelper(getEntityManager());
        otb.buildOrgTree();
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecute1()
    {
        Set<Long> childOrgIds = sut.execute(5L);
        assertEquals(2, childOrgIds.size());
        assertTrue(childOrgIds.contains(6L));
        assertTrue(childOrgIds.contains(7L));
    }

    /**
     * Test execute without a warmed cache.
     */
    @Test
    public void testExecute2()
    {
        Set<Long> childOrgIds = sut.execute(6L);
        assertEquals(2, childOrgIds.size());
        assertTrue(childOrgIds.contains(otb.getOrg6a().getId()));
        assertTrue(childOrgIds.contains(otb.getOrg6b().getId()));
    }

    /**
     * Test execute without a warmed cache, on a leaf node.
     */
    @Test
    public void testExecute3()
    {
        Set<Long> childOrgIds = sut.execute(otb.getOrg7a1().getId());
        assertEquals(0, childOrgIds.size());
    }
}
