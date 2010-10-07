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

import java.util.List;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.OrgTreeBuilderTestHelper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for GetParentOrgIdsRecursiveByOrgIdDbMapper.
 * 
 */
public class GetParentOrgIdsRecursiveByOrgIdDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetParentOrgIdsRecursiveByOrgIdDbMapper sut;

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
     * Test execute without a warmed cache.
     */
    @Test
    public void testExecuteAtTopLevel()
    {
        List<Long> parentOrgIds = sut.execute(5L);
        assertEquals(0, parentOrgIds.size());
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        List<Long> parentOrgIds = sut.execute(6L);
        assertEquals(1, parentOrgIds.size());
        assertTrue(parentOrgIds.contains(5L));
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecute1()
    {
        List<Long> parentOrgIds = sut.execute(otb.getOrg6a().getId());
        assertEquals(2, parentOrgIds.size());
        assertTrue(parentOrgIds.contains(6L));
        assertTrue(parentOrgIds.contains(5L));
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecute2()
    {
        List<Long> parentOrgIds = sut.execute(otb.getOrg6a1().getId());
        assertEquals(3, parentOrgIds.size());
        assertTrue(parentOrgIds.contains(otb.getOrg6a().getId()));
        assertTrue(parentOrgIds.contains(6L));
        assertTrue(parentOrgIds.contains(5L));
    }

}
