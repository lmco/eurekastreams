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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.domain.Enrollment;
import org.eurekastreams.server.domain.Person;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for EducationListStringBridge.
 */
public class EducationListStringBridgeTest
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
    private EducationListStringBridge sut = new EducationListStringBridge();

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
        Enrollment school1 =
                new Enrollment(person, "Harvard", "Bachelors", Arrays.asList(new BackgroundItem("Computer Science",
                        BackgroundItemType.AREA_OF_STUDY)), new Date(), Arrays.asList(new BackgroundItem("ACM",
                        BackgroundItemType.ACTIVITY_OR_SOCIETY)), "description1");

        Enrollment school2 =
                new Enrollment(person, "Yale", "Masters", Arrays.asList(new BackgroundItem("Software Engineering",
                        BackgroundItemType.AREA_OF_STUDY)), new Date(), Arrays.asList(new BackgroundItem(
                        "Skull and Crossbones", BackgroundItemType.ACTIVITY_OR_SOCIETY)), "description2");

        List<Enrollment> schools = new ArrayList<Enrollment>();
        schools.add(school1);
        schools.add(school2);

        String objToString = sut.objectToString(schools);

        assertTrue(objToString.contains(" Harvard "));
        assertTrue(objToString.contains(" Yale "));
        assertTrue(objToString.contains(" Bachelors "));
        assertTrue(objToString.contains(" Masters "));
        assertTrue(objToString.contains(" Computer Science "));
        assertTrue(objToString.contains(" Software Engineering "));
        assertTrue(objToString.contains(" ACM "));
        assertTrue(objToString.contains(" Skull and Crossbones "));
        assertTrue(objToString.contains(" description1 "));
        assertTrue(objToString.contains(" description2 "));

    }
}
