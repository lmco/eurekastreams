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
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamSearch;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.stream.BulkCompositeStreamSearchesMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByIds;
import org.eurekastreams.server.persistence.mappers.stream.UserCompositeStreamSearchIdsMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.response.GetCurrentUserStreamFiltersResponse;

/**
 * Execution strategy to get all composite stream searches for the current user.
 */
public class GetCurrentUserCompositeStreamSearchesExecutionStrategy implements
        ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Mapper to get a list of IDs for composite stream searches for a user.
     */
    private UserCompositeStreamSearchIdsMapper idsMapper;

    /**
     * Mapper to get the actual composite stream search.
     */
    private BulkCompositeStreamSearchesMapper bulkStreamSearchesMapper;

    /**
     * Mapper to get a person.
     */
    private GetPeopleByIds peopleMapper;

    /**
     * Mapper to get organization details.
     */
    private GetOrganizationsByShortNames orgsMapper;

    /**
     * Mapper to get group details.
     */
    private GetDomainGroupsByShortNames groupsMapper;

    /**
     * Constructor.
     * 
     * @param inIdsMapper
     *            the ids mapper.
     * @param inBulkStreamSearchesMapper
     *            the bulk activities mapper.
     * @param inPeopleMapper
     *            the people mapper.
     * @param inOrgsMapper
     *            the organizations mapper.
     * @param inGroupsMapper
     *            the groups mapper.
     */
    public GetCurrentUserCompositeStreamSearchesExecutionStrategy(final UserCompositeStreamSearchIdsMapper inIdsMapper,
            final BulkCompositeStreamSearchesMapper inBulkStreamSearchesMapper, final GetPeopleByIds inPeopleMapper,
            final GetOrganizationsByShortNames inOrgsMapper, final GetDomainGroupsByShortNames inGroupsMapper)
    {
        bulkStreamSearchesMapper = inBulkStreamSearchesMapper;
        idsMapper = inIdsMapper;
        peopleMapper = inPeopleMapper;
        orgsMapper = inOrgsMapper;
        groupsMapper = inGroupsMapper;
    }

    @Override
    public Serializable execute(final PrincipalActionContext inActionContext)
    {
        long personId = inActionContext.getPrincipal().getId();

        // Gets the list of composite stream searches ids for this user
        List<Long> searchKeys = idsMapper.execute(personId);

        // Get the current person's PersonModelView
        PersonModelView currentUserModelView = peopleMapper.execute(personId);

        GetCurrentUserStreamFiltersResponse response = new GetCurrentUserStreamFiltersResponse(currentUserModelView
                .getCompositeStreamSearchHiddenLineIndex(), bulkStreamSearchesMapper.execute(searchKeys));

        for (StreamFilter s : response.getStreamFilters())
        {
            StreamSearch search = (StreamSearch) s;
            List<StreamScope> scopes = new ArrayList<StreamScope>(search.getStreamView().getIncludedScopes());
            if (StreamView.PARENT_ORG_TAG.compareTo(search.getStreamView().getName()) == 0)
            {
                search.getStreamView().setName(currentUserModelView.getParentOrganizationName());
            }
        }

        return response;
    }

}
