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

import org.eurekastreams.server.action.request.stream.DeleteActivityCacheUpdateRequest;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * DAO for updating cache after Activity delete.
 */
public class DeleteActivityCacheUpdate extends BaseArgCachedDomainMapper<DeleteActivityCacheUpdateRequest, Boolean>
{
    /**
     * DAO to get followers of a person.
     */
    private GetFollowerIds userIdsFollowingPersonDAO;

    /**
     * DAO to get followers of a group.
     */
    private GetGroupFollowerIds userIdsFollowingGroupDAO;

    /**
     * DAO to get people by account ids.
     */
    private GetPeopleByAccountIds personByAccountIdDAO;

    /**
     * DAO to get groups by short name.
     */
    private GetDomainGroupsByShortNames groupByShortNameDAO;

    /**
     * Constructor.
     *
     * @param inUserIdsFollowingPersonDAO
     *            DAO to get followers of a person.
     * @param inUserIdsFollowingGroupDAO
     *            DAO to get followers of a group.
     * @param inPersonByAccountIdDAO
     *            DAO to get people by account ids.
     * @param inGroupByShortNameDAO
     *            DAO to get groups by short name.
     */
    public DeleteActivityCacheUpdate(final GetFollowerIds inUserIdsFollowingPersonDAO,
            final GetGroupFollowerIds inUserIdsFollowingGroupDAO, final GetPeopleByAccountIds inPersonByAccountIdDAO,
            final GetDomainGroupsByShortNames inGroupByShortNameDAO)
    {
        userIdsFollowingPersonDAO = inUserIdsFollowingPersonDAO;
        userIdsFollowingGroupDAO = inUserIdsFollowingGroupDAO;
        personByAccountIdDAO = inPersonByAccountIdDAO;
        groupByShortNameDAO = inGroupByShortNameDAO;
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
        long activityId = inRequest.getActivity().getId();

        // remove activity from starred list for people that have this starred
        for (Long personId : inRequest.getPersonIdsWithActivityStarred())
        {
            getCache().removeFromList(CacheKeys.STARRED_BY_PERSON_ID + personId, activityId);
        }

        // remove comments by activityId list from cache.
        getCache().delete(CacheKeys.COMMENT_IDS_BY_ACTIVITY_ID + activityId);

        // remove activity id user's "following" lists for everyone following destination stream.
        List<Long> followingUserIds = getIdsForUsersFollowingDestinationStream(inRequest.getActivity()
                .getDestinationStream());
        for (Long followerId : followingUserIds)
        {
            getCache().removeFromList(CacheKeys.ACTIVITIES_BY_FOLLOWING + followerId, activityId);
        }

        // remove all comments from cache.
        for (Long commentId : inRequest.getCommentIds())
        {
            getCache().delete(CacheKeys.COMMENT_BY_ID + commentId);
        }

        // remove activity from cache.
        getCache().delete(CacheKeys.ACTIVITY_BY_ID + activityId);

        return true;
    }

    /**
     * Returns list of user Ids for users that are following the activity's destination stream.
     *
     * @param inDestinationStream
     *            The destination stream of activity being deleted.
     * @return List of user Ids for users that are following the activity's destination stream.
     */
    private List<Long> getIdsForUsersFollowingDestinationStream(final StreamEntityDTO inDestinationStream)
    {
        List<Long> followingUserIds = null;
        switch (inDestinationStream.getType())
        {
        case PERSON:
            long personId = personByAccountIdDAO.fetchId(inDestinationStream.getUniqueIdentifier());
            followingUserIds = userIdsFollowingPersonDAO.execute(personId);
            break;
        case GROUP:
            long groupId = groupByShortNameDAO.fetchId(inDestinationStream.getUniqueIdentifier());
            followingUserIds = userIdsFollowingGroupDAO.execute(groupId);
            break;
        default:
            throw new RuntimeException("Unexpected Activity destination stream type: " + inDestinationStream.getType());
        }

        return followingUserIds;
    }

}
