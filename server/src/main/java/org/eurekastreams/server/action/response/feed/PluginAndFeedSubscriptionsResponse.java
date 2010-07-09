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
package org.eurekastreams.server.action.response.feed;

import java.io.Serializable;
import java.util.List;

import org.eurekastreams.server.domain.stream.plugins.FeedSubscriber;
import org.eurekastreams.server.domain.stream.plugins.PluginDefinition;

/**
 * Response for the get plugin and sub action.
 *
 */
public class PluginAndFeedSubscriptionsResponse implements Serializable
{
	/**
	 * Plugins.
	 */
	private List<PluginDefinition> plugins;
	/**
	 * Feed subs.
	 */
	private List<FeedSubscriber> feedSubscriptions;
	
	/**
	 * For Serialization.
	 */
	public PluginAndFeedSubscriptionsResponse()
	{
	    
	}
	
	/**
	 * Default constructor.
	 * @param inPlugins plugins.
	 * @param inFeedSubs feed subs.
	 */
	public PluginAndFeedSubscriptionsResponse(
			final List<PluginDefinition> inPlugins, final List<FeedSubscriber> inFeedSubs)
	{
		plugins = inPlugins;
		feedSubscriptions = inFeedSubs;
	}
	
	/**
	 * Set plugins.
	 * @param inPlugins plugins.
	 */
	private void setPlugins(final List<PluginDefinition> inPlugins)
	{
		plugins = inPlugins;
	}
	
	/**
	 * Get plugins.
	 * @return plugins.
	 */
	public List<PluginDefinition> getPlugins()
	{
		return plugins;
	}
	
	/**
	 * Set the feed subs, here for serialization.
	 * @param inFeedSubs feed subs.
	 */
	private void setFeedSubcribers(final List<FeedSubscriber> inFeedSubs)
	{
		feedSubscriptions = inFeedSubs;
	}
	
	/**
	 * Get the feed subs.
	 * @return the feed subs.
	 */
	public List<FeedSubscriber> getFeedSubcribers()
	{
		return feedSubscriptions;
	}
}
