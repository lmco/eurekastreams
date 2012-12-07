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
package org.eurekastreams.web.client.events.data;

import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;

/**
 * Stream response event.
 */
public class GotStreamResponseEvent extends BaseDataRequestResponseEvent<String, PagedSet<ActivityDTO>>
{
    /**
     * The sort type.
     */
    private final String sortType;

    /**
     * Constructor.
     *
     * @param inStream
     *            the stream.
     * @param inJsonRequest
     *            the original request.
     * @param sortString
     *            the sort string.
     */
    public GotStreamResponseEvent(final PagedSet<ActivityDTO> inStream, final String inJsonRequest,
            final String sortString)
    {
        super(inJsonRequest, inStream);
        sortType = sortString;
    }

    /**
     * Get the stream.
     *
     * @return the stream.
     */
    public PagedSet<ActivityDTO> getStream()
    {
        return getResponse();
    }

    /**
     * Get the JSON request.
     *
     * @return the JSON request.
     */
    public String getJsonRequest()
    {
        return getRequest();
    }

    /**
     * Get the sort type.
     *
     * @return the sort type.
     */
    public String getSortType()
    {
        return sortType;
    }
}
