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
package org.eurekastreams.server.service.actions.strategies.activity.datasources;

import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.search.ProjectionSearchRequestBuilder;
import org.hibernate.search.jpa.FullTextQuery;

/**
 * Gets activity IDs from Lucene based on the query.
 */
public class LuceneDataSource implements SortedDataSource
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Search request builder.
     */
    private ProjectionSearchRequestBuilder searchRequestBuilder;

    /**
     * Constructor.
     *
     * @param inSearchRequestBuilder
     *            the search request builder
     */
    public LuceneDataSource(final ProjectionSearchRequestBuilder inSearchRequestBuilder)
    {
        searchRequestBuilder = inSearchRequestBuilder;
    }

    /**
     * Fetch a page of search results, using the keywords in the input JSON request.
     *
     * @param inRequest
     *            the JSON request containing query->keywords
     * @return the activity ids
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Long> fetch(final JSONObject inRequest)
    {
        if (!inRequest.getJSONObject("query").containsKey("keywords"))
        {
            log.info("No search term found");
            return null;
        }

        String luceneQuery = inRequest.getJSONObject("query").getString("keywords");

        // remove semicolons, which can be used to search other fields
        luceneQuery = luceneQuery.replace(":", "");

        log.info("Lucene keywords: " + luceneQuery);

        FullTextQuery query = searchRequestBuilder.buildQueryFromNativeSearchString(luceneQuery);
        searchRequestBuilder.setPaging(query, 0, inRequest.getInt("count"));

        List<Long> activityIds = query.getResultList();
        if (log.isInfoEnabled())
        {
            log.info("Found " + activityIds.size() + " activities");
        }
        return activityIds;
    }
}
