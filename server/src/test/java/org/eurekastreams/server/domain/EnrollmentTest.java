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

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Enrollment test class.
 *
 */
public class EnrollmentTest
{

    /**
     * test person.
     */
    private final Person testPerson = new Person("mortimer", "richard", "snerd", "mort", "mort");

    /**
     * test company name.
     */
    private final String testSchoolName = "West Chester State University";

    /**
     * test degree.
     */
    private final String testDegree = "BS Computer Sciences";

    /**
     * test date from.
     */
    private final Date testGradDate = null;

    /**
     * test additional details.
     */
    private final String testAdditionalDetails = "Responsibilities included this, that and the other thing";

    /**
     * Subject under test.
     */
    private Enrollment sut;

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut =
                new Enrollment(testPerson, testSchoolName, testDegree, new ArrayList<BackgroundItem>(), testGradDate,
                        new ArrayList<BackgroundItem>(), testAdditionalDetails);
    }

    /**
     * Test company name setter/getter.
     */
    @Test
    public void setAndGetSchoolName()
    {
        sut.setSchoolName(testSchoolName);
        assertEquals("property should be gotten", testSchoolName, sut.getSchoolName());
    }

    /**
     * Test degree setter/getter.
     */
    @Test
    public void setAndGetDegree()
    {
        sut.setDegree(testDegree);
        assertEquals("property should be gotten", testDegree, sut.getDegree());
    }

    /**
     * Test grad date setter/getter.
     */
    @Test
    public void setAndGetGradDate()
    {
        sut.setGradDate(testGradDate);
        assertEquals("property should be gotten", testGradDate, sut.getGradDate());
    }

    /**
     * Test AreasOfStudy Set and Get.
     */
    @Test
    public void testAreasOfStudySetAndGet()
    {
        String message = "should get and set area of study list appropriately";

        List<BackgroundItem> expectedAreasOfStudy = new ArrayList<BackgroundItem>();
        expectedAreasOfStudy.add(new BackgroundItem("electrical engineering", BackgroundItemType.AREA_OF_STUDY));
        expectedAreasOfStudy.add(new BackgroundItem("computer scientce", BackgroundItemType.AREA_OF_STUDY));
        expectedAreasOfStudy.add(new BackgroundItem("music", BackgroundItemType.AREA_OF_STUDY));

        sut.setEnrollmentItems(expectedAreasOfStudy, BackgroundItemType.AREA_OF_STUDY);

        List<BackgroundItem> actualAreasOfStudy = sut.getEnrollmentItems(BackgroundItemType.AREA_OF_STUDY);

        assertEquals(message, expectedAreasOfStudy, actualAreasOfStudy);

    }

    /**
     * Test setting the enrollment items with an invalid type throws RuntimeException.
     */
    @Test(expected = RuntimeException.class)
    public void testSetBackgroundItemsWithInvalidType()
    {
        sut.setEnrollmentItems(new ArrayList<BackgroundItem>(), BackgroundItemType.NOT_SET);
    }

    /**
     * Test getting the enrollment items with an invalid type throws RuntimeException.
     */
    @Test(expected = RuntimeException.class)
    public void testGetBackgroundItemsWithInvalidType()
    {
        sut.getEnrollmentItems(BackgroundItemType.NOT_SET);
    }

    /**
     * Test Activities Set and Get.
     */
    @Test
    public void testActivitiesSetAndGet()
    {
        String message = "should get and set activity list appropriately";

        List<BackgroundItem> expectedActivities = new ArrayList<BackgroundItem>();
        expectedActivities.add(new BackgroundItem("marching band", BackgroundItemType.ACTIVITY_OR_SOCIETY));
        expectedActivities.add(new BackgroundItem("computer geek club", BackgroundItemType.ACTIVITY_OR_SOCIETY));
        expectedActivities.add(new BackgroundItem("ieee", BackgroundItemType.ACTIVITY_OR_SOCIETY));

        sut.setEnrollmentItems(expectedActivities, BackgroundItemType.ACTIVITY_OR_SOCIETY);

        List<BackgroundItem> actualActivities = sut.getEnrollmentItems(BackgroundItemType.ACTIVITY_OR_SOCIETY);

        assertEquals(message, expectedActivities, actualActivities);

    }

    /**
     * Test additional details to setter/getter.
     */
    @Test
    public void setAndGetAdditionalDetails()
    {
        sut.setAdditionalDetails(testAdditionalDetails);
        assertEquals("property should be gotten", testAdditionalDetails, sut.getAdditionalDetails());
    }

}
