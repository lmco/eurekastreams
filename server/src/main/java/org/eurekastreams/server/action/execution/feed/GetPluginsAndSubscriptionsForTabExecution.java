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
package org.eurekastreams.server.action.execution.feed;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.formatting.DateFormatter;
import org.eurekastreams.server.action.response.feed.PluginAndFeedSubscriptionsResponse;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.plugins.FeedSubscriber;
import org.eurekastreams.server.domain.stream.plugins.PluginDefinition;
import org.eurekastreams.server.persistence.mappers.db.GetAllPluginsMapper;
import org.eurekastreams.server.persistence.mappers.db.GetFeedSubscriptionsByEntity;
import org.eurekastreams.server.persistence.mappers.requests.GetFeedSubscriberRequest;
import org.eurekastreams.server.service.actions.requests.EmptyRequest;
import org.eurekastreams.server.service.actions.strategies.activity.plugins.GetEntityIdForFeedSubscription;

/**
 * Get the plugins and subs for a user/group's tab.
 *
 */
public class GetPluginsAndSubscriptionsForTabExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Milliseconds in minutes.
     */
    private static final int MSINMIN = 60000;
    /**
     * Get all the plugins.
     */
    private GetAllPluginsMapper getAllPluginsMapper;
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
     * Default constructor.
     *
     * @param inGetAllPluginsMapper
     *            plugin mapper.
     * @param inGetFeedSubsMapper
     *            feed sub mapper.
     * @param inGetEntityId
     *            get entity id mapper.
     * @param inType
     *            type.
     */
    public GetPluginsAndSubscriptionsForTabExecution(final GetAllPluginsMapper inGetAllPluginsMapper,
            final GetFeedSubscriptionsByEntity inGetFeedSubsMapper,
            final GetEntityIdForFeedSubscription inGetEntityId, final EntityType inType)
    {
        getAllPluginsMapper = inGetAllPluginsMapper;
        getFeedSubsMapper = inGetFeedSubsMapper;
        getEntityId = inGetEntityId;
        type = inType;
    }

    /**
     * This method performs the action.
     *
     * @param context
     *            the context for the strategy
     * @return the response object.
     * @throws ExecutionException
     *             if any errors occur in strategy
     */
    public PluginAndFeedSubscriptionsResponse execute(final PrincipalActionContext context) throws ExecutionException
    {

        HashMap<String, Serializable> values = new HashMap<String, Serializable>();
        Principal principal = context.getPrincipal();
        values.put("EUREKA:USER", principal.getAccountId());
        values.put("EUREKA:GROUP", (String) context.getParams());

        List<PluginDefinition> plugins = getAllPluginsMapper.execute(new EmptyRequest());
        List<FeedSubscriber> feedSubs =
                getFeedSubsMapper.execute(new GetFeedSubscriberRequest(0L, getEntityId.getEntityId(values), type,
                        principal.getId()));
        for (FeedSubscriber feedSub : feedSubs)
        {
            feedSub.getFeed().getPlugin().getId();
            if (feedSub.getFeed().getLastUpdated() == null)
            {
                feedSub.getFeed().setTimeAgo(null);
            }
            else
            {
                DateFormatter dateFormatter = new DateFormatter(new Date());
                feedSub.getFeed().setTimeAgo(
                        dateFormatter.timeAgo(new Date(feedSub.getFeed().getLastUpdated() * MSINMIN)));
            }
        }

        return new PluginAndFeedSubscriptionsResponse(plugins, feedSubs);
    }
}
