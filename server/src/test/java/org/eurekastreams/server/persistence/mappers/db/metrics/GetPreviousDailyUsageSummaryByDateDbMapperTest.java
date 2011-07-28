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

import org.eurekastreams.server.domain.DailyUsageSummary;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.service.actions.requests.UsageMetricDailyStreamInfoRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetPreviousDailyUsageSummaryByDateDbMapper.
 */
public class GetPreviousDailyUsageSummaryByDateDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetPreviousDailyUsageSummaryByDateDbMapper sut;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new GetPreviousDailyUsageSummaryByDateDbMapper();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test execute with data.
     */
    @Test
    public void testExecuteWithData()
    {
        final long streamScopeId = 2L;
        final long april8th2011 = 1302282402000L;

        DailyUsageSummary summary = sut.execute(new UsageMetricDailyStreamInfoRequest(new Date(april8th2011),
                streamScopeId));
        Assert.assertEquals(6L, summary.getId());
    }

    /**
     * Test execute with no prior data.
     */
    @Test
    public void testExecuteWithNoPriorData()
    {
        final long streamScopeId = 2L;
        final long april6th2001 = 1302109602000L;

        DailyUsageSummary summary = sut.execute(new UsageMetricDailyStreamInfoRequest(new Date(april6th2001),
                streamScopeId));
        Assert.assertNull(summary);
    }

    /**
     * Test execute with no prior data.
     */
    @Test
    public void testExecuteWithNoStreamData()
    {
        final long streamScopeId = 222L;
        final long april7th2011 = 1302196002000L;

        DailyUsageSummary summary = sut.execute(new UsageMetricDailyStreamInfoRequest(new Date(april7th2011),
                streamScopeId));
        Assert.assertNull(summary);
    }
}
