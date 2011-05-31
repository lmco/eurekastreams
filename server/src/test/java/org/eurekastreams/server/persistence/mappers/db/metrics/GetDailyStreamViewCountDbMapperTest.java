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

import org.eurekastreams.server.domain.UsageMetric;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.service.actions.requests.UsageMetricDailyStreamInfoRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetDailyStreamViewCountDbMapper.
 */
public class GetDailyStreamViewCountDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetDailyStreamViewCountDbMapper sut;

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
        sut = new GetDailyStreamViewCountDbMapper();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test execute() for all streams.
     */
    @Test
    public void testExecuteForAllStreams()
    {
        // right day - 4
        getEntityManager().persist(new UsageMetric(1L, true, true, 1L, new Date(apri8th2011)));
        getEntityManager().persist(new UsageMetric(3L, true, false, 2L, new Date(apri8th2011 + 3)));
        getEntityManager().persist(new UsageMetric(6L, true, false, null, new Date(apri8th2011)));
        getEntityManager().persist(new UsageMetric(2L, true, true, 3L, new Date(apri8th2011 + 5)));
        getEntityManager().persist(new UsageMetric(4L, true, true, 4L, new Date(apri8th2011)));
        getEntityManager().persist(new UsageMetric(5L, true, true, 5L, new Date(apri8th2011)));

        // wrong day
        getEntityManager().persist(new UsageMetric(6L, true, true, 1L, new Date(april7th2011)));
        getEntityManager().persist(new UsageMetric(5L, true, true, 2L, new Date(april7th2011)));
        getEntityManager().persist(new UsageMetric(4L, true, true, 3L, new Date(april7th2011)));
        getEntityManager().persist(new UsageMetric(3L, true, false, null, new Date(april7th2011)));

        getEntityManager().flush();
        getEntityManager().clear();

        Assert.assertEquals(4L, (long) sut.execute(new UsageMetricDailyStreamInfoRequest(new Date(apri8th2011), null)));
    }

    /**
     * Test execute() for a specific stream.
     */
    @Test
    public void testExecuteForSpecificStream()
    {
        final Long targetScopeId = 382L;
        final Long otherScopeId = 828L;

        // right day - 4
        getEntityManager().persist(new UsageMetric(1L, true, true, targetScopeId, new Date(apri8th2011))); // yes
        getEntityManager().persist(new UsageMetric(3L, true, false, null, new Date(apri8th2011 + 3)));
        getEntityManager().persist(new UsageMetric(6L, true, false, null, new Date(apri8th2011)));
        getEntityManager().persist(new UsageMetric(2L, true, true, targetScopeId, new Date(apri8th2011 + 5))); // yes
        getEntityManager().persist(new UsageMetric(4L, true, true, targetScopeId, new Date(apri8th2011))); // yes
        getEntityManager().persist(new UsageMetric(5L, true, true, otherScopeId, new Date(apri8th2011)));

        // wrong day
        getEntityManager().persist(new UsageMetric(6L, true, true, targetScopeId, new Date(april7th2011)));
        getEntityManager().persist(new UsageMetric(5L, true, true, targetScopeId, new Date(april7th2011)));
        getEntityManager().persist(new UsageMetric(4L, true, true, otherScopeId, new Date(april7th2011)));
        getEntityManager().persist(new UsageMetric(3L, true, false, null, new Date(april7th2011)));

        getEntityManager().flush();
        getEntityManager().clear();

        Assert.assertEquals(3L, (long) sut.execute(new UsageMetricDailyStreamInfoRequest(new Date(apri8th2011),
                targetScopeId)));
    }
}
