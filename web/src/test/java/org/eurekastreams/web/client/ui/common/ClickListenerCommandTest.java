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
package org.eurekastreams.web.client.ui.common;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.ClickListener;

/**
 * Test for the ClickListenerCommand class.
 * These tests don't work because of GWT silliness. I think it's because ClickListenerC 
 */
public class ClickListenerCommandTest
{
    /**
     * Subject under test. 
     */
    private ClickListenerCommand sut;
    
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
     * Create the SUT. 
     */
    @Before
    public void setup()
    {
        GWTMockUtilities.disarm();
        sut = new ClickListenerCommand(null);
    }

    /**
     * Post-test tear down.
     */
    @After
    public final void tearDown()
    {
        GWTMockUtilities.restore();
    }
    
    /**
     * Verify that added listeners get called. 
     */
    @Test
    public void addListener()
    {
        final ClickListener listener1 = context.mock(ClickListener.class, "listener1");
        final ClickListener listener2 = context.mock(ClickListener.class, "listener2");
        
        context.checking(new Expectations()
        {
            {
                oneOf(listener1).onClick(null);
                oneOf(listener2).onClick(null);
            }
        });
        
        sut.addClickListener(listener1);
        sut.addClickListener(listener2);
        
        sut.execute();
        
        context.assertIsSatisfied();
    }
    
    /**
     * Verify that removed listeners do not get called but others do. 
     */
    @Test
    public void removeListener()
    {
        final ClickListener listener1 = context.mock(ClickListener.class, "listener1");
        final ClickListener listener2 = context.mock(ClickListener.class, "listener2");
        
        context.checking(new Expectations()
        {
            {
                oneOf(listener2).onClick(null);
            }
        });
        
        sut.addClickListener(listener1);
        sut.addClickListener(listener2);

        sut.removeClickListener(listener1);
        
        sut.execute();
        
        context.assertIsSatisfied();
    }
}
