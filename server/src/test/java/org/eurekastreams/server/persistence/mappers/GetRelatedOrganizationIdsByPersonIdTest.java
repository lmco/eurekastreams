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
package org.eurekastreams.server.persistence.mappers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetRelatedOrganizationIdsForPerson.
 */
public class GetRelatedOrganizationIdsByPersonIdTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetRelatedOrganizationIdsByPersonId sut;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        final long saganId = 4507L;

        sut = new GetRelatedOrganizationIdsByPersonId();
        sut.setEntityManager(getEntityManager());

        getEntityManager().createQuery("DELETE FROM PersonRelatedOrganization WHERE pk.personId = :personId")
                .setParameter("personId", saganId).executeUpdate();
    }

    /**
     * Test execute for single person.
     */
    @Test
    public void testExecuteSinglePerson()
    {
        final long fordId = 42L;

        List<Long> relatedOrgIds = sut.execute(fordId);

        assertEquals(2, relatedOrgIds.size());

        assertTrue((relatedOrgIds.get(0) == 5 && relatedOrgIds.get(1) == 6)
                || (relatedOrgIds.get(1) == 5 && relatedOrgIds.get(0) == 6));
    }

    /**
     * Test execute for a single person with no results.
     */
    @Test
    public void testExecuteSinglePersonNoResults()
    {
        final Long saganId = 4507L;
        assertEquals(0, sut.execute(saganId).size());
    }

    /**
     * Test execute for multiple people.
     */
    @Test
    public void testExecuteMultiplePerson()
    {
        final long fordId = 42L; // 2 related orgs
        final long ford2Id = 142L; // 2 related orgs
        final long smithersId = 98L; // 3 related orgs
        final long saganId = 4507L; // 0 related orgs

        ArrayList<Long> peopleIds = new ArrayList<Long>();
        peopleIds.add(fordId);
        peopleIds.add(ford2Id);
        peopleIds.add(smithersId);
        peopleIds.add(saganId);

        Map<Long, List<Long>> relatedOrgs = sut.execute(peopleIds);

        assertEquals(4, relatedOrgs.size());

        List<Long> fordList = relatedOrgs.get(fordId);
        List<Long> ford2List = relatedOrgs.get(ford2Id);
        List<Long> smithersList = relatedOrgs.get(smithersId);
        List<Long> saganList = relatedOrgs.get(saganId);

        assertEquals(2, fordList.size());
        assertTrue(fordList.contains(5L));
        assertTrue(fordList.contains(6L));

        assertEquals(2, ford2List.size());
        assertTrue(ford2List.contains(5L));
        assertTrue(ford2List.contains(6L));

        assertEquals(3, smithersList.size());
        assertTrue(smithersList.contains(5L));
        assertTrue(smithersList.contains(6L));
        assertTrue(smithersList.contains(7L));

        assertEquals(0, saganList.size());
    }
}
