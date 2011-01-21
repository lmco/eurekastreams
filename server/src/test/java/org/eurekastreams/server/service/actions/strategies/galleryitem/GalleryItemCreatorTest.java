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

import java.io.Serializable;
import java.util.HashMap;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.persistence.PersonMapper;
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
public class GalleryItemCreatorTest
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
     * The mock person mapper to be used by the action.
     */
    private PersonMapper personMapper = context.mock(PersonMapper.class);

    /**
     * The mock person mapper to be used by the action.
     */
    private Person user = context.mock(Person.class);

    /**
     * The mock mapper to be used by the action.
     */
    @SuppressWarnings("unchecked")
    private GalleryItemFactory<Theme> galleryItemFactory = context.mock(GalleryItemFactory.class);

    /**
     * Subject under test.
     */
    private GalleryItemCreator<Theme> sut = null;

    /**
     * User making the request.
     */
    private final String username = "validuser";

    /**
     *
     */
    @Before
    public final void setup()
    {
        sut = new GalleryItemCreator<Theme>(themeMapper, galleryItemFactory, personMapper);

        context.assertIsSatisfied();
    }

    /**
     * Call the provide method and make sure it produces what it should.
     *
     * @throws Exception
     *             can throw an exception on bad UUID.
     */
    @Test
    public final void testProvideWithNonExistentUrl() throws Exception
    {
        final Theme theme = new Theme("src/main/webapp/themes/vegas.xml", "Las Vegas", "Las Vegas sky line", null,
                null, null, "Phil Plait", "phil.plait@awesome.com");

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("url", "src/main/webapp/themes/vegas.xml");
        formData.put("category", "CITY");

        context.checking(new Expectations()
        {
            {
                oneOf(galleryItemFactory).create();
                will(returnValue(theme));

                oneOf(themeMapper).findByUrl("src/main/webapp/themes/vegas.xml");
                will(returnValue(null));

                oneOf(personMapper).findByAccountId(username);
                will(returnValue(user));
            }
        });

        // Make the call
        Theme actual = sut.provide(getActionContext(username), formData);

        context.assertIsSatisfied();

        assertEquals("property should be gotten", "Las Vegas", actual.getName());
        assertEquals("property should be gotten", "Las Vegas sky line", actual.getDescription());
        assertEquals("property should be gotten", "Phil Plait", actual.getAuthorName());
        assertEquals("property should be gotten", "phil.plait@awesome.com", actual.getAuthorEmail());
        assertEquals("property should be gotten", "src/main/webapp/themes/vegas.xml", actual.getUrl());
    }

    /**
     * Call the provide method and make sure it throws an exception because the url is already in use.
     *
     * @throws Exception
     *             can throw an exception on bad UUID.
     */
    @Test(expected = ValidationException.class)
    public final void testProvideWithExistingUrl() throws Exception
    {
        final Theme theme = new Theme("src/main/webapp/themes/vegas.xml", "Las Vegas", "Las Vegas sky line", null,
                null, null, "Phil Plait", "phil.plait@awesome.com");

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("url", "src/main/webapp/themes/vegas.xml");
        formData.put("category", "CITY");

        context.checking(new Expectations()
        {
            {
                oneOf(galleryItemFactory).create();
                will(returnValue(theme));

                oneOf(themeMapper).findByUrl("src/main/webapp/themes/vegas.xml");
                will(returnValue(theme));
            }
        });

        // Make the call
        sut.provide(getActionContext(username), formData);
    }

    /**
     * Get a ServiceActionContext suitable for testing with the input user name.
     *
     * @param userName
     *            the username to put in the Principal
     * @return a service action context suitable for testing, with a principal containing the input user name
     */
    private PrincipalActionContext getActionContext(final String userName)
    {
        return new ServiceActionContext(null, new Principal()
        {
            private static final long serialVersionUID = -5821486041207651728L;

            @Override
            public String getAccountId()
            {
                return userName;
            }

            @Override
            public Long getId()
            {
                return null;
            }

            @Override
            public String getOpenSocialId()
            {
                return null;
            }
            
            @Override
            public String getSessionId()
            {
                return "";
            }
        });
    }
}
