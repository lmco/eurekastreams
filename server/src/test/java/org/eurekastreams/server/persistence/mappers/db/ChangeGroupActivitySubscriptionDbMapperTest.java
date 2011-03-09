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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.domain.GroupFollower;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.ChangeGroupActivitySubscriptionMapperRequest;
import org.junit.Test;

/**
 * Test fixture for ChangeGroupActivitySubscriptionDbMapper.
 */
public class ChangeGroupActivitySubscriptionDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private ChangeGroupActivitySubscriptionDbMapper sut;

    /**
     * Test for unsubscribing from a group.
     */
    @Test
    public void testUnsubscribingFromGroup()
    {
        final long personId = 98L;
        final long groupId = 1L;
        final boolean shouldReceiveNotifications = false;
        GroupFollower follower;

        // for some reason, DBUnit isn't populating the booleans properly - set them here
        getEntityManager().createQuery("UPDATE GroupFollower SET receiveNewActivityNotifications = :value")
                .setParameter("value", true).executeUpdate();

        follower = (GroupFollower) getEntityManager().createQuery(
                "From GroupFollower WHERE followerId = :personId AND followingId = :groupId").setParameter("personId",
                personId).setParameter("groupId", groupId).getResultList().get(0);
        assertTrue(follower.getReceiveNewActivityNotifications());

        getEntityManager().clear();

        sut = new ChangeGroupActivitySubscriptionDbMapper();
        sut.setEntityManager(getEntityManager());
        sut.execute(new ChangeGroupActivitySubscriptionMapperRequest(personId, groupId, shouldReceiveNotifications));

        follower = (GroupFollower) getEntityManager().createQuery(
                "From GroupFollower WHERE followerId = :personId AND followingId = :groupId").setParameter("personId",
                personId).setParameter("groupId", groupId).getResultList().get(0);
        assertFalse(follower.getReceiveNewActivityNotifications());
    }

    /**
     * Test subscribing to a group.
     */
    @Test
    public void testSubscribingToGroup()
    {
        final long personId = 99L;
        final long groupId = 1L;
        final boolean shouldReceiveNotifications = true;
        GroupFollower follower;

        getEntityManager().clear();

        // for some reason, DBUnit isn't populating the booleans properly - set them here
        getEntityManager().createQuery("UPDATE GroupFollower SET receiveNewActivityNotifications = :value")
                .setParameter("value", false).executeUpdate();

        getEntityManager().clear();

        follower = (GroupFollower) getEntityManager().createQuery(
                "From GroupFollower WHERE followerId = :personId AND followingId = :groupId").setParameter("personId",
                personId).setParameter("groupId", groupId).getResultList().get(0);
        assertFalse(follower.getReceiveNewActivityNotifications());

        getEntityManager().clear();

        sut = new ChangeGroupActivitySubscriptionDbMapper();
        sut.setEntityManager(getEntityManager());
        sut.execute(new ChangeGroupActivitySubscriptionMapperRequest(personId, groupId, shouldReceiveNotifications));

        follower = (GroupFollower) getEntityManager().createQuery(
                "From GroupFollower WHERE followerId = :personId AND followingId = :groupId").setParameter("personId",
                personId).setParameter("groupId", groupId).getResultList().get(0);
        assertTrue(follower.getReceiveNewActivityNotifications());
    }

    /**
     * Test subscribing to a group not a member of.
     */
    @Test
    public void testSubscribingToGroupNotMemberOf()
    {
        final long personId = 4507L;
        final long groupId = 1L;
        final boolean shouldReceiveNotifications = true;
        int count;

        getEntityManager().clear();

        count = getEntityManager().createQuery(
                "From GroupFollower WHERE followerId = :personId AND followingId = :groupId").setParameter("personId",
                personId).setParameter("groupId", groupId).getResultList().size();
        assertEquals(0, count);

        getEntityManager().clear();

        sut = new ChangeGroupActivitySubscriptionDbMapper();
        sut.setEntityManager(getEntityManager());
        sut.execute(new ChangeGroupActivitySubscriptionMapperRequest(personId, groupId, shouldReceiveNotifications));

        count = getEntityManager().createQuery(
                "From GroupFollower WHERE followerId = :personId AND followingId = :groupId").setParameter("personId",
                personId).setParameter("groupId", groupId).getResultList().size();
        assertEquals(0, count);
    }
}
