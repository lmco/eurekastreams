/*
 * Copyright (c) 2011-2012 Lockheed Martin Corporation
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.util.CollectionListAdapter;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Follower.FollowerStatus;
import org.eurekastreams.server.domain.dto.DisplayInfoSettable;
import org.eurekastreams.server.domain.dto.StreamDTO;
import org.eurekastreams.server.domain.dto.StreamDiscoverListsDTO;
import org.eurekastreams.server.domain.strategies.DisplayInfoSettableDataPopulator;
import org.eurekastreams.server.domain.strategies.FollowerStatusPopulator;
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
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Mapper to get suggested people streams.
     */
    private final DomainMapper<SuggestedStreamsRequest, List<PersonModelView>> suggestedPersonMapper;

    /**
     * Mapper to get suggested group streams.
     */
    private final DomainMapper<SuggestedStreamsRequest, List<DomainGroupModelView>> suggestedGroupMapper;

    /**
     * Mapper to get the stream discovery lists that are the same for everyone.
     */
    private final DomainMapper<Serializable, StreamDiscoverListsDTO> streamDiscoveryListsMapper;

    /**
     * Data populator for setting the DisplayName and avatar id on DisplayInfoSettables.
     */
    private final DisplayInfoSettableDataPopulator displayInfoSettableDataPopulator;

    /**
     * {@link FollowerStatusPopulator}.
     */
    private final FollowerStatusPopulator<DisplayInfoSettable> followerStatusPopulator;

    /**
     * Mapper to get a list of PersonModelViews from a list of AccountIds.
     */
    private final DomainMapper<List<Long>, List<PersonModelView>> getPersonModelViewsByIdsDAO;

    /**
     * The number of stream suggestions to get.
     */
    final int suggestionCount;

    /**
     * Constructor.
     *
     * @param inSuggestedPersonMapper
     *            mapper to get suggested people streams
     * @param inSuggestedGroupMapper
     *            mapper to get suggested group streams
     * @param inSuggestionCount
     *            the number of suggested streams to get
     * @param inStreamDiscoveryListsMapper
     *            mapper to get the stream discovery lists that are the same for everyone
     * @param inDisplayInfoSettableDataPopulator
     *            data populator for setting the DisplayName and avatar id on DisplayInfoSettables
     * @param inFollowerStatusPopulator
     *            list of DisplayInfoSettable.
     * @param inGetPersonModelViewsByIdsDAO
     *            Mapper to get a list of PersonModelViews from a list of AccountIds.
     */
    public GetStreamDiscoverListsDTOExecution(
            final DomainMapper<SuggestedStreamsRequest, List<PersonModelView>> inSuggestedPersonMapper,
            final DomainMapper<SuggestedStreamsRequest, List<DomainGroupModelView>> inSuggestedGroupMapper,
            final int inSuggestionCount,
            final DomainMapper<Serializable, StreamDiscoverListsDTO> inStreamDiscoveryListsMapper,
            final DisplayInfoSettableDataPopulator inDisplayInfoSettableDataPopulator,
            final FollowerStatusPopulator<DisplayInfoSettable> inFollowerStatusPopulator,
            final DomainMapper<List<Long>, List<PersonModelView>> inGetPersonModelViewsByIdsDAO)
    {
        suggestedPersonMapper = inSuggestedPersonMapper;
        suggestedGroupMapper = inSuggestedGroupMapper;
        suggestionCount = inSuggestionCount;
        streamDiscoveryListsMapper = inStreamDiscoveryListsMapper;
        displayInfoSettableDataPopulator = inDisplayInfoSettableDataPopulator;
        followerStatusPopulator = inFollowerStatusPopulator;
        getPersonModelViewsByIdsDAO = inGetPersonModelViewsByIdsDAO;
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

        log.info("BEGIN getting the lists of streams that apply to all users.");
        StreamDiscoverListsDTO result = streamDiscoveryListsMapper.execute(null);
        log.info("END getting the lists of streams that apply to all users.");

        log.info("BEGIN getting the list of suggested streams for user " + personId);
        getSuggestionsForPerson(personId, result);
        log.info("END getting the list of suggested streams for user " + personId);

        // put all of the streams in a single list for transient data population
        List<DisplayInfoSettable> displayInfoSettables = new ArrayList<DisplayInfoSettable>();
        displayInfoSettables.addAll(result.getFeaturedStreams());
        displayInfoSettables.addAll(result.getMostFollowedStreams());
        displayInfoSettables.addAll(result.getMostRecentStreams());
        displayInfoSettables.addAll(result.getMostViewedStreams());
        displayInfoSettables.addAll(result.getSuggestedStreams());
        displayInfoSettables.addAll(result.getMostActiveStreams().getResultsSublist());

        // determine list of allowed people (not locked/deactivated)
        Set<Long> allowedPeople = getAllowedPeopleList(displayInfoSettables);
        // remove all unallowed people from lists
        filterList(result.getFeaturedStreams(), allowedPeople);
        filterList(result.getMostFollowedStreams(), allowedPeople);
        filterList(result.getMostRecentStreams(), allowedPeople);
        filterList(result.getMostViewedStreams(), allowedPeople);
        filterList(result.getSuggestedStreams(), allowedPeople);
        int oldCount = result.getMostActiveStreams().getResultsSublist().size();
        filterList(result.getMostActiveStreams().getResultsSublist(), allowedPeople);
        result.getMostActiveStreams().setTotalResultsCount(
                result.getMostActiveStreams().getTotalResultsCount()
                        - (oldCount - result.getMostActiveStreams().getResultsSublist().size()));

        // fill in the avatars and display names of all of the StreamDTOs
        log.info("BEGIN setting the display info on " + displayInfoSettables.size()
                + " GroupModelViews and PersonModelViews");
        displayInfoSettableDataPopulator.execute(displayInfoSettables);
        log.info("END setting the display info on " + displayInfoSettables.size()
                + " GroupModelViews and PersonModelViews");

        // Set follower status on all dtos.
        log.info("BEGIN setting the follower statuses on " + displayInfoSettables.size()
                + " GroupModelViews and PersonModelViews");
        followerStatusPopulator.execute(personId, displayInfoSettables, FollowerStatus.NOTFOLLOWING);
        log.info("END setting the follower statuses on " + displayInfoSettables.size()
                + " GroupModelViews and PersonModelViews");
        return result;
    }

    /**
     * Get the suggested streams for the current user, and populate them in the input StreamDiscoverListsDTO.
     *
     * @param inPersonId
     *            the person id to fetch suggested streams for
     * @param inStreamDiscoverLists
     *            the StreamDiscoverListsDTO to add the results to
     */
    private void getSuggestionsForPerson(final Long inPersonId, final StreamDiscoverListsDTO inStreamDiscoverLists)
    {
        SuggestedStreamsRequest mapperRequest = new SuggestedStreamsRequest(inPersonId, suggestionCount);
        ArrayList<StreamDTO> suggestions = new ArrayList<StreamDTO>();

        suggestions.addAll(suggestedPersonMapper.execute(mapperRequest));
        suggestions.addAll(suggestedGroupMapper.execute(mapperRequest));

        // sort the list
        Collections.sort(suggestions, new StreamDTOFollowerCountDescendingComparator());

        // return those requested
        if (suggestions.size() > suggestionCount)
        {
            suggestions = new ArrayList<StreamDTO>(suggestions.subList(0, suggestionCount));
        }
        inStreamDiscoverLists.setSuggestedStreams(suggestions);
    }

    /**
     * Given the list of streams, determines which person streams are allowed to be shown.
     *
     * @param displayInfoSettables
     *            List of streams.
     * @return Which person streams are allowed to be shown.
     */
    private Set<Long> getAllowedPeopleList(final List<DisplayInfoSettable> displayInfoSettables)
    {
        Set<Long> ids = new HashSet<Long>();
        for (DisplayInfoSettable ds : displayInfoSettables)
        {
            if (EntityType.PERSON.equals(ds.getEntityType()))
            {
                ids.add(ds.getEntityId());
            }
        }
        List<PersonModelView> people = getPersonModelViewsByIdsDAO.execute(new CollectionListAdapter<Long>(ids));
        Set<Long> allowedIds = new HashSet<Long>();
        for (PersonModelView person : people)
        {
            if (!person.isAccountLocked())
            {
                allowedIds.add(person.getId());
            }
        }
        return allowedIds;
    }

    /**
     * Removes any non-allowed people (locked, deactivated) from the list.
     *
     * @param list
     *            List to update.
     * @param allowedPeople
     *            IDs of people to allow.
     */
    private void filterList(final List< ? extends DisplayInfoSettable> list, final Set<Long> allowedPeople)
    {
        for (int i = 0; i < list.size(); i++)
        {
            DisplayInfoSettable item = list.get(i);
            if (EntityType.PERSON.equals(item.getEntityType()) && !allowedPeople.contains(item.getEntityId()))
            {
                list.remove(i);
                i--;
            }
        }
    }
}
