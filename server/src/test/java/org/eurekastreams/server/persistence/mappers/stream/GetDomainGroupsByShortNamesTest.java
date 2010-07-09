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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests getting DTOs from a list of pointer ids.
 */
public class GetDomainGroupsByShortNamesTest extends CachedMapperTest
{
    /**
     * The main id to test with.
     */
    private static final String GROUP_SHORT_NAME = "group1";

    /**
     * An additional id to test with.
     */
    private static final String GROUP_SHORT_NAME2 = "group2";

    /**
     * System under test.
     */
    @Autowired
    private GetDomainGroupsByShortNames mapper;

    /**
     * test.
     */
    @Test
    public void testExecute()
    {
        List<String> list = new ArrayList<String>();
        list.add(GROUP_SHORT_NAME);
        List<DomainGroupModelView> results = mapper.execute(list);
        assertEquals(1, results.size());

        // now that the cache should be populated, run the execute again
        results = mapper.execute(list);
        assertEquals(1, results.size());
        assertEquals(1, results.get(0).getEntityId());
    }

    /**
     * test.
     */
    @Test
    public void testExecuteWithMultipleIds()
    {
        List<String> list = new ArrayList<String>();
        list.add(GROUP_SHORT_NAME);
        list.add(GROUP_SHORT_NAME2);
        List<DomainGroupModelView> results = mapper.execute(list);
        assertEquals(2, results.size());

        results = mapper.execute(list);
        assertEquals(2, results.size());
    }
}
