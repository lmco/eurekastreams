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

import org.eurekastreams.web.client.AnonymousClassInterceptor;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacade;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;

/**
 * Test class for AvatarUploadFormElementController.
 */
public class AvatarUploadFormElementControllerTest
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
     * Model mock.
     */
    private final AvatarUploadFormElementModel modelMock = context.mock(AvatarUploadFormElementModel.class);

    /**
     * Mocked jSNIFacade. 
     */
    private final WidgetJSNIFacade jSNIFacade = context.mock(WidgetJSNIFacade.class);

    /**
     * SUT.
     */
    private AvatarUploadFormElementController sut = new AvatarUploadFormElementController(modelMock, jSNIFacade);

    /**
     * Mock ClickListener.
     */
    final AnonymousClassInterceptor<ClickListener> newClickInt = new AnonymousClassInterceptor<ClickListener>();

    /**
     * For catching the listeners. 
     */
    final AnonymousClassInterceptor<FormHandler> newFormInt = new AnonymousClassInterceptor<FormHandler>();

    /**
     * /** Setup test fixture.
     */
    @Before
    public final void setUp()
    {
        GWTMockUtilities.disarm();
    }



    /**
     * Add close click listener to gadget test.
     */
    @Test
    public void addResizeClickListener()
    {
        final Anchor resizeButton = context.mock(Anchor.class);

        context.checking(new Expectations()
        {
            {
                oneOf(resizeButton).addClickListener(with(any(ClickListener.class)));
                will(newClickInt);

                oneOf(modelMock).setResizePanelShown(true);
            }
        });

        sut.addResizeClickListener(resizeButton);
        newClickInt.getObject().onClick(null);
        context.assertIsSatisfied();
    }

    /**
     * Add close click listener to gadget test.
     */
    @Test
    public void addResizeClickListenerOnSubmitComplete()
    {
        final FormPanel uploadForm = context.mock(FormPanel.class);
        final FormSubmitCompleteEvent results = context.mock(FormSubmitCompleteEvent.class);

        context.checking(new Expectations()
        {
            {
                oneOf(uploadForm).addFormHandler(with(any(FormHandler.class)));
                will(newFormInt);

                oneOf(results).getResults();
                will(returnValue("<pre>something</pre>"));

                oneOf(modelMock).setFormResult("something");
            }
        });

        sut.addFormHandler(uploadForm);
        newFormInt.getObject().onSubmitComplete(results);
        context.assertIsSatisfied();
    }

    /**
     * Test clicking the submit button. 
     */
    @Test
    public void addResizeClickListenerOnSubmit()
    {
        final FormPanel uploadForm = context.mock(FormPanel.class);

        context.checking(new Expectations()
        {
            {
                oneOf(uploadForm).addFormHandler(with(any(FormHandler.class)));
                will(newFormInt);
            }
        });

        sut.addFormHandler(uploadForm);
        newFormInt.getObject().onSubmit(null);
        context.assertIsSatisfied();
    }

}
