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

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for SetSystemAdminsDbMapper.
 */
public class SetSystemAdminsDbMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private SetSystemAdminsDbMapper sut;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        sut = new SetSystemAdminsDbMapper();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test execute.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testExecute()
    {
        final Long ford1 = 42L;
        final Long ford2 = 142L;
        final Long sagan = 4507L;

        List<String> adminAccountIds = new ArrayList<String>();
        adminAccountIds.add("fordp");
        adminAccountIds.add("fordp2");
        adminAccountIds.add("csagan");

        sut.execute(adminAccountIds);

        List<Long> peopleIds = getEntityManager().createQuery("SELECT id FROM Person WHERE isAdministrator=true")
                .getResultList();
        Assert.assertEquals(3, peopleIds.size());
        Assert.assertTrue(peopleIds.contains(ford1));
        Assert.assertTrue(peopleIds.contains(ford2));
        Assert.assertTrue(peopleIds.contains(sagan));
    }
}
