/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.server.domain.stream.plugins.Feed;
import org.eurekastreams.server.persistence.mappers.requests.CurrentDateInMinutesRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for setRefreshableFeedsAsPending.
 *
 */
public class SetRefreshableFeedsAsPendingTest extends MapperTest
{
    /**
     * Time 1011.
     */
    private static final long TIME1011 = 1011;

    /**
     * Time 1031.
     */
    private static final long TIME1031 = 1031;

    /**
     * Time 1061.
     */
    private static final long TIME1061 = 1061;

    /**
     * Time 1005.
     */
    private static final long TIME1005 = 1005;

    /**
     * System under test.
     */
    @Autowired
    private SetRefreshableFeedsAsPending sut;

    /**
     * All of the feeds in data set were updated at minute 1000. Only one of them is not pending and has an update time
     * < 11, so this should only update 1 feed, feed ID 1.
     */
    @Test
    public void testExecuteAtTime1011()
    {
        CurrentDateInMinutesRequest request = new CurrentDateInMinutesRequest(TIME1011);
        sut.execute(request);

        Query q = getEntityManager().createQuery("FROM Feed");
        List<Feed> results = q.getResultList();

        assertTrue(results.get(0).getPending());
        assertTrue(results.get(1).getPending());
        assertFalse(results.get(2).getPending());
        assertFalse(results.get(3).getPending());
        assertFalse(results.get(4).getPending());
    }

    /**
     * Two of the feeds have update times of less than 31 (10 and 30, IDs 1 and 5) One of the feeds has no update time
     * but it's plugin has an update time of 30. Therefore this should update 3 feeds.
     */
    @Test
    public void testExecuteAtTime1031()
    {
        CurrentDateInMinutesRequest request = new CurrentDateInMinutesRequest(TIME1031);
        sut.execute(request);

        getEntityManager().flush();
        Query q = getEntityManager().createQuery("FROM Feed");

        List<Feed> results = q.getResultList();

        assertTrue(results.get(0).getPending());
        assertTrue(results.get(1).getPending());
        assertFalse(results.get(2).getPending());
        assertTrue(results.get(3).getPending());
        assertTrue(results.get(4).getPending());
    }

    /**
     * All of the feeds have over under 60 min refresh times except the 1 pending Four results should be updated.
     */
    @Test
    public void testExecuteAtTime1061()
    {
        CurrentDateInMinutesRequest request = new CurrentDateInMinutesRequest(TIME1061);
        sut.execute(request);

        getEntityManager().flush();
        Query q = getEntityManager().createQuery("FROM Feed");

        List<Feed> results = q.getResultList();

        assertTrue(results.get(0).getPending());
        assertTrue(results.get(1).getPending());
        assertTrue(results.get(2).getPending());
        assertTrue(results.get(3).getPending());
        assertTrue(results.get(4).getPending());
    }

    /**
     * None of the feeds have refresh times of under 5 min. No updates.
     */
    @Test
    public void testExecuteAtTime1005()
    {
        CurrentDateInMinutesRequest request = new CurrentDateInMinutesRequest(TIME1005);
        sut.execute(request);

        getEntityManager().flush();
        Query q = getEntityManager().createQuery("FROM Feed");

        List<Feed> results = q.getResultList();

        assertFalse(results.get(0).getPending());
        assertTrue(results.get(1).getPending());
        assertFalse(results.get(2).getPending());
        assertFalse(results.get(3).getPending());
        assertFalse(results.get(4).getPending());
    }

}
