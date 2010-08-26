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
package org.eurekastreams.server.service.restlets;

import java.util.Arrays;
import java.util.List;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.PrincipalPopulator;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.server.service.ActionController;
import org.eurekastreams.server.action.request.stream.GetActivitiesByCompositeStreamRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * Fetches activities for a group stream.
 * 
 */
public class GroupStreamActivityFetcher implements StreamFilterFetcher
{
    /**
     * Action.
     */
    private ServiceAction action;

    /**
     * Service Action Controller.
     */
    private ActionController serviceActionController;

    /**
     * Principal populator.
     */
    private PrincipalPopulator principalPopulator;

    /**
     * Mapper to get domain group model view by group id.
     */
    private GetDomainGroupsByIds groupMapper;

    /**
     * Default constructor.
     * 
     * @param inAction
     *            the action.
     * @param inServiceActionController
     *            {@link ActionController} used to execute action.
     * @param inPrincipalPopulator
     *            {@link PrincipalPopulator} used to create principal via open social id.
     * @param inGroupMapper
     *            {@link GetDomainGroupsByIds} used to find the group's composite stream id.
     */
    public GroupStreamActivityFetcher(final ServiceAction inAction, final ActionController inServiceActionController,
            final PrincipalPopulator inPrincipalPopulator, final GetDomainGroupsByIds inGroupMapper)
    {
        action = inAction;
        serviceActionController = inServiceActionController;
        principalPopulator = inPrincipalPopulator;
        groupMapper = inGroupMapper;
    }

    /**
     * Get the activities.
     * 
     * @param id
     *            the id of the group stream.
     * @param openSocialId
     *            the open social id of the user.
     * @param maxCount
     *            the number of activities to return
     * @return the activity DTOs.
     * @throws Exception
     *             exception.
     */
    @SuppressWarnings("unchecked")
    public PagedSet<ActivityDTO> getActivities(final Long id, final String openSocialId, final int maxCount)
            throws Exception
    {
        // finds the composite stream id for the group.
        List<DomainGroupModelView> groups = groupMapper.execute(Arrays.asList(id));

        if (groups == null || groups.size() == 0)
        {
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
        }

        long streamId = groups.get(0).getCompositeStreamId();

        // create the request
        GetActivitiesByCompositeStreamRequest request = new GetActivitiesByCompositeStreamRequest(streamId, maxCount);
        request.setMaxActivityId(Long.MAX_VALUE);

        // Create the actionContext
        PrincipalActionContext ac = new ServiceActionContext(request, principalPopulator.getPrincipal(openSocialId));

        // execute action and return results.
        return (PagedSet<ActivityDTO>) serviceActionController.execute((ServiceActionContext) ac, action);
    }
}
