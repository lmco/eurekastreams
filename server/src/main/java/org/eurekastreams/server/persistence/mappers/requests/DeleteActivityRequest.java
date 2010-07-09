/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

/**
 * Request object for use with DeleteActivity DAO.
 *
 */
public class DeleteActivityRequest
{
    /**
     * The current user's id.
     */
    private Long userId;
    
    /**
     * The id of activity to delete.
     */
    private Long activityId;
    
    /**
     * 
     * @param inUserId The current user's id.
     * @param inActivityId The id of activity to delete.
     */
    public DeleteActivityRequest(final Long inUserId, final Long inActivityId)
    {
        userId = inUserId;
        activityId = inActivityId;
    }

    /**
     * @return the userId
     */
    public Long getUserId()
    {
        return userId;
    }

    /**
     * @param inUserId the userId to set
     */
    public void setUserId(final Long inUserId)
    {
        this.userId = inUserId;
    }

    /**
     * @return the activityId
     */
    public Long getActivityId()
    {
        return activityId;
    }

    /**
     * @param inActivityId the activityId to set
     */
    public void setActivityId(final Long inActivityId)
    {
        this.activityId = inActivityId;
    }
}
