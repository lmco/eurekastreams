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
package org.eurekastreams.server.action.authorization.stream;

import java.io.Serializable;

import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.eurekastreams.server.service.actions.strategies.ActivityInteractionType;
import org.eurekastreams.server.service.utility.authorization.ActivityInteractionAuthorizationStrategy;
import org.eurekastreams.server.testing.TestContextCreator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;


/**
 * Test fixture for ActivityAuthorizationStrategy.
 */
public class ActivityAuthorizationStrategyTest
{
    /** Test data. */
    private static final Long ACTIVITY_ID = 1234L;

    /** Test data. */
    private static final long USER_ID = 2345L;

    /** Used for mocking objects. */
    private final Mockery mockery = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Does the actual work of determining authorization. */
    private final ActivityInteractionAuthorizationStrategy activityAuthorizer = mockery.mock(
            ActivityInteractionAuthorizationStrategy.class, "activityAuthorizer");

    /** Strategy for getting activity ID from incoming params. */
    private final Transformer<Serializable, Long> activityIdFromParamsTransformer = mockery.mock(Transformer.class,
            "activityIdFromParamsTransformer");

    /** Gets activity. */
    private final DomainMapper<Long, ActivityDTO> getActivityDAO = mockery.mock(DomainMapper.class, "getActivityDAO");

    /** Activity. */
    private final ActivityDTO activity = mockery.mock(ActivityDTO.class, "activity");

    /** Fixture: params. */
    private final Serializable params = mockery.mock(Serializable.class, "params");

    /** SUT. */
    private ActivityAuthorizationStrategy sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
        {
        sut = new ActivityAuthorizationStrategy(activityAuthorizer, ActivityInteractionType.COMMENT,
                activityIdFromParamsTransformer, getActivityDAO);

    }

    /**
     * Test.
     */
    @Test
    public void testAllowed()
        {
        mockery.checking(new Expectations()
            {
            {
                allowing(activityIdFromParamsTransformer).transform(params);
                will(returnValue(ACTIVITY_ID));
                allowing(getActivityDAO).execute(ACTIVITY_ID);
                will(returnValue(activity));
                oneOf(activityAuthorizer).authorize(USER_ID, activity, ActivityInteractionType.COMMENT);
                will(returnValue(true));
            }
        });
        sut.authorize(TestContextCreator.createPrincipalActionContext(params, null, USER_ID));
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = AuthorizationException.class)
    public void testForbidden()
        {
        mockery.checking(new Expectations()
            {
            {
                allowing(activityIdFromParamsTransformer).transform(params);
                will(returnValue(ACTIVITY_ID));
                allowing(getActivityDAO).execute(ACTIVITY_ID);
                will(returnValue(activity));
                oneOf(activityAuthorizer).authorize(USER_ID, activity, ActivityInteractionType.COMMENT);
                will(returnValue(false));
            }
        });
        sut.authorize(TestContextCreator.createPrincipalActionContext(params, null, USER_ID));
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = AuthorizationException.class)
    public void testError()
        {
        mockery.checking(new Expectations()
            {
            {
                allowing(activityIdFromParamsTransformer).transform(params);
                will(throwException(new IllegalArgumentException("BAD")));
            }
        });
        sut.authorize(TestContextCreator.createPrincipalActionContext(params, null, USER_ID));
        mockery.assertIsSatisfied();
    }
}
