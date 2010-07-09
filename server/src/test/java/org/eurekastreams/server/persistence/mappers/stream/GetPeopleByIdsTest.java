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
public class GetPeopleByIdsTest extends CachedMapperTest
{
    /**
     * The main id to test with.
     */
    private static final long PERSON_ID = 98;

    /**
     * An additional id to test with.
     */
    private static final long PERSON_ID2 = 99;

    /**
     * System under test.
     */
    @Autowired
    private GetPeopleByIds mapper;

    /**
     * test.
     */
    @Test
    public void testExecute()
    {
        List<Long> list = new ArrayList<Long>();
        list.add(new Long(PERSON_ID));
        List<PersonModelView> results = mapper.execute(list);
        assertEquals(1, results.size());

        assertSmithers(results.get(0));

        // now that the cache should be populated, run the execute again
        results = mapper.execute(list);
        assertEquals(1, results.size());

    }

    /**
     * test.
     */
    @Test
    public void testExecuteWithMultipleIds()
    {
        List<Long> list = new ArrayList<Long>();
        list.add(new Long(PERSON_ID));
        list.add(new Long(PERSON_ID2));
        List<PersonModelView> results = mapper.execute(list);
        assertEquals(2, results.size());

        results = mapper.execute(list);
        assertEquals(2, results.size());
    }

    /**
     * test.
     */
    @Test
    public void testExecuteSingleId()
    {
        assertSmithers(mapper.execute(PERSON_ID));
    }

    /**
     * Assert the input PersonModelView is Smithers.
     *
     * @param person
     *            the person to test
     */
    private void assertSmithers(final PersonModelView person)
    {
        assertEquals("smithers", person.getAccountId());
        assertEquals("child2orgname", person.getParentOrganizationShortName());
        assertEquals(7, person.getParentOrganizationId());
        assertEquals(0, person.getCompositeStreamHiddenLineIndex());
        assertEquals(2, person.getCompositeStreamSearchHiddenLineIndex());
        assertEquals("smithers@gmail.com", person.getEmail());
        assertEquals("skljk klsdjlsdlsj lkj5", person.getDescription());
    }
}
