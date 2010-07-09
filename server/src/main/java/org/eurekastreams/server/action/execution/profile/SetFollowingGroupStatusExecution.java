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

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.server.NoCurrentUserDetails;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.action.request.profile.RequestForGroupMembershipRequest;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusByGroupCreatorRequest;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.action.request.stream.RefreshCachedCompositeStreamRequest;
import org.eurekastreams.server.domain.Follower.FollowerStatus;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.mappers.GetFollowedStreamViewByUser;
import org.eurekastreams.server.persistence.mappers.cache.AddCachedGroupFollower;
import org.eurekastreams.server.persistence.mappers.cache.RemoveCachedActivitiesFromList;
import org.eurekastreams.server.persistence.mappers.cache.RemoveCachedGroupFollower;
import org.eurekastreams.server.persistence.mappers.db.DeleteRequestForGroupMembership;
import org.eurekastreams.server.persistence.mappers.requests.RemoveCachedActivitiesFromListRequest;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetGroupFollowerIds;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Class responsible for providing the strategy that updates the appropriate lists when a group is followed.
 *
 */
public class SetFollowingGroupStatusExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /**
     * Local instance of the GetDomainGroupsByShortNames mapper.
     */
    private final GetDomainGroupsByShortNames groupMapper;

    /**
     * Local instance of the GetPeopleByAccountIds mapper.
     */
    private final GetPeopleByAccountIds personMapper;

    /**
     * Local instance of the DomainGroupMapper mapper.
     */
    private final DomainGroupMapper domainGroupMapper;

    /**
     * Local instance of the AddCachedGroupFollower mapper.
     */
    private final AddCachedGroupFollower addCachedGroupFollowerMapper;

    /**
     * Local instance of the RemoveCachedGroupFollower mapper.
     */
    private final RemoveCachedGroupFollower removeCachedGroupFollowerMapper;

    /**
     * Local instance of the GetGroupFollowerIds mapper.
     */
    private final GetGroupFollowerIds followerIdsMapper;

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
     * Mapper to remove group access requests.
     */
    private DeleteRequestForGroupMembership deleteRequestForGroupMembershipMapper;

    /**
     * Constructor for the SetFollowingGroupStatusExecution.
     *
     * @param inGroupMapper
     *            - instance of the GetDomainGroupsByShortNames mapper.
     * @param inPersonMapper
     *            - instance of the GetPeopleByAccountIds mapper.
     * @param inDomainGroupMapper
     *            - instance of the DomainGroupMapper mapper.
     * @param inAddCachedGroupFollowerMapper
     *            - instance of the AddCachedGroupFollower mapper.
     * @param inRemoveCachedGroupFollowerMapper
     *            - instance of the RemoveCachedGroupFollower mapper.
     * @param inFollowerIdsMapper
     *            - instance of the GetGroupFollowerIds mapper.
     * @param inRemoveCachedActivitiesMapper
     *            - instance of the RemoveCachedActivitiesFromList mapper.
     * @param inFollowedStreamViewMapper
     *            = instance of the GetFollowedStreamViewByUser mapper.
     * @param inDeleteRequestForGroupMembershipMapper
     *            Mapper to remove group access requests.
     */
    public SetFollowingGroupStatusExecution(final GetDomainGroupsByShortNames inGroupMapper,
            final GetPeopleByAccountIds inPersonMapper, final DomainGroupMapper inDomainGroupMapper,
            final AddCachedGroupFollower inAddCachedGroupFollowerMapper,
            final RemoveCachedGroupFollower inRemoveCachedGroupFollowerMapper,
            final GetGroupFollowerIds inFollowerIdsMapper,
            final RemoveCachedActivitiesFromList inRemoveCachedActivitiesMapper,
            final GetFollowedStreamViewByUser inFollowedStreamViewMapper,
            final DeleteRequestForGroupMembership inDeleteRequestForGroupMembershipMapper)
    {
        groupMapper = inGroupMapper;
        personMapper = inPersonMapper;
        domainGroupMapper = inDomainGroupMapper;
        addCachedGroupFollowerMapper = inAddCachedGroupFollowerMapper;
        removeCachedGroupFollowerMapper = inRemoveCachedGroupFollowerMapper;
        followerIdsMapper = inFollowerIdsMapper;
        removeCachedActivitiesMapper = inRemoveCachedActivitiesMapper;
        followedStreamViewMapper = inFollowedStreamViewMapper;
        deleteRequestForGroupMembershipMapper = inDeleteRequestForGroupMembershipMapper;
    }

    /**
     * {@inheritDoc}.
     *
     * This method sets the following status based on the passed in request object. There is an extra block of code here
     * that handles an additional request object type that passes in the follower and target ids by string name instead
     * of their long id's. This extra support is needed for the GroupCreator object that gets called from the back end
     * with long ids instead of the string unique keys which require an additional mapper to look up the correct long
     * values.
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
            throws ExecutionException
    {
        Long followerId;
        Long targetId;
        FollowerStatus followerStatus;
        List<UserActionRequest> taskRequests = new ArrayList<UserActionRequest>();

        // this switching here is a hold over until the GroupCreator can be refactored to call this strategy
        // and not fail because of additional mapper calls on the DomainGroupModelView and PersonModelView objects.
        if (inActionContext.getActionContext().getParams() instanceof SetFollowingStatusRequest)
        {
            SetFollowingStatusRequest currentRequest = (SetFollowingStatusRequest) inActionContext.getActionContext()
                    .getParams();
            followerStatus = currentRequest.getFollowerStatus();
            PersonModelView followerResult = personMapper.fetchUniqueResult(currentRequest.getFollowerUniqueId());
            DomainGroupModelView targetResult = groupMapper.fetchUniqueResult(currentRequest.getTargetUniqueId());
            followerId = followerResult.getEntityId();
            targetId = targetResult.getEntityId();
        }
        else if (inActionContext.getActionContext().getParams() instanceof SetFollowingStatusByGroupCreatorRequest)
        {
            SetFollowingStatusByGroupCreatorRequest currentRequest = // \n
            (SetFollowingStatusByGroupCreatorRequest) inActionContext.getActionContext().getParams();
            followerId = currentRequest.getFollowerId();
            targetId = currentRequest.getTargetId();
            followerStatus = currentRequest.getFollowerStatus();
        }
        else
        {
            throw new IllegalArgumentException("Invalid Request type sent to SetFollowingGroupStatusExecution.");
        }
        Long compositeStreamId = followedStreamViewMapper.execute(followerId);

        switch (followerStatus)
        {
        case FOLLOWING:
            // Update the db and cache for list of followers and following.
            domainGroupMapper.addFollower(followerId, targetId);
            // Update the cache list of followers
            addCachedGroupFollowerMapper.execute(followerId, targetId);

            // remove any requests from the user for group membership
            deleteRequestForGroupMembershipMapper.execute(new RequestForGroupMembershipRequest(targetId, followerId));

            // Post an async action to update the cache with the rest of the followers.
            RefreshCachedCompositeStreamRequest actionRequest = new RefreshCachedCompositeStreamRequest(
                    compositeStreamId, followerId);
            taskRequests.add(new UserActionRequest("refreshCachedFollowingCompositeStreamAction",
                    new NoCurrentUserDetails(), actionRequest));

            // Sends new follower notifications.
            CreateNotificationsRequest notificationRequest = new CreateNotificationsRequest(RequestType.GROUP_FOLLOWER,
                    followerId, targetId, 0);
            taskRequests.add(new UserActionRequest("createNotificationsAction", null, notificationRequest));
            break;
        case NOTFOLLOWING:
            // Update the db and cache for list of followers and following.
            domainGroupMapper.removeFollower(followerId, targetId);
            // Update the cache list
            removeCachedGroupFollowerMapper.execute(followerId, targetId);

            RemoveCachedActivitiesFromListRequest removeRequest = new RemoveCachedActivitiesFromListRequest(
                    compositeStreamId, followerId, targetId);
            removeCachedActivitiesMapper.execute(removeRequest);

            break;
        default:
            // nothing to do here.
        }

        inActionContext.getUserActionRequests().addAll(taskRequests);
        return followerIdsMapper.execute(targetId).size();
    }
}
