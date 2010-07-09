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
 * Tests the meta description parser.
 */
public class MetaDescriptionParserTest
{
    /**
     * System under test.
     */
    private MetaDescriptionParser sut;

    /**
     * Setup test fixtures.
     */
    @Before
    public final void setUp()
    {
        final int maxDescLength = 255;
        sut = new MetaDescriptionParser(maxDescLength);
    }

    /**
     * Tests when the description is not found.
     */
    @Test
    public final void noDescriptionTest()
    {
        final String html = "<html></html>";
        final LinkInformation link = new LinkInformation();

        sut.parseInformation(html, link);

        Assert.assertEquals("", link.getDescription());
    }

    /**
     * Tests when the description is found.
     */
    @Test
    public final void parseTitleTest()
    {
        final String html = "<html><meta name=\"description\" content=\"some description\">Some webpage</title></html>";
        final LinkInformation link = new LinkInformation();

        sut.parseInformation(html, link);

        Assert.assertEquals("some description", link.getDescription());
    }
}
