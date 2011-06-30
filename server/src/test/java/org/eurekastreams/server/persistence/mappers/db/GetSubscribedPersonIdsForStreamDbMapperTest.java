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

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.BaseDomainMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.testing.TestHelper;
import org.junit.Test;

/**
 * Tests GetSubscribedPersonIdsForStreamDbMapper.
 */
public class GetSubscribedPersonIdsForStreamDbMapperTest extends MapperTest
{
    /**
     * Test.
     */
    @Test
    public void testExecuteGroup()
    {
        final long groupId = 1L;

        DomainMapper<Long, List<Long>> sut = new GetSubscribedPersonIdsForStreamDbMapper(EntityType.GROUP);
        ((BaseDomainMapper) sut).setEntityManager(getEntityManager());

        List<Long> results = sut.execute(groupId);
        assertEquals(2, results.size());
        TestHelper.containsExactly(results, 98L, 42L);

        getEntityManager().createQuery("Update GroupFollower set receiveNewActivityNotifications = :boolean")
                .setParameter("boolean", false).executeUpdate();

        getEntityManager().flush();
        getEntityManager().clear();

        results = sut.execute(groupId);
        assertEquals(0, results.size());
    }

    /**
     * Test.
     */
    @Test
    public void testExecutePerson()
    {
        final long personId = 98L;

        DomainMapper<Long, List<Long>> sut = new GetSubscribedPersonIdsForStreamDbMapper(EntityType.PERSON);
        ((BaseDomainMapper) sut).setEntityManager(getEntityManager());

        List<Long> results = sut.execute(personId);
        assertEquals(1, results.size());
        assertEquals(142L, (long) results.get(0));
    }

    /**
     * Tests attempting to create for unsupported type.
     */
    @Test(expected = Exception.class)
    public void testConstructInvalidType()
    {
        new GetSubscribedPersonIdsForStreamDbMapper(EntityType.RESOURCE);
    }
}
