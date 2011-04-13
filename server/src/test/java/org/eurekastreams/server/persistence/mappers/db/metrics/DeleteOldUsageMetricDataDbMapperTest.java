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
import java.util.List;

import org.eurekastreams.server.domain.UsageMetric;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.ibm.icu.util.Calendar;

/**
 * Test fixture for DeleteOldUsageMetricDataDbMapper.
 */
public class DeleteOldUsageMetricDataDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private DeleteOldUsageMetricDataDbMapper sut;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new DeleteOldUsageMetricDataDbMapper(3);
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        final int neg1 = -1;
        final int neg2 = -2;
        final int neg3 = -3;

        Calendar day = Calendar.getInstance();
        day.add(Calendar.DATE, neg2);
        Date twoDaysAgo = new Date(day.getTimeInMillis());

        day = Calendar.getInstance();
        day.add(Calendar.DATE, neg3);
        day.add(Calendar.MINUTE, neg1);
        Date threeDaysAgo = new Date(day.getTimeInMillis());

        // delete all existing usage metrics
        getEntityManager().createQuery("DELETE FROM UsageMetric").executeUpdate();

        // three usage metrics from two days ago
        getEntityManager().persist(new UsageMetric(1, true, true, twoDaysAgo));
        getEntityManager().persist(new UsageMetric(1, true, true, twoDaysAgo));
        getEntityManager().persist(new UsageMetric(1, true, true, twoDaysAgo));

        // 2 usage metrics from just over 3 days ago - these should be deleted
        getEntityManager().persist(new UsageMetric(2, true, true, threeDaysAgo));
        getEntityManager().persist(new UsageMetric(2, true, true, threeDaysAgo));

        getEntityManager().flush();
        getEntityManager().clear();

        sut.execute(0);

        List<UsageMetric> existingData = getEntityManager().createQuery("FROM UsageMetric").getResultList();
        Assert.assertEquals(3, existingData.size());

        assertEquals(1L, existingData.get(0).getActorPersonId());
        assertEquals(1L, existingData.get(1).getActorPersonId());
    }
}
