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

import org.eurekastreams.server.domain.OrganizationTreeDTO;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test fixture for GetOrganizationTreeDTO.
 */
public class GetOrganizationTreeDTOTest extends CachedMapperTest
{
    /**
     * Org tree builder helper - creates nested orgs that we can't setup in DBUnit.
     */
    private OrgTreeBuilderTestHelper otb;

    /**
     * System under test.
     */
    @Autowired
    private GetOrganizationTreeDTO sut;

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
     * Test that the tree is stored in cache.
     */
    @Test
    public void testExecuteStoresInCache()
    {
        sut.execute();
        assertSame(getCache().get(CacheKeys.ORGANIZATION_TREE_DTO), sut.execute());
    }

    /**
     * test the execute() method brings back an org hierarchy tree we'd expect.
     */
    @Test
    public void testExecute()
    {
        OrganizationTreeDTO orgTree = sut.execute();

        assertEquals(5L, (long) orgTree.getOrgId());
        assertEquals(2, orgTree.getChildren().size());
        assertEquals("Test Organization Name", orgTree.getDisplayName());
        assertEquals("tstorgname", orgTree.getShortName());

        OrganizationTreeDTO org6 = null;
        OrganizationTreeDTO org7 = null;
        for (OrganizationTreeDTO org : orgTree.getChildren())
        {
            if (org.getOrgId() == 6L)
            {
                org6 = org;
            }
            else if (org.getOrgId() == 7L)
            {
                org7 = org;
            }
        }

        // verify org 6
        assertEquals(2, org6.getChildren().size());
        assertEquals("Child 1 Organization Name", org6.getDisplayName());
        assertEquals("child1orgname", org6.getShortName());

        // verify org 7
        assertEquals(1, org7.getChildren().size());
        assertEquals("Child 2 Organization Name", org7.getDisplayName());
        assertEquals("child2orgname", org7.getShortName());

        // find 6a, 6b
        OrganizationTreeDTO org6a = null;
        OrganizationTreeDTO org6b = null;
        for (OrganizationTreeDTO org : org6.getChildren())
        {
            if (org.getOrgId() == otb.getOrg6a().getId())
            {
                org6a = org;
            }
            else if (org.getOrgId() == otb.getOrg6b().getId())
            {
                org6b = org;
            }
        }

        // verify org 6a
        assertEquals(2, org6a.getChildren().size());
        assertEquals(otb.getOrg6a().getId(), (long) org6a.getOrgId());
        assertEquals(otb.getOrg6a().getShortName(), org6a.getShortName());
        assertEquals(otb.getOrg6a().getName(), org6a.getDisplayName());

        // verify org 6b
        assertEquals(0, org6b.getChildren().size());
        assertEquals(otb.getOrg6b().getId(), (long) org6b.getOrgId());
        assertEquals(otb.getOrg6b().getShortName(), org6b.getShortName());
        assertEquals(otb.getOrg6b().getName(), org6b.getDisplayName());

        // find org 6a1, 6a2
        OrganizationTreeDTO org6a1 = null;
        OrganizationTreeDTO org6a2 = null;

        for (OrganizationTreeDTO org : org6a.getChildren())
        {
            if (org.getOrgId() == otb.getOrg6a1().getId())
            {
                org6a1 = org;
            }
            else if (org.getOrgId() == otb.getOrg6a2().getId())
            {
                org6a2 = org;
            }
        }

        // verify 6a1
        assertEquals(0, org6a1.getChildren().size());
        assertEquals(otb.getOrg6a1().getId(), (long) org6a1.getOrgId());
        assertEquals(otb.getOrg6a1().getShortName(), org6a1.getShortName());
        assertEquals(otb.getOrg6a1().getName(), org6a1.getDisplayName());

        // verify 6a2
        assertEquals(0, org6a2.getChildren().size());
        assertEquals(otb.getOrg6a2().getId(), (long) org6a2.getOrgId());
        assertEquals(otb.getOrg6a2().getShortName(), org6a2.getShortName());
        assertEquals(otb.getOrg6a2().getName(), org6a2.getDisplayName());

        // verify 7a
        OrganizationTreeDTO org7a = org7.getChildren().get(0);
        assertEquals(1, org7.getChildren().size());
        assertEquals(otb.getOrg7a().getId(), (long) org7a.getOrgId());
        assertEquals(otb.getOrg7a().getShortName(), org7a.getShortName());
        assertEquals(otb.getOrg7a().getName(), org7a.getDisplayName());

        // verify 7a1
        OrganizationTreeDTO org7a1 = org7a.getChildren().get(0);
        assertEquals(0, org7a1.getChildren().size());
        assertEquals(otb.getOrg7a1().getId(), (long) org7a1.getOrgId());
        assertEquals(otb.getOrg7a1().getShortName(), org7a1.getShortName());
        assertEquals(otb.getOrg7a1().getName(), org7a1.getDisplayName());
    }
}
