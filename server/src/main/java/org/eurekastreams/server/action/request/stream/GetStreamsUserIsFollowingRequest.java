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
 * Request for GetStreamsUserIsFollowing action.
 * 
 */
public class GetStreamsUserIsFollowingRequest extends BasePageableRequest implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 9116845597177341218L;

    /**
     * Constructor for serialization.
     */
    public GetStreamsUserIsFollowingRequest()
    {
    }

    /**
     * Constructor.
     * 
     * @param inStartIndex
     *            The start index for items to return.
     * @param inEndIndex
     *            The end index for items to return.
     */
    public GetStreamsUserIsFollowingRequest(final int inStartIndex, final int inEndIndex)
    {
        super(inStartIndex, inEndIndex);
    }

}
