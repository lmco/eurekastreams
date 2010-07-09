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
package org.eurekastreams.server.service.actions.strategies.links;

import org.eurekastreams.server.domain.stream.LinkInformation;

/**
 * Parses an HTML document.
 */
public class HtmlLinkParser
{
    /**
     * Image parser.
     */
    private HtmlLinkInformationParserStrategy imageParser;

    /**
     * Description parser.
     */
    private HtmlLinkInformationParserStrategy descriptionParser;

    /**
     * Title parser.
     */
    private HtmlLinkInformationParserStrategy titleParser;

    /**
     * Matching host regex this strategy applies to.
     */
    private String regex;

    /**
     * Parse the HTML.
     * 
     * @param htmlString
     *            the HTML as a string.
     * @param link
     *            the link.
     */
    public void parseLinkInformation(final String htmlString, final LinkInformation link)
    {
        titleParser.parseInformation(htmlString, link);
        descriptionParser.parseInformation(htmlString, link);
        imageParser.parseInformation(htmlString, link);
    }

    /**
     * @param inImageParser
     *            the imageParser to set
     */
    public void setImageParser(final HtmlLinkInformationParserStrategy inImageParser)
    {
        this.imageParser = inImageParser;
    }

    /**
     * @param inDescriptionParser
     *            the descriptionParser to set
     */
    public void setDescriptionParser(final HtmlLinkInformationParserStrategy inDescriptionParser)
    {
        this.descriptionParser = inDescriptionParser;
    }

    /**
     * @param inTitleParser
     *            the titleParser to set
     */
    public void setTitleParser(final HtmlLinkInformationParserStrategy inTitleParser)
    {
        this.titleParser = inTitleParser;
    }

    /**
     * The regex this strategy applies to. Applied to the host name.
     * 
     * @param inRegex
     *            the regex.
     */
    public void setRegex(final String inRegex)
    {
        regex = inRegex;
    }

    /**
     * Get the regex the host name must match to apply this strategy.
     * 
     * @return the regex.
     */
    public String getRegex()
    {
        return regex;
    }
}
