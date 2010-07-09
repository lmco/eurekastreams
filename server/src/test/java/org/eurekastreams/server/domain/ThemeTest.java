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
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test class for Tab.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext*-test.xml" })
public class ThemeTest
{

    /**
     * UUID used by SUT.
     */
    String uuid = UUID.randomUUID().toString();

    /**
     * test industry.
     */
    private final String testName = "My Theme";

    /**
     * test date from.
     */
    private final Date created = null;

    /**
     * test description.
     */
    private final String testDescription = "some desc";

    /**
     * test author name.
     */
    private final String testAuthorName = "some author name";

    /**
     * test author name.
     */
    private final String testAuthorEmail = "some author email";

    /**
     * test person.
     */
    private final Person person = new Person("homers", "homer", "jay", "simpson", "MaxPower");

    /**
     * Subject under test.
     */
    Theme theme;

    /**
     * Test class setup.
     */
    @Before
    public final void setUp()
    {
        theme =
                new Theme("http://www.example.org/theme.xml", testName, testDescription, "generated.css", uuid,
                        "bannerId", testAuthorName, testAuthorEmail);
        theme.setOwner(person);
    }

    /**
     * Constructor test for theme name parameter.
     */
    @Test
    public final void testConstructorThemeName()
    {
        assertEquals("theme name does not match value passed into constructor", "My Theme", theme.getName());
    }

    /**
     * Constructor test for theme URL parameter.
     */
    @Test
    public final void testConstructorThemeUrl()
    {
        assertEquals("theme url does not match value passed into constructor", "http://www.example.org/theme.xml",
                theme.getUrl());
    }

    /**
     * Constructor test for theme URL parameter.
     */
    @Test
    public final void testConstructorThemeCssFile()
    {
        assertEquals("theme css does not match value passed into constructor", "generated.css", theme.getCssFile());
    }

    /**
     * Constructor test for theme UUID parameter.
     */
    @Test
    public final void testConstructorThemeUUID()
    {
        assertEquals("theme css does not match value passed into constructor", uuid, theme.getUUID());
    }

    /**
     * Test description to setter/getter.
     */
    @Test
    public void setAndGetDescription()
    {
        theme.setDescription(testDescription);
        assertEquals("property should be gotten", testDescription, theme.getDescription());
    }

    /**
     * Test title setter/getter.
     */
    @Test
    public void setAndGetTitle()
    {
        assertEquals("property should be gotten", testName, theme.getName());
    }

    /**
     * Test owner setter/getter.
     */
    @Test
    public void setAndGetOwner()
    {
        assertEquals("property should be gotten", "homer", theme.getOwner().getFirstName());
    }

    /**
     * Test author name setter/getter.
     */
    @Test
    public void setAndGetAuthorName()
    {
        assertEquals("property should be gotten", testAuthorName, theme.getAuthorName());
    }

    /**
     * Test author name setter/getter.
     */
    @Test
    public void setAndGetAuthorEmail()
    {
        assertEquals("property should be gotten", testAuthorEmail, theme.getAuthorEmail());
    }

}
