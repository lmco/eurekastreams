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
package org.eurekastreams.server.persistence.mappers.db;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test fixture for DeleteAllTempWeekdaysSinceDateDbMapper.
 */
public class DeleteAllTempWeekdaysSinceDateDbMapperTest extends MapperTest
{
    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        Assert.assertEquals(3L, getEntityManager().createQuery("SELECT COUNT(*) from TempWeekdaysSinceDate")
                .getSingleResult());

        // execute SUT
        DeleteAllTempWeekdaysSinceDateDbMapper sut = new DeleteAllTempWeekdaysSinceDateDbMapper();
        sut.setEntityManager(getEntityManager());
        sut.execute();

        Assert.assertEquals(0L, getEntityManager().createQuery("SELECT COUNT(*) from TempWeekdaysSinceDate")
                .getSingleResult());
    }
}
