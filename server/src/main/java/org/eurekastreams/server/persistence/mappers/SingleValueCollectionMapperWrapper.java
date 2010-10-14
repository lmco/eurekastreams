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
package org.eurekastreams.server.persistence.mappers;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic mapper to wrap a collection mapper with a single value input/output.
 *
 * @param <RequestType>
 *            the request type the wrapped mapper takes
 * @param <ResponseType>
 *            the response type the wrapped mapper returns
 */
public class SingleValueCollectionMapperWrapper<RequestType, ResponseType> implements
        DomainMapper<RequestType, ResponseType>
{
    /**
     * List mapper to wrap.
     */
    private DomainMapper<List<RequestType>, List<ResponseType>> wrappedMapper;

    /**
     * Whether to return null when anything other than exactly one result is found.
     */
    private boolean returnNullWhenSingleResultNotFound;

    /**
     * Constructor.
     *
     * @param inWrappedMapper
     *            list mapper to wrap.
     * @param inReturnNullWhenSingleResultNotFound
     *            Whether to return null when anything other than exactly one result is found - else, throws an
     *            exception
     */
    public SingleValueCollectionMapperWrapper(
            final DomainMapper<List<RequestType>, List<ResponseType>> inWrappedMapper,
            final boolean inReturnNullWhenSingleResultNotFound)
    {
        wrappedMapper = inWrappedMapper;
        returnNullWhenSingleResultNotFound = inReturnNullWhenSingleResultNotFound;
    }

    /**
     * Use the collection mapper to execute with a single value request.
     *
     * @param inRequest
     *            the request
     * @return the first value in the response list
     */
    @Override
    public ResponseType execute(final RequestType inRequest)
    {
        ArrayList<RequestType> request = new ArrayList<RequestType>();
        request.add(inRequest);
        List<ResponseType> response = wrappedMapper.execute(request);
        if (response.size() != 1)
        {
            if (returnNullWhenSingleResultNotFound)
            {
                return null;
            }
            else
            {
                throw new RuntimeException("Could not found exactly one response for key: " + inRequest + " - found "
                        + response.size() + " results.");
            }
        }
        return response.get(0);
    }

}
