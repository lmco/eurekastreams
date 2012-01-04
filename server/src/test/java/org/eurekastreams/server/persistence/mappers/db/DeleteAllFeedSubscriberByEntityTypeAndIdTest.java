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

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.DeleteAllFeedSubscriberByEntityTypeAndIdRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for DeleteAllFeedSubscriberByEntityTypeAndId mapper.
 * 
 */
public class DeleteAllFeedSubscriberByEntityTypeAndIdTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private DeleteAllFeedSubscriberByEntityTypeAndId sut;

    /**
     * Entity id used in test.
     */
    private Long entityId = 8L;

    /**
     * Test deleting feed subscribers when a feed is subscribed to by many.
     */
    @Test
    public void executeTest()
    {
        // get total number of subscriptions.
        Long origTotalSubscriptionCount = (Long) getEntityManager().createQuery("SELECT COUNT(*) FROM FeedSubscriber")
                .getSingleResult();

        // get number of subscriptions to delete.
        Long origTargetSubscriptionCount = (Long) getEntityManager()
                .createQuery("SELECT COUNT(*) FROM FeedSubscriber WHERE type = :type AND entityId = :entityId")
                .setParameter("type", EntityType.GROUP).setParameter("entityId", entityId).getSingleResult();

        // do the delete.
        sut.execute(new DeleteAllFeedSubscriberByEntityTypeAndIdRequest(entityId, EntityType.GROUP));

        getEntityManager().flush();
        getEntityManager().clear();

        // get new total number of subscriptions.
        Long newTotalSubscriptionCount = (Long) getEntityManager().createQuery("SELECT COUNT(*) FROM FeedSubscriber")
                .getSingleResult();

        // get count of targets after delete (should be 0).
        Long newTargetSubscriptionCount = (Long) getEntityManager()
                .createQuery("SELECT COUNT(*) FROM FeedSubscriber WHERE type = :type AND entityId = :entityId")
                .setParameter("type", EntityType.GROUP).setParameter("entityId", entityId).getSingleResult();

        // assert that targets have been deleted.
        assertEquals(0L, newTargetSubscriptionCount.longValue());

        // assert that ONLY targets have been deleted.
        assertEquals(newTotalSubscriptionCount.longValue(), origTotalSubscriptionCount.longValue()
                - origTargetSubscriptionCount.longValue());

        // assert feed 2 is still there
        assertEquals(1L, getEntityManager().createQuery("SELECT COUNT(*) FROM Feed WHERE id=2").getSingleResult());
    }

    /**
     * Test deleting feed subscribers when a feed is subscribed to by only this group - feed should go away.
     */
    @Test
    public void executeTestWithFeedBeingDeleted()
    {
        // delete the other feed subscriber to feed 2
        getEntityManager().createQuery("DELETE FROM FeedSubscriber WHERE id = 2").executeUpdate();
        getEntityManager().createQuery("DELETE FROM FeedSubscriber WHERE id = 5").executeUpdate();

        // get total number of subscriptions.
        Long origTotalSubscriptionCount = (Long) getEntityManager().createQuery("SELECT COUNT(*) FROM FeedSubscriber")
                .getSingleResult();

        // get number of subscriptions to delete.
        Long origTargetSubscriptionCount = (Long) getEntityManager()
                .createQuery("SELECT COUNT(*) FROM FeedSubscriber WHERE type = :type AND entityId = :entityId")
                .setParameter("type", EntityType.GROUP).setParameter("entityId", entityId).getSingleResult();

        // do the delete.
        sut.execute(new DeleteAllFeedSubscriberByEntityTypeAndIdRequest(entityId, EntityType.GROUP));

        getEntityManager().flush();
        getEntityManager().clear();

        // get new total number of subscriptions.
        Long newTotalSubscriptionCount = (Long) getEntityManager().createQuery("SELECT COUNT(*) FROM FeedSubscriber")
                .getSingleResult();

        // get count of targets after delete (should be 0).
        Long newTargetSubscriptionCount = (Long) getEntityManager()
                .createQuery("SELECT COUNT(*) FROM FeedSubscriber WHERE type = :type AND entityId = :entityId")
                .setParameter("type", EntityType.GROUP).setParameter("entityId", entityId).getSingleResult();

        // assert that targets have been deleted.
        assertEquals(0L, newTargetSubscriptionCount.longValue());

        // assert that ONLY targets have been deleted.
        assertEquals(newTotalSubscriptionCount.longValue(), origTotalSubscriptionCount.longValue()
                - origTargetSubscriptionCount.longValue());

        // assert feed 2 is gone
        assertEquals(0L, getEntityManager().createQuery("SELECT COUNT(*) FROM Feed WHERE id=2").getSingleResult());
    }
}
