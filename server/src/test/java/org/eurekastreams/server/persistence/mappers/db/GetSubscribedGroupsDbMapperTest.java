/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetSubscribedGroupsDbMapper.
 */
public class GetSubscribedGroupsDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetSubscribedGroupsDbMapper sut;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new GetSubscribedGroupsDbMapper();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test for person with two followed groups.
     */
    @Test
    public void testExecuteForMultipleFollowedGroups()
    {
        final long fordId = 42L; // groups 1, 2, 3
        final String group1ShortName = "group1";
        final String group3ShortName = "group3";

        getEntityManager().clear();

        // don't trust dataset.xml for this one since it deals with booleans

        // unsubscribe from all groups
        getEntityManager().createQuery("UPDATE GroupFollower SET receiveNewActivityNotifications = :trueOrFalse")
                .setParameter("trueOrFalse", false).executeUpdate();

        getEntityManager().clear();

        // subscribe to group 1
        getEntityManager().createQuery(
                "UPDATE GroupFollower SET receiveNewActivityNotifications = :trueOrFalse "
                        + "WHERE followerId = :personId AND followingId = :groupId").setParameter("trueOrFalse", true)
                .setParameter("personId", fordId).setParameter("groupId", 1).executeUpdate();

        getEntityManager().clear();

        // subscribe to group 3
        getEntityManager().createQuery(
                "UPDATE GroupFollower SET receiveNewActivityNotifications = :trueOrFalse "
                        + "WHERE followerId = :personId AND followingId = :groupId").setParameter("trueOrFalse", true)
                .setParameter("personId", fordId).setParameter("groupId", 3).executeUpdate();

        getEntityManager().clear();

        ArrayList<String> groups = sut.execute(fordId);
        assertEquals(2, groups.size());

        assertTrue(groups.contains(group1ShortName));
        assertTrue(groups.contains(group3ShortName));
    }

    /**
     * Test for person with no followed groups.
     */
    @Test
    public void testExecuteWithNoFollowedGroups()
    {
        final long fordId = 42L; // groups 1, 2, 3

        getEntityManager().clear();

        // don't trust dataset.xml for this one since it deals with booleans

        // unsubscribe from all groups
        getEntityManager().createQuery("UPDATE GroupFollower SET receiveNewActivityNotifications = :trueOrFalse")
                .setParameter("trueOrFalse", false).executeUpdate();

        getEntityManager().clear();

        ArrayList<String> groups = sut.execute(fordId);
        assertEquals(0, groups.size());
    }
}
