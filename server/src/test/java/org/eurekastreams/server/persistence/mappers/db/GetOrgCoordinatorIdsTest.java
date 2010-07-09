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
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test fixture for GetOrgCoordinatorIds.
 */
public class GetOrgCoordinatorIdsTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetOrgCoordinatorIds sut;

    /**
     * Test execute on 5 alone.
     */
    @Test
    public void testExecuteOrg5()
    {
        final Long personId1 = 42L;
        final Long personId2 = 99L;

        Set<Long> coordinatorIds = sut.execute(5L);
        assertEquals(2, coordinatorIds.size());
        assertTrue(coordinatorIds.contains(personId1));
        assertTrue(coordinatorIds.contains(personId2));
    }

    /**
     * Test execute on 6 alone.
     */
    @Test
    public void testExecuteOrg6()
    {
        final Long personId1 = 142L;
        final Long personId2 = 99L;

        Set<Long> coordinatorIds = sut.execute(6L);
        assertEquals(2, coordinatorIds.size());
        assertTrue(coordinatorIds.contains(personId1));
        assertTrue(coordinatorIds.contains(personId2));
    }
}
