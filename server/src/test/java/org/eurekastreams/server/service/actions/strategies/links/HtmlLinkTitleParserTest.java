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
 * HTML Title parse.
 *
 */
public class HtmlLinkTitleParserTest
{
    /**
     * System under test.
     */
    private HtmlLinkTitleParser sut;

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
        final int titleMaxLen = 128;
        sut = new HtmlLinkTitleParser(titleMaxLen);
    }

    /**
     * Tests when the title is not found.
     */
    @Test
    public final void noTitleTest()
    {
        final String html = "<html></html>";
        final LinkInformation link = new LinkInformation();
        link.setUrl("http:///www.someurl.com");

        sut.parseInformation(html, link, TEST_ACCOUNT);

        Assert.assertEquals(link.getUrl(), link.getTitle());
    }

    /**
     * Tests when the title is found.
     */
    @Test
    public final void parseTitleTest()
    {
        final String html = "<html><title>Some webpage</title></html>";
        final LinkInformation link = new LinkInformation();

        sut.parseInformation(html, link, TEST_ACCOUNT);

        Assert.assertEquals("Some webpage", link.getTitle());
    }
}
