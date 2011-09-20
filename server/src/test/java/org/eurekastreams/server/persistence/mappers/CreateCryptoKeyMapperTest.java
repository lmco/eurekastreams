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
package org.eurekastreams.server.persistence.mappers;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertTrue;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.internal.ArrayComparisonFailure;

/**
 * Tests CreateCryptoKeyMapper.
 */
public class CreateCryptoKeyMapperTest
{
    /** SUT. */
    private CreateCryptoKeyMapper sut;

    /**
     * Setup before each test.
     *
     * @throws NoSuchAlgorithmException
     *             Could fail if AES is not installed in JRE.
     */
    @Before
    public void setUp() throws NoSuchAlgorithmException
    {
        // KeyGenerator.generateKey() is final, thus jMock cannot mock it. So we have to use the real thing.
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        sut = new CreateCryptoKeyMapper(keyGenerator);
    }

    /**
     * Tests.
     */
    @Test(expected = ArrayComparisonFailure.class)
    public void testExecute()
    {
        byte[] result1 = sut.execute(null);
        assertNotNull(result1);
        assertTrue(result1.length > 0);

        byte[] result2 = sut.execute(null);
        assertNotNull(result2);
        assertTrue(result2.length > 0);

        // The keys SHOULD be different, but there isn't an Assert.assertArrayNotEquals, so I coded for the reverse...
        // Thus I expect this line to throw an exception
        Assert.assertArrayEquals(result1, result2);
        // If it didn't, then throw an exception because two subsequent keys matched
        fail("Keys should be different on subsequent calls");
    }
}
