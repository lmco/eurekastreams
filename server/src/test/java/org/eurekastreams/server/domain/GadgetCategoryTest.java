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
public class GadgetCategoryTest
{

    /**
     * Subject under test.
     */
    private GalleryItemCategory sut;

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new GalleryItemCategory("News");
    }

    /**
     * Test theme getter & setter.
     */
    @Test
    public void testName()
    {
        String value = "News";
        assertEquals("property should be gotten", value, sut.getName());
        assertEquals(value, sut.toString());
    }

    /**
     * Test getting and setting the name.
     */
    @Test
    public void testGetSetName()
    {
        String myName = "sdlkfjsdljfsdl";
        sut.setName(myName);
        assertEquals(myName, sut.getName());
    }
}
