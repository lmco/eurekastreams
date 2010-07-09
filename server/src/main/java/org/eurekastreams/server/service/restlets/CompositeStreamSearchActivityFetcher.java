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

import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.actions.service.ServiceAction;
import org.eurekastreams.commons.server.service.ServiceActionController;
import org.eurekastreams.server.action.principal.OpenSocialPrincipalPopulator;
import org.eurekastreams.server.action.request.stream.GetStreamSearchResultsRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * Fetcher for stream searches.
 *
 */
public class CompositeStreamSearchActivityFetcher implements StreamFilterFetcher
{
    /**
     * Action.
     */
    private final ServiceAction action;

    /**
     * Local instance of the {@link OpenSocialPrincipalPopulator}.
     */
    private final OpenSocialPrincipalPopulator principalPopulator;

    /**
     * Local instance of the {@link ServiceActionController}.
     */
    private final ServiceActionController serviceActionController;

    /**
     * Find by ID Mapper to find the search.
     */
    private final FindByIdMapper<StreamSearch> findByIdMapper;

    /**
     * Default constructor.
     *
     * @param inAction
     *            the action.
     * @param inPrincipalPopulator
     *            instance of {@link OpenSocialPrincipalPopulator} for this restlet.
     * @param inServiceActionController
     *            instance of the {@link ServiceActionController} for this restlet.
     * @param inFindByIdMapper
     *            the find mapper.
     */
    @SuppressWarnings("unchecked")
    public CompositeStreamSearchActivityFetcher(final ServiceAction inAction,
            final OpenSocialPrincipalPopulator inPrincipalPopulator,
            final ServiceActionController inServiceActionController, final FindByIdMapper inFindByIdMapper)
    {
        action = inAction;
        principalPopulator = inPrincipalPopulator;
        findByIdMapper = inFindByIdMapper;
        serviceActionController = inServiceActionController;
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
     *             exception
     */
    @SuppressWarnings("unchecked")
    public PagedSet<ActivityDTO> getActivities(final Long id, final String openSocialId, final int maxCount)
            throws Exception
    {
        FindByIdRequest findByIdRequest = new FindByIdRequest("StreamSearch", id);
        StreamSearch search = findByIdMapper.execute(findByIdRequest);
        
        if (search == null)
        {
            throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
        }

        String keywords = search.getKeywordsAsString();

        GetStreamSearchResultsRequest request = new GetStreamSearchResultsRequest(search.getStreamView().getId(),
                keywords, maxCount, 0L);

        ServiceActionContext currentContext = new ServiceActionContext(request, principalPopulator
                .getPrincipal(openSocialId));

        return (PagedSet<ActivityDTO>) serviceActionController.execute(currentContext, action);

    }
}
