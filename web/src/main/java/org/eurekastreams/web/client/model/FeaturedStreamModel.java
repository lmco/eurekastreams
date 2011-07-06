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
import java.util.List;

import org.eurekastreams.server.action.request.stream.GetFeaturedStreamsPageRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.dto.FeaturedStreamDTO;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.AddedFeaturedStreamResponseEvent;
import org.eurekastreams.web.client.events.data.DeletedFeaturedStreamResponse;
import org.eurekastreams.web.client.events.data.GotFeaturedStreamsPageResponseEvent;
import org.eurekastreams.web.client.events.data.GotStreamDiscoverListsDTOResponseEvent;
import org.eurekastreams.web.client.ui.Session;

/**
 * Model for featured stream interaction.
 * 
 */
public class FeaturedStreamModel extends BaseModel implements Insertable<FeaturedStreamDTO>, Deletable<Long>,
        Fetchable<GetFeaturedStreamsPageRequest>
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
     * Fetch a page of data with the input GetFeaturedStreamsPageRequest.
     * 
     * @param inRequest
     *            the request, specifying the start and end index, inclusive
     * @param inUseClientCacheIfAvailable
     *            whether to use the client cache
     */
    public void fetch(final GetFeaturedStreamsPageRequest inRequest, final boolean inUseClientCacheIfAvailable)
    {
        EventBus.getInstance().addObserver(GotStreamDiscoverListsDTOResponseEvent.class,
                new Observer<GotStreamDiscoverListsDTOResponseEvent>()
                {
                    public void update(final GotStreamDiscoverListsDTOResponseEvent event)
                    {
                        EventBus.getInstance().removeObserver(GotStreamDiscoverListsDTOResponseEvent.class, this);

                        // we have the data - determine the page of data we need
                        List<FeaturedStreamDTO> featuredStreams = event.getResponse().getFeaturedStreams();

                        PagedSet<FeaturedStreamDTO> resultPage = new PagedSet<FeaturedStreamDTO>();
                        resultPage.setFromIndex(inRequest.getStartIndex());
                        resultPage.setToIndex(inRequest.getEndIndex());
                        resultPage.setTotal(featuredStreams.size());

                        if (featuredStreams != null && inRequest.getStartIndex() != null
                                && inRequest.getStartIndex() >= 0
                                && featuredStreams.size() > inRequest.getStartIndex())
                        {
                            // we have data
                            resultPage.setPagedSet(new ArrayList<FeaturedStreamDTO>());
                            if (featuredStreams.size() < inRequest.getEndIndex() + 1)
                            {
                                // we're at the end of the list
                                resultPage.setPagedSet(featuredStreams.subList(inRequest.getStartIndex(), //
                                        featuredStreams.size()));
                            }
                            else
                            {
                                // this isn't the last page
                                resultPage.setPagedSet(featuredStreams.subList(inRequest.getStartIndex(),
                                        inRequest.getEndIndex() + 1));
                            }
                        }
                        else
                        {
                            // no data
                            resultPage.setPagedSet(new ArrayList<FeaturedStreamDTO>());
                        }

                        Session.getInstance().getEventBus()
                                .notifyObservers(new GotFeaturedStreamsPageResponseEvent(resultPage));
                    }
                });

        StreamsDiscoveryModel.getInstance().fetch(null, inUseClientCacheIfAvailable);
    }

}
