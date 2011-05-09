/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.SharedResourceRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.search.modelview.SharedResourceDTO;

/**
 * Execution strategy for getting shared resources by unique key.
 */
public class GetSharedResourcePropertiesExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * The mapper to get the shared resource dto by unique key - includes everything except the sampling of people that
     * liked the shared resource - those need to be filled in at runtime to avoid the headaches of keeping display names
     * and avatars in cache in yet another place.
     */
    private DomainMapper<SharedResourceRequest, SharedResourceDTO> mapper;

    /**
     * Mapper to get skeleton person model views by ids - only info needed for avatars.
     */
    private DomainMapper<List<Long>, List<PersonModelView>> getPeopleModelViewsByIdsMapper;

    /**
     * Constructor.
     * 
     * @param inMapper
     *            the mapper to get the shared resource dto by unique key - includes everything except the sampling of
     *            people that liked the shared resource - those need to be filled in at runtime to avoid the headaches
     *            of keeping display names and avatars in cache in yet another place
     * @param inGetPeopleModelViewsByIdsMapper
     *            Mapper to get skeleton person model views by ids - only info needed for avatars
     */
    public GetSharedResourcePropertiesExecution(final DomainMapper<SharedResourceRequest, SharedResourceDTO> inMapper,
            final DomainMapper<List<Long>, List<PersonModelView>> inGetPeopleModelViewsByIdsMapper)
    {
        mapper = inMapper;
        getPeopleModelViewsByIdsMapper = inGetPeopleModelViewsByIdsMapper;
    }

    /**
     * Get the shared resource dto by unique key, then fill in a sample of the people.
     * 
     * @param inActionContext
     *            the action context
     * @return a SharedResourceDTO for the input unique key - will not return null, it'll make a skeleton version if the
     *         actual shared resource doesn't exist in the system
     * @throws ExecutionException
     *             Never, actually
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        SharedResourceRequest request = (SharedResourceRequest) inActionContext.getParams();
        Long currentUserPersonId = inActionContext.getPrincipal().getId();

        log.info("Searching for the cacheable component of the SharedResourceDTO");

        // get the dto in its cacheable state - everything else i do to it here isn't cacheable, including setting
        // whether the current person liked it, and setting some skeleton personmodelviews for avatar display
        SharedResourceDTO dto = mapper.execute(request);

        // --- BEGIN customizing the previously cacheable DTO

        // make sure the collections aren't null
        if (dto.getLikerPersonIds() == null)
        {
            dto.setLikerPersonIds(new ArrayList<Long>());
        }
        if (dto.getSharerPersonIds() == null)
        {
            dto.setSharerPersonIds(new ArrayList<Long>());
        }
        dto.setSharersSample(new ArrayList<PersonModelView>());
        dto.setLikersSample(new ArrayList<PersonModelView>());

        log.info("Now customizing the SharedResourceDTO with info about the current user, and include avatar info");

        // set whether the current user likes the shared resource
        dto.setIsLiked(dto.getLikerPersonIds().contains(currentUserPersonId));

        // --
        // create a sampling of the people that liked and shared a resource - trim the two lists each to 4 people,
        // make a single request to get the person modelviews, then put the modelviews back in the sample lists
        // --

        List<Long> sharedPersonIds = dto.getSharerPersonIds();
        List<Long> likedPersonIds = dto.getLikerPersonIds();

        // 1. trim the list of people in the sample collections
        if (sharedPersonIds.size() > 4)
        {
            sharedPersonIds = sharedPersonIds.subList(0, 4);
        }
        if (likedPersonIds.size() > 4)
        {
            likedPersonIds = likedPersonIds.subList(0, 4);
        }

        // 2. put all the people ids together for a single request to get the skeleton person modelviews
        List<Long> personIds = new ArrayList<Long>();
        personIds.addAll(sharedPersonIds);
        for (long id : likedPersonIds)
        {
            if (!personIds.contains(id))
            {
                personIds.add(id);
            }
        }

        // 3. Create the sample lists of people that shared and liked this resource
        if (personIds.size() > 0)
        {
            List<PersonModelView> people = getPeopleModelViewsByIdsMapper.execute(personIds);
            PersonModelView foundPerson;
            for (long personId : personIds)
            {
                foundPerson = findPersonInList(people, personId);
                if (foundPerson != null)
                {
                    // add the DTO to the people collections
                    if (likedPersonIds.contains(personId))
                    {
                        dto.getLikersSample().add(foundPerson);
                    }
                    if (sharedPersonIds.contains(personId))
                    {
                        dto.getSharersSample().add(foundPerson);
                    }
                }
            }
        }

        return dto;
    }

    /**
     * Get a person by id from a list of PersonModelViews.
     * 
     * @param inPeople
     *            the people to look through
     * @param personId
     *            the person id we're looking for
     * @return the person with the input id in the list of person model views
     */
    private PersonModelView findPersonInList(final List<PersonModelView> inPeople, final Long personId)
    {
        for (PersonModelView p : inPeople)
        {
            if (p.getId() == personId)
            {
                return p;
            }
        }
        return null;
    }
}
