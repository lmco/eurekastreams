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
package org.eurekastreams.server.action.request.stream;

import java.io.Serializable;

import org.eurekastreams.server.action.request.BasePageableRequest;

/**
 * Request a page of flagged activities.
 */
public class GetFlaggedActivitiesRequest extends BasePageableRequest implements Serializable
{
    /** Fingerprint. */
    private static final long serialVersionUID = 377907186485656173L;

    /**
     * The requesting user account id.
     */
    private String requestingUserAccountId;

    /**
     * Constructor for serialization.
     */
    public GetFlaggedActivitiesRequest()
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
    public GetFlaggedActivitiesRequest(final int inStartIndex, final int inEndIndex)
    {
        super(inStartIndex, inEndIndex);
    }

    /**
     * Set the requesting user account id.
     * 
     * @param inRequestingUserAccountId
     *            the request user account id
     */
    public void setRequestingUserAccountId(final String inRequestingUserAccountId)
    {
        this.requestingUserAccountId = inRequestingUserAccountId;
    }

    /**
     * Get the requesting user account id.
     * 
     * @return the requesting user account id
     */
    public String getRequestingUserAccountId()
    {
        return requestingUserAccountId;
    }
}
