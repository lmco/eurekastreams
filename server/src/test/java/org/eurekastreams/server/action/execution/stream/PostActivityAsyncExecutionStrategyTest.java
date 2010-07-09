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

import org.eurekastreams.commons.actions.context.async.AsyncActionContext;
import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.stream.PostCachedActivity;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link PostActivityAsyncExecutionStrategy} class.
 *
 */
public class PostActivityAsyncExecutionStrategyTest
{
    /**
     * System under test.
     */
    private PostActivityAsyncExecutionStrategy sut;

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
     * Mocked instance of the {@link PostCachedActivity} mapper.
     */
    private PostCachedActivity postCachedActivityMapperMock = context.mock(PostCachedActivity.class);

    /**
     * Mocked instance of the {@link AsyncActionContext} class.
     */
    private AsyncActionContext asyncActionContextMock = context.mock(AsyncActionContext.class);

    /**
     * Mocked instance of the {@link PostActivityRequest} object.
     */
    private PostActivityRequest postActivityRequestMock = context.mock(PostActivityRequest.class);

    /**
     * Mocked instance of the {@link ActivityDTO} class for this test.
     */
    private ActivityDTO activityDTOMock = context.mock(ActivityDTO.class);

    /**
     * Setup the sut for the test suite.
     */
    @Before
    public void setup()
    {
        sut = new PostActivityAsyncExecutionStrategy(postCachedActivityMapperMock);
    }

    /**
     * Test the successful execution of the strategy.
     */
    @Test
    public void testExecute()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(asyncActionContextMock).getParams();
                will(returnValue(postActivityRequestMock));

                oneOf(postActivityRequestMock).getActivityDTO();
                will(returnValue(activityDTOMock));

                oneOf(postCachedActivityMapperMock).execute(activityDTOMock);
            }
        });

        sut.execute(asyncActionContextMock);

        context.assertIsSatisfied();
    }
}
