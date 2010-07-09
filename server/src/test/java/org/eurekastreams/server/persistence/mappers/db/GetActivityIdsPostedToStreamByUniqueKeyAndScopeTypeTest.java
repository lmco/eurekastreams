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

import java.util.List;

import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetActivityIdsPostedToDomainGroup.
 */
public class GetActivityIdsPostedToStreamByUniqueKeyAndScopeTypeTest extends MapperTest
{
    /**
     * System under test.
     */
    private GetActivityIdsPostedToStreamByUniqueKeyAndScopeType sut;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new GetActivityIdsPostedToStreamByUniqueKeyAndScopeType();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test execute for a group scope.
     */
    @Test
    public void testExecuteForGroupScope()
    {
        final Long expectedActivityId = 6793L;
        List<Long> activityIds = sut.execute(ScopeType.GROUP, "group1");
        assertEquals(1, activityIds.size());
        assertEquals(expectedActivityId, activityIds.get(0));
    }

    /**
     * Test execute for a person scope.
     */
    @Test
    public void testExecuteForPersonScope()
    {
        final Long expectedActivityId = 6792L;
        List<Long> activityIds = sut.execute(ScopeType.PERSON, "fordp");
        assertEquals(1, activityIds.size());
        assertEquals(expectedActivityId, activityIds.get(0));
    }
}
