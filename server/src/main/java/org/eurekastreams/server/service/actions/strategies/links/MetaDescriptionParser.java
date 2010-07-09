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
 * Parses the meta description.
 */
public class MetaDescriptionParser implements HtmlLinkInformationParserStrategy
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(MetaDescriptionParser.class);

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
    public MetaDescriptionParser(final int inMaxLength)
    {
        maxLength = inMaxLength;
    }

    /**
     * Parse the information.
     * 
     * @param htmlString
     *            the HTML as a string.
     * @param link
     *            the LinkInformation.
     */
    public void parseInformation(final String htmlString, final LinkInformation link)
    {
        Pattern descriptionPattern = Pattern.compile("<meta name=\"description\" content=\"(.*?)\"",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = descriptionPattern.matcher(htmlString);

        if (matcher.find())
        {
            for (int i = 0; i < matcher.groupCount(); i++)
            {
                log.info("Found description: " + i + "=" + matcher.group(i));
            }

            String desc = matcher.group(1);
            log.info("Found description: " + desc);
            
            if (desc.length() > maxLength)
            {
                String endWith = "...";
                desc = desc.substring(0, maxLength - (1 + endWith.length()));
                desc += endWith;
            }

            link.setDescription(desc);
        }

    }
}
