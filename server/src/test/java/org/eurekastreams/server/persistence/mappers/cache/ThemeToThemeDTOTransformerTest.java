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
package org.eurekastreams.server.persistence.mappers.cache;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;

import org.eurekastreams.server.domain.Theme;
import org.eurekastreams.server.domain.dto.ThemeDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for ThemeToThemeDTOTransformer.
 * 
 */
public class ThemeToThemeDTOTransformerTest
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
    private ThemeToThemeDTOTransformer sut = new ThemeToThemeDTOTransformer();

    /**
     * Theme to transform.
     */
    private Theme theme = context.mock(Theme.class);

    /**
     * Theme name.
     */
    private String themeName = "themeName";

    /**
     * Theme id.
     */
    private Long themeId = 5L;

    /**
     * Test.
     */
    @Test
    public void test()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(theme).getId();
                will(returnValue(themeId));

                oneOf(theme).getName();
                will(returnValue(themeName));

            }
        });

        ThemeDTO result = sut.transform(new ArrayList<Theme>(Arrays.asList(theme))).get(0);

        assertEquals(themeId, result.getId());
        assertEquals(themeName, result.getName());

        context.assertIsSatisfied();
    }
}
