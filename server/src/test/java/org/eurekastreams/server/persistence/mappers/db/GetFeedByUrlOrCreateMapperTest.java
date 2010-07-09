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

import org.eurekastreams.server.domain.stream.plugins.Feed;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.requests.GetFeedByUrlRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * GetFeedByUrlOrCreate mapper.
 * 
 */
public class GetFeedByUrlOrCreateMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetFeedByUrlOrCreateMapper sut;
    
    /**
     * Mapper to make sure we persisted our new feeds.
     */
    @Autowired
    private FindByIdMapper<Feed> findByIdMapper;

    /**
     * Test execute on existing feed.
     */
    @Test
    public void testExecuteWithExisting()
    {
        GetFeedByUrlRequest request = new GetFeedByUrlRequest(1L, "http://www.google2.com");

        Feed feed = sut.execute(request);
        assertEquals("http://www.google2.com", feed.getUrl());
        assertEquals(1L, feed.getPlugin().getId());
        assertEquals(2L, feed.getId());
    }

    /**
     * Test execute on feed with a url thats in the system but tied to another plugin.
     */
    @Test
    public void testExecuteWithOutExisting()
    {
        GetFeedByUrlRequest request = new GetFeedByUrlRequest(2L, "http://www.google2.com");

        Feed feed = sut.execute(request);
        assertEquals("http://www.google2.com", feed.getUrl());
        assertEquals(2L, feed.getPlugin().getId());
        
        Feed persisted = findByIdMapper.execute(new FindByIdRequest("Feed", feed.getId()));
        assertEquals(persisted.getId(), feed.getId());
    }

    /**
     * Test with a url thats not in the system at all.
     */
    @Test
    public void testExecuteWithOutExisting2()
    {
        GetFeedByUrlRequest request = new GetFeedByUrlRequest(2L, "http://www.google8.com");

        Feed feed = sut.execute(request);
        assertEquals("http://www.google8.com", feed.getUrl());
        assertEquals(2L, feed.getPlugin().getId());
        
        Feed persisted = findByIdMapper.execute(new FindByIdRequest("Feed", feed.getId()));
        assertEquals(persisted.getId(), feed.getId());
    }
}
