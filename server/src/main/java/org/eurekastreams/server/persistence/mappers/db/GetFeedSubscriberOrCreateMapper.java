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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.List;

import org.eurekastreams.server.domain.stream.plugins.Feed;
import org.eurekastreams.server.domain.stream.plugins.FeedSubscriber;
import org.eurekastreams.server.persistence.mappers.ReadMapper;
import org.eurekastreams.server.persistence.mappers.requests.GetFeedSubscriberRequest;

/**
 * Get the feed subscriber if he/she exists, or create a new one.
 *
 */
public class GetFeedSubscriberOrCreateMapper extends ReadMapper<GetFeedSubscriberRequest, FeedSubscriber>
{
    /**
     * Execute.
     * @param inRequest the request.
     * @return the Feed.
     */
    @SuppressWarnings("unchecked")
    @Override
    public FeedSubscriber execute(final GetFeedSubscriberRequest inRequest)
    {
        String query = "FROM FeedSubscriber WHERE feed.id=:feedId AND entityId=:entityId AND type = :type";

        List<FeedSubscriber> feedSubs = getEntityManager().createQuery(query)
        		.setParameter("feedId", inRequest.getFeedId())
        		.setParameter("entityId", inRequest.getEntityId())
                .setParameter("type", inRequest.getEntityType()).getResultList();

        if (feedSubs.size() > 0)
        {
            return feedSubs.get(0);
        }

        FeedSubscriber feedSub = new FeedSubscriber();
        
        feedSub.setEntityId(inRequest.getEntityId());
        feedSub.setEntityType(inRequest.getEntityType());
        feedSub.setFeed((Feed) getHibernateSession().load(Feed.class, inRequest.getFeedId()));
        getEntityManager().persist(feedSub);

        return feedSub;
    }
}
