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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test fixture for GetPrivateGroupIdsUnderOrganizations.
 */
public class GetPrivateGroupIdsUnderOrganizationsTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetPrivateGroupIdsUnderOrganizations sut;

    /**
     * Test execute() with no org ids.
     */
    @Test
    public void testExecuteWithNoOrgIds()
    {
        assertEquals(new HashSet<Long>(), sut.execute(new ArrayList<Long>()));
    }

    /**
     * Test execute with a single org id with all public groups.
     */
    @Test
    public void testExecuteWithOrgsHavingAllPublicGroups()
    {
        final Long rootOrgId = 5L;
        final Set<Long> groupIds = sut.execute(Collections.singleton(rootOrgId));
        assertEquals(0, groupIds.size());
    }

    /**
     * Test execute with a single org id and one public, one private group.
     */
    @Test
    public void testExecuteSingleOrgAndMixedPublicPrivateGroups()
    {
        getEntityManager().createQuery("UPDATE DomainGroup set publicGroup=false WHERE id=5").executeUpdate();

        final Long rootOrgId = 5L;
        final Long privateGroupId = 5L;
        final Set<Long> groupIds = sut.execute(Collections.singleton(rootOrgId));
        assertEquals(Collections.singleton(privateGroupId), groupIds);
    }

    /**
     * Test execute with all org ids and a few private groups.
     */
    @Test
    public void testExecuteAllOrgsAndMixedPublicPrivateGroups()
    {
        // set group ids 3, 5, 7 private
        getEntityManager().createQuery("UPDATE DomainGroup set publicGroup=false WHERE id IN(3,5,7)").executeUpdate();

        // execute sut
        final Set<Long> groupIds = sut.execute(new HashSet<Long>());

        // assert we got back 3,5,7
        assertEquals(3, groupIds.size());
        assertTrue(groupIds.contains(3L));
        assertTrue(groupIds.contains(5L));
        assertTrue(groupIds.contains(7L));
    }

    /**
     * Test execute with a couple org ids and all private groups.
     */
    @Test
    public void testExecuteManyOrgsWhenAllDomainGroupsPrivate()
    {
        // update all groups to be private
        getEntityManager().createQuery("UPDATE DomainGroup set publicGroup=false").executeUpdate();

        // execute sut
        final Set<Long> groupIds = sut.execute(new HashSet<Long>());

        assertEquals(8, groupIds.size());
        assertTrue(groupIds.contains(1L));
        assertTrue(groupIds.contains(2L));
        assertTrue(groupIds.contains(3L));
        assertTrue(groupIds.contains(4L));
        assertTrue(groupIds.contains(5L));
        assertTrue(groupIds.contains(6L));
        assertTrue(groupIds.contains(7L));
        assertTrue(groupIds.contains(8L));
    }
}
