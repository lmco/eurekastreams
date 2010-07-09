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
package org.eurekastreams.server.action.execution.opensocial;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.action.request.opensocial.GetUserActivitiesRequest;
import org.eurekastreams.server.domain.stream.StreamFilter;
import org.eurekastreams.server.persistence.mappers.stream.BulkActivitiesMapper;
import org.eurekastreams.server.persistence.mappers.stream.CompositeStreamActivityIdsMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByOpenSocialIds;
import org.eurekastreams.server.persistence.mappers.stream.GetStreamByOwnerId;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link GetUserActivitiesExecution} class.
 *
 */
public class GetUserActivitiesExecutionTest
{
    /**
     * System under test.
     */
    private GetUserActivitiesExecution sut;

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
     * Mocked instance of the {@link GetStreamByOwnerId}.
     */
    private final GetStreamByOwnerId getStreamByOwnerIdMock = context.mock(GetStreamByOwnerId.class);

    /**
     * Mocked instance of the {@link BulkActivitiesMapper}.
     */
    private final BulkActivitiesMapper bulkActivitiesMapperMock = context.mock(BulkActivitiesMapper.class);

    /**
     * Mocked instance of the {@link CompositeStreamActivityIdsMapper}.
     */
    private final CompositeStreamActivityIdsMapper compositeStreamActivityIdsMapper = context
            .mock(CompositeStreamActivityIdsMapper.class);

    /**
     * Mocked instance of the {@link GetPeopleByOpenSocialIds} mapper.
     */
    private final GetPeopleByOpenSocialIds getPeopleByOpenSocialIdsMapper = context
            .mock(GetPeopleByOpenSocialIds.class);

    /**
     * Mocked instance of {@link StreamFilter}.
     */
    private final StreamFilter streamFilterMock = context.mock(StreamFilter.class);

    /**
     * Mocked principal object for test.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * Prepare the test suite.
     */
    @Before
    public void setup()
    {
        sut = new GetUserActivitiesExecution(getStreamByOwnerIdMock, bulkActivitiesMapperMock,
                compositeStreamActivityIdsMapper, getPeopleByOpenSocialIdsMapper);
    }

    /**
     * Test the successful execution with activity ids.
     */
    @Test
    public void testExecute()
    {
        final String userId = "testuserid";

        final LinkedList<Long> activityIds = new LinkedList<Long>();
        activityIds.add(1L);
        activityIds.add(2L);

        final List<String> userIds = new ArrayList<String>();
        userIds.add(userId);

        context.checking(new Expectations()
        {
            {
                allowing(getPeopleByOpenSocialIdsMapper).execute(userIds);

                allowing(bulkActivitiesMapperMock).execute(activityIds, userId);
            }
        });

        GetUserActivitiesRequest currentRequest = new GetUserActivitiesRequest(activityIds,
                new HashSet<String>(userIds));
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principal);

        sut.execute(currentContext);
    }

    /**
     * Test the successful execution with activity ids.
     */
    @Test
    public void testExecuteWithNoActivityIds()
    {
        final Long longUserId = 1L;

        final Long compStreamId = 2L;

        final String userId = "testuser";

        final List<String> userIds = new ArrayList<String>();
        userIds.add(userId);

        final LinkedList<Long> activityIds = new LinkedList<Long>();

        context.checking(new Expectations()
        {
            {
                allowing(getPeopleByOpenSocialIdsMapper).execute(userIds);

                allowing(getStreamByOwnerIdMock).execute(longUserId);
                will(returnValue(streamFilterMock));

                allowing(streamFilterMock).getId();
                will(returnValue(compStreamId));

                allowing(compositeStreamActivityIdsMapper).execute(compStreamId, longUserId);

                allowing(bulkActivitiesMapperMock).execute(activityIds, userId);
            }
        });

        GetUserActivitiesRequest currentRequest = new GetUserActivitiesRequest(activityIds,
                new HashSet<String>(userIds));
        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principal);

        sut.execute(currentContext);
    }
}
