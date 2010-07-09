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

import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.plugins.FeedSubscriber;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.GetFeedSubscriberRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests the get feed subs by entity.
 *
 */
public class GetFeedSubscriptionsByEntityTest  extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetFeedSubscriptionsByEntity sut;
   

    /**
     * Test execute on existing feed.
     */
    @Test
    public void execute()
    {
    	final long entityId = 42L;
        List<FeedSubscriber> feedSubs = sut.execute(new GetFeedSubscriberRequest(0L, entityId, EntityType.PERSON));
        assertEquals(4, feedSubs.size());
        
        assertEquals(1L, feedSubs.get(0).getId());
        assertEquals(2L, feedSubs.get(1).getId());
        assertEquals(3L, feedSubs.get(2).getId());
        assertEquals(4L, feedSubs.get(3).getId());
    }
    
    /**
     * Test execute on existing feed.
     */
    @Test
    public void executeWithNoResults()
    {
    	final long entityId = 41L;
        List<FeedSubscriber> feedSubs = sut.execute(new GetFeedSubscriberRequest(0L, entityId, EntityType.PERSON));
        assertEquals(0, feedSubs.size());
        
    }
}
