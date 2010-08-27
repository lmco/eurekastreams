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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.stream.BulkActivitiesMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetCommentsById;
import org.eurekastreams.server.persistence.mappers.stream.GetOrderedCommentIdsByActivityId;
import org.eurekastreams.server.persistence.strategies.CommentDeletePropertyStrategy;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.server.service.actions.strategies.activity.ActivityFilter;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for GetActivityByIdExecutionStrategy.
 */
public class GetActivityByIdExecutionStrategyTest
{
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
     * Current user's account id.
     */
    private final String accountId = "sdljfdsl";

    /**
     * System under test.
     */
    private GetActivityByIdExecutionStrategy sut;

    /**
     * Bulk mapper mock.
     */
    private BulkActivitiesMapper activityDAO = context.mock(BulkActivitiesMapper.class);

    /**
     * DAO for finding comment ids.
     */
    private GetOrderedCommentIdsByActivityId commentIdsByActivityIdDAO = context
            .mock(GetOrderedCommentIdsByActivityId.class);

    /**
     * DAO for finding comments by id.
     */
    private GetCommentsById commentsByIdDAO = context.mock(GetCommentsById.class);

    /**
     * ActivityDTO.
     */
    private ActivityDTO activityDTO = context.mock(ActivityDTO.class);

    /**
     * Mock strategy for setting Deletable property on CommentDTOs.
     */
    private CommentDeletePropertyStrategy commentDeletableSetter = context.mock(CommentDeletePropertyStrategy.class);

    /**
     * Filter Mock.
     */
    private ActivityFilter filterMock = context.mock(ActivityFilter.class);

    /**
     * Setup text fixtures.
     */
    @Before
    public final void setUp()
    {
        List<ActivityFilter> filters = new LinkedList<ActivityFilter>();
        filters.add(filterMock);
        sut = new GetActivityByIdExecutionStrategy(activityDAO, commentIdsByActivityIdDAO, commentsByIdDAO,
                commentDeletableSetter, filters);
    }

    /**
     * Perform execute.
     * 
     * @throws Exception
     *             on failure.
     */
    @Test
    @SuppressWarnings("unchecked")
    public final void executeTest() throws Exception
    {
        // result list of activityDTOs.
        final ArrayList<ActivityDTO> activities = new ArrayList<ActivityDTO>();
        activities.add(activityDTO);

        // result list of comment ids.
        final List<Long> commentIds = new ArrayList<Long>(2);
        commentIds.add(1L);
        commentIds.add(2L);

        // result list of comments.
        final List<CommentDTO> comments = new ArrayList(2);
        comments.add(context.mock(CommentDTO.class, "comment1"));
        comments.add(context.mock(CommentDTO.class, "comment2"));

        context.checking(new Expectations()
        {
            {
                oneOf(activityDAO).execute(with(any(ArrayList.class)), with(any(String.class)));
                will(returnValue(activities));

                oneOf(activityDTO).getId();
                will(returnValue(5L));

                oneOf(commentIdsByActivityIdDAO).execute(5L);
                will(returnValue(commentIds));

                oneOf(commentsByIdDAO).execute(commentIds);
                will(returnValue(comments));

                oneOf(commentDeletableSetter).execute(accountId, activityDTO, comments);

                allowing(filterMock).filter(activities, accountId);
                will(returnValue(activities));

                oneOf(activityDTO).setComments(comments);
            }
        });

        // call sut method.
        assertSame(activityDTO, sut.execute(buildActionContext()));
        context.assertIsSatisfied();
    }

    /**
     * Perform execute.
     * 
     * @throws Exception
     *             on failure.
     */
    @Test
    @SuppressWarnings("unchecked")
    public final void executeTestNoResults() throws Exception
    {
        // result list of activityDTOs.
        final ArrayList<ActivityDTO> activities = new ArrayList<ActivityDTO>();

        // result list of comment ids.
        final List<Long> commentIds = new ArrayList<Long>(2);
        commentIds.add(1L);
        commentIds.add(2L);

        // result list of comments.
        final List<CommentDTO> comments = new ArrayList(2);
        comments.add(context.mock(CommentDTO.class, "comment1"));
        comments.add(context.mock(CommentDTO.class, "comment2"));

        context.checking(new Expectations()
        {
            {
                oneOf(activityDAO).execute(with(any(ArrayList.class)), with(any(String.class)));
                will(returnValue(activities));

                allowing(filterMock).filter(activities, accountId);
                will(returnValue(activities));
            }
        });

        // call sut method.
        assertNull(sut.execute(buildActionContext()));
        context.assertIsSatisfied();
    }

    /**
     * Build the principal action context for testing.
     * 
     * @return the principal action context for testing
     */
    private PrincipalActionContext buildActionContext()
    {
        return new PrincipalActionContext()
        {
            private static final long serialVersionUID = 2653778274776584672L;

            @Override
            public Principal getPrincipal()
            {
                return new Principal()
                {
                    private static final long serialVersionUID = -447861595917953702L;

                    @Override
                    public String getAccountId()
                    {
                        return accountId;
                    }

                    @Override
                    public Long getId()
                    {
                        return null;
                    }

                    @Override
                    public String getOpenSocialId()
                    {
                        return null;
                    }
                };
            }

            @Override
            public Serializable getParams()
            {
                return null;
            }

            @Override
            public Map<String, Object> getState()
            {
                return null;
            }

            @Override
            public String getActionId()
            {
                return null;
            }

            @Override
            public void setActionId(final String inActionId)
            {

            }
        };
    }
}
