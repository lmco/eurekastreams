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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Background test class.
 *
 */
public class BackgroundTest
{

    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Subject under test.
     */
    private Background sut;

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new Background(new Person("mortimer", "richard", "snerd", "mort", "mort"));
    }

    /**
     * Test the name getter.
     */
    @Test
    public void testConstructor()
    {
    }

    /**
     * Test name.
     */
    @Test
    public void testBackgroundItemsSetAndGet()
    {
        String message = "should get and set background lists appropriately";

        // test for equivalence of sets so that it doesn't matter
        // if the order of elements is changed during parsing or formatting
        // also parsing removes extraneous commas

        List<BackgroundItem> expectedBackgroundItems = new ArrayList<BackgroundItem>();
        expectedBackgroundItems.add(new BackgroundItem("sports", BackgroundItemType.INTEREST));
        expectedBackgroundItems.add(new BackgroundItem("music", BackgroundItemType.INTEREST));
        expectedBackgroundItems.add(new BackgroundItem("software", BackgroundItemType.INTEREST));
        sut.setBackgroundItems(expectedBackgroundItems, BackgroundItemType.INTEREST);
        List<BackgroundItem> actualBackgroundItems = sut.getBackgroundItems(BackgroundItemType.INTEREST);
        assertEquals(message, expectedBackgroundItems, actualBackgroundItems);

        expectedBackgroundItems = new ArrayList<BackgroundItem>();
        expectedBackgroundItems.add(new BackgroundItem("nobel", BackgroundItemType.HONOR));
        sut.setBackgroundItems(expectedBackgroundItems, BackgroundItemType.HONOR);
        actualBackgroundItems = sut.getBackgroundItems(BackgroundItemType.HONOR);
        assertEquals(message, expectedBackgroundItems, actualBackgroundItems);

        expectedBackgroundItems = new ArrayList<BackgroundItem>();
        expectedBackgroundItems.add(new BackgroundItem("JUG", BackgroundItemType.AFFILIATION));
        sut.setBackgroundItems(expectedBackgroundItems, BackgroundItemType.AFFILIATION);
        actualBackgroundItems = sut.getBackgroundItems(BackgroundItemType.AFFILIATION);
        assertEquals(message, expectedBackgroundItems, actualBackgroundItems);

        expectedBackgroundItems = new ArrayList<BackgroundItem>();
        expectedBackgroundItems.add(new BackgroundItem("basket weaving", BackgroundItemType.SKILL));
        sut.setBackgroundItems(expectedBackgroundItems, BackgroundItemType.SKILL);
        actualBackgroundItems = sut.getBackgroundItems(BackgroundItemType.SKILL);
        assertEquals(message, expectedBackgroundItems, actualBackgroundItems);

        // unimplemented type shouldn't call any mocks
        // and should throw an exception instead
        String exMessage = "should throw exception on unhandled enum";
        try
        {
            BackgroundItemType type = context.mock(BackgroundItemType.class);
            sut.getBackgroundItems(type);
            fail(exMessage);
        }
        catch (IllegalArgumentException arg)
        {
            assertNotNull(exMessage, arg);
        }

        // unimplemented type shouldn't call any mocks
        // and should throw an exception instead
        try
        {
            BackgroundItemType type = context.mock(BackgroundItemType.class);
            sut.setBackgroundItems(null, type);
            fail(exMessage);
        }
        catch (IllegalArgumentException arg)
        {
            assertNotNull(exMessage, arg);
        }

    }

    /**
     * Assert asking for background items with NOT_SET throws an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetBackgroundItemsNotSet()
    {
        sut.getBackgroundItems(BackgroundItemType.NOT_SET);
    }

    /**
     * Assert setting background items with NOT_SET throws an IllegalArgumentException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetBackgroundItemsNotSet()
    {
        List<BackgroundItem> items = new ArrayList<BackgroundItem>();
        items.add(new BackgroundItem("JUG", BackgroundItemType.NOT_SET));
        sut.setBackgroundItems(items, BackgroundItemType.NOT_SET);
    }
}
