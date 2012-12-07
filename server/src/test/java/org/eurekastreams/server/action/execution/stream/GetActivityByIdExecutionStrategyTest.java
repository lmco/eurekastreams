/*
 * Copyright (c) 2010-2012 Lockheed Martin Corporation
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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.strategies.CommentDeletePropertyStrategy;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.activity.ActivityFilter;
import org.eurekastreams.server.testing.TestContextCreator;
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
    private final DomainMapper<List<Long>, List<ActivityDTO>> activityDAO = context.mock(DomainMapper.class);

    /**
     * Mapper to lookup a PersonModelView from an account id.
     */
    private final DomainMapper<String, PersonModelView> getPersonModelViewByAccountIdMapper = context.mock(
            DomainMapper.class, "getPersonModelViewByAccountIdMapper");

    /**
     * DAO for finding comment ids.
     */
    private final DomainMapper<Long, List<Long>> commentIdsByActivityIdDAO = context.mock(DomainMapper.class,
            "commentIdsByActivityIdDAO");

    /**
     * DAO for finding comments by id.
     */
    private final DomainMapper<List<Long>, List<CommentDTO>> commentsByIdDAO = context.mock(DomainMapper.class,
            "commentsByIdDAO");

    /**
     * ActivityDTO.
     */
    private final ActivityDTO activityDTO = context.mock(ActivityDTO.class);

    /**
     * Mock strategy for setting Deletable property on CommentDTOs.
     */
    private final CommentDeletePropertyStrategy commentDeletableSetter = context
            .mock(CommentDeletePropertyStrategy.class);

    /**
     * Filter Mock.
     */
    private final ActivityFilter filterMock = context.mock(ActivityFilter.class);

    /**
     * Setup text fixtures.
     */
    @Before
    public final void setUp()
    {
        List<ActivityFilter> filters = new LinkedList<ActivityFilter>();
        filters.add(filterMock);
        sut = new GetActivityByIdExecutionStrategy(activityDAO, commentIdsByActivityIdDAO, commentsByIdDAO,
                commentDeletableSetter, getPersonModelViewByAccountIdMapper, filters);
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

        final PersonModelView person = new PersonModelView();

        context.checking(new Expectations()
        {
            {
                oneOf(activityDAO).execute(with(any(ArrayList.class)));
                will(returnValue(activities));

                oneOf(activityDTO).getId();
                will(returnValue(5L));

                oneOf(commentIdsByActivityIdDAO).execute(5L);
                will(returnValue(commentIds));

                oneOf(commentsByIdDAO).execute(commentIds);
                will(returnValue(comments));

                oneOf(commentDeletableSetter).execute(accountId, activityDTO, comments);

                allowing(filterMock).filter(with(activities), with(any(PersonModelView.class)));

                oneOf(getPersonModelViewByAccountIdMapper).execute(accountId);
                will(returnValue(person));

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

        final PersonModelView person = new PersonModelView();

        context.checking(new Expectations()
        {
            {
                oneOf(activityDAO).execute(with(any(ArrayList.class)));
                will(returnValue(activities));

                allowing(filterMock).filter(with(activities), with(any(PersonModelView.class)));
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
        return TestContextCreator.createPrincipalActionContext(null, accountId, 0);
    }
}
