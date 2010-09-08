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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.execution.stream.ActivitySecurityTrimmer;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test for ViewActivityAuthorizationStrategy.
 */
public class ViewActivityAuthorizationStrategyTest
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
     * Security Trimmer.
     */
    private ActivitySecurityTrimmer securityTrimmer = context.mock(ActivitySecurityTrimmer.class);
    
    /**
     * {@link ServiceActionContext}.
     */
    private ServiceActionContext actionContext = context.mock(ServiceActionContext.class);

    /**
     * System under test.
     */
    private ViewActivityAuthorizationStrategy sut = new ViewActivityAuthorizationStrategy(securityTrimmer);

    /**
     * {@link Principal}.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * Activity id used in tests.
     */
    private Long activityId = 5L;

    /**
     * Principal id used in tests.
     */
    private Long principalId = 6L;

    /**
     * Test.
     */
    @Test
    public void testStreamIsPublic()
    {
        final List<Long> visibleIds = Arrays.asList(activityId);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getId();
                will(returnValue(principalId));

                allowing(actionContext).getParams();
                will(returnValue(activityId));

                allowing(securityTrimmer).trim(with(any(List.class)), with(principalId));
                will(returnValue(visibleIds));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test(expected = AuthorizationException.class)
    public void testStreamIsNotPublicUserNotInGroup()
    {
        final List<Long> visibleIds = new LinkedList<Long>();

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getId();
                will(returnValue(principalId));

                allowing(actionContext).getParams();
                will(returnValue(activityId));

                allowing(securityTrimmer).trim(with(any(List.class)), with(principalId));
                will(returnValue(visibleIds));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }
}
