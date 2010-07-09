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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.GetRecursiveParentOrgIds;
import org.eurekastreams.server.persistence.mappers.stream.BulkCompositeStreamsMapper;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetFollowerIds;
import org.eurekastreams.server.persistence.mappers.stream.GetGroupFollowerIds;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByIds;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.OrganizationModelView;

/**
 * Gets all of the composite streams an activity should be added to.
 *
 */
public class GetCompositeStreamIdsByAssociatedActivity extends CachedDomainMapper
{
    /**
     * Logging instance for this class.
     */
    private final Log logger = LogFactory.make();

    /**
     * Mapper to get composite streams.
     */
    private BulkCompositeStreamsMapper bulkCompositeStreamsMapper;

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
     * Mapper to get hierarchical parent org ids.
     */
    private final GetRecursiveParentOrgIds parentOrgIdsMapper;

    /**
     * Mapper to get organizations by ids.
     */
    private final GetOrganizationsByIds orgsMapper;

    /**
     * Organization by id DAO.
     */
    private GetOrganizationsByShortNames organizationsByShortNameDAO;

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
     * @param inBulkCompositeStreamsMapper
     *            the bulk composite stream mapper.
     * @param inBulkPeopleByAccountIdMapper
     *            the get people by account id mapper.
     * @param inBulkDomainGroupsByShortNameMapper
     *            the bulk domain group by short name mapper.
     * @param inParentOrgIdsMapper
     *            ids for parent orgs mapper.
     * @param inOrgsMapper
     *            orgs by ids mapper.
     * @param inOrganizationsByShortNameDAO
     *            the organization mapper.
     */
    public GetCompositeStreamIdsByAssociatedActivity(final GetFollowerIds inPersonFollowersMapper,
            final GetGroupFollowerIds inGroupFollowersMapper,
            final BulkCompositeStreamsMapper inBulkCompositeStreamsMapper,
            final GetPeopleByAccountIds inBulkPeopleByAccountIdMapper,
            final GetDomainGroupsByShortNames inBulkDomainGroupsByShortNameMapper,
            final GetRecursiveParentOrgIds inParentOrgIdsMapper, final GetOrganizationsByIds inOrgsMapper,
            final GetOrganizationsByShortNames inOrganizationsByShortNameDAO)
    {
        personFollowersMapper = inPersonFollowersMapper;
        groupFollowersMapper = inGroupFollowersMapper;
        bulkCompositeStreamsMapper = inBulkCompositeStreamsMapper;
        bulkPeopleByAccountIdMapper = inBulkPeopleByAccountIdMapper;
        bulkDomainGroupsByShortNameMapper = inBulkDomainGroupsByShortNameMapper;
        parentOrgIdsMapper = inParentOrgIdsMapper;
        orgsMapper = inOrgsMapper;
        organizationsByShortNameDAO = inOrganizationsByShortNameDAO;
    }

    /**
     * Gets a list of composite streams that this activity goes in (minus followers).
     *
     * @param activity
     *            the activity.
     * @return the composite streams.
     */
    public List<StreamView> getCompositeStreams(final ActivityDTO activity)
    {

        List<StreamView> streamViews = getParentOrgCompositeStreamId(activity);

        if (logger.isDebugEnabled())
        {
            logger.debug("Retrieved " + streamViews.size()
                    + " parentOrg stream views for activity destination stream id: "
                    + activity.getDestinationStream().getId());
        }

        // query for compositestreams that have this streamId
        StringBuilder query = new StringBuilder("select containingCompositeStreams FROM StreamScope ss WHERE ss.id = "
                + activity.getDestinationStream().getId());
        Query q = getEntityManager().createQuery(query.toString());

        List<StreamView> results = q.getResultList();

        for (StreamView compositeStream : results)
        {
            streamViews.add(compositeStream);
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("Total stream views: " + streamViews.size() + " for activity destination stream id: "
                    + activity.getDestinationStream().getId());
        }

        return streamViews;
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

    /**
     * Returns parentOrg compositeStream for given ActivityDTO's destination.
     *
     * @param inActivity
     *            The activity.
     * @return ParentOrg compositeStream for given ActivityDTO destination.
     */
    @SuppressWarnings({ "unchecked", "serial" })
    private List<StreamView> getParentOrgCompositeStreamId(final ActivityDTO inActivity)
    {
        // grab destination stream of activity.
        final StreamEntityDTO destinationStream = inActivity.getDestinationStream();

        // get parent org of destinationStream based on destination stream type.
        String parentOrgShortName = null;
        switch (destinationStream.getType())
        {
        case PERSON:
            parentOrgShortName = bulkPeopleByAccountIdMapper.execute(new ArrayList()
            {
                {
                    add(destinationStream.getUniqueIdentifier());
                }
            }).get(0).getParentOrganizationShortName();
            break;
        case GROUP:
            parentOrgShortName = bulkDomainGroupsByShortNameMapper.execute(new ArrayList()
            {
                {
                    add(destinationStream.getUniqueIdentifier());
                }
            }).get(0).getParentOrganizationShortName();
            break;
        default:
            throw new IllegalArgumentException("This mapper does not support the destination stream type supplied.");
        }

        // gets the org itself
        final OrganizationModelView parentOrg = organizationsByShortNameDAO.execute(Arrays.asList(parentOrgShortName))
                .get(0);

        // gets the org ids of all org parents of the activity's parent org
        List<Long> parentIds = parentOrgIdsMapper.execute(parentOrg.getEntityId());
        //Need to include the parentOrg's id as well.
        parentIds.add(parentOrg.getEntityId());

        List<OrganizationModelView> orgs = orgsMapper.execute(parentIds);

        List<Long> compositeStreamIds = new ArrayList<Long>();
        for (OrganizationModelView org : orgs)
        {
            compositeStreamIds.add(org.getCompositeStreamId());
        }

        ArrayList<StreamView> views = new ArrayList<StreamView>();
        // return the compositeStreams for destinationStream's parent org.
        for (StreamFilter filter : bulkCompositeStreamsMapper.execute(compositeStreamIds))
        {
            views.add((StreamView) filter);
        }

        return views;
    }
}
