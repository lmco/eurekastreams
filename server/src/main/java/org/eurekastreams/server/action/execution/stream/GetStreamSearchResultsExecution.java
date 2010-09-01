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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.stream.GetStreamSearchResultsRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.requests.StreamSearchRequest;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.search.stream.SearchActivitiesMapper;
import org.eurekastreams.server.service.actions.strategies.activity.ActivityFilter;

/**
 * This class provides the execution strategy for retrieving stream search results.
 * 
 */
public class GetStreamSearchResultsExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Local instance of the logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Mapper to use to find the Stream Items.
     */
    private final SearchActivitiesMapper searchActivitiesMapper;

    /**
     * List of filters to apply to action.
     */
    private final List<ActivityFilter> filters;

    /**
     * The people mapper.
     */
    private GetPeopleByAccountIds peopleMapper;

    /**
     * Constructor.
     * 
     * @param inSearchActivitiesMapper
     *            - instance of the {@link SearchActivitiesMapper} for this execution.
     * @param inPeopleMapper
     *            people mapper.
     * @param inFilters
     *            - list of {@link ActivityFilter} objects that filter the results of the search.
     */
    public GetStreamSearchResultsExecution(final SearchActivitiesMapper inSearchActivitiesMapper,
            final GetPeopleByAccountIds inPeopleMapper, final List<ActivityFilter> inFilters)
    {
        searchActivitiesMapper = inSearchActivitiesMapper;
        filters = inFilters;
        peopleMapper = inPeopleMapper;
    }

    /**
     * {@inheritDoc}.
     * 
     * Retrieve the search results from searching the stream.
     */
    @Override
    public PagedSet<ActivityDTO> execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        long startTime = System.currentTimeMillis();
        GetStreamSearchResultsRequest request = (GetStreamSearchResultsRequest) inActionContext.getParams();

        // ask for 1 more record than we need to see if there are more results
        List<ActivityDTO> results = searchActivitiesMapper.execute(new StreamSearchRequest(inActionContext
                .getPrincipal().getAccountId(), request.getStreamViewId(), request.getSearchText(), request
                .getPageSize() + 1, request.getLastSeenStreamItemId()));

        // TODO: consider using the same security filter as used in GetActivitiesByCompositeStreamAction,
        // to safeguard against search lag after user access is removed

        // get the paged set, getting the total now that we've already made the
        // query
        int resultCount = results.size();
        if (results.size() == request.getPageSize() + 1)
        {
            // there is another page of data, but only return the current page
            // by removing the last result
            results.remove(request.getPageSize());
        }

        // Loop backwards because we're removing results.
        for (int i = results.size() - 1; i >= 0; i--)
        {
            if (results.get(i).getId() <= request.getMinActivityId())
            {
                results.remove(i);
            }
        }

        PagedSet<ActivityDTO> pagedResults = new PagedSet<ActivityDTO>(0, results.size() - 1, resultCount, results);

        // set the elapsed time
        String elapsedTime = formatElapasedTime(startTime, System.currentTimeMillis());
        pagedResults.setElapsedTime(elapsedTime);

        PersonModelView person = peopleMapper.execute(Arrays.asList(inActionContext.getPrincipal().getAccountId()))
                .get(0);

        // execute filter strategies.
        for (ActivityFilter filter : filters)
        {
            filter.filter(results, person);
        }

        log.info("Searched in " + elapsedTime);
        return pagedResults;
    }

    /**
     * Determine and format the elapsed time for a server request.
     * 
     * @param startTime
     *            the starting milliseconds
     * @param endTime
     *            the ending milliseconds
     * @return a formatted elapsed time in the format "0.23 seconds"
     */
    protected String formatElapasedTime(final long startTime, final long endTime)
    {
        final int millisecondsPerSecond = 1000;
        String elapsedTime = String.format("%1$.2f seconds", (endTime - startTime) / (float) millisecondsPerSecond);
        if (elapsedTime == "0.00 seconds")
        {
            elapsedTime = "0.01 seconds";
        }
        return elapsedTime;
    }

}
