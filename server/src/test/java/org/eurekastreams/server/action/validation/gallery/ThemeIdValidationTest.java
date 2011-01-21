/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.validation.gallery;

import static org.junit.Assert.assertSame;

import java.io.Serializable;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.persistence.ThemeMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for ThemeIdValidation.
 */
public class ThemeIdValidationTest
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
     * Test theme.
     */
    private final Theme testTheme = new Theme("http://foo.foo.com/something.xml", "My Theme", "My Theme Description",
            "http://localhost:8080/themes/myTheme.css", "FAKE-UUID-0000-0000-0000", "My Theme Banner Id", "authorName",
            "authorEmail");

    /**
     * The mock mapper to be used by the action.
     */
    private ThemeMapper themeMapper = context.mock(ThemeMapper.class);

    /**
     * System under test.
     */
    private ThemeIdValidation sut = new ThemeIdValidation(themeMapper);

    /**
     * Make sure that valid uuid argument get approved.
     */
    @Test
    public final void testValidateParamsWithGoodUuidParams()
    {
        final String uuid = "FAKE-UUID-0000-0000-0000";
        final ServiceActionContext serviceContext = buildServerActionContext("{" + uuid + "}");
        context.checking(new Expectations()
        {
            {
                oneOf(themeMapper).findByUUID(uuid);
                will(returnValue(testTheme));
            }
        });

        sut.validate(serviceContext);

        assertSame(testTheme, serviceContext.getState().get("THEME"));
    }

    /**
     * Make sure that valid url argument get approved.
     */
    @Test
    public final void testValidateParamsWithGoodUrlParams()
    {
        final ServiceActionContext serviceContext = buildServerActionContext(testTheme.getUrl());
        context.checking(new Expectations()
        {
            {
                oneOf(themeMapper).findByUrl(testTheme.getUrl());
                will(returnValue(testTheme));
            }
        });

        sut.validate(serviceContext);

        assertSame(testTheme, serviceContext.getState().get("THEME"));
    }

    /**
     * Make sure that sending bad arguments results in the expected exception.
     * 
     */
    @Test(expected = Exception.class)
    public final void testValidateParamsWithBadParams()
    {
        sut.validate(buildServerActionContext(1L));
    }

    /**
     * Build a server action context for testing.
     * 
     * @param themeId
     *            the theme id to set as the parameter
     * @return a server action context for testing
     */
    private ServiceActionContext buildServerActionContext(final Serializable themeId)
    {
        return new ServiceActionContext(themeId, new Principal()
        {
            private static final long serialVersionUID = -6362239925984016955L;

            @Override
            public String getAccountId()
            {
                return null;
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
