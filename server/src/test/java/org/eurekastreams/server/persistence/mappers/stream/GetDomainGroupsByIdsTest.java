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
package org.eurekastreams.server.persistence.mappers.stream;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests getting person DTOs from a list of person ids.
 */
public class GetDomainGroupsByIdsTest extends CachedMapperTest
{
    /**
     * The main id to test with.
     */
    private static final long GROUP_ID = 2;

    /**
     * An additional id to test with.
     */
    private static final long GROUP_ID2 = 3;

    /**
     * System under test.
     */
    @Autowired
    private GetDomainGroupsByIds mapper;

    /**
     * Verifies the group returned is correct for group 2.
     *
     * @param group
     *            group returned
     */
    private void verifyGroup2(final DomainGroupModelView group)
    {
        assertNotNull(group);
        assertEquals("D Group 1 Name", group.getName());
        assertEquals("Child 2 Organization Name", group.getParentOrganizationName());
        assertEquals("Volgon-Swatter Prefect", group.getPersonCreatedByDisplayName());
    }

    /**
     * test.
     */
    @Test
    public void testExecute()
    {
        List<Long> list = new ArrayList<Long>();
        list.add(new Long(GROUP_ID));
        List<DomainGroupModelView> results = mapper.execute(list);
        assertEquals(1, results.size());
        verifyGroup2(results.get(0));

        // now that the cache should be populated, run the execute again
        results = mapper.execute(list);
        assertEquals(1, results.size());
        verifyGroup2(results.get(0));
    }

    /**
     * test.
     */
    @Test
    public void testExecuteSinlge()
    {
        DomainGroupModelView result = mapper.execute(GROUP_ID);
        verifyGroup2(result);

        // now that the cache should be populated, run the execute again
        result = mapper.execute(GROUP_ID);
        verifyGroup2(result);
    }

    /**
     * test.
     */
    @Test
    public void testExecuteWithMultipleIds()
    {
        List<Long> list = new ArrayList<Long>();
        list.add(new Long(GROUP_ID));
        list.add(new Long(GROUP_ID2));
        List<DomainGroupModelView> results = mapper.execute(list);
        assertEquals(2, results.size());

        results = mapper.execute(list);
        assertEquals(2, results.size());
    }
}
