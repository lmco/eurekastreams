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

import java.io.Serializable;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.eurekastreams.server.search.bridge.ActivitySourceClassBridge;

/**
 * Transforms a JSON request into a list of app identifiers (type + id) for querying (space separated list of ids
 * prefixed with the type code).
 */
public class AppSourcePersistenceRequestTransformer implements PersistenceDataSourceRequestTransformer
{
    /** Pattern to validate ids to protect against Lucene query injection. */
    private static final Pattern APP_ID_REGEX = Pattern.compile("^\\*|\\d+$");

    /**
     * Transforms a JSON request into a query term.
     *
     * @param request
     *            the request.
     * @param userEntityId
     *            the user entity id.
     * @return the transformed request.
     */
    @Override
    public Serializable transform(final JSONObject request, final Long userEntityId)
    {
        JSONArray apps = request.getJSONArray("fromApp");

        StringBuilder queryTerm = new StringBuilder();

        for (int i = 0; i < apps.size(); i++)
        {
            if (i > 0)
            {
                queryTerm.append(" ");
            }

            JSONObject app = apps.getJSONObject(i);
            char typePrefix = getTypePrefix(app);
            queryTerm.append(typePrefix);

            String appId = app.getString("name");
            if (!APP_ID_REGEX.matcher(appId).matches())
            {
                throw new IllegalArgumentException("Invalid app id.");
            }
            queryTerm.append(appId);
        }
        return queryTerm.toString();
    }

    /**
     * Returns a valid app type prefix for the given app entry.
     *
     * @param entry
     *            JSON app entry.
     * @return Type prefix.
     */
    private char getTypePrefix(final JSONObject entry)
    {
        String appType = entry.getString("type");
        if ("app".equals(appType))
        {
            return ActivitySourceClassBridge.APPLICATION_PREFIX;
        }
        else if ("plugin".equals(appType))
        {
            return ActivitySourceClassBridge.PLUGIN_PREFIX;
        }
        else
        {
            throw new IllegalArgumentException("Invalid appType.");
        }
    }
}
