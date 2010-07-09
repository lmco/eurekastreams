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

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.stream.LinkInformation;

/**
 * Parses a YouTube thumbnail.
 */
public class YoutubeVideoThumbnailParser implements HtmlLinkInformationParserStrategy
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(YoutubeVideoThumbnailParser.class);

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
        String videoId = "";

        Pattern idPattern = Pattern.compile("^[^v]+v.(.{11}).*", Pattern.CASE_INSENSITIVE);
        Matcher matcher = idPattern.matcher(link.getUrl());

        if (matcher.find())
        {
            videoId = matcher.group(1);
            log.info("Found YouTube Video ID: " + videoId);

            String thumbnailUrl = "http://i.ytimg.com/vi/" + videoId + "/default.jpg";
            Set<String> thumbnails = new HashSet<String>();
            thumbnails.add(thumbnailUrl);

            link.setImageUrls(thumbnails);
            link.setLargestImageUrl(thumbnailUrl);
        }
    }
}
