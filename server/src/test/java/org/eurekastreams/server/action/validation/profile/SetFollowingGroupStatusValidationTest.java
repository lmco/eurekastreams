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

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * This class is responsible for testing the {@link SetFollowingGroupStatusValidation} class.
 * 
 */
public class SetFollowingGroupStatusValidationTest
{
    /**
     * System under test.
     */
    private SetFollowingGroupStatusValidation sut;

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
     * Mock instance of GetDomainGroupsByShortNames.
     */
    private final GetDomainGroupsByShortNames domainGroupMapperMock = context.mock(GetDomainGroupsByShortNames.class);

    /**
     * Mapper to get Person's id by account id.
     */
    private final DomainMapper<String, Long> getPersonIdByAccountIdMapper = context.mock(DomainMapper.class,
            "getPersonIdByAccountIdMapper");

    /**
     * Mocked instance of the Principal object.
     */
    private final Principal principalMock = context.mock(Principal.class);

    /**
     * Prepare the system under test.
     */
    @Before
    public void setup()
    {
        sut = new SetFollowingGroupStatusValidation(domainGroupMapperMock, getPersonIdByAccountIdMapper);
    }

    /**
     * Test successful validation.
     */
    @Test
    public void testValidate()
    {
        SetFollowingStatusRequest request = new SetFollowingStatusRequest("ntaccount", "groupshortname",
                EntityType.GROUP, false, Follower.FollowerStatus.FOLLOWING);

        final DomainGroupModelView testTarget = new DomainGroupModelView();
        testTarget.setEntityId(2L);

        context.checking(new Expectations()
        {
            {
                oneOf(getPersonIdByAccountIdMapper).execute("ntaccount");
                will(returnValue(1L));

                oneOf(domainGroupMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testTarget));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        sut.validate(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * Test unsuccessful validation with wrong entity type.
     */
    @Test(expected = ValidationException.class)
    public void testValidateWrongEntityType()
    {
        SetFollowingStatusRequest request = new SetFollowingStatusRequest("ntaccount", "groupshortname",
                EntityType.NOTSET, false, Follower.FollowerStatus.FOLLOWING);

        context.checking(new Expectations()
        {
            {
                oneOf(getPersonIdByAccountIdMapper).execute("ntaccount");
                will(returnValue(1L));

                oneOf(domainGroupMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(null));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        sut.validate(currentContext);

        context.assertIsSatisfied();
    }

    /**
     * Test unsuccessful validation with invalid entities.
     */
    @Test(expected = ValidationException.class)
    public void testValidateInvalidEntities()
    {
        SetFollowingStatusRequest request = new SetFollowingStatusRequest("ntaccount", "groupshortname",
                EntityType.GROUP, false, Follower.FollowerStatus.FOLLOWING);

        context.checking(new Expectations()
        {
            {
                oneOf(getPersonIdByAccountIdMapper).execute("ntaccount");
                will(returnValue(1L));

                oneOf(domainGroupMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(null));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        sut.validate(currentContext);

        context.assertIsSatisfied();
    }
}
