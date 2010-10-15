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

import java.util.Map;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
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
     * Mapper to get a person's id from account id.
     */
    private final DomainMapper<String, Long> getPersonIdByAccountIdMapper = context.mock(DomainMapper.class,
            "getPersonIdByAccountIdMapper");

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
        sut = new SetFollowingPersonStatusValidation(getPersonIdByAccountIdMapper);
    }

    /**
     * Test the validate method for success.
     */
    @Test
    public void testValidate()
    {
        SetFollowingStatusRequest request = new SetFollowingStatusRequest("ntaccount", "followingntaccount",
                EntityType.PERSON, false, Follower.FollowerStatus.FOLLOWING);

        context.checking(new Expectations()
        {
            {
                oneOf(getPersonIdByAccountIdMapper).execute("ntaccount");
                will(returnValue(1L));

                oneOf(getPersonIdByAccountIdMapper).execute("followingntaccount");
                will(returnValue(2L));
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

        context.checking(new Expectations()
        {
            {
                oneOf(getPersonIdByAccountIdMapper).execute("ntaccount");
                will(returnValue(1L));

                oneOf(getPersonIdByAccountIdMapper).execute("followingntaccount");
                will(returnValue(2L));
            }
        });

        Map<String, String> errors = null;
        try
        {
            ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
            sut.validate(currentContext);
        }
        catch (ValidationException vex)
        {
            errors = vex.getErrors();
        }
        assertTrue(errors.containsKey("EntityType"));

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

        context.checking(new Expectations()
        {
            {
                oneOf(getPersonIdByAccountIdMapper).execute("ntaccount");
                will(returnValue(null));

                oneOf(getPersonIdByAccountIdMapper).execute("followingntaccount");
                will(returnValue(2L));
            }
        });

        Map<String, String> errors = null;
        try
        {
            ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
            sut.validate(currentContext);
        }
        catch (ValidationException vex)
        {
            errors = vex.getErrors();
        }
        assertTrue(errors.containsKey("FollowerAndTarget"));
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

        context.checking(new Expectations()
        {
            {
                oneOf(getPersonIdByAccountIdMapper).execute("ntaccount");
                will(returnValue(1L));

                oneOf(getPersonIdByAccountIdMapper).execute("");
                will(returnValue(null));
            }
        });

        Map<String, String> errors = null;
        try
        {
            ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
            sut.validate(currentContext);
        }
        catch (ValidationException vex)
        {
            errors = vex.getErrors();
        }
        assertTrue(errors.containsKey("FollowerAndTarget"));

        context.assertIsSatisfied();
    }
}
