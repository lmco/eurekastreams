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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.TreeMap;

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

        String token = sut.encode("p4507s888", USER_KEY);

        assertEquals("X8xF5hXq+v3HsPnb0F5wXw==", token);
    }

    /**
     * Tests encoding.
     */
    @Test
    public void testEncodeBadKey()
    {
        String token = sut.encode("p4507s888", "2short".getBytes());
        assertNull(token);
    }

    /**
     * Tests decoding.
     */
    @Test
    public void testDecodeValid()
    {
        String data = sut.decode("X8xF5hXq+v3HsPnb0F5wXw==", USER_KEY);
        assertEquals("p4507s888", data);
    }

    /**
     * Tests decoding.
     */
    @Test
    public void testDecodeBadBase64()
    {
        String data = sut.decode("&^#(#", USER_KEY);
        assertNull(data);
    }

    /**
     * Tests decoding.
     */
    @Test
    public void testDecodeBadKey()
    {
        String data = sut.decode("X8xF5hXq+v3HsPnb0F5wXw==", "2short".getBytes());
        assertNull(data);
    }

    /**
     * Tests decoding.
     */
    @Test
    public void testDecodeWrongKey()
    {
        String data = sut.decode("X8xF5hXq+v3HsPnb0F5wXw==", "ThisIsNotTheRightKey".getBytes());
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

}
