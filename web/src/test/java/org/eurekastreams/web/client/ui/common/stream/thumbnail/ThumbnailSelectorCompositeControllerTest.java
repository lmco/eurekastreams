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
package org.eurekastreams.web.client.ui.common.stream.thumbnail;

import org.eurekastreams.web.client.AnonymousClassInterceptor;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.ClickListener;

/**
 * Thumb selector controller test.
 */
public class ThumbnailSelectorCompositeControllerTest
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
    private ThumbnailSelectorCompositeController sut = null;

    /**
     * The view.
     */
    private ThumbnailSelectorCompositeView view = null;

    /**
     * The model.
     */
    private ThumbnailSelectorCompositeModel model = null;

    /**
     * Next click intercepter.
     */
    private AnonymousClassInterceptor<ClickListener> nextClickInt = new AnonymousClassInterceptor<ClickListener>();

    /**
     * Previous click intercepter.
     */
    private AnonymousClassInterceptor<ClickListener> prevClickInt = new AnonymousClassInterceptor<ClickListener>();

    /**
     * Remove thumbnail intercepter.
     */
    private AnonymousClassInterceptor<ClickListener> removeClickInt = new AnonymousClassInterceptor<ClickListener>();

    /**
     * Setup test fixtures.
     */
    @Before
    public final void setUp()
    {
        GWTMockUtilities.disarm();
        model = context.mock(ThumbnailSelectorCompositeModel.class);
        view = context.mock(ThumbnailSelectorCompositeView.class);
        sut = new ThumbnailSelectorCompositeController(view, model);

        context.checking(new Expectations()
        {
            {
                oneOf(view).addNextClickListener(with(any(ClickListener.class)));
                will(nextClickInt);

                oneOf(view).addPrevClickListener(with(any(ClickListener.class)));
                will(prevClickInt);

                oneOf(view).addRemoveThumbClickListener(with(any(ClickListener.class)));
                will(removeClickInt);
            }
        });

        sut.init();
    }

    /**
     * Test init.
     */
    @Test
    public final void initTest()
    {
        context.assertIsSatisfied();
    }

    /**
     * Test the previous click listener.
     */
    @Test
    public final void prevCanPageTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(model).hasPrevious();
                will(returnValue(true));

                oneOf(model).selectPrevious();
                
                oneOf(view).updateImage();
            }
        });

        prevClickInt.getObject().onClick(null);

        context.assertIsSatisfied();
    }

    /**
     * Test the previous click listener.
     */
    @Test
    public final void prevCantPageTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(model).hasPrevious();
                will(returnValue(false));
            }
        });

        prevClickInt.getObject().onClick(null);

        context.assertIsSatisfied();
    }

    /**
     * Test the previous click listener.
     */
    @Test
    public final void nextCanPageTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(model).hasNext();
                will(returnValue(true));

                oneOf(model).selectNext();

                oneOf(view).updateImage();
            }
        });

        nextClickInt.getObject().onClick(null);

        context.assertIsSatisfied();
    }

    /**
     * Test the next click listener when a next page isn't available.
     */
    @Test
    public final void nextCantPageTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(model).hasNext();
                will(returnValue(false));
            }
        });

        nextClickInt.getObject().onClick(null);

        context.assertIsSatisfied();
    }

    /**
     * Test the remove thumbnail click listener.
     */
    @Test
    public final void removeTest()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(view).showHideThumbnail();
            }
        });

        removeClickInt.getObject().onClick(null);
    }

}
