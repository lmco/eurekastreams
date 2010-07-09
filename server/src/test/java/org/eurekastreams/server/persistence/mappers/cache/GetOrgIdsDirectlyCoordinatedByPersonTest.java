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
package org.eurekastreams.server.persistence.mappers.cache;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test fixture for GetOrgIdsDirectlyCoordinatedByPerson.
 */
public class GetOrgIdsDirectlyCoordinatedByPersonTest extends MapperTest
{
    /**
     * The system under test.
     */
    @Autowired
    private GetOrgIdsDirectlyCoordinatedByPerson sut;

    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        final Long userId = 142L;
        final Set<Long> expected = new HashSet<Long>();
        expected.add(6L);
        expected.add(7L);
        assertEquals(expected, sut.execute(userId));
    }
}
