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
package org.eurekastreams.server.action.request.stream;

import java.io.Serializable;

import org.eurekastreams.server.action.request.BasePageableRequest;

/**
 * Request object to get a paged set of StreamDTOs representing the most active streams.
 */
public class GetMostActiveStreamsPageRequest extends BasePageableRequest implements Serializable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -989997376886390798L;

    /**
     * Constructor.
     *
     * @param inStartIndex
     *            the start index
     * @param inEndIndex
     *            the end index
     */
    public GetMostActiveStreamsPageRequest(final int inStartIndex, final int inEndIndex)
    {
        super(inStartIndex, inEndIndex);
    }
}
