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
package org.eurekastreams.server.persistence.mappers.db.notification;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eurekastreams.server.domain.InAppNotificationDTO;
import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests GetInAppNotificationsByUserId.
 */
public class GetInAppNotificationsByUserIdTest extends MapperTest
{
    /** SUT. */
    private DomainMapper<Long, List<InAppNotificationDTO>> sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new GetInAppNotificationsByUserId(5);
        ((BaseDomainMapper) sut).setEntityManager(getEntityManager());
    }

    /**
     * Tests execute.
     */
    @Test
    public void testExecute()
    {
        // test
        final long id42 = 42L;
        List<InAppNotificationDTO> results = sut.execute(id42);

        // verify
        assertEquals(5, results.size());
        assertEquals(7, results.get(0).getId());
        assertEquals(6, results.get(1).getId());
        assertEquals(1, results.get(2).getId());
        assertEquals(8, results.get(3).getId());
        assertEquals(5, results.get(4).getId());
    }
}
