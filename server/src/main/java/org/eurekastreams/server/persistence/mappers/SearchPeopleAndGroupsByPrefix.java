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
package org.eurekastreams.server.persistence.mappers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.search.ProjectionSearchRequestBuilder;
import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.cache.GetPrivateCoordinatedAndFollowedGroupIdsForUser;
import org.eurekastreams.server.persistence.mappers.requests.GetEntitiesByPrefixRequest;
import org.eurekastreams.server.search.modelview.DisplayEntityModelView;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.hibernate.search.jpa.FullTextQuery;

/**
 * Search for stream-postable people and groups by prefix, using Hibernate Search.
 */
public class SearchPeopleAndGroupsByPrefix extends ReadMapper<GetEntitiesByPrefixRequest, List<DisplayEntityModelView>>
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.getLog(SearchPeopleAndGroupsByPrefix.class);

    /**
     * default max results from query.
     */
    private final Integer maxResults;

    /**
     * Search request builder.
     */
    private final ProjectionSearchRequestBuilder searchRequestBuilder;

    /**
     * Mapper to get a list of all group ids that aren't public that a user can see activity for.
     */
    private final GetPrivateCoordinatedAndFollowedGroupIdsForUser getGroupIdsMapper;

    /**
     * Mapper used to translate user accountId to DB id.
     */
    private final DomainMapper<String, Long> getPersonIdByAccountIdMapper;

    /**
     * Flag for excluding entities with read-only streams from results.
     */
    private boolean excludeReadOnlyStreams;

    /** Regex to match characters which need to be escaped for Lucene. */
    private static final String LUCENE_ESCAPE_REGEX_STRING = // \n
    "[\\\\\\+\\-\\!\\(\\)\\:\\^\\[\\]\\\"\\{\\}\\~\\*\\?\\|\\&]";

    /** Compiled regex to match characters which need to be escaped for Lucene. */
    private static final Pattern LUCENE_ESCAPE_REGEX = Pattern.compile(LUCENE_ESCAPE_REGEX_STRING);

    /**
     * Constructor.
     * 
     * @param inMaxResults
     *            the max number of results to return
     * @param inSearchRequestBuilder
     *            the search request builder
     * @param inGetGroupIdsMapper
     *            Mapper to get groups user has access to.
     * @param inGetPersonIdByAccountIdMapper
     *            Mapper used to translate user accountId to DB id.
     * @param inExcludeReadOnlyStreams
     *            Flag for excluding entities with read-only streams from results.
     */
    public SearchPeopleAndGroupsByPrefix(final Integer inMaxResults,
            final ProjectionSearchRequestBuilder inSearchRequestBuilder,
            final GetPrivateCoordinatedAndFollowedGroupIdsForUser inGetGroupIdsMapper,
            final DomainMapper<String, Long> inGetPersonIdByAccountIdMapper, final boolean inExcludeReadOnlyStreams)
    {
        maxResults = inMaxResults;
        searchRequestBuilder = inSearchRequestBuilder;
        getGroupIdsMapper = inGetGroupIdsMapper;
        getPersonIdByAccountIdMapper = inGetPersonIdByAccountIdMapper;
        excludeReadOnlyStreams = inExcludeReadOnlyStreams;
    }

    /**
     * Search for people and groups by prefix.
     * 
     * @param inRequest
     *            The request object containing parameters for search.
     * @return List of DisplayEntityModelView representing people/groups matching search criteria.
     */
    @Override
    public List<DisplayEntityModelView> execute(final GetEntitiesByPrefixRequest inRequest)
    {
        // build a search string that includes all of the fields for both people
        // and groups.
        // - people: firstName, lastName, preferredName
        // - group: name
        // - both: isStreamPostable, isPublic
        // Due to text stemming, we need to search with and without the wildcard
        String term = escapeSearchTerm(inRequest.getPrefix());
        String excludeReadOnlyClause = excludeReadOnlyStreams ? "+isStreamPostable:true" : "";
        String searchText = String.format("+(name:(%1$s* %1$s) lastName:(%1$s* %1$s) preferredName:(%1$s* %1$s)^0.5) "
                + "%2$s %3$s", term, excludeReadOnlyClause, getGroupVisibilityClause(inRequest));

        if (log.isTraceEnabled())
        {
            log.trace("Searching for " + maxResults + " people and groups with Lucene query: " + searchText);
        }

        FullTextQuery query = searchRequestBuilder.buildQueryFromNativeSearchString(searchText);

        searchRequestBuilder.setPaging(query, 0, maxResults);

        // get the model views (via the injected cache transformer)
        List<ModelView> searchResults = query.getResultList();

        if (log.isTraceEnabled())
        {
            log.trace("Found " + searchResults.size() + " search results");
        }

        // transform the list to DisplayEntityModelView
        List<DisplayEntityModelView> displayModelViews = new ArrayList<DisplayEntityModelView>();
        for (ModelView modelView : searchResults)
        {
            DisplayEntityModelView displayModelView = new DisplayEntityModelView();
            if (modelView instanceof PersonModelView)
            {
                PersonModelView person = (PersonModelView) modelView;

                if (log.isTraceEnabled())
                {
                    log.trace("Found person '" + person.getAccountId() + " with search prefix '" + searchText + "'");
                }

                displayModelView.setDisplayName(person.getDisplayName());
                displayModelView.setStreamScopeId(person.getStreamId());
                displayModelView.setType(EntityType.PERSON);
                displayModelView.setUniqueKey(person.getAccountId());
                displayModelView.setAccountLocked(person.isAccountLocked());
                displayModelViews.add(displayModelView);
            }
            else if (modelView instanceof DomainGroupModelView)
            {
                DomainGroupModelView group = (DomainGroupModelView) modelView;

                if (log.isTraceEnabled())
                {
                    log.trace("Found domain group '" + group.getShortName() + " with search prefix '" + searchText
                            + "'");
                }

                displayModelView.setDisplayName(group.getName());
                displayModelView.setStreamScopeId(group.getStreamId());
                displayModelView.setType(EntityType.GROUP);
                displayModelView.setUniqueKey(group.getShortName());
                displayModelViews.add(displayModelView);
            }
        }

        return displayModelViews;
    }

    /**
     * Returns search clause used to sort out groups user doesn't have access to.
     * 
     * @param inRequest
     *            The search parameters
     * @return Search clause used to sort out groups user doesn't have access to.
     */
    private String getGroupVisibilityClause(final GetEntitiesByPrefixRequest inRequest)
    {
        // get user id from userKey passed from client.
        Long userId = getPersonIdByAccountIdMapper.execute(inRequest.getUserKey());

        StringBuffer result = new StringBuffer("+(isPublic:true ");

        // get all the group ids followed or coordinated by current user.
        Set<Long> groupIds = getGroupIdsMapper.execute(userId);

        // If group list is greater than zero, include private group visibility clause.
        if (groupIds.size() != 0)
        {
            result.append("( +id:(");
            for (Long id : groupIds)
            {
                result.append(id + " ");
            }
            // TODO: this "-isPublic:true" is because text stemmer turns "false" into "fals"
            // and the query fails. Investigate Lucene API to see how to do this via object
            // model rather than query string generation to get around this.
            result.append(") -isPublic:true)");
        }
        result.append(")");

        return result.toString();
    }

    /**
     * Escapes a search term for Lucene.
     * 
     * @param term
     *            Term to escape.
     * @return Escaped term.
     */
    private String escapeSearchTerm(final String term)
    {
        return LUCENE_ESCAPE_REGEX.matcher(term).replaceAll("\\\\$0");
    }
}
