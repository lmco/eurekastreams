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
import static org.junit.Assert.assertSame;

import java.io.Serializable;
import java.util.HashMap;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.persistence.ThemeMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the SetThemeAction.
 */
public class GalleryItemFinderTest
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
     * The mock theme mapper to be used by the action.
     */
    private ThemeMapper themeMapper = context.mock(ThemeMapper.class);

    /**
     * Subject under test.
     */
    private GalleryItemFinder<Theme> sut = null;

    /**
     * The mock user information from the session.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     *
     */
    @Before
    public final void setup()
    {
        sut = new GalleryItemFinder<Theme>(themeMapper);
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
        final String name = "Foo Bar";
        final String email = "foo.bar@blah.com";
        final String themeName = "Las Vegas";
        final String themeDesc = "Las Vegas sky line";
        final String themeUrl = "src/main/webapp/themes/vegas.xml";

        final Theme theme = new Theme(themeUrl, themeName, themeDesc, null, null, null, name, email);

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("id", "1");

        context.checking(new Expectations()
        {
            {
                oneOf(themeMapper).findById(1L);
                will(returnValue(theme));
            }
        });

        // Make the call
        Theme actual = sut.provide(actionContext, formData);

        context.assertIsSatisfied();

        assertSame(theme, actual);
        assertEquals("property should be gotten", themeName, theme.getName());
        assertEquals("property should be gotten", themeDesc, theme.getDescription());
        assertEquals("property should be gotten", name, theme.getAuthorName());
        assertEquals("property should be gotten", email, theme.getAuthorEmail());
        assertEquals("property should be gotten", themeUrl, theme.getUrl());
    }
}
