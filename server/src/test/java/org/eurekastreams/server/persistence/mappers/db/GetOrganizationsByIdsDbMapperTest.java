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

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for GetOrganizationsByIdsDbMapper.
 * 
 */
public class GetOrganizationsByIdsDbMapperTest extends MapperTest
{
    /**
     * The main id to test with.
     */
    private static final long ORG_ID = 5;

    /**
     * An additional id to test with.
     */
    private static final long ORG_ID2 = 6;

    /**
     * System under test.
     */
    @Autowired
    private GetOrganizationsByIdsDbMapper mapper;

    /**
     * Verifies the returned org matches what is expected for org 5.
     * 
     * @param org
     *            The returned org.
     */
    private void verifyOrg5(final OrganizationModelView org)
    {
        assertNotNull(org);
        assertEquals("Test Organization Name", org.getName());
        assertEquals("Mission Statement", org.getDescription());
        assertEquals(5L, org.getParentOrganizationId());
    }

    /**
     * test.
     */
    @Test
    public void testExecute()
    {
        List<Long> list = new ArrayList<Long>();
        list.add(new Long(ORG_ID));
        List<OrganizationModelView> results = mapper.execute(list);
        assertEquals(1, results.size());
        verifyOrg5(results.get(0));
    }

    /**
     * test.
     */
    @Test
    public void testExecuteWithMultipleIds()
    {
        List<Long> list = new ArrayList<Long>(Arrays.asList(new Long(ORG_ID), new Long(ORG_ID2)));
        List<OrganizationModelView> results = mapper.execute(list);
        assertEquals(2, results.size());

        OrganizationModelView org5 = null;
        OrganizationModelView org6 = null;
        for (OrganizationModelView org : results)
        {
            if (org.getEntityId() == 5L)
            {
                org5 = org;
            }
            else
            {
                org6 = org;
            }
        }
        assertEquals(5L, org5.getEntityId());
        assertEquals(6L, org6.getEntityId());
        assertEquals("Bar", org6.getBannerId());
    }

}
