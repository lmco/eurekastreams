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
package org.eurekastreams.server.persistence.mappers.stream;

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Class for retrieving a the personal stream of an entity by the entity id.
 *
 */
public class GetStreamByOwnerIdTest extends MapperTest
{
    /**
     * Group system under test.
     */
    private GetStreamByOwnerId groupSut;

    /**
     * Person system under test.
     */
    private GetStreamByOwnerId personSut;

    /**
     * Preparation for the test suite.
     */
    @Before
    public void setup()
    {
        groupSut = new GetStreamByOwnerId(EntityType.GROUP);
        groupSut.setEntityManager(getEntityManager());

        personSut = new GetStreamByOwnerId(EntityType.PERSON);
        personSut.setEntityManager(getEntityManager());
    }

    /**
     * Test execute method for a person configured mapper.
     */
    @Test
    public void testExecutePerson()
    {
        final Long ownerId = 42L;
        assertEquals(5L, personSut.execute(ownerId).getId());
    }

    /**
     * Test execute method for a group configured mapper.
     */
    @Test
    public void testExecuteGroup()
    {
        final Long ownerId = 1L;
        assertEquals(9L, groupSut.execute(ownerId).getId());
    }
}
