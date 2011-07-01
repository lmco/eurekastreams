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
package org.eurekastreams.server.persistence.mappers.db.metrics;

import org.eurekastreams.server.domain.dto.StreamDTO;
import org.eurekastreams.server.domain.dto.SublistWithResultCount;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetStreamsByDailyAverageMessageCountDbMapper.
 */
public class GetStreamsByDailyAverageMessageCountDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetStreamsByDailyAverageMessageCountDbMapper sut;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new GetStreamsByDailyAverageMessageCountDbMapper(100);
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        SublistWithResultCount<StreamDTO> results = sut.execute(null);
        Assert.assertEquals(new Long(2), results.getTotalResultsCount());
        Assert.assertEquals(2, results.getResultsSublist().size());

        StreamDTO fordp2 = results.getResultsSublist().get(0);
        StreamDTO fordp = results.getResultsSublist().get(1);

        Assert.assertEquals("fordp", fordp2.getUniqueId());
        Assert.assertEquals("fordp2", fordp.getUniqueId());

        Assert.assertTrue(fordp2.getFollowersCount() >= 0);
        Assert.assertTrue(fordp.getFollowersCount() >= 0);
    }

    /**
     * Test execute with no data.
     */
    @Test
    public void testExecuteWhenNoData()
    {
        getEntityManager().createQuery("DELETE FROM DailyUsageSummary").executeUpdate();
        getEntityManager().clear();

        SublistWithResultCount<StreamDTO> results = sut.execute(null);
        Assert.assertEquals(new Long(0L), results.getTotalResultsCount());
        Assert.assertEquals(0, results.getResultsSublist().size());
    }
}
