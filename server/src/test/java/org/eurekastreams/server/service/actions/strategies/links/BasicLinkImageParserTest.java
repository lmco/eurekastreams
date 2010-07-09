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
package org.eurekastreams.server.service.actions.strategies.links;

import java.io.IOException;
import java.net.MalformedURLException;

import junit.framework.Assert;

import org.eurekastreams.server.domain.stream.LinkInformation;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests parsing images out of a link.
 */
public class BasicLinkImageParserTest
{

    /**
     * System under test.
     */
    private BasicLinkImageParser sut;

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
     * Mock file downloader.
     */
    private ConnectionFacade urlUtils = context.mock(ConnectionFacade.class);

    /**
     * The max number of images to parse.
     */
    private static final int MAX_IMAGES = 5;

    /**
     * Setup test fixtures.
     */
    @Before
    public final void setUp()
    {
        final long timeOut = 3000L;
        sut = new BasicLinkImageParser(urlUtils, MAX_IMAGES, timeOut);
    }

    /**
     * Test parsing with img tags with width/height less than the threshhold.
     */
    @Test
    public final void smallImgTagHeightTest()
    {
        final LinkInformation link = new LinkInformation();

        final String html = "<html><body>" + "<img src=\"http://www.someurl.com/someimg1.png\" width=\""
                + (BasicLinkImageParser.MIN_IMG_SIZE + 1) + "\" height=\"" + (BasicLinkImageParser.MIN_IMG_SIZE - 1)
                + "\">" + "<img src=\"http://www.someurl.com/someimg2.png\" width=\""
                + (BasicLinkImageParser.MIN_IMG_SIZE - 1) + "\" height=\"" + (BasicLinkImageParser.MIN_IMG_SIZE + 1)
                + "\">" + "<img src=\"http://www.someurl.com/someimg3.png\" width=\""
                + (BasicLinkImageParser.MIN_IMG_SIZE - 1) + "\" height=\"" + (BasicLinkImageParser.MIN_IMG_SIZE - 1)
                + "\">" + "</body></html>";

        // Shouldn't bother downloading images that have tags saying they're too small.

        sut.parseInformation(html, link);
    }

    /**
     * Test parsing when the link is an image.
     */
    @Test
    public final void isImageTest()
    {
        final LinkInformation link = new LinkInformation();
        link.setUrl("http://www.someurl.com/image.png");

        // Wouldn't really happen, but shows that it's not actually parsing.
        final String html = "<html><body>" + "<img src=\"http://www.someurl.com/someimg1.png\" /></body></html>";

        // Shouldn't bother downloading images, this is an image.

        sut.parseInformation(html, link);
        Assert.assertTrue(link.getImageUrls().contains(link.getUrl()));
    }

    /**
     * Test parsing with a malformed img tag.
     */
    @Test
    public final void malformedTagTest()
    {
        final LinkInformation link = new LinkInformation();

        final String html = "<html><body>" + "<img s1rc=\"http://www.someurl.com/someimg1.png\" width=\""
                + (BasicLinkImageParser.MIN_IMG_SIZE + 1) + "\" height=\"" + (BasicLinkImageParser.MIN_IMG_SIZE - 1)
                + "\">" + "</body></html>";

        // Shouldn't bother downloading images that have tags saying they're too small.

        sut.parseInformation(html, link);
    }

    /**
     * Test parsing with img tags with width/height less than the threshhold.
     *
     * @throws IOException
     *             shouldn't happen.
     */
    @Test
    public final void smallImgHeightDownloadTest() throws IOException
    {
        final LinkInformation link = new LinkInformation();
        link.setUrl("http://www.someurl.com");

        final String html = "<html><body>" + "<img src=\"http://www.someurl.com/someimg1.png\" /></body></html>";

        // Shouldn't bother downloading images that have tags saying they're too small.

        context.checking(new Expectations()
        {
            {
                oneOf(urlUtils).getImgHeight("http://www.someurl.com/someimg1.png");
                will(returnValue(BasicLinkImageParser.MIN_IMG_SIZE - 1));

                 oneOf(urlUtils).getImgWidth("http://www.someurl.com/someimg1.png");
                 will(returnValue(BasicLinkImageParser.MIN_IMG_SIZE + 1));
            }
        });

        sut.parseInformation(html, link);

        context.assertIsSatisfied();
    }

    /**
     * Test parsing with img tags with width/height less than the threshhold.
     *
     * @throws IOException
     *             shouldn't happen.
     */
    @Test
    public final void smallImgWidthDownloadTest() throws IOException
    {
        final LinkInformation link = new LinkInformation();
        link.setUrl("http://www.someurl.com");

        final String html = "<html><body>" + "<img src=\"http://www.someurl.com/someimg1.png\" /></body></html>";

        context.checking(new Expectations()
        {
            {
                oneOf(urlUtils).getImgHeight("http://www.someurl.com/someimg1.png");
                will(returnValue(BasicLinkImageParser.MIN_IMG_SIZE + 1));

                oneOf(urlUtils).getImgWidth("http://www.someurl.com/someimg1.png");
                will(returnValue(BasicLinkImageParser.MIN_IMG_SIZE - 1));
            }
        });

        sut.parseInformation(html, link);

        Assert.assertFalse(link.getImageUrls().contains("http://www.someurl.com/someimg1.png"));

        context.assertIsSatisfied();
    }

    /**
     * Test parsing with img tags with width/height less than the threshhold.
     *
     * @throws IOException
     *             shouldn't happen.
     */
    @Test
    public final void smallImgHeightWidthDownloadTest() throws IOException
    {
        final LinkInformation link = new LinkInformation();
        link.setUrl("http://www.someurl.com");

        final String html = "<html><body>" + "<img src=\"http://www.someurl.com/someimg1.png\" /></body></html>";

        context.checking(new Expectations()
        {
            {
                oneOf(urlUtils).getImgHeight("http://www.someurl.com/someimg1.png");
                will(returnValue(BasicLinkImageParser.MIN_IMG_SIZE - 1));

                 oneOf(urlUtils).getImgWidth("http://www.someurl.com/someimg1.png");
                 will(returnValue(BasicLinkImageParser.MIN_IMG_SIZE - 1));
            }
        });

        sut.parseInformation(html, link);

        Assert.assertFalse(link.getImageUrls().contains("http://www.someurl.com/someimg1.png"));

        context.assertIsSatisfied();
    }

    /**
     * Test parsing with an img meeting the size requirements.
     *
     * @throws IOException
     *             shouldn't happen.
     */
    @Test
    public final void imgDownloadTest() throws IOException
    {
        final LinkInformation link = new LinkInformation();
        link.setUrl("http://www.someurl.com");

        final String html = "<html><body>" + "<img src=\"http://www.someurl.com/someimg1.png\" /></body></html>";

        context.checking(new Expectations()
        {
            {
                oneOf(urlUtils).getImgHeight("http://www.someurl.com/someimg1.png");
                will(returnValue(BasicLinkImageParser.MIN_IMG_SIZE + 1));

                oneOf(urlUtils).getImgWidth("http://www.someurl.com/someimg1.png");
                will(returnValue(BasicLinkImageParser.MIN_IMG_SIZE + 1));
            }
        });

        sut.parseInformation(html, link);

        Assert.assertTrue(link.getImageUrls().contains("http://www.someurl.com/someimg1.png"));

        context.assertIsSatisfied();
    }

    /**
     * Test parsing with an img meeting the size requirements.
     *
     * @throws IOException
     *             shouldn't happen.
     */
    @Test
    public final void imgDownloadHostPathTest() throws IOException
    {
        final LinkInformation link = new LinkInformation();
        link.setUrl("http://www.someurl.com");

        final String html = "<html><body>" + "<img src=\"/someimg1.png\" /></body></html>";

        context.checking(new Expectations()
        {
            {
                oneOf(urlUtils).getImgHeight("http://www.someurl.com/someimg1.png");
                will(returnValue(BasicLinkImageParser.MIN_IMG_SIZE + 1));

                oneOf(urlUtils).getImgWidth("http://www.someurl.com/someimg1.png");
                will(returnValue(BasicLinkImageParser.MIN_IMG_SIZE + 1));

                oneOf(urlUtils).getProtocol("http://www.someurl.com");
                will(returnValue("http"));

                oneOf(urlUtils).getHost("http://www.someurl.com");
                will(returnValue("www.someurl.com"));

            }
        });

        sut.parseInformation(html, link);

        Assert.assertTrue(link.getImageUrls().contains("http://www.someurl.com/someimg1.png"));

        context.assertIsSatisfied();
    }

    /**
     * Test parsing with an img meeting the size requirements.
     *
     * @throws IOException
     *             shouldn't happen.
     */
    @Test
    public final void imgDownloadRelativePathNoTrailingSlashTest() throws IOException
    {
        final LinkInformation link = new LinkInformation();
        link.setUrl("http://www.someurl.com");

        final String html = "<html><body>" + "<img src=\"someimg1.png\" /></body></html>";

        context.checking(new Expectations()
        {
            {
                oneOf(urlUtils).getImgHeight("http://www.someurl.com/someimg1.png");
                will(returnValue(BasicLinkImageParser.MIN_IMG_SIZE + 1));

                oneOf(urlUtils).getImgWidth("http://www.someurl.com/someimg1.png");
                will(returnValue(BasicLinkImageParser.MIN_IMG_SIZE + 1));

            }
        });

        sut.parseInformation(html, link);

        Assert.assertTrue(link.getImageUrls().contains("http://www.someurl.com/someimg1.png"));

        context.assertIsSatisfied();
    }

    /**
     * Test parsing with an img meeting the size requirements.
     *
     * @throws IOException
     *             shouldn't happen.
     */
    @Test
    public final void imgDownloadRelativePathTrailingSlashTest() throws IOException
    {
        final LinkInformation link = new LinkInformation();
        link.setUrl("http://www.someurl.com/");

        final String html = "<html><body>" + "<img src=\"someimg1.png\" /></body></html>";

        context.checking(new Expectations()
        {
            {
                oneOf(urlUtils).getImgHeight("http://www.someurl.com/someimg1.png");
                will(returnValue(BasicLinkImageParser.MIN_IMG_SIZE + 1));

                oneOf(urlUtils).getImgWidth("http://www.someurl.com/someimg1.png");
                will(returnValue(BasicLinkImageParser.MIN_IMG_SIZE + 1));

            }
        });

        sut.parseInformation(html, link);

        Assert.assertTrue(link.getImageUrls().contains("http://www.someurl.com/someimg1.png"));

        context.assertIsSatisfied();
    }

    /**
     * Test parsing when a MalformedURLException is thrown.
     *
     * @throws IOException
     *             shouldn't happen.
     */
    @Test
    public final void malformedUrl() throws IOException
    {
        final LinkInformation link = new LinkInformation();
        link.setUrl("http://www.someurl.com/");

        final String html = "<html><body>" + "<img src=\"http://\\f\\2w\\asomeimg1.png\" /></body></html>";

        context.checking(new Expectations()
        {
            {
                oneOf(urlUtils).getImgHeight("http://\\f\\2w\\asomeimg1.png");
                will(throwException(new MalformedURLException()));

            }
        });

        sut.parseInformation(html, link);

        context.assertIsSatisfied();
    }

    /**
     * Test parsing when an IOException is thrown.
     *
     * @throws IOException
     *             shouldn't happen.
     */
    @Test
    public final void ioExceptionUrl() throws IOException
    {
        final LinkInformation link = new LinkInformation();
        link.setUrl("http://www.someurl.com/");

        final String html = "<html><body>" + "<img src=\"http://\\f\\2w\\asomeimg1.png\" /></body></html>";

        context.checking(new Expectations()
        {
            {
                oneOf(urlUtils).getImgHeight("http://\\f\\2w\\asomeimg1.png");
                will(throwException(new IOException()));

            }
        });

        sut.parseInformation(html, link);

        context.assertIsSatisfied();
    }
}
