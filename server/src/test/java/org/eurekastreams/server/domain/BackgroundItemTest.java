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
package org.eurekastreams.server.domain;

import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Background test class.
 *
 */
public class BackgroundItemTest
{

    /**
     * Subject under test.
     */
    private BackgroundItem sut;

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new BackgroundItem("sports", BackgroundItemType.INTEREST);
    }

    /**
     * Test theme getter & setter.
     */
    @Test
    public void testName()
    {
        String value = "sports";
        assertEquals("property should be gotten", value, sut.getName());
    }

    /**
     * Test theme getter & setter.
     */
    @Test
    public void testType()
    {
        assertEquals("property should be gotten", BackgroundItemType.INTEREST, sut.getBackgroundType());
    }
}
