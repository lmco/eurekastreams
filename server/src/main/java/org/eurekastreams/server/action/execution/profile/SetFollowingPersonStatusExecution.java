/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.profile;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.action.request.notification.TargetEntityNotificationsRequest;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.action.request.stream.DeleteIdsFromListsRequest;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.AddCachedPersonFollower;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * This class provides the Following Strategy for a Person object.
 *
 */
public class SetFollowingPersonStatusExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /**
     * Local log instance.
     */
    private final Log logger = LogFactory.make();

    /**
     * Local instance of the Person mapper.
     */
    private final PersonMapper mapper;

    /**
     * mapper to get person id from account id.
     */
    private final DomainMapper<String, Long> getPersonIdByAccountIdMapper;

    /**
     * Local instance of the Cached Mapper for updating the follower status.
     */
    private final AddCachedPersonFollower addCachedFollowerMapper;

    /**
     * Local instance of the cache mapper for retrieving followers of a user.
     */
    private final DomainMapper<Long, List<Long>> followerIdsMapper;

    /**
     * Constructor for the FollowingPersonStrategy.
     *
     * @param inMapper
     *            - instance of the PersonMapper for this class.
     * @param inGetPersonIdByAccountIdMapper
     *            - mapper to get person id from account id.
     * @param inAddCachedFollowerMapper
     *            - instance of AddCachedPersonFollower mapper.
     * @param inFollowerIdsMapper
     *            - instance of GetFollowerIds mapper.
     */
    public SetFollowingPersonStatusExecution(final PersonMapper inMapper,
            final DomainMapper<String, Long> inGetPersonIdByAccountIdMapper,
            final AddCachedPersonFollower inAddCachedFollowerMapper,
            final DomainMapper<Long, List<Long>> inFollowerIdsMapper)
    {
        mapper = inMapper;
        getPersonIdByAccountIdMapper = inGetPersonIdByAccountIdMapper;
        addCachedFollowerMapper = inAddCachedFollowerMapper;
        followerIdsMapper = inFollowerIdsMapper;
    }

    /**
     * {@inheritDoc}.
     *
     * This method performs the concrete implementation for the setting the Following status of a person following
     * another person.
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
            throws ExecutionException
    {
        SetFollowingStatusRequest request = (SetFollowingStatusRequest) inActionContext.getActionContext().getParams();
        if (logger.isTraceEnabled())
        {
            logger.trace("Entering follow person strategy with followerid: " + request.getFollowerUniqueId()
                    + " targetid: " + request.getTargetUniqueId() + " and status " + request.getFollowerStatus());
        }

        List<UserActionRequest> asyncRequests = inActionContext.getUserActionRequests();
        int followingCount;

        Long followerPersonId;
        Long followedPersonId = getPersonIdByAccountIdMapper.execute(request.getTargetUniqueId());

        // gets the current user if no follower id was passed in.
        if (request.getFollowerUniqueId() == null || request.getFollowerUniqueId() == "")
        {
            followerPersonId = inActionContext.getActionContext().getPrincipal().getId();
        }
        else
        {
            followerPersonId = getPersonIdByAccountIdMapper.execute(request.getFollowerUniqueId());
        }

        switch (request.getFollowerStatus())
        {
        case FOLLOWING:
            logger.trace("Add new following to the list of following.");
            mapper.addFollower(followerPersonId, followedPersonId);
            addCachedFollowerMapper.execute(followerPersonId, followedPersonId);

            // Queue async action to remove the newly followed person from cache (to sync follower counts)
            asyncRequests.add(new UserActionRequest("deleteCacheKeysAction", null, (Serializable) Collections
                    .singleton(CacheKeys.PERSON_BY_ID + followedPersonId)));

            logger.trace("Submit async action to update all cached activities.");

            // Post an async action to update the cache for the user's list of following activity ids.
            asyncRequests.add(new UserActionRequest("refreshFollowedByActivities", null, followerPersonId));

            // queues up new follower notifications.
            asyncRequests
                    .add(new UserActionRequest(CreateNotificationsRequest.ACTION_NAME, null,
                            new TargetEntityNotificationsRequest(RequestType.FOLLOW_PERSON, followerPersonId,
                                    followedPersonId)));

            break;
        case NOTFOLLOWING:
            logger.trace("Remove new following from the list of following.");

            mapper.removeFollower(followerPersonId, followedPersonId);

            // Queue async action to remove the newly followed person from cache (to sync follower counts)
            asyncRequests.add(new UserActionRequest("deleteCacheKeysAction", null, (Serializable) Collections
                    .singleton(CacheKeys.PERSON_BY_ID + followedPersonId)));

            // Remove the current user that is severing a relationship with the target
            // from the list of followers for that target user.
            asyncRequests.add(new UserActionRequest("deleteIdsFromLists", null, new DeleteIdsFromListsRequest(
                    Collections.singletonList(CacheKeys.FOLLOWERS_BY_PERSON + followedPersonId), Collections
                            .singletonList(followerPersonId))));

            // Remove the target user the current user is no longer following from the list of
            // users that the current is already following.
            asyncRequests.add(new UserActionRequest("deleteIdsFromLists", null, new DeleteIdsFromListsRequest(
                    Collections.singletonList(CacheKeys.PEOPLE_FOLLOWED_BY_PERSON + followerPersonId), Collections
                            .singletonList(followedPersonId))));

            // Post an async action to update the cache for the user's list of following activity ids.
            asyncRequests.add(new UserActionRequest("refreshFollowedByActivities", null, followerPersonId));
            break;
        default:
            // do nothing.
        }

        followingCount = followerIdsMapper.execute(followedPersonId).size();

        return new Integer(followingCount);
    }

}
