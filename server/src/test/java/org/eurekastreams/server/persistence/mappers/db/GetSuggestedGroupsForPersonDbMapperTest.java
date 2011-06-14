/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.server.domain.GroupFollower;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.SuggestedStreamsRequest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetSuggestedGroupsForPersonDbMapper.
 */
public class GetSuggestedGroupsForPersonDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetSuggestedGroupsForPersonDbMapper sut;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new GetSuggestedGroupsForPersonDbMapper();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        getEntityManager().createQuery("DELETE FROM GroupFollower").executeUpdate();
        getEntityManager().createQuery("DELETE FROM Follower").executeUpdate();

        // ---
        // person 42 is following 142, 4507, 98, 99
        getEntityManager().persist(new Follower(42, 142));
        getEntityManager().persist(new Follower(42, 4507));
        getEntityManager().persist(new Follower(42, 98));
        getEntityManager().persist(new Follower(42, 99));

        // ---
        // person 142, 4507, and 98 are following group 5
        getEntityManager().persist(new GroupFollower(142, 5));
        getEntityManager().persist(new GroupFollower(4507, 5));
        getEntityManager().persist(new GroupFollower(98, 5));

        // person 142, 4507 follow group 1
        getEntityManager().persist(new GroupFollower(142, 1));
        getEntityManager().persist(new GroupFollower(4507, 1));

        // person 98 follow group 3, 42 (current user - should be ignored)
        getEntityManager().persist(new GroupFollower(142, 3));

        // ---
        // 

        List<Long> suggestedGroups = sut.execute(new SuggestedStreamsRequest(42L, 4));
        Assert.assertEquals(3, suggestedGroups.size());
        Assert.assertEquals(new Long(5), suggestedGroups.get(0));
        Assert.assertEquals(new Long(1), suggestedGroups.get(1));
        Assert.assertEquals(new Long(3), suggestedGroups.get(2));

        suggestedGroups = sut.execute(new SuggestedStreamsRequest(42L, 2));
        Assert.assertEquals(2, suggestedGroups.size());
        Assert.assertEquals(new Long(5), suggestedGroups.get(0));
        Assert.assertEquals(new Long(1), suggestedGroups.get(1));

        // now follow group 1 so it's removed from the list
        getEntityManager().persist(new GroupFollower(42, 1));
        suggestedGroups = sut.execute(new SuggestedStreamsRequest(42L, 3));
        Assert.assertEquals(2, suggestedGroups.size());
        Assert.assertEquals(new Long(5), suggestedGroups.get(0));
        Assert.assertEquals(new Long(3), suggestedGroups.get(1));
    }
}
