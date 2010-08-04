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

/**
 * Datasource for ChainedReaderMapper. This lives in a chain, if the data can't be found in this source, the next in the
 * list will be searched, then this one will be updated with whatever was found downstream.
 *
 * @param <Request>
 *            the request type
 * @param <Response>
 *            the response type
 */
public class ChainedDomainMapperDataSource<Request, Response>
{
    /**
     * Mapper responsible for updating the data source with content found downstream in the list of data sources.
     */
    private RefreshDataSourceMapper<Request, Response> refreshMapper;

    /**
     * Mapper responsible for fetching the data.
     */
    private DomainMapper<Request, Response> domainMapper;

    /**
     * Constructor for data sources that can be updated.
     *
     * @param inDomainMapper
     *            mapper responsible for fetching the data from the data source
     * @param inRefreshMapper
     *            mapper responsible for updating the data source
     */
    public ChainedDomainMapperDataSource(final DomainMapper<Request, Response> inDomainMapper,
            final RefreshDataSourceMapper<Request, Response> inRefreshMapper)
    {
        domainMapper = inDomainMapper;
        refreshMapper = inRefreshMapper;
    }

    /**
     * Constructor - for data sources that can't be updated.
     *
     * @param inDomainMapper
     *            mapper responsible for fetching the data from the data source
     */
    public ChainedDomainMapperDataSource(final DomainMapper<Request, Response> inDomainMapper)
    {
        domainMapper = inDomainMapper;
    }

    /**
     * Get the mapper responsible for updating the data source.
     *
     * @return the mapper responsible for updating the data source
     */
    public RefreshDataSourceMapper<Request, Response> getRefreshMapper()
    {
        return refreshMapper;
    }

    /**
     * Set the mapper responsible for updating the data source.
     *
     * @param inRefreshMapper
     *            the mapper responsible for updating the data source
     */
    public void setRefreshMapper(final RefreshDataSourceMapper<Request, Response> inRefreshMapper)
    {
        refreshMapper = inRefreshMapper;
    }

    /**
     * Get the mapper responsible for fetching the data from the data source.
     *
     * @return mapper responsible for fetching the data from the data source
     */
    public DomainMapper<Request, Response> getDomainMapper()
    {
        return domainMapper;
    }

    /**
     * Set the mapper responsible for fetching the data from the data source.
     *
     * @param inDomainMapper
     *            the mapper responsible for fetching the data from the data source
     */
    public void setDomainMapper(final DomainMapper<Request, Response> inDomainMapper)
    {
        domainMapper = inDomainMapper;
    }

}
