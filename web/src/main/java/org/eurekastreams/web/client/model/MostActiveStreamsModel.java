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

import org.eurekastreams.server.action.request.stream.GetMostActiveStreamsPageRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.dto.StreamDTO;
import org.eurekastreams.server.domain.dto.SublistWithResultCount;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotMostActiveStreamsPageResponseEvent;
import org.eurekastreams.web.client.events.data.GotStreamDiscoverListsDTOResponseEvent;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.user.client.Window;

/**
 * Model to get paged results of StreamDTOs for displaying the most active streams.
 */
public class MostActiveStreamsModel extends BaseModel implements Fetchable<GetMostActiveStreamsPageRequest>
{
    /**
     * Singleton.
     */
    private static MostActiveStreamsModel model = new MostActiveStreamsModel();

    /**
     * Gets the singleton.
     *
     * @return the singleton.
     */
    public static MostActiveStreamsModel getInstance()
    {
        return model;
    }

    /**
     * Fetch a page of StreamDTOs from the most active streams.
     *
     * @param inRequest
     *            the request, containing the start and end index, inclusive
     * @param inUseClientCacheIfAvailable
     *            whether to use client cache
     */
    public void fetch(final GetMostActiveStreamsPageRequest inRequest, final boolean inUseClientCacheIfAvailable)
    {
        // Get all of the most active stream DTOs from the StreamsDiscovery model
        EventBus.getInstance().addObserver(GotStreamDiscoverListsDTOResponseEvent.class,
                new Observer<GotStreamDiscoverListsDTOResponseEvent>()
                {
                    public void update(final GotStreamDiscoverListsDTOResponseEvent event)
                    {
                        EventBus.getInstance().removeObserver(GotStreamDiscoverListsDTOResponseEvent.class, this);
                        Window.alert("GotStreamDiscoverListsDTOResponseEvent");

                        // we have the data - determine the page of data we need
                        SublistWithResultCount<StreamDTO> mostActiveStreams = event.getResponse()
                                .getMostActiveStreams();
                        List<StreamDTO> streamDTOs = mostActiveStreams.getResultsSublist();

                        PagedSet<StreamDTO> resultPage = new PagedSet<StreamDTO>();
                        resultPage.setFromIndex(inRequest.getStartIndex());
                        resultPage.setToIndex(inRequest.getEndIndex());
                        resultPage.setTotal(mostActiveStreams.getTotalResultsCount().intValue());

                        if (streamDTOs != null && inRequest.getStartIndex() != null && inRequest.getStartIndex() > 0
                                && streamDTOs.size() > inRequest.getStartIndex())
                        {
                            // we have data
                            resultPage.setPagedSet(new ArrayList<StreamDTO>());
                            if (streamDTOs.size() < inRequest.getEndIndex() + 1)
                            {
                                // we're at the end of the list
                                resultPage.setPagedSet(streamDTOs
                                        .subList(inRequest.getStartIndex(), streamDTOs.size()));
                            }
                            else
                            {
                                // this isn't the last page
                                resultPage.setPagedSet(streamDTOs.subList(inRequest.getStartIndex(), inRequest
                                        .getEndIndex() + 1));
                            }
                        }
                        else
                        {
                            // no data
                            resultPage.setPagedSet(new ArrayList<StreamDTO>());
                        }

                        Session.getInstance().getEventBus().notifyObservers(
                                new GotMostActiveStreamsPageResponseEvent(resultPage));
                    }
                });

        Window.alert("MostActiveStreamsModel - about to fetch");
        StreamsDiscoveryModel.getInstance().fetch(null, inUseClientCacheIfAvailable);
    }
}
