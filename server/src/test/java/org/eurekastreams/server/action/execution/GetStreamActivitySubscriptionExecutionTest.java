/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
import static org.junit.Assert.assertTrue;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.GetStreamActivitySubscriptionMapperRequest;
import org.eurekastreams.server.testing.TestContextCreator;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests GetStreamActivitySubscriptionExecution.
 */
public class GetStreamActivitySubscriptionExecutionTest
{
    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** DAO to get a stream entity id by its unique ID. */
    private final DomainMapper<String, Long> entityIdFromUniqueIdDAO = context.mock(DomainMapper.class,
            "entityIdFromUniqueIdDAO");

    /** DAO to get a person's activity notification preference for a specific stream. */
    private final DomainMapper<GetStreamActivitySubscriptionMapperRequest, Boolean> getNotificationPreferenceDAO = // \n
    context.mock(DomainMapper.class, "getNotificationPreferenceDAO");

    /**
     * Tests execute.
     */
    @Test
    public void testExecute()
    {
        final String streamUID = "stream";
        final long streamId = 55L;
        long userId = 44L;
        final GetStreamActivitySubscriptionMapperRequest request = new GetStreamActivitySubscriptionMapperRequest(
                userId, streamId);

        context.checking(new Expectations()
        {
            {
                allowing(entityIdFromUniqueIdDAO).execute(streamUID);
                will(returnValue(streamId));

                allowing(getNotificationPreferenceDAO).execute(with(equalInternally(request)));
                will(returnValue(true));
            }
        });

        ExecutionStrategy<PrincipalActionContext> sut = new GetStreamActivitySubscriptionExecution(
                entityIdFromUniqueIdDAO, getNotificationPreferenceDAO);
        assertTrue((Boolean) sut.execute(TestContextCreator.createPrincipalActionContext(streamUID, "jdoe", userId)));
        context.assertIsSatisfied();
    }
}
