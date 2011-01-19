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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * Test fixture for GetRecursiveParentOrgIds.
 */
@TransactionConfiguration(defaultRollback = false)
public class GetRecursiveOrgCoordinatorsTest extends CachedMapperTest
{
    /**
     * System under test.
     */
    @Autowired
    @Qualifier("getRecursiveOrgCoordinators")
    private GetRecursiveOrgCoordinators sut;

    /**
     * System under test.
     */
    @Autowired
    @Qualifier("getRecursiveOrgCoordinatorsDownStream")
    private GetRecursiveOrgCoordinators sutDownStream;

    /**
     * Test That the root has two coordinators.
     */
    @Test
    public void testExecuteRoot()
    {
        Set<Long> recOrgCoordinatorIds = sut.execute(5L);
        assertEquals(2, recOrgCoordinatorIds.size());
    }

    /**
     * Test That the root has two coordinators.
     */
    @Test
    public void testExecuteRootShortName()
    {
        Set<Long> recOrgCoordinatorIds = sut.execute("tstorgname");
        assertEquals(2, recOrgCoordinatorIds.size());
    }

    /**
     * Test a child org has extra coordinators plus the extra one from root.
     */
    @Test
    public void testExecuteUpTheChain()
    {
        Set<Long> recOrgCoordinatorIds = sut.execute(6L);
        assertEquals(3, recOrgCoordinatorIds.size());
    }

    /**
     * Test a child org has extra coordinators plus the extra one from root.
     */
    @Test
    public void testExecuteDownTheChain()
    {
        Set<Long> recOrgCoordinatorIds = sutDownStream.execute(5L);
        assertEquals(3, recOrgCoordinatorIds.size());
    }

    /**
     * Test isOrgCoordinatorRecursively.
     */
    @Test
    public void testIsOrgCoordinatorRecursively()
    {
        // coordinators for org#6: 99, 142, and for #5: 42
        final long org5Id = 5L; // root org
        final long org6Id = 6L; // sub org of 6

        final long fordId = 42L; // coordinator for 5
        final long burnsId = 99L; // coordinator for 5, 6
        final long ford2Id = 142L; // coordinator for 6
        final long smithersId = 98L; // not a coordinator

        // check persissions to org 6, which has 5 up the tree:
        assertTrue(sut.isOrgCoordinatorRecursively(fordId, org6Id));
        assertTrue(sut.isOrgCoordinatorRecursively(burnsId, org6Id));
        assertTrue(sut.isOrgCoordinatorRecursively(ford2Id, org6Id));
        assertFalse(sut.isOrgCoordinatorRecursively(smithersId, org6Id));

        // check persissions to org 5
        assertTrue(sut.isOrgCoordinatorRecursively(fordId, org5Id));
        assertTrue(sut.isOrgCoordinatorRecursively(burnsId, org5Id));
        assertFalse(sut.isOrgCoordinatorRecursively(ford2Id, org5Id));
        assertFalse(sut.isOrgCoordinatorRecursively(smithersId, org5Id));

        // check with shortname
        assertTrue(sut.isOrgCoordinatorRecursively(fordId, "tstorgname"));
    }

    /**
     * Test hasCoordinatorAccessRecursively.
     */
    @Test
    public void testHasCoordinatorAccessRecursively()
    {
        // coordinators for org#6: 99, 142, and for #5: 42
        final long org5Id = 5L; // root org
        final long org6Id = 6L; // sub org of 6

        final long fordId = 42L; // coordinator for 5
        final long burnsId = 99L; // coordinator for 5, 6
        final long ford2Id = 142L; // coordinator for 6
        final long smithersId = 98L; // not a coordinator

        // check persissions to org 6, which has 5 up the tree:
        assertTrue(sut.hasCoordinatorAccessRecursively(fordId, org6Id));
        assertTrue(sut.hasCoordinatorAccessRecursively(burnsId, org6Id));
        assertTrue(sut.hasCoordinatorAccessRecursively(ford2Id, org6Id));
        assertFalse(sut.hasCoordinatorAccessRecursively(smithersId, org6Id));

        // check persissions to org 5
        assertTrue(sut.hasCoordinatorAccessRecursively(fordId, org5Id));
        assertTrue(sut.hasCoordinatorAccessRecursively(burnsId, org5Id));
        assertFalse(sut.hasCoordinatorAccessRecursively(ford2Id, org5Id));
        assertFalse(sut.hasCoordinatorAccessRecursively(smithersId, org5Id));
    }

}
