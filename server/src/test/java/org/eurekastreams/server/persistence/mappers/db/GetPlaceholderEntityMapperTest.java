/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;

/**
 * Tests GetPlaceholderEntityMapper.
 */
public class GetPlaceholderEntityMapperTest extends MapperTest
{
    /**
     * Tests the mapper.
     */
    @Test
    public void test()
    {
        final long personId = 42L;

        DomainMapper<Long, Person> sut = new GetPlaceholderEntityMapper<Person>(Person.class);
        ((BaseDomainMapper) sut).setEntityManager(getEntityManager());
        Person result = sut.execute(personId);
        assertNotNull(result);
        assertEquals(personId, result.getId());
    }
}
