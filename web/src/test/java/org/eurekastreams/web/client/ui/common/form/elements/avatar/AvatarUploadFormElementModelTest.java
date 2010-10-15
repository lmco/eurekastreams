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
package org.eurekastreams.web.client.ui.common.form.elements.avatar;

import org.eurekastreams.web.client.ui.common.form.elements.avatar.strategies.ImageUploadStrategy;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.junit.GWTMockUtilities;

/**
 * Test.
 */
public class AvatarUploadFormElementModelTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
            GWTMockUtilities.disarm();
        }
    };



    /**
     * Execution sequence.
     */
    final Sequence executeSequence = context.sequence("executeSequence");

    /**
     * The view mock.
     */
    final AvatarUploadFormElementView viewMock = context.mock(AvatarUploadFormElementView.class);
    
    /**
     * Image upload strategy mock.
     */
    final ImageUploadStrategy strategy = context.mock(ImageUploadStrategy.class);


    /**
     * sut.
     */
    private AvatarUploadFormElementModel sut = new AvatarUploadFormElementModel(strategy);

    /**
     * Setup test fixture.
     */
    @Before
    public final void setUp()
    {
        GWTMockUtilities.disarm();
        sut.registerView(viewMock);

    }
    

    /**
     * Test.
     */
    @Test
    public void setResizePanelShown()
    {
        
        context.checking(new Expectations()
        {
            {
                oneOf(viewMock).onResizePanelShownChanged(true);
            }
        });
        
        sut.setResizePanelShown(true);
        context.assertIsSatisfied();
    }
    
    /**
     * Test.
     */
    @Test
    public void setFormResult()
    {
        
        context.checking(new Expectations()
        {
            {
                oneOf(viewMock).onFormResultChanged("hahahaha");
            }
        });
        
        sut.setFormResult("hahahaha");
        context.assertIsSatisfied();
    }
}
