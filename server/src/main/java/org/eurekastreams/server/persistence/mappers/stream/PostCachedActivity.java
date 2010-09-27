/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.GetRecursiveParentOrgIds;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.search.modelview.OrganizationModelView;

/**
 * Adds data to the cache for a newly created activity.
 */
public class PostCachedActivity extends CachedDomainMapper
{
    /**
     * Mapper to get followers of a person.
     */
    private final GetFollowerIds personFollowersMapper;

    /**
     * Mapper to get people by account ids.
     */
    private final GetPeopleByAccountIds bulkPeopleByAccountIdMapper;

    /**
     * Mapper to get hierarchical parent org ids.
     */
    private final GetRecursiveParentOrgIds parentOrgIdsMapper;

    /**
     * Local instance of the {@link GetOrganizationsByShortNames} mapper.
     */
    private final GetOrganizationsByShortNames organizationsByShortNameMapper;

    /**
     * Local instance of the {@link GetDomainGroupsByShortNames} mapper.
     */
    private final GetDomainGroupsByShortNames bulkDomainGroupsByShortNameMapper;

    /**
     * Constructor.
     *
     * @param inPersonFollowersMapper
     *            person follower mapper.
     * @param inBulkPeopleByAccountIdMapper
     *            people by account id mapper.
     * @param inParentOrgIdsMapper
     *            ids for parent orgs mapper.
     * @param inOrganizationsByShortNameMapper
     *            orgs by short names mapper.
     * @param inBulkDomainGroupsByShortNameMapper
     *            groups by short names mapper.
     */
    public PostCachedActivity(final GetFollowerIds inPersonFollowersMapper,
            final GetPeopleByAccountIds inBulkPeopleByAccountIdMapper,
            final GetRecursiveParentOrgIds inParentOrgIdsMapper,
            final GetOrganizationsByShortNames inOrganizationsByShortNameMapper,
            final GetDomainGroupsByShortNames inBulkDomainGroupsByShortNameMapper)
    {
        personFollowersMapper = inPersonFollowersMapper;
        bulkPeopleByAccountIdMapper = inBulkPeopleByAccountIdMapper;
        parentOrgIdsMapper = inParentOrgIdsMapper;
        organizationsByShortNameMapper = inOrganizationsByShortNameMapper;
        bulkDomainGroupsByShortNameMapper = inBulkDomainGroupsByShortNameMapper;
    }

    /**
     * Adds a new item in cache for the activity itself and adds the id to all necessary composite stream activity id
     * lists.
     *
     * @param activity
     *            the activity to be added.
     */
    public void execute(final ActivityDTO activity)
    {
        // Gets the followers and add to their followed stream if the
        // activity is posted to a Person's stream.
        if (activity.getDestinationStream().getType() == EntityType.PERSON)
        {
            List<String> param = new ArrayList<String>();
            param.add(activity.getDestinationStream().getUniqueIdentifier());
            long personId = bulkPeopleByAccountIdMapper.execute(param).get(0).getEntityId();
            List<Long> followers = personFollowersMapper.execute(personId);

            for (Long follower : followers)
            {
                getCache().addToTopOfList(CacheKeys.ACTIVITIES_BY_FOLLOWING + follower, activity.getId());
            }
        }

        // add to everyone list
        getCache().addToTopOfList(CacheKeys.EVERYONE_ACTIVITY_IDS, activity.getId());

        // TODO: climb up the tree, adding activity to each org
    }

    /**
     * Returns all parent org ids up the tree.
     *
     * @param inActivity
     *            The activity.
     * @return all parent org ids up the tree
     */
    private List<Long> getAllParentOrgIds(final ActivityDTO inActivity)
    {
        final StreamEntityDTO destinationStream = inActivity.getDestinationStream();
        String parentOrgShortName = null;

        switch (destinationStream.getType())
        {
        case PERSON:
            parentOrgShortName = bulkPeopleByAccountIdMapper.execute(
                    Arrays.asList(destinationStream.getUniqueIdentifier())).get(0).getParentOrganizationShortName();
            break;
        case GROUP:
            parentOrgShortName = bulkDomainGroupsByShortNameMapper.execute(
                    Arrays.asList(destinationStream.getUniqueIdentifier())).get(0).getParentOrganizationShortName();
            break;
        default:
            throw new RuntimeException("Unexpected Activity destination stream type: " + destinationStream.getType());
        }

        // gets the org itself
        final OrganizationModelView parentOrg = organizationsByShortNameMapper.execute(
                Arrays.asList(parentOrgShortName)).get(0);

        // gets the org ids of all org parents of the activity's parent org
        List<Long> parentIds = parentOrgIdsMapper.execute(parentOrg.getEntityId());
        parentIds.add(parentOrg.getEntityId());

        return parentIds;
    }
}
