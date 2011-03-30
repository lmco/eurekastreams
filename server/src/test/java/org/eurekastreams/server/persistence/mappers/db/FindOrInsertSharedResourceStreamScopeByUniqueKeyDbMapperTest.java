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
import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for FindOrInsertSharedResourceStreamScopeByUniqueKeyDbMapper.
 * 
 */
public class FindOrInsertSharedResourceStreamScopeByUniqueKeyDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private FindOrInsertSharedResourceStreamScopeByUniqueKeyDbMapper sut;

    /**
     * Set up.
     */
    @Before
    public void setup()
    {
        FindOrInsertSharedResourceByUniqueKeyDbMapper srMapper = new FindOrInsertSharedResourceByUniqueKeyDbMapper();
        srMapper.setEntityManager(getEntityManager());

        sut = new FindOrInsertSharedResourceStreamScopeByUniqueKeyDbMapper(srMapper);
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testNullInput()
    {
        sut.execute(null);
    }

    /**
     * Test.
     */
    @Test
    public void testWithMatch()
    {
        final long ssid = 100L;
        StreamScope result = sut.execute("resource1");
        assertNotNull(result);
        assertEquals(ssid, result.getId());
    }

    /**
     * Test.
     */
    @Test
    public void testWithMatchVerifyCaseInsensitive()
    {
        final long ssid = 100L;
        StreamScope result = sut.execute("ResoUrce1");
        assertNotNull(result);
        assertEquals(ssid, result.getId());
    }

    /**
     * Test.
     */
    @Test
    public void testWithNoMatch()
    {
        StreamScope result = sut.execute("fooResource");
        assertNotNull(result);
        assertTrue(result.getId() > 0);
    }
}
