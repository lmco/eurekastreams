/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import java.util.ArrayList;

import org.eurekastreams.server.domain.dto.FeaturedStreamDTO;
import org.eurekastreams.web.client.events.data.AddedFeaturedStreamResponseEvent;
import org.eurekastreams.web.client.events.data.DeletedFeaturedStreamResponse;
import org.eurekastreams.web.client.events.data.GotFeaturedStreamsResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Model for featured stream interaction.
 * 
 */
public class FeaturedStreamModel extends BaseModel implements Insertable<FeaturedStreamDTO>, Deletable<Long>,
        Fetchable<Long>
{

    /**
     * Singleton.
     */
    private static FeaturedStreamModel model = new FeaturedStreamModel();

    /**
     * Gets the singleton.
     * 
     * @return the singleton.
     */
    public static FeaturedStreamModel getInstance()
    {
        return model;
    }

    /**
     * {@inheritDoc}
     */
    public void insert(final FeaturedStreamDTO inRequest)
    {
        super.callWriteAction("addFeaturedStream", inRequest, new OnSuccessCommand<Long>()
        {
            public void onSuccess(final Long response)
            {
                Session.getInstance().getEventBus().notifyObservers(new AddedFeaturedStreamResponseEvent(response));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void delete(final Long inRequest)
    {
        super.callWriteAction("deleteFeaturedStream", inRequest, new OnSuccessCommand<Boolean>()
        {
            public void onSuccess(final Boolean response)
            {
                Session.getInstance().getEventBus().notifyObservers(new DeletedFeaturedStreamResponse(inRequest));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void fetch(final Long inRequest, final boolean inUseClientCacheIfAvailable)
    {
        super.callReadAction("getFeaturedStreams", inRequest, new OnSuccessCommand<ArrayList<FeaturedStreamDTO>>()
        {
            public void onSuccess(final ArrayList<FeaturedStreamDTO> response)
            {
                Session.getInstance().getEventBus().notifyObservers(new GotFeaturedStreamsResponseEvent(response));
            }
        }, inUseClientCacheIfAvailable);
    }
}
