/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.requests;

import java.io.Serializable;

import org.eurekastreams.server.domain.stream.BaseObjectType;

/**
 * Request for setting the like/unlike status of a shared resource.
 */
public class SetLikedResourceStatusRequest implements Serializable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -9193476106311785978L;

    /**
     * Shared resource unique key.
     */
    private String uniqueKey;

    /**
     * Shared resource type.
     */
    private BaseObjectType sharedResourceType;

    /**
     * Whether the user is liking the resource.
     */
    private boolean likedStatus;

    /**
     * The id of the person making the request.
     */
    private long personId;

    /**
     * Constructor.
     */
    public SetLikedResourceStatusRequest()
    {
    }

    /**
     * Constructor.
     * 
     * @param inPersonId
     *            the id of the person making the request
     * @param inUniqueKey
     *            the shared resource unique key
     * @param inSharedResourceType
     *            the shared resource type
     * @param inLikedStatus
     *            true if the user wishes to like, or false to unlike
     */
    public SetLikedResourceStatusRequest(final long inPersonId, final String inUniqueKey,
            final BaseObjectType inSharedResourceType, final boolean inLikedStatus)
    {
        personId = inPersonId;
        uniqueKey = inUniqueKey;
        sharedResourceType = inSharedResourceType;
        likedStatus = inLikedStatus;
    }

    /**
     * @return the uniqueKey
     */
    public String getUniqueKey()
    {
        return uniqueKey;
    }

    /**
     * @param inUniqueKey
     *            the uniqueKey to set
     */
    public void setUniqueKey(final String inUniqueKey)
    {
        uniqueKey = inUniqueKey;
    }

    /**
     * @return the sharedResourceType
     */
    public BaseObjectType getSharedResourceType()
    {
        return sharedResourceType;
    }

    /**
     * @param inSharedResourceType
     *            the sharedResourceType to set
     */
    public void setSharedResourceType(final BaseObjectType inSharedResourceType)
    {
        sharedResourceType = inSharedResourceType;
    }

    /**
     * @return the likedStatus
     */
    public boolean getLikedStatus()
    {
        return likedStatus;
    }

    /**
     * @param inLikedStatus
     *            the likedStatus to set
     */
    public void setLikedStatus(final boolean inLikedStatus)
    {
        likedStatus = inLikedStatus;
    }

    /**
     * @return the personId
     */
    public long getPersonId()
    {
        return personId;
    }

    /**
     * @param inPersonId
     *            the personId to set
     */
    public void setPersonId(final long inPersonId)
    {
        personId = inPersonId;
    }

}
