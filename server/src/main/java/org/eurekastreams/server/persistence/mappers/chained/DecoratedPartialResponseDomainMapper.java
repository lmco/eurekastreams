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
package org.eurekastreams.server.persistence.mappers.chained;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Domain Mapper that handles a partial request, combining the results with a those retrieved from a decorated mapper
 * given a new, unsatified request, if needed.
 *
 * @param <Request>
 *            the type of request
 * @param <Response>
 *            the type of response
 */
public class DecoratedPartialResponseDomainMapper<Request, Response> implements DomainMapper<Request, Response>
{
    /**
     * Logger.
     */
    private static Log log = LogFactory.make();

    /**
     * partial response mapper to get the response and new request.
     */
    private DomainMapper<Request, PartialMapperResponse<Request, Response>> partialMapper;

    /**
     * the decorated mapper.
     */
    private DomainMapper<Request, Response> decoratedMapper;

    /**
     * results combiner.
     */
    private ResultsCombinerStrategy<Response> resultsCombiner;

    /**
     * refresher.
     */
    private RefreshStrategy<Request, Response> refreshStrategy;

    /**
     * Constructor with data refreshing. The refresher is used to update the main datasource with the results from the
     * decorated one.
     *
     * @param inPartialMapper
     *            the main mapper to use to find our data
     * @param inDecoratedMapper
     *            the decorated mapper to fall back to if the main mapper doesn't satisfy the request
     * @param inResultsCombiner
     *            the results combiner to combine the main and decorated mapper results
     * @param inRefreshStrategy
     *            refresher used to update the main datasource with the results from the decorated one
     */
    public DecoratedPartialResponseDomainMapper(
            final DomainMapper<Request, PartialMapperResponse<Request, Response>> inPartialMapper,
            final DomainMapper<Request, Response> inDecoratedMapper,
            final ResultsCombinerStrategy<Response> inResultsCombiner,
            final RefreshStrategy<Request, Response> inRefreshStrategy)
    {
        partialMapper = inPartialMapper;
        decoratedMapper = inDecoratedMapper;
        resultsCombiner = inResultsCombiner;
        refreshStrategy = inRefreshStrategy;
    }

    /**
     * Get the results from the mapper, falling through to the decorated mapper if the main mapper doesn't completely
     * satisfy the request.
     *
     * @param request
     *            the request to use to retreive the data
     * @return the response as built from the mapper and the decorated mapper
     */
    public Response execute(final Request request)
    {
        PartialMapperResponse<Request, Response> partialResponse = partialMapper.execute(request);
        if (partialResponse.hasCompleteResponse())
        {
            // has full response
            if (log.isInfoEnabled())
            {
                log.info("Found complete response with " + partialMapper.getClass());
            }
            return partialResponse.getResponse();
        }

        if (log.isInfoEnabled())
        {
            log.info("Found partial response with " + partialMapper.getClass());
        }

        Response decoratedResponse = null;
        if (decoratedMapper != null)
        {
            if (log.isInfoEnabled())
            {
                log.info("Trying to complete response with " + decoratedMapper.getClass());
            }

            // get the response from the next mapper in the chain
            decoratedResponse = decoratedMapper.execute(partialResponse.getUnhandledRequest());

            // refresh this datasource
            refreshStrategy.refresh(partialResponse.getUnhandledRequest(), decoratedResponse);
        }

        // we don't have anywhere else to go - consider it a null response from decorated mapper - let the combiner
        // decide what to do
        return resultsCombiner.combine(partialResponse.getResponse(), decoratedResponse);

    }

}
