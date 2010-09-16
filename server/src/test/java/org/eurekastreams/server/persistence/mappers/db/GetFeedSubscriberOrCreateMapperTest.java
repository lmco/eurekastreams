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

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.plugins.FeedSubscriber;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.requests.GetFeedSubscriberRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests the get feed subscriber or create mapper.
 *
 */
public class GetFeedSubscriberOrCreateMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetFeedSubscriberOrCreateMapper sut;

    /**
     * Mapper to make sure we persisted our new feeds.
     */
    @Autowired
    private FindByIdMapper<FeedSubscriber> findByIdMapper;


    /**
     * Test execute on existing feedsubscriber.
     */
    @Test
    public void testExecuteWithExisting()
    {
    	final long id = 42L;

        GetFeedSubscriberRequest request = new GetFeedSubscriberRequest(2L, id, EntityType.PERSON, 0);

        FeedSubscriber feedSub = sut.execute(request);
        // Just make sure it found the right one.
        assertEquals(2L, feedSub.getId());
    }

    /**
     * Test execute on non existing forcing a creation.
     */
    @Test
    public void testExecuteWithOutExisting()
    {
    	final long id = 42L;
    	final long id2 = 142L;

    	GetFeedSubscriberRequest request = new GetFeedSubscriberRequest(4L, id, EntityType.PERSON, id2);

        FeedSubscriber feed = sut.execute(request);
        // Make sure the created guy has all the right attributes.
        assertEquals(4L, feed.getFeed().getId());
        assertEquals(id, (long) feed.getEntityId());
        assertEquals(EntityType.PERSON, feed.getEntityType());
        assertEquals(id2, (long) feed.getRequestor().getId());
        // And make sure he got persisted to the DB.
        FeedSubscriber persisted = findByIdMapper.execute(new FindByIdRequest("FeedSubscriber", feed.getId()));
        assertEquals(persisted.getId(), feed.getId());
    }

}
