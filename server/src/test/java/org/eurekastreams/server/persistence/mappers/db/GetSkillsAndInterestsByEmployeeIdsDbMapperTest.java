/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
import java.util.List;
import java.util.Map;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetSkillsAndInterestsByEmployeeIdsDbMapper.
 */
public class GetSkillsAndInterestsByEmployeeIdsDbMapperTest extends MapperTest
{
    /**
     * System under test. Autowired mapper is config'ed to grab ids from Person table.
     */
    private GetSkillsAndInterestsByEmployeeIdsDbMapper sut;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new GetSkillsAndInterestsByEmployeeIdsDbMapper();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test for person ids.
     */
    @Test
    public void testExecute()
    {
        final long fordId = 42L; // skill_1, skill_2
        final long montyBurnsId = 99L; // skill_2, skill_3

        List<Long> peopleIds = new ArrayList<Long>();
        peopleIds.add(fordId);
        peopleIds.add(montyBurnsId);

        Map<Long, List<String>> data = sut.execute(peopleIds);
        assertEquals(2, data.keySet().size());

        // assert ford has "skill_1", "skill_2"
        assertEquals(2, data.get(fordId).size());
        assertEquals("skill_1", data.get(fordId).get(0));
        assertEquals("skill_2", data.get(fordId).get(1));

        // assert monty has "skill_2", "skill_3"
        assertEquals(2, data.get(montyBurnsId).size());
        assertEquals("skill_2", data.get(montyBurnsId).get(0));
        assertEquals("skill_3", data.get(montyBurnsId).get(1));
    }
}
