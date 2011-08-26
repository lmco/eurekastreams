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
import static org.junit.Assert.assertNull;

import org.eurekastreams.server.action.request.UpdateStickyActivityRequest;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Before;
import org.junit.Test;


/**
 * Tests UpdateGroupStickyActivityDbMapper.
 */
public class UpdateGroupStickyActivityDbMapperTest extends MapperTest
{
    /** SUT. */
    private UpdateGroupStickyActivityDbMapper sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new UpdateGroupStickyActivityDbMapper();
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test.
     */
    @Test
    public void testSetValue()
    {
        Long activityId = (Long) getEntityManager().createQuery(
                "SELECT stickyActivityId FROM DomainGroup WHERE id = 1").getSingleResult();
        assertNull(activityId);

        sut.execute(new UpdateStickyActivityRequest(1L, 6789L));

        getEntityManager().clear();

        activityId = (Long) getEntityManager().createQuery("SELECT stickyActivityId FROM DomainGroup WHERE id = 1")
                .getSingleResult();
        assertEquals((Long) 6789L, activityId);
    }

    /**
     * Test.
     */
    @Test
    public void testSetNull()
    {
        getEntityManager().createQuery("UPDATE DomainGroup SET stickyActivityId = 6793 WHERE id = 2").executeUpdate();
        Long activityId = (Long) getEntityManager().createQuery(
                "SELECT stickyActivityId FROM DomainGroup WHERE id = 2").getSingleResult();
        assertEquals((Long) 6793L, activityId);

        getEntityManager().clear();

        sut.execute(new UpdateStickyActivityRequest(2L, null));

        getEntityManager().clear();

        activityId = (Long) getEntityManager().createQuery("SELECT stickyActivityId FROM DomainGroup WHERE id = 2")
                .getSingleResult();
        assertNull(activityId);
    }
}
