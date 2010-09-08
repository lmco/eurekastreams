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

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.service.actions.strategies.activity.ListCollider;
import org.eurekastreams.server.service.actions.strategies.activity.datasources.DescendingOrderDataSource;
import org.eurekastreams.server.service.actions.strategies.activity.datasources.SortedDataSource;

/**
 * Get Activity IDs with a JSON request.
 */
public class GetActivityIdsByJsonRequest
{
    /**
     * Max activities if none is specified.
     */
    private static final int MAXRESULTS = 10;

    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(GetActivityIdsByJsonRequest.class);

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
     * Security trimmer.
     */
    private ActivitySecurityTrimmer securityTrimmer;

    /**
     * Empty constructor for AOP.
     */
    public GetActivityIdsByJsonRequest()
    {

    }

    /**
     * Default constructor.
     *
     * @param inDescendingOrderdataSource
     *            the data sources to fetch the sorted descending data from.
     * @param inSortedDataSource
     *            the data sources to fetch the sorted data from.
     * @param inAndCollider
     *            the and collider to merge results.
     * @param inSecurityTrimmer
     *            the security trimmer;
     */
    public GetActivityIdsByJsonRequest(final DescendingOrderDataSource inDescendingOrderdataSource,
            final SortedDataSource inSortedDataSource, final ListCollider inAndCollider,
            final ActivitySecurityTrimmer inSecurityTrimmer)
    {
        descendingOrderdataSource = inDescendingOrderdataSource;
        sortedDataSource = inSortedDataSource;
        andCollider = inAndCollider;
        securityTrimmer = inSecurityTrimmer;
    }

    /**
     * Get activity ids base on a request and user entity ID.
     *
     * @param inRequest
     *            the request.
     * @param userEntityId
     *            the user entity ID.
     * @return the activity ids.
     */
    public List<Long> execute(final String inRequest, final Long userEntityId)
    {
        final JSONObject request = JSONObject.fromObject(inRequest);

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

        return results;
    }

}
