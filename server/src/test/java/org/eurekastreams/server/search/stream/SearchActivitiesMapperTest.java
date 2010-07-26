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
package org.eurekastreams.server.search.stream;

import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.search.ProjectionSearchRequestBuilder;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.GetStreamScopesByStreamViewId;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.cache.OrganizationHierarchyCache;
import org.eurekastreams.server.persistence.mappers.requests.StreamSearchRequest;
import org.eurekastreams.server.persistence.mappers.stream.BuildActivityStreamSearchStringForUser;
import org.eurekastreams.server.persistence.mappers.stream.BulkActivitiesMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for SearchActivitiesMapper.
 */
public class SearchActivitiesMapperTest
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
     * Search terms to be reused.
     */
    private String searchTerms = "hello there";

    /**
     * Mock ProjectionSearchRequestBuilder.
     */
    private ProjectionSearchRequestBuilder projectionSearchRequestBuilderMock;

    /**
     * System under test.
     */
    private SearchActivitiesMapper sut;

    /**
     * OrganizationHierarchyCache mock.
     */
    private OrganizationHierarchyCache orgCacheMock;

    /**
     * Factory to build the SearchResultSecurityScoper.
     */
    private SearchResultListScoperFactory searchResultScoperFactoryMock;

    /**
     * Factory to build StreamViewActivityIdListPageFetcherFactory.
     */
    private StreamViewActivityIdListPageFetcherFactory streamViewActivityIdListPageFetcherFactoryMock;

    /**
     * Mapper to get stream scopes from a stream view id.
     */
    private GetStreamScopesByStreamViewId getStreamScopesByStreamViewIdMapper;

    /**
     * Mocked ActivityIdSearchPageFetcher.
     */
    private ActivityIdSearchPageFetcher activityIdSearchPageFetcherMock = context
            .mock(ActivityIdSearchPageFetcher.class);

    /**
     * Mocked ActivityIdSearchPageFetcherFactory.
     */
    private ActivityIdSearchPageFetcherFactory activityIdSearchPageFetcherFactoryMock;

    /**
     * Mocked GetPeopleByAccountIds.
     */
    private GetPeopleByAccountIds getPeopleByAccountIdsMapperMock = context.mock(GetPeopleByAccountIds.class);

    /**
     * Test group scope.
     */
    private String groupAShortName = "groupa";

    /**
     * ID of test group A.
     */
    private final long groupAId = 8884L;

    /**
     * Test group scope.
     */
    private String groupBShortName = "groupb";

    /**
     * ID of test group B.
     */
    private final long groupBId = 88324L;

    /**
     * Test Person scope.
     */
    private String personAAccountId = "persona";

    /**
     * ID of test person A.
     */
    private final long personAId = 3232L;

    /**
     * Test Person scope.
     */
    private String personBAccountId = "personb";

    /**
     * ID of test person A.
     */
    private final long personBId = 3992L;

    /**
     * Mocked GetDomainGroupsByShortNames.
     */
    private GetDomainGroupsByShortNames getDomainGroupsByShortNames;

    /**
     * Current user's account id.
     */
    private String currentUserAccountId = "currentUserAccountId";

    /**
     * Current user's ID.
     */
    private final long currentUserId = 88271;

    /**
     * The organization id of the current user.
     */
    private final long currentUserParentOrgId = 8884482L;

    /**
     * The short name of organization A.
     */
    private String organizationAShortName = "orgashortname";

    /**
     * the ID of org A.
     */
    private final long organizationAId = 918475L;

    /**
     * Last seen activity id to use.
     */
    private final long lastSeenActivityId = 883L;

    /**
     * Bulk activities mapper.
     */
    private BulkActivitiesMapper bulkActivitiesMapper;

    /**
     * PersonModelView for person A.
     */
    private PersonModelView personAModelView;

    /**
     * PersonModelView for person B.
     */
    private PersonModelView personBModelView;

    /**
     * PersonModelView for current user.
     */
    private PersonModelView currentUserPersonModelView;

    /**
     * Lucene query string to be returned by getActivityStreamSearchStringForUserMapper for a user searching all
     * activities.
     */
    private String userScopedActivitiesSearchQuery = "lskdfjsdlfsdj";

    /**
     * Mapper to get the scoped search string for searching the everyone list.
     */
    private BuildActivityStreamSearchStringForUser buildActivityStreamSearchStringForUserMapper = context
            .mock(BuildActivityStreamSearchStringForUser.class);

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        personAModelView = new PersonModelView();
        personBModelView = new PersonModelView();
        currentUserPersonModelView = new PersonModelView();

        personAModelView.setAccountId(personAAccountId);
        personAModelView.setEntityId(personAId);

        personBModelView.setAccountId(personBAccountId);
        personBModelView.setEntityId(personBId);

        currentUserPersonModelView.setAccountId(currentUserAccountId);
        currentUserPersonModelView.setEntityId(currentUserId);
        currentUserPersonModelView.setParentOrganizationId(currentUserParentOrgId);

        sut = new SearchActivitiesMapper();
        projectionSearchRequestBuilderMock = context.mock(ProjectionSearchRequestBuilder.class);
        orgCacheMock = context.mock(OrganizationHierarchyCache.class);
        searchResultScoperFactoryMock = context.mock(SearchResultListScoperFactory.class);
        streamViewActivityIdListPageFetcherFactoryMock = context.mock(StreamViewActivityIdListPageFetcherFactory.class);
        getStreamScopesByStreamViewIdMapper = context.mock(GetStreamScopesByStreamViewId.class);
        getDomainGroupsByShortNames = context.mock(GetDomainGroupsByShortNames.class);
        bulkActivitiesMapper = context.mock(BulkActivitiesMapper.class);
        activityIdSearchPageFetcherFactoryMock = context.mock(ActivityIdSearchPageFetcherFactory.class);

        // wire up the SUT
        sut.setSearchRequestBuilder(projectionSearchRequestBuilderMock);
        sut.setOrgCache(orgCacheMock);
        sut.setSearchResultScoperFactory(searchResultScoperFactoryMock);
        sut.setStreamViewActivityIdListPageFetcherFactory(streamViewActivityIdListPageFetcherFactoryMock);
        sut.setGetStreamScopesByStreamViewIdMapper(getStreamScopesByStreamViewIdMapper);
        sut.setGetDomainGroupsByShortNames(getDomainGroupsByShortNames);
        sut.setBulkActivitiesMapper(bulkActivitiesMapper);
        sut.setActivityIdSearchPageFetcherFactory(activityIdSearchPageFetcherFactoryMock);
        sut.setGetPeopleByAccountIdsMapper(getPeopleByAccountIdsMapperMock);
        sut.setBuildActivityStreamSearchStringForUserMapper(buildActivityStreamSearchStringForUserMapper);
    }

    /**
     * Test searching with scopes that includes one for searching all activities.
     */
    @Test
    public void testExecuteWithBareWildcardComponent()
    {
        final String expectedLuceneQuery = "+(" + userScopedActivitiesSearchQuery
                + ") +content:escapedHello +content:escapedThere";

        final List<StreamScope> scopes = new ArrayList<StreamScope>();
        scopes.add(new StreamScope(ScopeType.GROUP, "abcdefg"));
        scopes.add(new StreamScope(ScopeType.ALL, "ALL"));

        context.checking(new Expectations()
        {
            {
                // make sure query was split apart and escaped properly
                allowing(projectionSearchRequestBuilderMock).escapeAllButWildcardCharacters("*");
                will(returnValue(""));

                allowing(projectionSearchRequestBuilderMock).escapeAllButWildcardCharacters("***");
                will(returnValue(""));

                allowing(projectionSearchRequestBuilderMock).escapeAllButWildcardCharacters("?");
                will(returnValue(""));

                allowing(projectionSearchRequestBuilderMock).escapeAllButWildcardCharacters("??");
                will(returnValue(""));
            }
        });

        searchContentWithoutListsHelper("hello * ? *** ?? there", lastSeenActivityId, expectedLuceneQuery, scopes);
    }

    /**
     * Test searching with a NOT word.
     */
    @Test
    public void testExecuteWithNotSearch()
    {
        final String expectedLuceneQuery = "+(" + userScopedActivitiesSearchQuery
                + ") +content:escapedHello -content:escapedThere";

        final List<StreamScope> scopes = new ArrayList<StreamScope>();
        scopes.add(new StreamScope(ScopeType.GROUP, "abcdefg"));
        scopes.add(new StreamScope(ScopeType.ALL, "ALL"));

        context.checking(new Expectations()
        {
            {
                // make sure query was split apart and escaped properly
                allowing(projectionSearchRequestBuilderMock).escapeAllButWildcardCharacters("-there");
                will(returnValue("\\-escapedThere"));
            }
        });

        searchContentWithoutListsHelper("hello -there", lastSeenActivityId, expectedLuceneQuery, scopes);
    }

    /**
     * Test searching with scopes that includes one for searching all activities.
     */
    @Test
    public void testExecuteWithScopeIncludingAllScope()
    {
        final String expectedLuceneQuery = "+(" + userScopedActivitiesSearchQuery
                + ") +content:escapedHello +content:escapedThere";

        final List<StreamScope> scopes = new ArrayList<StreamScope>();
        scopes.add(new StreamScope(ScopeType.GROUP, "abcdefg"));
        scopes.add(new StreamScope(ScopeType.ALL, "ALL"));

        searchContentWithoutListsHelper(searchTerms, lastSeenActivityId, expectedLuceneQuery, scopes);
    }

    /**
     * Test searching with scopes that includes one for searching all activities.
     */
    @Test
    public void testExecuteWithScopeIncludingAllScopeAndFirstPage()
    {
        final String expectedLuceneQuery = "+(" + userScopedActivitiesSearchQuery
                + ") +content:escapedHello +content:escapedThere";

        final List<StreamScope> scopes = new ArrayList<StreamScope>();
        scopes.add(new StreamScope(ScopeType.GROUP, "abcdefg"));
        scopes.add(new StreamScope(ScopeType.ALL, "ALL"));

        searchContentWithoutListsHelper(searchTerms, 0L, expectedLuceneQuery, scopes);
    }

    /**
     * Test searching with scopes that includes one for searching all activities due to no scopes being included.
     */
    @Test
    public void testExecuteWithScopeIncludingNoScopesAndFirstPage()
    {
        final String expectedLuceneQuery = "+(" + userScopedActivitiesSearchQuery
                + ") +content:escapedHello +content:escapedThere";
        searchContentWithoutListsHelper(searchTerms, 0L, expectedLuceneQuery, new ArrayList<StreamScope>());
    }

    /**
     * Test searching with scopes that includes one for searching all activities, also asking for starred activities,
     * making sure its clever enough to realize that ALL trumps Starred, going to lucene alone.
     */
    @Test
    public void testExecuteWithScopeIncludingAllScopeAndStarred()
    {
        final String expectedLuceneQuery = "+(" + userScopedActivitiesSearchQuery
                + ") +content:escapedHello +content:escapedThere";

        final List<StreamScope> scopes = new ArrayList<StreamScope>();
        scopes.add(new StreamScope(ScopeType.GROUP, "abcdefg"));
        scopes.add(new StreamScope(ScopeType.STARRED, "abcdefg"));
        scopes.add(new StreamScope(ScopeType.ALL, "ALL"));

        searchContentWithoutListsHelper(searchTerms, lastSeenActivityId, expectedLuceneQuery, scopes);
    }

    /**
     * Test searching all content, with no search terms.
     */
    @Test
    public void testExecuteWithAllContentAndNoSearchTerms()
    {
        final String expectedLuceneQuery = "+(" + userScopedActivitiesSearchQuery + ") ";

        final List<StreamScope> scopes = new ArrayList<StreamScope>();
        scopes.add(new StreamScope(ScopeType.GROUP, "abcdefg"));
        scopes.add(new StreamScope(ScopeType.STARRED, "abcdefg"));
        scopes.add(new StreamScope(ScopeType.ALL, "ALL"));

        searchContentWithoutListsHelper("  ", lastSeenActivityId, expectedLuceneQuery, scopes);
    }

    /**
     * Test searching scoped content without the need for scoping against a list.
     */
    @Test
    public void testSearchingContentWithScopes()
    {
        final List<String> groupShortNames = new ArrayList<String>();
        groupShortNames.add(groupAShortName);
        groupShortNames.add(groupBShortName);

        final List<String> peopleAccountIdsToLookup = new ArrayList<String>();
        peopleAccountIdsToLookup.add(personAAccountId);
        peopleAccountIdsToLookup.add(personBAccountId);
        peopleAccountIdsToLookup.add(currentUserAccountId);

        final List<PersonModelView> peopleReturnedFromLookup = new ArrayList<PersonModelView>();
        peopleReturnedFromLookup.add(personAModelView);
        peopleReturnedFromLookup.add(personBModelView);
        peopleReturnedFromLookup.add(currentUserPersonModelView);

        final Map<String, Long> groupIdsMap = new HashMap<String, Long>();
        groupIdsMap.put(CacheKeys.GROUP_BY_SHORT_NAME + groupAShortName, groupAId);
        groupIdsMap.put(CacheKeys.GROUP_BY_SHORT_NAME + groupBShortName, groupBId);

        context.checking(new Expectations()
        {
            {
                oneOf(getDomainGroupsByShortNames).fetchIds(groupShortNames);
                will(returnValue(groupIdsMap));

                oneOf(getPeopleByAccountIdsMapperMock).execute(peopleAccountIdsToLookup);
                will(returnValue(peopleReturnedFromLookup));
            }
        });

        final String expectedLuceneQuery = "+(" + userScopedActivitiesSearchQuery
                + ") +content:escapedHello +content:escapedThere" + " +(recipient:g" + groupAId + " recipient:g"
                + groupBId + " recipient:p" + personAId + " recipient:p" + personBId + " recipientParentOrgId:"
                + organizationAId + " recipientParentOrgId:" + currentUserParentOrgId + ")";

        final List<StreamScope> scopes = new ArrayList<StreamScope>();
        scopes.add(new StreamScope(ScopeType.GROUP, groupAShortName));
        scopes.add(new StreamScope(ScopeType.GROUP, groupBShortName));
        scopes.add(new StreamScope(ScopeType.PERSON, personAAccountId));
        scopes.add(new StreamScope(ScopeType.PERSON, personBAccountId));
        scopes.add(new StreamScope(ScopeType.ORGANIZATION, organizationAShortName));
        scopes.add(new StreamScope(ScopeType.PERSONS_PARENT_ORGANIZATION, currentUserAccountId));

        searchContentWithoutListsHelper(searchTerms, lastSeenActivityId, expectedLuceneQuery, scopes);
    }

    /**
     * Test executing a search with criteria that requires us to search all activities, then scope the list to those in
     * the cached ID lists.
     */
    @Test
    public void testExecuteWithCachedIdListFromStarred()
    {
        final List<StreamScope> scopes = new ArrayList<StreamScope>();
        scopes.add(new StreamScope(ScopeType.GROUP, "abcdefg"));
        scopes.add(new StreamScope(ScopeType.STARRED, currentUserAccountId));
        executeWithCachedIdListHelper(scopes);
    }

    /**
     * Test executing a search with criteria that requires us to search all activities, then scope the list to those in
     * the cached ID lists.
     */
    @Test
    public void testExecuteWithCachedIdListFromFollowed()
    {
        final List<StreamScope> scopes = new ArrayList<StreamScope>();
        scopes.add(new StreamScope(ScopeType.GROUP, "abcdefg"));
        scopes.add(new StreamScope(ScopeType.PERSONS_FOLLOWED_STREAMS, currentUserAccountId));
        executeWithCachedIdListHelper(scopes);
    }

    /**
     * Helper method to test searching when all activities are to be searched.
     *
     * @param inSearchTerms
     *            the search terms
     * @param inLastSeenActivityId
     *            the last seen activity id
     * @param inExpectedLuceneQuery
     *            the expected lucene query
     * @param inScopes
     *            the scopes
     */
    private void searchContentWithoutListsHelper(final String inSearchTerms, final long inLastSeenActivityId,
            final String inExpectedLuceneQuery, final List<StreamScope> inScopes)
    {
        final long streamViewId = 838L;
        final int pageSize = 22;

        final List<Long> activityIds = new ArrayList<Long>();
        final List<ActivityDTO> results = new ArrayList<ActivityDTO>();

        context.checking(new Expectations()
        {
            {
                oneOf(getStreamScopesByStreamViewIdMapper).execute(streamViewId);
                will(returnValue(inScopes));

                // make sure query was split apart and escaped properly
                allowing(projectionSearchRequestBuilderMock).escapeAllButWildcardCharacters("hello");
                will(returnValue("escapedHello"));
                allowing(projectionSearchRequestBuilderMock).escapeAllButWildcardCharacters("there");
                will(returnValue("escapedThere"));

                // make sure the correct lucene query was created
                oneOf(activityIdSearchPageFetcherFactoryMock).buildActivityIdSearchPageFetcher(inExpectedLuceneQuery,
                        projectionSearchRequestBuilderMock, inLastSeenActivityId);
                will(returnValue(activityIdSearchPageFetcherMock));

                // make sure that the first page was requested
                oneOf(activityIdSearchPageFetcherMock).fetchPage(0, pageSize);
                will(returnValue(activityIds));

                // and that those IDs were passed into the bulk activities
                // mapper
                oneOf(bulkActivitiesMapper).execute(activityIds, currentUserAccountId);
                will(returnValue(results));

                oneOf(getPeopleByAccountIdsMapperMock).fetchId(currentUserAccountId);
                will(returnValue(currentUserId));

                allowing(orgCacheMock).getOrganizationIdFromShortName(organizationAShortName);
                will(returnValue(organizationAId));

                oneOf(buildActivityStreamSearchStringForUserMapper).execute(currentUserId);
                will(returnValue(userScopedActivitiesSearchQuery));
            }
        });

        assertSame(results, sut.execute(new StreamSearchRequest(currentUserAccountId, streamViewId, inSearchTerms,
                pageSize, inLastSeenActivityId)));

        context.assertIsSatisfied();
    }

    /**
     * Helper method for searching against a list.
     *
     * @param inStreamScopes
     *            the scopes to use - should include either STARRED or PERSONS_FOLLOWED_STREAMS to trigger list
     *            searching.
     */
    @SuppressWarnings("unchecked")
    private void executeWithCachedIdListHelper(final List<StreamScope> inStreamScopes)
    {
        final long streamViewId = 382L;
        final String expectedLuceneQuery = "+content:escapedHello +content:escapedThere";
        final int pageSize = 22;

        final List<Long> activityIds = Collections.singletonList(8L);
        final List<ActivityDTO> results = new ArrayList<ActivityDTO>();

        // the page fetcher returned by the factory
        final PageFetcher<Long> activityIdListFetcher = context.mock(PageFetcher.class, "A");

        // the final security scoped page fetcher
        final SearchResultListScoper securityScopingListFetcher = context.mock(SearchResultListScoper.class);

        context.checking(new Expectations()
        {
            {
                // TODO: need person lookup allows?

                oneOf(getStreamScopesByStreamViewIdMapper).execute(streamViewId);
                will(returnValue(inStreamScopes));

                // make sure query was split apart and escaped properly
                oneOf(projectionSearchRequestBuilderMock).escapeAllButWildcardCharacters("hello");
                will(returnValue("escapedHello"));
                oneOf(projectionSearchRequestBuilderMock).escapeAllButWildcardCharacters("there");
                will(returnValue("escapedThere"));

                // make sure the correct lucene query was created
                oneOf(activityIdSearchPageFetcherFactoryMock).buildActivityIdSearchPageFetcher(expectedLuceneQuery,
                        projectionSearchRequestBuilderMock, lastSeenActivityId);
                will(returnValue(activityIdSearchPageFetcherMock));

                // make sure the streamViewActivityIdListPageFetcherFactory was
                // asked for a PageFetcher to get the list
                // ids
                oneOf(streamViewActivityIdListPageFetcherFactoryMock).buildPageFetcher(streamViewId, currentUserId);
                will(returnValue(activityIdListFetcher));

                // make sure search result scoper factory was created, given the
                // search page fetcher
                oneOf(searchResultScoperFactoryMock).buildSearchResultSecurityScoper(activityIdSearchPageFetcherMock,
                        activityIdListFetcher, lastSeenActivityId);
                will(returnValue(securityScopingListFetcher));

                // make sure that the first page was requested
                oneOf(securityScopingListFetcher).fetchPage(0, pageSize);
                will(returnValue(activityIds));

                oneOf(getPeopleByAccountIdsMapperMock).fetchId(currentUserAccountId);
                will(returnValue(currentUserId));

                // make sure the DTO-fetcher was called
                oneOf(bulkActivitiesMapper).execute(activityIds, currentUserAccountId);
                will(returnValue(results));
            }
        });

        assertSame(results, sut.execute(new StreamSearchRequest(currentUserAccountId, streamViewId, searchTerms,
                pageSize, lastSeenActivityId)));

        context.assertIsSatisfied();
    }
}
