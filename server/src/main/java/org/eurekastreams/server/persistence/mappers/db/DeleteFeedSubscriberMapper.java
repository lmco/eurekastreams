/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.stream.plugins.FeedSubscriber;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Deletes a FeedSubscriber, deleting the Feed as well, if there are no more subscribers.
 */
public class DeleteFeedSubscriberMapper extends BaseArgDomainMapper<Long, Boolean>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Delete the FeedSubscriber, also deleting the Feed if there are no more subscribers to it.
     * 
     * @param inFeedSubscriberId
     *            the ID of the FeedSubscriber to delete
     * @return true
     */
    @Override
    public Boolean execute(final Long inFeedSubscriberId)
    {
        Long feedId;
        FeedSubscriber subscriber;

        // get the feed
        subscriber = (FeedSubscriber) getEntityManager().createQuery("FROM FeedSubscriber WHERE id = :id")
                .setParameter("id", inFeedSubscriberId).getSingleResult();

        feedId = subscriber.getFeed().getId();

        getEntityManager().remove(subscriber);
        getEntityManager().flush();

        // see if the feed has no subscribers - if so, delete it
        Long subscriberCount = (Long) getEntityManager()
                .createQuery("SELECT COUNT(elements(feedSubscribers)) FROM Feed WHERE id = :id")
                .setParameter("id", feedId).getSingleResult();

        if (subscriberCount == 0)
        {
            log.info("Deleting feed - no more subscribers - id: " + feedId);
            getEntityManager().createQuery("DELETE FROM Feed WHERE id = :id").setParameter("id", feedId)
                    .executeUpdate();
            getEntityManager().flush();
        }

        return Boolean.TRUE;
    }
}
