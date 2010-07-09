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
package org.eurekastreams.server.action.authorization.feed;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.feed.DeleteFeedSubscriptionRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.plugins.FeedSubscriber;
import org.eurekastreams.server.persistence.mappers.db.GetFeedSubscriptionsByEntity;
import org.eurekastreams.server.persistence.mappers.requests.GetFeedSubscriberRequest;
import org.eurekastreams.server.service.actions.strategies.activity.plugins.GetEntityIdForFeedSubscription;
import org.springframework.security.AccessDeniedException;

/**
 * Make sure the feed subscription that the person or group
 * wants to delete is theirs.
 *
 */
public class DeleteFeedSubscriberAuthorizationStrategy implements AuthorizationStrategy<PrincipalActionContext>
{
	/**
	 * Get all the subs for an entity.
	 */
	private GetFeedSubscriptionsByEntity getFeedSubsMapper;
	/**
	 * Get the entity id.
	 */
	private GetEntityIdForFeedSubscription getEntityId;
	
	/**
	 * Get the entity type.
	 */
	private EntityType type;
	
	/**
	 * Delete feed subscriber authorization.
	 * @param inGetFeedSubsMapper get feed subs mapper.
	 * @param inGetEntityId get entity id mapper.
	 * @param inType entity type.
	 */
	public DeleteFeedSubscriberAuthorizationStrategy(
			final GetFeedSubscriptionsByEntity inGetFeedSubsMapper,
			final GetEntityIdForFeedSubscription inGetEntityId,
			final EntityType inType)
	{
		getFeedSubsMapper = inGetFeedSubsMapper;
		getEntityId = inGetEntityId;
		type = inType;
	}

	/**
	 * Authorize.
	 * @param inActionContext context for the strategy
	 */
	public void authorize(final PrincipalActionContext inActionContext) 
	{
		DeleteFeedSubscriptionRequest request = (DeleteFeedSubscriptionRequest) inActionContext.getParams();
		Serializable[] paramsForAuthorizor = new Serializable[1];
		paramsForAuthorizor[0] = request.getEntityId();
		
		HashMap<String, Serializable> values = new HashMap<String, Serializable>();
		values.put("EUREKA:USER", inActionContext.getPrincipal().getAccountId());
		values.put("EUREKA:GROUP", request.getEntityId());
		
		List<FeedSubscriber> feedSubs = getFeedSubsMapper.execute(
				new GetFeedSubscriberRequest(0L, getEntityId.getEntityId(values), type));
		
		boolean found = false;
		
		for (FeedSubscriber feedSub : feedSubs)
		{
			if (feedSub.getId() == request.getFeedSubscriberId())
			{
				found = true;
				break;
			}
		}
		
		if (!found)
		{
			throw new AccessDeniedException("You can only delete feed subscriptions you own.");
		}
	}
	
}
