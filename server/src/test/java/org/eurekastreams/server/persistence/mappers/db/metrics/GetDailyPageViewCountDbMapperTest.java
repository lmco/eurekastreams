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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetDailyPageViewCountDbMapper.
 */
public class GetDailyPageViewCountDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetDailyPageViewCountDbMapper sut;

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
        sut = new GetDailyPageViewCountDbMapper();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test execute().
     */
    @Test
    public void testExecute()
    {
        // right day
        getEntityManager().persist(new UsageMetric(1L, true, true, new Date(apri8th2011)));
        getEntityManager().persist(new UsageMetric(3L, true, false, new Date(apri8th2011 + 1)));
        getEntityManager().persist(new UsageMetric(6L, true, false, new Date(apri8th2011)));
        getEntityManager().persist(new UsageMetric(2L, true, true, new Date(apri8th2011 + 5)));
        getEntityManager().persist(new UsageMetric(4L, true, true, new Date(apri8th2011)));
        getEntityManager().persist(new UsageMetric(5L, true, true, new Date(apri8th2011)));

        // wrong day
        getEntityManager().persist(new UsageMetric(6L, true, false, new Date(april7th2011)));
        getEntityManager().persist(new UsageMetric(5L, true, false, new Date(april7th2011)));
        getEntityManager().persist(new UsageMetric(4L, true, false, new Date(april7th2011)));
        getEntityManager().persist(new UsageMetric(3L, true, false, new Date(april7th2011)));

        getEntityManager().flush();
        getEntityManager().clear();

        Assert.assertEquals(6L, (long) sut.execute(new Date(apri8th2011)));
    }
}
