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
package org.eurekastreams.server.action.validation.profile;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
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
 * Test suite for the {@link SetFollowingStatusBaseValidation} class.
 *
 */
public class SetFollowingStatusBaseValidationTest
{
    /**
     * System under test.
     */
    private SetFollowingStatusBaseValidation sut;

    /**
     * Setup the mocking context for this test class.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mock instance of the Principal object.
     */
    private final Principal principalMock = context.mock(Principal.class);

    /**
     * Mocked instance of the decorator validation strategy.
     */
    private final ValidationStrategy<PrincipalActionContext> mockValidator = context.mock(ValidationStrategy.class);

    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new SetFollowingStatusBaseValidation(mockValidator);
    }

    /**
     * Method to test successful validation.
     */
    @Test
    public void testValidate()
    {
        SetFollowingStatusRequest request = new SetFollowingStatusRequest("ntaccount", "followingntaccount",
                EntityType.PERSON, false, Follower.FollowerStatus.FOLLOWING);

        // Need to satisfy this expectation since I am using a sub class to test the base.
        context.checking(new Expectations()
        {
            {
                oneOf(mockValidator).validate(with(any(PrincipalActionContext.class)));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        sut.validate(currentContext);
    }

    /**
     * Method to test failed validation because of missing ids.
     */
    @Test(expected = ValidationException.class)
    public void testValidateFailureMissingUniqueIds()
    {
        SetFollowingStatusRequest request = new SetFollowingStatusRequest("", "followingntaccount", EntityType.PERSON,
                false, Follower.FollowerStatus.FOLLOWING);

        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        sut.validate(currentContext);
    }

    /**
     * Method to test failed validation because of invalid following status.
     */
    @Test(expected = ValidationException.class)
    public void testValidateFailureInvalidFollowingStatus()
    {
        SetFollowingStatusRequest request = new SetFollowingStatusRequest("ntaccount", "followingntaccount",
                EntityType.PERSON, false, Follower.FollowerStatus.NOTSPECIFIED);

        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        sut.validate(currentContext);
    }
}
