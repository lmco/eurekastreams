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
package org.eurekastreams.server.action.execution.profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.AnonymousClassInterceptor;
import org.eurekastreams.server.action.execution.notification.Notifier;
import org.eurekastreams.server.action.request.profile.ReviewPendingGroupRequest;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.mappers.cache.AddPrivateGroupIdToCachedCoordinatorAccessList;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.security.userdetails.ExtendedUserDetails;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for ReviewPendingGroupExecution.
 */
public class ReviewPendingGroupExecutionTest
{
    /**
     * A shortname to pass into the params.
     */
    private static final String GROUP_SHORTNAME = "shortname";

    /** Test data. */
    private static final String GROUP_NAME = "Group Name";

    /** Test data. */
    private static final long GROUP_ID = 3425L;

    /**
     * System under test.
     */
    private ReviewPendingGroupExecution sut;

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
     * User info in the session.
     */
    private ExtendedUserDetails user = context.mock(ExtendedUserDetails.class);

    /**
     * Mocked group cache.
     */
    private DomainGroupMapper groupMapper = context.mock(DomainGroupMapper.class);

    /**
     * Mocked group.
     */
    private DomainGroup group = context.mock(DomainGroup.class);

    /**
     * Mock used to update privategroupcoordinatorcache.
     */
    private AddPrivateGroupIdToCachedCoordinatorAccessList addPrivateGroupIdToCachedListMock = context
            .mock(AddPrivateGroupIdToCachedCoordinatorAccessList.class);

    /**
     * A username for tests.
     */
    private String username = "username";

    /**
     * User's email address.
     */
    private String userEmailAddress = "username@example.com";

    /**
     * PersonModelView returned for the user.
     */
    private PersonModelView userModelView;

    /**
     * Mocked principal.
     */
    private Principal mockedPrincipal = context.mock(Principal.class);

    /** Fixture: email notifier. */
    private Notifier emailNotifier = context.mock(Notifier.class);

    /** Fixture: async request to send notification. */
    private UserActionRequest notifAsyncRequest = context.mock(UserActionRequest.class);

    /**
     * Mocked instance of the {@link Organization}.
     */
    private final Organization orgMock = context.mock(Organization.class);

    /** Fixture: Execution strategy for deleting a group. */
    private DeleteGroupFromDBExecution deleteGroupExecution = context.mock(DeleteGroupFromDBExecution.class);

    /**
     * Set up the SUT.
     * 
     * @throws MalformedURLException
     *             won't happen
     */
    @Before
    public void setup() throws MalformedURLException
    {
        sut = new ReviewPendingGroupExecution(groupMapper, emailNotifier, addPrivateGroupIdToCachedListMock,
                deleteGroupExecution);
    }

    /**
     * Test a valid case where the coordinator approves.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public void performActionApprovePublicGroupTest() throws Exception
    {
        final ReviewPendingGroupRequest request = new ReviewPendingGroupRequest(GROUP_SHORTNAME, true);

        setupCommonExpectations();
        context.checking(new Expectations()
        {
            {
                oneOf(group).setPending(false);

                oneOf(groupMapper).flush();

                oneOf(group).isPublicGroup();
                will(returnValue(true));
            }
        });

        List<UserActionRequest> asyncRequests = callExecute(request);
        context.assertIsSatisfied();
        assertEquals(1, asyncRequests.size());
        assertSame(notifAsyncRequest, asyncRequests.get(0));
    }

    /**
     * Test a valid case where the coordinator approves.
     * 
     * @throws Exception
     *             not expected
     */
    @SuppressWarnings("deprecation")
    @Test
    public void performActionApprovePrivateGroupTest() throws Exception
    {
        final ReviewPendingGroupRequest request = new ReviewPendingGroupRequest(GROUP_SHORTNAME, true);
        setupCommonExpectations();

        context.checking(new Expectations()
        {
            {
                oneOf(group).setPending(false);

                oneOf(groupMapper).flush();

                oneOf(group).isPublicGroup();
                will(returnValue(false));

                oneOf(addPrivateGroupIdToCachedListMock).execute(GROUP_ID);
            }
        });

        List<UserActionRequest> asyncRequests = callExecute(request);
        context.assertIsSatisfied();
        assertEquals(1, asyncRequests.size());
        assertSame(notifAsyncRequest, asyncRequests.get(0));
    }

    /**
     * Test a valid case where the coordinator denies the new group.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public void performActionDenyPrivateGroupTest() throws Exception
    {
        final ReviewPendingGroupRequest request = new ReviewPendingGroupRequest(GROUP_SHORTNAME, false);

        setupCommonExpectations();
        final AnonymousClassInterceptor<TaskHandlerActionContext<ActionContext>> intCtx = // \n
        new AnonymousClassInterceptor<TaskHandlerActionContext<ActionContext>>();
        context.checking(new Expectations()
        {
            {
                oneOf(deleteGroupExecution).execute(with(any(TaskHandlerActionContext.class)));
                will(intCtx);
            }
        });

        List<UserActionRequest> asyncRequests = callExecute(request);
        context.assertIsSatisfied();
        assertTrue(asyncRequests.size() >= 1);
        assertSame(notifAsyncRequest, asyncRequests.get(0));
        TaskHandlerActionContext<ActionContext> thac = intCtx.getObject();
        assertSame(asyncRequests, thac.getUserActionRequests());
        assertEquals(GROUP_ID, thac.getActionContext().getParams());
    }

    /**
     * Executes the SUT with the proper action context setup.
     * 
     * @param request
     *            The request to pass.
     * @return List with any async requests made by the SUT.
     */
    private List<UserActionRequest> callExecute(final ReviewPendingGroupRequest request)
    {
        List<UserActionRequest> asyncRequests = new ArrayList<UserActionRequest>();

        sut.execute(new TaskHandlerActionContext<PrincipalActionContext>(new PrincipalActionContext()
        {
            /**
             * Serial version uid.
             */
            private static final long serialVersionUID = -1644049510102329123L;

            @Override
            public Map<String, Object> getState()
            {
                return null;
            }

            @Override
            public Serializable getParams()
            {
                return request;
            }

            @Override
            public Principal getPrincipal()
            {
                return mockedPrincipal;
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
        }, asyncRequests));

        return asyncRequests;
    }

    /**
     * Set up expectations that are common to multiple tests.
     * 
     * @throws Exception
     *             Shouldn't.
     */
    private void setupCommonExpectations() throws Exception
    {
        final Set<Person> coordinators = new HashSet<Person>();
        final Person coordinator1 = context.mock(Person.class, "coord1");
        final Person coordinator2 = context.mock(Person.class, "coord2");
        coordinators.add(coordinator1);
        coordinators.add(coordinator2);

        userModelView = new PersonModelView();
        userModelView.setEmail(userEmailAddress);

        context.checking(new Expectations()
        {
            {
                allowing(mockedPrincipal).getAccountId();
                will(returnValue(username));

                allowing(groupMapper).findByShortName(GROUP_SHORTNAME);
                will(returnValue(group));

                allowing(group).getId();
                will(returnValue(GROUP_ID));

                allowing(group).getName();
                will(returnValue(GROUP_NAME));

                allowing(group).getShortName();
                will(returnValue(GROUP_SHORTNAME));

                allowing(group).getCoordinators();
                will(returnValue(coordinators));

                allowing(coordinator1).getId();
                will(returnValue(7L));
                allowing(coordinator2).getId();
                will(returnValue(8L));

                allowing(user).getPerson();

                oneOf(emailNotifier).notify(with(aNonNull(NotificationDTO.class)));
                will(returnValue(notifAsyncRequest));
            }
        });
    }
}
