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

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests GetEveryoneActivityDbMapper.
 */
public class GetEveryoneActivityDbMapperTest extends MapperTest
{
    /** Expected results. */
    private static final long[] EXPECTED_RESULTS = { 6793L, 6792L, 6791L, 6790L, 6789L };

    /** SUT. */
    @Autowired
    private GetEveryoneActivityDbMapper sut;

    /**
     * Tests executing.
     */
    @Test
    public void testExecute()
    {
        List<Long> results = sut.execute(null);

        assertEquals(5, results.size());
        for (int i = 0; i < EXPECTED_RESULTS.length; i++)
        {
            assertEquals((Long) EXPECTED_RESULTS[i], results.get(i));
        }
    }

    /**
     * Tests executing.
     */
    @Test
    public void testExecuteObeyShowInStreamFalse()
    {
        // set activities showInStream flag to false.
        getEntityManager().createQuery("UPDATE Activity SET showInStream = :showInStreamFlag").setParameter(
                "showInStreamFlag", false).executeUpdate();

        List<Long> results = sut.execute(null);

        assertEquals(0, results.size());
    }
}
