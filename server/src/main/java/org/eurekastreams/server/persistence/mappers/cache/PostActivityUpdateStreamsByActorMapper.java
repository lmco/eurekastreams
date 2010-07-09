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
import java.util.Arrays;
import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.stream.BulkCompositeStreamsMapper;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.persistence.mappers.stream.UserCompositeStreamIdsMapper;

/**
 * This mapper adds an activity to the composite streams that are related to the actor posting the activity.
 *
 */
public class PostActivityUpdateStreamsByActorMapper extends CachedDomainMapper
{
    /**
     * Local instance of the {@link UserCompositeStreamIdsMapper}.
     */
    private final UserCompositeStreamIdsMapper compositeStreamIdsMapper;

    /**
     * Local instance of the {@link BulkCompositeStreamsMapper}.
     */
    private final BulkCompositeStreamsMapper bulkCompositeStreamsMapper;

    /**
     * Local instance of the {@link GetPeopleByAccountIds} mapper.
     */
    private final GetPeopleByAccountIds bulkPeopleByAccountIdMapper;

    /**
     * Local instance of the {@link GetDomainGroupsByShortNames} mapper.
     */
    private final GetDomainGroupsByShortNames bulkDomainGroupsByShortNameMapper;

    /**
     * Local instance of the {@link GetOrganizationsByShortNames} mapper.
     */
    private final GetOrganizationsByShortNames organizationsByShortNameDAO;

    /**
     * Constructor for the {@link PostActivityUpdateStreamsByActorMapper}.
     *
     * @param inCompositeStreamIdsMapper
     *            - instance of the {@link UserCompositeStreamIdsMapper}.
     * @param inBulkCompositeStreamsMapper
     *            - instance of the {@link BulkCompositeStreamsMapper}.
     * @param inBulkPeopleByAccountIdMapper
     *            - instance of the {@link GetPeopleByAccountIds} mapper.
     * @param inBulkDomainGroupsByShortNameMapper
     *            - instance of the {@link GetDomainGroupsByShortNames} mapper.
     * @param inOrganizationsByShortNameDAO
     *            - instance of the {@link GetOrganizationsByShortNames} mapper.
     */
    public PostActivityUpdateStreamsByActorMapper(final UserCompositeStreamIdsMapper inCompositeStreamIdsMapper,
            final BulkCompositeStreamsMapper inBulkCompositeStreamsMapper,
            final GetPeopleByAccountIds inBulkPeopleByAccountIdMapper,
            final GetDomainGroupsByShortNames inBulkDomainGroupsByShortNameMapper,
            final GetOrganizationsByShortNames inOrganizationsByShortNameDAO)
    {
        compositeStreamIdsMapper = inCompositeStreamIdsMapper;
        bulkCompositeStreamsMapper = inBulkCompositeStreamsMapper;
        bulkPeopleByAccountIdMapper = inBulkPeopleByAccountIdMapper;
        bulkDomainGroupsByShortNameMapper = inBulkDomainGroupsByShortNameMapper;
        organizationsByShortNameDAO = inOrganizationsByShortNameDAO;
    }

    /**
     * Post the provided {@link ActivityDTO} into the cached composite streams related to the actor.
     *
     * @param activity
     *            - {@link ActivityDTO} to be posted into the streams.
     */
    public void execute(final ActivityDTO activity)
    {
        Long entityStreamViewId = null;
        Query query = null;
        // Retrieve the destination streamview id and push the newly created activity onto the top of that list.
        if (activity.getDestinationStream().getType() == EntityType.PERSON)
        {
            query = getEntityManager().createQuery(
                    "select entityStreamView.id from Person where streamScope.id =:streamScopeId").setParameter(
                    "streamScopeId", activity.getDestinationStream().getId());
        }
        else if (activity.getDestinationStream().getType() == EntityType.GROUP)
        {
            query = getEntityManager().createQuery(
                    "select entityStreamView.id from DomainGroup where streamScope.id =:streamScopeId").setParameter(
                    "streamScopeId", activity.getDestinationStream().getId());
        }
        else
        {
            throw new ExecutionException("Error occurred retrieving the streaview for the destination stream scope, "
                    + "entity type cannot be posted to.");
        }
        entityStreamViewId = (Long) query.getSingleResult();
        getCache().addToTopOfList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + entityStreamViewId, activity.getId());

        // Gets streams for this user from mapper
        if (activity.getActor().getType() == EntityType.PERSON)
        {
            // Retrieve the list of composite streams for the actor and find the everyone composite stream.
            List<Long> userCompositeStreamIds = compositeStreamIdsMapper.execute(activity.getActor().getId());
            List<StreamFilter> compositeStreams = bulkCompositeStreamsMapper.execute(userCompositeStreamIds);
            for (StreamFilter compositeStream : compositeStreams)
            {
                // Adds activity to the Everyone composite stream
                if (((StreamView) compositeStream).getType() == StreamView.Type.EVERYONE)
                {
                    getCache().addToTopOfList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + compositeStream.getId(),
                            activity.getId());
                }
            }

            // Add to actor's parent org composite stream.
            getCache().addToTopOfList(
                    CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + getParentOrgCompositeStreamId(activity),
                    activity.getId());
        }
    }

    /**
     * Returns parentOrg compositeStream for given ActivityDTO's destination.
     *
     * @param inActivity
     *            The activity.
     * @return ParentOrg compositeStream for given ActivityDTO destination.
     */
    @SuppressWarnings({ "unchecked", "serial" })
    private long getParentOrgCompositeStreamId(final ActivityDTO inActivity)
    {
        // grab destination stream of activity.
        final StreamEntityDTO destinationStream = inActivity.getDestinationStream();

        // get parent org of destinationStream based on destination stream type.
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

        // grab the compositeStreamId from the org.
        List<String> parentOrgShortNames = new ArrayList<String>();
        parentOrgShortNames.add(parentOrgShortName);
        final long parentOrgCompositeStreamId = organizationsByShortNameDAO.execute(parentOrgShortNames).get(0)
                .getCompositeStreamId();

        // return the compositeStream for destinationStream's parent org.
        return bulkCompositeStreamsMapper.execute(new ArrayList()
        {
            {
                add(parentOrgCompositeStreamId);
            }
        }).get(0).getId();
    }
}
