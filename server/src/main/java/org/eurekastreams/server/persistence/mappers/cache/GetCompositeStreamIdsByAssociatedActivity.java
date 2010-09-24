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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetFollowerIds;
import org.eurekastreams.server.persistence.mappers.stream.GetGroupFollowerIds;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;

/**
 * Gets all of the composite streams an activity should be added to.
 *
 */
public class GetCompositeStreamIdsByAssociatedActivity extends CachedDomainMapper
{
    /**
     * Mapper to get followers of a person.
     */
    private GetFollowerIds personFollowersMapper;

    /**
     * Mapper to get followers of a group.
     */
    private GetGroupFollowerIds groupFollowersMapper;

    /**
     * Mapper to get people by account ids.
     */
    private GetPeopleByAccountIds bulkPeopleByAccountIdMapper;

    /**
     * Mapper to get groups by short name.
     */
    private GetDomainGroupsByShortNames bulkDomainGroupsByShortNameMapper;

    /**
     * Default constructor.
     *
     * @param inPersonFollowersMapper
     *            the person follower mapper.
     * @param inGroupFollowersMapper
     *            the group follower mapper.
     * @param inBulkPeopleByAccountIdMapper
     *            the get people by account id mapper.
     * @param inBulkDomainGroupsByShortNameMapper
     *            the bulk domain group by short name mapper.
     */
    public GetCompositeStreamIdsByAssociatedActivity(final GetFollowerIds inPersonFollowersMapper,
            final GetGroupFollowerIds inGroupFollowersMapper,
            final GetPeopleByAccountIds inBulkPeopleByAccountIdMapper,
            final GetDomainGroupsByShortNames inBulkDomainGroupsByShortNameMapper)
    {
        personFollowersMapper = inPersonFollowersMapper;
        groupFollowersMapper = inGroupFollowersMapper;
        bulkPeopleByAccountIdMapper = inBulkPeopleByAccountIdMapper;
        bulkDomainGroupsByShortNameMapper = inBulkDomainGroupsByShortNameMapper;
    }

    /**
     * Returns a list of followers that get this activity. Separate from the composite streams Because its stored in
     * cache differently.
     *
     * @param activity
     *            the activity.
     * @return A list of IDs of following composite streams.
     */
    public List<Long> getFollowers(final ActivityDTO activity)
    {
        // Gets the followers and add to their followed stream
        StreamEntityDTO destinationStream = activity.getDestinationStream();
        List<Long> followers = null;
        List<String> param = new ArrayList<String>();
        param.add(destinationStream.getUniqueIdentifier());
        if (destinationStream.getType() == EntityType.PERSON)
        {
            long personId = bulkPeopleByAccountIdMapper.execute(param).get(0).getEntityId();
            followers = personFollowersMapper.execute(personId);
        }
        else if (destinationStream.getType() == EntityType.GROUP)
        {
            long groupId = bulkDomainGroupsByShortNameMapper.execute(param).get(0).getEntityId();
            followers = groupFollowersMapper.execute(groupId);
        }
        else
        {
            throw new IllegalArgumentException("This mapper does not support the destination stream type supplied.");
        }

        return followers;
    }
}
