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

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the GetAllPersonIds mapper.
 *
 */
public class GetAllPersonIdsTest extends MapperTest
{
    /**
     * Local instance of sut.
     */
    private GetAllPersonIds sut;
    
    /**
     * Total number of user ids.
     */
    private static final int TOTAL_USER_IDS = 5;
    
    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new GetAllPersonIds();
        sut.setEntityManager(getEntityManager());
    }
    
    /**
     * Simple test for full number of account ids.
     */
    @Test
    public void testGet()
    {
        List<Long> results = sut.execute(null);
        assertEquals(TOTAL_USER_IDS, results.size());
    }
}
