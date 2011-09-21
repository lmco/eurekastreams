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

import java.util.Map;
import java.util.TreeMap;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test TokenEncoder.
 */
public class TokenEncoderTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** SUT. */
    private TokenEncoder sut;

    /** Fixture: key DAO. */
    @SuppressWarnings("unchecked")
    private final DomainMapper<Long, byte[]> getPersonCryptoKeyDao = context.mock(DomainMapper.class,
            "getPersonCryptoKeyDao");

    /** Fixture: algorithm name. */
    private static final String ALGORITHM = "AES";

    /** Test data: user key. */
    private static final byte[] USER_KEY = "ThisIsASecretKey".getBytes();

    /** Test data: user key. */
    private static final long USER_ID = 8L;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new TokenEncoder(ALGORITHM, getPersonCryptoKeyDao);

        context.checking(new Expectations()
        {
            {
                allowing(getPersonCryptoKeyDao).execute(USER_ID);
                will(returnValue(USER_KEY));
            }
        });
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

        String token = sut.encode(data, USER_ID);
        context.assertIsSatisfied();

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

        context.checking(new Expectations()
        {
            {
                allowing(getPersonCryptoKeyDao).execute(5L);
                will(returnValue("2short".getBytes()));
            }
        });

        String token = sut.encode(data, 5L);
        context.assertIsSatisfied();

        assertNull(token);
    }

    /**
     * Tests decoding.
     */
    @Test
    public void testDecodeValid()
    {
        Map<String, Long> data = sut.decode("X8xF5hXq+v3HsPnb0F5wXw==", USER_ID);
        context.assertIsSatisfied();

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
        Map<String, Long> data = sut.decode("&^#(#", USER_ID);
        context.assertIsSatisfied();

        assertNull(data);
    }

    /**
     * Tests decoding.
     */
    @Test
    public void testDecodeBadKey()
    {
        context.checking(new Expectations()
        {
            {
                allowing(getPersonCryptoKeyDao).execute(5L);
                will(returnValue("2short".getBytes()));
            }
        });

        Map<String, Long> data = sut.decode("X8xF5hXq+v3HsPnb0F5wXw==", 5L);
        context.assertIsSatisfied();

        assertNull(data);
    }

    /**
     * Tests decoding.
     */
    @Test
    public void testDecodeWrongKey()
    {
        context.checking(new Expectations()
        {
            {
                allowing(getPersonCryptoKeyDao).execute(5L);
                will(returnValue("ThisIsNotTheRightKey".getBytes()));
            }
        });

        Map<String, Long> data = sut.decode("X8xF5hXq+v3HsPnb0F5wXw==", 5L);
        context.assertIsSatisfied();

        assertNull(data);
    }

    /**
     * Tests decoding.
     */
    @Test
    public void testDecodeNoTag()
    {
        // encoded form of "888"
        Map<String, Long> data = sut.decode("HITQvyyBOqWbzuMRxQmq+A==", USER_ID);
        context.assertIsSatisfied();

        assertNull(data);
    }

    /**
     * Tests decoding.
     */
    @Test
    public void testDecodeNoValue()
    {
        // encoded form of "p"
        Map<String, Long> data = sut.decode("EEcx6kWZsDainEankWK7Pw==", USER_ID);
        context.assertIsSatisfied();

        assertNull(data);
    }

    /**
     * Tests decoding.
     */
    @Test
    public void testDecodeNoPair()
    {
        // encoded form of "-a1"
        Map<String, Long> data = sut.decode("C/9+GotQA+vyWRO34pqEcQ==", USER_ID);
        context.assertIsSatisfied();

        assertNull(data);
    }
}
