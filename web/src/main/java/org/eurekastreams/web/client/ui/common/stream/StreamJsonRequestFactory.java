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

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

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
    public static JSONValue addRecipient(final EntityType type, final String uniqueId, final JSONObject json)
    {
        JSONObject query = json.get("query").isObject();

        JSONObject recipient = new JSONObject();
        recipient.put(RECIPIENT_TYPE_KEY, new JSONString(type.toString()));
        recipient.put(RECIPIENT_UNIQUE_ID_KEY, new JSONString(uniqueId));

        JSONArray recipientArray = null;

        if (json.containsKey(RECIPIENT_KEY))
        {
            recipientArray = json.get(RECIPIENT_KEY).isArray();
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
    public static JSONValue setOrganization(final String orgShortName, final JSONObject json)
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
     * Sets the sorting of a request..
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
     * Constructor.
     */
    private StreamJsonRequestFactory()
    {
    }
}
