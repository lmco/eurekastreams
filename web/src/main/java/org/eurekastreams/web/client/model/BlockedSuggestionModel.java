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
package org.eurekastreams.web.client.model;

import java.io.Serializable;
import java.util.ArrayList;

import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.data.GotCurrentUserStreamBookmarks;

/**
 * Custom stream model.
 * 
 */
public class BlockedSuggestionModel extends BaseModel implements Insertable<Long>
{
    /**
     * Singleton.
     */
    private static BlockedSuggestionModel model = new BlockedSuggestionModel();

    /**
     * Gets the singleton.
     * 
     * @return the singleton.
     */
    public static BlockedSuggestionModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final Long request)
    {
        super.callWriteAction("insertBlockedSuggestion", request, new OnSuccessCommand<Serializable>()
        {
            public void onSuccess(final Serializable response)
            {
                StreamsDiscoveryModel.getInstance().clearCache();
            }
        });
    }
}
