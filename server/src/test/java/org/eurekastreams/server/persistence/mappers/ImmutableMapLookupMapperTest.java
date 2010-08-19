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
package org.eurekastreams.server.persistence.mappers;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests ImmutableMapLookupMapper.
 */
public class ImmutableMapLookupMapperTest
{
    /** Wrapped map. */
    private Map<String, Integer> map = new HashMap<String, Integer>();

    /** The SUT. */
    private ImmutableMapLookupMapper<String, Integer> sut;

    /**
     * Constructor.
     */
    public ImmutableMapLookupMapperTest()
    {
        map.put("apples", 2);
        map.put("bananas", 8);
    }

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new ImmutableMapLookupMapper<String, Integer>(map);
    }

    /**
     * Tests a found item.
     */
    @Test
    public void testFound()
    {
        Integer result = sut.execute("apples");
        assertEquals(new Integer(2), result);
    }

    /**
     * Tests a not found item.
     */
    @Test
    public void testNotFound()
    {
        Integer result = sut.execute("grapes");
        assertNull(result);
    }

}
