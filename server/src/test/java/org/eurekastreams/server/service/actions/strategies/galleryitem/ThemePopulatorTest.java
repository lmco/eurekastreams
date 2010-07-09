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
package org.eurekastreams.server.service.actions.strategies.galleryitem;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.service.actions.strategies.DocumentCreator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Tests the SetThemeAction.
 */
public class ThemePopulatorTest
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
     * Subject under test.
     */
    private ThemePopulator sut = null;

    /**
     * The decorator injected into the action.
     */
    private DocumentCreator documentCreator = context.mock(DocumentCreator.class);

    /**
     *
     */
    @Before
    public final void setup()
    {
        sut = new ThemePopulator(documentCreator);
    }

    /**
     * Call the execute method and make sure it produces what it should.
     *
     * @throws Exception
     *             can throw an exception on bad UUID.
     */
    @Test
    public final void testExecuteWithUrl() throws Exception
    {
        final String themeName = "Las Vegas";
        final String themeDesc = "Las Vegas sky line";
        final String personName = "Phil Plait";
        final String personEmail = "phil.plait@awesome.com";
        final String themeBannerId = "http://i26.photobucket.com/albums/c104/tridirk/12.jpg";

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        final Document xmlDoc = db.parse("src/test/resources/themes/vegas.xml");

        Theme theme = new Theme();

        context.checking(new Expectations()
        {
            {
                oneOf(documentCreator).execute("src/test/resources/themes/vegas.xml");
                will(returnValue(xmlDoc));
            }
        });

        // Make the call
        sut.populate(theme, "src/test/resources/themes/vegas.xml");

        context.assertIsSatisfied();

        assertEquals("property should be gotten", themeName, theme.getName());
        assertEquals("property should be gotten", themeDesc, theme.getDescription());
        assertEquals("property should be gotten", personName, theme.getAuthorName());
        assertEquals("property should be gotten", personEmail, theme.getAuthorEmail());
        assertEquals("property should be gotten", themeBannerId, theme.getBannerId());
    }

    /**
     * Call the execute method and make sure it produces what it should, working with a theme with an attempted exploit
     * name.
     *
     * @throws Exception
     *             can throw an exception on bad UUID.
     */
    @Test
    public final void testExecuteWithUrlWithDangerousThemeTitle() throws Exception
    {
        final String themeName = "../../../Las Vegas Super Cool Theme - it's cool, isn't it?";
        final String themeNameCleaned = "..-..-..-Las Vegas S";
        final String themeDesc = "Las Vegas sky line";
        final String personName = "Phil Plait";
        final String personEmail = "phil.plait@awesome.com";
        final String themeBannerId = "http://i26.photobucket.com/albums/c104/tridirk/12.jpg";

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        final Document xmlDoc = db.parse("src/test/resources/themes/vegas-dangerous-title.xml");

        Theme theme = new Theme();

        context.checking(new Expectations()
        {
            {
                oneOf(documentCreator).execute("src/test/resources/themes/vegas.xml");
                will(returnValue(xmlDoc));
            }
        });

        // Make the call
        sut.populate(theme, "src/test/resources/themes/vegas.xml");
        context.assertIsSatisfied();
        assertEquals("property should be gotten", themeName, theme.getName());
        assertEquals("property should be gotten", themeDesc, theme.getDescription());
        assertEquals("property should be gotten", personName, theme.getAuthorName());
        assertEquals("property should be gotten", personEmail, theme.getAuthorEmail());
        assertEquals("property should be gotten", themeBannerId, theme.getBannerId());
        assertTrue("path separator wasn't escaped out of theme name when creating the theme path - was: "
                + theme.getCssFile(), theme.getCssFile().startsWith("/themes/" + themeNameCleaned));

        final int sixtySevenPlusOne = 68;
        assertEquals("Length of the theme css path should be 68 characters: /themes/<truncated to "
                + "20 chars><36 char uuid>.css", sixtySevenPlusOne, theme.getCssFile().length());
    }
}
