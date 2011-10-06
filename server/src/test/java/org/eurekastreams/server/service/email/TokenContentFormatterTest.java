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
package org.eurekastreams.server.service.email;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.server.domain.EntityType;
import org.junit.Before;
import org.junit.Test;

/**
 * Test TokenContentFormatter. Note: Most tests are all round-trip so that the encoded format can be opaque. Two SUTs
 * are used to insure no state is saved between build and parse.
 */
public class TokenContentFormatterTest
{
    /** SUT for building. */
    private TokenContentFormatter sut1;

    /** SUT for parsing. */
    private TokenContentFormatter sut2;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut1 = new TokenContentFormatter();
        sut2 = new TokenContentFormatter();
    }

    /**
     * Test.
     */
    @Test
    public void testWithMap()
    {
        Map<String, Long> data = new HashMap<String, Long>();
        data.put("s", 888L);
        data.put("p", 4507L);

        String token = sut1.build(data);

        Map<String, Long> result = sut2.parse(token);

        assertEquals(2, result.size());
        assertEquals((Long) 888L, result.get("s"));
        assertEquals((Long) 4507L, result.get("p"));
    }

    /**
     * Test.
     */
    @Test
    public void testWithPersonStream()
    {
        String token = sut1.buildForStream(EntityType.PERSON, 98L, 42L);

        Map<String, Long> result = sut2.parse(token);

        assertEquals(2, result.size());
        assertEquals((Long) 98L, result.get(TokenContentFormatter.META_KEY_PERSON_STREAM));
        assertEquals((Long) 42L, result.get(TokenContentFormatter.META_KEY_ACTOR));
    }

    /**
     * Test.
     */
    @Test
    public void testWithGroupStream()
    {
        String token = sut1.buildForStream(EntityType.GROUP, 8L, 42L);

        Map<String, Long> result = sut2.parse(token);

        assertEquals(2, result.size());
        assertEquals((Long) 8L, result.get(TokenContentFormatter.META_KEY_GROUP_STREAM));
        assertEquals((Long) 42L, result.get(TokenContentFormatter.META_KEY_ACTOR));
    }


    /**
     * Tests encodeForStream.
     */
    @Test(expected = Exception.class)
    public void testWithOtherStream()
    {
        sut1.buildForStream(EntityType.RESOURCE, 8L, 42L);
    }


    /**
     * Test.
     */
    @Test
    public void testWithActivity()
    {
        String token = sut1.buildForActivity(6789L, 42L);

        Map<String, Long> result = sut2.parse(token);

        assertEquals(2, result.size());
        assertEquals((Long) 6789L, result.get(TokenContentFormatter.META_KEY_ACTIVITY));
        assertEquals((Long) 42L, result.get(TokenContentFormatter.META_KEY_ACTOR));
    }

    // ---------- FORMAT-SPECIFIC TESTS ----------

    /**
     * Tests parsing.
     */
    @Test
    public void testParseValid()
    {
        Map<String, Long> data = sut2.parse("s888p4507");
        assertNotNull(data);
        assertEquals(2, data.size());
        assertEquals(888L, (long) data.get("s"));
        assertEquals(4507L, (long) data.get("p"));
    }

    /**
     * Tests parsing.
     */
    @Test
    public void testParseNoTag()
    {
        Map<String, Long> data = sut2.parse("888");
        assertNull(data);
    }

    /**
     * Tests parsing.
     */
    @Test
    public void testParseNoValue()
    {
        Map<String, Long> data = sut2.parse("p");
        assertNull(data);
    }

    /**
     * Tests parsing.
     */
    @Test
    public void testParseNoPair()
    {
        Map<String, Long> data = sut2.parse("-a1");
        assertNull(data);
    }

}
