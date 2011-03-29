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
package org.eurekastreams.server.domain.strategies;

import java.io.Serializable;

import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Mapper that chains two other mappers.
 */
public class ChainedMapperWrapperMapper implements DomainMapper<Serializable, Serializable>
{
    /**
     * The first mapper.
     */
    private DomainMapper<Serializable, Serializable> mapper1;

    /**
     * The second mapper.
     */
    private DomainMapper<Serializable, Serializable> mapper2;

    /**
     * Constructor.
     * 
     * @param inMapper1
     *            first mapper
     * @param inMapper2
     *            second mapper
     */
    public ChainedMapperWrapperMapper(final DomainMapper<Serializable, Serializable> inMapper1,
            final DomainMapper<Serializable, Serializable> inMapper2)
    {
        mapper1 = inMapper1;
        mapper2 = inMapper2;
    }

    /**
     * Transform the input request by passing it into the wrapped mapper.
     * 
     * @param inRequest
     *            the request
     * @return the result of the second mapper
     */
    @Override
    public Serializable execute(final Serializable inRequest)
    {
        Serializable results1 = mapper1.execute(inRequest);
        return mapper2.execute(results1);
    }
}
