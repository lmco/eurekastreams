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
package org.eurekastreams.server.domain.stream;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Tests for LinkInformation.
 */
public class LinkInformationTest
{
    /**
     * System under test.
     */
    private LinkInformation sut = new LinkInformation();

    /**
     * Description.
     */
    private String description = "some description";

    /**
     * Image urls.
     */
    private Set<String> imageUrls = new TreeSet<String>();

    /**
     * Selected thumbnail.
     */
    private String selectedThumbnail = "http://www.someurl.com/img.png";

    /**
     * The largest image URL.
     */
    private String largestThumbnail = "http://www.someurl.com/img.png";

    /**
     * Title.
     */
    private String title = "Some Site";

    /**
     * URL.
     */
    private String url = "http:///www.someurl.com/something.html";

    /**
     * Source.
     */
    private String source = "http:///www.someurl.com";

    /**
     * Date created.
     */
    private Date created = new Date();

    /**
     * Tests the properties.
     */
    @Test
    public final void propertyTest()
    {
        sut.setDescription(description);
        sut.setImageUrls(imageUrls);
        sut.setSelectedThumbnail(selectedThumbnail);
        sut.setTitle(title);
        sut.setUrl(url);
        sut.setLargestImageUrl(largestThumbnail);
        sut.setCreated(created);
        sut.setSource(source);

        Assert.assertEquals(description, sut.getDescription());
        Assert.assertEquals(imageUrls, sut.getImageUrls());
        Assert.assertEquals(selectedThumbnail, sut.getSelectedThumbnail());
        Assert.assertEquals(title, sut.getTitle());
        Assert.assertEquals(url, sut.getUrl());
        Assert.assertEquals(created, sut.getCreated());
        Assert.assertEquals(largestThumbnail, sut.getLargestImageUrl());
        Assert.assertEquals(source, sut.getSource());

    }

    /**
     * Get HTML test.
     */
    @Test
    public final void getHtmlTestWithThumbnail()
    {
        sut.setDescription(description);
        sut.setImageUrls(imageUrls);
        sut.setSelectedThumbnail(selectedThumbnail);
        sut.setTitle(title);
        sut.setUrl(url);
        sut.setCreated(created);

        /*
         * The only way to make this test have any value would make it very brittle and at the end of the day it needs
         * to be verified visually.
         */
        Assert.assertTrue(sut.getHtml().length() > 0);
        Assert.assertTrue(sut.getHtml().contains(description));
        Assert.assertTrue(sut.getHtml().contains(url));
        Assert.assertTrue(sut.getHtml().contains(title));
        Assert.assertTrue(sut.getHtml().contains(selectedThumbnail));
        Assert.assertTrue(sut.getHtml().contains("<img"));
    }

    /**
     * Get HTML test.
     */
    @Test
    public final void getHtmlTestWithoutThumbnail()
    {
        sut.setDescription(description);
        sut.setImageUrls(imageUrls);

        // No thumbnail
        sut.setSelectedThumbnail("");
        sut.setTitle(title);
        sut.setUrl(url);
        sut.setCreated(created);

        /*
         * The only way to make this test have any value would make it very brittle and at the end of the day it needs
         * to be verified visually.
         */
        Assert.assertTrue(sut.getHtml().length() > 0);
        Assert.assertTrue(sut.getHtml().contains(description));
        Assert.assertTrue(sut.getHtml().contains(url));
        Assert.assertTrue(sut.getHtml().contains(title));
        Assert.assertFalse(sut.getHtml().contains(selectedThumbnail));
        Assert.assertFalse(sut.getHtml().contains("<img"));
    }
}
