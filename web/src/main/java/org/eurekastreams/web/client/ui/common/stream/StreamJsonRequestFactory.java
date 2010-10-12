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
    public static final String RECIPIENT_KEY = "recipient";

    /**
     * Search key.
     */
    public static final String SEARCH_KEY = "keywords";

    /**
     * Organization key.
     */
    public static final String ORGANIZATION_KEY = "organization";

    /**
     * Recipient type key.
     */
    public static final String ENTITY_TYPE_KEY = "type";

    /**
     * Recipient unique ID key.
     */
    public static final String ENTITY_UNIQUE_ID_KEY = "name";

    /**
     * Sort key.
     */
    public static final String SORT_KEY = "sortBy";

    /**
     * Sort key.
     */
    public static final String FOLLOWED_BY_KEY = "followedBy";

    /**
     * Sort key.
     */
    public static final String PARENT_ORG_KEY = "parentOrg";

    /**
     * Sort key.
     */
    public static final String SAVED_KEY = "savedBy";

    /**
     * Min ID key.
     */
    public static final String MIN_ID_KEY = "minId";

    /**
     * Max ID key.
     */
    public static final String MAX_ID_KEY = "maxId";

    /**
     * Max results key.
     */
    public static final String MAX_RESULTS_KEY = "count";

    /**
     * Authored by key.
     */
    public static final String AUTHOR_KEY = "authoredBy";

    /**
     * Liked By Key.
     */
    public static final String LIKER_KEY = "likedBy";

    /**
     * Joined groups key.
     */
    public static final String JOINED_GROUPS_KEY = "joinedGroups";

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
     *            the type of recipient.
     * @param uniqueId
     *            the unique ID of the recipient.
     * @param json
     *            the request.
     * @return the modified request.
     */
    public static JSONObject addRecipient(final EntityType type, final String uniqueId, final JSONObject json)
    {
        return addEntity(RECIPIENT_KEY, type, uniqueId, json);
    }

    /**
     * Adds a recipient to a request.
     * 
     * @param type
     *            the type of author.
     * @param uniqueId
     *            the unique ID of the author.
     * @param json
     *            the request.
     * @return the modified request.
     */
    public static JSONObject addAuthor(final EntityType type, final String uniqueId, final JSONObject json)
    {
        return addEntity(AUTHOR_KEY, type, uniqueId, json);
    }

    /**
     * Adds a liker to a request.
     * 
     * @param type
     *            the type of liker.
     * @param uniqueId
     *            the unique ID of the liker.
     * @param json
     *            the request.
     * @return the modified request.
     */
    public static JSONObject addLiker(final EntityType type, final String uniqueId, final JSONObject json)
    {
        return addEntity(LIKER_KEY, type, uniqueId, json);
    }

    /**
     * Init the recipient array.
     * 
     * @param json
     *            the JSON object.
     */
    public static void initRecipient(final JSONObject json)
    {
        initEntity(RECIPIENT_KEY, json);
    }

    /**
     * Init the likers array.
     * 
     * @param json
     *            the JSON object.
     */
    public static void initLikers(final JSONObject json)
    {
        initEntity(LIKER_KEY, json);
    }

    /**
     * Init the authors array.
     * 
     * @param json
     *            the JSON object.
     */
    public static void initAuthors(final JSONObject json)
    {
        initEntity(AUTHOR_KEY, json);
    }

    /**
     * Init an entity array.
     * 
     * @param key
     *  jj          the key.
     * @param json
     *            the JSON object.
     */
    private static void initEntity(final String key, final JSONObject json)
    {
        JSONObject query = json.get("query").isObject();

        if (!query.containsKey(key))
        {
            JSONArray entityArray = new JSONArray();
            query.put(key, entityArray);
        }
    }

    /**
     * Adds an entity to a request.
     * 
     * @param key
     *            the JSON key.
     * @param type
     *            the type of entity.
     * @param uniqueId
     *            the unique ID of the entity.
     * @param json
     *            the request.
     * @return the modified request.
     */
    private static JSONObject addEntity(final String key, final EntityType type, final String uniqueId,
            final JSONObject json)
    {
        JSONObject query = json.get("query").isObject();

        JSONObject entity = new JSONObject();
        entity.put(ENTITY_TYPE_KEY, new JSONString(type.toString()));
        entity.put(ENTITY_UNIQUE_ID_KEY, new JSONString(uniqueId));

        JSONArray entityArray = null;

        if (query.containsKey(key))
        {
            entityArray = query.get(key).isArray();
        }
        else
        {
            entityArray = new JSONArray();
            query.put(key, entityArray);
        }

        entityArray.set(entityArray.size(), entity);

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
    public static JSONObject setSort(final String sortBy, final JSONObject json)
    {
        JSONObject query = json.get("query").isObject();

        query.put(SORT_KEY, new JSONString(sortBy));

        return json;
    }

    /**
     * Sets the source as the current user's parent org.
     * 
     * @param json
     *            the json.
     * @return the json.
     */
    public static JSONObject setSourceAsParentOrg(final JSONObject json)
    {
        JSONObject query = json.get("query").isObject();
        query.put(PARENT_ORG_KEY, new JSONString(Session.getInstance().getCurrentPerson().getAccountId()));
        return json;
    }

    /**
     * Sets the source as the current user's joined groups..
     * 
     * @param json
     *            the json.
     * @return the json.
     */
    public static JSONObject setSourceAsJoinedGroups(final JSONObject json)
    {
        JSONObject query = json.get("query").isObject();
        query.put(JOINED_GROUPS_KEY, new JSONString(Session.getInstance().getCurrentPerson().getAccountId()));
        return json;
    }

    /**
     * Sets the source as the current user's saved..
     * 
     * @param json
     *            the json.
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
     * 
     * @param json
     *            the json.
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
