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

import org.eurekastreams.server.domain.Follower.FollowerStatus;

/**
 * This class contains the parameters that comprise a request made from the GroupCreator to the
 * SetFollowingGroupStatusExecution strategy.
 * 
 */
public class SetFollowingStatusByGroupCreatorRequest implements Serializable
{
    /**
     * Serialization id.
     */
    private static final long serialVersionUID = -2747040898603392049L;

    /**
     * Local instance of the Follower id for this request.
     */
    private Long followerId;

    /**
     * Local instance of the Target id for this request.
     */
    private Long targetId;

    /**
     * Local instance of the {@link FollowerStatus} for this request.
     */
    private FollowerStatus followerStatus;

    /**
     * Local instance of the target unique id.
     */
    private String targetUniqueId;

    /**
     * Local instance of the Target name for this request.
     */
    private String targetName;

    /**
     * Flag indicating if this request is pending.
     */
    private boolean isPending;

    /**
     * Constructor.
     * 
     * @param inFollowerId
     *            - instance of the follower id for this request.
     * @param inTargetId
     *            - instance of the target id for this request.
     * @param inFollowerStatus
     *            - instance of the {@link FollowerStatus} for this request.
     * @param inTargetName
     *            - instance of the target name for this request.
     * @param inTargetUniqueId
     *            - instance of the target name for this request.
     * @param inIsPending
     *            - instance of the flag for isPenging for this request.
     */
    public SetFollowingStatusByGroupCreatorRequest(final Long inFollowerId, final Long inTargetId,
            final FollowerStatus inFollowerStatus, final String inTargetName, final String inTargetUniqueId,
            final boolean inIsPending)
    {
        followerId = inFollowerId;
        targetId = inTargetId;
        followerStatus = inFollowerStatus;
        targetName = inTargetName;
        targetUniqueId = inTargetUniqueId;
        isPending = inIsPending;
    }

    /**
     * @return the followerId
     */
    public Long getFollowerId()
    {
        return followerId;
    }

    /**
     * @param inFollowerId
     *            the followerId to set
     */
    public void setFollowerId(final Long inFollowerId)
    {
        this.followerId = inFollowerId;
    }

    /**
     * @return the targetId
     */
    public Long getTargetId()
    {
        return targetId;
    }

    /**
     * @param inTargetId
     *            the inTargetId to set
     */
    public void setTargetId(final Long inTargetId)
    {
        this.targetId = inTargetId;
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
     *            the inFollowerStatus to set
     */
    public void setFollowerStatus(final FollowerStatus inFollowerStatus)
    {
        this.followerStatus = inFollowerStatus;
    }

    /**
     * @return the targetName
     */
    public String getTargetName()
    {
        return targetName;
    }

    /**
     * @param inTargetName
     *            the inTargetName to set
     */
    public void setTargetName(final String inTargetName)
    {
        this.targetName = inTargetName;
    }

    /**
     * @param inIsPending
     *            the isPending to set
     */
    public void setPending(final boolean inIsPending)
    {
        isPending = inIsPending;
    }

    /**
     * @return the isPending
     */
    public boolean isPending()
    {
        return isPending;
    }

    /**
     * @param targetUniqueId
     *            the targetUniqueId to set.
     */
    public void setTargetUniqueId(String inTargetUniqueId)
    {
        this.targetUniqueId = inTargetUniqueId;
    }

    /**
     * @return the targetUniqueId.
     */
    public String getTargetUniqueId()
    {
        return targetUniqueId;
    }
}
