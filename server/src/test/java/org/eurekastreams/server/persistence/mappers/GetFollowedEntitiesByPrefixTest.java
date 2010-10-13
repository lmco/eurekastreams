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
package org.eurekastreams.server.persistence.mappers;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.requests.GetEntitiesByPrefixRequest;
import org.eurekastreams.server.persistence.mappers.stream.GetFollowedPersonIds;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.DisplayEntityModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetFollowedEntitiesByPrefix DAO.
 * 
 */
public class GetFollowedEntitiesByPrefixTest extends MapperTest
{
    /**
     * mock context.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * AccountId of smithers.
     */
    final String smithersAccountId = "smithers";

    /**
     * Smithers' id.
     */
    final long smithersId = 98L;

    /**
     * Ford's id.
     */
    final long fordId = 42L;

    /**
     * Ford2's id.
     */
    final long fordId2 = 142L;

    /**
     * Burns' id.
     */
    final long burnsId = 99L;

    /**
     * ID for group "E Group 1 Name".
     */
    final long eGroup1NameId = 1L;

    /**
     * Mapper to get the people ids that a person is following.
     */
    private GetFollowedPersonIds getFollowedPersonIdsMapper = context.mock(GetFollowedPersonIds.class);

    /**
     * Mapper to get the group ids that a person is following.
     */
    private DomainMapper<Long, List<Long>> getFollowedGroupIdsMapper = context.mock(DomainMapper.class);

    /**
     * Mapper to get the person id of the user by accountid.
     */
    private GetPeopleByAccountIds getPeopleByAccountIdsMapper = context.mock(GetPeopleByAccountIds.class);

    /**
     * System under test.
     */
    private GetFollowedEntitiesByPrefix getFollowedEntitiesByPrefix;

    /**
     * Setup method - initialize the caches.
     */
    @Before
    public void setup()
    {
        getFollowedEntitiesByPrefix = new GetFollowedEntitiesByPrefix();
        getFollowedEntitiesByPrefix.setEntityManager(getEntityManager());
        getFollowedEntitiesByPrefix.setGetPeopleByAccountIdsMapper(getPeopleByAccountIdsMapper);
        getFollowedEntitiesByPrefix.setGetFollowedGroupIdsMapper(getFollowedGroupIdsMapper);
        getFollowedEntitiesByPrefix.setGetFollowedPersonIdsMapper(getFollowedPersonIdsMapper);
    }

    /**
     * Test execute expecting person matching prefix as result.
     */
    @Test
    public void testExecuteWithPersonResult()
    {
        GetEntitiesByPrefixRequest request = new GetEntitiesByPrefixRequest(smithersAccountId, "bu");

        // create a list of people ids that smithers is following
        final List<Long> peopleIdsSmithersIsFollowing = new ArrayList<Long>();
        peopleIdsSmithersIsFollowing.add(burnsId);

        // create a list of group ids that smithers is following
        final List<Long> groupIdsSmithersIsFollowing = new ArrayList<Long>();
        groupIdsSmithersIsFollowing.add(eGroup1NameId);

        context.checking(new Expectations()
        {
            {
                oneOf(getPeopleByAccountIdsMapper).fetchId(smithersAccountId);
                will(returnValue(smithersId));

                oneOf(getFollowedPersonIdsMapper).execute(smithersId);
                will(returnValue(peopleIdsSmithersIsFollowing));

                oneOf(getFollowedGroupIdsMapper).execute(smithersId);
                will(returnValue(groupIdsSmithersIsFollowing));
            }
        });

        // sut
        List<DisplayEntityModelView> results = getFollowedEntitiesByPrefix.execute(request);

        assertEquals(1, results.size());
        assertEquals(EntityType.PERSON, results.get(0).getType());
        assertEquals(4L, results.get(0).getStreamScopeId().longValue());

        context.assertIsSatisfied();
    }

    /**
     * Test execute expecting person matching prefix as result ignores account with "StreamPostable" set to false.
     */
    @Test
    public void testExecuteWithPersonResultMinusNotCommentable()
    {
        GetEntitiesByPrefixRequest request = new GetEntitiesByPrefixRequest(smithersAccountId, "F");

        // create a list of people ids that smithers is following
        final List<Long> peopleIdsSmithersIsFollowing = new ArrayList<Long>();
        peopleIdsSmithersIsFollowing.add(fordId2);
        peopleIdsSmithersIsFollowing.add(fordId);

        // create a list of group ids that smithers is following
        final List<Long> groupIdsSmithersIsFollowing = new ArrayList<Long>();
        groupIdsSmithersIsFollowing.add(eGroup1NameId);

        context.checking(new Expectations()
        {
            {
                oneOf(getPeopleByAccountIdsMapper).fetchId(smithersAccountId);
                will(returnValue(smithersId));

                oneOf(getFollowedPersonIdsMapper).execute(smithersId);
                will(returnValue(peopleIdsSmithersIsFollowing));

                oneOf(getFollowedGroupIdsMapper).execute(smithersId);
                will(returnValue(groupIdsSmithersIsFollowing));
            }
        });

        // sut
        List<DisplayEntityModelView> results = getFollowedEntitiesByPrefix.execute(request);

        assertEquals(1, results.size());
        assertEquals(EntityType.PERSON, results.get(0).getType());
        assertEquals(1L, results.get(0).getStreamScopeId().longValue());

        context.assertIsSatisfied();
    }

    /**
     * Test execute not expecting person matching prefix as result.
     */
    @Test
    public void testExecuteWithNoPersonResult()
    {
        GetEntitiesByPrefixRequest request = new GetEntitiesByPrefixRequest(smithersAccountId, "fu");

        // create a list of people ids smithers is following
        final List<Long> peopleIdsSmithersIsFollowing = new ArrayList<Long>();
        peopleIdsSmithersIsFollowing.add(burnsId);

        // create a list of group ids that smithers is following
        final List<Long> groupIdsSmithersIsFollowing = new ArrayList<Long>();
        groupIdsSmithersIsFollowing.add(eGroup1NameId);

        context.checking(new Expectations()
        {
            {
                oneOf(getPeopleByAccountIdsMapper).fetchId(smithersAccountId);
                will(returnValue(smithersId));

                oneOf(getFollowedPersonIdsMapper).execute(smithersId);
                will(returnValue(peopleIdsSmithersIsFollowing));

                oneOf(getFollowedGroupIdsMapper).execute(smithersId);
                will(returnValue(groupIdsSmithersIsFollowing));
            }
        });

        // sut
        List<DisplayEntityModelView> results = getFollowedEntitiesByPrefix.execute(request);

        assertEquals(1, results.size());
        assertEquals(EntityType.NOTSET, results.get(0).getType());

        context.assertIsSatisfied();
    }

    /**
     * Test execute with person that has no followers. (verify query short circuit).
     */
    @Test
    public void testExecuteWithNoFollowers()
    {
        GetEntitiesByPrefixRequest request = new GetEntitiesByPrefixRequest(smithersAccountId, "blah");

        // create an empty list of people ids smithers is following
        final List<Long> peopleIdsSmithersIsFollowing = new ArrayList<Long>();

        // create an empty list of group ids that smithers is following
        final List<Long> groupIdsSmithersIsFollowing = new ArrayList<Long>();

        context.checking(new Expectations()
        {
            {
                oneOf(getPeopleByAccountIdsMapper).fetchId(smithersAccountId);
                will(returnValue(smithersId));

                oneOf(getFollowedPersonIdsMapper).execute(smithersId);
                will(returnValue(peopleIdsSmithersIsFollowing));

                oneOf(getFollowedGroupIdsMapper).execute(smithersId);
                will(returnValue(groupIdsSmithersIsFollowing));
            }
        });

        // sut
        List<DisplayEntityModelView> results = getFollowedEntitiesByPrefix.execute(request);

        assertEquals(1, results.size());
        assertEquals(EntityType.NOTSET, results.get(0).getType());

        context.assertIsSatisfied();
    }

    /**
     * Test execute expecting person matching prefix as result.
     */
    @Test
    public void testExecuteWithGroupResult()
    {
        final long scopeId = 874L;
        GetEntitiesByPrefixRequest request = new GetEntitiesByPrefixRequest(smithersAccountId, "e");

        // create a list of people ids smithers is following
        final List<Long> peopleIdsSmithersIsFollowing = new ArrayList<Long>();
        peopleIdsSmithersIsFollowing.add(burnsId);

        // create a list of group ids that smithers is following
        final List<Long> groupIdsSmithersIsFollowing = new ArrayList<Long>();
        groupIdsSmithersIsFollowing.add(eGroup1NameId);

        context.checking(new Expectations()
        {
            {
                oneOf(getPeopleByAccountIdsMapper).fetchId(smithersAccountId);
                will(returnValue(smithersId));

                oneOf(getFollowedPersonIdsMapper).execute(smithersId);
                will(returnValue(peopleIdsSmithersIsFollowing));

                oneOf(getFollowedGroupIdsMapper).execute(smithersId);
                will(returnValue(groupIdsSmithersIsFollowing));
            }
        });

        // sut
        List<DisplayEntityModelView> results = getFollowedEntitiesByPrefix.execute(request);

        assertEquals(1, results.size());
        assertEquals(EntityType.GROUP, results.get(0).getType());
        assertEquals(scopeId, results.get(0).getStreamScopeId().longValue());

        context.assertIsSatisfied();
    }

    /**
     * Test execute expecting person matching prefix as result.
     */
    @Test
    public void testExecuteWithNoGroups()
    {
        GetEntitiesByPrefixRequest request = new GetEntitiesByPrefixRequest(smithersAccountId, "e");

        // create a list of people ids smithers is following
        final List<Long> peopleIdsSmithersIsFollowing = new ArrayList<Long>();
        peopleIdsSmithersIsFollowing.add(burnsId);

        // create an empty list of group ids that smithers is following
        final List<Long> groupIdsSmithersIsFollowing = new ArrayList<Long>();

        context.checking(new Expectations()
        {
            {
                oneOf(getPeopleByAccountIdsMapper).fetchId(smithersAccountId);
                will(returnValue(smithersId));

                oneOf(getFollowedPersonIdsMapper).execute(smithersId);
                will(returnValue(peopleIdsSmithersIsFollowing));

                oneOf(getFollowedGroupIdsMapper).execute(smithersId);
                will(returnValue(groupIdsSmithersIsFollowing));
            }
        });

        // sut
        List<DisplayEntityModelView> results = getFollowedEntitiesByPrefix.execute(request);

        assertEquals(1, results.size());
        assertEquals(EntityType.NOTSET, results.get(0).getType());

        context.assertIsSatisfied();
    }

}
