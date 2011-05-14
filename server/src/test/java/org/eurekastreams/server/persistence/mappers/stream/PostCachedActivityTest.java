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

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.SharedResource;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
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
    private final DomainMapper<Long, List<Long>> personFollowersMapper = context.mock(DomainMapper.class,
            "personFollowersMapper");

    /**
     * Mapper to get personmodelview from an accountid.
     */
    private final DomainMapper<String, PersonModelView> getPersonModelViewByAccountIdMapper = context.mock(
            DomainMapper.class, "getPersonModelViewByAccountIdMapper");

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
        final String personAccountId = "accountid";
        final PersonModelView person = context.mock(PersonModelView.class);

        final List<Long> followerIds = new ArrayList<Long>();
        final long follower1Id = 23821L;
        final long follower2Id = 23822L;
        followerIds.add(follower1Id);
        followerIds.add(follower2Id);

        final ArrayList<String> peopleAccountIds = new ArrayList<String>();
        peopleAccountIds.add(personAccountId);

        final List<String> orgShortNames = new ArrayList<String>();
        final String orgShortName1 = "abcdefg";
        final String orgShortName2 = "abcdefgh";
        final String orgShortName3 = "abcdefgi";
        orgShortNames.add(orgShortName1);
        orgShortNames.add(orgShortName2);
        orgShortNames.add(orgShortName3);

        PostCachedActivity sut = new PostCachedActivity(personFollowersMapper, getPersonModelViewByAccountIdMapper);
        sut.setCache(cache);

        final Activity act = context.mock(Activity.class);
        final StreamScope recipStreamScope = context.mock(StreamScope.class);

        context.checking(new Expectations()
        {
            {
                oneOf(getPersonModelViewByAccountIdMapper).execute("someAccountId");
                will(returnValue(person));

                allowing(person).getEntityId();
                will(returnValue(personId));

                oneOf(personFollowersMapper).execute(personId);
                will(returnValue(followerIds));

                // followers get the activity id
                oneOf(cache).addToTopOfList(CacheKeys.ACTIVITIES_BY_FOLLOWING + follower1Id, activityId);
                oneOf(cache).addToTopOfList(CacheKeys.ACTIVITIES_BY_FOLLOWING + follower2Id, activityId);

                // everyone list gets the activity id
                oneOf(cache).addToTopOfList(CacheKeys.EVERYONE_ACTIVITY_IDS, activityId);

                // -----
                // build the activity that we're passing in
                allowing(act).getId();
                will(returnValue(activityId));

                allowing(act).getRecipientStreamScope();
                will(returnValue(recipStreamScope));

                oneOf(recipStreamScope).getUniqueKey();
                will(returnValue("someAccountId"));

                oneOf(recipStreamScope).getScopeType();
                will(returnValue(ScopeType.PERSON));
                // -----

                oneOf(act).getSharedLink();
                will(returnValue(null));

                oneOf(act).getShowInStream();
                will(returnValue(true));
            }
        });

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
        final String personAccountId = "accountid";
        final PersonModelView person = context.mock(PersonModelView.class);
        final List<PersonModelView> people = new ArrayList<PersonModelView>();
        people.add(person);

        final List<Long> followerIds = new ArrayList<Long>();

        final ArrayList<String> peopleAccountIds = new ArrayList<String>();
        peopleAccountIds.add(personAccountId);

        final List<String> orgShortNames = new ArrayList<String>();
        final String orgShortName1 = "abcdefg";
        final String orgShortName2 = "abcdefgh";
        final String orgShortName3 = "abcdefgi";
        orgShortNames.add(orgShortName1);
        orgShortNames.add(orgShortName2);
        orgShortNames.add(orgShortName3);

        PostCachedActivity sut = new PostCachedActivity(personFollowersMapper, getPersonModelViewByAccountIdMapper);
        sut.setCache(cache);

        final Activity act = context.mock(Activity.class);
        final StreamScope recipStreamScope = context.mock(StreamScope.class);

        context.checking(new Expectations()
        {
            {
                oneOf(getPersonModelViewByAccountIdMapper).execute(personAccountId);
                will(returnValue(person));

                allowing(person).getEntityId();
                will(returnValue(personId));

                oneOf(personFollowersMapper).execute(personId);
                will(returnValue(followerIds));

                // everyone list gets the activity id
                oneOf(cache).addToTopOfList(CacheKeys.EVERYONE_ACTIVITY_IDS, activityId);

                // -----
                // build the activity that we're passing in
                allowing(act).getId();
                will(returnValue(activityId));

                allowing(act).getRecipientStreamScope();
                will(returnValue(recipStreamScope));

                allowing(recipStreamScope).getUniqueKey();
                will(returnValue(personAccountId));

                allowing(recipStreamScope).getScopeType();
                will(returnValue(ScopeType.PERSON));
                // -----

                allowing(act).getSharedLink();
                will(returnValue(null));

                allowing(act).getShowInStream();
                will(returnValue(true));
            }

        });

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
        PostCachedActivity sut = new PostCachedActivity(personFollowersMapper, getPersonModelViewByAccountIdMapper);
        sut.setCache(cache);

        final DomainGroupModelView group = new DomainGroupModelView();
        final List<DomainGroupModelView> groups = new ArrayList<DomainGroupModelView>();
        groups.add(group);

        final List<String> orgShortNames = new ArrayList<String>();
        final String orgShortName1 = "abcdefg";
        final String orgShortName2 = "abcdefgh";
        final String orgShortName3 = "abcdefgi";
        orgShortNames.add(orgShortName1);
        orgShortNames.add(orgShortName2);
        orgShortNames.add(orgShortName3);

        final Activity act = context.mock(Activity.class);
        final StreamScope recipStreamScope = context.mock(StreamScope.class);

        context.checking(new Expectations()
        {
            {
                // everyone list gets the activity id
                oneOf(cache).addToTopOfList(CacheKeys.EVERYONE_ACTIVITY_IDS, activityId);

                // -----
                // build the activity that we're passing in
                allowing(act).getId();
                will(returnValue(activityId));

                allowing(act).getRecipientStreamScope();
                will(returnValue(recipStreamScope));

                allowing(recipStreamScope).getScopeType();
                will(returnValue(ScopeType.GROUP));

                allowing(recipStreamScope).getUniqueKey();
                will(returnValue("blah"));

                // -----

                allowing(act).getSharedLink();
                will(returnValue(null));

                allowing(act).getShowInStream();
                will(returnValue(true));
            }
        });
        sut.execute(act);

        context.assertIsSatisfied();
    }

    /**
     * Test execute with a group activity with a shared link.
     */
    @Test
    public void testExecuteGroupActivityWithSharedLink()
    {
        final long activityId = 884872L;
        PostCachedActivity sut = new PostCachedActivity(personFollowersMapper, getPersonModelViewByAccountIdMapper);
        sut.setCache(cache);
        final long sharedResourceScopeId = 3838L;

        final DomainGroupModelView group = new DomainGroupModelView();
        final List<DomainGroupModelView> groups = new ArrayList<DomainGroupModelView>();
        groups.add(group);

        final List<String> orgShortNames = new ArrayList<String>();
        final String orgShortName1 = "abcdefg";
        final String orgShortName2 = "abcdefgh";
        final String orgShortName3 = "abcdefgi";
        orgShortNames.add(orgShortName1);
        orgShortNames.add(orgShortName2);
        orgShortNames.add(orgShortName3);

        final Activity act = context.mock(Activity.class);
        final StreamScope recipStreamScope = context.mock(StreamScope.class, "recipStreamScope");

        final SharedResource sharedResource = context.mock(SharedResource.class);
        final StreamScope sharedResourceStreamScope = context.mock(StreamScope.class, "sharedResourceStreamScope");

        context.checking(new Expectations()
        {
            {
                // everyone list gets the activity id
                oneOf(cache).addToTopOfList(CacheKeys.EVERYONE_ACTIVITY_IDS, activityId);

                // -----
                // build the activity that we're passing in
                allowing(act).getId();
                will(returnValue(activityId));

                allowing(act).getRecipientStreamScope();
                will(returnValue(recipStreamScope));

                allowing(recipStreamScope).getScopeType();
                will(returnValue(ScopeType.GROUP));

                allowing(recipStreamScope).getUniqueKey();
                will(returnValue("blah"));

                // -----

                allowing(act).getSharedLink();
                will(returnValue(sharedResource));

                allowing(sharedResource).getStreamScope();
                will(returnValue(sharedResourceStreamScope));

                allowing(sharedResourceStreamScope).getId();
                will(returnValue(sharedResourceScopeId));

                oneOf(cache).addToTopOfList(CacheKeys.ENTITY_STREAM_BY_SCOPE_ID + sharedResourceScopeId, activityId);

                allowing(act).getShowInStream();
                will(returnValue(true));
            }
        });
        sut.execute(act);

        context.assertIsSatisfied();
    }

    /**
     * Test execute with an invalid recipient type.
     */
    @Test
    public void testExecuteInvalidDestinationType()
    {
        PostCachedActivity sut = new PostCachedActivity(personFollowersMapper, getPersonModelViewByAccountIdMapper);
        sut.setCache(cache);

        final Activity act = context.mock(Activity.class);
        final StreamScope recipStreamScope = context.mock(StreamScope.class);

        context.checking(new Expectations()
        {
            {

                // -----
                // build the activity that we're passing in
                allowing(act).getId();
                will(returnValue(3L));

                allowing(act).getRecipientStreamScope();
                will(returnValue(recipStreamScope));

                allowing(recipStreamScope).getScopeType();
                will(returnValue(ScopeType.ORGANIZATION));

                allowing(recipStreamScope).getUniqueKey();
                will(returnValue("blah"));
            }
        });

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
