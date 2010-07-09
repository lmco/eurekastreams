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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests mapper to get expired activity ids.
 */
public class GetExpiredActivityIdsTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetExpiredActivities sut;

    /**
     * Test execute method.
     *
     * @throws Exception
     *             on error.
     */
    @Test
    public void testExecute() throws Exception
    {
        final Long activity1 = new Long(6791L);
        final Long activity2 = new Long(6792L);

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        List<Long> results = sut.execute(dateFormat.parse("10/29/2009"));
        assertEquals(2, results.size());
        assertEquals(activity1, results.get(0));
        assertEquals(activity2, results.get(1));

        results = sut.execute(dateFormat.parse("10/29/2008"));
        assertEquals(0, results.size());
    }
}
