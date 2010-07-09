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

import static junit.framework.Assert.assertTrue;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link SetFollowingPersonStatusValidation} class.
 *
 */
public class SetFollowingPersonStatusValidationTest
{
    /**
     * System under test.
     */
    private SetFollowingPersonStatusValidation sut;

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
     * Mock instance of GetPeopleByAccountIds.
     */
    private final GetPeopleByAccountIds peopleMapperMock = context.mock(GetPeopleByAccountIds.class);

    /**
     * Mocked instance of the Principal class.
     */
    private final Principal principalMock = context.mock(Principal.class);

    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new SetFollowingPersonStatusValidation(peopleMapperMock);
    }

    /**
     * Test the validate method for success.
     */
    @Test
    public void testValidate()
    {
        SetFollowingStatusRequest request = new SetFollowingStatusRequest("ntaccount", "followingntaccount",
                EntityType.PERSON, false, Follower.FollowerStatus.FOLLOWING);

        final PersonModelView testFollower = new PersonModelView();
        testFollower.setEntityId(1L);
        final PersonModelView testFollowing = new PersonModelView();
        testFollowing.setEntityId(2L);

        context.checking(new Expectations()
        {
            {
                oneOf(peopleMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testFollower));

                oneOf(peopleMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testFollowing));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        sut.validate(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * Test the validate method when the wrong EntityType is supplied.
     */
    @Test
    public void testFailedValidationWrongEntity()
    {
        SetFollowingStatusRequest request = new SetFollowingStatusRequest("ntaccount", "followingntaccount",
                EntityType.NOTSET, false, Follower.FollowerStatus.FOLLOWING);

        final PersonModelView testFollower = new PersonModelView();
        testFollower.setEntityId(1L);
        final PersonModelView testFollowing = new PersonModelView();
        testFollowing.setEntityId(2L);

        context.checking(new Expectations()
        {
            {
                oneOf(peopleMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testFollower));

                oneOf(peopleMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testFollowing));
            }
        });

        try
        {
            ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
            sut.validate(currentContext);
        }
        catch (ValidationException vex)
        {
            assertTrue(vex.getErrors().containsKey("EntityType"));
        }

        context.assertIsSatisfied();
    }

    /**
     * Test the validate method when the wrong Follower/Target is invalid.
     */
    @Test
    public void testFailedValidationFollowerTargetInvalid()
    {
        SetFollowingStatusRequest request = new SetFollowingStatusRequest("ntaccount", "followingntaccount",
                EntityType.PERSON, false, Follower.FollowerStatus.FOLLOWING);

        final PersonModelView testFollower = new PersonModelView();
        testFollower.setEntityId(1L);

        context.checking(new Expectations()
        {
            {
                oneOf(peopleMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testFollower));

                oneOf(peopleMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(null));
            }
        });

        try
        {
            ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
            sut.validate(currentContext);
        }
        catch (ValidationException vex)
        {
            assertTrue(vex.getErrors().containsKey("FollowerAndTarget"));
        }

        context.assertIsSatisfied();
    }

    /**
     * Test the validate method when the wrong Follower/Target is missing.
     */
    @Test
    public void testFailedValidationFollowerTargetMissing()
    {
        SetFollowingStatusRequest request = new SetFollowingStatusRequest("ntaccount", "", EntityType.PERSON, false,
                Follower.FollowerStatus.FOLLOWING);

        final PersonModelView testFollower = new PersonModelView();
        testFollower.setEntityId(1L);

        context.checking(new Expectations()
        {
            {
                oneOf(peopleMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testFollower));

                oneOf(peopleMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(null));
            }
        });

        try
        {
            ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
            sut.validate(currentContext);
        }
        catch (ValidationException vex)
        {
            assertTrue(vex.getErrors().containsKey("FollowerAndTarget"));
        }

        context.assertIsSatisfied();
    }

    /**
     * Test the validate method when the wrong Follower/Target is missing.
     */
    @Test
    public void testFailedValidationFollowingStatusInvalid()
    {
        SetFollowingStatusRequest request = new SetFollowingStatusRequest("ntaccount", "anotherntaccount",
                EntityType.PERSON, false, Follower.FollowerStatus.NOTSPECIFIED);

        final PersonModelView testFollower = new PersonModelView();
        testFollower.setEntityId(1L);
        final PersonModelView testFollowing = new PersonModelView();
        testFollowing.setEntityId(2L);

        context.checking(new Expectations()
        {
            {
                oneOf(peopleMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testFollower));

                oneOf(peopleMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testFollowing));
            }
        });

        try
        {
            ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
            sut.validate(currentContext);
        }
        catch (ValidationException vex)
        {
            assertTrue(vex.getErrors().containsKey("FollowingStatus"));
        }

        context.assertIsSatisfied();
    }
}
