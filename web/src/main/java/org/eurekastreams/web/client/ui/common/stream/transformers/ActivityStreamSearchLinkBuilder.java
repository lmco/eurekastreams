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
package org.eurekastreams.web.client.ui.common.stream.transformers;

import java.util.HashMap;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.history.HistoryHandler;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.http.client.URL;

/**
 * Builds a link to a stream search.
 */
public class ActivityStreamSearchLinkBuilder extends StreamSearchLinkBuilder
{
    /**
     * The activity that the link belongs to.
     */
    private ActivityDTO activity;

    /**
     * Constructor.
     * 
     * @param inActivity
     *            the activity that the link belongs to
     */
    public ActivityStreamSearchLinkBuilder(final ActivityDTO inActivity)
    {
        this.activity = inActivity;
    }

    /**
     * Build a hashtag search link for an activity.
     * 
     * @param searchText
     *            the hashtag to create a link for
     * @param streamViewId
     *            the id of the stream view to scope the search to, or null for all
     * @return a relative hyperlink to search the stream with the input search text
     */
    @Override
    public String buildHashtagSearchLink(final String searchText, final Long streamViewId)
    {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("search", searchText);

        HistoryHandler history = Session.getInstance().getHistoryHandler();

        // if a user clicks on a hashtag on the single activity view then search for the hashtag in the stream the
        // activity was posted to
        if (history.getPage() == Page.ACTIVITY && history.getViews().size() == 1
                && history.getViews().get(0).matches("\\d+"))
        {
            StreamEntityDTO destinationStream = activity.getDestinationStream();
            Page destinationPage = destinationStream.getEntityType() == EntityType.PERSON ? Page.PEOPLE : Page.GROUPS;
            String destinationView = destinationStream.getUniqueId();
            return URL.encode("#"
                    + Session.getInstance()
                            .generateUrl(new CreateUrlRequest(destinationPage, destinationView, params)));
        }
        // otherwise, search for hashtags in whatever stream the user is currently viewing
        else
        {
            return URL.encode("#" + Session.getInstance().generateUrl(new CreateUrlRequest(params, false)));
        }
    }
}
