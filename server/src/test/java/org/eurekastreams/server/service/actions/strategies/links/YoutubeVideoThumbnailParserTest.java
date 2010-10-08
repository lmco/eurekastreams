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

import junit.framework.Assert;

import org.eurekastreams.server.domain.stream.LinkInformation;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the YouTube thumbnail parser.
 */
public class YoutubeVideoThumbnailParserTest
{
    /**
     * System under test.
     */
    private YoutubeVideoThumbnailParser sut;

    /**
     * Test account.
     */
    private static final String TEST_ACCOUNT = "testaccount";
    
    /**
     * Setup test fixtures.
     */
    @Before
    public final void setUp()
    {
        sut = new YoutubeVideoThumbnailParser();
    }

    /**
     * Tests when the video ID is not found.
     */
    @Test
    public final void noVideoIdTest()
    {
        final String html = "<html></html>";
        final LinkInformation link = new LinkInformation();
        // No video ID.
        link.setUrl("http://www.youtube.com?something=something");

        sut.parseInformation(html, link, TEST_ACCOUNT);

        Assert.assertEquals(0, link.getImageUrls().size());
    }

    /**
     * Tests when the video ID is found.
     */
    @Test
    public final void parseTitleTest()
    {
        final String html = "<html></html>";
        final LinkInformation link = new LinkInformation();
        link.setUrl("http://www.youtube.com?v=0123456789A");

        sut.parseInformation(html, link, TEST_ACCOUNT);

        // TODO check content of image url.
        Assert.assertEquals(1, link.getImageUrls().size());
    }
}
