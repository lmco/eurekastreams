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
package org.eurekastreams.server.search.bridge;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for BackgroundItemListStringBridge.
 */
public class BackgroundItemListStringBridgeTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private BackgroundItemListStringBridge sut = new BackgroundItemListStringBridge();

    /**
     * Test objectToString on null object.
     */
    @Test
    public void testObjectToStringWhenNull()
    {
        assertNull(sut.objectToString(null));
    }

    /**
     * Test objectToString when we pass in the wrong type.
     */
    @Test
    public void testObjectToStringOnWrongType()
    {
        assertNull(sut.objectToString(3));
    }

    /**
     * Test objectToString with loaded data.
     */
    @Test
    public void testObjectToString()
    {
        List<BackgroundItem> items =
                Arrays.asList(new BackgroundItem("ACM", BackgroundItemType.AFFILIATION), new BackgroundItem(
                        "Skull and Crossbones", BackgroundItemType.AFFILIATION), new BackgroundItem("Java",
                        BackgroundItemType.SKILL), new BackgroundItem("C#", BackgroundItemType.SKILL),
                        new BackgroundItem("Technology", BackgroundItemType.INTEREST), new BackgroundItem("Apple",
                                BackgroundItemType.INTEREST), new BackgroundItem("SRA", BackgroundItemType.HONOR),
                        new BackgroundItem("SPOT", BackgroundItemType.HONOR));

        String objToString = sut.objectToString(items);

        assertTrue(objToString.contains(" ACM "));
        assertTrue(objToString.contains(" Skull and Crossbones "));
        assertTrue(objToString.contains(" Java "));
        assertTrue(objToString.contains(" C# "));
        assertTrue(objToString.contains(" Technology "));
        assertTrue(objToString.contains(" Apple "));
        assertTrue(objToString.contains(" SRA "));
        assertTrue(objToString.contains(" SPOT "));
    }
}
