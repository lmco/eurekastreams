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

import org.eurekastreams.server.search.modelview.PersonModelView;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests getting person DTOs from a list of person ids.
 */
public class GetPeopleByOpenSocialIdsTest extends CachedMapperTest
{
    /**
     * The main id to test with.
     */
    private static final String PERSON_ID = "2d359911-0977-418a-9490-57e8252b1a98";

    /**
     * An additional id to test with.
     */
    private static final String PERSON_ID2 = "2d359911-0977-418a-9490-57e8252b1a99";

    /**
     * System under test.
     */
    @Autowired
    private GetPeopleByOpenSocialIds mapper;

    /**
     * test.
     */
    @Test
    public void testExecute()
    {
        List<String> list = new ArrayList<String>();
        list.add(PERSON_ID);
        List<PersonModelView> results = mapper.execute(list);
        assertEquals(1, results.size());

        // now that the cache should be populated, run the execute again
        results = mapper.execute(list);
        assertEquals(1, results.size());
        assertEquals("smithers", results.get(0).getAccountId());
    }

    /**
     * test.
     */
    @Test
    public void testExecuteWithMultipleIds()
    {
        List<String> list = new ArrayList<String>();
        list.add(PERSON_ID);
        list.add(PERSON_ID2);
        List<PersonModelView> results = mapper.execute(list);
        assertEquals(2, results.size());

        results = mapper.execute(list);
        assertEquals(2, results.size());
    }
}
