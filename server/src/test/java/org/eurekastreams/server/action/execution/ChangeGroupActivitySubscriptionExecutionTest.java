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

package org.eurekastreams.server.action.execution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.stream.ChangeGroupActivitySubscriptionRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.ChangeGroupActivitySubscriptionMapperRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for ChangeGroupActivitySubscriptionExecution.
 */
public class ChangeGroupActivitySubscriptionExecutionTest
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
     * Action contest passed into execution.
     */
    private final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Mapper to get a domain group id by its short name.
     */
    private final DomainMapper<List<String>, List<Long>> groupIdsFromShortNamesMapper = context.mock(
            DomainMapper.class, "groupIdsFromShortNamesMapper");

    /**
     * Mapper to change a person's group new activity notification preference for a specific group.
     */
    private final ChangeNotificationPreferenceMapperFake changeNotificationPreferenceMapper // 
    = new ChangeNotificationPreferenceMapperFake();

    /**
     * System under test.
     */
    private final ChangeGroupActivitySubscriptionExecution sut = new ChangeGroupActivitySubscriptionExecution(
            groupIdsFromShortNamesMapper, changeNotificationPreferenceMapper);

    /**
     * Principal.
     */
    private final Principal principal = context.mock(Principal.class);

    /**
     * Test with invalid request type.
     */
    @Test(expected = ClassCastException.class)
    public void testInvalidRequestType()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue(new Long(2)));
            }
        });

        sut.execute(actionContext);
    }

    /**
     * Test with no group found.
     */
    @Test
    public void testWhenNoGroupFound()
    {
        final String groupShortName = "foobar";

        final ChangeGroupActivitySubscriptionRequest request = //
        new ChangeGroupActivitySubscriptionRequest(groupShortName, true);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                oneOf(groupIdsFromShortNamesMapper).execute(with(any(ArrayList.class)));
                will(returnValue(new ArrayList<Long>()));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getAccountId();
                will(returnValue("snuts"));
            }
        });

        assertFalse((Boolean) sut.execute(actionContext));

        context.assertIsSatisfied();
    }

    /**
     * Test success when should receive notifications.
     */
    @Test
    public void testSuccessOnTrue()
    {
        final String groupShortName = "foobar";
        final long personId = 2138L;
        final ArrayList<Long> groupIds = new ArrayList<Long>();
        final long groupId = 82L;
        final boolean shouldReceiveNotifications = true;

        groupIds.add(groupId);

        final ChangeGroupActivitySubscriptionRequest request = //
        new ChangeGroupActivitySubscriptionRequest(groupShortName, shouldReceiveNotifications);

        changeNotificationPreferenceMapper.execute(null);
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                oneOf(groupIdsFromShortNamesMapper).execute(with(any(ArrayList.class)));
                will(returnValue(groupIds));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(personId));

                allowing(principal).getAccountId();
                will(returnValue("snuts"));
            }
        });

        assertTrue((Boolean) sut.execute(actionContext));
        assertEquals(personId, changeNotificationPreferenceMapper.getRequest().getPersonId());
        assertEquals(groupId, changeNotificationPreferenceMapper.getRequest().getGroupId());
        assertEquals(shouldReceiveNotifications, changeNotificationPreferenceMapper.getRequest()
                .getReceiveNewActivityNotifications());

        context.assertIsSatisfied();
    }

    /**
     * Test success when should not receive notifications.
     */
    @Test
    public void testSuccessOnFalse()
    {
        final String groupShortName = "foobar";
        final long personId = 2138L;
        final ArrayList<Long> groupIds = new ArrayList<Long>();
        final long groupId = 82L;
        final boolean shouldReceiveNotifications = false;

        groupIds.add(groupId);

        final ChangeGroupActivitySubscriptionRequest request = //
        new ChangeGroupActivitySubscriptionRequest(groupShortName, shouldReceiveNotifications);

        changeNotificationPreferenceMapper.execute(null);
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(request));

                oneOf(groupIdsFromShortNamesMapper).execute(with(any(ArrayList.class)));
                will(returnValue(groupIds));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(personId));

                allowing(principal).getAccountId();
                will(returnValue("snuts"));
            }
        });

        assertTrue((Boolean) sut.execute(actionContext));
        assertEquals(personId, changeNotificationPreferenceMapper.getRequest().getPersonId());
        assertEquals(groupId, changeNotificationPreferenceMapper.getRequest().getGroupId());
        assertEquals(shouldReceiveNotifications, changeNotificationPreferenceMapper.getRequest()
                .getReceiveNewActivityNotifications());

        context.assertIsSatisfied();
    }

    /**
     * Fake ChangeNotificationPreferenceMapper to capture and report the request passed in.
     */
    private class ChangeNotificationPreferenceMapperFake implements
            DomainMapper<ChangeGroupActivitySubscriptionMapperRequest, Boolean>
    {
        /**
         * The request that was passed in.
         */
        private ChangeGroupActivitySubscriptionMapperRequest request;

        /**
         * Get the request that was passed in.
         * 
         * @return the request that was passed in
         */
        public ChangeGroupActivitySubscriptionMapperRequest getRequest()
        {
            return request;
        }

        /**
         * Store the input request and return true.
         * 
         * @param inRequest
         *            the request
         * @return true
         */
        @Override
        public Boolean execute(final ChangeGroupActivitySubscriptionMapperRequest inRequest)
        {
            request = inRequest;
            return true;
        }

    }
}
