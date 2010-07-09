/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.actions.strategies.directory;

import java.util.List;

import org.hibernate.search.jpa.FullTextQuery;
import org.eurekastreams.commons.search.ProjectionSearchRequestBuilder;
import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.ResourceSortCriteria;

/**
 * Strategy to get the children of an organization.
 * 
 * @param <T>
 *            the type of ModelView to return in list
 */
public class OrgChildrenGetter<T extends ModelView>
{
    /**
     * SortFieldBuilder.
     */
    private SortFieldBuilder sortFieldBuilder;

    /**
     * The search request builder to use to make all search requests.
     */
    private ProjectionSearchRequestBuilder searchRequestBuilder;

    /**
     * Strategy to build a Lucene query string for searching the directory.
     */
    private DirectorySearchLuceneQueryBuilder queryBuilder;

    /**
     * Whether we're searching recursively down the org tree.
     */
    private boolean isRecursive = false;

    /**
     * Constructor.
     * 
     * @param inQueryBuilder
     *            the strategy to build a Lucene query string for searching the directory
     * @param inSearchRequestBuilder
     *            the search request builder to build our query
     * @param inSortFieldBuilder
     *            the query sort builder
     * @param inIsRecursive
     *            whether we're searching for entities recursively from the org with the input shortName
     */
    public OrgChildrenGetter(final DirectorySearchLuceneQueryBuilder inQueryBuilder,
            final ProjectionSearchRequestBuilder inSearchRequestBuilder, final SortFieldBuilder inSortFieldBuilder,
            final boolean inIsRecursive)
    {
        queryBuilder = inQueryBuilder;
        searchRequestBuilder = inSearchRequestBuilder;
        sortFieldBuilder = inSortFieldBuilder;
        isRecursive = inIsRecursive;
    }

    /**
     * Get the child ModelViews of the organization with the input id. We're formatting the search string with %1 being
     * the organization shortName, %2 being a userId string for DomainGroup follower/coordinators checking. If we pass
     * the org's shortName in the shortName field, the results will be recursive from that org.
     * 
     * @param orgShortName
     *            the shortname or id of the organization to fetch the children for
     * @param from
     *            the starting index to fetch
     * @param to
     *            the ending index to fetch
     * @param sortCriteria
     *            the sort criteria
     * @param userPersonId
     *            the Person.id of the requesting user
     * @return a sorted list of ModelViews
     */
    @SuppressWarnings("unchecked")
    public PagedSet<T> getOrgChildren(final String orgShortName, final int from, final int to,
            final ResourceSortCriteria sortCriteria, final long userPersonId)
    {
        String nativeLuceneQuery;
        if (isRecursive)
        {
            // recursive - pass in the short name, which will return all matching entities below that org
            nativeLuceneQuery = queryBuilder.buildNativeQuery(orgShortName, "", orgShortName, userPersonId);
        }
        else
        {
            // not recursive - don't pass in a short name
            nativeLuceneQuery = queryBuilder.buildNativeQuery(orgShortName, "", "", userPersonId);
        }

        FullTextQuery query = searchRequestBuilder.buildQueryFromNativeSearchString(nativeLuceneQuery);

        query.setSort(sortFieldBuilder.getSort(sortCriteria));
        searchRequestBuilder.setPaging(query, from, to);

        // get the results before query.getResultSize() is called for performance (it avoids a second search)
        List<T> children = (List<T>) query.getResultList();
        
        // return the paged set, getting the total now that we've already made the query
        return new PagedSet<T>(from, to, query.getResultSize(), children);
    }
}
