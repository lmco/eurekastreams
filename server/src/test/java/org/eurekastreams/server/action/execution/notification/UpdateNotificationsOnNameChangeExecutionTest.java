/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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

import static org.junit.Assert.assertTrue;

import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.persistence.mappers.db.UpdateNotificationsOnNameChangeMapper;
import org.eurekastreams.server.persistence.mappers.requests.UpdateNotificationsOnNameChangeRequest;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Tests for UpdateNotificationsOnNameChangeExecution class.
 * 
 */
public class UpdateNotificationsOnNameChangeExecutionTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * {@link UpdateNotificationsOnNameChangeRequestGenerator}.
     */
    private UpdateNotificationsOnNameChangeRequestGenerator requestGenerator = context
            .mock(UpdateNotificationsOnNameChangeRequestGenerator.class);

    /**
     * {@link UpdateNotificationsOnNameChangeMapper}.
     */
    private UpdateNotificationsOnNameChangeMapper notificationUpdater = context
            .mock(UpdateNotificationsOnNameChangeMapper.class);

    /**
     * {@link UpdateNotificationsOnNameChangeRequest}.
     */
    private UpdateNotificationsOnNameChangeRequest request = context.mock(UpdateNotificationsOnNameChangeRequest.class);

    /**
     * System under test.
     */
    private UpdateNotificationsOnNameChangeExecution sut = new UpdateNotificationsOnNameChangeExecution(
            requestGenerator, notificationUpdater);

    /**
     * {@link ActionContext}.
     */
    private ActionContext actionContext = context.mock(ActionContext.class);

    /**
     * Id used for test.
     */
    private Long id = 5L;

    /**
     * Test.
     */
    @Test
    public void test()
    {
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(id));

                allowing(requestGenerator).getUpdateNotificationsOnNameChangeRequest(id);
                will(returnValue(request));

                allowing(notificationUpdater).execute(request);
            }
        });

        assertTrue(sut.execute(actionContext));
        context.assertIsSatisfied();
    }

}
