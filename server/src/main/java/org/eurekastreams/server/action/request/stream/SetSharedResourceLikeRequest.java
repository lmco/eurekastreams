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
package org.eurekastreams.server.action.request.stream;

import java.io.Serializable;

import org.eurekastreams.server.domain.stream.BaseObjectType;

/**
 * Request for setting the like/unlike status of a shared resource for a person.
 */
public class SetSharedResourceLikeRequest implements Serializable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = 7571165668395727581L;

    /**
     * Resource type.
     */
    private BaseObjectType resourceType;

    /**
     * Resource unique key.
     */
    private String uniqueKey;

    /**
     * Whether the user likes this resource.
     */
    private boolean likes;

    /**
     * Constructor.
     */
    public SetSharedResourceLikeRequest()
    {
    }

    /**
     * Constructor.
     * 
     * @param inResourceType
     *            the resource type
     * @param inUniqueKey
     *            the resource unique key
     * @param inLikes
     *            true if likes, false if unlikes
     */
    public SetSharedResourceLikeRequest(final BaseObjectType inResourceType, final String inUniqueKey,
            final boolean inLikes)
    {
        resourceType = inResourceType;
        uniqueKey = inUniqueKey;
        likes = inLikes;
    }

    /**
     * @return the resourceType
     */
    public BaseObjectType getResourceType()
    {
        return resourceType;
    }

    /**
     * @param inResourceType
     *            the resourceType to set
     */
    public void setResourceType(final BaseObjectType inResourceType)
    {
        resourceType = inResourceType;
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
     * @return the likes
     */
    public boolean getLikes()
    {
        return likes;
    }

    /**
     * @param inLikes
     *            the likes to set
     */
    public void setLikes(final boolean inLikes)
    {
        likes = inLikes;
    }

}
