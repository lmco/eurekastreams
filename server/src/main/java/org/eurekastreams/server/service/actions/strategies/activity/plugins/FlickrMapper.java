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

import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntryImpl;

/**
 * Maps the Flickr ATOM feeds to activity hashmaps.
 * 
 */
public class FlickrMapper extends StandardFeedPhotoMapper implements
		ObjectMapper 
{

	/**
	 * Use the standard photo mapper but fix the thumbnail to be flickr
	 * specific. Also, blank out the summary.
	 * 
	 * @param entry
	 *            the ATOM entry.
	 * @return the hashmap of values.
	 */
	@Override
	public HashMap<String, String> getBaseObject(final SyndEntryImpl entry) 
	{
		HashMap<String, String> object = super.getBaseObject(entry);

		String content = ((SyndContentImpl) entry.getContents().get(0))
				.getValue();
		object.put("thumbnail", content.split("<img src=\"")[1].split("\"")[0]);
		object.put("description", "");
		return object;
	}

}
