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
package org.eurekastreams.server.action.authorization.stream;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.strategies.CommentDeletePropertyStrategy;
import org.eurekastreams.server.search.modelview.CommentDTO;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for CommentModificationAuthorization.
 * 
 */
@SuppressWarnings("serial")
public class CommentModificationAuthorizationTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * DAO for looking up commentDTO.
     */
    private DomainMapper<List<Long>, List<CommentDTO>> commentDAO = context.mock(DomainMapper.class, "commentDAO");

    /**
     * DAO for looking up activity by id.
     */
    private DomainMapper<List<Long>, List<ActivityDTO>> activityDAO = context.mock(DomainMapper.class);

    /**
     * Strategy for setting Deletable property on CommentDTOs.
     */
    private CommentDeletePropertyStrategy commentDeletableSetter = context.mock(CommentDeletePropertyStrategy.class);;

    /**
     * Person object mock.
     */
    private Person user = context.mock(Person.class);

    /**
     * Comment id.
     */
    private Long commentId = 5L;

    /**
     * Comment's parent activity id.
     */
    private final long parentActivityId = 100L;

    /**
     * user acctounId.
     */
    private String userAcctId = "smithers";

    /**
     * {@link PrincipalActionContext}.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * {@link Principal}.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * CommentDTO mock.
     */
    private CommentDTO comment = context.mock(CommentDTO.class);

    /**
     * List of commentId.
     */
    private ArrayList<Long> commentIds = new ArrayList<Long>()
    {
        {
            add(commentId);
        }
    };

    /**
     * List of commentDTO.
     */
    private ArrayList<CommentDTO> comments = new ArrayList<CommentDTO>()
    {
        {
            add(comment);
        }
    };

    /**
     * List of activity id.
     */
    private ArrayList<Long> activityIds = new ArrayList<Long>()
    {
        {
            add(parentActivityId);
        }
    };

    /**
     * ActivityDTO mock.
     */
    private ActivityDTO parentActivity = context.mock(ActivityDTO.class);

    /**
     * List of activityDTO.
     */
    private ArrayList<ActivityDTO> activities = new ArrayList<ActivityDTO>()
    {
        {
            add(parentActivity);
        }
    };

    /**
     * System under test.
     */
    private CommentModificationAuthorization sut;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new CommentModificationAuthorization(commentDAO, activityDAO, commentDeletableSetter);
    }

    /**
     * Test authorize method, user is comment author.
     */
    @Test
    public void testAuthorizeSuccess()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(commentId));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getAccountId();
                will(returnValue(userAcctId));

                oneOf(comment).getActivityId();
                will(returnValue(parentActivityId));

                oneOf(activityDAO).execute(activityIds);
                will(returnValue(activities));

                oneOf(commentDAO).execute(commentIds);
                will(returnValue(comments));

                oneOf(commentDeletableSetter).execute(userAcctId, parentActivity, comments);

                oneOf(comment).isDeletable();
                will(returnValue(true));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test authorize method, user is comment author.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeFail()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(commentId));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getAccountId();
                will(returnValue(userAcctId));

                oneOf(comment).getActivityId();
                will(returnValue(parentActivityId));

                oneOf(activityDAO).execute(activityIds);
                will(returnValue(activities));

                oneOf(commentDAO).execute(commentIds);
                will(returnValue(comments));

                oneOf(commentDeletableSetter).execute(userAcctId, parentActivity, comments);

                oneOf(comment).isDeletable();
                will(returnValue(false));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test authorize method, comment lookup fail.
     */
    @SuppressWarnings("unchecked")
    @Test(expected = AuthorizationException.class)
    public void testCommentLookupFail()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(commentId));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getAccountId();
                will(returnValue(userAcctId));

                oneOf(comment).getActivityId();
                will(returnValue(parentActivityId));

                oneOf(activityDAO).execute(activityIds);
                will(returnValue(activities));

                oneOf(commentDAO).execute(commentIds);
                will(returnValue(new ArrayList(0)));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test authorize method, activity lookup fail.
     */
    @SuppressWarnings("unchecked")
    @Test(expected = AuthorizationException.class)
    public void testActivityLookupFail()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(commentId));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getAccountId();
                will(returnValue(userAcctId));

                allowing(comment).getActivityId();
                will(returnValue(parentActivityId));

                oneOf(comment).getId();
                will(returnValue(commentId));

                oneOf(commentDAO).execute(commentIds);
                will(returnValue(comments));

                oneOf(activityDAO).execute(activityIds);
                will(returnValue(new ArrayList(0)));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

}
