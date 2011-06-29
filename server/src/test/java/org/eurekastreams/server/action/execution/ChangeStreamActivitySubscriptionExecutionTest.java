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

package org.eurekastreams.server.action.execution;

import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.stream.ChangeStreamActivitySubscriptionRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.ChangeStreamActivitySubscriptionMapperRequest;
import org.eurekastreams.server.testing.TestContextCreator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests ChangeStreamActivitySubscriptionExecution.
 */
@SuppressWarnings("unchecked")
public class ChangeStreamActivitySubscriptionExecutionTest
{
    /** Test data. */
    private static final long PERSON_ID = 2138L;

    /** Test data. */
    private static final String PERSON_ACCOUNT_ID = "jdoe";

    /** Test data. */
    private static final long STREAM_ENTITY_ID = 100L;

    /** Test data. */
    private static final String STREAM_ENTITY_UNIQUE_ID = "snuts";

    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: DAO to get a stream entity id by its unique ID. */
    private final DomainMapper<String, Long> entityIdFromUniqueIdDAO = context.mock(DomainMapper.class,
            "entityIdFromUniqueIdDAO");

    /** Fixture: DAO to change a person's activity notification preference for a specific stream. */
    private final DomainMapper<ChangeStreamActivitySubscriptionMapperRequest, Boolean> changePreferenceDAO = // \n
    context.mock(DomainMapper.class, "changePreferenceDAO");

    /**
     * System under test.
     */
    private ExecutionStrategy<PrincipalActionContext> sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new ChangeStreamActivitySubscriptionExecution(entityIdFromUniqueIdDAO, changePreferenceDAO);
    }

    /**
     * Test with invalid request type.
     */
    @Test(expected = ClassCastException.class)
    public void testInvalidRequestType()
    {
        PrincipalActionContext actionContext = TestContextCreator.createPrincipalActionContext(2L, PERSON_ACCOUNT_ID,
                PERSON_ID);

        sut.execute(actionContext);
    }

    /**
     * Test success when should receive notifications.
     */
    @Test
    public void testSuccessOnTrue()
    {
        final ChangeStreamActivitySubscriptionMapperRequest expected = // \n
        new ChangeStreamActivitySubscriptionMapperRequest(PERSON_ID, STREAM_ENTITY_ID, true);

        context.checking(new Expectations()
        {
            {
                allowing(entityIdFromUniqueIdDAO).execute(STREAM_ENTITY_UNIQUE_ID);
                will(returnValue(STREAM_ENTITY_ID));

                oneOf(changePreferenceDAO).execute(with(equalInternally(expected)));
                will(returnValue(Boolean.TRUE));
            }
        });

        PrincipalActionContext actionContext = TestContextCreator.createPrincipalActionContext(
                new ChangeStreamActivitySubscriptionRequest(STREAM_ENTITY_UNIQUE_ID, true), PERSON_ACCOUNT_ID,
                PERSON_ID);

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }

    /**
     * Test success when should not receive notifications.
     */
    @Test
    public void testSuccessOnFalse()
    {
        final ChangeStreamActivitySubscriptionMapperRequest expected = // \n
        new ChangeStreamActivitySubscriptionMapperRequest(PERSON_ID, STREAM_ENTITY_ID, false);

        context.checking(new Expectations()
        {
            {
                allowing(entityIdFromUniqueIdDAO).execute(STREAM_ENTITY_UNIQUE_ID);
                will(returnValue(STREAM_ENTITY_ID));

                oneOf(changePreferenceDAO).execute(with(equalInternally(expected)));
                will(returnValue(Boolean.TRUE));
            }
        });

        PrincipalActionContext actionContext = TestContextCreator.createPrincipalActionContext(
                new ChangeStreamActivitySubscriptionRequest(STREAM_ENTITY_UNIQUE_ID, false), PERSON_ACCOUNT_ID,
                PERSON_ID);

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }
}
