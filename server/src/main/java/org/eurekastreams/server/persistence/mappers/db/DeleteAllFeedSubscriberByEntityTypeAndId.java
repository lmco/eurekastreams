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

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.DeleteAllFeedSubscriberByEntityTypeAndIdRequest;

/**
 * Mapper to remove all entries from FeedSubscriber table that match passed in entity type and id.
 * 
 */
public class DeleteAllFeedSubscriberByEntityTypeAndId extends
        BaseArgDomainMapper<DeleteAllFeedSubscriberByEntityTypeAndIdRequest, Boolean>
{

    /**
     * Delete all FeedSubscribers with the input entity type and id, then delete any Feeds that has no subscribers.
     * Disclaimer: this is done so hackily - this could be done much cleaner with SQL, and possibly even with HQL/JPA,
     * but I had to move on :)
     * 
     * @param inRequest
     *            the request containing the entity id and type
     * @return true
     */
    @Override
    public Boolean execute(final DeleteAllFeedSubscriberByEntityTypeAndIdRequest inRequest)
    {
        // keep track of the different feeds so we can delete those without subscribers
        List<Long> feedIds = getEntityManager()
                .createQuery(
                        "SELECT DISTINCT feed.id FROM FeedSubscriber where entityId = :entityId AND type = :entityType")
                .setParameter("entityId", inRequest.getEntityId())
                .setParameter("entityType", inRequest.getEntityType()).getResultList();

        getEntityManager()
                .createQuery("DELETE FROM FeedSubscriber fs WHERE fs.entityId = :entityId AND fs.type = :entityType")
                .setParameter("entityId", inRequest.getEntityId())
                .setParameter("entityType", inRequest.getEntityType()).executeUpdate();

        if (feedIds.size() > 0)
        {
            // select all of the feeds and their subscriber count
            List<Object[]> feedIdAndSubscriberCounts = getEntityManager()
                    .createQuery(
                            "SELECT f.id, count(fs.id) FROM Feed f LEFT OUTER JOIN f.feedSubscribers fs "
                                    + "WHERE f.id IN (:feedIds) GROUP BY f.id").setParameter("feedIds", feedIds)
                    .getResultList();

            // loop over the list, finding those that have zero subscribers now
            List<Long> feedsIdsToDelete = new ArrayList<Long>();
            for (Object[] feedIdAndSubscriberCount : feedIdAndSubscriberCounts)
            {
                if ((Long) feedIdAndSubscriberCount[1] == 0)
                {
                    feedsIdsToDelete.add((Long) feedIdAndSubscriberCount[0]);
                }
            }

            // delete the feeds that have no subscribers
            if (feedsIdsToDelete.size() > 0)
            {
                getEntityManager().createQuery("DELETE FROM Feed WHERE id IN (:feedIds)")
                        .setParameter("feedIds", feedsIdsToDelete).executeUpdate();
            }
        }
        return Boolean.TRUE;
    }
}
