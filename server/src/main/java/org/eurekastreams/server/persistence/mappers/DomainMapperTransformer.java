/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import java.io.Serializable;

import org.eurekastreams.server.persistence.mappers.cache.Transformer;

/**
 * Transformer that wraps a DomainMapper.
 */
public class DomainMapperTransformer implements DomainMapper<Serializable, Serializable>
{
    /**
     * The mapper to wrap.
     */
    private DomainMapper<Serializable, Serializable> domainMapper;

    /**
     * The transformer to transform the mapper results.
     */
    private Transformer<Serializable, Serializable> transformer;

    /**
     * Constructor.
     * 
     * @param inDomainMapper
     *            the mapper to wrap
     * @param inTransformer
     *            the transformer to transform the mapper results
     */
    public DomainMapperTransformer(final DomainMapper<Serializable, Serializable> inDomainMapper,
            final Transformer<Serializable, Serializable> inTransformer)
    {
        domainMapper = inDomainMapper;
        transformer = inTransformer;
    }

    /**
     * Call the wrapped mapper, transform and return the results.
     * 
     * @param inRequest
     *            the request to pass into the mapper
     * @return transformed results from the mapper
     */
    @Override
    public Serializable execute(final Serializable inRequest)
    {
        Serializable results = domainMapper.execute(inRequest);
        return transformer.transform(results);
    }
}
