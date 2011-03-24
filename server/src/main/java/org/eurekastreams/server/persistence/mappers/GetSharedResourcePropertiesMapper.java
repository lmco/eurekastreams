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
package org.eurekastreams.server.persistence.mappers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.requests.SharedResourceRequest;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.search.modelview.SharedResourceDTO;

/**
 * Mapper to get the properties of a shared resource, including shared and liked counts and 4 people from each list.
 * This combines a few mappers
 */
public class GetSharedResourcePropertiesMapper extends BaseArgDomainMapper<SharedResourceRequest, SharedResourceDTO>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Mapper that gets the ids of people that liked a shared resource.
     */
    private DomainMapper<SharedResourceRequest, List<Long>> getPeopleThatSharedResourceMapper;

    /**
     * Mapper that gets the ids of people that shared a shared resource.
     */
    private DomainMapper<SharedResourceRequest, List<Long>> getPeopleThatLikedResourceMapper;

    /**
     * Mapper to get person model views by ids.
     */
    private DomainMapper<List<Long>, List<PersonModelView>> getPeopleModelViewsByIdsMapper;

    /**
     * Constructor.
     * 
     * @param inGetPeopleThatSharedResourceMapper
     *            Mapper that gets the ids of people that liked a shared resource.
     * @param inGetPeopleThatLikedResourceMapper
     *            Mapper that gets the ids of people that shared a shared resource.
     * @param inGetPeopleModelViewsByIdsMapper
     *            mapper to get person model views
     */
    public GetSharedResourcePropertiesMapper(
            final DomainMapper<SharedResourceRequest, List<Long>> inGetPeopleThatSharedResourceMapper,
            final DomainMapper<SharedResourceRequest, List<Long>> inGetPeopleThatLikedResourceMapper,
            final DomainMapper<List<Long>, List<PersonModelView>> inGetPeopleModelViewsByIdsMapper)
    {
        getPeopleThatSharedResourceMapper = inGetPeopleThatSharedResourceMapper;
        getPeopleThatLikedResourceMapper = inGetPeopleThatLikedResourceMapper;
        getPeopleModelViewsByIdsMapper = inGetPeopleModelViewsByIdsMapper;
    }

    /**
     * Return the SharedResourceDTO from the input request.
     * 
     * @param inRequest
     *            the request
     * @return the shared resource dto
     */
    @Override
    public SharedResourceDTO execute(final SharedResourceRequest inRequest)
    {
        SharedResourceDTO dto = new SharedResourceDTO();

        List<Long> sharedPersonIds = getPeopleThatSharedResourceMapper.execute(inRequest);
        List<Long> likedPersonIds = getPeopleThatLikedResourceMapper.execute(inRequest);

        dto.setKey(inRequest.getUniqueKey());
        dto.setLikeCount(likedPersonIds.size());
        dto.setShareCount(sharedPersonIds.size());
        dto.setSharersSample(new ArrayList<PersonModelView>());
        dto.setLikersSample(new ArrayList<PersonModelView>());

        log.info("Getting shared and liked people lists for shared resource with key: " + inRequest.getUniqueKey());

        if (sharedPersonIds.size() > 4)
        {
            sharedPersonIds = sharedPersonIds.subList(0, 4);
        }
        if (likedPersonIds.size() > 4)
        {
            likedPersonIds = likedPersonIds.subList(0, 4);
        }

        // get the people
        List<Long> personIds = new ArrayList<Long>();
        personIds.addAll(sharedPersonIds);
        for (long id : likedPersonIds)
        {
            if (!personIds.contains(id))
            {
                personIds.add(id);
            }
        }

        if (personIds.size() > 0)
        {
            log.info("Getting " + personIds.size() + " people.");
            List<PersonModelView> people = getPeopleModelViewsByIdsMapper.execute(personIds);
            PersonModelView foundPerson, newPerson;
            for (long personId : personIds)
            {
                foundPerson = findPersonInList(people, personId);
                if (foundPerson != null)
                {
                    // only copy the fields we need
                    newPerson = new PersonModelView();
                    newPerson.setAccountId(foundPerson.getAccountId());
                    newPerson.setAvatarId(foundPerson.getAvatarId());
                    newPerson.setEntityId(foundPerson.getEntityId());
                    newPerson.setDisplayName(foundPerson.getDisplayName());

                    // add the DTO to the people collections
                    if (likedPersonIds.contains(personId))
                    {
                        dto.getLikersSample().add(newPerson);
                    }
                    if (sharedPersonIds.contains(personId))
                    {
                        dto.getSharersSample().add(newPerson);
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
