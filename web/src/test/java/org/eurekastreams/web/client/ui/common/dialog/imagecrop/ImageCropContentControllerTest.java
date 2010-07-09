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
package org.eurekastreams.web.client.ui.common.dialog.imagecrop;

import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.web.client.AnonymousClassInterceptor;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Hyperlink;

/**
 * Test class for ImageCropContentController.
 */
public class ImageCropContentControllerTest
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
    private final ImageCropContentModel modelMock = context.mock(ImageCropContentModel.class);

    /**
     * Gagdet zone mock.
     */
    private final Hyperlink saveButton = context.mock(Hyperlink.class);

    /**
     * A mocked close button.
     */
    private final Hyperlink closeButton = context.mock(Hyperlink.class, "close");

    /**
     * sut.
     */
    private ImageCropContentController sut = new ImageCropContentController(modelMock);

    /**
     * Mock clicklistener.
     */
    final AnonymousClassInterceptor<ClickListener> newClickInt = new AnonymousClassInterceptor<ClickListener>();

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
    public void addCloseClickListener()
    {
        context.checking(new Expectations()
        {
            {
                one(closeButton).addClickListener(with(any(ClickListener.class)));
                will(newClickInt);

                one(modelMock).setIsShown(false);
            }
        });

        sut.addCloseClickListener(closeButton);
        newClickInt.getObject().onClick(null);
        context.assertIsSatisfied();
    }

    /**
     * Add close click listener to gadget test.
     */
    @Test
    public void addSaveClickListener()
    {
        final ImageCropContentView view = context.mock(ImageCropContentView.class);
        final List<Integer> coords = new LinkedList<Integer>();

        context.checking(new Expectations()
        {
            {
                one(saveButton).addClickListener(with(any(ClickListener.class)));
                will(newClickInt);

                oneOf(view).getCoords();
                will(returnValue(coords));
                one(modelMock).setCoords(coords);
            }
        });

        sut.addSaveClickListener(saveButton, view);
        newClickInt.getObject().onClick(null);
        context.assertIsSatisfied();
    }

}
