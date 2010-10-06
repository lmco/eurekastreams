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
package org.eurekastreams.server.persistence.mappers.db;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for GetGroupsByIdsDbMapper.
 * 
 */
public class GetGroupsByIdsDbMapperTest extends MapperTest
{
    /**
     * The main id to test with.
     */
    private static final long GROUP_ID1 = 1;

    /**
     * An additional id to test with.
     */
    private static final long GROUP_ID2 = 2;

    /**
     * System under test.
     */
    @Autowired
    private GetGroupsByIdsDbMapper mapper;

    /**
     * test.
     */
    @Test
    public void testExecute()
    {
        List<Long> list = new ArrayList<Long>(Arrays.asList(GROUP_ID1));
        list.add(new Long(GROUP_ID1));
        List<DomainGroupModelView> results = mapper.execute(list);
        assertEquals(1, results.size());

        assertEquals("E Group 1 Name", results.get(0).getName());
    }

    /**
     * test.
     */
    @Test
    public void testExecuteWithMultipleIds()
    {
        List<Long> list = new ArrayList<Long>(Arrays.asList(GROUP_ID1, GROUP_ID2));
        List<DomainGroupModelView> results = mapper.execute(list);
        assertEquals(2, results.size());

        results = mapper.execute(list);
        assertEquals(2, results.size());
    }
}
