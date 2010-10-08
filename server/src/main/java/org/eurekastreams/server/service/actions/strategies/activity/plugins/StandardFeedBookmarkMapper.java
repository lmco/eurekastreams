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

import org.eurekastreams.server.domain.stream.BaseObjectType;

import com.sun.syndication.feed.module.mediarss.MediaModule;
import com.sun.syndication.feed.module.mediarss.MediaModuleImpl;
import com.sun.syndication.feed.synd.SyndEntryImpl;

/**
 * Mapper for bookmarks.
 *
 */
public class StandardFeedBookmarkMapper implements ObjectMapper
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
    @Override
    public HashMap<String, String> getBaseObject(final SyndEntryImpl entry)
    {
        MediaModule media = (MediaModuleImpl) entry.getModule(MediaModule.URI);

        HashMap<String, String> object = new HashMap<String, String>();
        // TODO: Strip markup
        object.put("title", stripHTML(entry.getTitle()));
        if (entry.getDescription() != null)
        {
        	object.put("description", stripHTML(entry.getDescription().getValue()));
        }
        else
        {
        	object.put("description", "");
        }

        object.put("targetUrl", stripHTML(entry.getLink()));
        object.put("targetTitle", stripHTML(entry.getTitle()));
        if (media != null && media.getMetadata().getThumbnail().length > 0)
        {
            object.put("thumbnail", stripHTML(
            		media.getMetadata().getThumbnail()[0].getUrl().toString()));
        }

        return object;
    }

    /**
     * Strip HTML.
     * @param input the input string.
     * @return the output string.
     */
    String stripHTML(final String input)
    {
    	String out = input.replaceAll("\\<.*?\\>", "");
    	if (out.length() > MAXLENGTH)
    	{
    		return out.substring(0, MAXLENGTH);
    	}

    	return out;
    }

    /**
     * get BOOKMARK.
     *
     * @return BOOKMARK.
     */
    @Override
    public BaseObjectType getBaseObjectType()
    {
        return BaseObjectType.BOOKMARK;
    }

}
