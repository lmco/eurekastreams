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
package org.eurekastreams.server.persistence.mappers.chained;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

/**
 * Test fixture for NonNullResultsCombiner.
 */
public class NonNullResultsCombinerTest
{
    /**
     * Test combine().
     */
    @Test
    public void testCombine()
    {
        Object param1 = new Object();
        Object param2 = new Object();

        assertSame(param1, new NonNullResultsCombiner<Object>().combine(param1, param2));
        assertSame(param1, new NonNullResultsCombiner<Object>().combine(param1, null));
        assertSame(param2, new NonNullResultsCombiner<Object>().combine(null, param2));
        assertNull(new NonNullResultsCombiner<Object>().combine(null, null));
    }
}
