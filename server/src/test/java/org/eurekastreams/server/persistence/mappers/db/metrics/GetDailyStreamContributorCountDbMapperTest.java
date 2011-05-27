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

import java.util.Date;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.service.actions.requests.UsageMetricDailyStreamInfoRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetDailyStreamContributorCountDbMapper.
 */
public class GetDailyStreamContributorCountDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetDailyStreamContributorCountDbMapper sut;

    /**
     * April 8th, 2011 in ticks.
     */
    private final long apri8th2011 = 1301944331000L;

    /**
     * April 7th, 2011 in ticks.
     */
    private final long april7th2011 = 1302220680000L;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new GetDailyStreamContributorCountDbMapper();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test execute().
     */
    @Test
    public void testExecute()
    {
        // dataset.xml has 3 people that have commented, 3 people that have posted activities. overlap is 2, total
        // unique: 4
        getEntityManager().createQuery("UPDATE Activity set postedTime=:date").setParameter("date",
                new Date(apri8th2011)).executeUpdate();
        getEntityManager().createQuery("UPDATE Comment set timeSent=:date").setParameter("date", new Date(apri8th2011))
                .executeUpdate();

        Assert.assertEquals(4L, (long) sut.execute(new UsageMetricDailyStreamInfoRequest(new Date(apri8th2011), null)));

        // push all activities out of the date range
        getEntityManager().createQuery("UPDATE Activity set postedTime=:date").setParameter("date",
                new Date(april7th2011)).executeUpdate();
        Assert.assertEquals(3L, (long) sut.execute(new UsageMetricDailyStreamInfoRequest(new Date(apri8th2011), null)));

        // push all comments out of the date range
        getEntityManager().createQuery("UPDATE Comment set timeSent=:date")
                .setParameter("date", new Date(april7th2011)).executeUpdate();
        Assert.assertEquals(0L, (long) sut.execute(new UsageMetricDailyStreamInfoRequest(new Date(apri8th2011), null)));

        // now pull back just the comments
        getEntityManager().createQuery("UPDATE Comment set timeSent=:date").setParameter("date", new Date(apri8th2011))
                .executeUpdate();
        Assert.assertEquals(3L, (long) sut.execute(new UsageMetricDailyStreamInfoRequest(new Date(apri8th2011), null)));
    }
}
