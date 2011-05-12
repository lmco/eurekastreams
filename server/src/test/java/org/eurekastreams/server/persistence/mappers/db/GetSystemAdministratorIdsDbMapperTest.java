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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;

/**
 * Test fixture for GetSystemAdministratorIdsDbMapper.
 */
public class GetSystemAdministratorIdsDbMapperTest extends MapperTest
{
    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        final long id1 = 42L;
        final long id2 = 142L;

        GetSystemAdministratorIdsDbMapper sut = new GetSystemAdministratorIdsDbMapper();
        sut.setEntityManager(getEntityManager());
        List<Long> admins = sut.execute(null);

        Assert.assertEquals(2, admins.size());
        Assert.assertTrue(admins.contains(id1));
        Assert.assertTrue(admins.contains(id2));
    }
}
