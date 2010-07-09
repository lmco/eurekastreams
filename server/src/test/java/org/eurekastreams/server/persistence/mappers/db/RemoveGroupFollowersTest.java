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

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eurekastreams.server.domain.GroupFollower;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for RemoveGroupFollowers.
 * 
 */
public class RemoveGroupFollowersTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private RemoveGroupFollowers sut;

    /**
     * Mr. Burns id.
     */
    private final Long burnsId = 99L;

    /**
     * Ford Prefect id.
     */
    private final Long fordId = 42L;

    /**
     * Smithers id.
     */
    private final Long smithersId = 98L;

    /**
     * Group id for group to be removed.
     */
    private Long groupId = 1L;

    /**
     * Test.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecute()
    {
        // VERIFY FORD INFO
        List<GroupFollower> gf = getEntityManager().createQuery(
                "FROM GroupFollower gf WHERE gf.pk.followerId=:followerId ORDER BY gf.groupStreamIndex").setParameter(
                "followerId", fordId).getResultList();

        assertEquals(3, gf.size());

        // verify that expected ids are in correct order.
        assertEquals(1, gf.get(0).getFollowingId());
        assertEquals(2, gf.get(1).getFollowingId());
        assertEquals(3, gf.get(2).getFollowingId());

        // verify index
        assertEquals(0, gf.get(0).getGroupStreamIndex());
        assertEquals(1, gf.get(1).getGroupStreamIndex());
        assertEquals(2, gf.get(2).getGroupStreamIndex());

        // make sure person groupsCount is in sync with GroupFollower entries.
        getEntityManager().createQuery("UPDATE VERSIONED Person SET groupsCount = followingGroup.size WHERE id = :id")
                .setParameter("id", fordId).executeUpdate();

        // verify groupCount for user.
        Integer fordGroupCount = (Integer) getEntityManager().createQuery(
                "SELECT p.groupsCount FROM Person p WHERE p.id = :id").setParameter("id", fordId).getResultList()
                .get(0);
        assertEquals(3, fordGroupCount.intValue());

        // VERIFY BURNS INFO
        List<GroupFollower> burnsGf = getEntityManager().createQuery(
                "FROM GroupFollower gf WHERE gf.pk.followerId=:followerId ORDER BY gf.groupStreamIndex").setParameter(
                "followerId", burnsId).getResultList();

        assertEquals(1, burnsGf.size());

        // verify that expected ids are in correct order.
        assertEquals(0, burnsGf.get(0).getGroupStreamIndex());

        // make sure person groupsCount is in sync with GroupFollower entries.
        getEntityManager().createQuery("UPDATE VERSIONED Person SET groupsCount = followingGroup.size WHERE id = :id")
                .setParameter("id", burnsId).executeUpdate();

        // verify groupCount for user.
        Integer burnsGroupCount = (Integer) getEntityManager().createQuery(
                "SELECT p.groupsCount FROM Person p WHERE p.id = :id").setParameter("id", burnsId).getResultList().get(
                0);
        assertEquals(1, burnsGroupCount.intValue());

        // **************** RUN SUT ****************
        List<Long> followerIds = sut.execute(groupId);

        getEntityManager().flush();
        getEntityManager().clear();

        // assert return values correct
        assertEquals(3, followerIds.size());
        assertTrue(followerIds.contains(burnsId));
        assertTrue(followerIds.contains(smithersId));
        assertTrue(followerIds.contains(fordId));

        // VERIFY FORD INFO
        gf = getEntityManager().createQuery(
                "FROM GroupFollower gf WHERE gf.pk.followerId=:followerId ORDER BY gf.groupStreamIndex").setParameter(
                "followerId", fordId).getResultList();

        assertEquals(2, gf.size());

        // verify that expected ids are in correct order.
        assertEquals(2, gf.get(0).getFollowingId());
        assertEquals(3, gf.get(1).getFollowingId());

        // verify index decrement.
        assertEquals(0, gf.get(0).getGroupStreamIndex());
        assertEquals(1, gf.get(1).getGroupStreamIndex());

        // verify groupCount for user.
        fordGroupCount = (Integer) getEntityManager()
                .createQuery("SELECT p.groupsCount FROM Person p WHERE p.id = :id").setParameter("id", fordId)
                .getResultList().get(0);
        assertEquals(2, fordGroupCount.intValue());

        // VERIFY BURNS INFO
        burnsGf = getEntityManager().createQuery(
                "FROM GroupFollower gf WHERE gf.pk.followerId=:followerId ORDER BY gf.groupStreamIndex").setParameter(
                "followerId", burnsId).getResultList();

        assertEquals(0, burnsGf.size());

        // verify groupCount for user.
        burnsGroupCount = (Integer) getEntityManager().createQuery(
                "SELECT p.groupsCount FROM Person p WHERE p.id = :id").setParameter("id", burnsId).getResultList().get(
                0);
        assertEquals(0, burnsGroupCount.intValue());

    }
}
