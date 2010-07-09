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
package org.eurekastreams.server.persistence.mappers.cache;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test suite for the {@link PostActivityUpdateStreamsByActorMapper} class.
 *
 */
public class PostActivityUpdateStreamsByActorMapperTest extends CachedMapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private PostActivityUpdateStreamsByActorMapper sut;

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
     * Test the execution of the cache mapper.
     */
    @Test
    public void testExecute()
    {
        //Everyone stream view id.
        String key1 = CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + "5002";
        //Destination stream view id.
        String key2 = CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + "4";

        Cache cache = sut.getCache();

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

        sut.execute(activity);

        assertTrue(cache.get(key1) != null);
        assertTrue(cache.get(key2) != null);

        assertTrue(((List<Long>) cache.getList(key1)).contains(ACTIVITY_ID));
        assertTrue(((List<Long>) cache.getList(key2)).contains(ACTIVITY_ID));
    }
}
