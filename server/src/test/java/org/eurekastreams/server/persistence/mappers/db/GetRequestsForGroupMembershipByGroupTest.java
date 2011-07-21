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

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.action.request.profile.GetRequestForGroupMembershipRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests mapper.
 */
public class GetRequestsForGroupMembershipByGroupTest extends MapperTest
{
    /**
     * System under test.
     */
    @Autowired
    private GetRequestsForGroupMembershipByGroup sut;

    /**
     * Test getting requests.
     */
    @Test
    public void testExecute()
    {
        final long groupId = 8L;
        final int startIndex = 0;
        final int endIndex = 9;
        final long personId = 4507L;

        GetRequestForGroupMembershipRequest request = new GetRequestForGroupMembershipRequest(groupId, null,
                startIndex, endIndex);

        // perform SUT
        PagedSet<Long> results = sut.execute(request);

        // verify
        assertEquals("Wrong From index", 0, results.getFromIndex());
        assertEquals("Wrong To index", 0, results.getToIndex());
        assertEquals("Wrong total", 1, results.getTotal());
        assertEquals(1, results.getPagedSet().size());
        assertEquals((Long) personId, results.getPagedSet().get(0));
    }

    /**
     * Test getting activities for an org that has flagged activities.
     */
    @Test
    public void testExecuteWithNoFlaggedActivities()
    {
        final long groupId = 7L;
        final int startIndex = 0;
        final int endIndex = 9;

        GetRequestForGroupMembershipRequest request = new GetRequestForGroupMembershipRequest(groupId, null,
                startIndex, endIndex);

        // perform SUT
        PagedSet<Long> results = sut.execute(request);

        // verify
        assertTrue(results.getPagedSet().isEmpty());
        assertEquals("Wrong From index", 0, results.getFromIndex());
        assertEquals("Wrong To index", -1, results.getToIndex());
        assertEquals("Wrong total", 0, results.getTotal());
    }
}
