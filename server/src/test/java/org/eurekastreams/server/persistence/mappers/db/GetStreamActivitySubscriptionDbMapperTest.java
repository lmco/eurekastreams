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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.GetStreamActivitySubscriptionMapperRequest;
import org.junit.Test;
/**
 * Test fixture for ChangeStreamActivitySubscriptionDbMapper.
 */
public class GetStreamActivitySubscriptionDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private DomainMapper<GetStreamActivitySubscriptionMapperRequest, Boolean> sut;

    /**
     * Tests execute.
     */
    @Test
    public void testExecutePersonSubscribed()
    {
        sut = new GetStreamActivitySubscriptionDbMapper(EntityType.PERSON);
        ((BaseDomainMapper) sut).setEntityManager(getEntityManager());

        GetStreamActivitySubscriptionMapperRequest request = new GetStreamActivitySubscriptionMapperRequest(142, 98);
        assertTrue(sut.execute(request));
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecutePersonNotSubscribed()
    {
        sut = new GetStreamActivitySubscriptionDbMapper(EntityType.PERSON);
        ((BaseDomainMapper) sut).setEntityManager(getEntityManager());

        GetStreamActivitySubscriptionMapperRequest request = new GetStreamActivitySubscriptionMapperRequest(98, 99);
        assertFalse(sut.execute(request));
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecutePersonNotFollowing()
    {
        sut = new GetStreamActivitySubscriptionDbMapper(EntityType.PERSON);
        ((BaseDomainMapper) sut).setEntityManager(getEntityManager());

        GetStreamActivitySubscriptionMapperRequest request = new GetStreamActivitySubscriptionMapperRequest(42, 142);
        assertFalse(sut.execute(request));
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteGroupSubscribed()
    {
        sut = new GetStreamActivitySubscriptionDbMapper(EntityType.GROUP);
        ((BaseDomainMapper) sut).setEntityManager(getEntityManager());

        GetStreamActivitySubscriptionMapperRequest request = new GetStreamActivitySubscriptionMapperRequest(98, 1);
        assertTrue(sut.execute(request));
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteGroupNotSubscribed()
    {
        sut = new GetStreamActivitySubscriptionDbMapper(EntityType.GROUP);
        ((BaseDomainMapper) sut).setEntityManager(getEntityManager());

        GetStreamActivitySubscriptionMapperRequest request = new GetStreamActivitySubscriptionMapperRequest(99, 1);
        assertFalse(sut.execute(request));
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecuteGroupNotFollowing()
    {
        sut = new GetStreamActivitySubscriptionDbMapper(EntityType.GROUP);
        ((BaseDomainMapper) sut).setEntityManager(getEntityManager());

        GetStreamActivitySubscriptionMapperRequest request = new GetStreamActivitySubscriptionMapperRequest(99, 2);
        assertFalse(sut.execute(request));
    }

    /**
     * Tests attempting to create for unsupported type.
     */
    @Test(expected = Exception.class)
    public void testConstructInvalidType()
    {
        new GetStreamActivitySubscriptionDbMapper(EntityType.RESOURCE);
    }
}
