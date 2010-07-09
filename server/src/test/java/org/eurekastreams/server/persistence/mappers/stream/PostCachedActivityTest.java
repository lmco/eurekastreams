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
package org.eurekastreams.server.persistence.mappers.stream;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.cache.testhelpers.SimpleMemoryCache;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests adding an activity.
 */
public class PostCachedActivityTest extends MapperTest
{
    /**
     * The search id to test with.
     */
    private static final long ACTIVITY_ID = 101;

    /**
     * The actor id.
     */
    private static final long ACTOR_ID = 42;

    /**
     * The destination stream id.
     */
    private static final long STREAM_ID = 87433;

    /**
     * The destination stream id for a group.
     */
    private static final long GROUP_STREAM_ID = 874;

    /**
     * System under test.
     */
    @Autowired
    private PostCachedActivity postCachedActivity;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        ((SimpleMemoryCache) postCachedActivity.getCache()).clear();
    }

    /**
     * test.
     */
    @Test
    public void testExecute()
    {
        //Key for Custom Composite Stream
        String key1 = CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + "938";
        //Key for Following Composite Stream
        String key2 = CacheKeys.ACTIVITIES_BY_FOLLOWING + "99";

        Cache cache = postCachedActivity.getCache();

        assertTrue(cache.getList(key1) == null);
        assertTrue(cache.getList(key2) == null);

        // "warm" the cache
        cache.setList(key1, new ArrayList<Long>());
        cache.setList(key2, new ArrayList<Long>());

        assertTrue(cache.getList(key1) != null);
        assertTrue(cache.getList(key2) != null);

        ActivityDTO activity = new ActivityDTO();
        activity.setId(ACTIVITY_ID);
        StreamEntityDTO actor = new StreamEntityDTO();
        actor.setId(ACTOR_ID);
        actor.setType(EntityType.PERSON);
        actor.setUniqueIdentifier("fordp");

        StreamEntityDTO destinationStream = new StreamEntityDTO();
        destinationStream.setId(STREAM_ID);
        destinationStream.setType(EntityType.PERSON);
        destinationStream.setUniqueIdentifier("smithers");

        activity.setActor(actor);
        activity.setDestinationStream(destinationStream);

        postCachedActivity.execute(activity);

        assertTrue(cache.getList(key1) != null);
        assertTrue(cache.getList(key2) != null);

        assertTrue(((List<Long>) cache.getList(key1)).contains(ACTIVITY_ID));
        assertTrue(((List<Long>) cache.getList(key2)).contains(ACTIVITY_ID));
    }

    /**
     * test.
     */
    @Test
    public void testExecuteGroupPost()
    {
        //Key for Custom Composite Stream including group
        String key1 = CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + "382";

        Cache cache = postCachedActivity.getCache();

        assertTrue(cache.getList(key1) == null);

        // "warm" the cache
        cache.setList(key1, new ArrayList<Long>());

        assertTrue(cache.getList(key1) != null);

        ActivityDTO activity = new ActivityDTO();
        activity.setId(ACTIVITY_ID);
        StreamEntityDTO actor = new StreamEntityDTO();
        actor.setId(ACTOR_ID);
        actor.setType(EntityType.PERSON);
        actor.setUniqueIdentifier("fordp");

        StreamEntityDTO destinationStream = new StreamEntityDTO();
        destinationStream.setId(GROUP_STREAM_ID);
        destinationStream.setType(EntityType.GROUP);
        destinationStream.setUniqueIdentifier("group1");

        activity.setActor(actor);
        activity.setDestinationStream(destinationStream);

        postCachedActivity.execute(activity);

        assertTrue(cache.getList(key1) != null);

        assertTrue(((List<Long>) cache.getList(key1)).contains(ACTIVITY_ID));
    }
}
