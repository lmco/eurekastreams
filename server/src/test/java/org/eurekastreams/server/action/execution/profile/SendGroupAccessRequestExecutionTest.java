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

import static junit.framework.Assert.assertEquals;
import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.DomainGroupShortNameRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.action.request.profile.RequestForGroupMembershipRequest;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.mappers.db.InsertRequestForGroupMembership;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for SendGroupAccessRequestExecutionStrategy.
 */
public class SendGroupAccessRequestExecutionTest
{
    /** Test data. */
    private static final String USER_ACCOUNTID = "fordp";

    /** Test data. */
    private static final long USER_ID = 4000L;

    /** Test data. */
    private static final String GROUP_SHORTNAME = "thegroup";

    /** Test data. */
    private static final long GROUP_ID = 5000L;

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
     * The mock DomainGroupMapper to be used by the action.
     */
    private final DomainGroupMapper groupMapper = context.mock(DomainGroupMapper.class);

    /**
     * The mock DomainGroup to be used by the action.
     */
    private final DomainGroup groupMock = context.mock(DomainGroup.class);

    /** Mapper. */
    private InsertRequestForGroupMembership insertMembershipRequestMapper = context
            .mock(InsertRequestForGroupMembership.class);

    /**
     * Subject under test.
     */
    private SendGroupAccessRequestExecution sut = null;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new SendGroupAccessRequestExecution(groupMapper, insertMembershipRequestMapper);
    }

    /**
     * Call the execute method and make sure it produces what it should.
     * 
     * @throws Exception
     *             should not occur
     */
    @Test
    public final void testPerformAction() throws Exception
    {
        final RequestForGroupMembershipRequest mapperRqst = new RequestForGroupMembershipRequest(GROUP_ID, USER_ID);
        context.checking(new Expectations()
        {
            {
                allowing(groupMapper).findByShortName(GROUP_SHORTNAME);
                will(returnValue(groupMock));

                allowing(groupMock).getId();
                will(returnValue(GROUP_ID));

                oneOf(insertMembershipRequestMapper).execute(with(equalInternally(mapperRqst)));
            }
        });

        List<UserActionRequest> uaRequests = callExecute();
        assertEquals(1, uaRequests.size());

        UserActionRequest uaRequest = uaRequests.get(0);
        assertEquals("createNotificationsAction", uaRequest.getActionKey());
        CreateNotificationsRequest request = (CreateNotificationsRequest) uaRequest.getParams();
        assertEquals(RequestType.REQUEST_GROUP_ACCESS, request.getType());
        assertEquals(USER_ID, request.getActorId());
        assertEquals(GROUP_ID, request.getDestinationId());
        assertEquals(0L, request.getActivityId());
    }

    /**
     * Make sure that sending bad arguments results in the expected exception.
     * 
     * @throws Exception
     *             Throws an Exception.
     * 
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testPerformActionNoGroupError() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(groupMapper).findByShortName(GROUP_SHORTNAME);
                will(returnValue(null));
            }
        });

        List<UserActionRequest> requests = callExecute();
        assertEquals(0, requests.size());
    }

    /**
     * Executes the SUT with the proper action context setup.
     * 
     * @return List with any async requests made by the SUT.
     */
    private List<UserActionRequest> callExecute()
    {
        List<UserActionRequest> asyncRequests = new ArrayList<UserActionRequest>();

        sut.execute(new TaskHandlerActionContext<PrincipalActionContext>(new PrincipalActionContext()
        {
            public Map<String, Object> getState()
            {
                return null;
            }

            public Serializable getParams()
            {
                return new DomainGroupShortNameRequest(GROUP_SHORTNAME);
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

            public Principal getPrincipal()
            {
                return new Principal()
                {
                    public String getAccountId()
                    {
                        return USER_ACCOUNTID;
                    }

                    public Long getId()
                    {
                        return USER_ID;
                    }

                    public String getOpenSocialId()
                    {
                        return null;
                    }
                };
            }
        }, asyncRequests));

        context.assertIsSatisfied();

        return asyncRequests;
    }
}
