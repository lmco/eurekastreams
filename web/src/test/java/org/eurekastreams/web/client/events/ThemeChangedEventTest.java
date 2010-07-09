/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.events;

import junit.framework.Assert;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.eurekastreams.server.domain.Theme;

/**
 * Tests the theme changed event.
 *
 */
public class ThemeChangedEventTest
{
    /**
     * JMock context for making mocks. 
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };
    
    /**
     * The expected theme.
     */
    private Theme expectedTheme = context.mock(Theme.class);
    
    /**
     * The System Under Test being fed in the expected theme.
     */
    private ThemeChangedEvent sut = new ThemeChangedEvent(expectedTheme);
    
    /**
     * Second silliest test ever.
     */
    @Test
    public void getEvent()
    {
        Assert.assertEquals(sut.getClass(), ThemeChangedEvent.getEvent().getClass());
    }
    
    /**
     * Silliest test ever.
     */
    @Test
    public void getTheme()
    {
        Assert.assertEquals(expectedTheme, sut.getTheme());
    }
}
