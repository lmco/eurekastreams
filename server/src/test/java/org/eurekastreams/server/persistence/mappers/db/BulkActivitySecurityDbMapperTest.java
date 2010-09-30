/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.server.domain.stream.ActivitySecurityDTO;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.chained.PartialMapperResponse;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the bulk activity security DB mapper.
 */
public class BulkActivitySecurityDbMapperTest extends MapperTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private BulkActivitySecurityDbMapper sut = null;

    /**
     * Setup test fixtures.
     */
    @Before
    public void before()
    {
        sut = new BulkActivitySecurityDbMapper();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        final Long activityId = 6789L;
        final Long destinationEntityId = 98L;

        final List<Long> activites = new ArrayList<Long>();
        activites.add(activityId);

        final PartialMapperResponse<List<Long>, List<ActivitySecurityDTO>> results = sut.execute(activites);

        Assert.assertEquals(1, results.getResponse().size());
        Assert.assertEquals(activityId, results.getResponse().get(0).getId());
        Assert.assertEquals(destinationEntityId, results.getResponse().get(0).getDestinationEntityId());
        Assert.assertTrue(results.getResponse().get(0).isDestinationStreamPublic());

        context.assertIsSatisfied();
    }

}
