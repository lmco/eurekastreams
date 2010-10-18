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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.db.GetOrgShortNamesByIdsMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Adds data to the cache for a newly created activity.
 */
public class PostCachedActivity extends CachedDomainMapper
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Mapper to get followers of a person.
     */
    private final DomainMapper<Long, List<Long>> personFollowersMapper;

    /**
     * Mapper to get personmodelview from an accountid.
     */
    private final DomainMapper<String, PersonModelView> getPersonModelViewByAccountIdMapper;

    /**
     * mapper to get all parent org ids for an org id.
     */
    private DomainMapper<Long, List<Long>> parentOrgIdsMapper;

    /**
     * Local instance of the {@link GetDomainGroupsByShortNames} mapper.
     */
    private final GetDomainGroupsByShortNames bulkDomainGroupsByShortNameMapper;

    /**
     * Mapper to get the short names from org ids.
     */
    private final GetOrgShortNamesByIdsMapper orgShortNamesFromIdsMapper;

    /**
     * Constructor.
     * 
     * @param inPersonFollowersMapper
     *            person follower mapper.
     * @param inGetPersonModelViewByAccountIdMapper
     *            Mapper to get personmodelview from an accountid.
     * @param inParentOrgIdsMapper
     *            ids for parent orgs mapper.
     * @param inBulkDomainGroupsByShortNameMapper
     *            groups by short names mapper.
     * @param inOrgShortNamesFromIdsMapper
     *            mapper to get org shortnames from ids
     */
    public PostCachedActivity(final DomainMapper<Long, List<Long>> inPersonFollowersMapper,
            final DomainMapper<String, PersonModelView> inGetPersonModelViewByAccountIdMapper,
            final DomainMapper<Long, List<Long>> inParentOrgIdsMapper,
            final GetDomainGroupsByShortNames inBulkDomainGroupsByShortNameMapper,
            final GetOrgShortNamesByIdsMapper inOrgShortNamesFromIdsMapper)
    {
        personFollowersMapper = inPersonFollowersMapper;
        getPersonModelViewByAccountIdMapper = inGetPersonModelViewByAccountIdMapper;
        parentOrgIdsMapper = inParentOrgIdsMapper;
        bulkDomainGroupsByShortNameMapper = inBulkDomainGroupsByShortNameMapper;
        orgShortNamesFromIdsMapper = inOrgShortNamesFromIdsMapper;
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
        StreamEntityDTO destinationStream = activity.getDestinationStream();
        EntityType type = destinationStream.getType();
        Long parentOrgId;

        if (type == EntityType.PERSON)
        {
            PersonModelView person = getPersonModelViewByAccountIdMapper.execute(destinationStream
                    .getUniqueIdentifier());

            long personId = person.getEntityId();

            parentOrgId = person.getParentOrganizationId();

            List<Long> followers = personFollowersMapper.execute(personId);

            for (Long follower : followers)
            {
                getCache().addToTopOfList(CacheKeys.ACTIVITIES_BY_FOLLOWING + follower, activity.getId());
            }
        }
        else if (type == EntityType.GROUP)
        {
            parentOrgId = bulkDomainGroupsByShortNameMapper.execute(
                    Arrays.asList(destinationStream.getUniqueIdentifier())).get(0).getParentOrganizationId();
        }
        else
        {
            throw new RuntimeException("Unexpected Activity destination stream type: " + type);
        }

        // add to everyone list
        log.trace("Adding activity id " + activity.getId() + " into everyone activity list.");
        getCache().addToTopOfList(CacheKeys.EVERYONE_ACTIVITY_IDS, activity.getId());

        // climb up the tree, adding activity to each org
        for (String orgShortName : getAllParentOrgShortNames(parentOrgId))
        {
            log.trace("Adding activity id " + activity.getId() + " to organization cache list " + orgShortName);
            getCache().addToTopOfList(CacheKeys.ACTIVITY_IDS_FOR_ORG_BY_SHORTNAME_RECURSIVE + orgShortName,
                    activity.getId());
        }
    }

    /**
     * Returns all parent org short names up the tree.
     * 
     * @param inOrgId
     *            The id of the org.
     * @return all parent org ids up the tree
     */
    private List<String> getAllParentOrgShortNames(final Long inOrgId)
    {
        // gets the org ids of all org parents of the activity's parent org
        List<Long> parentIds = parentOrgIdsMapper.execute(inOrgId);
        parentIds.add(inOrgId);

        return orgShortNamesFromIdsMapper.execute(parentIds);
    }
}
