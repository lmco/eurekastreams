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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.eurekastreams.commons.actions.context.async.AsyncActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.service.actions.strategies.EmailerFactory;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the {@link CreateEmailNotificationExecution} class.
 *
 */
@SuppressWarnings("serial")
public class CreateEmailNotificationExecutionTest
{
    /**
     * System under test.
     */
    private CreateEmailNotificationExecution sut;

    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: For sending email. */
    private EmailerFactory emailer = context.mock(EmailerFactory.class);

    /** Fixture: For creating the content of the email for each kind of notification. */
    private NotificationEmailBuilder builder = context.mock(NotificationEmailBuilder.class);

    /** Fixture: For creating the content of the email for each kind of notification. */
    private Map<NotificationType, NotificationEmailBuilder> builders =
            new HashMap<NotificationType, NotificationEmailBuilder>()
            {
                {
                    put(NOTIFICATION_TYPE, builder);
                }
            };

    /** Fixture: message. */
    private MimeMessage message = context.mock(MimeMessage.class);

    /** Test data. */
    private static final long RECIPIENT_ID = 1111L;

    /** Test data. */
    private static final long ACTOR_ID = 2222L;

    /** Test data. */
    private static final long DESTINATION_ID = 3333L;

    /** Test data. */
    private static final long ACTIVITY_ID = 4444L;

    /** Test data. */
    private static final NotificationType NOTIFICATION_TYPE = NotificationType.POST_TO_PERSONAL_STREAM;

    /** Fixture: notification to send. */
    private NotificationDTO notification =
            new NotificationDTO(Arrays.asList(RECIPIENT_ID), NOTIFICATION_TYPE, ACTOR_ID, DESTINATION_ID,
                    EntityType.PERSON, ACTIVITY_ID);


    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new CreateEmailNotificationExecution(emailer, builders);

    }

     /**
     * Test notifying.
     *
     * @throws Exception
     *             On test failure.
     */
    @Test
    public void testNotify() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(emailer).createMessage();
                will(returnValue(message));

                oneOf(builder).build(with(same(notification)), with(same(message)));

                oneOf(emailer).sendMail(with(same(message)));
            }
        });

        AsyncActionContext currentContext = new AsyncActionContext(notification);
        sut.execute(currentContext);
        context.assertIsSatisfied();
    }

    /**
     * Test notifying.
     *
     * @throws Exception
     *             On test failure.
     */
    @Test(expected = ExecutionException.class)
    public void testNotifyWithMissingBuilder() throws Exception
    {
        NotificationDTO notif =
                new NotificationDTO(Arrays.asList(RECIPIENT_ID), NotificationType.FOLLOW_PERSON, ACTOR_ID,
                        DESTINATION_ID, EntityType.PERSON, ACTIVITY_ID);

        AsyncActionContext currentContext = new AsyncActionContext(notif);
        sut.execute(currentContext);
    }

    /**
     * Test notifying.
     *
     * @throws Exception
     *             On test failure.
     */
    @Test(expected = ExecutionException.class)
    public void testNotifyWithBuildError() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(emailer).createMessage();
                will(returnValue(message));

                oneOf(builder).build(with(same(notification)), with(same(message)));
                will(throwException(new Exception("Can't send")));
            }
        });

        AsyncActionContext currentContext = new AsyncActionContext(notification);
        sut.execute(currentContext);
        context.assertIsSatisfied();
    }

    /**
     * Test notifying.
     *
     * @throws Exception
     *             On test failure.
     */
    @Test(expected = ExecutionException.class)
    public void testNotifyWithSendError() throws Exception
    {
        context.checking(new Expectations()
        {
            {
                oneOf(emailer).createMessage();
                will(returnValue(message));

                oneOf(builder).build(with(same(notification)), with(same(message)));

                oneOf(emailer).sendMail(with(same(message)));
                will(throwException(new Exception("Can't send")));
            }
        });

        AsyncActionContext currentContext = new AsyncActionContext(notification);
        sut.execute(currentContext);
        context.assertIsSatisfied();
    }
}
