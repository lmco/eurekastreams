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

import static org.junit.Assert.assertEquals;

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for GetOrganizationLeaderIdsByOrgId class.
 */
public class GetOrganizationLeaderIdsByOrgIdTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetOrganizationLeaderIdsByOrgId sut;

    /**
     * Organization id with leaders from dataset.xml.
     */
    private final Long orgIdWithLeaders = 5L;

    /**
     * Organization id with no leaders from dataset.xml.
     */
    private final Long orgIdNoLeaders = 6L;

    /**
     * Test with leaders.
     */
    @Test
    public void testExecuteWithLeaders()
    {
        assertEquals(2, sut.execute(orgIdWithLeaders).size());
    }

    /**
     * Test with no leaders.
     */
    @Test
    public void testExecuteNoLeaders()
    {
        assertEquals(0, sut.execute(orgIdNoLeaders).size());
    }
}
