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
package org.eurekastreams.server.action.execution.notification;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.ApplicationAlertNotification;
import org.eurekastreams.server.persistence.mappers.db.GetApplicationAlertsByUserId;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests execution strategy for GetApplicationAlerts.
 */
public class GetApplicationAlertsExecutionTest
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
     * System under test.
     */
    private GetApplicationAlertsExecution sut;

    /**
     * PrincipalActionContext mock.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Principal mock.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * Alert mapper mock.
     */
    private GetApplicationAlertsByUserId alertMapper = context.mock(GetApplicationAlertsByUserId.class);

    /**
     * Count of alerts.
     */
    private static final int COUNT = 25;

    /**
     * Sample alert.
     */
    private ApplicationAlertNotification alert1 = new ApplicationAlertNotification();

    /**
     * Sample alert.
     */
    private ApplicationAlertNotification alert2 = new ApplicationAlertNotification();

    /**
     * Sample alert.
     */
    private ApplicationAlertNotification alert3 = new ApplicationAlertNotification();

    /**
     * List of alerts.
     */
    private List<ApplicationAlertNotification> alerts = Arrays.asList(alert1, alert2, alert3);

    /**
     * Setup.
     */
    @Before
    public final void setUp()
    {
        sut = new GetApplicationAlertsExecution(alertMapper, COUNT);
    }

    /**
     * Tests execute method.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void testExecute()
    {
        final long userId = 123L;

        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(userId));

                oneOf(alertMapper).execute(userId, COUNT);
                will(returnValue(alerts));
            }
        });

        List<ApplicationAlertNotification> results = (List<ApplicationAlertNotification>) sut.execute(actionContext);
        assertEquals(3, results.size());
        context.assertIsSatisfied();
    }
}
