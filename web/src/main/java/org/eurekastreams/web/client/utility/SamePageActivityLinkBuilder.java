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
package org.eurekastreams.web.client.utility;

import java.util.Map;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.web.client.history.CreateUrlRequest;

/**
 * Builds activity links that display the activity on the same page.
 */
public class SamePageActivityLinkBuilder extends BaseActivityLinkBuilder
{
    /**
     * {@inheritDoc}
     */
    @Override
    public CreateUrlRequest buildActivityPermalinkUrlRequest(final long inActivityId, final EntityType inStreamType,
            final String inStreamUniqueId, final Map<String, String> inExtraParameters)
    {
        return new CreateUrlRequest(Page.ACTIVITY, Long.toString(inActivityId));
    }
}
