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
package org.eurekastreams.server.action.execution.profile;

import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.profile.RequestForGroupMembershipRequest;
import org.eurekastreams.server.persistence.mappers.db.DeleteRequestForGroupMembership;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;


/**
 *
 *
 */
public class DeleteRequestForGroupMembershipExecutionTest
{
    /** Test data. */
    private static final long GROUP_ID = 9988L;

    /** Test data. */
    private static final long PERSON_ID = 222L;

    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Mapper for deleting group requests. */
    private DeleteRequestForGroupMembership mapper = context.mock(DeleteRequestForGroupMembership.class);

    /** Fixture: request. */
    private RequestForGroupMembershipRequest request;

    /** Fixture: action context. */
    private PrincipalActionContext actionCtx = context.mock(PrincipalActionContext.class);

    /** SUT. */
    private DeleteRequestForGroupMembershipExecution sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new DeleteRequestForGroupMembershipExecution(mapper);
        request = new RequestForGroupMembershipRequest(GROUP_ID, PERSON_ID);

        context.checking(new Expectations()
        {
            {
                allowing(actionCtx).getParams();
                will(returnValue(request));
            }
        });
    }

    /**
     * Tests deleting a request.
     */
    @Test
    public void testExecute()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(mapper).execute(with(equalInternally(request)));
                will(returnValue(Boolean.TRUE));
            }
        });
        sut.execute(actionCtx);

        context.assertIsSatisfied();
    }
}
