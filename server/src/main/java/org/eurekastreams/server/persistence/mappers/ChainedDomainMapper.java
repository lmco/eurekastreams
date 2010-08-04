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

import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;

/**
 * Chain of mappers in order of priority. Look for data by starting at the top of the list and working down. If the data
 * is found, tell anyone back upstream that has a refresh mapper.
 *
 * @param <Request>
 *            the request type
 * @param <Response>
 *            response type
 */
public class ChainedDomainMapper<Request, Response> implements DomainMapper<Request, Response>
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Mappers.
     */
    private List<ChainedDomainMapperDataSource<Request, Response>> mappers;

    /**
     * Number of mappers - so we don't keep checking.
     */
    private int mapperCount;

    /**
     * Constructor - takes a list of mappers to try in order.
     *
     * @param inMappers
     *            the mapper list
     */
    public ChainedDomainMapper(final List<ChainedDomainMapperDataSource<Request, Response>> inMappers)
    {
        mappers = inMappers;
        mapperCount = inMappers.size();
    }

    /**
     * Get the response from the chain of mappers. If a result is found, run back up the chain, informing any data
     * sources that have refresh mappers.
     *
     * @param inRequest
     *            the request
     * @return the data, if found, else null
     */
    @Override
    public Response execute(final Request inRequest)
    {
        for (int readIndex = 0; readIndex < mapperCount; readIndex++)
        {
            // look for the response in this mapper
            DomainMapper<Request, Response> readMapper = mappers.get(readIndex).getDomainMapper();
            Response response = readMapper.execute(inRequest);

            if (response != null)
            {
                log.info("Found response via read mapper at index #" + readIndex + " - " + readMapper);

                // found the response - see if there's anyone up the chain that would like it
                for (int refreshIndex = readIndex - 1; refreshIndex >= 0; refreshIndex--)
                {
                    RefreshDataSourceMapper<Request, Response> refreshMapper = mappers.get(refreshIndex).getRefreshMapper();
                    if (refreshMapper != null)
                    {
                        log.info("While walking up the mapper chain, refreshing mapper at index #" + refreshIndex
                                + " - " + refreshMapper.toString());

                        // found someone that can do something about it - give them the update
                        refreshMapper.refresh(inRequest, response);
                    }
                }

                return response;
            }
        }
        return null;
    }
}
