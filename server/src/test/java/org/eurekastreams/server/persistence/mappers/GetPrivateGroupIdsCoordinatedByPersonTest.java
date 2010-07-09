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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test fixture for GetPrivateGroupIdsCoordinatedByPerson.
 */
public class GetPrivateGroupIdsCoordinatedByPersonTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetPrivateGroupIdsCoordinatedByPerson sut;

    /**
     * Test execute for person that's not a coordinator.
     */
    @Test
    public void testExecuteForPersonNotCoordinatingGroups()
    {
        final Long personId = 999L;
        List<Long> groupIds = sut.execute(personId);
        assertEquals(0, groupIds.size());
    }

    /**
     * Test execute for person that's a coordinator of a public group.
     */
    @Test
    public void testExecuteForPersonCoordinatingPublicGroup()
    {
        final Long personId = 42L;
        List<Long> groupIds = sut.execute(personId);
        assertEquals(0, groupIds.size());
    }

    /**
     * Test execute for person that's a coordinator of a private group.
     */
    @Test
    public void testExecuteForPersonCoordinatingPrivateGroup()
    {
        // make group 1 private
        getEntityManager().createQuery("UPDATE DomainGroup SET publicGroup=false WHERE id=1").executeUpdate();

        final Long personId = 42L;
        List<Long> groupIds = sut.execute(personId);
        assertEquals(1, groupIds.size());
        assertTrue(groupIds.contains(1L));
    }
}
