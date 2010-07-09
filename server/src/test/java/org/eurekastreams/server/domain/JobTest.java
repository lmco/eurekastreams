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

import org.junit.Before;
import org.junit.Test;

/**
 * Job test class.
 *
 */
public class JobTest
{

    /**
     * test person.
     */
    private final Person testPerson = new Person("mortimer", "richard", "snerd", "mort", "mort");

    /**
     * test company name.
     */
    private final String testCompanyName = "Example Company";

    /**
     * test industry.
     */
    private final String testIndustry = "Aerospace";

    /**
     * test industry.
     */
    private final String testTitle = "Engineer";

    /**
     * test date from.
     */
    private final Date testDateFrom = null;

    /**
     * test date to.
     */
    private final Date testDateTo = null;

    /**
     * test description.
     */
    private final String testDescription = "Responsibilities included this, that and the other thing";

    /**
     * Subject under test.
     */
    private Job sut;

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new Job(testPerson, testCompanyName, testIndustry, testTitle, testDateFrom, testDateTo, testDescription);
    }

    /**
     * Test company name setter/getter.
     */
    @Test
    public void setAndGetCompanyName()
    {
        sut.setCompanyName(testCompanyName);
        assertEquals("property should be gotten", testCompanyName, sut.getCompanyName());
    }

    /**
     * Test industry setter/getter.
     */
    @Test
    public void setAndGetIndustry()
    {
        sut.setIndustry(testIndustry);
        assertEquals("property should be gotten", testIndustry, sut.getIndustry());
    }

    /**
     * Test title setter/getter.
     */
    @Test
    public void setAndGetTitle()
    {
        sut.setTitle(testTitle);
        assertEquals("property should be gotten", testTitle, sut.getTitle());
    }

    /**
     * Test date from setter/getter.
     */
    @Test
    public void setAndGetDateFrom()
    {
        sut.setDateFrom(testDateFrom);
        assertEquals("property should be gotten", testDateFrom, sut.getDateFrom());
    }

    /**
     * Test date to getter.
     */
    @Test
    public void setAndGetDateTo()
    {
        sut.setDateTo(testDateTo);
        assertEquals("property should be gotten", testDateTo, sut.getDateTo());
    }

    /**
     * Test description to setter/getter.
     */
    @Test
    public void setAndGetDescription()
    {
        sut.setDescription(testDescription);
        assertEquals("property should be gotten", testDescription, sut.getDescription());
    }

    /**
     * Test owner getter.
     */
    @Test
    public void getOwner()
    {
        assertEquals("property should be gotten", testPerson.getDisplayName(), sut.getOwner().getDisplayName());
    }

}
