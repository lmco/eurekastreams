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

import org.eurekastreams.server.domain.HasGroupId;

/**
 * Request to manage a request for group membership.
 */
public class RequestForGroupMembershipRequest implements Serializable, HasGroupId
{
    /** Fingerprint. */
    private static final long serialVersionUID = 376427717519564134L;

    /** ID of group. */
    private long groupId;

    /** ID of person. */
    private long personId;

    /**
     * Constructor.
     *
     * @param inGroupId
     *            Group.
     * @param inPersonId
     *            Person.
     */
    public RequestForGroupMembershipRequest(final long inGroupId, final long inPersonId)
    {
        groupId = inGroupId;
        personId = inPersonId;
    }

    /**
     * Constructor for serialization.
     */
    private RequestForGroupMembershipRequest()
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
     * @return the personId
     */
    public long getPersonId()
    {
        return personId;
    }

    /**
     * Setter for serialization.
     *
     * @param inGroupId
     *            the groupId to set
     */
    private void setGroupId(final long inGroupId)
    {
        groupId = inGroupId;
    }

    /**
     * Setter for serialization.
     *
     * @param inPersonId
     *            the personId to set
     */
    private void setPersonId(final long inPersonId)
    {
        personId = inPersonId;
    }
}
