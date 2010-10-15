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
package org.eurekastreams.server.persistence.mappers;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;

/**
 * Mapper to get a list of person ids for people following the destination stream of an activity.
 */
public class GetPersonIdsFollowingActivityDestinationStreamMapper implements DomainMapper<ActivityDTO, List<Long>>
{
    /**
     * Mapper to get followers of a person.
     */
    private DomainMapper<Long, List<Long>> personFollowersMapper;

    /**
     * Mapper to get person id from accountid.
     */
    private DomainMapper<String, Long> getPersonIdFromAccountIdMapper;

    /**
     * Default constructor.
     * 
     * @param inPersonFollowersMapper
     *            the person follower mapper.
     * @param inGetPersonIdFromAccountIdMapper
     *            mapper to get person id frmo account id
     */
    public GetPersonIdsFollowingActivityDestinationStreamMapper(
            final DomainMapper<Long, List<Long>> inPersonFollowersMapper,
            final DomainMapper<String, Long> inGetPersonIdFromAccountIdMapper)
    {
        personFollowersMapper = inPersonFollowersMapper;
        getPersonIdFromAccountIdMapper = inGetPersonIdFromAccountIdMapper;
    }

    /**
     * Returns a list of followers that get this activity. Separate from the composite streams Because its stored in
     * cache differently.
     * 
     * @param activity
     *            the activity.
     * @return A list of IDs of following composite streams.
     */
    public List<Long> execute(final ActivityDTO activity)
    {
        // Gets the followers and add to their followed stream
        StreamEntityDTO destinationStream = activity.getDestinationStream();
        List<Long> followers = null;
        if (destinationStream.getType() == EntityType.PERSON)
        {
            long personId = getPersonIdFromAccountIdMapper.execute(destinationStream.getUniqueIdentifier());
            followers = personFollowersMapper.execute(personId);
        }
        else if (destinationStream.getType() == EntityType.GROUP)
        {
            // Empty for groups, group activity doesn't show up in following streams.
            followers = new ArrayList<Long>();
        }
        else
        {
            throw new IllegalArgumentException("This mapper does not support the destination stream type supplied.");
        }

        return followers;
    }
}
