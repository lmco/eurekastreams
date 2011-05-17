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
package org.eurekastreams.server.action.execution.notification;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.execution.notification.idle.AsyncNotifier;
import org.eurekastreams.server.action.execution.notification.idle.NotificationDTO;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the async notifier.
 */
public class AsyncNotifierTest
{
    /** Test data. */
    private static final String ACTION_NAME = "TheAction";

    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** SUT. */
    private AsyncNotifier sut;

    /** Fixture: notification to send. */
    private NotificationDTO notification = context.mock(NotificationDTO.class);

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new AsyncNotifier(ACTION_NAME);
    }

    /**
     * Tests notifying.
     *
     * @throws Exception
     *             Shouldn't.
     */
    @Test
    public void testNotify() throws Exception
    {
        UserActionRequest rqst = sut.notify(notification);
        assertEquals(ACTION_NAME, rqst.getActionKey());
        NotificationDTO innerRqst = (NotificationDTO) rqst.getParams();
        assertSame(notification, innerRqst);
    }

}
