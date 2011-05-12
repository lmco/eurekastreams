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

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;

//import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * Test fixture for GetRecursiveParentOrgIds.
 */
@TransactionConfiguration(defaultRollback = false)
public class GetAllPersonIdsWhoHaveGroupCoordinatorAccessTest extends CachedMapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetAllPersonIdsWhoHaveGroupCoordinatorAccess sut;

    /**
     * Test That the root has two coordinators.
     */
    @Test
    public void testExecuteAGroup()
    {
        Set<Long> recOrgCoordinatorIds = sut.execute(1L);
        assertEquals(3, recOrgCoordinatorIds.size());

        // system admins
        assertTrue(recOrgCoordinatorIds.contains(Long.parseLong("42")));
        assertTrue(recOrgCoordinatorIds.contains(Long.parseLong("142")));

        // one of the coords only in the group
        assertTrue(recOrgCoordinatorIds.contains(Long.parseLong("98")));

    }

    /**
     * Test That if a group does not exist that null is returned.
     */
    @Test
    public void testExecuteAGroup2()
    {
        Set<Long> recOrgCoordinatorIds = sut.execute(9L);
        assertTrue(recOrgCoordinatorIds.isEmpty());
    }

    /**
     * Test hasOrgCoordinatorAccessRecursively() for org coordinators up the tree.
     */
    @Test
    public void testHasOrgCoordinatorAccessRecursivelyForSystemAdmins()
    {
        final long fordId = 42L; // system admin
        final long ford2Id = 142L; // system admin
        final long smithersId = 98L; // not an admin

        // check persissions to org 6, which has 5 up the tree:
        assertTrue(sut.hasGroupCoordinatorAccessRecursively(fordId, 4L));
        assertTrue(sut.hasGroupCoordinatorAccessRecursively(ford2Id, 4L));
        assertFalse(sut.hasGroupCoordinatorAccessRecursively(smithersId, 4L));
    }

    /**
     * Test hasOrgCoordinatorAccessRecursively() for group coordinators.
     */
    @Test
    public void testHasOrgCoordinatorAccessRecursivelyForGroupCoordinators()
    {
        final long groupId = 1L; // a group under org 7

        final long fordId = 42L; // coordinator for group 1, system admin
        final long smithersId = 98L; // coordinator for group 1
        final long burnsId = 99L; // not a coordinator for group 1
        // org, two levels up
        final long ford2Id = 142L; // not a group coordinator, but system admin
        final long saganId = 4507L; // not a group or org coordinator

        assertTrue(sut.hasGroupCoordinatorAccessRecursively(fordId, groupId));
        assertTrue(sut.hasGroupCoordinatorAccessRecursively(smithersId, groupId));
        assertFalse(sut.hasGroupCoordinatorAccessRecursively(burnsId, groupId));
        assertTrue(sut.hasGroupCoordinatorAccessRecursively(ford2Id, groupId));
        assertFalse(sut.hasGroupCoordinatorAccessRecursively(saganId, groupId));
    }
}
