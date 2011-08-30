/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.search.ProjectionSearchRequestBuilder;
import org.eurekastreams.server.domain.stream.Activity;
import org.hibernate.search.jpa.FullTextQuery;

/**
 * Gets activity IDs from Lucene based on the query.
 */
public class LuceneDataSource implements SortedDataSource
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Search request builder.
     */
    private final ProjectionSearchRequestBuilder searchRequestBuilder;

    /**
     * Unstemmed request builder.
     */
    private final ProjectionSearchRequestBuilder unstemmedRequestBuilder;

    /**
     * Translates the request to search fields.
     */
    private final Map<String, String> requestToField;

    /**
     * Transformers.
     */
    private final Map<String, PersistenceDataSourceRequestTransformer> transformers;

    /**
     * Max allowed results.
     */
    private int maxResults = 0;

    /**
     * Constructor.
     *
     * @param inSearchRequestBuilder
     *            the search request builder
     * @param inUnstemmedRequestBuilder
     *            the unstemmed request builder.
     * @param inRequestToField
     *            maps requests to Lucene fields.
     * @param inTransformers
     *            the transformers (more than meets the eye).
     * @param inMaxResults
     *            max results.
     */
    public LuceneDataSource(final ProjectionSearchRequestBuilder inSearchRequestBuilder,
            final ProjectionSearchRequestBuilder inUnstemmedRequestBuilder,
            final Map<String, String> inRequestToField,
            final Map<String, PersistenceDataSourceRequestTransformer> inTransformers, final int inMaxResults)
    {
        searchRequestBuilder = inSearchRequestBuilder;
        unstemmedRequestBuilder = inUnstemmedRequestBuilder;
        requestToField = inRequestToField;
        transformers = inTransformers;
        maxResults = inMaxResults;
    }

    /**
     * Fetch a page of search results, using the keywords in the input JSON request.
     *
     * @param inRequest
     *            the JSON request containing query->keywords
     * @param userEntityId
     *            the user entity ID.
     * @return the activity ids
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Long> fetch(final JSONObject inRequest, final Long userEntityId)
    {
        JSONObject jsonQuery = inRequest.getJSONObject("query");

        Boolean hasTerm = false;

        for (String term : requestToField.keySet())
        {
            if (jsonQuery.containsKey(term))
            {
                hasTerm = true;
                break;
            }
        }

        // As an optimizing, the LuceneDataSource is not execute if there is not search term, and
        // the sort is by date, since it can just fall back to memcached.
        if (!hasTerm
                && (!jsonQuery.containsKey("sortBy") || (jsonQuery.containsKey("sortBy") && jsonQuery.getString(
                        "sortBy").equals("date"))))
        {
            log.debug("No search term found");
            return null;
        }

        String luceneQuery = "";
        FullTextQuery query = null;

        for (Entry<String, String> entry : requestToField.entrySet())
        {
            String req = entry.getKey();

            if (jsonQuery.containsKey(req))
            {
                String searchWord = jsonQuery.getString(req);
                // remove colons, which can be used to search other fields
                searchWord = searchWord.replace(":", "");

                if (transformers.containsKey(req))
                {
                    searchWord = (String) transformers.get(req).transform(jsonQuery, userEntityId);
                }

                // if this is activity content - keep track of it for special content-only handling
                if (req.toLowerCase().equals("keywords"))
                {
                    if (searchWord.contains("NOT ") || searchWord.contains("!") || searchWord.contains("-"))
                    {
                        // searching content with a NOT component
                        log.info("User is querying for activity content with:(" + searchWord
                                + ") and seems to be using a NOT/!/- component.  Lucene doesn't allow "
                                + "NOT queries with one component, so I'll add a constant keyword that "
                                + "I know is present: " + Activity.CONSTANT_KEYWORD_IN_EVERY_ACTIVITY_CONTENT);

                        luceneQuery += "+" + entry.getValue() + ":("
                                + Activity.CONSTANT_KEYWORD_IN_EVERY_ACTIVITY_CONTENT + " " + searchWord + ") ";
                    }
                    else
                    {
                        // searching content without NOT component
                        luceneQuery += "+" + entry.getValue() + ":(" + searchWord + ") ";
                    }
                }
                else
                {
                    // searching non-content
                    luceneQuery += "+" + entry.getValue() + ":(" + searchWord + ") ";
                }
            }
        }

        if (luceneQuery.length() == 0)
        {
            log.debug("Returning all activity");
            query = unstemmedRequestBuilder.buildQueryFromNativeSearchString("_hibernate_class:"
                    + "org.eurekastreams.server.domain.stream.Activity");
        }
        else
        {
            // don't let query parsing throw an exception that bubbles out to the client - it could be from an
            // incomplete as-you-type search
            try
            {
                query = searchRequestBuilder.buildQueryFromNativeSearchString(luceneQuery);
            }
            catch (Exception ex)
            {
                return new ArrayList<Long>();
            }
        }

        if (jsonQuery.containsKey("sortBy"))
        {
            query.setSort(new Sort(new SortField(jsonQuery.getString("sortBy"), true)));
        }

        if (log.isDebugEnabled())
        {
            log.debug("Native Lucene Query: " + query.toString());
        }

        searchRequestBuilder.setPaging(query, 0, maxResults);

        List<Long> activityIds = query.getResultList();
        if (log.isInfoEnabled())
        {
            log.info("Found " + activityIds.size() + " activities");
        }
        return activityIds;
    }
}
