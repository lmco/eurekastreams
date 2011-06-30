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

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.ChangeStreamActivitySubscriptionMapperRequest;
import org.junit.Test;

/**
 * Test fixture for ChangeStreamActivitySubscriptionDbMapper.
 */
public class ChangeStreamActivitySubscriptionDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private DomainMapper<ChangeStreamActivitySubscriptionMapperRequest, Boolean> sut;

    /**
     * Tests attempting to create for unsupported type.
     */
    @Test(expected = Exception.class)
    public void testConstructInvalidType()
    {
        new ChangeStreamActivitySubscriptionDbMapper(EntityType.RESOURCE);
    }

    // ---- Test helpers ----

    /**
     * Gets the subscription status from the database.
     * 
     * @param entityName
     *            ORM entity type.
     * @param userId
     *            User.
     * @param entityId
     *            Followed stream.
     * @return subscription status.
     */
    private boolean getSubscription(final String entityName, final long userId, final long entityId)
    {
        final String q = "SELECT receiveNewActivityNotifications FROM " + entityName
                + " WHERE followerId = :userId AND followingId = :entityId";
        return (Boolean) getEntityManager().createQuery(q).setParameter("userId", userId)
                .setParameter("entityId", entityId).getSingleResult();
    }

    /**
     * Gets count of following entries from the database.
     *
     * @param entityName
     *            ORM entity type.
     * @param userId
     *            User.
     * @param entityId
     *            Followed stream.
     * @return count.
     */
    private long getFollowingCount(final String entityName, final long userId, final long entityId)
    {
        String q = "SELECT COUNT(pk.followerId) FROM " + entityName
                + " WHERE followerId = :personId AND followingId = :entityId";
        return (Long) getEntityManager().createQuery(q).setParameter("personId", userId)
                .setParameter("entityId", entityId).getSingleResult();
    }

    // ---- Group tests ----

    /**
     * Test for unsubscribing from a group.
     */
    @Test
    public void testExecuteUnsubscribingFromGroup()
    {
        final long userId = 98L;
        final long groupId = 1L;

        assertTrue(getSubscription("GroupFollower", userId, groupId));
        getEntityManager().clear();

        sut = new ChangeStreamActivitySubscriptionDbMapper(EntityType.GROUP);
        ((BaseDomainMapper) sut).setEntityManager(getEntityManager());
        sut.execute(new ChangeStreamActivitySubscriptionMapperRequest(userId, groupId, false));

        getEntityManager().clear();
        assertFalse(getSubscription("GroupFollower", userId, groupId));
    }

    /**
     * Test subscribing to a group.
     */
    @Test
    public void testExecuteSubscribingToGroup()
    {
        final long userId = 99L;
        final long groupId = 1L;

        assertFalse(getSubscription("GroupFollower", userId, groupId));
        getEntityManager().clear();

        sut = new ChangeStreamActivitySubscriptionDbMapper(EntityType.GROUP);
        ((BaseDomainMapper) sut).setEntityManager(getEntityManager());
        sut.execute(new ChangeStreamActivitySubscriptionMapperRequest(userId, groupId, true));

        getEntityManager().clear();
        assertTrue(getSubscription("GroupFollower", userId, groupId));
    }

    /**
     * Test subscribing to a group not a member of.
     */
    @Test
    public void testExecuteSubscribingToGroupNotMemberOf()
    {
        final long userId = 4507L;
        final long groupId = 1L;

        assertEquals(0, getFollowingCount("GroupFollower", userId, groupId));
        getEntityManager().clear();

        sut = new ChangeStreamActivitySubscriptionDbMapper(EntityType.GROUP);
        ((BaseDomainMapper) sut).setEntityManager(getEntityManager());
        sut.execute(new ChangeStreamActivitySubscriptionMapperRequest(userId, groupId, true));

        getEntityManager().clear();
        assertEquals(0, getFollowingCount("GroupFollower", userId, groupId));
    }

    // ---- Person tests ----

    /**
     * Test for unsubscribing from a person.
     */
    @Test
    public void testExecuteUnsubscribingFromPerson()
    {
        final long userId = 142L;
        final long personId = 98L;

        assertTrue(getSubscription("Follower", userId, personId));
        getEntityManager().clear();

        sut = new ChangeStreamActivitySubscriptionDbMapper(EntityType.PERSON);
        ((BaseDomainMapper) sut).setEntityManager(getEntityManager());
        sut.execute(new ChangeStreamActivitySubscriptionMapperRequest(userId, personId, false));

        getEntityManager().clear();
        assertFalse(getSubscription("Follower", userId, personId));
    }

    /**
     * Test subscribing to a person.
     */
    @Test
    public void testExecuteSubscribingToPerson()
    {
        final long userId = 99L;
        final long personId = 98L;

        assertFalse(getSubscription("Follower", userId, personId));
        getEntityManager().clear();

        sut = new ChangeStreamActivitySubscriptionDbMapper(EntityType.PERSON);
        ((BaseDomainMapper) sut).setEntityManager(getEntityManager());
        sut.execute(new ChangeStreamActivitySubscriptionMapperRequest(userId, personId, true));

        getEntityManager().clear();
        assertTrue(getSubscription("Follower", userId, personId));
    }

    /**
     * Test subscribing to a person not following.
     */
    @Test
    public void testExecuteSubscribingToPersonNotFollowing()
    {
        final long userId = 4507L;
        final long personId = 42L;

        assertEquals(0, getFollowingCount("Follower", userId, personId));
        getEntityManager().clear();

        sut = new ChangeStreamActivitySubscriptionDbMapper(EntityType.PERSON);
        ((BaseDomainMapper) sut).setEntityManager(getEntityManager());
        sut.execute(new ChangeStreamActivitySubscriptionMapperRequest(userId, personId, true));

        getEntityManager().clear();
        assertEquals(0, getFollowingCount("Follower", userId, personId));
    }
}
