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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetPeopleIdsUsingThemeByThemeId.
 * 
 */
public class GetPeopleIdsUsingThemeByThemeIdTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetPeopleIdsUsingThemeByThemeId sut = new GetPeopleIdsUsingThemeByThemeId();

    /**
     * Theme id from dataset.xml.
     */
    private final Long themeId = 102L;

    /**
     * Checkstyle magic number hack.
     */
    private final Long uid1 = 99L;

    /**
     * Checkstyle magic number hack.
     */
    private final Long uid2 = 98L;

    /**
     * Checkstyle magic number hack.
     */
    private final Long uid3 = 142L;

    /**
     * Checkstyle magic number hack.
     */
    private final Long uid4 = 4507L;

    /**
     * Pre-test setup.
     */
    @Before
    public void setup()
    {
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test.
     */
    @Test
    public void test()
    {
        List<Long> expectedResults = new ArrayList<Long>(Arrays.asList(uid1, uid2, uid3, uid4));

        List<Long> results = sut.execute(themeId);

        assertEquals(expectedResults.size(), results.size());
        assertTrue(results.containsAll(expectedResults));
    }
}
