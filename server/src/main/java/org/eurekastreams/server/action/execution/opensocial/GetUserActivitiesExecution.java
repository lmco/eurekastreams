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
package org.eurekastreams.server.action.execution.opensocial;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.opensocial.GetUserActivitiesRequest;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.persistence.mappers.stream.BulkActivitiesMapper;
import org.eurekastreams.server.persistence.mappers.stream.CompositeStreamActivityIdsMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByOpenSocialIds;
import org.eurekastreams.server.persistence.mappers.stream.GetStreamByOwnerId;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * This Execution strategy is responsible for retrieving activities for the opensocial adapter. There are two ways to
 * get activities back from this strategy: - Pass in a list of activity ids and the corresponding ActivityDTO objects
 * will be returned. - Pass in just the user id and all of the activities that user posted to their own stream will be
 * returned.
 *
 */
public class GetUserActivitiesExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Local instance of the {@link GetStreamByOwnerId} mapper.
     */
    private final GetStreamByOwnerId getStreamByOwnerIdMapper;

    /**
     * Local instance of the {@link BulkActivitiesMapper}.
     */
    private final BulkActivitiesMapper bulkActivitiesMapper;

    /**
     * Local instance of the {@link CompositeStreamActivityIdsMapper} used to retrieve the list of activity ids for the
     * user's personal composite stream (the wall).
     */
    private final CompositeStreamActivityIdsMapper compositeStreamActivityIdsMapper;

    /**
     * Local instance of the {@link GetPeopleByOpenSocialIds} mapper.
     */
    private final GetPeopleByOpenSocialIds getPeopleByOpenSocialIds;

    /**
     * Constructor.
     *
     * @param inGetStreamByOwnerId
     *            - instance of the {@link GetStreamByOwnerId} mapper.
     * @param inBulkActivitiesMapper
     *            - instance of the {@link BulkActivitiesMapper}.
     * @param inCompositeStreamActivityIdsMapper
     *            - instance of the {@link CompositeStreamActivityIdsMapper}.
     * @param inGetPeopleByOpenSocialIds
     *            - instance of the {@link GetPeopleByOpenSocialIds} mapper.
     */
    public GetUserActivitiesExecution(final GetStreamByOwnerId inGetStreamByOwnerId,
            final BulkActivitiesMapper inBulkActivitiesMapper,
            final CompositeStreamActivityIdsMapper inCompositeStreamActivityIdsMapper,
            final GetPeopleByOpenSocialIds inGetPeopleByOpenSocialIds)
    {
        getStreamByOwnerIdMapper = inGetStreamByOwnerId;
        bulkActivitiesMapper = inBulkActivitiesMapper;
        compositeStreamActivityIdsMapper = inCompositeStreamActivityIdsMapper;
        getPeopleByOpenSocialIds = inGetPeopleByOpenSocialIds;
    }

    /**
     * {@inheritDoc}
     *
     * This execute method retrieves the ActivityDTO objects for the parameters passed in.
     */
    @Override
    public LinkedList<ActivityDTO> execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        List<Long> activityIds = new ArrayList<Long>();
        GetUserActivitiesRequest currentRequest = (GetUserActivitiesRequest) inActionContext.getParams();
        LinkedList<ActivityDTO> currentActivityDTOs = new LinkedList<ActivityDTO>();
        // if the user has provided activities to retrieve in the parameters, retrieve the corresponding
        // ActivityDTO objects.
        if (currentRequest.getActivityIds().size() > 0)
        {
            activityIds.addAll(currentRequest.getActivityIds());
        }

        if (currentRequest.getOpenSocialIds().size() > 0)
        {
            List<PersonModelView> currentUsers = getPeopleByOpenSocialIds.execute(new ArrayList<String>(currentRequest
                    .getOpenSocialIds()));

            for (PersonModelView currentUser : currentUsers)
            {
                StreamFilter currentUserStreamView = getStreamByOwnerIdMapper.execute(currentUser.getEntityId());

                activityIds.addAll(compositeStreamActivityIdsMapper.execute(currentUserStreamView.getId(), currentUser
                        .getEntityId()));

                currentActivityDTOs.addAll(bulkActivitiesMapper.execute(activityIds, currentUser.getAccountId()));
            }
        }

        return currentActivityDTOs;
    }

}
