/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.db.metrics;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.Comment;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.service.actions.requests.UsageMetricDailyStreamInfoRequest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetDailyMessageCountDbMapper.
 */
public class GetDailyMessageCountDbMapperTest extends MapperTest
{
    /**
     * April 4th, 2011 in ticks.
     */
    private final long april4th2011 = 1301944331000L;

    /**
     * April 5th, 2011 in ticks.
     */
    private final long april5th2011 = 1302030731000L;

    /**
     * System under test.
     */
    private GetDailyMessageCountDbMapper sut;

    /**
     * Setup - set 2 activities and 1 comment to april 4th, 2011.
     */
    @Before
    public void setup()
    {
        Activity act;
        Comment comment;
        Date april4th = new Date(april4th2011);

        sut = new GetDailyMessageCountDbMapper();
        sut.setEntityManager(getEntityManager());

        final long actId1 = 6789L;
        act = getEntityManager().find(Activity.class, actId1);
        act.setPostedTime(april4th);
        act.setAppType(EntityType.APPLICATION);
        getEntityManager().persist(act);

        final long actId2 = 6790L;
        act = getEntityManager().find(Activity.class, actId2);
        act.setPostedTime(april4th);
        act.setAppType(null);
        getEntityManager().persist(act);

        // an activity not by a person - this should be ignored
        final long actId3 = 6791L;
        act = getEntityManager().find(Activity.class, actId3);
        act.setAppType(EntityType.PLUGIN);
        act.setPostedTime(april4th);
        getEntityManager().persist(act);

        // an activity not by a person - this should be ignored
        final long actId4 = 6793L;
        act = getEntityManager().find(Activity.class, actId4);
        act.setAppType(EntityType.PLUGIN);
        act.setPostedTime(april4th);
        getEntityManager().persist(act);

        final long commentId = 9;
        comment = getEntityManager().find(Comment.class, commentId);
        comment.setTimeSent(april4th);
        getEntityManager().persist(comment);

        getEntityManager().flush();
    }

    /**
     * Test execute for all streams.
     */
    @Test
    public void testExecuteForAllStreams()
    {
        Date april4th = new Date(april4th2011 + 8); // change the date a little bit
        assertEquals(3, (long) sut.execute(new UsageMetricDailyStreamInfoRequest(april4th, null)));
    }

    /**
     * Test execute for a specific stream with data.
     */
    @Test
    public void testExecuteForSpecificStreamWithData()
    {
        final Long streamScopeId = 87433L;
        Date april4th = new Date(april4th2011 + 8); // change the date a little bit
        assertEquals(2, (long) sut.execute(new UsageMetricDailyStreamInfoRequest(april4th, streamScopeId)));
    }

    /**
     * Test execute for a specific stream with no data - no data for this stream scope.
     */
    @Test
    public void testExecuteForSpecificStreamWithNoData1()
    {
        final Long streamScopeId = 877L;
        Date april4th = new Date(april4th2011 + 8); // change the date a little bit
        assertEquals(0, (long) sut.execute(new UsageMetricDailyStreamInfoRequest(april4th, streamScopeId)));
    }

    /**
     * Test execute for a specific stream with no data - no data for this date.
     */
    @Test
    public void testExecuteForSpecificStreamWithNoData2()
    {
        final Long streamScopeId = 877L;
        Date april4th = new Date(april5th2011 + 8); // change the date a little bit
        assertEquals(0, (long) sut.execute(new UsageMetricDailyStreamInfoRequest(april4th, streamScopeId)));
    }
}
