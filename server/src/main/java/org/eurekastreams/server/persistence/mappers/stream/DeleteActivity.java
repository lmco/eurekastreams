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
package org.eurekastreams.server.persistence.mappers.stream;

import java.util.List;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.requests.DeleteActivityRequest;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Delete an activity (and associated comments) from DB and update current user's CompositeStreams (and "Everyone"
 * CompositeStream) in cache so UI functions correctly without lag. NOTE: This will NOT update all CompositeStreams in
 * cache that may contain the activity.
 *
 */
public class DeleteActivity extends BaseArgCachedDomainMapper<DeleteActivityRequest, ActivityDTO>
{
    /**
     * Activity DAO.
     */
    private DomainMapper<List<Long>, List<ActivityDTO>> activityDAO;

    /**
     * Constructor.
     *
     * @param inActivityDAO
     *            Activity DAO.
     */
    public DeleteActivity(final DomainMapper<List<Long>, List<ActivityDTO>> inActivityDAO)
    {
        activityDAO = inActivityDAO;
    }

    /**
     * Delete an activity from DB and update current user's CompositeStreams (and "Everyone" CompositeStream) in cache
     * so UI functions correctly without lag.
     *
     * @param inDeleteActivityRequest
     *            The DeleteActivityRequest object.
     * @return The list of comment ids associated with the activity. If the activity doesn't have any comments, an empty
     *         list will be returned. If the activity to be deleted is no longer present, null will be returned as
     *         "short-circuit" value.
     */
    public ActivityDTO execute(final DeleteActivityRequest inDeleteActivityRequest)
    {
        final Long activityId = inDeleteActivityRequest.getActivityId();
        final Long userId = inDeleteActivityRequest.getUserId();
        List<ActivityDTO> activities = activityDAO.execute(Collections.singletonList(activityId));

        // activity already deleted, short circuit.
        if (activities.size() == 0)
        {
            return null;
        }

        // Activity to be deleted.
        ActivityDTO activity = activities.get(0);

        // delete activity comments from DB if needed.
        getEntityManager().createQuery("DELETE FROM Comment c WHERE c.target.id = :activityId").setParameter(
                "activityId", activityId).executeUpdate();

        // delete activity from currentUser's starred activity collections in DB.
        getEntityManager().createQuery("DELETE FROM StarredActivity where activityId = :activityId").setParameter(
                "activityId", activityId).executeUpdate();

        // delete any hashtags stored to streams on behalf of this activity
        getEntityManager().createQuery("DELETE FROM StreamHashTag WHERE activity.id = :activityId").setParameter(
                "activityId", activityId).executeUpdate();

        // delete activity from DB.
        getEntityManager().createQuery("DELETE FROM Activity WHERE id = :activityId").setParameter("activityId",
                activityId).executeUpdate();

        // Remove activity id from user's starred list in cache.
        getCache().removeFromList(CacheKeys.STARRED_BY_PERSON_ID + userId, activityId);

        // Remove activity id from user's following list in cache.
        getCache().removeFromList(CacheKeys.ACTIVITIES_BY_FOLLOWING + userId, activityId);

        return activity;
    }

}
