/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Takes in a list of requests and a domain mapper that takes in a single one of those requests.
 * Executes the domain mapper for each of those requests and returns a list of responses.
 * @author romanoa1
 *
 * @param <Request> the request object.
 * @param <Response> the response obkect.
 */
public class MultiRequestMapperWrapper<Request, Response> implements DomainMapper<List<Request>, List<Response>>
{
    /**
     * The domain mapper to query.
     */
    private DomainMapper<Request, Response> domainMapper;

    /**
     * Default constructor.
     * @param inDomainMapper the domain mapper.
     */
    public MultiRequestMapperWrapper(final DomainMapper<Request, Response> inDomainMapper)
    {
        domainMapper = inDomainMapper;
    }

    /**
     * Execute.
     * @param inRequest the list of requests.
     * @return the list of responses.
     */
    @Override
    public List<Response> execute(final List<Request> inRequest)
    {
        List<Response> responses = new ArrayList<Response>();

        for (Request request : inRequest)
        {
            responses.add(domainMapper.execute(request));
        }

        return responses;
    }

}
