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
package org.eurekastreams.web.client.ui.common.stream.thumbnail;

import org.eurekastreams.server.domain.stream.LinkInformation;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Thumbnail selector view test.
 */
public class ThumbnailSelectorCompositeViewTest
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
    private ThumbnailSelectorCompositeView sut;

    /**
     * The model.
     */
    private ThumbnailSelectorCompositeModel model = null;

    /**
     * Setup test fixtures.
     */
    @Before
    public final void setUp()
    {
        GWTMockUtilities.disarm();
        model = context.mock(ThumbnailSelectorCompositeModel.class);
        sut = new ThumbnailSelectorCompositeView(model);

        sut.caption = context.mock(Label.class, "caption");
        sut.nextThumb = context.mock(Label.class, "nextThumb");
        sut.prevThumb = context.mock(Label.class, "prevThumb");
        sut.removeThumbnail = context.mock(CheckBox.class, "removeThumbnail");
        sut.selectedThumbnail = context.mock(Image.class, "selectedThumbnail");
        sut.pagingContainer = context.mock(FlowPanel.class, "pagingContainer");
    }

    /**
     * Show thumbnail test.
     */
    @Test
    public final void showThumbnailTest()
    {
        context.checking(new Expectations()
        {
            {
                allowing(sut.removeThumbnail).isChecked();
                will(returnValue(false));

                allowing(sut.caption).setVisible(true);
                allowing(sut.prevThumb).setVisible(true);
                allowing(sut.nextThumb).setVisible(true);
                allowing(sut.selectedThumbnail).setVisible(true);

                allowing(model).hasNext();
                will(returnValue(true));

                allowing(model).hasPrevious();
                will(returnValue(true));

                allowing(sut.prevThumb).removeStyleName("previous-arrow-disabled");
                allowing(sut.nextThumb).removeStyleName("next-arrow-disabled");

                allowing(model).getSelectedThumbnailUrl();
                will(returnValue("http://someurl.com/someimage.png"));

                allowing(sut.selectedThumbnail).setUrl("http://someurl.com/someimage.png");

                allowing(model).getLink();

                oneOf(sut.pagingContainer).removeStyleName("no-thumbnail");
            }
        });

        sut.showHideThumbnail();
        context.assertIsSatisfied();
    }

    /**
     * Hide thumbnail test.
     */
    @Test
    public final void hideThumbnailTest()
    {
        context.checking(new Expectations()
        {
            {
                allowing(sut.removeThumbnail).isChecked();
                will(returnValue(true));

                allowing(sut.caption).setVisible(false);
                allowing(sut.prevThumb).setVisible(false);
                allowing(sut.nextThumb).setVisible(false);
                allowing(sut.selectedThumbnail).setVisible(false);

                allowing(model).getLink();

                oneOf(sut.pagingContainer).addStyleName("no-thumbnail");
            }
        });

        sut.showHideThumbnail();
        context.assertIsSatisfied();
    }

    /**
     * Update image without paging.
     */
    @Test
    public final void updateImageNoPageTest()
    {
        context.checking(new Expectations()
        {
            {
                allowing(sut.selectedThumbnail).setVisible(true);
                allowing(model).hasNext();
                will(returnValue(false));

                allowing(model).hasPrevious();
                will(returnValue(false));

                allowing(sut.prevThumb).addStyleName("previous-arrow-disabled");
                allowing(sut.nextThumb).addStyleName("next-arrow-disabled");

                allowing(model).getSelectedThumbnailUrl();
                will(returnValue("http://someurl.com/someimage.png"));

                allowing(sut.selectedThumbnail).setUrl("http://someurl.com/someimage.png");

                allowing(model).getLink();
            }
        });

        sut.updateImage();
        context.assertIsSatisfied();

    }

    /**
     * Update image with paging.
     */
    @Test
    public final void updateImageCanPageTest()
    {
        context.checking(new Expectations()
        {
            {
                allowing(sut.selectedThumbnail).setVisible(true);
                allowing(model).hasNext();
                will(returnValue(true));

                allowing(model).hasPrevious();
                will(returnValue(true));

                allowing(sut.prevThumb).removeStyleName("previous-arrow-disabled");
                allowing(sut.nextThumb).removeStyleName("next-arrow-disabled");

                allowing(model).getSelectedThumbnailUrl();
                will(returnValue("http://someurl.com/someimage.png"));

                allowing(sut.selectedThumbnail).setUrl("http://someurl.com/someimage.png");

                allowing(model).getLink();
            }
        });

        sut.updateImage();
        context.assertIsSatisfied();
    }

    /**
     * Test setting the link.
     */
    @Test
    public final void setLinkTest()
    {
        final LinkInformation link = new LinkInformation();

        context.checking(new Expectations()
        {
            {
                oneOf(model).setLink(link);

                oneOf(sut.removeThumbnail).setChecked(false);
                allowing(sut.removeThumbnail).isChecked();
                will(returnValue(false));

                allowing(sut.caption).setVisible(true);
                allowing(sut.prevThumb).setVisible(true);
                allowing(sut.nextThumb).setVisible(true);
                allowing(sut.selectedThumbnail).setVisible(true);

                allowing(model).hasNext();
                will(returnValue(true));

                allowing(model).hasPrevious();
                will(returnValue(true));

                allowing(sut.prevThumb).removeStyleName("previous-arrow-disabled");
                allowing(sut.nextThumb).removeStyleName("next-arrow-disabled");

                allowing(model).getSelectedThumbnailUrl();
                will(returnValue("http://someurl.com/someimage.png"));

                allowing(sut.selectedThumbnail).setUrl("http://someurl.com/someimage.png");

                allowing(model).getLink();

                oneOf(sut.pagingContainer).removeStyleName("no-thumbnail");
            }
        });

        sut.setLink(link);
    }

    /**
     * Add next click listener test.
     */
    @Test
    public final void addNextClickListnerTest()
    {
        final ClickListener listener = context.mock(ClickListener.class);

        context.checking(new Expectations()
        {
            {
                oneOf(sut.nextThumb).addClickListener(listener);
            }
        });

        sut.addNextClickListener(listener);

        context.assertIsSatisfied();
    }

    /**
     * Add previous click listener test.
     */
    @Test
    public final void addPrevClickListnerTest()
    {
        final ClickListener listener = context.mock(ClickListener.class);

        context.checking(new Expectations()
        {
            {
                oneOf(sut.prevThumb).addClickListener(listener);
            }
        });

        sut.addPrevClickListener(listener);

        context.assertIsSatisfied();
    }


    /**
     * Add remove thumbnail click listener test.
     */
    @Test
    public final void addRemoveThumbClickListnerTest()
    {
        final ClickListener listener = context.mock(ClickListener.class);

        context.checking(new Expectations()
        {
            {
                oneOf(sut.removeThumbnail).addClickListener(listener);
            }
        });

        sut.addRemoveThumbClickListener(listener);

        context.assertIsSatisfied();
    }

}
