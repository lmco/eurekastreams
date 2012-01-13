/*
 * Copyright (c) 2010-2012 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.cache;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for SharedResourceRequestToUniqueKeyTransformer.
 */
public class SharedResourceUniqueKeyToCacheKeySuffixTransformerTest
{
    /**
     * System under test.
     */
    private Transformer<String, String> sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new SharedResourceUniqueKeyToCacheKeySuffixTransformer();
    }

    /**
     * Test transforming null.
     */
    @Test
    public void testNull()
    {
        Assert.assertNull(sut.transform(null));
    }

    /**
     * Asserts a result meets the desired criteria.
     *
     * @param result
     *            Result to test.
     */
    private void assertGoodKey(final String result)
    {
        final int maxSize = 100;
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.length() <= maxSize);
    }

    /**
     * Test transforming a valid request with a valid unique key.
     */
    @Test
    public void test()
    {
        final String result = sut.transform("HI");
        assertGoodKey(result);
    }

    /**
     * Test that huge values convert to reasonably-sized values so big URLs convert to values small enough to be valid
     * memcached keys.
     */
    @Test
    public void testWithLongValue()
    {
        final int bigSize = 250;
        StringBuilder sb = new StringBuilder("http://www.example.com");
        while (sb.length() < bigSize * 2)
        {
            sb.append("/segmentToMakeItLong");
        }
        final String result = sut.transform(sb.toString());
        assertGoodKey(result);
    }

    /**
     * Test.
     */
    @Test
    public void testWithSame()
    {
        String url = "http://www.example.com/segment?parm1=value1&parm2=value2";

        final String result1 = sut.transform(url);
        assertGoodKey(result1);

        final String result2 = sut.transform(url);
        assertGoodKey(result2);

        assertEquals(result1, result2);
    }

    /**
     * Test.
     */
    @Test
    public void testWithDifferent()
    {
        String url = "http://www.example.com/segment?parm1=value1&parm2=value2";
        final String result1 = sut.transform(url);
        assertGoodKey(result1);

        final String result2 = sut.transform(url + "1");
        assertGoodKey(result2);

        assertFalse(result1.equals(result2));
    }
}
