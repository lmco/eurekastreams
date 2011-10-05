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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.TreeMap;

import org.eurekastreams.server.domain.EntityType;
import org.junit.Before;
import org.junit.Test;

/**
 * Test TokenEncoder.
 */
public class TokenEncoderTest
{
    /** SUT. */
    private TokenEncoder sut;

    /** Fixture: algorithm name. */
    private static final String ALGORITHM = "AES";

    /** Test data: user key. */
    private static final byte[] USER_KEY = "ThisIsASecretKey".getBytes();

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new TokenEncoder(ALGORITHM);
    }

    /**
     * Tests encoding.
     */
    @Test
    public void testEncodeValid()
    {
        Map<String, Long> data = new TreeMap<String, Long>();
        data.put("s", 888L);
        data.put("p", 4507L);

        String token = sut.encode(data, USER_KEY);

        assertEquals("X8xF5hXq+v3HsPnb0F5wXw==", token);
    }

    /**
     * Tests encoding.
     */
    @Test
    public void testEncodeBadKey()
    {
        Map<String, Long> data = new TreeMap<String, Long>();
        data.put("s", 888L);
        data.put("p", 4507L);

        String token = sut.encode(data, "2short".getBytes());

        assertNull(token);
    }

    /**
     * Tests decoding.
     */
    @Test
    public void testDecodeValid()
    {
        Map<String, Long> data = sut.decode("X8xF5hXq+v3HsPnb0F5wXw==", USER_KEY);
        assertNotNull(data);
        assertEquals(2, data.size());
        assertEquals(888L, (long) data.get("s"));
        assertEquals(4507L, (long) data.get("p"));
    }

    /**
     * Tests decoding.
     */
    @Test
    public void testDecodeBadBase64()
    {
        Map<String, Long> data = sut.decode("&^#(#", USER_KEY);
        assertNull(data);
    }

    /**
     * Tests decoding.
     */
    @Test
    public void testDecodeBadKey()
    {
        Map<String, Long> data = sut.decode("X8xF5hXq+v3HsPnb0F5wXw==", "2short".getBytes());
        assertNull(data);
    }

    /**
     * Tests decoding.
     */
    @Test
    public void testDecodeWrongKey()
    {
        Map<String, Long> data = sut.decode("X8xF5hXq+v3HsPnb0F5wXw==", "ThisIsNotTheRightKey".getBytes());
        assertNull(data);
    }

    /**
     * Tests decoding.
     */
    @Test
    public void testDecodeNoTag()
    {
        // encoded form of "888"
        Map<String, Long> data = sut.decode("HITQvyyBOqWbzuMRxQmq+A==", USER_KEY);
        assertNull(data);
    }

    /**
     * Tests decoding.
     */
    @Test
    public void testDecodeNoValue()
    {
        // encoded form of "p"
        Map<String, Long> data = sut.decode("EEcx6kWZsDainEankWK7Pw==", USER_KEY);
        assertNull(data);
    }

    /**
     * Tests decoding.
     */
    @Test
    public void testDecodeNoPair()
    {
        // encoded form of "-a1"
        Map<String, Long> data = sut.decode("C/9+GotQA+vyWRO34pqEcQ==", USER_KEY);
        assertNull(data);
    }

    /**
     * Tests couldBeToken.
     */
    @Test
    public void testCouldBeTokenYes()
    {
        assertTrue(sut.couldBeToken("C/9+GotQA+vyWRO34pqEcQ=="));
    }

    /**
     * Tests couldBeToken.
     */
    @Test
    public void testCouldBeTokenNo()
    {
        assertFalse(sut.couldBeToken("C/9+GotQ.A+vyWRO34pqEcQ=="));
    }

    /**
     * Tests encodeForStream.
     */
    @Test
    public void testEncodeForStreamPerson()
    {
        String token = sut.encodeForStream(EntityType.PERSON, 98L, 42L, USER_KEY);
        assertEquals("OnXIBYqExuxqrHyKCdSv6Q==", token);
    }

    /**
     * Tests encodeForStream.
     */
    @Test
    public void testEncodeForStreamGroup()
    {
        String token = sut.encodeForStream(EntityType.GROUP, 8L, 42L, USER_KEY);
        assertEquals("U/e4AnHIn3D/Zvx40Y5aGQ==", token);
    }

    /**
     * Tests encodeForStream.
     */
    @Test(expected = Exception.class)
    public void testEncodeForStreamOther()
    {
        sut.encodeForStream(EntityType.RESOURCE, 1L, 42L, USER_KEY);
    }

    /**
     * Tests encodeForActivity.
     */
    @Test
    public void testEncodeForActivity()
    {
        String token = sut.encodeForActivity(6789L, 42L, USER_KEY);
        assertEquals("66Zwtw6PeCVMgpqryeVLyQ==", token);
    }
}
