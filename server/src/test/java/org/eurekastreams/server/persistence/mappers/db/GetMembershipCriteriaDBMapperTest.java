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

import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetMembershipCriteriaDBMapper.
 * 
 */
public class GetMembershipCriteriaDBMapperTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetMembershipCriteriaDBMapper sut = new GetMembershipCriteriaDBMapper();

    /**
     * Setup.
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
        final long lowestId = 11L;
        List<MembershipCriteria> results = sut.execute(null);
        assertEquals(2, results.size());
        assertEquals(lowestId, results.get(0).getId());
    }
}
