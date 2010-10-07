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

import org.eurekastreams.server.domain.stream.LinkInformation;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Link parser test.
 */
public class HtmlLinkParserTest
{

    /**
     * System under test.
     */
    private HtmlLinkParser sut;

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
     * Image parser.
     */
    private HtmlLinkInformationParserStrategy imageParser = context.mock(HtmlLinkInformationParserStrategy.class,
            "imageParser");

    /**
     * Description parser.
     */
    private HtmlLinkInformationParserStrategy descriptionParser = context.mock(HtmlLinkInformationParserStrategy.class,
            "descriptionParser");

    /**
     * Title parser.
     */
    private HtmlLinkInformationParserStrategy titleParser = context.mock(HtmlLinkInformationParserStrategy.class,
            "titleParser");

    /**
     * Test user account.
     */
    private static final String TEST_ACCOUNT= "testAccount";
    
    /**
     * Setup test fixtures.
     */
    @Before
    public final void setUp()
    {
        sut = new HtmlLinkParser();
        sut.setDescriptionParser(descriptionParser);
        sut.setImageParser(imageParser);
        sut.setTitleParser(titleParser);
    }

    /**
     * Parse information test.
     */
    @Test
    public final void parseInformationTest()
    {
        final LinkInformation link = new LinkInformation();
        final String html = "<html></html>";

        context.checking(new Expectations()
        {
            {
                oneOf(descriptionParser).parseInformation(html, link, TEST_ACCOUNT);
                oneOf(imageParser).parseInformation(html, link, TEST_ACCOUNT);
                oneOf(titleParser).parseInformation(html, link, TEST_ACCOUNT);
            }
        });

        sut.parseLinkInformation(html, link, TEST_ACCOUNT);

        context.assertIsSatisfied();
    }
}
