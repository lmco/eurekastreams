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

import static junit.framework.Assert.assertEquals;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test TokenContentEmailAddressBuilder.
 */
public class TokenContentEmailAddressBuilderTest
{
    /** Test data. */
    private static final String TO_ADDR_START = "system";

    /** Test data. */
    private static final String TO_ADDR_END = "@eurekastreams.org";

    /** Test data. */
    private static final String TOKEN = "AAABBBAAACCC";

    /** Test data. */
    private static final long PERSON_ID = 800L;

    /** Used for mocking objects. */
    private final JUnit4Mockery mockery = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Creates the token. */
    private final TokenEncoder tokenEncoder = mockery.mock(TokenEncoder.class, "tokenEncoder");

    /** Gets the user's key. */
    private final DomainMapper<Long, byte[]> cryptoKeyDao = mockery.mock(DomainMapper.class, "cryptoKeyDao");

    /** SUT. */
    private TokenContentEmailAddressBuilder sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new TokenContentEmailAddressBuilder(tokenEncoder, cryptoKeyDao, TO_ADDR_START + TO_ADDR_END);
    }

    /**
     * Test.
     */
    @Test
    public void test()
    {
        final String tokenContent = "stuffToGoInTheToken";
        final byte[] key = "key".getBytes();

        mockery.checking(new Expectations()
        {
            {
                allowing(cryptoKeyDao).execute(PERSON_ID);
                will(returnValue(key));

                allowing(tokenEncoder).encode(tokenContent, key);
                will(returnValue(TOKEN));
            }
        });

        String result = sut.build(tokenContent, PERSON_ID);
        mockery.assertIsSatisfied();
        assertEquals(TO_ADDR_START + "+" + TOKEN + TO_ADDR_END, result);
    }
}
