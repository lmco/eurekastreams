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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.stream.DeleteActivityCacheUpdateRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * DAO for updating cache after Activity delete.
 */
public class DeleteActivityCacheUpdate extends BaseArgCachedDomainMapper<DeleteActivityCacheUpdateRequest, Boolean>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * DAO to get followers of a person.
     */
    private DomainMapper<Long, List<Long>> userIdsFollowingPersonDAO;

    /**
     * DAO to get followers of a group.
     */
    private DomainMapper<Long, List<Long>> userIdsFollowingGroupDAO;

    /**
     * Mapper to get a PersonModelView by account id.
     */
    private DomainMapper<String, PersonModelView> getPersonModelViewByAccountIdMapper;

    /**
     * Mapper to get a person's id by account id.
     */
    private DomainMapper<String, Long> getPersonIdByAccountIdMapper;

    /**
     * DAO to get groups by short name.
     */
    private GetDomainGroupsByShortNames groupByShortNameDAO;

    /**
     * Get the people who liked the activity.
     */
    private DomainMapper<List<Long>, List<List<Long>>> getLikersForActivity;

    /**
     * Constructor.
     * 
     * @param inUserIdsFollowingPersonDAO
     *            DAO to get followers of a person.
     * @param inUserIdsFollowingGroupDAO
     *            DAO to get followers of a group.
     * @param inGetPersonModelViewByAccountIdMapper
     *            Mapper to get a PersonModelView by account id.
     * @param inGetPersonIdByAccountIdMapper
     *            Mapper to get a PersonModelView by account id.
     * @param inGroupByShortNameDAO
     *            DAO to get groups by short name.
     * @param inGetLikersForActivity
     *            get the likers for the activity.
     */
    public DeleteActivityCacheUpdate(final DomainMapper<Long, List<Long>> inUserIdsFollowingPersonDAO,
            final DomainMapper<Long, List<Long>> inUserIdsFollowingGroupDAO,
            final DomainMapper<String, PersonModelView> inGetPersonModelViewByAccountIdMapper,
            final DomainMapper<String, Long> inGetPersonIdByAccountIdMapper,
            final GetDomainGroupsByShortNames inGroupByShortNameDAO,
            final DomainMapper<List<Long>, List<List<Long>>> inGetLikersForActivity)
    {
        userIdsFollowingPersonDAO = inUserIdsFollowingPersonDAO;
        userIdsFollowingGroupDAO = inUserIdsFollowingGroupDAO;
        getPersonModelViewByAccountIdMapper = inGetPersonModelViewByAccountIdMapper;
        getPersonIdByAccountIdMapper = inGetPersonIdByAccountIdMapper;
        groupByShortNameDAO = inGroupByShortNameDAO;
        getLikersForActivity = inGetLikersForActivity;
    }

    /**
     * Update cache after Activity delete.
     * 
     * @param inRequest
     *            the DeleteActivityCacheUpdateRequest.
     * @return true if successful.
     */
    @Override
    public Boolean execute(final DeleteActivityCacheUpdateRequest inRequest)
    {
        ActivityDTO activity = inRequest.getActivity();
        long activityId = activity.getId();

        log.info("Cleaning up cache for deleted activity #" + activity.getId());

        // Remove from entity stream.
        EntityType streamType = activity.getDestinationStream().getType();

        switch (streamType)
        {
        case GROUP:
            DomainGroupModelView group = groupByShortNameDAO.execute(
                    Arrays.asList(activity.getDestinationStream().getUniqueIdentifier())).get(0);
            log.info("Removing activity #" + activityId + " from group stream + " + group.getStreamId());

            getCache().removeFromList(CacheKeys.ENTITY_STREAM_BY_SCOPE_ID + group.getStreamId(), activityId);
            break;
        case PERSON:
            PersonModelView person = getPersonModelViewByAccountIdMapper.execute(activity.getDestinationStream()
                    .getUniqueIdentifier());

            log.info("Removing activity #" + activityId + " from person stream + " + person.getStreamId());

            getCache().removeFromList(CacheKeys.ENTITY_STREAM_BY_SCOPE_ID + person.getStreamId(), activityId);

            break;
        default:
            break;
        }

        // remove activity from starred list for people that have this starred
        for (Long personId : inRequest.getPersonIdsWithActivityStarred())
        {
            log.info("Removing activity #" + activityId + " from starred list for person #" + personId);
            getCache().removeFromList(CacheKeys.STARRED_BY_PERSON_ID + personId, activityId);
        }

        // Update likers
        List<List<Long>> likers = getLikersForActivity.execute(Arrays.asList(activityId));

        if (likers.size() > 0)
        {
            for (Long liker : likers.get(0))
            {
                log.info("Removing activity #" + activityId + " from liked list for person #" + liker);
                getCache().removeFromList(CacheKeys.LIKED_BY_PERSON_ID + liker, activityId);
            }
        }

        getCache().delete(CacheKeys.LIKERS_BY_ACTIVITY_ID + activityId);

        // remove comments by activityId list from cache.
        getCache().delete(CacheKeys.COMMENT_IDS_BY_ACTIVITY_ID + activityId);

        // remove activity id user's "following" lists for everyone following destination stream.
        List<Long> followingUserIds = getIdsForUsersFollowingDestinationStream(inRequest.getActivity());
        for (Long followerId : followingUserIds)
        {
            log.info("Removing activity #" + activityId + " from followed list for person #" + followerId);
            getCache().removeFromList(CacheKeys.ACTIVITIES_BY_FOLLOWING + followerId, activityId);
        }

        // remove all comments from cache.
        for (Long commentId : inRequest.getCommentIds())
        {
            log.info("Removing comment id #" + commentId + " from cache for activity #" + activityId);
            getCache().delete(CacheKeys.COMMENT_BY_ID + commentId);
        }

        // remove activity from cache.
        log.info("Removing activity with id #" + activityId + " from cache.");
        getCache().delete(CacheKeys.ACTIVITY_BY_ID + activityId);

        // remove activity from cache.
        log.info("Removing activity security dto with activity id #" + activityId + " from cache.");
        getCache().delete(CacheKeys.ACTIVITY_SECURITY_BY_ID + activityId);

        return true;
    }

    /**
     * Returns list of user Ids for users that are following the activity's destination stream.
     * 
     * @param inActivityDTO
     *            The activity being deleted.
     * @return List of user Ids for users that are following the activity's destination stream.
     */
    private List<Long> getIdsForUsersFollowingDestinationStream(final ActivityDTO inActivityDTO)
    {
        StreamEntityDTO destinationStream = inActivityDTO.getDestinationStream();
        List<Long> followingUserIds = null;
        switch (destinationStream.getType())
        {
        case PERSON:
            long personId = getPersonIdByAccountIdMapper.execute(destinationStream.getUniqueIdentifier());
            followingUserIds = userIdsFollowingPersonDAO.execute(personId);
            break;
        case GROUP:
            long groupId = groupByShortNameDAO.fetchId(destinationStream.getUniqueIdentifier());
            followingUserIds = userIdsFollowingGroupDAO.execute(groupId);
            break;
        case RESOURCE:
            if (inActivityDTO.getActor().getType() == EntityType.PERSON)
            {
                inActivityDTO.getActor().getUniqueIdentifier();
                long actorPersonId = getPersonIdByAccountIdMapper.execute(inActivityDTO.getActor()
                        .getUniqueIdentifier());
                followingUserIds = userIdsFollowingPersonDAO.execute(actorPersonId);
            }
            else
            {
                throw new RuntimeException("Unexpected Actor type for resource activity: "
                        + inActivityDTO.getActor().getType());
            }
            break;
        default:
            throw new RuntimeException("Unexpected Activity destination stream type: " + destinationStream.getType());
        }

        return followingUserIds;
    }
}
