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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.search.ProjectionSearchRequestBuilder;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.GetStreamScopesByStreamViewId;
import org.eurekastreams.server.persistence.mappers.ReadMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.cache.OrganizationHierarchyCache;
import org.eurekastreams.server.persistence.mappers.requests.StreamSearchRequest;
import org.eurekastreams.server.persistence.mappers.stream.BuildActivityStreamSearchStringForUser;
import org.eurekastreams.server.persistence.mappers.stream.BulkActivitiesMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Mapper to search for Activities using Lucene, and when necessary, scoping it against a list of ActivityIds.
 */
public class SearchActivitiesMapper extends ReadMapper<StreamSearchRequest, List<ActivityDTO>>
{
    /**
     * The logger.
     */
    private Log log = LogFactory.getLog(SearchActivitiesMapper.class);

    /**
     * Mapper to convert group shortNames to IDs.
     */
    private GetDomainGroupsByShortNames getDomainGroupsByShortNames;

    /**
     * Mapper to convert group account ids to IDs.
     */
    private GetPeopleByAccountIds getPeopleByAccountIdsMapper;

    /**
     * The search request builder.
     */
    private ProjectionSearchRequestBuilder searchRequestBuilder;

    /**
     * The organization hierarchy cache to convert org short name to id.
     */
    private OrganizationHierarchyCache orgCache;

    /**
     * Factory to build the SearchResultSecurityScoper.
     */
    private SearchResultListScoperFactory searchResultScoperFactory;

    /**
     * Factory to return a PageFetcher that returns a list of ActivityIds for a given stream view.
     */
    private StreamViewActivityIdListPageFetcherFactory streamViewActivityIdListPageFetcherFactory;

    /**
     * Mapper to get stream scopes from a stream view id.
     */
    private GetStreamScopesByStreamViewId getStreamScopesByStreamViewIdMapper;

    /**
     * Mapper to get Activities by ID.
     */
    private BulkActivitiesMapper bulkActivitiesMapper;

    /**
     * Factory to build ActivityIdSearchPageFetcher.
     */
    private ActivityIdSearchPageFetcherFactory activityIdSearchPageFetcherFactory;

    /**
     * Mapper to get the scoped search string for searching the everyone list.
     */
    private BuildActivityStreamSearchStringForUser buildActivityStreamSearchStringForUserMapper;

    /**
     * Search a stream by the stream id and search text.
     *
     * @param inRequest
     *            the request, containing the stream id to search and search text
     * @return a list of Messages that match the search request
     */
    @Override
    public List<ActivityDTO> execute(final StreamSearchRequest inRequest)
    {
        PageFetcher<Long> activityIdFetcher = buildActivityIdPageFetcher(inRequest);

        log.info("Fetching a page of activities");
        List<Long> activityIds = activityIdFetcher.fetchPage(0, inRequest.getPageSize());
        log.info("Fetched the page of activity ids from search");

        if (log.isInfoEnabled())
        {
            log.info("Fetching the activities from cache/db with the activity ids: "
                    + (activityIds == null ? "(null)" : activityIds.toString()));
        }
        List<ActivityDTO> activities = bulkActivitiesMapper
                .execute(activityIds, inRequest.getRequestingUserAccountId());

        if (log.isInfoEnabled())
        {
            log.info("Fetched " + (activities == null ? "(null)" : activities.size())
                    + " activities from cache/database.");
        }

        return activities;
    }

    /**
     * Fetch a list of Activity IDs as efficiently as possible.
     *
     * If searching all activities, go straight to Lucene. If the search includes starred activities or those by people
     * the user is following, search all activities, then scope the search against the cached list of activity ids.
     * Else, flatten the search scopes and use Lucene, 100%
     *
     * @param inRequest
     *            the search request
     * @return a Page Fetcher to fetch the list of ActivityIDs
     */
    private PageFetcher<Long> buildActivityIdPageFetcher(final StreamSearchRequest inRequest)
    {
        Long personId = getPeopleByAccountIdsMapper.fetchId(inRequest.getRequestingUserAccountId());

        // get scopes
        List<StreamScope> scopes = getStreamScopesByStreamViewIdMapper.execute(inRequest.getStreamViewId());

        // determine if we can use only Lucene
        String baseSearchText = getKeywordSearchComponent(inRequest.getSearchText(), inRequest
                .getLastSeenStreamItemId());

        if (isSearchingAllActivity(scopes))
        {
            // search all activities - we need to scope this to everything the
            // user is allowed to see
            log.info("Searching all activities - get the security-scoped search for this user.");
            String userScopedSearchString = buildActivityStreamSearchStringForUserMapper.execute(personId);
            log.info("Security-scoped Lucene query component: " + userScopedSearchString);

            String searchString = "+(" + userScopedSearchString + ") " + baseSearchText;

            return activityIdSearchPageFetcherFactory.buildActivityIdSearchPageFetcher(searchString,
                    searchRequestBuilder, inRequest.getLastSeenStreamItemId());
        }
        else if (isSearchingAgainstCachedActivityIdList(scopes))
        {
            // use the base search against the cached list
            log.info("Searching against a cached list of activities.  "
                    + "Building page fetchers for search and cached lists");
            PageFetcher<Long> searchPageFetcher = activityIdSearchPageFetcherFactory.buildActivityIdSearchPageFetcher(
                    baseSearchText, searchRequestBuilder, inRequest.getLastSeenStreamItemId());

            PageFetcher<Long> activityIdListPageFetcher = streamViewActivityIdListPageFetcherFactory.buildPageFetcher(
                    inRequest.getStreamViewId(), personId);

            return searchResultScoperFactory.buildSearchResultSecurityScoper(searchPageFetcher,
                    activityIdListPageFetcher, inRequest.getLastSeenStreamItemId());
        }
        else
        {
            // we're going to search entirely with lucene, so fetch the rest of
            // the query criteria
            log.info("Searching scopes directly within Lucene, so getting the"
                    + " security-scoped search for this user.");
            String userScopedSearchString = buildActivityStreamSearchStringForUserMapper.execute(personId);
            log.info("Security-scoped Lucene query component: " + userScopedSearchString);

            String queryString = "+(" + userScopedSearchString + ") " + baseSearchText
                    + getQueryStringComponentFromCriteria(inRequest.getRequestingUserAccountId(), scopes);

            log.info("Searching with: " + queryString);

            return activityIdSearchPageFetcherFactory.buildActivityIdSearchPageFetcher(queryString,
                    searchRequestBuilder, inRequest.getLastSeenStreamItemId());
        }
    }

    /**
     * Get the first part of the search string with keywords.
     *
     * @param inSearchText
     *            the search text the user entered
     * @param inLastSeenStreamItemId
     *            the last stream id the user saw on the page
     * @return a Lucene query that searches the 'content' field for the search words the user typed in, and a range
     */
    private String getKeywordSearchComponent(final String inSearchText, final long inLastSeenStreamItemId)
    {
        StringBuilder sb = new StringBuilder();
        for (String keyword : inSearchText.trim().split("\\s"))
        {
            if (keyword.length() > 0)
            {
                keyword = searchRequestBuilder.escapeAllButWildcardCharacters(keyword);
                if (keyword.length() > 0)
                {
                    if (keyword.length() > 2 && keyword.startsWith("\\-"))
                    {
                        sb.append(" -content:");
                        sb.append(keyword.substring(2));
                    }
                    else
                    {
                        sb.append(" +content:");
                        sb.append(keyword);
                    }
                }
            }
        }

        return sb.toString().trim();
    }

    /**
     * Check whether we're searching all activity - if so, no additional scoping is required.
     *
     * @param inScopes
     *            the scopes for the search
     * @return true if there are scopes to add to the query and the query doesn't search all activities
     */
    private boolean isSearchingAllActivity(final List<StreamScope> inScopes)
    {
        for (StreamScope scope : inScopes)
        {
            if (scope.getScopeType() == ScopeType.ALL)
            {
                return true;
            }
        }
        return inScopes.size() == 0;
    }

    /**
     * Check whether the input list of scopes is too complicated to search Lucene alone, requiring joining a full search
     * against the cached list of message ids from the specific list.
     *
     * @param inScopes
     *            the scopes of the list to check
     * @return true if we need to scope the search with the list, or false if the query is simple enough to use Lucene
     *         alone
     */
    private boolean isSearchingAgainstCachedActivityIdList(final List<StreamScope> inScopes)
    {
        for (StreamScope scope : inScopes)
        {
            if (scope.getScopeType() == ScopeType.PERSONS_FOLLOWED_STREAMS || scope.getScopeType() == ScopeType.STARRED)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the Lucene query string component from the input list of Scopes.
     *
     * @param inRequestingUserAccountId
     *            the
     * @param inStreamScopes
     *            the stream scopes to use to build the search query
     * @return a Lucene query component that represents the stream scopes
     */
    protected String getQueryStringComponentFromCriteria(final String inRequestingUserAccountId,
            final List<StreamScope> inStreamScopes)
    {
        StringBuilder clause = new StringBuilder();
        Map<String, Long> groupShortNameToIdMap = new HashMap<String, Long>();
        Map<String, PersonModelView> peopleMap = new HashMap<String, PersonModelView>();

        // look up the group IDs from their short names, people ids from their
        // account ids
        loadGroupsAndPeopleFromCache(inRequestingUserAccountId, inStreamScopes, groupShortNameToIdMap, peopleMap);

        for (StreamScope scope : inStreamScopes)
        {
            switch (scope.getScopeType())
            {
            case ORGANIZATION:
                // recipient type is org - convert the org short name to id
                clause.append(" recipientParentOrgId:");
                clause.append(orgCache.getOrganizationIdFromShortName(scope.getUniqueKey()));
                break;

            case PERSON:
                // recipient type is person
                clause.append(" recipient:p");
                clause.append(peopleMap.get(scope.getUniqueKey()).getEntityId());
                break;

            case GROUP:
                clause.append(" recipient:g");
                clause.append(groupShortNameToIdMap.get(scope.getUniqueKey()));
                break;

            case PERSONS_PARENT_ORGANIZATION:
                // use cache to find the person's parent org id from their
                // account id
                clause.append(" recipientParentOrgId:");
                clause.append(peopleMap.get(inRequestingUserAccountId).getParentOrganizationId());
                break;

            default:
                throw new RuntimeException("Invalid scope type.");
            }
        }

        if (clause.length() == 0)
        {
            return "";
        }

        return " +(" + clause.toString().trim() + ")";
    }

    /**
     * Load PersonModelViews and DomainGroup IDs that will be needed from cache.
     *
     * @param inRequestingUserAccountId
     *            the accountId of the user making the request
     * @param inStreamScopes
     *            the stream scopes of the search
     * @param inGroupShortNameToIdMap
     *            the map to store the groupShortName->groupId
     * @param inAccountIdToPersonMap
     *            the map to store the personAccountId->PersonModelView
     */
    private void loadGroupsAndPeopleFromCache(final String inRequestingUserAccountId,
            final List<StreamScope> inStreamScopes, final Map<String, Long> inGroupShortNameToIdMap,
            final Map<String, PersonModelView> inAccountIdToPersonMap)
    {
        // figure out the group and person short names to look up
        List<String> groupShortNames = new ArrayList<String>();
        List<String> personAccountIds = new ArrayList<String>();
        for (StreamScope scope : inStreamScopes)
        {
            switch (scope.getScopeType())
            {
            case GROUP:
                groupShortNames.add(scope.getUniqueKey());
                break;
            case PERSON:
                personAccountIds.add(scope.getUniqueKey());
                break;
            case PERSONS_PARENT_ORGANIZATION:
                personAccountIds.add(inRequestingUserAccountId);
                break;
            default:
                // do nothing - ignore
                break;
            }
        }

        // lookup the group IDs from their short names and populate
        // inGroupShortNameToIdMap
        if (groupShortNames.size() > 0)
        {
            Map<String, Long> groupCacheKeyToIdMap = getDomainGroupsByShortNames.fetchIds(groupShortNames);
            for (String groupKey : groupCacheKeyToIdMap.keySet())
            {
                // remove the cache key prefix from the key
                inGroupShortNameToIdMap.put(groupKey.substring(CacheKeys.GROUP_BY_SHORT_NAME.length()),
                        groupCacheKeyToIdMap.get(groupKey));
            }
        }

        // look up the PersonModelViews from their accountIds
        if (personAccountIds.size() > 0)
        {
            // get the list, then convert to map
            List<PersonModelView> people = getPeopleByAccountIdsMapper.execute(personAccountIds);
            for (PersonModelView person : people)
            {
                inAccountIdToPersonMap.put(person.getAccountId(), person);
            }
        }
    }

    /**
     * Set the organization hierarchy cache.
     *
     * @param inOrgCache
     *            the orgCache to set
     */
    public void setOrgCache(final OrganizationHierarchyCache inOrgCache)
    {
        this.orgCache = inOrgCache;
    }

    /**
     * Set the search request builder.
     *
     * @param inSearchRequestBuilder
     *            the searchRequestBuilder to set
     */
    public void setSearchRequestBuilder(final ProjectionSearchRequestBuilder inSearchRequestBuilder)
    {
        this.searchRequestBuilder = inSearchRequestBuilder;
    }

    /**
     * Set the factory responsible for creating SearchResultScopers.
     *
     * @param inSearchResultScoperFactory
     *            the searchResultScoperFactory to set
     */
    public void setSearchResultScoperFactory(final SearchResultListScoperFactory inSearchResultScoperFactory)
    {
        this.searchResultScoperFactory = inSearchResultScoperFactory;
    }

    /**
     * Set the Factory that builds PageFetchers that fetch ActivityIds for a stream view.
     *
     * @param inStreamViewActivityIdListPageFetcherFactory
     *            the StreamViewActivityIdListPageFetcherFactory to set
     */
    public void setStreamViewActivityIdListPageFetcherFactory(
            final StreamViewActivityIdListPageFetcherFactory inStreamViewActivityIdListPageFetcherFactory)
    {
        this.streamViewActivityIdListPageFetcherFactory = inStreamViewActivityIdListPageFetcherFactory;
    }

    /**
     * Set the mapper to get stream scopes by stream view id.
     *
     * @param inGetStreamScopesByStreamViewIdMapper
     *            the getStreamScopesByStreamViewIdMapper to set
     */
    public void setGetStreamScopesByStreamViewIdMapper(
            final GetStreamScopesByStreamViewId inGetStreamScopesByStreamViewIdMapper)
    {
        this.getStreamScopesByStreamViewIdMapper = inGetStreamScopesByStreamViewIdMapper;
    }

    /**
     * Setter for activityIdSearchPageFetcherFactory.
     *
     * @param inActivityIdSearchPageFetcherFactory
     *            the activityIdSearchPageFetcherFactory to set
     */
    public void setActivityIdSearchPageFetcherFactory(
            final ActivityIdSearchPageFetcherFactory inActivityIdSearchPageFetcherFactory)
    {
        this.activityIdSearchPageFetcherFactory = inActivityIdSearchPageFetcherFactory;
    }

    /**
     * @param inGetDomainGroupsByShortNames
     *            the getDomainGroupsByShortNames to set
     */
    public void setGetDomainGroupsByShortNames(final GetDomainGroupsByShortNames inGetDomainGroupsByShortNames)
    {
        this.getDomainGroupsByShortNames = inGetDomainGroupsByShortNames;
    }

    /**
     * Set the BulkActivitiesMapper.
     *
     * @param inBulkActivitiesMapper
     *            the bulkActivitiesMapper to set
     */
    public void setBulkActivitiesMapper(final BulkActivitiesMapper inBulkActivitiesMapper)
    {
        bulkActivitiesMapper = inBulkActivitiesMapper;
    }

    /**
     * @param inGetPeopleByAccountIdsMapper
     *            the getPeopleByAccountIds to set
     */
    public void setGetPeopleByAccountIdsMapper(final GetPeopleByAccountIds inGetPeopleByAccountIdsMapper)
    {
        this.getPeopleByAccountIdsMapper = inGetPeopleByAccountIdsMapper;
    }

    /**
     * @param inBuildActivityStreamSearchStringForUserMapper
     *            the mapper to get a security-scoped lucene activities search string for a user.
     */
    public void setBuildActivityStreamSearchStringForUserMapper(
            final BuildActivityStreamSearchStringForUser inBuildActivityStreamSearchStringForUserMapper)
    {
        buildActivityStreamSearchStringForUserMapper = inBuildActivityStreamSearchStringForUserMapper;
    }
}
