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
        SublistWithResultCount<Long> results = sut.execute(null);
        Assert.assertEquals(new Long(2), results.getTotalResultsCount());
        Assert.assertEquals(new Long(2L), results.getResultsSublist().get(0));
        Assert.assertEquals(new Long(1L), results.getResultsSublist().get(1));
    }
}
