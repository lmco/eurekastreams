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

import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.server.domain.stream.plugins.Feed;
import org.eurekastreams.server.domain.stream.plugins.FeedSubscriber;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for DeleteFeedSubscriberMapper.
 */
public class DeleteFeedSubscriberMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private DeleteFeedSubscriberMapper sut = new DeleteFeedSubscriberMapper();

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test deleting feed subscribers until there are no more.
     */
    @Test
    public void testDeletingFeedSubscriptionUntilNoneLeftForFeed()
    {
        final Long feedSubscriptionId1 = 1L;
        final Long feedSubscriptionId2 = 6L;
        final Long feedSubscriptionId3 = 8L;

        final Long feedId = 1L;

        // test initial conditions - we should have
        Assert.assertTrue(feedExists(feedId));
        Assert.assertEquals(3, subscriberCount(feedId));
        flush();

        // execute SUT on the first feed subscription - should be 2 left
        sut.execute(feedSubscriptionId1);

        Assert.assertTrue(feedExists(feedId));
        Assert.assertEquals(2, subscriberCount(feedId));
        flush();

        // execute SUT on the second feed subscription - should be 1 left
        sut.execute(feedSubscriptionId2);

        Assert.assertTrue(feedExists(feedId));
        Assert.assertEquals(1, subscriberCount(feedId));
        flush();

        // execute SUT on the first feed subscription - should be 0 left, and no more feed
        sut.execute(feedSubscriptionId3);

        Assert.assertFalse(feedExists(feedId));
        Assert.assertEquals(0, subscriberCount(feedId));
    }

    /**
     * Return whether the feed with the input id exists.
     * 
     * @param inFeedId
     *            the id of the feed to test
     * @return whether it exists
     */
    private boolean feedExists(final long inFeedId)
    {
        List<Feed> feeds = getEntityManager().createQuery("FROM Feed WHERE id = :feedId")
                .setParameter("feedId", inFeedId).getResultList();
        return feeds.size() == 1;
    }

    /**
     * Get the number of subscribers for the feed with the input id.
     * 
     * @param inFeedId
     *            the id of the feed to search for subscribers
     * @return the number of subscribers for the feed with the input id
     */
    private long subscriberCount(final long inFeedId)
    {
        List<FeedSubscriber> subscribers = getEntityManager()
                .createQuery("FROM FeedSubscriber WHERE feedId = :feedId").setParameter("feedId", inFeedId)
                .getResultList();
        return subscribers.size();
    }

    /**
     * clear the entity manager's cache.
     */
    private void flush()
    {
        getEntityManager().flush();
    }
}
