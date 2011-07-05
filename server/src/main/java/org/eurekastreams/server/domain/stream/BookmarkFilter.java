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
package org.eurekastreams.server.domain.stream;

import java.io.Serializable;

/**
 * Stream Bookmark Filter.
 */
public class BookmarkFilter implements StreamFilter, Serializable
{
    /**
     * Version ID.
     */
    private static final long serialVersionUID = 154645451231L;

    /**
     * Name.
     */
    private String name = "";

    /**
     * Request.
     */
    private String request = "";

    /**
     * ID.
     */
    private Long id = 0L;

    /**
     * Owner entity ID.
     */
    private Long ownerEntityId;

    /**
     * Owner avatar ID.
     */
    private String ownerAvatarId;

    /**
     * Used for serialization.
     */
    private BookmarkFilter()
    {

    }

    /**
     * Constructor.
     * 
     * @param inId
     *            the id.
     * @param inName
     *            the name.
     * @param inRequest
     *            the request.
     * @param inOwnerAvatarId
     *            the avatar ID.
     * @param inEntityId
     *            the entity ID.
     */
    public BookmarkFilter(final Long inId, final String inName, final String inRequest, final String inOwnerAvatarId,
            final long inEntityId)
    {
        id = inId;
        name = inName;
        request = inRequest;
        ownerAvatarId = inOwnerAvatarId;
        ownerEntityId = inEntityId;
    }

    /**
     * Get the id.
     * 
     * @return the id.
     */
    public long getId()
    {
        return id;
    }

    /**
     * Get the name.
     * 
     * @return the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the request.
     * 
     * @return the request.
     */
    public String getRequest()
    {
        return request;
    }

    /**
     * Set the name.
     * 
     * @param inName
     *            the name.
     */
    public void setName(final String inName)
    {
        name = inName;
    }

    /**
     * Set the request.
     * 
     * @param inRequest
     *            the request.
     */
    public void setRequest(final String inRequest)
    {
        request = inRequest;
    }

    /**
     * Set the id.
     * 
     * @param inId
     *            the id.
     */
    public void setId(final Long inId)
    {
        id = inId;
    }

    /**
     * Get the owner avatar ID.
     * 
     * @return the owner avatar ID.
     */
    public String getOwnerAvatarId()
    {
        return ownerAvatarId;
    }

    /**
     * Get the owner entity ID.
     * 
     * @return the owner entity ID.
     */
    public Long getOwnerEntityId()
    {
        return ownerEntityId;
    }

    /**
     * Set the owner avatar id.
     * 
     * @param inOwnerAvatarId
     *            the owner avatar id.
     */
    public void setOwnerAvatarId(final String inOwnerAvatarId)
    {
        ownerAvatarId = inOwnerAvatarId;
    }

    /**
     * Set the owner entity id.
     * 
     * @param inEntityId
     *            the owner entity id.
     */
    public void setOwnerEntityId(final long inEntityId)
    {
        ownerEntityId = inEntityId;
    }
}