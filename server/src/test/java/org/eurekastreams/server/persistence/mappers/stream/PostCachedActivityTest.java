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
package org.eurekastreams.server.persistence.mappers.stream;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.GetRecursiveParentOrgIds;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.db.GetOrgShortNamesByIdsMapper;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for PostCachedActivity.
 */
public class PostCachedActivityTest
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
     * Mapper to get followers of a person.
     */
    private final GetFollowerIds personFollowersMapper = context.mock(GetFollowerIds.class);

    /**
     * Mapper to get people by account ids.
     */
    private final GetPeopleByAccountIds bulkPeopleByAccountIdMapper = context.mock(GetPeopleByAccountIds.class);

    /**
     * Mapper to get hierarchical parent org ids.
     */
    private final GetRecursiveParentOrgIds parentOrgIdsMapper = context.mock(GetRecursiveParentOrgIds.class);

    /**
     * Local instance of the {@link GetDomainGroupsByShortNames} mapper.
     */
    private final GetDomainGroupsByShortNames bulkDomainGroupsByShortNameMapper = context
            .mock(GetDomainGroupsByShortNames.class);

    /**
     * Mapper to get the short names from org ids.
     */
    private final GetOrgShortNamesByIdsMapper orgShortNamesFromIdsMapper = context
            .mock(GetOrgShortNamesByIdsMapper.class);

    /**
     * Cache.
     */
    private final Cache cache = context.mock(Cache.class);

    /**
     * Test execute for a person activity with followers.
     */
    @Test
    public void testExecutePersonActivityWithFollowers()
    {
        final long activityId = 884872L;
        final long personId = 1737L;
        final long recipientParentOrgId = 877777L;
        final String personAccountId = "accountid";
        final PersonModelView person = context.mock(PersonModelView.class);
        final List<PersonModelView> people = new ArrayList<PersonModelView>();
        people.add(person);

        final List<Long> followerIds = new ArrayList<Long>();
        final long follower1Id = 23821L;
        final long follower2Id = 23822L;
        followerIds.add(follower1Id);
        followerIds.add(follower2Id);

        final List<Long> parentOrgIds = context.mock(List.class);

        final ArrayList<String> peopleAccountIds = new ArrayList<String>();
        peopleAccountIds.add(personAccountId);

        final List<String> orgShortNames = new ArrayList<String>();
        final String orgShortName1 = "abcdefg";
        final String orgShortName2 = "abcdefgh";
        final String orgShortName3 = "abcdefgi";
        orgShortNames.add(orgShortName1);
        orgShortNames.add(orgShortName2);
        orgShortNames.add(orgShortName3);

        PostCachedActivity sut = new PostCachedActivity(personFollowersMapper, bulkPeopleByAccountIdMapper,
                parentOrgIdsMapper, bulkDomainGroupsByShortNameMapper, orgShortNamesFromIdsMapper);
        sut.setCache(cache);

        context.checking(new Expectations()
        {
            {
                oneOf(bulkPeopleByAccountIdMapper).execute(with(any(List.class)));
                will(returnValue(people));

                allowing(person).getEntityId();
                will(returnValue(personId));

                oneOf(person).getParentOrganizationId();
                will(returnValue(recipientParentOrgId));

                oneOf(personFollowersMapper).execute(personId);
                will(returnValue(followerIds));

                // followers get the activity id
                oneOf(cache).addToTopOfList(CacheKeys.ACTIVITIES_BY_FOLLOWING + follower1Id, activityId);
                oneOf(cache).addToTopOfList(CacheKeys.ACTIVITIES_BY_FOLLOWING + follower2Id, activityId);

                // everyone list gets the activity id
                oneOf(cache).addToTopOfList(CacheKeys.EVERYONE_ACTIVITY_IDS, activityId);

                oneOf(parentOrgIdsMapper).execute(recipientParentOrgId);
                will(returnValue(parentOrgIds));

                oneOf(parentOrgIds).add(recipientParentOrgId);

                oneOf(orgShortNamesFromIdsMapper).execute(parentOrgIds);
                will(returnValue(orgShortNames));

                oneOf(cache).addToTopOfList(CacheKeys.ACTIVITY_IDS_FOR_ORG_BY_SHORTNAME_RECURSIVE + orgShortName1,
                        activityId);
                oneOf(cache).addToTopOfList(CacheKeys.ACTIVITY_IDS_FOR_ORG_BY_SHORTNAME_RECURSIVE + orgShortName2,
                        activityId);
                oneOf(cache).addToTopOfList(CacheKeys.ACTIVITY_IDS_FOR_ORG_BY_SHORTNAME_RECURSIVE + orgShortName3,
                        activityId);
            }
        });

        ActivityDTO act = new ActivityDTO();
        act.setId(activityId);
        StreamEntityDTO entity = new StreamEntityDTO();
        entity.setUniqueIdentifier(personAccountId);
        entity.setType(EntityType.PERSON);
        act.setDestinationStream(entity);

        sut.execute(act);

        context.assertIsSatisfied();
    }

    /**
     * Test execute for a person activity with followers.
     */
    @Test
    public void testExecutePersonActivityWithNoFollowers()
    {
        final long activityId = 884872L;
        final long personId = 1737L;
        final long recipientParentOrgId = 877777L;
        final String personAccountId = "accountid";
        final PersonModelView person = context.mock(PersonModelView.class);
        final List<PersonModelView> people = new ArrayList<PersonModelView>();
        people.add(person);

        final List<Long> followerIds = new ArrayList<Long>();

        final List<Long> parentOrgIds = context.mock(List.class);

        final ArrayList<String> peopleAccountIds = new ArrayList<String>();
        peopleAccountIds.add(personAccountId);

        final List<String> orgShortNames = new ArrayList<String>();
        final String orgShortName1 = "abcdefg";
        final String orgShortName2 = "abcdefgh";
        final String orgShortName3 = "abcdefgi";
        orgShortNames.add(orgShortName1);
        orgShortNames.add(orgShortName2);
        orgShortNames.add(orgShortName3);

        PostCachedActivity sut = new PostCachedActivity(personFollowersMapper, bulkPeopleByAccountIdMapper,
                parentOrgIdsMapper, bulkDomainGroupsByShortNameMapper, orgShortNamesFromIdsMapper);
        sut.setCache(cache);

        context.checking(new Expectations()
        {
            {
                oneOf(bulkPeopleByAccountIdMapper).execute(with(any(List.class)));
                will(returnValue(people));

                allowing(person).getEntityId();
                will(returnValue(personId));

                oneOf(person).getParentOrganizationId();
                will(returnValue(recipientParentOrgId));

                oneOf(personFollowersMapper).execute(personId);
                will(returnValue(followerIds));

                // everyone list gets the activity id
                oneOf(cache).addToTopOfList(CacheKeys.EVERYONE_ACTIVITY_IDS, activityId);

                oneOf(parentOrgIdsMapper).execute(recipientParentOrgId);
                will(returnValue(parentOrgIds));

                oneOf(parentOrgIds).add(recipientParentOrgId);

                oneOf(orgShortNamesFromIdsMapper).execute(parentOrgIds);
                will(returnValue(orgShortNames));

                oneOf(cache).addToTopOfList(CacheKeys.ACTIVITY_IDS_FOR_ORG_BY_SHORTNAME_RECURSIVE + orgShortName1,
                        activityId);
                oneOf(cache).addToTopOfList(CacheKeys.ACTIVITY_IDS_FOR_ORG_BY_SHORTNAME_RECURSIVE + orgShortName2,
                        activityId);
                oneOf(cache).addToTopOfList(CacheKeys.ACTIVITY_IDS_FOR_ORG_BY_SHORTNAME_RECURSIVE + orgShortName3,
                        activityId);
            }
        });

        ActivityDTO act = new ActivityDTO();
        act.setId(activityId);
        StreamEntityDTO entity = new StreamEntityDTO();
        entity.setUniqueIdentifier(personAccountId);
        entity.setType(EntityType.PERSON);
        act.setDestinationStream(entity);

        sut.execute(act);

        context.assertIsSatisfied();
    }

    /**
     * Test execute with a group activity.
     */
    @Test
    public void testExecuteGroupActivity()
    {
        final long activityId = 884872L;
        PostCachedActivity sut = new PostCachedActivity(personFollowersMapper, bulkPeopleByAccountIdMapper,
                parentOrgIdsMapper, bulkDomainGroupsByShortNameMapper, orgShortNamesFromIdsMapper);
        sut.setCache(cache);
        final long recipientParentOrgId = 877777L;

        final DomainGroupModelView group = new DomainGroupModelView();
        group.setParentOrganizationId(recipientParentOrgId);
        final List<DomainGroupModelView> groups = new ArrayList<DomainGroupModelView>();
        groups.add(group);

        final List<Long> parentOrgIds = context.mock(List.class);

        final List<String> orgShortNames = new ArrayList<String>();
        final String orgShortName1 = "abcdefg";
        final String orgShortName2 = "abcdefgh";
        final String orgShortName3 = "abcdefgi";
        orgShortNames.add(orgShortName1);
        orgShortNames.add(orgShortName2);
        orgShortNames.add(orgShortName3);

        context.checking(new Expectations()
        {
            {
                oneOf(bulkDomainGroupsByShortNameMapper).execute(with(any(List.class)));
                will(returnValue(groups));

                // everyone list gets the activity id
                oneOf(cache).addToTopOfList(CacheKeys.EVERYONE_ACTIVITY_IDS, activityId);

                oneOf(parentOrgIdsMapper).execute(recipientParentOrgId);
                will(returnValue(parentOrgIds));

                oneOf(parentOrgIds).add(recipientParentOrgId);

                oneOf(orgShortNamesFromIdsMapper).execute(parentOrgIds);
                will(returnValue(orgShortNames));

                oneOf(cache).addToTopOfList(CacheKeys.ACTIVITY_IDS_FOR_ORG_BY_SHORTNAME_RECURSIVE + orgShortName1,
                        activityId);
                oneOf(cache).addToTopOfList(CacheKeys.ACTIVITY_IDS_FOR_ORG_BY_SHORTNAME_RECURSIVE + orgShortName2,
                        activityId);
                oneOf(cache).addToTopOfList(CacheKeys.ACTIVITY_IDS_FOR_ORG_BY_SHORTNAME_RECURSIVE + orgShortName3,
                        activityId);
            }
        });

        ActivityDTO act = new ActivityDTO();
        StreamEntityDTO entity = new StreamEntityDTO();
        entity.setType(EntityType.GROUP);
        act.setDestinationStream(entity);
        act.setId(activityId);

        sut.execute(act);

        context.assertIsSatisfied();
    }

    /**
     * Test execute with an invalid recipient type.
     */
    @Test
    public void testExecuteInvalidDestinationType()
    {
        PostCachedActivity sut = new PostCachedActivity(personFollowersMapper, bulkPeopleByAccountIdMapper,
                parentOrgIdsMapper, bulkDomainGroupsByShortNameMapper, orgShortNamesFromIdsMapper);
        sut.setCache(cache);

        ActivityDTO act = new ActivityDTO();
        StreamEntityDTO entity = new StreamEntityDTO();
        entity.setType(EntityType.ORGANIZATION);
        act.setDestinationStream(entity);

        boolean exceptionFired = false;
        try
        {
            sut.execute(act);
        }
        catch (RuntimeException ex)
        {
            exceptionFired = true;
        }
        assertTrue(exceptionFired);
    }
}
