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
package org.eurekastreams.server.action.execution.feed;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.feed.DeleteFeedSubscriptionRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.userdetails.UserDetails;

/**
 * Delete feed subscriber test.
 * 
 */
public class DeleteFeedSubscriberExecutionTest
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
     * Delete mapper.
     */
    private DomainMapper deleteMapper = context.mock(DomainMapper.class);

    /**
     * User mock.
     */
    private UserDetails user = context.mock(UserDetails.class);

    /**
     * System under test.
     */
    private DeleteFeedSubscriberExecution sut;

    /**
     * Empty params.
     */
    final DeleteFeedSubscriptionRequest testParam = context.mock(DeleteFeedSubscriptionRequest.class);

    /**
     * action context.
     */
    final PrincipalActionContext ac = context.mock(PrincipalActionContext.class);

    /**
     * Setup the test.
     */
    @Before
    public final void setUp()
    {
        sut = new DeleteFeedSubscriberExecution(deleteMapper);
    }

    /**
     * Perform action.
     * 
     * @throws Exception
     *             exception.
     */
    @Test
    public void performAction() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(deleteMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(true));

                allowing(ac).getParams();
                will(returnValue(testParam));

                allowing(testParam).getFeedSubscriberId();
                will(returnValue(1L));
            }
        });

        sut.execute(ac);
        context.assertIsSatisfied();
    }

}
