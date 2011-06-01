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

import java.util.List;

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
        final Long ssid1 = 87433L;
        final Long ssid2 = 1L;
        final Long ssid3 = 2L;
        final Long ssid4 = 3L;
        final Long ssid5 = 4L;
        final Long ssid6 = 874L;
        final Long ssid7 = 875L;

        final Long badSsid1 = 50000L;
        final Long badSsid2 = 50001L;
        final Long badSsid3 = 50002L;

        List<Long> streamScopeIds = sut.execute(null);

        Assert.assertTrue(streamScopeIds.size() > 0);
        Assert.assertTrue(streamScopeIds.contains(ssid1));
        Assert.assertTrue(streamScopeIds.contains(ssid2));
        Assert.assertTrue(streamScopeIds.contains(ssid3));
        Assert.assertTrue(streamScopeIds.contains(ssid4));
        Assert.assertTrue(streamScopeIds.contains(ssid5));
        Assert.assertTrue(streamScopeIds.contains(ssid6));
        Assert.assertTrue(streamScopeIds.contains(ssid7));

        Assert.assertFalse(streamScopeIds.contains(badSsid1));
        Assert.assertFalse(streamScopeIds.contains(badSsid2));
        Assert.assertFalse(streamScopeIds.contains(badSsid3));
    }
}
