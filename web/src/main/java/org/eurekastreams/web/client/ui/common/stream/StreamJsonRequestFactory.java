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
package org.eurekastreams.web.client.ui.common.stream;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;

/**
 * Constructs a stream JSON request.
 */
public final class StreamJsonRequestFactory
{
    /**
     * Recipient key.
     */
    private static final String RECIPIENT_KEY = "recipient";

    /**
     * Search key.
     */
    private static final String SEARCH_KEY = "keywords";

    /**
     * Organization key.
     */
    private static final String ORGANIZATION_KEY = "organization";

    /**
     * Recipient type key.
     */
    private static final String RECIPIENT_TYPE_KEY = "type";

    /**
     * Recipient unique ID key.
     */
    private static final String RECIPIENT_UNIQUE_ID_KEY = "name";

    /**
     * Sort key.
     */
    private static final String SORT_KEY = "sortBy";

    /**
     * Sort key.
     */
    private static final String FOLLOWED_BY_KEY = "followedBy";

    /**
     * Sort key.
     */
    private static final String PARENT_ORG_KEY = "parentOrg";

    /**
     * Sort key.
     */
    private static final String SAVED_KEY = "savedBy";

    /**
     * Min ID key.
     */
    private static final String MIN_ID_KEY = "minId";

    /**
     * Max ID key.
     */
    private static final String MAX_ID_KEY = "maxId";

    /**
     * Max results key.
     */
    private static final String MAX_RESULTS_KEY = "count";

    /**
     * Sort types.
     */
    public enum SortType
    {
        /**
         * Sort by date.
         */
        DATE
        {
            /**
             * @return the value as a string.
             */
            @Override
            public String toString()
            {
                return "date";
            }
        }
    }

    /**
     * Gets an empty request. Used for everyone stream.
     *
     * @return new empty JSON request.
     */
    public static JSONObject getEmptyRequest()
    {
        return getJSONRequest("{ query : {} }");
    }

    /**
     * Get a JSON object from a String.
     *
     * @param request
     *            the request.
     * @return the JSON object.
     */
    public static JSONObject getJSONRequest(final String request)
    {
        return JSONParser.parse(request).isObject();
    }

    /**
     * Adds a recipient to a request.
     *
     * @param type
     *            the type of recepient.
     * @param uniqueId
     *            the unique ID of the recipient.
     * @param json
     *            the request.
     * @return the modified request.
     */
    public static JSONObject addRecipient(final EntityType type, final String uniqueId, final JSONObject json)
    {
        JSONObject query = json.get("query").isObject();

        JSONObject recipient = new JSONObject();
        recipient.put(RECIPIENT_TYPE_KEY, new JSONString(type.toString()));
        recipient.put(RECIPIENT_UNIQUE_ID_KEY, new JSONString(uniqueId));

        JSONArray recipientArray = null;

        if (query.containsKey(RECIPIENT_KEY))
        {
            recipientArray = query.get(RECIPIENT_KEY).isArray();
        }
        else
        {
            recipientArray = new JSONArray();
            query.put(RECIPIENT_KEY, recipientArray);
        }

        recipientArray.set(recipientArray.size(), recipient);

        return json;
    }

    /**
     * Set the org stream in the request.
     *
     * @param orgShortName
     *            the org short name.
     * @param json
     *            the json request.
     * @return the modified request.
     */
    public static JSONObject setOrganization(final String orgShortName, final JSONObject json)
    {
        JSONObject query = json.get("query").isObject();

        query.put(ORGANIZATION_KEY, new JSONString(orgShortName));

        return json;
    }

    /**
     * Sets the search term in a request..
     *
     * @param searchText
     *            the search text.
     * @param json
     *            the request.
     * @return the request.
     */
    public static JSONObject setSearchTerm(final String searchText, final JSONObject json)
    {
        JSONObject query = json.get("query").isObject();

        query.put(SEARCH_KEY, new JSONString(searchText));

        return json;
    }

    /**
     * Sets the sorting of a request.
     *
     * @param sortBy
     *            the type of sort.
     * @param json
     *            the request.
     * @return the request.
     */
    public static JSONObject setSort(final SortType sortBy, final JSONObject json)
    {
        JSONObject query = json.get("query").isObject();

        query.put(SORT_KEY, new JSONString(sortBy.toString()));

        return json;
    }


    /**
     * Sets the source as the current user's parent org.
     * @param json the json.
     * @return the json.
     */
    public static JSONObject setSourceAsParentOrg(final JSONObject json)
    {
        JSONObject query = json.get("query").isObject();
        query.put(PARENT_ORG_KEY, new JSONString(Session.getInstance().getCurrentPerson().getAccountId()));
        return json;
    }

    /**
     * Sets the source as the current user's saved..
     * @param json the json.
     * @return the json.
     */
    public static JSONObject setSourceAsSaved(final JSONObject json)
    {
        JSONObject query = json.get("query").isObject();
        query.put(SAVED_KEY, new JSONString(Session.getInstance().getCurrentPerson().getAccountId()));
        return json;
    }

    /**
     * Sets the source as the current user's following.
     * @param json the json.
     * @return the json.
     */
    public static JSONObject setSourceAsFollowing(final JSONObject json)
    {
        JSONObject query = json.get("query").isObject();
        query.put(FOLLOWED_BY_KEY, new JSONString(Session.getInstance().getCurrentPerson().getAccountId()));
        return json;
    }
    
    /**
     * Sets the min ID of a request.
     *
     * @param minId
     *            the min ID.
     * @param json
     *            the request.
     * @return the request.
     */
    public static JSONObject setMinId(final Long minId, final JSONObject json)
    {
        json.put(MIN_ID_KEY, new JSONString(minId.toString()));

        return json;
    }

    /**
     * Sets the max ID of a request.
     *
     * @param maxId
     *            the max ID.
     * @param json
     *            the request.
     * @return the request.
     */
    public static JSONObject setMaxId(final Long maxId, final JSONObject json)
    {
        json.put(MAX_ID_KEY, new JSONString(maxId.toString()));

        return json;
    }
    
    /**
     * Sets the max number of results for the request.
     *
     * @param maxResults
     *            the max results.
     * @param json
     *            the request.
     * @return the request.
     */
    public static JSONObject setMaxResults(final Integer maxResults, final JSONObject json)
    {
        json.put(MAX_RESULTS_KEY, new JSONString(maxResults.toString()));

        return json;
    }

    /**
     * Constructor.
     */
    private StreamJsonRequestFactory()
    {
    }
}
