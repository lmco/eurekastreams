/*
 * Copyright (c) 2010-2013 Lockheed Martin Corporation
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

import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eurekastreams.commons.search.ProjectionSearchRequestBuilder;
import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.server.persistence.mappers.cache.GetPrivateCoordinatedAndFollowedGroupIdsForUser;
import org.eurekastreams.server.persistence.mappers.requests.GetEntitiesByPrefixRequest;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.hibernate.search.jpa.FullTextQuery;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for SearchPeopleAndGroupsByPrefix class.
 * 
 */
public class SearchPeopleAndGroupsByPrefixTest
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
     * Mock Search request builder.
     */
    private final ProjectionSearchRequestBuilder searchRequestBuilder = context
            .mock(ProjectionSearchRequestBuilder.class);

    /**
     * Mock Mapper to get a list of all group ids that aren't public that a user can see activity for.
     */
    private final GetPrivateCoordinatedAndFollowedGroupIdsForUser getGroupIdsMapper = context
            .mock(GetPrivateCoordinatedAndFollowedGroupIdsForUser.class);

    /**
     * Mapper used to translate user accountId to DB id.
     */
    private final DomainMapper<String, Long> getPersonIdByAccountIdMapper = context.mock(DomainMapper.class,
            "getPersonIdByAccountIdMapper");

    /**
     * Mock GetEntitiesByPrefixRequest.
     */
    private final GetEntitiesByPrefixRequest request = context.mock(GetEntitiesByPrefixRequest.class);

    /**
     * Mock full text query.
     */
    private final FullTextQuery query = context.mock(FullTextQuery.class);

    /**
     * PersonModelView mock.
     */
    private final PersonModelView personView = context.mock(PersonModelView.class);

    /**
     * DomainGroupModelView mock.
     */
    private final DomainGroupModelView groupView = context.mock(DomainGroupModelView.class);

    /**
     * Search results.
     */
    private List<ModelView> searchResults;

    /**
     * Search prefix used in tests.
     */
    private final String searchPrefix = "foo";

    /**
     * User key used in tests.
     */
    private final String userKey = "userKey";

    /**
     * User Id used in tests.
     */
    private final Long userId = 1L;

    /**
     * System under test.
     */
    private SearchPeopleAndGroupsByPrefix sut;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new SearchPeopleAndGroupsByPrefix(9, searchRequestBuilder, getGroupIdsMapper,
                getPersonIdByAccountIdMapper, true);
        searchResults = new ArrayList<ModelView>();
        searchResults.add(personView);
        searchResults.add(groupView);
    }

    /**
     * Test the execute method.
     */
    @Test
    public void testExecuteWithEmptyGroupList()
    {
        final String emptyGroupSearchString = "+(name:(foo* foo) lastName:(foo* foo) "
                + "preferredName:(foo* foo)^0.5) +isStreamPostable:true +(isPublic:true )";

        context.checking(new Expectations()
        {
            {
                oneOf(request).getPrefix();
                will(returnValue(searchPrefix));

                oneOf(request).getUserKey();
                will(returnValue(userKey));

                oneOf(getPersonIdByAccountIdMapper).execute(userKey);
                will(returnValue(userId));

                oneOf(getGroupIdsMapper).execute(userId);
                will(returnValue(new HashSet<Long>()));

                oneOf(searchRequestBuilder).buildQueryFromNativeSearchString(emptyGroupSearchString);
                will(returnValue(query));

                oneOf(searchRequestBuilder).setPaging(query, 0, 9);

                oneOf(query).getResultList();
                will(returnValue(searchResults));

                allowing(personView).getAccountId();
                oneOf(personView).getDisplayName();
                oneOf(personView).getStreamId();

                allowing(groupView).getShortName();
                oneOf(groupView).getName();
                oneOf(groupView).getStreamId();

                allowing(personView).isAccountLocked();
            }
        });

        assertEquals(2, sut.execute(request).size());
        context.assertIsSatisfied();
    }

    /**
     * Test the execute method.
     */
    @Test
    public void testExecuteWithGroupList()
    {
        final LinkedHashSet<Long> groupIds = new LinkedHashSet<Long>(2);
        groupIds.add(5L);
        groupIds.add(6L);

        final String searchString = "+(name:(foo\\-foo* foo\\-foo) lastName:(foo\\-foo* foo\\-foo) "
                + "preferredName:(foo\\-foo* foo\\-foo)^0.5) +isStreamPostable:true "
                + "+(isPublic:true ( +id:(5 6 ) -isPublic:true))";

        context.checking(new Expectations()
        {
            {
                oneOf(request).getPrefix();
                will(returnValue("foo-foo"));

                oneOf(request).getUserKey();
                will(returnValue(userKey));

                oneOf(getPersonIdByAccountIdMapper).execute(userKey);
                will(returnValue(userId));

                oneOf(getGroupIdsMapper).execute(userId);
                will(returnValue(groupIds));

                oneOf(searchRequestBuilder).buildQueryFromNativeSearchString(searchString);
                will(returnValue(query));

                oneOf(searchRequestBuilder).setPaging(query, 0, 9);

                oneOf(query).getResultList();
                will(returnValue(searchResults));

                allowing(personView).getAccountId();
                oneOf(personView).getDisplayName();
                oneOf(personView).getStreamId();

                allowing(groupView).getShortName();
                oneOf(groupView).getName();
                oneOf(groupView).getStreamId();

                allowing(personView).isAccountLocked();
            }
        });

        assertEquals(2, sut.execute(request).size());
        context.assertIsSatisfied();
    }
}
