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

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.stream.BulkActivitiesMapper;
import org.eurekastreams.server.persistence.strategies.ActivityDeletePropertyStrategy;
import org.eurekastreams.server.service.security.userdetails.ExtendedUserDetails;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for DeleteActivityAuthorization class.
 * 
 */
public class DeleteActivityAuthorizationTest
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
     * DAO for looking up activity by id.
     */
    private BulkActivitiesMapper activityDAO = context.mock(BulkActivitiesMapper.class);

    /**
     * Strategy for setting Deletable property on CommentDTOs.
     */
    private ActivityDeletePropertyStrategy activityDeletableSetter = context.mock(ActivityDeletePropertyStrategy.class);

    /**
     * User info mock.
     */
    private ExtendedUserDetails userInfo = context.mock(ExtendedUserDetails.class);

    /**
     * Activity id.
     */
    private Long activityId = 5L;

    /**
     * user acctounId.
     */
    private String userAcctId = "smithers";

    /**
     * List of activityId.
     */
    private ArrayList<Long> activityIds = new ArrayList<Long>()
    {
        {
            add(activityId);
        }

    };

    /**
     * ActivityDTO mock.
     */
    private ActivityDTO activity = context.mock(ActivityDTO.class);

    /**
     * List of activityDTO.
     */
    private ArrayList<ActivityDTO> activities = new ArrayList<ActivityDTO>()
    {
        {
            add(activity);
        }
    };

    /**
     * {@link PrincipalActionContext}.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * {@link Principal}.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * System under test.
     */
    private DeleteActivityAuthorization sut;

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new DeleteActivityAuthorization(activityDAO, activityDeletableSetter);
    }

    /**
     * Test authorize method.
     * 
     * @throws Exception
     *             on error.
     */
    @Test
    public void testAuthorizeSuccess() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(activityId));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getAccountId();
                will(returnValue(userAcctId));

                oneOf(activityDAO).execute(activityIds, null);
                will(returnValue(activities));

                oneOf(activityDeletableSetter).execute(userAcctId, activity);

                oneOf(activity).isDeletable();
                will(returnValue(true));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test authorize method.
     * 
     * @throws Exception
     *             on error.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeFail() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(activityId));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getAccountId();
                will(returnValue(userAcctId));

                oneOf(activityDAO).execute(activityIds, null);
                will(returnValue(activities));

                oneOf(activityDeletableSetter).execute(userAcctId, activity);

                oneOf(activity).isDeletable();
                will(returnValue(false));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test authorize method, comment lookup fail.
     */
    @Test(expected = AuthorizationException.class)
    public void testActivityLookupFail()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(activityId));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getAccountId();
                will(returnValue(userAcctId));

                oneOf(activityDAO).execute(activityIds, null);
                will(returnValue(new ArrayList<ActivityDTO>(0)));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test authorize method, activity lookup fail.
     * 
     * @throws Exception
     *             on error
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizationProcessFail() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(activityId));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getAccountId();
                will(throwException(new Exception()));

                oneOf(activityDAO).execute(activityIds, null);
                will(returnValue(activities));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

}
