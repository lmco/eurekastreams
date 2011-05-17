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
package org.eurekastreams.server.persistence.mappers.cache;

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.stream.CachedMapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests PostActivityUpdateStreamsByActorMapper.
 */
public class PostActivityUpdateStreamsByActorMapperTest extends CachedMapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private PostActivityUpdateStreamsByActorMapper sut;

    /**
     * Tests execute method with a group scope.
     */
    @Test
    public void testExecuteWithGroup()
    {
        final long activityId = 98765L;
        final long scopeId = 878;

        ActivityDTO activity = new ActivityDTO();
        StreamEntityDTO destinationStream = new StreamEntityDTO();
        destinationStream.setType(EntityType.GROUP);
        destinationStream.setUniqueIdentifier("group5");
        activity.setDestinationStream(destinationStream);
        activity.setId(activityId);

        final String cacheKey = CacheKeys.ENTITY_STREAM_BY_SCOPE_ID + scopeId;

        assertEquals(null, getCache().get(cacheKey));

        sut.execute(activity);
        assertEquals(1, getCache().getList(cacheKey).size());
        assertEquals(activityId, (long) getCache().getList(cacheKey).get(0));
    }

    /**
     * Tests execute method with a person scope.
     */
    @Test
    public void testExecuteWithPerson()
    {
        final long activityId = 98766L;
        final long scopeId = 4;

        ActivityDTO activity = new ActivityDTO();
        StreamEntityDTO destinationStream = new StreamEntityDTO();
        destinationStream.setType(EntityType.PERSON);
        destinationStream.setUniqueIdentifier("mrburns");
        activity.setDestinationStream(destinationStream);
        activity.setId(activityId);

        final String cacheKey = CacheKeys.ENTITY_STREAM_BY_SCOPE_ID + scopeId;

        assertEquals(null, getCache().get(cacheKey));

        sut.execute(activity);
        assertEquals(1, getCache().getList(cacheKey).size());
        assertEquals(activityId, (long) getCache().getList(cacheKey).get(0));
    }

    /**
     * Test.
     */
    @Test(expected = RuntimeException.class)
    public void testExecuteWithResourceNotFound()
    {
        final long activityId = 98766L;

        ActivityDTO activity = new ActivityDTO();
        StreamEntityDTO destinationStream = new StreamEntityDTO();
        destinationStream.setType(EntityType.RESOURCE);
        destinationStream.setUniqueIdentifier("FOO");
        activity.setDestinationStream(destinationStream);
        activity.setId(activityId);

        sut.execute(activity);
    }

    /**
     * Tests execute method with a resource scope.
     */
    @Test
    public void testExecuteWithResource()
    {
        final long activityId = 98766L;
        final long scopeId = 100;
        final long actorscopeId = 4;

        ActivityDTO activity = new ActivityDTO();
        StreamEntityDTO destinationStream = new StreamEntityDTO();
        destinationStream.setType(EntityType.RESOURCE);
        destinationStream.setUniqueIdentifier("resource1");

        StreamEntityDTO actorStream = new StreamEntityDTO();
        actorStream.setType(EntityType.PERSON);
        actorStream.setUniqueIdentifier("mrburns");

        activity.setDestinationStream(destinationStream);
        activity.setActor(actorStream);
        activity.setId(activityId);

        final String cacheKey = CacheKeys.ENTITY_STREAM_BY_SCOPE_ID + scopeId;
        final String actorCacheKey = CacheKeys.ENTITY_STREAM_BY_SCOPE_ID + actorscopeId;

        assertEquals(null, getCache().get(cacheKey));
        assertEquals(null, getCache().get(actorCacheKey));

        sut.execute(activity);
        assertEquals(1, getCache().getList(cacheKey).size());
        assertEquals(activityId, (long) getCache().getList(cacheKey).get(0));

        assertEquals(1, getCache().getList(actorCacheKey).size());
        assertEquals(activityId, (long) getCache().getList(actorCacheKey).get(0));
    }

    /**
     * Tests execute method with a resource scope.
     */
    @Test
    public void testExecuteWithResourceShowInStreamFalse()
    {
        final long activityId = 98766L;
        final long scopeId = 100;
        final long actorscopeId = 4;

        ActivityDTO activity = new ActivityDTO();
        activity.setShowInStream(false);
        StreamEntityDTO destinationStream = new StreamEntityDTO();
        destinationStream.setType(EntityType.RESOURCE);
        destinationStream.setUniqueIdentifier("resource1");

        StreamEntityDTO actorStream = new StreamEntityDTO();
        actorStream.setType(EntityType.PERSON);
        actorStream.setUniqueIdentifier("mrburns");

        activity.setDestinationStream(destinationStream);
        activity.setActor(actorStream);
        activity.setId(activityId);

        final String cacheKey = CacheKeys.ENTITY_STREAM_BY_SCOPE_ID + scopeId;
        final String actorCacheKey = CacheKeys.ENTITY_STREAM_BY_SCOPE_ID + actorscopeId;

        assertEquals(null, getCache().get(cacheKey));
        assertEquals(null, getCache().get(actorCacheKey));

        sut.execute(activity);
        assertEquals(1, getCache().getList(cacheKey).size());
        assertEquals(activityId, (long) getCache().getList(cacheKey).get(0));

        assertEquals(null, getCache().get(actorCacheKey));
    }

    /**
     * Tests execute method with an unsupported scope type.
     */
    @Test
    public void testExecuteWithBadType()
    {
        final long activityId = 98767;
        final long scopeId = 837433;

        ActivityDTO activity = new ActivityDTO();
        StreamEntityDTO destinationStream = new StreamEntityDTO();
        destinationStream.setType(EntityType.APPLICATION);
        destinationStream.setUniqueIdentifier("tstorgname");
        activity.setDestinationStream(destinationStream);
        activity.setId(activityId);

        final String cacheKey = CacheKeys.ENTITY_STREAM_BY_SCOPE_ID + scopeId;

        assertEquals(null, getCache().get(cacheKey));

        sut.execute(activity);
        assertEquals(null, getCache().get(cacheKey));
    }

}
