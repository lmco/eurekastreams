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
package org.eurekastreams.server.action.execution.stream;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.commons.actions.InlineActionExecutor;
import org.eurekastreams.commons.actions.TaskHandlerAction;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.commons.test.EasyMatcher;
import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.action.request.stream.PostSplitActivityAndCommentsRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.ActivityVerb;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.server.service.utility.TextSplitter;
import org.eurekastreams.server.testing.TestContextCreator;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests PostSplitActivityAndCommentsExecution.
 */
public class PostSplitActivityAndCommentsExecutionTest
{
    /** Test data. */
    private static final String INPUT_STRING = "  This is the text to be split and posted as multiple comments.  ";

    /** Test data. */
    private static final String PIECE1 = "This is the text...";

    /** Test data. */
    private static final String PIECE2 = "...to be split and posted...";

    /** Test data. */
    private static final String PIECE3 = "...as multiple comments.";

    /** Test data. */
    private static final long ACTIVITY_ID = 100L;

    /** Test data. */
    private static final long PERSON_ID = 101L;

    /** Test data. */
    private static final long GROUP_ID = 103L;

    /** Test data. */
    private static final String PERSON_UNIQUE_ID = "PersonUniqueId";

    /** Test data. */
    private static final String GROUP_UNIQUE_ID = "GroupUniqueId";

    /** Test data. */
    private static final long USER_ID = 102L;

    /** Test data. */
    private static final String USER_ACCOUNT_ID = "useraccountid";

    /** Used for mocking objects. */
    private final Mockery mockery = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: text splitter. */
    private final TextSplitter textSplitter = mockery.mock(TextSplitter.class, "textSplitter");

    /** Fixture: executor. */
    private final InlineActionExecutor executor = mockery.mock(InlineActionExecutor.class, "executor");

    /** Fixture: post person activity action. */
    private final TaskHandlerAction postPersonActivityAction = mockery.mock(TaskHandlerAction.class,
            "postPersonActivityAction");

    /** Fixture: post group activity action. */
    private final TaskHandlerAction postGroupActivityAction = mockery.mock(TaskHandlerAction.class,
            "postGroupActivityAction");

    /** Fixture: post activity actions. */
    private final Map<EntityType, TaskHandlerAction> postActivityActions = new HashMap<EntityType, TaskHandlerAction>();

    /** Fixture: post comment action. */
    private final TaskHandlerAction postCommentAction = mockery.mock(TaskHandlerAction.class, "postCommentAction");

    /** Fixture: unique ID DAO. */
    private final DomainMapper<Long, String> personUniqueIdDAO = mockery.mock(DomainMapper.class, "personUniqueIdDAO");

    /** Fixture: unique ID DAO. */
    private final DomainMapper<Long, String> groupUniqueIdDAO = mockery.mock(DomainMapper.class, "groupUniqueIdDAO");

    /** Fixture: unique ID DAOs. */
    private final Map<EntityType, DomainMapper<Long, String>> uniqueIdDAOs = // \n
    new HashMap<EntityType, DomainMapper<Long, String>>();

    /** SUT. */
    private PostSplitActivityAndCommentsExecution sut;

    /** Fixture: activity. */
    private final ActivityDTO activity = mockery.mock(ActivityDTO.class, "activity");

    /**
     * Constructor.
     */
    public PostSplitActivityAndCommentsExecutionTest()
    {
        postActivityActions.put(EntityType.PERSON, postPersonActivityAction);
        postActivityActions.put(EntityType.GROUP, postGroupActivityAction);
        uniqueIdDAOs.put(EntityType.PERSON, personUniqueIdDAO);
        uniqueIdDAOs.put(EntityType.GROUP, groupUniqueIdDAO);
    }

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new PostSplitActivityAndCommentsExecution(textSplitter, executor,
                Collections.unmodifiableMap(postActivityActions), postCommentAction,
                Collections.unmodifiableMap(uniqueIdDAOs));
        mockery.checking(new Expectations()
        {
            {
                allowing(activity).getId();
                will(returnValue(ACTIVITY_ID));

                allowing(personUniqueIdDAO).execute(PERSON_ID);
                will(returnValue(PERSON_UNIQUE_ID));

                allowing(groupUniqueIdDAO).execute(GROUP_ID);
                will(returnValue(GROUP_UNIQUE_ID));
            }
        });
    }

    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testInvalidStreamType()
    {
        PostSplitActivityAndCommentsRequest request = new PostSplitActivityAndCommentsRequest(EntityType.RESOURCE,
                PERSON_ID, INPUT_STRING);
        TaskHandlerActionContext<PrincipalActionContext> context = TestContextCreator
                .createTaskHandlerContextWithPrincipal(request, USER_ACCOUNT_ID, USER_ID);

        sut.execute(context);
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = ValidationException.class)
    public void testEmptyText()
    {
        PostSplitActivityAndCommentsRequest request = new PostSplitActivityAndCommentsRequest(EntityType.PERSON,
                PERSON_ID, INPUT_STRING);
        TaskHandlerActionContext<PrincipalActionContext> context = TestContextCreator
                .createTaskHandlerContextWithPrincipal(request, USER_ACCOUNT_ID, USER_ID);

        mockery.checking(new Expectations()
        {
            {
                allowing(textSplitter).split(INPUT_STRING);
                will(returnValue(Collections.EMPTY_LIST));
            }
        });

        sut.execute(context);
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testActivityOnlyPersonStream()
    {
        PostSplitActivityAndCommentsRequest request = new PostSplitActivityAndCommentsRequest(EntityType.PERSON,
                PERSON_ID, INPUT_STRING);
        final TaskHandlerActionContext<PrincipalActionContext> context = TestContextCreator
                .createTaskHandlerContextWithPrincipal(request, USER_ACCOUNT_ID, USER_ID);

        mockery.checking(new Expectations()
        {
            {
                allowing(textSplitter).split(INPUT_STRING);
                will(returnValue(Collections.singletonList(PIECE1)));

                oneOf(executor).execute(with(same(postPersonActivityAction)), with(same(context)),
                        with(new EasyMatcher<PostActivityRequest>()
                        {
                            @Override
                            protected boolean isMatch(final PostActivityRequest inTestObject)
                            {
                                ActivityDTO act = inTestObject.getActivityDTO();
                                return PIECE1.equals(act.getBaseObjectProperties().get("content"))
                                        && BaseObjectType.NOTE == act.getBaseObjectType()
                                        && act.getVerb() == ActivityVerb.POST
                                        && act.getDestinationStream().getEntityType() == EntityType.PERSON
                                        && PERSON_UNIQUE_ID.equals(act.getDestinationStream().getUniqueId());
                            }
                        }));
                will(returnValue(activity));
            }
        });

        sut.execute(context);
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testActivityOnlyGroupStream()
    {
        PostSplitActivityAndCommentsRequest request = new PostSplitActivityAndCommentsRequest(EntityType.GROUP,
                GROUP_ID, INPUT_STRING);
        final TaskHandlerActionContext<PrincipalActionContext> context = TestContextCreator
                .createTaskHandlerContextWithPrincipal(request, USER_ACCOUNT_ID, USER_ID);

        mockery.checking(new Expectations()
        {
            {
                allowing(textSplitter).split(INPUT_STRING);
                will(returnValue(Collections.singletonList(PIECE1)));

                oneOf(executor).execute(with(same(postGroupActivityAction)), with(same(context)),
                        with(new EasyMatcher<PostActivityRequest>()
                        {
                            @Override
                            protected boolean isMatch(final PostActivityRequest inTestObject)
                            {
                                ActivityDTO act = inTestObject.getActivityDTO();
                                return PIECE1.equals(act.getBaseObjectProperties().get("content"))
                                        && BaseObjectType.NOTE == act.getBaseObjectType()
                                        && act.getVerb() == ActivityVerb.POST
                                        && act.getDestinationStream().getEntityType() == EntityType.GROUP
                                        && GROUP_UNIQUE_ID.equals(act.getDestinationStream().getUniqueId());
                            }
                        }));
                will(returnValue(activity));
            }
        });

        sut.execute(context);
        mockery.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public void testActivityAndComments()
    {
        PostSplitActivityAndCommentsRequest request = new PostSplitActivityAndCommentsRequest(EntityType.PERSON,
                PERSON_ID, INPUT_STRING);
        final TaskHandlerActionContext<PrincipalActionContext> context = TestContextCreator
                .createTaskHandlerContextWithPrincipal(request, USER_ACCOUNT_ID, USER_ID);

        final Sequence seq = mockery.sequence("posts");
        mockery.checking(new Expectations()
        {
            {
                allowing(textSplitter).split(INPUT_STRING);
                will(returnValue(Arrays.asList(PIECE1, PIECE2, PIECE3)));

                oneOf(executor).execute(with(same(postPersonActivityAction)), with(same(context)),
                        with(new EasyMatcher<PostActivityRequest>()
                        {
                            @Override
                            protected boolean isMatch(final PostActivityRequest inTestObject)
                            {
                                ActivityDTO act = inTestObject.getActivityDTO();
                                return PIECE1.equals(act.getBaseObjectProperties().get("content"))
                                        && BaseObjectType.NOTE == act.getBaseObjectType()
                                        && act.getVerb() == ActivityVerb.POST
                                        && act.getDestinationStream().getEntityType() == EntityType.PERSON
                                        && PERSON_UNIQUE_ID.equals(act.getDestinationStream().getUniqueId());
                            }
                        }));
                will(returnValue(activity));
                inSequence(seq);

                oneOf(executor).execute(with(same(postCommentAction)), with(same(context)),
                        with(getCommentDtoMatcher(PIECE2)));
                inSequence(seq);
                oneOf(executor).execute(with(same(postCommentAction)), with(same(context)),
                        with(getCommentDtoMatcher(PIECE3)));
                inSequence(seq);
            }
        });

        sut.execute(context);
        mockery.assertIsSatisfied();
    }

    /**
     * Creates a matcher to check for the specific comment being created.
     *
     * @param text
     *            Text to expect.
     * @return Matcher.
     */
    private Matcher<CommentDTO> getCommentDtoMatcher(final String text)
    {
        return new EasyMatcher<CommentDTO>()
        {
            @Override
            protected boolean isMatch(final CommentDTO inTestObject)
            {
                return inTestObject.getActivityId() == ACTIVITY_ID && text.equals(inTestObject.getBody());
            }
        };
    }
}
