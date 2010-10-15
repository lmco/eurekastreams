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
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.activity.ActivityFilter;

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
     * Max activities if none is specified.
     */
    private static final int MAXRESULTS = 10;

    /**
     * Get activity IDs by JSON request.
     */
    private GetActivityIdsByJsonRequest getActivityIdsByJsonRequest;

    /**
     * List of filters to apply to action.
     */
    private List<ActivityFilter> filters;

    /**
     * Mapper to get the actual Activities.
     */
    private DomainMapper<List<Long>, List<ActivityDTO>> bulkActivitiesMapper;

    /**
     * Mapper to get a person model view by account id.
     */
    private DomainMapper<String, PersonModelView> getPersonModelViewByAccountIdMapper;

    /**
     * Default constructor.
     * 
     * @param inBulkActivitiesMapper
     *            the bulk activity mapper to get activity from.
     * @param inFilters
     *            the filters.
     * @param inGetPersonModelViewByAccountIdMapper
     *            Mapper to get a person model view by account id.
     * @param inGetActivityIdsByJsonRequest
     *            get activity IDs by JSON request.
     */
    public GetActivitiesByRequestExecution(final DomainMapper<List<Long>, List<ActivityDTO>> inBulkActivitiesMapper,
            final List<ActivityFilter> inFilters,
            final DomainMapper<String, PersonModelView> inGetPersonModelViewByAccountIdMapper,
            final GetActivityIdsByJsonRequest inGetActivityIdsByJsonRequest)
    {
        bulkActivitiesMapper = inBulkActivitiesMapper;
        filters = inFilters;
        getPersonModelViewByAccountIdMapper = inGetPersonModelViewByAccountIdMapper;
        getActivityIdsByJsonRequest = inGetActivityIdsByJsonRequest;
    }

    /**
     * Get activities base on a request.
     * 
     * @param inActionContext
     *            action context.
     * @return the activities.
     * @throws ExecutionException
     *             execution exception.
     */
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        log.debug("Attempted to parse:  " + inActionContext.getParams());

        final JSONObject request = JSONObject.fromObject(inActionContext.getParams());

        final JSONObject query = request.getJSONObject("query");

        if (!request.containsKey("count"))
        {
            request.put("count", MAXRESULTS);
        }

        final int currentMax = request.getInt("count");

        if (!query.containsKey("sortBy") || query.getString("sortBy").equals("date"))
        {
            request.put("count", currentMax + 1);
        }

        final Long userEntityId = inActionContext.getPrincipal().getId();
        final String userAccountId = inActionContext.getPrincipal().getAccountId();

        final List<Long> results = getActivityIdsByJsonRequest.execute(request.toString(), userEntityId);

        // the results list
        PagedSet<ActivityDTO> pagedSet = new PagedSet<ActivityDTO>();
        pagedSet.setTotal(results.size());

        if (log.isTraceEnabled())
        {
            log.trace(results.size() + " Items found.");
        }

        if (results.size() > currentMax)
        {
            if (log.isTraceEnabled())
            {
                log.trace("Removing extra item");
            }

            results.remove(currentMax);
        }

        final List<ActivityDTO> dtoResults = bulkActivitiesMapper.execute(results);

        PersonModelView person = getPersonModelViewByAccountIdMapper.execute(userAccountId);

        // execute filter strategies.
        for (ActivityFilter filter : filters)
        {
            filter.filter(dtoResults, person);
        }

        if (log.isTraceEnabled())
        {
            log.trace("Returning " + results.size() + " activities.");
        }

        pagedSet.setPagedSet(dtoResults);

        return pagedSet;
    }
}
