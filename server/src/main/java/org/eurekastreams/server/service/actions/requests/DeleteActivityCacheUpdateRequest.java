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
package org.eurekastreams.server.service.actions.requests;

import java.io.Serializable;
import java.util.List;

import org.eurekastreams.server.domain.stream.ActivityDTO;

/**
 * Request for DeleteActivityCacheUpdateAction.
 *
 */
public class DeleteActivityCacheUpdateRequest implements Serializable
{
    /**
     * Serialization version id.
     */
    private static final long serialVersionUID = 3424397485738162395L;

    /**
     * The id of activity to delete.
     */
    private ActivityDTO activity;

    /**
     * The commentIds for the activity.
     */
    private List<Long> commentIds;

    /**
     * List of people ids for users who have this activity starred.
     */
    private List<Long> personIdsWithActivityStarred;

    /**
     * Constructor.
     * @param inActivity The activity to delete.
     * @param inCommentIds The commentIds for given activity.
     * @param inPersonIdsWithActivityStarred User ids for people who starred the activity.
     */
    public DeleteActivityCacheUpdateRequest(final ActivityDTO inActivity,
            final List<Long> inCommentIds,
            final List<Long> inPersonIdsWithActivityStarred)
    {
        activity = inActivity;
        commentIds = inCommentIds;
        personIdsWithActivityStarred = inPersonIdsWithActivityStarred;
    }

    /**
     * @return the activity
     */
    public ActivityDTO getActivity()
    {
        return activity;
    }

    /**
     * @param inActivity the activityId to set
     */
    public void setActivity(final ActivityDTO inActivity)
    {
        this.activity = inActivity;
    }

    /**
     * @return the commentIds
     */
    public List<Long> getCommentIds()
    {
        return commentIds;
    }

    /**
     * @param inCommentIds the commentIds to set
     */
    public void setCommentIds(final List<Long> inCommentIds)
    {
        this.commentIds = inCommentIds;
    }

    /**
     * @return the personIdsWithActivityStarred
     */
    public List<Long> getPersonIdsWithActivityStarred()
    {
        return personIdsWithActivityStarred;
    }

    /**
     * @param inPersonIdsWithActivityStarred the personIdsWithActivityStarred to set
     */
    public void setPersonIdsWithActivityStarred(final List<Long> inPersonIdsWithActivityStarred)
    {
        this.personIdsWithActivityStarred = inPersonIdsWithActivityStarred;
    }

}
