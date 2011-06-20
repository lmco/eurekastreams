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
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.SuggestedStreamsRequest;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetSuggestedPeopleForPersonDbMapper.
 */
public class GetSuggestedPeopleForPersonDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetSuggestedPeopleForPersonDbMapper sut;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new GetSuggestedPeopleForPersonDbMapper();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        getEntityManager().createQuery("DELETE FROM Follower").executeUpdate();

        // person 42 is following 98, 4507
        getEntityManager().persist(new Follower(42L, 98));
        getEntityManager().persist(new Follower(42L, 4507));

        // -- ask for more than exist
        // 98 and 4507 are following 142
        getEntityManager().persist(new Follower(98, 142));
        getEntityManager().persist(new Follower(4507, 142));

        // 98 is following 99
        getEntityManager().persist(new Follower(98, 99));

        List<PersonModelView> suggestions = sut.execute(new SuggestedStreamsRequest(42, 5));
        Assert.assertEquals(2, suggestions.size());
        Assert.assertEquals(142, suggestions.get(0).getId());
        Assert.assertEquals("fordp2", suggestions.get(0).getAccountId());
        Assert.assertEquals("Volgon-Vwatter Prefect", suggestions.get(0).getDisplayName());
        Assert.assertEquals(2, suggestions.get(0).getFollowersCount());

        Assert.assertEquals(99L, suggestions.get(1).getId());
        Assert.assertEquals("mrburns", suggestions.get(1).getAccountId());
        Assert.assertEquals("Mr.Burns Burns", suggestions.get(1).getDisplayName());
        Assert.assertEquals(1, suggestions.get(1).getFollowersCount());

        // -- now only ask for 1
        suggestions = sut.execute(new SuggestedStreamsRequest(42, 1));
        Assert.assertEquals(1, suggestions.size());
        Assert.assertEquals(142L, suggestions.get(0).getId());
        Assert.assertEquals("fordp2", suggestions.get(0).getAccountId());
        Assert.assertEquals("Volgon-Vwatter Prefect", suggestions.get(0).getDisplayName());
        Assert.assertEquals(2, suggestions.get(0).getFollowersCount());

        // -- now follow 99, which will no longer suggest it
        getEntityManager().persist(new Follower(42, 99));
        suggestions = sut.execute(new SuggestedStreamsRequest(42, 5));
        Assert.assertEquals(1, suggestions.size());
        Assert.assertEquals(142L, suggestions.get(0).getId());
        Assert.assertEquals("fordp2", suggestions.get(0).getAccountId());
        Assert.assertEquals("Volgon-Vwatter Prefect", suggestions.get(0).getDisplayName());
        Assert.assertEquals(2, suggestions.get(0).getFollowersCount());
    }
}
