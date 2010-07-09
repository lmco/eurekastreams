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
package org.eurekastreams.web.client.utility;

import java.util.Collections;
import java.util.Map;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.web.client.history.CreateUrlRequest;

/**
 * Builds activity links that display the activity on the profile page for the stream in which it lives (or the activity
 * page if that cannot be determined).
 */
public class InContextActivityLinkBuilder extends BaseActivityLinkBuilder
{
    /** Assists with building links. */
    static LinkBuilderHelper linkBuilderHelper = new LinkBuilderHelper();

    /**
     * {@inheritDoc}
     */
    @Override
    public CreateUrlRequest buildActivityPermalinkUrlRequest(final long inActivityId, final EntityType inStreamType,
            final String inStreamUniqueId, final Map<String, String> inExtraParameters)
    {
        Map<String, String> parameters = buildParameters(inExtraParameters);
        parameters.put("activityId", Long.toString(inActivityId));

        Page page = linkBuilderHelper.getEntityProfilePage(inStreamType);

        // if we cannot determine the profile page for the destination type, then go to the activity page, else go to
        // the appropriate profile page
        return page == null ? new CreateUrlRequest(Page.ACTIVITY, Collections.EMPTY_LIST, parameters)
                : new CreateUrlRequest(page, inStreamUniqueId, parameters);
    }
}
