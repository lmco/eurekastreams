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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.stream.LinkInformation;

/**
 * Parses the title out of an HTML page.
 */
public class HtmlLinkTitleParser implements HtmlLinkInformationParserStrategy
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(HtmlLinkTitleParser.class);

    /**
     * Maximum length.
     */
    private int maxLength = 0;

    /**
     * Constuctor.
     * 
     * @param inMaxLength
     *            maximum description length.
     */
    public HtmlLinkTitleParser(final int inMaxLength)
    {
        maxLength = inMaxLength;
    }

    /**
     * Finds the title of a page in the HTML.
     * 
     * @param htmlString
     *            the HTML of the page as a String.
     * @param link the link information.
     */
    public void parseInformation(final String htmlString, final LinkInformation link)
    {
        Pattern titlePattern = Pattern.compile("<title>(.*?)</title>", Pattern.CASE_INSENSITIVE);
        Matcher matcher = titlePattern.matcher(htmlString);
        String title = "";
        
        if (matcher.find())
        {
            title = matcher.group(1);
            log.info("Found title: " + title);
        }
        else
        {
            title = link.getUrl();
        }

        if (title.length() > maxLength)
        {
            String endWith = "...";
            title = title.substring(0, maxLength - (1 + endWith.length()));
            title += endWith;
        }            
        
        link.setTitle(title);
    }

}
