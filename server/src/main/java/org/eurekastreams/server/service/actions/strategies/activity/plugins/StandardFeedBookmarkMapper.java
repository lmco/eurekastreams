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

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.domain.stream.plugins.Feed;

import com.sun.syndication.feed.module.mediarss.MediaModule;
import com.sun.syndication.feed.module.mediarss.MediaModuleImpl;
import com.sun.syndication.feed.synd.SyndEntry;

/**
 * Mapper for bookmarks.
 *
 */
public class StandardFeedBookmarkMapper implements FeedObjectActivityBuilder
{
    /**
     * Max length.
     */
    private static final int MAXLENGTH = 250;

    /**
     * Gets the base object.
     *
     * @param entry
     *            the entry.
     * @return the object.
     */
    protected HashMap<String, String> getBaseObject(final SyndEntry entry)
    {
        HashMap<String, String> object = new HashMap<String, String>();
        // TODO: Strip markup
        object.put("title", InputCleaner.stripHtml(entry.getTitle(), MAXLENGTH));
        if (entry.getDescription() != null)
        {
            object.put("description", InputCleaner.stripHtml(entry.getDescription().getValue(), MAXLENGTH));
        }
        else
        {
            object.put("description", "");
        }

        object.put("targetUrl", InputCleaner.stripHtml(entry.getLink(), MAXLENGTH));
        object.put("targetTitle", InputCleaner.stripHtml(entry.getTitle(), MAXLENGTH));

        MediaModule media = (MediaModuleImpl) entry.getModule(MediaModule.URI);
        if (media != null && media.getMetadata().getThumbnail().length > 0)
        {
            object.put("thumbnail", InputCleaner.stripHtml(media.getMetadata().getThumbnail()[0].getUrl().toString(),
                    MAXLENGTH));
        }

        return object;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void build(final Feed inFeed, final SyndEntry inEntry, final Activity inActivity)
    {
        inActivity.setBaseObjectType(BaseObjectType.BOOKMARK);
        inActivity.setBaseObject(getBaseObject(inEntry));
    }

}
