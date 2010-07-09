/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
/**
 *
 */
package org.eurekastreams.server.domain;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the Need class for basic functionality.
 *
 */
public class TestBackgroundType
{

    /**
     * Reusable message for each test.
     */
    private String message = null;

    /**
     * Called before each test method.
     *
     * @throws java.lang.Exception
     *             if an exception occurs.
     */
    @Before
    public final void setUp() throws Exception
    {
        message = null;
    }

    /**
     * Called after each test method.
     *
     * @throws java.lang.Exception
     *             if an exception occurs.
     */
    @After
    public final void tearDown() throws Exception
    {
        message = null;
    }

    /**
     * Categories should be turned into strings the right way.
     */
    @Test
    public void testEnum()
    {
        message = "Enum string handling should work";

        // this ensures that the mapping is bidirectional
        // without checking the actual value of the string
        BackgroundItemType[] enums = BackgroundItemType.values();
        for (int i = 0; i < enums.length; i++)
        {
            assertEquals(message, enums[i], BackgroundItemType.toEnum(enums[i].toString()));
            assertEquals(message, enums[i], BackgroundItemType.valueOf(enums[i].name()));
        }

        // see what happens when you pass in something not parsable
        assertNull(message, BackgroundItemType.toEnum("unrecognized"));
    }
}
