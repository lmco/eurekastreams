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
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;

/**
 * Tests GetSubscribedPersonIdsForStreamDbMapper.
 */
public class GetSubscribedPersonIdsForStreamDbMapperTest extends MapperTest
{
    /**
     * test.
     */
    @Test
    public void testExecuteGroup()
    {
        final long groupId = 1L;

        GetSubscribedPersonIdsForStreamDbMapper sut = new GetSubscribedPersonIdsForStreamDbMapper(EntityType.GROUP);
        sut.setEntityManager(getEntityManager());

        getEntityManager().createQuery("Update GroupFollower set receiveNewActivityNotifications = :boolean")
                .setParameter("boolean", true).executeUpdate();

        getEntityManager().flush();
        getEntityManager().clear();

        List<Long> results = sut.execute(groupId);
        assertEquals(3, results.size());

        getEntityManager().createQuery("Update GroupFollower set receiveNewActivityNotifications = :boolean")
                .setParameter("boolean", false).executeUpdate();

        getEntityManager().flush();
        getEntityManager().clear();

        results = sut.execute(groupId);
        assertEquals(0, results.size());
    }

}
