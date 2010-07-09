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

import org.eurekastreams.server.domain.Background;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.domain.Person;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for BackgroundStringBridge.
 */
public class BackgroundStringBridgeTest
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
    private BackgroundStringBridge sut = new BackgroundStringBridge();

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
        Person person = context.mock(Person.class);
        Background bg = new Background(person);

        bg.setBackgroundItems(Arrays.asList(new BackgroundItem("ACM", BackgroundItemType.SKILL), new BackgroundItem(
                "Skull and Crossbones", BackgroundItemType.SKILL),
                new BackgroundItem("Java", BackgroundItemType.SKILL),
                new BackgroundItem("C#", BackgroundItemType.SKILL), new BackgroundItem("Technology",
                        BackgroundItemType.SKILL), new BackgroundItem("Apple", BackgroundItemType.SKILL),
                new BackgroundItem("SRA", BackgroundItemType.SKILL), new BackgroundItem("SPOT",
                        BackgroundItemType.SKILL)), BackgroundItemType.SKILL);

        String objToString = sut.objectToString(bg);

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
