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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.commons.actions.context.async.AsyncActionContext;
import org.eurekastreams.commons.test.IsEqualInternally;
import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.domain.strategies.HashTagExtractor;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.domain.stream.HashTag;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.chained.DecoratedPartialResponseDomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.stream.ActivityContentExtractor;
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
     * Hash tag extractor.
     */
    private HashTagExtractor hashTagExtractor = context.mock(HashTagExtractor.class);

    /**
     * Content extractor.
     */
    private final ActivityContentExtractor contentExtractor = context.mock(ActivityContentExtractor.class);

    /**
     * Mapper to get hash tags from the database.
     */
    private final DecoratedPartialResponseDomainMapper<List<String>, List<HashTag>> hashTagMapper = context
            .mock(DecoratedPartialResponseDomainMapper.class);

    /**
     * Mapper to store hash tags to an activity.
     */
    private final FindByIdMapper findByIdMapper = context.mock(FindByIdMapper.class);

    /**
     * Setup the sut for the test suite.
     */
    @Before
    public void setup()
    {
        sut = new PostActivityAsyncExecutionStrategy(postCachedActivityMapperMock, hashTagExtractor, contentExtractor,
                hashTagMapper, findByIdMapper);
    }

    /**
     * Test the successful execution of the strategy - missing activity.
     */
    @Test
    public void testExecuteNoActivity()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(asyncActionContextMock).getParams();
                will(returnValue(postActivityRequestMock));

                allowing(activityDTOMock).getId();
                will(returnValue(3L));

                oneOf(findByIdMapper).execute(
                        with(IsEqualInternally.equalInternally(new FindByIdRequest("Activity", 3L))));
                will(returnValue(null));

                oneOf(postActivityRequestMock).getActivityDTO();
                will(returnValue(activityDTOMock));

                oneOf(postCachedActivityMapperMock).execute(activityDTOMock);
            }
        });

        sut.execute(asyncActionContextMock);

        context.assertIsSatisfied();
    }

    /**
     * Test the successful execution of the strategy - has hash tags.
     */
    @Test
    public void testExecuteWithHashTag()
    {
        final BaseObjectType type = BaseObjectType.NOTE;
        final HashMap<String, String> properties = new HashMap<String, String>();
        final String activityContent = "foo #bar #hey now";

        final List<String> hashTagContents = new ArrayList<String>();
        hashTagContents.add("#bar");
        hashTagContents.add("#hey");

        final List<HashTag> hashTags = new ArrayList<HashTag>();
        hashTags.add(new HashTag("#bar"));
        hashTags.add(new HashTag("#hey"));

        final Set<HashTag> hashTagSet = new HashSet<HashTag>();
        hashTagSet.addAll(hashTags);

        final Activity activityMock = context.mock(Activity.class);

        context.checking(new Expectations()
        {
            {
                oneOf(asyncActionContextMock).getParams();
                will(returnValue(postActivityRequestMock));

                allowing(activityDTOMock).getId();
                will(returnValue(3L));

                oneOf(findByIdMapper).execute(
                        with(IsEqualInternally.equalInternally(new FindByIdRequest("Activity", 3L))));
                will(returnValue(activityMock));

                allowing(activityMock).getId();
                will(returnValue(3L));

                oneOf(postActivityRequestMock).getActivityDTO();
                will(returnValue(activityDTOMock));

                oneOf(postCachedActivityMapperMock).execute(activityDTOMock);

                oneOf(activityDTOMock).getBaseObjectType();
                will(returnValue(type));

                oneOf(activityDTOMock).getBaseObjectProperties();
                will(returnValue(properties));

                oneOf(contentExtractor).extractContent(type, properties);
                will(returnValue(activityContent));

                oneOf(hashTagExtractor).extractAll(activityContent);
                will(returnValue(hashTagContents));

                oneOf(hashTagMapper).execute(hashTagContents);
                will(returnValue(hashTags));
            }
        });

        sut.execute(asyncActionContextMock);

        context.assertIsSatisfied();
    }

    /**
     * Test the successful execution of the strategy - no hashtags.
     */
    @Test
    public void testExecuteNoHashTags()
    {
        final BaseObjectType type = BaseObjectType.NOTE;
        final HashMap<String, String> properties = new HashMap<String, String>();
        final List<String> hashTagContents = new ArrayList<String>();
        final String activityContent = "foo #bar #hey now";
        final Activity activityMock = context.mock(Activity.class);

        context.checking(new Expectations()
        {
            {
                oneOf(asyncActionContextMock).getParams();
                will(returnValue(postActivityRequestMock));

                allowing(activityDTOMock).getId();
                will(returnValue(3L));

                oneOf(findByIdMapper).execute(
                        with(IsEqualInternally.equalInternally(new FindByIdRequest("Activity", 3L))));
                will(returnValue(activityMock));

                allowing(activityMock).getId();
                will(returnValue(3L));

                oneOf(postActivityRequestMock).getActivityDTO();
                will(returnValue(activityDTOMock));

                oneOf(postCachedActivityMapperMock).execute(activityDTOMock);

                oneOf(activityDTOMock).getBaseObjectType();
                will(returnValue(type));

                oneOf(activityDTOMock).getBaseObjectProperties();
                will(returnValue(properties));

                oneOf(contentExtractor).extractContent(type, properties);
                will(returnValue(activityContent));

                oneOf(hashTagExtractor).extractAll(activityContent);
                will(returnValue(hashTagContents));
            }
        });

        sut.execute(asyncActionContextMock);

        context.assertIsSatisfied();
    }
}
