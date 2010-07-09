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
package org.eurekastreams.server.action.request.feed;

import java.io.Serializable;

/**
 * Request sent over the line to delete a feed subscription.
 */
public class DeleteFeedSubscriptionRequest implements Serializable
{
	/**
	 * The feed subscription id.
	 */
	private long feedSubscriberId;
	/**
	 * Short name or account id of the entity making the request.
	 */
	private String entityId;
	
	/**
	 * Just for serialization.
	 */
	public DeleteFeedSubscriptionRequest()
	{
		
	}
	
	/**
	 * Default constructor.
	 * @param inFeedSubscriberId feed subscriber id.
	 * @param inEntityId requester id.
	 */
	public DeleteFeedSubscriptionRequest(final long inFeedSubscriberId, final String inEntityId)
	{
		feedSubscriberId = inFeedSubscriberId;
		entityId = inEntityId;
	}
	
	/**
	 * For serialization.
	 * @param inFeedSubscriberId the domain entity id.
	 */
	private void setFeedSubscriberId(final long inFeedSubscriberId)
	{
		feedSubscriberId = inFeedSubscriberId;
	}
	
	/**
	 * Get the id of the feed subscriber.
	 * @return the id.
	 */
	public long getFeedSubscriberId()
	{
		return feedSubscriberId;
	}
	
	/**
	 * For serialization.
	 * @param inEntityId the entity id.
	 */
	private void setEntityId(final String inEntityId)
	{
		entityId = inEntityId;
	}
	
	/**
	 * Get the id of the requester.
	 * @return the id.
	 */
	public String getEntityId()
	{
		return entityId;
	}
	
	
}
