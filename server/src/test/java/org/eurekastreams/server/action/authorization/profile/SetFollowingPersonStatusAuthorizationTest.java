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
package org.eurekastreams.server.action.authorization.profile;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Follower;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * This class is responsible for testing the {@link SetFollowingPersonStatusAuthorization} class.
 *
 */
public class SetFollowingPersonStatusAuthorizationTest
{
    /**
     * System under test.
     */
    private SetFollowingPersonStatusAuthorization sut;

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
     * Mocked instance of the principal object.
     */
    private final Principal principalMock = context.mock(Principal.class);

    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new SetFollowingPersonStatusAuthorization();
    }

    /**
     * Test successful authorization.
     */
    @Test
    public void testAuthorizeFollowingSuccess()
    {
        final SetFollowingStatusRequest request = new SetFollowingStatusRequest("ntaccount", "anotherntaccount",
                EntityType.PERSON, false, Follower.FollowerStatus.FOLLOWING);

        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getAccountId();
                will(returnValue("ntaccount"));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        sut.authorize(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * Test authorization when the person trying to add a follower is not the follower.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeFollowingFailDifferentUser()
    {
        final SetFollowingStatusRequest request = new SetFollowingStatusRequest("ntaccount", "anotherntaccount",
                EntityType.PERSON, false, Follower.FollowerStatus.FOLLOWING);

        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getAccountId();
                will(returnValue("ntaccountdifferentfromfollower"));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        sut.authorize(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * Test authorization when user is trying to follow themselves.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeFollowingFailUserFollowingSelf()
    {
        final SetFollowingStatusRequest request = new SetFollowingStatusRequest("ntaccount", "ntaccount",
                EntityType.PERSON, false, Follower.FollowerStatus.FOLLOWING);

        context.checking(new Expectations()
        {
            {
                oneOf(principalMock).getAccountId();
                will(returnValue("ntaccount"));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        sut.authorize(currentContext);

        context.assertIsSatisfied();
    }
}
