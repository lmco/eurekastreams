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
package org.eurekastreams.server.action.execution.stream;

import org.eurekastreams.server.action.request.stream.SetActivityLikeRequest;
import org.eurekastreams.server.action.request.stream.SetActivityLikeRequest.LikeActionType;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.LikedActivity;
import org.eurekastreams.server.persistence.mappers.DeleteLikedActivity;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.IndexEntity;
import org.eurekastreams.server.persistence.mappers.InsertLikedActivity;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.testing.TestContextCreator;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link SetActivityLikeExecution} class.
 *
 */
public class SetActivityLikeExecutionTest
{
    /**
     * System under test.
     */
    private SetActivityLikeExecution sut;

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
     * DeleteLikedActivity mapper mock.
     */
    private final InsertLikedActivity likeMapperMock = context.mock(InsertLikedActivity.class);

    /**
     * DeleteLikedActivity mapper mock.
     */
    private final DeleteLikedActivity unlikeMapperMock = context.mock(DeleteLikedActivity.class);

    /**
     * Indexer mock.
     */
    private final IndexEntity<Activity> activityIndexer = context.mock(IndexEntity.class);

    /**
     * Find Activity by id mock.
     */
    private final FindByIdMapper<Activity> activityEntityMapper = context.mock(FindByIdMapper.class);

    /**
     * Prepare the sut.
     */
    @Before
    public void setup()
    {
        sut = new SetActivityLikeExecution(likeMapperMock, unlikeMapperMock, activityIndexer, activityEntityMapper);
    }

    /**
     * Test adding a like.
     */
    @Test
    public void testAddLike()
    {
        final Activity activityEntity = context.mock(Activity.class);
        final SetActivityLikeRequest currentRequest = new SetActivityLikeRequest(1L, LikeActionType.ADD_LIKE);

        context.checking(new Expectations()
        {
            {
                oneOf(likeMapperMock).execute(with(any(LikedActivity.class)));

                oneOf(activityEntityMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(activityEntity));

                oneOf(activityIndexer).execute(activityEntity);
            }
        });

        sut.execute(TestContextCreator.createTaskHandlerContextWithPrincipal(currentRequest, null, 5L));
        context.assertIsSatisfied();
    }

    /**
     * Test removing a like.
     */
    @Test
    public void testRemoveLike()
    {
        final Activity activityEntity = context.mock(Activity.class);
        final SetActivityLikeRequest currentRequest = new SetActivityLikeRequest(1L, LikeActionType.REMOVE_LIKE);

        context.checking(new Expectations()
        {
            {
                oneOf(unlikeMapperMock).execute(with(any(LikedActivity.class)));

                oneOf(activityEntityMapper).execute(with(any(FindByIdRequest.class)));
                will(returnValue(activityEntity));

                oneOf(activityIndexer).execute(activityEntity);
            }
        });

        sut.execute(TestContextCreator.createTaskHandlerContextWithPrincipal(currentRequest, null, 5L));
        context.assertIsSatisfied();
    }
}
