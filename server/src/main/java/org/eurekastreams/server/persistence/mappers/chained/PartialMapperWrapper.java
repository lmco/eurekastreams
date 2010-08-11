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

import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Partial result mapper wrapping a non-partial mapper. The request will be processed by the next in the chain if the
 * response is null.
 *
 * @param <Request>
 *            the request type
 * @param <Response>
 *            the response type
 */
public class PartialMapperWrapper<Request, Response> implements
        DomainMapper<Request, PartialMapperResponse<Request, Response>>
{
    /**
     * Mapper to wrap with a partial mapper wrapper.
     */
    private DomainMapper<Request, Response> domainMapper;

    /**
     * Constructor.
     *
     * @param inDomainMapper
     *            the domain mapper to wrap
     */
    public PartialMapperWrapper(final DomainMapper<Request, Response> inDomainMapper)
    {
        domainMapper = inDomainMapper;
    }

    /**
     * Return a non-partial response with a partial response.
     *
     * @param inRequest
     *            the request
     * @return the results wrapped in a partial response
     */
    @Override
    public PartialMapperResponse<Request, Response> execute(final Request inRequest)
    {
        Response response = domainMapper.execute(inRequest);
        if (response == null)
        {
            // response was null - pass on the request
            return new PartialMapperResponse<Request, Response>(response, inRequest);
        }
        else
        {
            // response was not null - return it with no follow-up request
            return new PartialMapperResponse<Request, Response>(response);
        }
    }

}
