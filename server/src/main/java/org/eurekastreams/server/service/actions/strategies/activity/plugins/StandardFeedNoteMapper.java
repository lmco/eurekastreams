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

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntryImpl;

/**
 * Mapper for note entries.
 *
 */
public class StandardFeedNoteMapper implements ObjectMapper
{

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
        HashMap<String, String> object = new HashMap<String, String>();
        // TODO: Strip markup
        if (entry.getContents().size() > 0)
        {
            SyndContent content = (SyndContentImpl) entry.getContents().get(0);
            object.put("content", content.getValue());
        }
        else
        {
            object.put("content", entry.getTitle());
        }

        return object;
    }

    /**
     * Return NOTE.
     * @return NOTE.
     */
    @Override
    public BaseObjectType getBaseObjectType()
    {
        return BaseObjectType.NOTE;
    }

}
