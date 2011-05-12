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

import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.requests.GetRelatedEntityCountRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test for GetRelatedEntityCount mapper.
 * 
 */
public class GetRelatedEntityCountTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetRelatedEntityCount sut;

    /**
     * Test.
     */
    @Test
    public void test()
    {
        // verify with some values from dataset.xml

        // Get count of DomainGroups with parent org of 5
        assertEquals(8, sut.execute(new GetRelatedEntityCountRequest("DomainGroup", "parentOrganization", 5L))
                .longValue());

        // Get count of activities with recipient parent org of 5
        assertEquals(5, //
                sut.execute(new GetRelatedEntityCountRequest("Activity", "recipientParentOrg", 5L)).longValue());

        assertEquals(4, sut.execute(
                new GetRelatedEntityCountRequest("Activity", "recipientParentOrg", 5L,
                        "AND recipientStreamScope.scopeType = 'PERSON'")).longValue());

        assertEquals(1, sut.execute(
                new GetRelatedEntityCountRequest("Activity", "recipientParentOrg", 5L,
                        "AND recipientStreamScope.scopeType = 'GROUP'")).longValue());
    }
}
