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
package org.eurekastreams.server.action.execution.stream;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.DeleteFromSearchIndexRequest;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.persistence.mappers.DeleteFromSearchIndex;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.RemoveExpiredActivities;
import org.eurekastreams.server.persistence.mappers.db.GetExpiredActivities;
import org.eurekastreams.server.persistence.mappers.db.GetListsContainingActivities;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for DeleteExpiredActivitiesExecution class.
 *
 */
public class DeleteExpiredActivitiesExecutionTest
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
     * ActionContext mock.
     */
    @SuppressWarnings("unchecked")
    private TaskHandlerActionContext actionContext = context.mock(TaskHandlerActionContext.class);

    /**
     * {@link FindSystemSettings} mock.
     */
    private DomainMapper<MapperRequest, SystemSettings> settingsMapper = context.mock(DomainMapper.class);

    /**
     * {@link GetExpiredActivities} mock.
     */
    private GetExpiredActivities expiredActivitiesMapper = context.mock(GetExpiredActivities.class);

    /**
     * {@link GetListsContainingActivities} mock.
     */
    private GetListsContainingActivities listsMapper = context.mock(GetListsContainingActivities.class);

    /**
     * {@link RemoveExpiredActivities} mock.
     */
    private RemoveExpiredActivities deleteMapper = context.mock(RemoveExpiredActivities.class);

    /**
     * {@link DeleteFromSearchIndex} mock.
     */
    private DeleteFromSearchIndex indexDeleteMapper = context.mock(DeleteFromSearchIndex.class);

    /**
     * Test chunk size.
     */
    private int chunkSize = 2;

    /**
     * {@link Activity} mock.
     */
    private Activity activity = context.mock(Activity.class);

    /**
     * The system under test.
     */
    private DeleteExpiredActivitiesExecution sut;

    /**
     * Setup sut.
     */
    @Before
    public void setUp()
    {
        sut = new DeleteExpiredActivitiesExecution(settingsMapper, expiredActivitiesMapper, chunkSize);
    }

    /**
     * Test execute method.
     *
     * @throws Exception
     *             not expected.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testPerformAction() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                final int expireDays = 20;
                SystemSettings settings = new SystemSettings();
                settings.setContentExpiration(expireDays);
                allowing(settingsMapper).execute(null);
                will(returnValue(settings));

                List<Long> activities = new ArrayList<Long>();
                activities.add(1L);
                activities.add(2L);
                activities.add(3L);

                allowing(expiredActivitiesMapper).execute(with(any(Date.class)));
                will(returnValue(activities));

                // first chunk
                allowing(listsMapper).execute(with(any(List.class)));
                will(returnValue(Arrays.asList("cacheKey1", "cacheKey2")));

                allowing(deleteMapper).execute(with(any(List.class)));

                List<UserActionRequest> requests = new ArrayList<UserActionRequest>();
                oneOf(actionContext).getUserActionRequests();
                will(returnValue(requests));

                allowing(indexDeleteMapper).execute(with(any(DeleteFromSearchIndexRequest.class)));

                // second chunk
                allowing(listsMapper).execute(with(any(List.class)));
                will(returnValue(Arrays.asList("cacheKey3")));

                allowing(deleteMapper).execute(with(any(List.class)));

                requests = new ArrayList<UserActionRequest>();
                oneOf(actionContext).getUserActionRequests();
                will(returnValue(requests));

                allowing(indexDeleteMapper).execute(with(any(DeleteFromSearchIndexRequest.class)));
            }
        });

        Boolean result = sut.execute(actionContext);
        context.assertIsSatisfied();
        assertTrue(result);
    }

    /**
     * Test execute method with no work to do.
     *
     * @throws Exception
     *             not expected.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testPerformActionNoWork() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                final int expireDays = 0;
                SystemSettings settings = new SystemSettings();
                settings.setContentExpiration(expireDays);
                allowing(settingsMapper).execute(null);
                will(returnValue(settings));
            }
        });

        Boolean result = sut.execute(actionContext);
        context.assertIsSatisfied();
        assertTrue(result);
    }

}
