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
    private static final long serialVersionUID = -2927474010674049347L;

    /**
     * Account id of user to get streams for.
     */
    private String accountId;

    /**
     * Constructor for serialization.
     */
    public GetStreamsUserIsFollowingRequest()
    {
    }

    /**
     * Constructor.
     * 
     * @param inAccountId
     *            Account id for user to get streams for.
     * @param inStartIndex
     *            The start index for items to return.
     * @param inEndIndex
     *            The end index for items to return.
     */
    public GetStreamsUserIsFollowingRequest(final String inAccountId, final int inStartIndex, final int inEndIndex)
    {
        super(inStartIndex, inEndIndex);
        accountId = inAccountId;
    }

    /**
     * @return the accountId
     */
    public String getAccountId()
    {
        return accountId;
    }

    /**
     * @param inAccountId
     *            the accountId to set
     */
    public void setAccountId(final String inAccountId)
    {
        accountId = inAccountId;
    }

}
