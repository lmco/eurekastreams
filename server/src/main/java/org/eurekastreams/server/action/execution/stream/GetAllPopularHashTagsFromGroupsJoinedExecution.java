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
import java.util.HashSet;
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.stream.StreamPopularHashTagsRequest;
import org.eurekastreams.server.domain.stream.GroupStreamDTO;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.StreamPopularHashTagsReportDTO;
import org.eurekastreams.server.service.actions.response.GetCurrentUserStreamFiltersResponse;

/**
 * Gets all the popular hashtags from all the groups the user is in and the user's.
 *
 */
public class GetAllPopularHashTagsFromGroupsJoinedExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Mapper to get the popular hashtags for a stream.
     */
    private final DomainMapper<List<StreamPopularHashTagsRequest>,
    List<StreamPopularHashTagsReportDTO>> popularHashTagsMapper;

    /**
     * Groups mapper.
     */
    private final ExecutionStrategy<PrincipalActionContext> getGroups;

    /**
     * Constructor.
     *
     * @param inPopularHashTagsMapper
     *            the mapper to get the popular hashtags for a stream
     * @param inGetGroups
     *            get the groups mapper.
     */
    public GetAllPopularHashTagsFromGroupsJoinedExecution(
            final DomainMapper<List<StreamPopularHashTagsRequest>,
            List<StreamPopularHashTagsReportDTO>> inPopularHashTagsMapper,
            final ExecutionStrategy<PrincipalActionContext> inGetGroups)
    {
        popularHashTagsMapper = inPopularHashTagsMapper;
        getGroups = inGetGroups;
    }

    /**
     * Get the popular hashtags for an activity stream.
     *
     * @param inActionContext
     *            the action context
     * @return an ArrayList of the popular hashtags
     * @throws ExecutionException
     *             on error
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        GetCurrentUserStreamFiltersResponse groupResponse = (GetCurrentUserStreamFiltersResponse) getGroups
                .execute(inActionContext);
        List<StreamPopularHashTagsRequest> requests = new ArrayList<StreamPopularHashTagsRequest>();

        for (StreamFilter filter : groupResponse.getStreamFilters())
        {
            StreamPopularHashTagsRequest request = new StreamPopularHashTagsRequest(ScopeType.GROUP,
                    ((GroupStreamDTO) filter).getShortName());
            requests.add(request);
        }

        StreamPopularHashTagsRequest personRequest = new StreamPopularHashTagsRequest(ScopeType.PERSON, inActionContext
                .getPrincipal().getAccountId());
        requests.add(personRequest);

        List<StreamPopularHashTagsReportDTO> responses = popularHashTagsMapper.execute(requests);

        HashSet<String> result = new HashSet<String>();
        for (StreamPopularHashTagsReportDTO response : responses)
        {
            if (response.getPopularHashTags() != null)
            {
                result.addAll(response.getPopularHashTags());
            }
        }

        return result;
    }
}
