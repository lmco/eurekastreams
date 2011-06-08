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
package org.eurekastreams.server.domain.dto;

import java.io.Serializable;

import org.eurekastreams.server.domain.Follower.FollowerStatus;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;

/**
 * Featured Stream DTO.
 * 
 */
public class FeaturedStreamDTO implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = -6581698377320020277L;

    /**
     * FeaturedStream id.
     */
    private Long id;

    /**
     * FeaturedStream description.
     */
    private String description;

    /**
     * FeaturedStream display name.
     */
    private String displayName;

    /**
     * If user is following this featured stream.
     */
    private FollowerStatus followerStatus = FollowerStatus.NOTSPECIFIED;

    /**
     * StreamScope id.
     */
    private Long streamId;

    /**
     * Stream scopetype.
     */
    private ScopeType streamType;

    /**
     * Stream unique id.
     */
    private String streamUniqueKey;

    /**
     * Avatar Id.
     */
    private String avatarId;

    /**
     * @return the id
     */
    public Long getId()
    {
        return id;
    }

    /**
     * @param inId
     *            the id to set
     */
    public void setId(final Long inId)
    {
        id = inId;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param inDescription
     *            the description to set
     */
    public void setDescription(final String inDescription)
    {
        description = inDescription;
    }

    /**
     * @return the followerStatus
     */
    public FollowerStatus getFollowerStatus()
    {
        return followerStatus;
    }

    /**
     * @param inFollowerStatus
     *            the followerStatus to set
     */
    public void setFollowerStatus(final FollowerStatus inFollowerStatus)
    {
        followerStatus = inFollowerStatus;
    }

    /**
     * @return the streamId
     */
    public Long getStreamId()
    {
        return streamId;
    }

    /**
     * @param inStreamId
     *            the streamId to set
     */
    public void setStreamId(final Long inStreamId)
    {
        streamId = inStreamId;
    }

    /**
     * @return the streamType
     */
    public ScopeType getStreamType()
    {
        return streamType;
    }

    /**
     * @param inStreamType
     *            the streamType to set
     */
    public void setStreamType(final ScopeType inStreamType)
    {
        streamType = inStreamType;
    }

    /**
     * @return the streamUniqueKey
     */
    public String getStreamUniqueKey()
    {
        return streamUniqueKey;
    }

    /**
     * @param inStreamUniqueKey
     *            the streamUniqueKey to set
     */
    public void setStreamUniqueKey(final String inStreamUniqueKey)
    {
        streamUniqueKey = inStreamUniqueKey;
    }

    /**
     * @return the avatarId
     */
    public String getAvatarId()
    {
        return avatarId;
    }

    /**
     * @param inAvatarId
     *            the avatarId to set
     */
    public void setAvatarId(final String inAvatarId)
    {
        avatarId = inAvatarId;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * @param inDisplayName
     *            the displayName to set
     */
    public void setDisplayName(final String inDisplayName)
    {
        displayName = inDisplayName;
    }

}
