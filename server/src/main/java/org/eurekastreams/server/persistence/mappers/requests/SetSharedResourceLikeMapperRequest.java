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

import org.eurekastreams.server.domain.stream.SharedResource;

/**
 * Request for setting the like/unlike status of a shared resource.
 */
public class SetSharedResourceLikeMapperRequest implements Serializable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -9193476106311785978L;

    /**
     * Whether the user is liking the resource.
     */
    private boolean likedStatus;

    /**
     * The id of the person making the request.
     */
    private long personId;

    /**
     * The shared resource.
     */
    private SharedResource sharedResource;

    /**
     * Constructor.
     */
    public SetSharedResourceLikeMapperRequest()
    {
    }

    /**
     * Constructor.
     * 
     * @param inPersonId
     *            the id of the person making the request
     * @param inSharedResource
     *            the shared resource
     * @param inLikedStatus
     *            true if the user wishes to like, or false to unlike
     */
    public SetSharedResourceLikeMapperRequest(final long inPersonId, final SharedResource inSharedResource,
            final boolean inLikedStatus)
    {
        personId = inPersonId;
        sharedResource = inSharedResource;
        likedStatus = inLikedStatus;
    }

    /**
     * @return the sharedResource
     */
    public SharedResource getSharedResource()
    {
        return sharedResource;
    }

    /**
     * @param inSharedResource
     *            the sharedResource to set
     */
    public void setSharedResource(final SharedResource inSharedResource)
    {
        sharedResource = inSharedResource;
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
