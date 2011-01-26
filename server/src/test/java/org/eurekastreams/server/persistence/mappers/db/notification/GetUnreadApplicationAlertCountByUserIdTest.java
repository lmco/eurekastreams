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

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests mapper to get unread alert counts.
 */
public class GetUnreadApplicationAlertCountByUserIdTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetUnreadApplicationAlertCountByUserId sut;

    /**
     * Tests execute method.
     */
    @Test
    public void testExecute()
    {
        final long userId = 99L;
        int result = sut.execute(userId);
        assertEquals(2, result);
    }
}
