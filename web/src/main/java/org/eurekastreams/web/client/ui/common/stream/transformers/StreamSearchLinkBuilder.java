/*
 * Copyright (c) 2010-2012 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.stream.transformers;

import java.util.HashMap;

import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;

/**
 * Builds a link to a stream search.
 */
public class StreamSearchLinkBuilder
{
    /**
     * Build a hashtag search link.
     *
     * @param searchText
     *            the hashtag to create a link for
     * @param streamViewId
     *            the id of the stream view to scope the search to, or null for all
     * @return a relative hyperlink to search the stream with the input search text
     */
    public String buildHashtagSearchLink(final String searchText, final Long streamViewId)
    {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("search", searchText);

        // in case we're viewing a single activity
        params.put("activityId", null);
        if (streamViewId != null)
        {
            params.put("viewId", Long.toString(streamViewId));
        }
        return "#" + Session.getInstance().generateUrl(new CreateUrlRequest(params, false));
    }
}
