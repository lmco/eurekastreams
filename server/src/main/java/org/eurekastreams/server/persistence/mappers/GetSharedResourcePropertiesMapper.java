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
import org.eurekastreams.server.action.request.SharedResourceRequest;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.search.modelview.SharedResourceDTO;

/**
 * Mapper to get the properties of a shared resource, including shared and liked counts and 4 people from each list.
 * This combines a few mappers.
 */
public class GetSharedResourcePropertiesMapper extends BaseArgDomainMapper<SharedResourceRequest, SharedResourceDTO>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Mapper to get a stream scope by scope type and unique key.
     */
    private DomainMapper<String, StreamScope> getResourceStreamScopeByKeyMapper;

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
     * @param inGetResourceStreamScopeByKeyMapper
     *            Mapper to get a stream scope by scope type and unique key.
     * @param inGetPeopleThatSharedResourceMapper
     *            Mapper that gets the ids of people that liked a shared resource.
     * @param inGetPeopleThatLikedResourceMapper
     *            Mapper that gets the ids of people that shared a shared resource.
     * @param inGetPeopleModelViewsByIdsMapper
     *            mapper to get person model views
     */
    public GetSharedResourcePropertiesMapper(
            final DomainMapper<String, StreamScope> inGetResourceStreamScopeByKeyMapper,
            final DomainMapper<SharedResourceRequest, List<Long>> inGetPeopleThatSharedResourceMapper,
            final DomainMapper<SharedResourceRequest, List<Long>> inGetPeopleThatLikedResourceMapper,
            final DomainMapper<List<Long>, List<PersonModelView>> inGetPeopleModelViewsByIdsMapper)
    {
        getResourceStreamScopeByKeyMapper = inGetResourceStreamScopeByKeyMapper;
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
        dto.setKey(inRequest.getUniqueKey());
        dto.setSharersSample(new ArrayList<PersonModelView>());
        dto.setLikersSample(new ArrayList<PersonModelView>());

        // either null or a stream scope id
        StreamScope sharedResourceStreamScope = getResourceStreamScopeByKeyMapper.execute(inRequest.getUniqueKey());

        // if the stream scope doesn't exist, then this resource doesn't either
        if (sharedResourceStreamScope == null)
        {
            // not found - if the shared resource existed, it would have a stream scope, so we can stop looking through
            // the other tables now
            dto.setStreamScopeId(null);
            dto.setIsLiked(false);
            dto.setLikeCount(0);
            dto.setShareCount(0);
            return dto;
        }
        dto.setStreamScopeId(sharedResourceStreamScope.getId());

        // since we know the destination SharedResource id, we can get the likers much quicker
        inRequest.setSharedResourceId(sharedResourceStreamScope.getDestinationEntityId());

        List<Long> sharedPersonIds = getPeopleThatSharedResourceMapper.execute(inRequest);
        List<Long> likedPersonIds = getPeopleThatLikedResourceMapper.execute(inRequest);

        // need to check if the current user liked this before trimming the list
        dto.setIsLiked(likedPersonIds.contains(inRequest.getPersonId()));
        dto.setLikeCount(likedPersonIds.size());
        dto.setShareCount(sharedPersonIds.size());

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
