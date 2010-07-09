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

import javax.persistence.Query;

import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamView;
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
     * Mapper to get organizations by ids.
     */
    private final GetOrganizationsByIds orgsMapper;

    /**
     * Mapper to get composite stream info.
     */
    private final BulkCompositeStreamsMapper bulkCompositeStreamsMapper;

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
     * @param inOrgsMapper
     *            orgs by ids mapper.
     * @param inBulkCompositeStreamsMapper
     *            composite streams bulk mapper.
     * @param inOrganizationsByShortNameMapper
     *            orgs by short names mapper.
     * @param inBulkDomainGroupsByShortNameMapper
     *            groups by short names mapper.
     */
    public PostCachedActivity(final GetFollowerIds inPersonFollowersMapper,
            final GetPeopleByAccountIds inBulkPeopleByAccountIdMapper,
            final GetRecursiveParentOrgIds inParentOrgIdsMapper,
            final GetOrganizationsByIds inOrgsMapper,
            final BulkCompositeStreamsMapper inBulkCompositeStreamsMapper,
            final GetOrganizationsByShortNames inOrganizationsByShortNameMapper,
            final GetDomainGroupsByShortNames inBulkDomainGroupsByShortNameMapper)
    {
        personFollowersMapper = inPersonFollowersMapper;
        bulkPeopleByAccountIdMapper = inBulkPeopleByAccountIdMapper;
        parentOrgIdsMapper = inParentOrgIdsMapper;
        orgsMapper = inOrgsMapper;
        bulkCompositeStreamsMapper = inBulkCompositeStreamsMapper;
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
    @SuppressWarnings("unchecked")
    public void execute(final ActivityDTO activity)
    {
        Long destinationScopeStreamViewId = null;

        // If the destination stream is a person, find the streamview id and skip it during
        // cache updates since it was already updated during the sync execution.
        if (activity.getDestinationStream().getType() == EntityType.PERSON)
        {
            Query queryPersonStreamViewEntityId = getEntityManager().createQuery(
                    "select entityStreamView.id from Person p where p.streamScope.id =:scopeId").setParameter(
                    "scopeId", activity.getDestinationStream().getId());

            destinationScopeStreamViewId = (Long) queryPersonStreamViewEntityId.getSingleResult();
        }
        // Do the same for a group destination stream.
        else if (activity.getDestinationStream().getType() == EntityType.GROUP)
        {
            Query queryDomainGroupStreamViewEntityId = getEntityManager().createQuery(
                    "select entityStreamView.id from DomainGroup dg where dg.streamScope.id =:scopeId").setParameter(
                    "scopeId", activity.getDestinationStream().getId());

            destinationScopeStreamViewId = (Long) queryDomainGroupStreamViewEntityId.getSingleResult();
        }
        else
        {
            throw new ExecutionException(
                    "Error occurred updating the cached composite streams, invalid destination stream type.");
        }

        // query for compositestreams that have this streamId
        StringBuilder query = new StringBuilder("select containingCompositeStreams FROM StreamScope ss WHERE ss.id = "
                + activity.getDestinationStream().getId());
        Query q = getEntityManager().createQuery(query.toString());

        List<StreamView> results = q.getResultList();
        for (StreamView compositeStream : results)
        {
            if ((destinationScopeStreamViewId == null) || (compositeStream.getId() != destinationScopeStreamViewId))
            {
                getCache().addToTopOfList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + compositeStream.getId(),
                        activity.getId());
            }
        }

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

        // puts the activity on each hierarchical parent's composite stream
        // the direct parent stream is updated as part of the surgical strike so is not needed here
        List<StreamFilter> streams = getParentOrgCompositeStreamIds(activity);
        for (StreamFilter stream : streams)
        {
            getCache().addToTopOfList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + stream.getId(), activity.getId());
        }
    }

    /**
     * Returns parentOrg compositeStreams for all hierarchical parents of the given ActivityDTO's destination.
     * This excludes the direct parent org of the destination of the activity since that is updated during
     * the sync post activity.
     *
     * @param inActivity
     *            The activity.
     * @return ParentOrg compositeStreams for given ActivityDTO destination.
     */
    private List<StreamFilter> getParentOrgCompositeStreamIds(final ActivityDTO inActivity)
    {
        final StreamEntityDTO destinationStream = inActivity.getDestinationStream();
        String parentOrgShortName = null;

        switch (destinationStream.getType())
        {
            case PERSON:
                parentOrgShortName = bulkPeopleByAccountIdMapper.execute(
                        Arrays.asList(destinationStream.getUniqueIdentifier())).get(0)
                        .getParentOrganizationShortName();
                break;
            case GROUP:
                parentOrgShortName = bulkDomainGroupsByShortNameMapper.execute(
                        Arrays.asList(destinationStream.getUniqueIdentifier())).get(0)
                        .getParentOrganizationShortName();
                break;
            default:
                throw new RuntimeException("Unexpected Activity destination stream type: "
                        + destinationStream.getType());
        }

        // gets the org itself
        final OrganizationModelView parentOrg = organizationsByShortNameMapper
            .execute(Arrays.asList(parentOrgShortName)).get(0);

        // gets the org ids of all org parents of the activity's parent org
        List<Long> parentIds = parentOrgIdsMapper.execute(parentOrg.getEntityId());

        // checks to see if this org is the root org
        if (parentIds.size() == 1 && parentIds.get(0) == parentOrg.getEntityId())
        {
            return new ArrayList<StreamFilter>();
        }

        List<OrganizationModelView> orgs = orgsMapper.execute(parentIds);

        List<Long> compositeStreamIds = new ArrayList<Long>();
        for (OrganizationModelView org : orgs)
        {
            compositeStreamIds.add(org.getCompositeStreamId());
        }

        // return the compositeStreams for destinationStream's parent org.
        return bulkCompositeStreamsMapper.execute(compositeStreamIds);
    }
}
