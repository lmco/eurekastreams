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
package org.eurekastreams.server.persistence.mappers;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.plugins.FeedSubscriber;
import org.eurekastreams.server.persistence.mappers.db.GetFeedSubscriptionsByEntity;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.requests.GetFeedSubscriberRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Delete mapper test.
 *
 */
public class DeleteByIdMapperTest extends MapperTest
{
    /**
     * Allows me to check if it was actually deleted.
     */
    @Autowired
    private GetFeedSubscriptionsByEntity getFeeds;
   
    /**
     * System under test.
     */
    @Autowired
    private DeleteByIdMapper sut;

    /**
     * Test execute.
     */
    @Test
    public void execute()
    {
    	// Make sure user 42 has his 4 feeds.
    	final long entityId = 42L;
        List<FeedSubscriber> feedSubs = getFeeds.execute(
        		new GetFeedSubscriberRequest(0L, entityId, EntityType.PERSON, 0));
        assertEquals(4, feedSubs.size());
        
        // Delete feed 1.
        sut.execute(new FindByIdRequest("FeedSubscriber", 1L));
        
        List<FeedSubscriber> feedSubs2 = getFeeds.execute(
        		new GetFeedSubscriberRequest(0L, entityId, EntityType.PERSON, 0));
        // Make sure he has 3 left.
        assertEquals(3, feedSubs2.size());
        // Make sure feed 1 isn't in there.
        assertEquals(2L, feedSubs2.get(0).getId());
        assertEquals(3L, feedSubs2.get(1).getId());
        assertEquals(4L, feedSubs2.get(2).getId());
        
    }
}
