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

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetStreamTotalActivityCountDbMapper.
 */
public class GetStreamTotalActivityCountDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetStreamTotalActivityCountDbMapper sut;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new GetStreamTotalActivityCountDbMapper();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test execute when results are found.
     */
    @Test
    public void testExecuteWithResults()
    {
        final Long streamScopeId = 87433L;
        Assert.assertEquals(new Long(2L), sut.execute(streamScopeId));
    }

    /**
     * Test execute when results are not found.
     */
    @Test
    public void testExecuteWithNoResults()
    {
        final Long streamScopeId = 87433388L;
        Assert.assertEquals(new Long(0), sut.execute(streamScopeId));
    }
}
