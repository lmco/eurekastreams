/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.actions.strategies.activity.plugins;

import java.util.HashMap;

import com.sun.syndication.feed.synd.SyndEntry;

/**
 * Youtube mapper.
 */
public class YoutubeMapper extends StandardFeedVideoMapper implements FeedObjectActivityBuilder
{

    /**
     * Gets the base object.
     *
     * @param entry
     *            the entry.
     * @return the object.
     */
    @Override
    protected HashMap<String, String> getBaseObject(final SyndEntry entry)
    {
        HashMap<String, String> object = super.getBaseObject(entry);

        object.put("description", InputCleaner.stripHtml(entry.getDescription().getValue().split("<span>")[1]
                .split("</span>")[0], MAXLENGTH));
        object.put("thumbnail", InputCleaner.stripHtml(
                entry.getDescription().getValue().split("<img alt=\"\" src=\"")[1].split("\"")[0], MAXLENGTH));
        object.put("videoStream", "http://www.youtube.com/v/" + entry.getLink().split("v=")[1].split("&")[0]);
        object.put("videoPageUrl", InputCleaner.stripHtml(entry.getLink(), MAXLENGTH));
        return object;
    }
}
