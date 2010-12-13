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
package org.eurekastreams.server.persistence.mappers.db;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.search.modelview.PersonPagePropertiesDTO;

/**
 * Mapper to get PersonPagePropertiesDTO.
 */
public class GetPersonPagePropertiesByIdDbMapper implements DomainMapper<Long, PersonPagePropertiesDTO>
{
    /**
     * Transformer to convert person to PersonPageProperties.
     */
    private Transformer<Person, PersonPagePropertiesDTO> transformer;

    /**
     * {@link FindByIdMapper}.
     */
    private FindByIdMapper<Person> personByIdMapper;

    /**
     * Constructor.
     * 
     * @param inPersonByIdMapper
     *            {@link FindByIdMapper}.
     * @param inTransformer
     *            Transformer to convert person to PersonPageProperties.
     */
    public GetPersonPagePropertiesByIdDbMapper(final FindByIdMapper<Person> inPersonByIdMapper,
            final Transformer<Person, PersonPagePropertiesDTO> inTransformer)
    {
        personByIdMapper = inPersonByIdMapper;
        transformer = inTransformer;
    }

    @Override
    public PersonPagePropertiesDTO execute(final Long inRequest)
    {
        return transformer.transform(personByIdMapper.execute(new FindByIdRequest("Person", inRequest)));
    }

}
