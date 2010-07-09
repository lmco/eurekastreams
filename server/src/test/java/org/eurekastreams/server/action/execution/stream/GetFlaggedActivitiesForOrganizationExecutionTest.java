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

import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.Serializable;
import java.util.ArrayList;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.stream.GetFlaggedActivitiesByOrgRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.stream.GetFlaggedActivitiesForOrganization;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests GetFlaggedActivitiesForOrganizationExecution.
 */
public class GetFlaggedActivitiesForOrganizationExecutionTest
{

    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Test data. */
    private static final long ORG_ID = 1245L;

    /** Fixture: mapper. */
    private GetFlaggedActivitiesForOrganization mapper = context.mock(GetFlaggedActivitiesForOrganization.class);

    /** Fixture: actionCtx. */
    private PrincipalActionContext actionCtx = context.mock(PrincipalActionContext.class);

    /**
     * Principal mock.
     */
    private Principal principal = context.mock(Principal.class);

    /** SUT. */
    private GetFlaggedActivitiesForOrganizationExecution sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new GetFlaggedActivitiesForOrganizationExecution(mapper);
    }

    /**
     * Tests execute.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testExecute()
    {
        final String accountId = "sldfjkds";
        final GetFlaggedActivitiesByOrgRequest rqst = new GetFlaggedActivitiesByOrgRequest(ORG_ID, 3, 5);
        final PagedSet<ActivityDTO> pagedSet = context.mock(PagedSet.class);

        context.checking(new Expectations()
        {
            {
                // mapper should get a request with the same values as the one passed to the action.
                oneOf(mapper).execute(with(equalInternally(rqst)));
                will(returnValue(pagedSet));

                allowing(actionCtx).getParams();
                will(returnValue(rqst));

                oneOf(actionCtx).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getAccountId();
                will(returnValue(accountId));

                allowing(pagedSet).getPagedSet();
                will(returnValue(new ArrayList<ActivityDTO>()));
            }
        });

        Serializable result = sut.execute(actionCtx);
        context.assertIsSatisfied();
        assertSame(pagedSet, result);

        assertEquals(accountId, rqst.getRequestingUserAccountId());
    }
}
