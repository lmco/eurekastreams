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

import org.eurekastreams.server.domain.UnreadInAppNotificationCountDTO;
import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests mapper to get unread alert counts.
 */
public class GetUnreadInAppNotificationCountsByUserIdTest extends MapperTest
{
    /** Assertion message. */
    private static final String NORMAL_MSG = "Wrong count for normal priority";

    /** Assertion message. */
    private static final String HIGH_MSG = "Wrong count for high priority";

    /**
     * System under test.
     */
    private DomainMapper<Long, UnreadInAppNotificationCountDTO> sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new GetUnreadInAppNotificationCountsByUserId();
        ((BaseDomainMapper) sut).setEntityManager(getEntityManager());
    }

    /**
     * Tests execute method.
     */
    @Test
    public void testExecuteBoth()
    {
        UnreadInAppNotificationCountDTO result = sut.execute(42L);
        assertEquals(NORMAL_MSG, 1, result.getNormalPriority());
        assertEquals(HIGH_MSG, 2, result.getHighPriority());
    }

    /**
     * Tests execute method.
     */
    @Test
    public void testExecuteOnlyNormal()
    {
        UnreadInAppNotificationCountDTO result = sut.execute(98L);
        assertEquals(NORMAL_MSG, 1, result.getNormalPriority());
        assertEquals(HIGH_MSG, 0, result.getHighPriority());
    }

    /**
     * Tests execute method.
     */
    @Test
    public void testExecuteOnlyHigh()
    {
        UnreadInAppNotificationCountDTO result = sut.execute(99L);
        assertEquals(NORMAL_MSG, 0, result.getNormalPriority());
        assertEquals(HIGH_MSG, 1, result.getHighPriority());
    }

    /**
     * Tests execute method.
     */
    @Test
    public void testExecuteNeither()
    {
        UnreadInAppNotificationCountDTO result = sut.execute(4507L);
        assertEquals(NORMAL_MSG, 0, result.getNormalPriority());
        assertEquals(HIGH_MSG, 0, result.getHighPriority());
    }
}
