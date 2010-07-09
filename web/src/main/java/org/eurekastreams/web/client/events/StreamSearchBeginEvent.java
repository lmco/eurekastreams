/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.events;

import org.eurekastreams.server.domain.stream.StreamSearch;

/**
 * Gets fired when the search text changes.
 * 
 */
public class StreamSearchBeginEvent
{
    /**
     * Gets an instance of the event.
     * 
     * @return the event.
     */
    public static StreamSearchBeginEvent getEvent()
    {
        return new StreamSearchBeginEvent(null, 0);
    }

    /**
     * The search text.
     */
    private String searchText;

    /**
     * The stream id.
     */
    private long streamId;
    
    /**
     * The search.
     */
    private StreamSearch search = null;

    /**
     * Default constructor.
     * 
     * @param inSearchText
     *            the search text.
     * @param inStreamId
     *            the stream view id.
     */
    public StreamSearchBeginEvent(final String inSearchText, final long inStreamId)
    {
        searchText = inSearchText;
        streamId = inStreamId;
    }
    
    /**
     * Default constructor.
     * 
     * @param inSearchText
     *            the search text.
     * @param inSearch the search.
     */
    public StreamSearchBeginEvent(final String inSearchText, final StreamSearch inSearch)
    {
        search = inSearch;
        searchText = inSearchText;
        streamId = search.getStreamView().getId();
    }

    /**
     * Returns the search text.
     * 
     * @return the search text.
     */
    public String getSearchText()
    {
        return searchText;
    }

    /**
     * Get the stream id.
     * 
     * @return the stream id.
     */
    public long getStreamId()
    {
        return streamId;
    }
    
    /**
     * Gets the search.
     * @return the search.
     */
    public StreamSearch getSearch()
    {
        return search;
    }
}
