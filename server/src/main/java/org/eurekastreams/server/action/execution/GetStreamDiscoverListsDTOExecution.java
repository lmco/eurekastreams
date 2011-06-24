/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.dto.StreamDTO;
import org.eurekastreams.server.domain.dto.StreamDiscoverListsDTO;
import org.eurekastreams.server.persistence.comparators.StreamDTOFollowerCountDescendingComparator;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.SuggestedStreamsRequest;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Execution strategy to get suggested group and people streams for a user.
 */
public class GetStreamDiscoverListsDTOExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Mapper to get suggested people streams.
     */
    private DomainMapper<SuggestedStreamsRequest, List<PersonModelView>> suggestedPersonMapper;

    /**
     * Mapper to get suggested group streams.
     */
    private DomainMapper<SuggestedStreamsRequest, List<DomainGroupModelView>> suggestedGroupMapper;

    /**
     * Mapper to get the stream discovery lists that are the same for everyone.
     */
    private DomainMapper<Serializable, StreamDiscoverListsDTO> streamDiscoveryListsMapper;

    /**
     * Constructor.
     * 
     * @param inSuggestedPersonMapper
     *            mapper to get suggested people streams
     * @param inSuggestedGroupMapper
     *            mapper to get suggested group streams
     * @param inStreamDiscoveryListsMapper
     *            mapper to get the stream discovery lists that are the same for everyone
     */
    public GetStreamDiscoverListsDTOExecution(
            final DomainMapper<SuggestedStreamsRequest, List<PersonModelView>> inSuggestedPersonMapper,
            final DomainMapper<SuggestedStreamsRequest, List<DomainGroupModelView>> inSuggestedGroupMapper,
            final DomainMapper<Serializable, StreamDiscoverListsDTO> inStreamDiscoveryListsMapper)
    {
        suggestedPersonMapper = inSuggestedPersonMapper;
        suggestedGroupMapper = inSuggestedGroupMapper;
        streamDiscoveryListsMapper = inStreamDiscoveryListsMapper;
    }

    /**
     * Get the StreamDiscoverListsDTO for the current user, which includes data for all users along with suggestions for
     * the current user. Integer representing how many suggestions to get
     * 
     * @param inActionContext
     *            the action context
     * @return StreamDiscoverListsDTO representing all of the discover page lists and the featured streams.
     * @throws ExecutionException
     *             (never)
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        Long personId = inActionContext.getPrincipal().getId();
        Integer suggestionCount = (Integer) inActionContext.getParams();

        StreamDiscoverListsDTO result = streamDiscoveryListsMapper.execute(null);
        getSuggestionsForPerson(personId, suggestionCount, result);

        return result;
    }

    /**
     * Get the suggested streams for the current user, and populate them in the input StreamDiscoverListsDTO.
     * 
     * @param inPersonId
     *            the person id to fetch suggested streams for
     * @param inSuggestionCount
     *            the number of suggestions to fetch
     * @param inStreamDiscoverLists
     *            the StreamDiscoverListsDTO to add the results to
     */
    private void getSuggestionsForPerson(final Long inPersonId, final Integer inSuggestionCount,
            final StreamDiscoverListsDTO inStreamDiscoverLists)
    {
        SuggestedStreamsRequest mapperRequest = new SuggestedStreamsRequest(inPersonId, inSuggestionCount.intValue());
        ArrayList<StreamDTO> suggestions = new ArrayList<StreamDTO>();

        suggestions.addAll(suggestedPersonMapper.execute(mapperRequest));
        suggestions.addAll(suggestedGroupMapper.execute(mapperRequest));

        // sort the list
        Collections.sort(suggestions, new StreamDTOFollowerCountDescendingComparator());

        // return those requested
        suggestions = new ArrayList<StreamDTO>(suggestions.subList(0, inSuggestionCount));
        inStreamDiscoverLists.setSuggestedStreams(suggestions);
    }
}
