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
package org.eurekastreams.web.client.events.data;

import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;

/**
 * Stream response event.
 */
public class GotStreamResponseEvent
{
    /**
     * The Stream.
     */
    private PagedSet<ActivityDTO> stream;

    /**
     * The JSON request.
     */
    private String jsonRequest;

    /**
     * Constructor.
     * 
     * @param inStream
     *            the stream.
     * @param inJsonRequest
     *            the original request.
     */
    public GotStreamResponseEvent(final PagedSet<ActivityDTO> inStream, final String inJsonRequest)
    {
        stream = inStream;
        jsonRequest = inJsonRequest;
    }

    /**
     * Get the stream.
     * 
     * @return the stream.
     */
    public PagedSet<ActivityDTO> getStream()
    {
        return stream;
    }

    /**
     * Get the JSON request.
     * 
     * @return the JSON request.
     */
    public String getJsonRequest()
    {
        return jsonRequest;
    }
}
