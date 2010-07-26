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
package org.eurekastreams.server.action.execution.profile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.NoCurrentUserDetails;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.action.request.stream.RefreshCachedCompositeStreamRequest;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.GetFollowedStreamViewByUser;
import org.eurekastreams.server.persistence.mappers.cache.AddCachedPersonFollower;
import org.eurekastreams.server.persistence.mappers.cache.RemoveCachedActivitiesFromList;
import org.eurekastreams.server.persistence.mappers.cache.RemoveCachedPersonFollower;
import org.eurekastreams.server.persistence.mappers.requests.RemoveCachedActivitiesFromListRequest;
import org.eurekastreams.server.persistence.mappers.stream.GetFollowerIds;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.PersonModelView;

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
     * Local instance of the GetPeopleByAccountIds.
     */
    private final GetPeopleByAccountIds cachedPersonMapper;

    /**
     * Local instance of the Cached Mapper for updating the follower status.
     */
    private final AddCachedPersonFollower addCachedFollowerMapper;

    /**
     * Local instance of the Cached Mapper for removing the follower status.
     */
    private final RemoveCachedPersonFollower removeCachedFollowerMapper;

    /**
     * Local instance of the cache mapper for retrieving followers of a user.
     */
    private final GetFollowerIds followerIdsMapper;

    /**
     * Local instance of the cache mapper for removing all activities of the newly unfollowed users from the following
     * activities list in cache.
     */
    private final RemoveCachedActivitiesFromList removeCachedActivitiesMapper;

    /**
     * Local instance of the db mapper that retrieves the followed composite stream (streamview) id.
     */
    private final GetFollowedStreamViewByUser followedStreamViewMapper;

    /**
     * Constructor for the FollowingPersonStrategy.
     *
     * @param inMapper
     *            - instance of the GetPeopleByAccountIds for this class.
     * @param inCachedPersonMapper
     *            - instance of the GetPeopleByAccountIds mapper for this class.
     * @param inAddCachedFollowerMapper
     *            - instance of AddCachedPersonFollower mapper.
     * @param inRemoveCachedFollowerMapper
     *            - instance of RemoveCachedPersonFollower mapper.
     * @param inFollowerIdsMapper
     *            - instance of GetFollowerIds mapper.
     * @param inRemoveCachedActivitiesMapper
     *            - instance of the RemoveCachedActivitiesFromList mapper.
     * @param inFollowedStreamViewMapper
     *            - instance of the GetFollowedStreamViewByUser mapper.
     */
    public SetFollowingPersonStatusExecution(final PersonMapper inMapper,
            final GetPeopleByAccountIds inCachedPersonMapper, final AddCachedPersonFollower inAddCachedFollowerMapper,
            final RemoveCachedPersonFollower inRemoveCachedFollowerMapper, final GetFollowerIds inFollowerIdsMapper,
            final RemoveCachedActivitiesFromList inRemoveCachedActivitiesMapper,
            final GetFollowedStreamViewByUser inFollowedStreamViewMapper)
    {
        mapper = inMapper;
        cachedPersonMapper = inCachedPersonMapper;
        addCachedFollowerMapper = inAddCachedFollowerMapper;
        removeCachedFollowerMapper = inRemoveCachedFollowerMapper;
        followerIdsMapper = inFollowerIdsMapper;
        removeCachedActivitiesMapper = inRemoveCachedActivitiesMapper;
        followedStreamViewMapper = inFollowedStreamViewMapper;
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
        List<UserActionRequest> currentRequests = new ArrayList<UserActionRequest>();
        SetFollowingStatusRequest request = (SetFollowingStatusRequest) inActionContext.getActionContext().getParams();
        if (logger.isTraceEnabled())
        {
            logger.trace("Entering follow person strategy with followerid: " + request.getFollowerUniqueId()
                    + " targetid: " + request.getTargetUniqueId() + " and status " + request.getFollowerStatus());
        }

        int followingCount;

        PersonModelView followerResult = cachedPersonMapper.fetchUniqueResult(request.getFollowerUniqueId());

        PersonModelView targetResult = cachedPersonMapper.fetchUniqueResult(request.getTargetUniqueId());

        Long compositeStreamId = followedStreamViewMapper.execute(followerResult.getEntityId());
        if (logger.isDebugEnabled())
        {
            logger.debug("Retrieved the compositeStreamId: " + compositeStreamId
                    + " for the list of following activities");
        }

        switch (request.getFollowerStatus())
        {
        case FOLLOWING:
            logger.trace("Add new following to the list of following.");
            mapper.addFollower(followerResult.getEntityId(), targetResult.getEntityId());
            addCachedFollowerMapper.execute(followerResult.getEntityId(), targetResult.getEntityId());

            logger.trace("Submit async action to update all cached activities.");
            // Post an async action to update the cache with the rest of the followers.
            RefreshCachedCompositeStreamRequest actionRequest = new RefreshCachedCompositeStreamRequest(
                    compositeStreamId, followerResult.getEntityId());
            currentRequests.add(new UserActionRequest("refreshCachedFollowingCompositeStreamAction",
                    new NoCurrentUserDetails(), actionRequest));

            // queues up new follower notifications.
            currentRequests.add(new UserActionRequest("createNotificationsAction", null,
                    new CreateNotificationsRequest(RequestType.FOLLOWER, followerResult.getEntityId(), targetResult
                            .getEntityId(), 0)));
            break;
        case NOTFOLLOWING:
            logger.trace("Remove new following from the list of following.");

            mapper.removeFollower(followerResult.getEntityId(), targetResult.getEntityId());

            removeCachedFollowerMapper.execute(followerResult.getEntityId(), targetResult.getEntityId());

            // Update the cache list of followers' activities by removing the activities for the
            // entity being unfollowed.
            RemoveCachedActivitiesFromListRequest removeRequest = new RemoveCachedActivitiesFromListRequest(
                    compositeStreamId, followerResult.getEntityId(), targetResult.getEntityId());
            removeCachedActivitiesMapper.execute(removeRequest);

            break;
        default:
            // do nothing.
        }

        followingCount = followerIdsMapper.execute(targetResult.getEntityId()).size();

        inActionContext.getUserActionRequests().addAll(currentRequests);
        return new Integer(followingCount);
    }

}
