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
package org.eurekastreams.server.persistence.mappers.stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.action.request.stream.GetFlaggedActivitiesByOrgRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetFlaggedActivitiesForOrganization.
 */
public class GetFlaggedActivitiesForOrganizationTest extends MapperTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Bulk activities mapper.
     */
    private BulkActivitiesMapper bulkActivitiesMapper = context.mock(BulkActivitiesMapper.class);

    /**
     * System under test.
     */
    private GetFlaggedActivitiesForOrganization sut;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new GetFlaggedActivitiesForOrganization(bulkActivitiesMapper);
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test getting activities for an org that has flagged activities.
     */
    @Test
    public void testExecute()
    {
        final long orgId = 7L;
        final int startIndex = 0;
        final int endIndex = 9;
        final long flaggedActivityId = 6789L;
        final String userName = "sdkjfsdlkfjs";

        final List<Long> expectedFlaggedActivities = new ArrayList<Long>();
        expectedFlaggedActivities.add(flaggedActivityId);

        final List<ActivityDTO> activityDTOs = new ArrayList<ActivityDTO>();

        context.checking(new Expectations()
        {
            {
                oneOf(bulkActivitiesMapper).execute(expectedFlaggedActivities, userName);
                will(returnValue(activityDTOs));
            }
        });

        GetFlaggedActivitiesByOrgRequest request = new GetFlaggedActivitiesByOrgRequest(orgId, startIndex, endIndex);
        request.setRequestingUserAccountId(userName);

        // perform SUT
        PagedSet<ActivityDTO> flaggedActivities = sut.execute(request);

        // verify
        assertSame(activityDTOs, flaggedActivities.getPagedSet());
        assertEquals(startIndex, flaggedActivities.getFromIndex());
        assertEquals(endIndex, flaggedActivities.getToIndex());
        assertEquals(1, flaggedActivities.getTotal());
    }

    /**
     * Test getting activities for an org that has flagged activities.
     */
    @Test
    public void testExecuteWithNoFlaggedActivities()
    {
        final long orgId = 5L;
        final int startIndex = 0;
        final int endIndex = 9;
        final String userName = "sdkjfsdlkfjs";

        GetFlaggedActivitiesByOrgRequest request = new GetFlaggedActivitiesByOrgRequest(orgId, startIndex, endIndex);
        request.setRequestingUserAccountId(userName);

        // perform SUT
        PagedSet<ActivityDTO> flaggedActivities = sut.execute(request);

        // verify
        assertEquals(0, flaggedActivities.getPagedSet().size());
        assertEquals(startIndex, flaggedActivities.getFromIndex());
        assertEquals(endIndex, flaggedActivities.getToIndex());
        assertEquals(0, flaggedActivities.getTotal());
    }

    /**
     * Test getting activities without a user account.
     */
    @Test(expected = RuntimeException.class)
    public void testExecuteWithoutUserAccountId()
    {
        final long orgId = 7L;
        PagedSet<ActivityDTO> flaggedActivities = sut.execute(new GetFlaggedActivitiesByOrgRequest(orgId, 0, 9));
        assertEquals(1, flaggedActivities.getPagedSet().size());
    }
}
