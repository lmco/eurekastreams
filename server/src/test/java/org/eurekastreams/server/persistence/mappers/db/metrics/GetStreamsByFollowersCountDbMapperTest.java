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

import junit.framework.Assert;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.dto.StreamDTO;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetStreamsByFollowersCountDbMapper.
 */
public class GetStreamsByFollowersCountDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetStreamsByFollowersCountDbMapper sut;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new GetStreamsByFollowersCountDbMapper(10);
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test execute().
     */
    @Test
    public void testExecute()
    {
        // GROUP 3 - 10
        // GROUP 1 - 3
        // GROUP 4 - 1 < CAN BE SWAPPED
        // GROUP 2 - 1 < CAN BE SWAPPED
        // PERSON 99 - 1

        List<StreamDTO> results = sut.execute(null);
        Assert.assertEquals(5, results.size());
        Assert.assertEquals(EntityType.GROUP, results.get(0).getEntityType());
        Assert.assertEquals(10, results.get(0).getFollowersCount());
        Assert.assertEquals(3, results.get(0).getId());

        Assert.assertEquals(EntityType.GROUP, results.get(1).getEntityType());
        Assert.assertEquals(3, results.get(1).getFollowersCount());
        Assert.assertEquals(1, results.get(1).getId());

        // item at index 2 and 3 are either group 4 or group 2 - they both have 1 follower
        Assert.assertEquals(EntityType.GROUP, results.get(2).getEntityType());
        Assert.assertEquals(1, results.get(2).getFollowersCount());
        if (results.get(2).getId() == 4)
        {
            Assert.assertEquals(4, results.get(2).getId());
            Assert.assertEquals(2, results.get(3).getId());
        }
        else
        {
            Assert.assertEquals(2, results.get(2).getId());
            Assert.assertEquals(4, results.get(3).getId());
        }
        Assert.assertEquals(EntityType.GROUP, results.get(3).getEntityType());
        Assert.assertEquals(1, results.get(2).getFollowersCount());

        Assert.assertEquals(EntityType.PERSON, results.get(4).getEntityType());
        Assert.assertEquals(1, results.get(4).getFollowersCount());
    }
}
