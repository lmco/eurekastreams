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
package org.eurekastreams.server.action.execution.stream;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.action.request.stream.SetActivityLikeRequest;
import org.eurekastreams.server.action.request.stream.SetActivityLikeRequest.LikeActionType;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.LikedActivity;
import org.eurekastreams.server.persistence.mappers.DeleteLikedActivity;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.IndexEntity;
import org.eurekastreams.server.persistence.mappers.InsertLikedActivity;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Action to add or remove like on activity for current user.
 * 
 */
public class SetActivityLikeExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /**
     * Mapper for adding like.
     */
    private InsertLikedActivity insertLikedActivity;

    /**
     * Mapper for removing like.
     */
    private DeleteLikedActivity deleteLikedActivity;

    /**
     * Activity mapper.
     */
    private DomainMapper<List<Long>, List<ActivityDTO>> activityMapper;

    /**
     * The entity indexer.
     */
    private IndexEntity<Activity> indexEntity;

    /**
     * Find Activity by ID mapper.
     */
    private FindByIdMapper<Activity> activityEntityMapper;

    /**
     * Constructor.
     * 
     * @param inInsertLikedActivity
     *            Mapper for liking an activity.
     * @param inDeleteLikedActivity
     *            Mapper for unliking an activity.
     * @param inActivityMapper
     *            activity mapper.
     * @param inIndexEntity
     *            the activity indexer.
     * @param inActivityEntityMapper
     *            activity entity mapper, used for indexing.
     */
    public SetActivityLikeExecution(final InsertLikedActivity inInsertLikedActivity,
            final DeleteLikedActivity inDeleteLikedActivity,
            final DomainMapper<List<Long>, List<ActivityDTO>> inActivityMapper,
            final IndexEntity<Activity> inIndexEntity, final FindByIdMapper<Activity> inActivityEntityMapper)
    {
        insertLikedActivity = inInsertLikedActivity;
        deleteLikedActivity = inDeleteLikedActivity;
        activityMapper = inActivityMapper;
        indexEntity = inIndexEntity;
        activityEntityMapper = inActivityEntityMapper;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
            throws ExecutionException
    {
        SetActivityLikeRequest request = (SetActivityLikeRequest) inActionContext.getActionContext().getParams();
        LikedActivity likeActivityData = new LikedActivity(inActionContext.getActionContext().getPrincipal().getId(),
                request.getActivityId());

        ActivityDTO activity = activityMapper.execute(Collections.singletonList(request.getActivityId())).get(0);

        if (request.getLikeActionType() == LikeActionType.ADD_LIKE)
        {
            CreateNotificationsRequest notificationRequest = new CreateNotificationsRequest(RequestType.LIKE,
                    inActionContext.getActionContext().getPrincipal().getId(), activity.getActor().getId(), request
                            .getActivityId());

            List<UserActionRequest> queuedRequests = null;
            // create list if it has not already been done.
            queuedRequests = queuedRequests == null ? new ArrayList<UserActionRequest>() : queuedRequests;

            // add UserRequest.
            queuedRequests.add(new UserActionRequest("createNotificationsAction", null, notificationRequest));

            inActionContext.getUserActionRequests().addAll(queuedRequests);

            insertLikedActivity.execute(likeActivityData);

        }
        else
        {
            deleteLikedActivity.execute(likeActivityData);
        }

        indexEntity.execute(activityEntityMapper.execute(new FindByIdRequest("Activity", activity.getId())));

        return Boolean.TRUE;

    }
}
