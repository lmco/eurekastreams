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

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.stream.BulkCompositeStreamsMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByIds;
import org.eurekastreams.server.persistence.mappers.stream.UserCompositeStreamIdsMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.response.GetCurrentUserStreamFiltersResponse;

/**
 * ExecutionStrategy to get all composite streams for the current user.
 */
public class GetCurrentUserCompositeStreamsExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Mapper to get a list of IDs for composite streams for a user.
     */
    private UserCompositeStreamIdsMapper idsMapper;

    /**
     * Mapper to get the actual composite stream.
     */
    private BulkCompositeStreamsMapper bulkStreamsMapper;

    /**
     * Mapper to get a person.
     */
    private GetPeopleByIds peopleMapper;

    /**
     * Constructor.
     *
     * @param inIdsMapper
     *            the ids mapper.
     * @param inBulkStreamsMapper
     *            the bulk activities mapper.
     * @param inPeopleMapper
     *            the people mapper.
     */
    public GetCurrentUserCompositeStreamsExecution(final UserCompositeStreamIdsMapper inIdsMapper,
            final BulkCompositeStreamsMapper inBulkStreamsMapper, final GetPeopleByIds inPeopleMapper)
    {
        bulkStreamsMapper = inBulkStreamsMapper;
        idsMapper = inIdsMapper;
        peopleMapper = inPeopleMapper;
    }

    /**
     * {@inheritDoc}.
     *
     * Get the list of composite streams for a user.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        // Gets the list of composite stream ids for this user
        List<Long> streamKeys = idsMapper.execute(inActionContext.getPrincipal().getId());

        List<Long> personIds = new ArrayList<Long>();
        personIds.add(inActionContext.getPrincipal().getId());
        List<PersonModelView> people = peopleMapper.execute(personIds);
        PersonModelView currentUser = people.get(0);

        // Returns the desired filters
        GetCurrentUserStreamFiltersResponse response = new GetCurrentUserStreamFiltersResponse(currentUser
                .getCompositeStreamHiddenLineIndex(), bulkStreamsMapper.execute(streamKeys));

        for (StreamFilter s : response.getStreamFilters())
        {
            if (StreamView.PARENT_ORG_TAG.compareTo(s.getName()) == 0)
            {
                s.setName(currentUser.getParentOrganizationName());
            }

            //pull out streamView scopes as they are not needed by the client
            if (s instanceof StreamView)
            {
                ((StreamView) s).setIncludedScopes(null);
            }

        }
        return response;
    }

}
