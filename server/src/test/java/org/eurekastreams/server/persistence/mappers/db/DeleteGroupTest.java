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

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.DeleteGroupResponse;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for DeleteGroup mapper.
 *
 */
public class DeleteGroupTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private DeleteGroup sut;

    /**
     * Group id.
     */
    private final Long groupId = 2L;

    /**
     * Group StreamScope id.
     */
    private final long groupStreamScopeId = 875L;

    /**
     * Test.
     */
    @Test
    public void executeTest()
    {
        // verify associated items are present as expected.
        assertEquals(1, getEntityManager().createQuery("FROM DomainGroup WHERE id = 2").getResultList().size());
        assertEquals(1, getEntityManager().createQuery("FROM StreamScope WHERE id = 875").getResultList().size());
        assertEquals(1, getEntityManager().createQuery("FROM StreamView WHERE id = 10").getResultList().size());

        // get a parent org group count to compare with after delete.
        int descendantGroupCount = (Integer) getEntityManager().createQuery(
                "SELECT descendantGroupCount FROM Organization WHERE id = 7").getSingleResult();

        DeleteGroupResponse response = sut.execute(groupId);

        getEntityManager().flush();
        getEntityManager().clear();

        // assert response values are correct.
        assertEquals(2, response.getGroupId().longValue());
        assertEquals("group2", response.getGroupShortName());
        assertEquals(groupStreamScopeId, response.getStreamScopeId().longValue());
        assertEquals(2, response.getParentOrganizationIds().size());
        assertTrue(response.getParentOrganizationIds().contains(5L));
        assertTrue(response.getParentOrganizationIds().contains(7L));

        // verify associated items are gone as expected.
        // TODO: This could be filled out with more related entities to ensure cascading doesn't change for DomainGroup.
        assertEquals(0, getEntityManager().createQuery("FROM DomainGroup WHERE id = 2").getResultList().size());
        assertEquals(0, getEntityManager().createQuery("FROM StreamScope WHERE id = 875").getResultList().size());
        assertEquals(0, getEntityManager().createQuery("FROM StreamView WHERE id = 10").getResultList().size());

        // verify organization stats were updated.
        assertEquals(descendantGroupCount - 1, ((Integer) getEntityManager().createQuery(
                "SELECT descendantGroupCount FROM Organization WHERE id = 7").getSingleResult()).intValue());

    }
}
