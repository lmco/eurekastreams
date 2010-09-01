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
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.stream.GetActivitiesByCompositeStreamRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.GetPrivateCoordinatedAndFollowedGroupIdsForUser;
import org.eurekastreams.server.persistence.mappers.stream.CompositeStreamActivityIdsMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.activity.ActivityFilter;

/**
 * Action to get a page of activities for a given composite stream.
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
public class GetActivitiesByCompositeStreamExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(GetActivitiesByCompositeStreamExecution.class);

    /**
     * Mapper to get a list of IDs for activities in this composite stream.
     */
    private CompositeStreamActivityIdsMapper idsMapper;

    /**
     * Mapper to get the actual Activities.
     */
    private DomainMapper<List<Long>, List<ActivityDTO>> bulkActivitiesMapper;

    /**
     * Mapper to get the list of group ids that includes private groups the current user can see activity for.
     */
    private GetPrivateCoordinatedAndFollowedGroupIdsForUser getVisibleGroupsForUserMapper;

    /**
     * List of filters to apply to action.
     */
    private List<ActivityFilter> filters;

    /**
     * Multiplier for how many times the batch size we should request. This is multiplied by the previous batch size
     * each round.
     */
    private final float batchPageSizeMultiplier;

    /**
     * People mapper.
     */
    private GetPeopleByAccountIds peopleMapper;

    /**
     * Constructor.
     * 
     * @param inIdsMapper
     *            the ids mapper.
     * @param inBulkActivitiesMapper
     *            the bulk activities mapper.
     * @param inFilters
     *            A list of filters to apply to the activities.
     * @param inGetVisibleGroupsForUserMapper
     *            Mapper to get the list of group ids that includes private groups the current user can see activity
     *            for.
     * @param inBatchPageSizeMultiplier
     *            Multiplier for how many times the batch size we should request. This is multiplied by the previous
     *            batch size each round.
     * @param inPeopleMapper
     *            the people mapper.
     */
    public GetActivitiesByCompositeStreamExecution(final CompositeStreamActivityIdsMapper inIdsMapper,
            final DomainMapper<List<Long>, List<ActivityDTO>> inBulkActivitiesMapper,
            final List<ActivityFilter> inFilters,
            final GetPrivateCoordinatedAndFollowedGroupIdsForUser inGetVisibleGroupsForUserMapper,
            final float inBatchPageSizeMultiplier, final GetPeopleByAccountIds inPeopleMapper)
    {
        bulkActivitiesMapper = inBulkActivitiesMapper;
        idsMapper = inIdsMapper;
        filters = inFilters;
        getVisibleGroupsForUserMapper = inGetVisibleGroupsForUserMapper;
        batchPageSizeMultiplier = inBatchPageSizeMultiplier;
        peopleMapper = inPeopleMapper;
    }

    /**
     * Returns activities for a given compositeStream.
     * 
     * @param inActionContext
     *            The action context.
     * @return paged set of activities for a compositeStream.
     */
    @Override
    @SuppressWarnings("unchecked")
    public Serializable execute(final PrincipalActionContext inActionContext)
    {
        // get the user's accountId
        final String userAccountId = inActionContext.getPrincipal().getAccountId();

        // get the user's Id
        final long userEntityId = inActionContext.getPrincipal().getId();

        // get the request
        GetActivitiesByCompositeStreamRequest inRequest = (GetActivitiesByCompositeStreamRequest) inActionContext
                .getParams();

        List<Long> allKeys;

        // Get the list of activity ids from the context state, if possible (should be set by validation strategy)
        if (inActionContext.getState().containsKey("activityIds"))
        {
            allKeys = (List<Long>) inActionContext.getState().get("activityIds");
        }
        else
        {
            // Gets the list of activity ids for this composite stream
            if (log.isTraceEnabled())
            {
                log.trace("Loading list of activity ids for composite stream with id #"
                        + inRequest.getCompositeStreamId() + ", person id #" + userEntityId);
            }
            allKeys = idsMapper.execute(inRequest.getCompositeStreamId(), userEntityId);
        }

        // the list of activities to return
        List<ActivityDTO> results = new ArrayList<ActivityDTO>();

        // the results list
        PagedSet<ActivityDTO> pagedSet = new PagedSet<ActivityDTO>();
        pagedSet.setPagedSet(results);
        pagedSet.setTotal(allKeys.size());

        // The set of group ids that the user can see - this is only fetched if
        // necessary
        GroupIdSetWrapper accessibleGroupIdsSetWrapper = new GroupIdSetWrapper(userEntityId);

        // used for paging, this is the next activity in the list to add to the
        // current page
        int startingIndex = 0;

        // the batch size for each page - increases with every page with
        // batchPageSizeMultiplier
        int batchSize = inRequest.getMaxResults();

        // paging loop
        do
        {
            // multiply the batch size by the multiplier to avoid extra cache
            // hits
            batchSize *= batchPageSizeMultiplier;

            // build a list of activity ids to fetch for this page, and
            // increment the start index for next page
            List<Long> page = new ArrayList<Long>();

            // the starting index for this batch - for logging
            int thisBatchStartIndex = -1;
            for (int i = startingIndex; i < allKeys.size() && page.size() < batchSize; i++, startingIndex++)
            {
                if (allKeys.get(i) < inRequest.getMaxActivityId() && allKeys.get(i) > inRequest.getMinActivityId())
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
                log.trace("Paging loop - page size: " + inRequest.getMaxResults() + "; batchSize: " + batchSize
                        + "; activity list size: " + allKeys.size() + "; starting index: " + thisBatchStartIndex);
            }

            // get the activities from cache to see which we have access to
            List<ActivityDTO> pagedResults = bulkActivitiesMapper.execute(page);

            // add the activities that the user can see to the result page
            for (int i = 0; i < pagedResults.size() && results.size() < inRequest.getMaxResults(); i++)
            {
                ActivityDTO activityDTO = pagedResults.get(i);

                if (hasAccessToActivity(userEntityId, activityDTO, accessibleGroupIdsSetWrapper))
                {
                    results.add(activityDTO);
                }
            }
        }
        while (results.size() < inRequest.getMaxResults() && startingIndex < allKeys.size());

        PersonModelView person = peopleMapper.execute(Arrays.asList(userAccountId)).get(0);

        // execute filter strategies.
        for (ActivityFilter filter : filters)
        {
            filter.filter(results, person);
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
