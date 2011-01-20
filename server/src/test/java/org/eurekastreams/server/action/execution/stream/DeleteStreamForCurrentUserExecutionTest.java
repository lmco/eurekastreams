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

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.DeleteAndReorderStreamsRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests deleting a stream for the current user.
 */
public class DeleteStreamForCurrentUserExecutionTest
{
    /**
     * Context for building mock objects.
     */
    private static final Mockery CONTEXT = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private static DeleteStreamForCurrentUserExecution sut;

    /**
     * Domain mapper for deleting person_stream entry.
     */
    private static DomainMapper<DeleteAndReorderStreamsRequest, Boolean> deleteAndReorderStreamsMapper = CONTEXT
            .mock(DomainMapper.class);

    /**
     * Action context.
     */
    private static PrincipalActionContext actionContext = CONTEXT.mock(PrincipalActionContext.class);

    /**
     * Principle.
     */
    private static Principal principal = CONTEXT.mock(Principal.class);

    /**
     * User Id.
     */
    private static final Long USER_ID = 6L;

    /**
     * Setup fixtures.
     */
    @BeforeClass
    public static final void setup()
    {
        sut = new DeleteStreamForCurrentUserExecution(deleteAndReorderStreamsMapper);
    }

    /**
     * Tests deleting a stream that belongs to the current user.
     */
    @Test
    public void testDelete()
    {
        final Long streamId = 1L;

        CONTEXT.checking(new Expectations()
        {
            {
                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(USER_ID));

                allowing(actionContext).getParams();
                will(returnValue(streamId));

                oneOf(deleteAndReorderStreamsMapper).execute(with(any(DeleteAndReorderStreamsRequest.class)));
                will(returnValue(true));
            }
        });

        sut.execute(actionContext);

        CONTEXT.assertIsSatisfied();
    }
}
