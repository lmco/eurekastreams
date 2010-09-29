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

import java.util.List;

import org.eurekastreams.server.persistence.mappers.chained.RefreshStrategy;

/**
 * Refresh strategy that executes multiple {@link RefreshStrategy}s.
 * 
 * @param <Request>
 *            the request that the data was found with
 * @param <Response>
 *            the data found by the previous level
 */
public class MultiRefreshCacheRefreshStrategy<Request, Response> implements RefreshStrategy<Request, Response>
{
    /**
     * List of {@link RefreshStrategy}s.
     */
    List<RefreshStrategy<Request, Response>> strategies;

    /**
     * Constructor.
     * 
     * @param inStrategies
     *            List of strategies to use.
     */
    public MultiRefreshCacheRefreshStrategy(final List<RefreshStrategy<Request, Response>> inStrategies)
    {
        strategies = inStrategies;
    }

    /**
     * Loop through {@link RefreshStrategy}s calling each with provided Request/Response.
     * 
     * @param inRequest
     *            the request that the data was found with
     * @param inResponse
     *            the data found by the previous level
     */
    @Override
    public void refresh(final Request inRequest, final Response inResponse)
    {
        for (RefreshStrategy<Request, Response> strat : strategies)
        {
            strat.refresh(inRequest, inResponse);
        }
    }

}
