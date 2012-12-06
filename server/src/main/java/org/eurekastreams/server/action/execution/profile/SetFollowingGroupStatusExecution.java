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
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.InlineExecutionStrategyExecutor;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.DefaultPrincipal;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.action.request.notification.GroupMembershipResponseNotificationsRequest;
import org.eurekastreams.server.action.request.notification.TargetEntityNotificationsRequest;
import org.eurekastreams.server.action.request.profile.RequestForGroupMembershipRequest;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusByGroupCreatorRequest;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.action.request.stream.DeleteIdsFromListsRequest;
import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.server.domain.Follower.FollowerStatus;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.ActivityVerb;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.AddCachedGroupFollower;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.db.DeleteRequestForGroupMembership;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Class responsible for providing the strategy that updates the appropriate lists when a group is followed.
 * 
 */
public class SetFollowingGroupStatusExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Local instance of the GetDomainGroupsByShortNames mapper.
     */
    private final GetDomainGroupsByShortNames groupMapper;

    /** Mapper to get person by id (for getting account id). */
    private final DomainMapper<Long, PersonModelView> getPersonByIdMapper;

    /**
     * Mapper to get the person id from an account id.
     */
    private final DomainMapper<String, Long> getPersonIdFromAccountIdMapper;

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
    private final DeleteRequestForGroupMembership deleteRequestForGroupMembershipMapper;

    /**
     * The post activity executor.
     */
    private final TaskHandlerExecutionStrategy postActivityExecutor;

    /**
     * Delete cache key mapper.
     */
    private final DomainMapper<Set<String>, Boolean> deleteCacheKeyMapper;

    /**
     * Constructor for the SetFollowingGroupStatusExecution.
     * 
     * @param inGroupMapper
     *            - instance of the GetDomainGroupsByShortNames mapper.
     * @param inGetPersonByIdMapper
     *            Mapper to get person by id (for getting account id).
     * @param inGetPersonIdFromAccountIdMapper
     *            - Mapper to get the person id from an account id
     * @param inDomainGroupMapper
     *            - instance of the DomainGroupMapper mapper.
     * @param inAddCachedGroupFollowerMapper
     *            - instance of the AddCachedGroupFollower mapper.
     * @param inFollowerIdsMapper
     *            - mapper to get the follower ids for a group
     * @param inDeleteRequestForGroupMembershipMapper
     *            Mapper to remove group access requests.
     * @param inPostActivityExecutor
     *            post executor.
     * @param inDeleteCacheKeyMapper
     *            Delete cache key mapper.
     * 
     */
    public SetFollowingGroupStatusExecution(final GetDomainGroupsByShortNames inGroupMapper,
            final DomainMapper<Long, PersonModelView> inGetPersonByIdMapper,
            final DomainMapper<String, Long> inGetPersonIdFromAccountIdMapper,
            final DomainGroupMapper inDomainGroupMapper, final AddCachedGroupFollower inAddCachedGroupFollowerMapper,
            final DomainMapper<Long, List<Long>> inFollowerIdsMapper,
            final DeleteRequestForGroupMembership inDeleteRequestForGroupMembershipMapper,
            final TaskHandlerExecutionStrategy inPostActivityExecutor,
            final DomainMapper<Set<String>, Boolean> inDeleteCacheKeyMapper)
    {
        groupMapper = inGroupMapper;
        getPersonByIdMapper = inGetPersonByIdMapper;
        getPersonIdFromAccountIdMapper = inGetPersonIdFromAccountIdMapper;
        domainGroupMapper = inDomainGroupMapper;
        addCachedGroupFollowerMapper = inAddCachedGroupFollowerMapper;
        followerIdsMapper = inFollowerIdsMapper;
        deleteRequestForGroupMembershipMapper = inDeleteRequestForGroupMembershipMapper;
        postActivityExecutor = inPostActivityExecutor;
        deleteCacheKeyMapper = inDeleteCacheKeyMapper;
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
        String followerAccountId = null;
        Long targetId;
        FollowerStatus followerStatus;
        String targetName;
        boolean isPending = false;
        List<UserActionRequest> asyncRequests = inActionContext.getUserActionRequests();
        final Serializable params = inActionContext.getActionContext().getParams();

        // this switching here is a hold over until the GroupCreator can be refactored to call this strategy
        // and not fail because of additional mapper calls on the DomainGroupModelView and PersonModelView objects.
        if (params instanceof SetFollowingStatusRequest)
        {
            SetFollowingStatusRequest currentRequest = (SetFollowingStatusRequest) params;
            followerStatus = currentRequest.getFollowerStatus();
            followerAccountId = currentRequest.getFollowerUniqueId();

            // gets the current user if no follower id was passed in.
            if (followerAccountId == null || followerAccountId == "")
            {
                followerAccountId = inActionContext.getActionContext().getPrincipal().getAccountId();
            }

            followerId = getPersonIdFromAccountIdMapper.execute(followerAccountId);
            DomainGroupModelView targetResult = groupMapper.fetchUniqueResult(currentRequest.getTargetUniqueId());
            targetName = targetResult.getName();
            targetId = targetResult.getEntityId();
        }
        else if (params instanceof SetFollowingStatusByGroupCreatorRequest)
        {
            SetFollowingStatusByGroupCreatorRequest currentRequest = // \n
            (SetFollowingStatusByGroupCreatorRequest) params;
            followerId = currentRequest.getFollowerId();
            targetId = currentRequest.getTargetId();
            targetName = currentRequest.getTargetName();
            followerStatus = currentRequest.getFollowerStatus();
            isPending = currentRequest.isPending();

            if (Follower.FollowerStatus.FOLLOWING.equals(followerStatus) && !isPending)
            {
                followerAccountId = getPersonByIdMapper.execute(followerId).getAccountId();
            }
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
            asyncRequests.add(new UserActionRequest("deleteCacheKeysAction", null, (Serializable) Collections
                    .singleton(CacheKeys.GROUP_BY_ID + targetId)));

            // remove any requests from the user for group membership
            if (deleteRequestForGroupMembershipMapper
                    .execute(new RequestForGroupMembershipRequest(targetId, followerId)))
            {
                // if any requests were present, then user was just approved for access
                asyncRequests.add(new UserActionRequest(CreateNotificationsRequest.ACTION_NAME, null,
                        new GroupMembershipResponseNotificationsRequest(RequestType.REQUEST_GROUP_ACCESS_APPROVED,
                                inActionContext.getActionContext().getPrincipal().getId(), targetId, followerId)));
            }

            // remove person modelview from cache as groupstreamhiddenlineindex will be changed.
            deleteCacheKeyMapper.execute(Collections.singleton(CacheKeys.PERSON_BY_ID + followerId));

            // Sends new follower notifications.
            asyncRequests.add(new UserActionRequest(CreateNotificationsRequest.ACTION_NAME, null,
                    new TargetEntityNotificationsRequest(RequestType.FOLLOW_GROUP, followerId, targetId)));

            // Posts a message to the user's personal stream unless this is a new pending group
            if (!isPending)
            {
                String targetStream = "";

                if (params instanceof SetFollowingStatusRequest)
                {
                    SetFollowingStatusRequest currentRequest = (SetFollowingStatusRequest) params;
                    targetStream = currentRequest.getTargetUniqueId();
                }
                else if (params instanceof SetFollowingStatusByGroupCreatorRequest)
                {
                    SetFollowingStatusByGroupCreatorRequest currentRequest = // \n
                    (SetFollowingStatusByGroupCreatorRequest) params;
                    targetStream = currentRequest.getTargetUniqueId();
                }

                StreamEntityDTO destination = new StreamEntityDTO();
                destination.setUniqueIdentifier(followerAccountId);
                destination.setType(EntityType.PERSON);

                ActivityDTO activity = new ActivityDTO();
                HashMap<String, String> props = new HashMap<String, String>();
                activity.setBaseObjectProperties(props);

                String content = "";

                if (targetStream.length() > 0)
                {
                    content = "%EUREKA:ACTORNAME% is now following the [" + targetName + "](#activity/group/"
                            + targetStream + ") group";
                }
                else
                {
                    content = "%EUREKA:ACTORNAME% is now following the " + targetName + " group";
                }

                activity.getBaseObjectProperties().put("content", content);
                activity.setDestinationStream(destination);
                activity.setBaseObjectType(BaseObjectType.NOTE);
                activity.setVerb(ActivityVerb.POST);

                // Note: create a principal for the follower: we want to post on the follower's stream as the
                // follower.
                // The current principal will be different from the follower in some cases, namely when following a
                // private group (the current principal / actor is the coordinator who approved access).
                new InlineExecutionStrategyExecutor().execute(postActivityExecutor, new PostActivityRequest(activity),
                        new DefaultPrincipal(followerAccountId, null, followerId),
                        inActionContext.getUserActionRequests());
            }
            break;

        case NOTFOLLOWING:

            // Check if the User to be removed is a Group Coordinator.
            boolean isToBeRemovedUserGroupCoordinator = domainGroupMapper.isInputUserGroupCoordinator(followerId,
                    targetId);

            // Do not remove the last Group Coordinator.
            if ((domainGroupMapper.getGroupCoordinatorCount(targetId) == 1) && isToBeRemovedUserGroupCoordinator)
            {
                log.error("Cannot remove followerId: " + followerId + " " + "from targetId:" + targetId
                        + " since there's " + "only a single Group Coordinator remaining " + "in the Group");
                throw new ExecutionException("Cannot remove followerId: " + followerId + " " + "from targetId:"
                        + targetId + " since there's " + "only a single Group Coordinator remaining " + "in the Group");
            }

            // Update the db for list of followers and following.
            domainGroupMapper.removeFollower(followerId, targetId);

            // Queue async action to remove the newly followed group from cache (to sync follower counts)
            asyncRequests.add(new UserActionRequest("deleteCacheKeysAction", null, (Serializable) Collections
                    .singleton(CacheKeys.GROUP_BY_ID + targetId)));

            // Remove the current user that is severing a relationship with the target group
            // from the list of followers for that target group.
            asyncRequests.add(new UserActionRequest("deleteIdsFromLists", null, new DeleteIdsFromListsRequest(
                    Collections.singletonList(CacheKeys.FOLLOWERS_BY_GROUP + targetId), Collections
                            .singletonList(followerId))));

            // Remove the target group the current user is now following from the list of
            // groups that the current user is already following.
            asyncRequests.add(new UserActionRequest("deleteIdsFromLists", null, new DeleteIdsFromListsRequest(
                    Collections.singletonList(CacheKeys.GROUPS_FOLLOWED_BY_PERSON + followerId), Collections
                            .singletonList(targetId))));

            if (isToBeRemovedUserGroupCoordinator)
            {
                // delete group coordinator from db
                domainGroupMapper.removeGroupCoordinator(followerId, targetId);

                // queue the removal of the target group's coordinator
                asyncRequests.add(new UserActionRequest("deleteCacheKeysAction", null, (Serializable) Collections
                        .singleton(CacheKeys.COORDINATOR_PERSON_IDS_BY_GROUP_ID + targetId)));

                // Update the 'PRIVATE_GROUP_IDS_VIEWABLE_BY_PERSON_AS_COORDINATOR' cache if Private Group
                if (domainGroupMapper.isGroupPrivate(targetId))
                {
                    // queue the removal the person's list of followed group ids
                    asyncRequests.add(new UserActionRequest("deleteCacheKeysAction", null, (Serializable) Collections
                            .singleton(CacheKeys.PRIVATE_GROUP_IDS_VIEWABLE_BY_PERSON_AS_COORDINATOR + followerId)));
                }
            }

            break;

        default:
            // nothing to do here.
        }

        return followerIdsMapper.execute(targetId).size();
    }

    /**
     * Creates a principal for the given user's id and account id.
     * 
     * @param followerId
     *            Person id.
     * @param followerAccountId
     *            Person account id.
     * @return Principal.
     */
    private Principal createPrincipal(final Long followerId, final String followerAccountId)
    {
        return new DefaultPrincipal(followerAccountId, null, followerId);
    }

    /**
     * Checks whether the given user is a Group Coordinator.
     * 
     * @param userAccountId
     *            Account Id of User to check whether he/she's a Group Coordinator
     * @param group
     *            Group to check if userAccountId is a Group Coordinator for it
     * 
     * @return Whether the input user is a Group Coordinator
     */
    private boolean isUserGroupCoordinator(final String userAccountId, final DomainGroupModelView group)
    {
        List<PersonModelView> groupCoordinators = group.getCoordinators();

        for (PersonModelView p : groupCoordinators)
        {
            if (p.getAccountId().equals(userAccountId))
            {
                return true;
            }
        }

        return false;
    }

}
