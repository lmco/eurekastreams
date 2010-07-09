/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
 * Request object for inserting comment on an activity.
 *
 */
public class InsertActivityCommentRequest
{
    /**
     * Comment content.
     */
    private String content;
    
    /**
     * Activity id comment applies to.
     */
    private long activityId;
    
    /**
     * User that in inserting the comment.
     */
    private long userId;
    
    /**
     * Constructor.
     * @param inUserId The current user id.
     * @param inActivityId The Activity id.
     * @param inContent The Comment content.
     */
    public InsertActivityCommentRequest(final long inUserId, final long inActivityId, final String inContent)
    {
        userId = inUserId;
        activityId = inActivityId;
        content = inContent;
    }

    /**
     * @return the content
     */
    public String getContent()
    {
        return content;
    }

    /**
     * @param inContent the content to set
     */
    public void setContent(final String inContent)
    {
        this.content = inContent;
    }

    /**
     * @return the activityId
     */
    public long getActivityId()
    {
        return activityId;
    }

    /**
     * @param inActivityId the activityId to set
     */
    public void setActivityId(final long inActivityId)
    {
        this.activityId = inActivityId;
    }

    /**
     * @return the userId
     */
    public long getUserId()
    {
        return userId;
    }

    /**
     * @param inUserId the user id to set
     */
    public void setUserId(final long inUserId)
    {
        this.userId = inUserId;
    }

}
