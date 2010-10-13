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
import java.util.Collections;
import java.util.List;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.action.request.profile.RequestForGroupMembershipRequest;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusByGroupCreatorRequest;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.action.request.stream.DeleteIdsFromListsRequest;
import org.eurekastreams.server.domain.Follower.FollowerStatus;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.AddCachedGroupFollower;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.db.DeleteRequestForGroupMembership;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
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
     * Local instance of the GetGroupFollowerIds mapper.
     */
    private final DomainMapper<Long, List<Long>> followerIdsMapper;

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
     * @param inFollowerIdsMapper
     *            - mapper to get the follower ids for a group
     * @param inDeleteRequestForGroupMembershipMapper
     *            Mapper to remove group access requests.
     */
    public SetFollowingGroupStatusExecution(final GetDomainGroupsByShortNames inGroupMapper,
            final GetPeopleByAccountIds inPersonMapper, final DomainGroupMapper inDomainGroupMapper,
            final AddCachedGroupFollower inAddCachedGroupFollowerMapper,
            final DomainMapper<Long, List<Long>> inFollowerIdsMapper,
            final DeleteRequestForGroupMembership inDeleteRequestForGroupMembershipMapper)
    {
        groupMapper = inGroupMapper;
        personMapper = inPersonMapper;
        domainGroupMapper = inDomainGroupMapper;
        addCachedGroupFollowerMapper = inAddCachedGroupFollowerMapper;
        followerIdsMapper = inFollowerIdsMapper;
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

        switch (followerStatus)
        {
        case FOLLOWING:
            // Update the db and cache for list of followers and following.
            domainGroupMapper.addFollower(followerId, targetId);
            // Update the cache list of followers
            addCachedGroupFollowerMapper.execute(followerId, targetId);

            // Queue async action to remove the newly followed group from cache (to sync follower counts)
            taskRequests.add(new UserActionRequest("deleteCacheKeysAction", null, (Serializable) Collections
                    .singleton(CacheKeys.GROUP_BY_ID + targetId)));

            // remove any requests from the user for group membership
            deleteRequestForGroupMembershipMapper.execute(new RequestForGroupMembershipRequest(targetId, followerId));

            // Sends new follower notifications.
            CreateNotificationsRequest notificationRequest = new CreateNotificationsRequest(RequestType.GROUP_FOLLOWER,
                    followerId, targetId, 0);
            taskRequests.add(new UserActionRequest("createNotificationsAction", null, notificationRequest));
            break;
        case NOTFOLLOWING:
            // Update the db and cache for list of followers and following.
            domainGroupMapper.removeFollower(followerId, targetId);

            // Queue async action to remove the newly followed group from cache (to sync follower counts)
            taskRequests.add(new UserActionRequest("deleteCacheKeysAction", null, (Serializable) Collections
                    .singleton(CacheKeys.GROUP_BY_ID + targetId)));

            // Remove the current user that is severing a relationship with the target group
            // from the list of followers for that target group.
            taskRequests.add(new UserActionRequest("deleteIdsFromLists", null, new DeleteIdsFromListsRequest(
                    Collections.singletonList(CacheKeys.FOLLOWERS_BY_GROUP + targetId), Collections
                            .singletonList(followerId))));

            // Remove the target group the current user is now following from the list of
            // groups that the current user is already following.
            taskRequests.add(new UserActionRequest("deleteIdsFromLists", null, new DeleteIdsFromListsRequest(
                    Collections.singletonList(CacheKeys.GROUPS_FOLLOWED_BY_PERSON + followerId), Collections
                            .singletonList(targetId))));

            break;
        default:
            // nothing to do here.
        }

        inActionContext.getUserActionRequests().addAll(taskRequests);
        return followerIdsMapper.execute(targetId).size();
    }
}
