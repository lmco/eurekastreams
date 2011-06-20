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

import java.util.List;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetStreamsByDailyAverageViewsDbMapper.
 */
public class GetStreamsByDailyAverageViewsDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetStreamsByDailyAverageViewsDbMapper sut;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new GetStreamsByDailyAverageViewsDbMapper(10);
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test execute.
     */
    @Test
    public void test()
    {
        List<Long> results = sut.execute(10);
        Assert.assertEquals(2L, results.size());
        Assert.assertEquals(new Long(2), results.get(0));
        Assert.assertEquals(new Long(1), results.get(1));
    }
}
