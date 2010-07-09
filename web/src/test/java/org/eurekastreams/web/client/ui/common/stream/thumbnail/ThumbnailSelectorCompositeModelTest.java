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

import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.eurekastreams.server.domain.stream.LinkInformation;

import com.google.gwt.user.client.ui.Image;

/**
 * Thumbnail selector model test.
 */
public class ThumbnailSelectorCompositeModelTest
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
    private ThumbnailSelectorCompositeModel sut;

    /**
     * Setup test fixtures.
     */
    @Before
    public final void setUp()
    {
        sut = new ThumbnailSelectorCompositeModel();
    }

    /**
     * Set link test without thumbnails.
     */
    @Test
    public final void setLinkWithoutThumbnailsTest()
    {
        final LinkInformation link = new LinkInformation();

        // No expectations necessary
        
        sut.setLink(link);

        Assert.assertEquals("", sut.getSelectedThumbnailUrl());
        Assert.assertEquals(link, sut.getLink());
        Assert.assertFalse(sut.hasNext());
        Assert.assertFalse(sut.hasPrevious());
    }

    /**
     * Set link test with thumbnails.
     */
    @Test
    public final void setLinkWithThumbnailsTest()
    {
        final LinkInformation link = new LinkInformation();
        Set<String> imageUrls = new HashSet<String>();
        imageUrls.add("http://www.someurl.com/someimg.png");
        imageUrls.add("http://www.someurl.com/someanimation.gif");

        final Image pngImg = context.mock(Image.class, "pngImg");
        final Image gifImg = context.mock(Image.class, "gifImg");

        final int gifSize = 100;
        final int pngSize = 500;
        
        context.checking(new Expectations()
        {
            {
                allowing(pngImg).getHeight();
                will(returnValue(pngSize));

                allowing(pngImg).getWidth();
                will(returnValue(pngSize));                
                
                allowing(gifImg).getHeight();
                will(returnValue(gifSize));

                allowing(gifImg).getWidth();
                will(returnValue(gifSize));
            }
        });

        link.setImageUrls(imageUrls);
        sut.setLink(link);

        Assert.assertEquals("http://www.someurl.com/someimg.png", sut.getSelectedThumbnailUrl());
        Assert.assertTrue(sut.hasNext());
        Assert.assertFalse(sut.hasPrevious());
        
        // Test paging
        sut.selectNext();
        Assert.assertFalse(sut.hasNext());
        Assert.assertTrue(sut.hasPrevious());

        sut.selectPrevious();
        Assert.assertTrue(sut.hasNext());
        Assert.assertFalse(sut.hasPrevious());
    }
}
