/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.stream.GetFlaggedActivitiesRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetFlaggedActivities;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.activity.ActivityFilter;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests GetFlaggedActivitiesExecution.
 */
public class GetFlaggedActivitiesExecutionTest
{

    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: mapper. */
    private GetFlaggedActivities mapper = context.mock(GetFlaggedActivities.class);

    /** Fixture: actionCtx. */
    private PrincipalActionContext actionCtx = context.mock(PrincipalActionContext.class);

    /**
     * Principal mock.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * ActivityFilter.
     */
    private ActivityFilter activityDeletabilityFilter = context.mock(ActivityFilter.class);

    /**
     * Mapper to get a person model view by account id.
     */
    private DomainMapper<String, PersonModelView> getPersonModelViewByAccountIdMapper = context.mock(
            DomainMapper.class, "getPersonModelViewByAccountIdMapper");

    /**
     * {@link PersonModelView}.
     */
    private PersonModelView pmv = context.mock(PersonModelView.class);

    /** SUT. */
    private GetFlaggedActivitiesExecution sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new GetFlaggedActivitiesExecution(mapper, getPersonModelViewByAccountIdMapper, //
                activityDeletabilityFilter);
    }

    /**
     * Tests execute.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testExecute()
    {
        final String accountId = "sldfjkds";
        final GetFlaggedActivitiesRequest rqst = new GetFlaggedActivitiesRequest(3, 5);
        final PagedSet<ActivityDTO> pagedSet = context.mock(PagedSet.class);
        final List<ActivityDTO> activities = new ArrayList<ActivityDTO>();

        context.checking(new Expectations()
        {
            {
                // mapper should get a request with the same values as the one passed to the action.
                oneOf(mapper).execute(with(equalInternally(rqst)));
                will(returnValue(pagedSet));

                allowing(actionCtx).getParams();
                will(returnValue(rqst));

                allowing(actionCtx).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getAccountId();
                will(returnValue(accountId));

                oneOf(getPersonModelViewByAccountIdMapper).execute(accountId);
                will(returnValue(pmv));

                allowing(pagedSet).getPagedSet();
                will(returnValue(activities));

                oneOf(activityDeletabilityFilter).filter(activities, pmv);
            }
        });

        Serializable result = sut.execute(actionCtx);
        context.assertIsSatisfied();
        assertSame(pagedSet, result);

        assertEquals(accountId, rqst.getRequestingUserAccountId());
    }
}
