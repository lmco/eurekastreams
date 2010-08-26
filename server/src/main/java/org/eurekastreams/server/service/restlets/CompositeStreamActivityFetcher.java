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
package org.eurekastreams.server.service.restlets;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.PrincipalPopulator;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.server.service.ActionController;
import org.eurekastreams.server.action.request.stream.GetActivitiesByCompositeStreamRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * Fetches activities for a composite stream.
 * 
 */
public class CompositeStreamActivityFetcher implements StreamFilterFetcher
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
     * Find by ID Mapper to find the stream.
     */
    private final FindByIdMapper<StreamView> findByIdMapper;

    /**
     * Default constructor.
     * 
     * @param inAction
     *            the action.
     * @param inServiceActionController
     *            {@link ActionController} used to execute action.
     * @param inPrincipalPopulator
     *            {@link PrincipalPopulator} used to create principal via open social id.
     * @param inFindByIdMapper
     *            the find mapper.
     */
    @SuppressWarnings("unchecked")
    public CompositeStreamActivityFetcher(final ServiceAction inAction,
            final ActionController inServiceActionController, final PrincipalPopulator inPrincipalPopulator,
            final FindByIdMapper inFindByIdMapper)
    {
        action = inAction;
        serviceActionController = inServiceActionController;
        principalPopulator = inPrincipalPopulator;
        findByIdMapper = inFindByIdMapper;
    }

    /**
     * Get the activities.
     * 
     * @param id
     *            the id of the composite stream.
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
        // checks to see if composite stream is still valid
        FindByIdRequest findByIdRequest = new FindByIdRequest("StreamView", id);
        StreamView stream = findByIdMapper.execute(findByIdRequest);

        if (stream == null)
        {
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
        }

        // create the request
        GetActivitiesByCompositeStreamRequest request = new GetActivitiesByCompositeStreamRequest(id, maxCount);
        request.setMaxActivityId(Long.MAX_VALUE);

        // Create the actionContext
        PrincipalActionContext ac = new ServiceActionContext(request, principalPopulator.getPrincipal(openSocialId));

        // execute action and return results.
        return (PagedSet<ActivityDTO>) serviceActionController.execute((ServiceActionContext) ac, action);
    }
}
