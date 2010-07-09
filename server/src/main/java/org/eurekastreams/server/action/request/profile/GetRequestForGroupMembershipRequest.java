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
package org.eurekastreams.server.action.request.profile;

import java.io.Serializable;

import org.eurekastreams.server.action.request.BasePageableRequest;
import org.eurekastreams.server.domain.HasGroupId;

/**
 * Request for list of people requesting membership in the group.
 */
public class GetRequestForGroupMembershipRequest extends BasePageableRequest implements Serializable, HasGroupId
{
    /** Fingerprint. */
    private static final long serialVersionUID = 4332918393608839460L;

    /** Group. */
    private long groupId;

    /**
     * Constructor.
     * 
     * @param inGroupId
     *            Group id.
     * @param inStartIndex
     *            The zero-based start index for items to return.
     * @param inEndIndex
     *            The zero-based end index for items to return.
     */
    public GetRequestForGroupMembershipRequest(final long inGroupId, final int inStartIndex, final int inEndIndex)
    {
        super(inStartIndex, inEndIndex);
        groupId = inGroupId;
    }

    /**
     * Constructor for serialization.
     */
    public GetRequestForGroupMembershipRequest()
    {
    }

    /**
     * @return the groupId
     */
    public long getGroupId()
    {
        return groupId;
    }

    /**
     * @param inGroupId
     *            the groupId to set
     */
    public void setGroupId(final long inGroupId)
    {
        groupId = inGroupId;
    }
}
