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
package org.eurekastreams.server.action.validation.notification;

import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.async.AsyncActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.execution.notification.translator.CommentTranslator;
import org.eurekastreams.server.action.execution.notification.translator.FollowPersonTranslator;
import org.eurekastreams.server.action.execution.notification.translator.NotificationTranslator;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * This class tests the {@link CreateNotificationsValidation} class.
 *
 */
public class CreateNotificationsValidationTest
{
    /**
     * System under test.
     */
    private ValidationStrategy<ActionContext> sut;

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
     * Mock comment translator.
     */
    private final CommentTranslator commentTranslator = context.mock(CommentTranslator.class);

    /**
     * Mock follower translator.
     */
    private final FollowPersonTranslator followerTranslator = context.mock(FollowPersonTranslator.class);

    /**
     * This method prepares the sut.
     */
    @Before
    public void setup()
    {
        Map<RequestType, NotificationTranslator> translators = new HashMap<RequestType, NotificationTranslator>();
        translators.put(RequestType.FOLLOW_PERSON, followerTranslator);
        translators.put(RequestType.COMMENT, commentTranslator);

        sut = new CreateNotificationsValidation(translators);
    }

    /**
     * Tests performAction with exception.
     */
    @Test
    public void testValidateValid()
    {
        sut.validate(new AsyncActionContext(new CreateNotificationsRequest(RequestType.FOLLOW_PERSON, 0)));
    }

    /**
     * Tests performAction with exception.
     */
    @Test(expected = ValidationException.class)
    public void testValidateWithInvalidTranslatorType()
    {
        // Intentionally left out STREAM_POST from translators to be able to test this.
        sut.validate(new AsyncActionContext(new CreateNotificationsRequest(RequestType.POST_PERSON_STREAM, 0)));
    }
}
