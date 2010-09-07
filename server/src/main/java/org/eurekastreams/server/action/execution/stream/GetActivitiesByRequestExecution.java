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

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.activity.ActivityFilter;
import org.eurekastreams.server.service.actions.strategies.activity.ListCollider;
import org.eurekastreams.server.service.actions.strategies.activity.datasources.DescendingOrderDataSource;
import org.eurekastreams.server.service.actions.strategies.activity.datasources.SortedDataSource;

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
     * Security trimmer.
     */
    private ActivitySecurityTrimmer securityTrimmer;

    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(GetActivitiesByRequestExecution.class);

    /**
     * Data source that MUST provide results in descending order of ID.
     */
    private DescendingOrderDataSource descendingOrderdataSource;

    /**
     * Data source that MUST provide results in the order they will be given to the user.
     */
    private SortedDataSource sortedDataSource;

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
    private DomainMapper<List<Long>, List<ActivityDTO>> bulkActivitiesMapper;

    /**
     * People mapper.
     */
    private GetPeopleByAccountIds peopleMapper;

    /**
     * Max activities if none is specified.
     */
    private static final int MAXRESULTS = 10;

    /**
     * Default constructor.
     * 
     * @param inDescendingOrderdataSource
     *            the data sources to fetch the sorted descending data from.
     * @param inSortedDataSource
     *            the data sources to fetch the sorted data from.
     * @param inBulkActivitiesMapper
     *            the bulk activity mapper to get activity from.
     * @param inFilters
     *            the filters.
     * @param inAndCollider
     *            the and collider to merge results.
     * @param inSecurityTrimmer
     *            the security trimmer;
     * @param inPeopleMapper
     *            people mapper.
     */
    public GetActivitiesByRequestExecution(final DescendingOrderDataSource inDescendingOrderdataSource,
            final SortedDataSource inSortedDataSource,
            final DomainMapper<List<Long>, List<ActivityDTO>> inBulkActivitiesMapper,
            final List<ActivityFilter> inFilters, final ListCollider inAndCollider,
            final ActivitySecurityTrimmer inSecurityTrimmer, final GetPeopleByAccountIds inPeopleMapper)
    {
        descendingOrderdataSource = inDescendingOrderdataSource;
        sortedDataSource = inSortedDataSource;
        andCollider = inAndCollider;
        bulkActivitiesMapper = inBulkActivitiesMapper;
        filters = inFilters;
        securityTrimmer = inSecurityTrimmer;
        peopleMapper = inPeopleMapper;
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

        // the list of activities to return
        List<Long> results = new ArrayList<Long>();

        List<Long> allKeys = new ArrayList<Long>();

        // The pass.
        int pass = 0;
        int batchSize = 0;

        // paging loop
        startingIndex = 0;

        do
        {
            allKeys.clear();

            // multiply the batch size by the multiplier to avoid extra cache
            // hits
            batchSize = maxResults * (int) (Math.pow(2, pass));

            request.put("count", batchSize);

            final List<Long> descendingOrderDataSet = descendingOrderdataSource.fetch(request, userEntityId);

            final List<Long> sortedDataSet = sortedDataSource.fetch(request, userEntityId);

            if (descendingOrderDataSet != null && sortedDataSet != null)
            {
                allKeys = andCollider.collide(descendingOrderDataSet, sortedDataSet, batchSize);
            }
            else if (descendingOrderDataSet != null)
            {
                allKeys = descendingOrderDataSet;
            }
            else if (sortedDataSet != null)
            {
                allKeys = sortedDataSet;
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
                log.trace("Paging loop - page size: " + maxResults + "; batchSize: " + batchSize + "; starting index: "
                        + thisBatchStartIndex);
            }

            page = securityTrimmer.trim(page, userEntityId);

            results.addAll(page);

            pass++;
        }
        while (results.size() < maxResults && allKeys.size() >= batchSize);

        List<ActivityDTO> dtoResults = bulkActivitiesMapper.execute(results);

        PersonModelView person = peopleMapper.execute(Arrays.asList(userAccountId)).get(0);

        // execute filter strategies.
        for (ActivityFilter filter : filters)
        {
            filter.filter(dtoResults, person);
        }

        if (log.isTraceEnabled())
        {
            log.trace("Returning " + results.size() + " activities.");
        }

        // the results list
        PagedSet<ActivityDTO> pagedSet = new PagedSet<ActivityDTO>();
        pagedSet.setPagedSet(dtoResults);

        return pagedSet;
    }
}
