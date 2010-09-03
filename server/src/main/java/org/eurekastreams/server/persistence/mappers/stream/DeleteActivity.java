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

import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.requests.DeleteActivityRequest;

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
     * CompositeStream Ids by user DAO.
     */
    private UserCompositeStreamIdsMapper userCompositeStreamIdsDAO;

    /**
     * CompositeStreams by id DAO.
     */
    private BulkCompositeStreamsMapper userCompositeStreamDAO;

    /**
     * People by account id DAO.
     */
    private GetPeopleByAccountIds bulkPeopleByAccountIdMapper;

    /**
     * Groups by short name DAO.
     */
    private GetDomainGroupsByShortNames bulkDomainGroupsByShortNameMapper;

    /**
     * Constructor.
     *
     * @param inActivityDAO
     *            Activity DAO.
     * @param inUserCompositeStreamIdsDAO
     *            CompositeStream Ids by user DAO.
     * @param inUserCompositeStreamDAO
     *            CompositeStreams by id DAO.
     * @param inBulkPeopleByAccountIdMapper
     *            People by account id DAO.
     * @param inBulkDomainGroupsByShortNameMapper
     *            Groups by short name DAO.
     * @param inCommentIdsByActivityIdDAO
     *            Comment ids by activity id DAO.
     */
    public DeleteActivity(final DomainMapper<List<Long>, List<ActivityDTO>> inActivityDAO,
            final UserCompositeStreamIdsMapper inUserCompositeStreamIdsDAO,
            final BulkCompositeStreamsMapper inUserCompositeStreamDAO,
            final GetPeopleByAccountIds inBulkPeopleByAccountIdMapper,
            final GetDomainGroupsByShortNames inBulkDomainGroupsByShortNameMapper,
            final GetOrderedCommentIdsByActivityId inCommentIdsByActivityIdDAO)
    {
        activityDAO = inActivityDAO;
        userCompositeStreamIdsDAO = inUserCompositeStreamIdsDAO;
        userCompositeStreamDAO = inUserCompositeStreamDAO;
        bulkDomainGroupsByShortNameMapper = inBulkDomainGroupsByShortNameMapper;
        bulkPeopleByAccountIdMapper = inBulkPeopleByAccountIdMapper;

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
        List<ActivityDTO> activities = activityDAO.execute(Arrays.asList(activityId));

        // activity already deleted, short circuit.
        if (activities.size() == 0)
        {
            return null;
        }

        // Activity to be deleted.
        ActivityDTO activity = activities.get(0);

        // Destination stream for activity.
        StreamEntityDTO destination = activity.getDestinationStream();

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

        // Remove Activity from Destination CompositeStream
        removeActivityIdFromCompositeStreamListInCache(getDestinationCompositeStreamId(activity), activityId);

        // Get user's composite streams
        List<StreamFilter> compositeStreams = userCompositeStreamDAO.execute(userCompositeStreamIdsDAO.execute(userId));

        // If user's compositeStream contains the activity's destination
        // stream, OR if compositeStream is "Everyone", remove activity
        // id from activity ids associated with that CompositeStream.
        Long destinationStreamId = destination.getId();
        for (StreamFilter streamFilter : compositeStreams)
        {
            StreamView compositeStream = (StreamView) streamFilter;
            if (compositeStream.getType() == StreamView.Type.EVERYONE
                    || containsStream(compositeStream, destinationStreamId))
            {
                removeActivityIdFromCompositeStreamListInCache(compositeStream.getId(), activityId);
            }
        }

        return activity;
    }

    /**
     * Returns true if StreamView contains provided stream id, false otherwise.
     *
     * @param inStreamView
     *            The StreamView
     * @param inStreamId
     *            The StreamId
     * @return true if StreamView contains provided stream id, false otherwise.
     */
    private boolean containsStream(final StreamView inStreamView, final Long inStreamId)
    {
        boolean containsStream = false;
        for (StreamScope scope : inStreamView.getIncludedScopes())
        {
            if (scope.getId() == inStreamId)
            {
                containsStream = true;
                break;
            }
        }
        return containsStream;
    }

    /**
     * Returns destination CompositeStream id for a given activity.
     *
     * @param inActivity
     *            The activity.
     * @return Destination CompositeStream id for a given activity.
     */
    private long getDestinationCompositeStreamId(final ActivityDTO inActivity)
    {
        // grab destination stream of activity.
        final StreamEntityDTO destinationStream = inActivity.getDestinationStream();

        // return destination's CompositeStream id.
        switch (destinationStream.getType())
        {
        case PERSON:
            return bulkPeopleByAccountIdMapper.fetchUniqueResult(destinationStream.getUniqueIdentifier())
                    .getCompositeStreamId();
        case GROUP:
            return bulkDomainGroupsByShortNameMapper.fetchUniqueResult(destinationStream.getUniqueIdentifier())
                    .getCompositeStreamId();
        default:
            throw new RuntimeException("Unexpected Activity destination stream type: " + destinationStream.getType());
        }
    }

    /**
     * Removes provided activityId from list of activities for the provided compositeStreamId in cache.
     *
     * @param inCompositeStreamId
     *            CompositeStream id.
     * @param inActivityId
     *            Activity id.
     */
    private void removeActivityIdFromCompositeStreamListInCache(final Long inCompositeStreamId, final Long inActivityId)
    {
        String cacheKey = CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + inCompositeStreamId;
        getCache().removeFromList(cacheKey, inActivityId);
    }
}
