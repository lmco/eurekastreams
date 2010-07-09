/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.utility;

import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;

/**
 * Base class for building links to activities.
 */
public abstract class BaseActivityLinkBuilder
{
    /** Stored extra parameters. */
    private Map<String, String> storedExtraParameters;

    /**
     * Build a permalink for an activity.
     *
     * @param activityId
     *            Activity ID.
     * @param streamType
     *            Type of entity whose stream the activity is in.
     * @param streamUniqueId
     *            Unique id of entity whose stream the activity is in.
     * @return Permalink URL token.
     */
    public String buildActivityPermalink(final long activityId, final EntityType streamType,
            final String streamUniqueId)
    {
        return Session.getInstance().generateUrl(
                buildActivityPermalinkUrlRequest(activityId, streamType, streamUniqueId, null));
    }

    /**
     * Build a permalink for an activity.
     *
     * @param activityId
     *            Activity ID.
     * @param streamType
     *            Type of entity whose stream the activity is in.
     * @param streamUniqueId
     *            Unique id of entity whose stream the activity is in.
     * @param extraParameters
     *            Additional parameters to include in the URL.
     * @return Permalink URL token.
     */
    public String buildActivityPermalink(final long activityId, final EntityType streamType,
            final String streamUniqueId, final Map<String, String> extraParameters)
    {
        return Session.getInstance().generateUrl(
                buildActivityPermalinkUrlRequest(activityId, streamType, streamUniqueId, extraParameters));
    }


    /**
     * Build the URL request for a permalink for an activity.
     *
     * @param activityId
     *            Activity ID.
     * @param streamType
     *            Type of entity whose stream the activity is in.
     * @param streamUniqueId
     *            Unique id of entity whose stream the activity is in.
     * @param extraParameters
     *            Additional parameters to include in the URL.
     * @return Permalink URL request.
     */
    public abstract CreateUrlRequest buildActivityPermalinkUrlRequest(final long activityId,
            final EntityType streamType, final String streamUniqueId, final Map<String, String> extraParameters);

    /**
     * Builds a map of parameters, merging all supplied extra parameters.
     *
     * @param extraParameters
     *            Extra parameters supplied from call.
     * @return Parameters map.
     */
    protected Map<String, String> buildParameters(final Map<String, String> extraParameters)
    {
        Map<String, String> parameters = new HashMap<String, String>();
        if (storedExtraParameters != null)
        {
            parameters.putAll(storedExtraParameters);
        }
        if (extraParameters != null)
        {
            parameters.putAll(extraParameters);
        }
        return parameters;
    }

    /**
     * Stores a parameter for use in building links.
     * 
     * @param key
     *            Key.
     * @param value
     *            Value (will be converted to string).
     */
    public void addExtraParameter(final String key, final Object value)
    {
        if (storedExtraParameters == null)
        {
            storedExtraParameters = new HashMap<String, String>();
        }
        storedExtraParameters.put(key, value.toString());
    }
}
