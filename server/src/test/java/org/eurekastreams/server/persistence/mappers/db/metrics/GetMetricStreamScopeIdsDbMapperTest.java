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
import java.util.List;

import org.eurekastreams.server.domain.UsageMetric;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetMetricStreamScopeIdsDbMapper.
 */
public class GetMetricStreamScopeIdsDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetMetricStreamScopeIdsDbMapper sut;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new GetMetricStreamScopeIdsDbMapper();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        getEntityManager().persist(new UsageMetric(1L, true, true, 1L, new Date()));
        getEntityManager().persist(new UsageMetric(3L, true, false, null, new Date()));
        getEntityManager().persist(new UsageMetric(3L, true, true, 2L, new Date()));
        getEntityManager().persist(new UsageMetric(4L, true, true, 4L, new Date()));
        getEntityManager().persist(new UsageMetric(5L, true, true, 5L, new Date()));

        List<Long> ids = sut.execute(null);
        Assert.assertEquals(4, ids.size());
        Assert.assertTrue(ids.contains(1L));
        Assert.assertTrue(ids.contains(2L));
        Assert.assertTrue(ids.contains(4L));
        Assert.assertTrue(ids.contains(5L));
    }
}
