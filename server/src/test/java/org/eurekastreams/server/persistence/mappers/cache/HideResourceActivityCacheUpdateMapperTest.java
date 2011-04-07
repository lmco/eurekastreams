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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for HideResourceActivityCacheUpdateMapper.
 * 
 */
public class HideResourceActivityCacheUpdateMapperTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * ActivityDTO DAO.
     */
    private DomainMapper<Long, ActivityDTO> activityDAO = context.mock(DomainMapper.class, "activityDAO");

    /**
     * Mapper to get a PersonModelView by account id.
     */
    private DomainMapper<String, PersonModelView> getPersonModelViewByAccountIdMapper = context.mock(
            DomainMapper.class, "getPersonModelViewByAccountIdMapper");

    /**
     * DAO to get followers of a person.
     */
    private DomainMapper<Long, List<Long>> userIdsFollowingPersonDAO = context.mock(DomainMapper.class,
            "userIdsFollowingPersonDAO");

    /**
     * System under test.
     */
    private HideResourceActivityCacheUpdateMapper sut = new HideResourceActivityCacheUpdateMapper(activityDAO,
            getPersonModelViewByAccountIdMapper, userIdsFollowingPersonDAO);

    /**
     * ActivityDTO.
     */
    private ActivityDTO activityDTO = context.mock(ActivityDTO.class);

    /**
     * Destination StreamEntityDTO.
     */
    private StreamEntityDTO destStreamEntityDTO = context.mock(StreamEntityDTO.class, "destStreamEntityDTO");

    /**
     * Actor StreamEntityDTO.
     */
    private StreamEntityDTO actorStreamEntityDTO = context.mock(StreamEntityDTO.class, "actorStreamEntityDTO");

    /**
     * PersonModelView mock.
     */
    private PersonModelView pmv = context.mock(PersonModelView.class);

    /**
     * Cache mock.
     */
    private Cache cache = context.mock(Cache.class);

    /**
     * Activity id.
     */
    private Long activityId = 5L;

    /**
     * Actor stream id.
     */
    private Long actorStreamId = 6L;

    /**
     * Actor id.
     */
    private Long actorId = 7L;

    /**
     * Follower Id.
     */
    private Long followerId = 2L;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut.setCache(cache);
    }

    /**
     * Test.
     */
    @Test
    public void test()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(activityDAO).execute(activityId);
                will(returnValue(activityDTO));

                oneOf(activityDTO).getShowInStream();
                will(returnValue(false));

                oneOf(activityDTO).getDestinationStream();
                will(returnValue(destStreamEntityDTO));

                allowing(destStreamEntityDTO).getType();
                will(returnValue(EntityType.RESOURCE));

                allowing(activityDTO).getActor();
                will(returnValue(actorStreamEntityDTO));

                allowing(actorStreamEntityDTO).getUniqueIdentifier();
                will(returnValue("actor"));

                allowing(getPersonModelViewByAccountIdMapper).execute("actor");
                will(returnValue(pmv));

                allowing(pmv).getStreamId();
                will(returnValue(actorStreamId));

                oneOf(cache).removeFromList(CacheKeys.ENTITY_STREAM_BY_SCOPE_ID + actorStreamId, activityId);

                allowing(pmv).getId();
                will(returnValue(actorId));

                oneOf(userIdsFollowingPersonDAO).execute(actorId);
                will(returnValue(new ArrayList<Long>(Arrays.asList(followerId))));

                oneOf(cache).removeFromList(CacheKeys.ACTIVITIES_BY_FOLLOWING + followerId, activityId);
            }
        });

        sut.execute(activityId);
        context.assertIsSatisfied();

    }

    /**
     * Test.
     */
    @Test
    public void testShortCircuitShowInStreamTrue()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(activityDAO).execute(activityId);
                will(returnValue(activityDTO));

                oneOf(activityDTO).getShowInStream();
                will(returnValue(true));
            }
        });

        sut.execute(activityId);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testShortCircuitActivityNull()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(activityDAO).execute(activityId);
                will(returnValue(null));
            }
        });

        sut.execute(activityId);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testShortCircuitWrongActivityDestinationType()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(activityDAO).execute(activityId);
                will(returnValue(activityDTO));

                oneOf(activityDTO).getShowInStream();
                will(returnValue(false));

                oneOf(activityDTO).getDestinationStream();
                will(returnValue(destStreamEntityDTO));

                oneOf(destStreamEntityDTO).getType();
                will(returnValue(EntityType.GROUP));
            }
        });

        sut.execute(activityId);
        context.assertIsSatisfied();
    }
}
