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
package org.eurekastreams.server.action.execution.stream;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.cache.GetPrivateCoordinatedAndFollowedGroupIdsForUser;
import org.eurekastreams.server.persistence.mappers.stream.BulkActivitiesMapper;
import org.eurekastreams.server.service.actions.strategies.activity.ActivityFilter;
import org.eurekastreams.server.service.actions.strategies.activity.ListCollider;
import org.eurekastreams.server.service.actions.strategies.activity.datasources.DataSource;

/**
 * Action to get a page of activities for a given request.
 *
 * Filter activities out that the current user doesn't have permission to see. Currently, this only includes activities
 * posted to private groups, where the user is not a follower of the group or a coordinator of the group or any
 * organizations above the group.
 *
 * If the user is requesting 10 activities, and batchPageSizeMultiplier is 2.0F, we pull 20 activities from cache. We
 * loop across each activity, first checking if it's public. If it is, we add it to the result batch. If not, then we
 * have to perform a security check on it.
 *
 * The first security check kicks off a request to get the list of private group ids that the current user can see
 * activity for. We check if the activity's destination stream's destinationEntityId (in this case, the domain group id)
 * is in that private list. If so, it's added to the results list.
 *
 * If we aren't able to put together a full page of 10 activities from those 20 pulled, we make another batch request
 * for activities, this time, asking for 40. The size of each activities batch increases by a factor of the input
 * multiplier. The thought here is that if there are enough activities in the system that are private to this user that
 * require 20, 40, etc, then we should more aggressively look ahead to prevent serial requests to cache.
 */
public class GetActivitiesByRequestExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(GetActivitiesByRequestExecution.class);

    /**
     * Data sources.
     */
    private List<DataSource> dataSources;

    /**
     * AND collider.
     */
    private ListCollider andCollider;

    /**
     * List of filters to apply to action.
     */
    private List<ActivityFilter> filters;

    /**
     * Mapper to get the actual Activities.
     */
    private BulkActivitiesMapper bulkActivitiesMapper;

    /**
     * Mapper to get the list of group ids that includes private groups the current user can see activity for.
     */
    private GetPrivateCoordinatedAndFollowedGroupIdsForUser getVisibleGroupsForUserMapper;
    /**
     * Multiplier for how many times the batch size we should request. This is multiplied by the previous batch size
     * each round.
     */
    private final float batchPageSizeMultiplier;

    /**
     * Max activities if none is specified.
     */
    private static final int MAXRESULTS = 10;

    /**
     * Default constructor.
     * @param inDataSources the data sources to fetch from.
     * @param inBulkActivitiesMapper the bulk activity mapper to get activity from.
     * @param inFilters the filters.
     * @param inAndCollider the and collider to merge results.
     * @param inGetVisibleGroupsForUserMapper to get which groups youre a member of.
     * @param inBatchPageSizeMultiplier the back size multiplier.
     */
    public GetActivitiesByRequestExecution(final List<DataSource> inDataSources,
            final BulkActivitiesMapper inBulkActivitiesMapper, final List<ActivityFilter> inFilters,
            final ListCollider inAndCollider,
            final GetPrivateCoordinatedAndFollowedGroupIdsForUser inGetVisibleGroupsForUserMapper,
            final float inBatchPageSizeMultiplier)
    {
        dataSources = inDataSources;
        andCollider = inAndCollider;
        bulkActivitiesMapper = inBulkActivitiesMapper;
        filters = inFilters;
        batchPageSizeMultiplier = inBatchPageSizeMultiplier;
        getVisibleGroupsForUserMapper = inGetVisibleGroupsForUserMapper;
    }


    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        JSONObject request = JSONObject.fromObject(inActionContext.getParams());

        int maxResults = MAXRESULTS;
        long minActivityId = 0;
        long maxActivityId = Long.MAX_VALUE;

        if (request.containsKey("count"))
        {
            maxResults = request.getInt("count");
        }
        if (request.containsKey("minId"))
        {
            minActivityId = request.getInt("minId");
        }
        if (request.containsKey("maxId"))
        {
            maxActivityId = request.getLong("maxId");
        }

        final String userAccountId = inActionContext.getPrincipal().getAccountId();

        // get the user's Id
        final long userEntityId = inActionContext.getPrincipal().getId();

        // used for paging, this is the next activity in the list to add to the
        // current page
        int startingIndex = 0;

        // the batch size for each page - increases with every page with
        // batchPageSizeMultiplier
        int batchSize = maxResults;

        // the list of activities to return
        List<ActivityDTO> results = new ArrayList<ActivityDTO>();

        // the results list
        PagedSet<ActivityDTO> pagedSet = new PagedSet<ActivityDTO>();
        pagedSet.setPagedSet(results);
        List<Long> allKeys = new ArrayList<Long>();


        // The set of group ids that the user can see - this is only fetched if
        // necessary
        GroupIdSetWrapper accessibleGroupIdsSetWrapper = new GroupIdSetWrapper(userEntityId);


        // paging loop
        do
        {
            startingIndex = 0;
            allKeys.clear();
            // multiply the batch size by the multiplier to avoid extra cache
            // hits
            batchSize *= batchPageSizeMultiplier;

            request.put("count", batchSize);
            List<List<Long>> dataSets = new ArrayList<List<Long>>();

            for (DataSource ds : dataSources)
            {
                dataSets.add(ds.fetch(request));
            }

            for (List<Long> dataSet : dataSets)
            {
                allKeys = andCollider.collide(dataSet, allKeys, batchSize);
            }

            // build a list of activity ids to fetch for this page, and
            // increment the start index for next page
            List<Long> page = new ArrayList<Long>();

            // the starting index for this batch - for logging
            int thisBatchStartIndex = -1;
            for (int i = startingIndex; i < allKeys.size() && page.size() < batchSize; i++, startingIndex++)
            {
                if (allKeys.get(i) < maxActivityId && allKeys.get(i) > minActivityId)
                {
                    if (thisBatchStartIndex < 0)
                    {
                        thisBatchStartIndex = i;
                    }
                    page.add(allKeys.get(i));
                }
            }

            if (log.isTraceEnabled())
            {
                log.trace("Paging loop - page size: " + maxResults + "; batchSize: " + batchSize
                        + "; starting index: " + thisBatchStartIndex);
            }

            // get the activities from cache to see which we have access to
            List<ActivityDTO> pagedResults = bulkActivitiesMapper.execute(page, userAccountId);

            // add the activities that the user can see to the result page
            for (int i = 0; i < pagedResults.size() && results.size() < maxResults; i++)
            {
                ActivityDTO activityDTO = pagedResults.get(i);

                if (hasAccessToActivity(userEntityId, activityDTO, accessibleGroupIdsSetWrapper))
                {
                    results.add(activityDTO);
                }
            }
        }
        while (results.size() < maxResults && allKeys.size() >= batchSize);

        // execute filter strategies.
        for (ActivityFilter filter : filters)
        {
            results = filter.filter(results, userAccountId);
        }

        if (log.isTraceEnabled())
        {
            log.trace("Returning " + results.size() + " activities.");
        }
        return pagedSet;
    }

    /**
     * Check whether the user with the input person id has access to view the input activity.
     *
     * @param inUserPersonId
     *            the person to check access for
     * @param inActivity
     *            the activity to check access for
     * @param inUserPermissionedGroupIdsWrapper
     *            a wrapper around the group id list that the user has permissions to see private activity for
     * @return whether or not the user can see the input activity
     */
    private boolean hasAccessToActivity(final Long inUserPersonId, final ActivityDTO inActivity,
            final GroupIdSetWrapper inUserPermissionedGroupIdsWrapper)
    {

        // check if it's public, because we don't want to do security
        // scoping unless we have to
        if (inActivity.getIsDestinationStreamPublic())
        {
            return true;
        }

        if (log.isTraceEnabled())
        {
            log.trace("Private activity #" + inActivity.getId() + " found.");
        }

        // see if the user has access to view the private group
        if (inUserPermissionedGroupIdsWrapper.getPermissionedGroupIds().contains(
                inActivity.getDestinationStream().getDestinationEntityId()))
        {
            if (log.isTraceEnabled())
            {
                log.trace("User #" + inUserPersonId + " can see activity #" + inActivity.getId()
                        + " in private group #" + inActivity.getDestinationStream().getDestinationEntityId());
            }
            return true;
        }
        else
        {
            if (log.isTraceEnabled())
            {
                log.trace("User #" + inUserPersonId + " can NOT see activity #" + inActivity.getId()
                        + " in private group #" + inActivity.getDestinationStream().getDestinationEntityId());
            }
            return false;
        }
    }

    /**
     * Wrapper around the lazy-loaded set of IDs. This is responsible for fetching a user's permissioned groups from
     * cache on demand.
     */
    private class GroupIdSetWrapper
    {
        /**
         * set of group ids that the user has permission to see activity for, including all groups the user is
         * coordinator of, those below orgs that he is coordinator of, and those the user is following.
         */
        private Set<Long> permissionedGroupIds;

        /**
         * The user's person id.
         */
        private Long userPersonId;

        /**
         * Constructor.
         *
         * @param inUserPersonId
         *            the user's person id
         */
        public GroupIdSetWrapper(final Long inUserPersonId)
        {
            userPersonId = inUserPersonId;
        }

        /**
         * Lazy-load get the group id set from cache.
         *
         * @return the set of group ids that the user has permission to see activity for, including all groups the user
         *         is coordinator of, those below orgs that he is coordinator of, and those the user is following.
         */
        public Set<Long> getPermissionedGroupIds()
        {
            if (permissionedGroupIds == null)
            {
                if (log.isTraceEnabled())
                {
                    log.trace("Looking up the list of groups user #" + userPersonId + " can view activity for.");
                }

                permissionedGroupIds = getVisibleGroupsForUserMapper.execute(userPersonId);

                if (log.isTraceEnabled())
                {
                    log.trace("User #" + userPersonId + " can see groups: " + permissionedGroupIds.toString());
                }
            }
            return permissionedGroupIds;
        }
    }

}
