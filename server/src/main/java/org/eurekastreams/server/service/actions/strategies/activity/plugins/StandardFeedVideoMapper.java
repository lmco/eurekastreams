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

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndLinkImpl;

/**
 * Map entries to videos.
 *
 */
public class StandardFeedVideoMapper implements ObjectMapper
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
        HashMap<String, String> object = new HashMap<String, String>();
        object.put("title", stripHTML(entry.getTitle()));

        for (Object linkObj : entry.getLinks())
        {
            SyndLinkImpl link = (SyndLinkImpl) linkObj;
            if (link.getRel().equals("enclosure"))
            {
                object.put("videoStream", stripHTML(link.getHref()));
            }
            else if (link.getRel().equals("alternate"))
            {
                object.put("videoPageUrl", stripHTML(link.getHref()));
            }
        }

        if (entry.getDescription() != null)
        {
            object.put("description", stripHTML(entry.getDescription().getValue()));
        }
        
        return object;
    }

    /**
     * Strip HTML.
     * @param input the input string.
     * @return the output string.
     */
    protected String stripHTML(final String input)
    {
    	String out = input.replaceAll("\\<.*?\\>", "");
    	if (out.length() > MAXLENGTH)
    	{
    		return out.substring(0, MAXLENGTH);
    	}
    	
    	return out;
    }
    
    
    /**
     * Return VIDEO.
     * @return VIDEO.
     */
    @Override
    public BaseObjectType getBaseObjectType()
    {
        return BaseObjectType.VIDEO;
    }

}
