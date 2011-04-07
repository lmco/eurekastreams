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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.SharedResource;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the bulk activity stream DB mapper.
 */
public class BulkActivityStreamsDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private BulkActivityStreamsDbMapper sut = null;

    /**
     * Max items.
     */
    private static final int MAX_ITEMS = 10000;

    /**
     * Setup fixtures.
     */
    @Before
    public void before()
    {
        sut = new BulkActivityStreamsDbMapper();
        sut.setEntityManager(getEntityManager());
        sut.setMaxItems(MAX_ITEMS);
    }

    /**
     * Executes a test with a real collider. Verifies correct results.
     */
    @Test
    public void testWithResultsWithRealCollider()
    {

        final int expectedSize = 2;

        final List<Long> request = new ArrayList<Long>();
        request.add(1L);
        request.add(2L);

        List<List<Long>> results = sut.execute(request);

        Assert.assertEquals(expectedSize, results.size());
        Assert.assertEquals(1, results.get(0).size());
        Assert.assertEquals(1, results.get(1).size());
    }

    /**
     * Executes a test with no results.
     */
    @Test
    public void testWithoutResults()
    {
        final int expectedSize = 0;

        final List<Long> request = new ArrayList<Long>();
        request.add(0L);

        List<List<Long>> results = sut.execute(request);

        Assert.assertEquals(expectedSize, results.get(0).size());
    }

    /**
     * Test.
     */
    @Test
    public void testShowInStreamIgnoredIfAskingForRecipientStreamScopeDirectly()
    {
        // set activities showInStream flag to false.
        getEntityManager().createQuery("UPDATE Activity SET showInStream = :showInStreamFlag").setParameter(
                "showInStreamFlag", false).executeUpdate();

        final int expectedSize = 2;

        final List<Long> request = new ArrayList<Long>();
        request.add(1L);
        request.add(2L);

        List<List<Long>> results = sut.execute(request);

        Assert.assertEquals(expectedSize, results.size());
        Assert.assertEquals(1, results.get(0).size());
        Assert.assertEquals(1, results.get(1).size());
    }

    /**
     * Test getting activities from a stream that represents a shared resource that was shared in activities.
     */
    @Test
    public void testWithStreamIdSharedInActivity()
    {
        final long activityId = 6789L;
        final long activityId2 = 6790L;

        final StreamScope scope = new StreamScope(ScopeType.RESOURCE, "http://foo.foo.foo.com");
        getEntityManager().persist(scope);
        getEntityManager().flush();

        // an activity that shares the shared resource
        SharedResource sr = new SharedResource("http://foo.foo.foo.com");
        sr.setStreamScope(scope);
        getEntityManager().persist(sr);
        getEntityManager().flush();

        scope.setDestinationEntityId(sr.getId());
        getEntityManager().flush();

        Activity act = getEntityManager().find(Activity.class, activityId);
        act.setSharedLink(sr);

        getEntityManager().flush();

        // an activity that came from the shared resource
        act = getEntityManager().find(Activity.class, activityId2);
        act.setRecipientStreamScope(scope);

        getEntityManager().flush();

        final List<Long> request = new ArrayList<Long>();
        request.add(scope.getId());
        List<List<Long>> activityIds = sut.execute(request);

        assertEquals(1, activityIds.size());
        // assertEquals(2, activityIds.get(0).size());
        assertTrue(activityIds.get(0).contains(activityId));
        assertTrue(activityIds.get(0).contains(activityId2));
    }
}
