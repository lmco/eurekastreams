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
package org.eurekastreams.server.action.execution.directory;

import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.search.ProjectionSearchRequestBuilder;
import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.server.action.request.directory.GetDirectorySearchResultsRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.service.actions.strategies.directory.DirectorySearchLuceneQueryBuilder;
import org.eurekastreams.server.service.actions.strategies.directory.TransientPropertyPopulator;
import org.hibernate.search.jpa.FullTextQuery;

/**
 * Action to search across People, DomainGroups, and Organizations.
 */
public class GetDirectorySearchResultsExecution implements ExecutionStrategy<ServiceActionContext>
{
    /**
     * Instance of the logger.
     */
    private Log log = LogFactory.make();

    /**
     * The search request builder.
     */
    private ProjectionSearchRequestBuilder searchRequestBuilder;

    /**
     * The transient property populator for search results.
     */
    private TransientPropertyPopulator transientPropertyPopulator;

    /**
     * Strategy to build a Lucene query string for searching the directory.
     */
    private DirectorySearchLuceneQueryBuilder queryBuilder;

    /**
     * Constructor.
     *
     * @param inQueryBuilder
     *            the strategy to build a Lucene query string for searching the directory
     * @param inSearchRequestBuilder
     *            the search request builder
     * @param inTransientPropertyPopulator
     *            the strategy to populate additional properties on the search results as they return
     */
    public GetDirectorySearchResultsExecution(final DirectorySearchLuceneQueryBuilder inQueryBuilder,
            final ProjectionSearchRequestBuilder inSearchRequestBuilder,
            final TransientPropertyPopulator inTransientPropertyPopulator)
    {
        queryBuilder = inQueryBuilder;
        searchRequestBuilder = inSearchRequestBuilder;
        transientPropertyPopulator = inTransientPropertyPopulator;
    }

    @SuppressWarnings("unchecked")
    @Override
    public PagedSet<ModelView> execute(final ServiceActionContext inActionContext) throws ExecutionException
    {
        long startTime = System.currentTimeMillis();

        GetDirectorySearchResultsRequest currentRequest = (GetDirectorySearchResultsRequest) inActionContext
                .getParams();
        // todo: do more than escape here - remove the advanced chars
        String searchText = searchRequestBuilder.escapeAllButWildcardCharacters(currentRequest.getSearchTerm());

        // get the current user's Person id.
        long userPersonId = inActionContext.getPrincipal().getId();

        // build and parse the query, set its paging
        String nativeLuceneQuery = queryBuilder.buildNativeQuery(searchText, currentRequest.getWeightedField(),
                currentRequest.getOrgShortName(), userPersonId);
        FullTextQuery query = searchRequestBuilder.buildQueryFromNativeSearchString(nativeLuceneQuery);
        searchRequestBuilder.setPaging(query, currentRequest.getStartIndex(), currentRequest.getEndIndex());

        // get the results before query.getResultSize() is called for performance (it avoids a second search)
        List<ModelView> results = query.getResultList();

        // populate any transient properties
        transientPropertyPopulator.populateTransientProperties(results, userPersonId, searchText);

        // get the paged set, getting the total now that we've already made the query
        PagedSet<ModelView> pagedResults = new PagedSet<ModelView>(currentRequest.getStartIndex(), currentRequest
                .getEndIndex(), query.getResultSize(), results);

        // set the elapsed time
        String elapsedTime = formatElapasedTime(startTime, System.currentTimeMillis());
        pagedResults.setElapsedTime(elapsedTime);

        log.info("Searched '" + searchText + "' in " + elapsedTime);
        return pagedResults;
    }

    /**
     * Determine and format the elapsed time for a server request.
     *
     * @param startTime
     *            the starting milliseconds
     * @param endTime
     *            the ending milliseconds
     * @return a formatted elapsed time in the format "0.23 seconds"
     */
    protected String formatElapasedTime(final long startTime, final long endTime)
    {
        final int millisecondsPerSecond = 1000;
        String elapsedTime = String.format("%1$.2f seconds", (endTime - startTime) / (float) millisecondsPerSecond);
        if (elapsedTime == "0.00 seconds")
        {
            elapsedTime = "0.01 seconds";
        }
        return elapsedTime;
    }
}
