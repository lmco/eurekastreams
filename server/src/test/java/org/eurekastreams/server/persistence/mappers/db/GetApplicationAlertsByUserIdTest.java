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

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eurekastreams.server.domain.ApplicationAlertNotification;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests mapper to get alerts for a user.
 */
public class GetApplicationAlertsByUserIdTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetApplicationAlertsByUserId sut;

    /**
     * Test execute method.
     */
    @Test
    public void testExecute()
    {
        final Long smithersId = 98L;
        final Long burnsId = 99L;
        final int count = 10;
        List<ApplicationAlertNotification> results = sut.execute(burnsId, count);
        assertEquals(3, results.size());

        results = sut.execute(burnsId, 2);
        assertEquals(2, results.size());

        results = sut.execute(smithersId, count);
        assertEquals(1, results.size());
    }
}
