/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.notification.SendPrebuiltNotificationRequest;
import org.eurekastreams.server.testing.TestContextCreator;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests SendMassPrebuiltNotificationValidation.
 */
public class SendMassPrebuiltNotificationValidationTest
{
    /** Test data. */
    private static final String CLIENT_ID = "CLIENT_ID";

    /** Test data. */
    private static final String RECIPIENT_ACCOUNT_ID = "jdoe";

    /** Test data. */
    private static final String MESSAGE = "This is a message";

    /** Test data. */
    private static final String URL = "http://www.eurekastreams.org";

    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** SUT. */
    private ValidationStrategy sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new SendMassPrebuiltNotificationValidation();
    }

    /**
     * Tests validate.
     */
    @Test
    public void testValidateOkNoUrl()
    {
        runTest(MESSAGE, null);
    }

    /**
     * Tests validate.
     */
    @Test
    public void testValidateOkWithUrl()
    {
        runTest(MESSAGE, URL);
    }

    /**
     * Tests validate.
     */
    @Test(expected = ValidationException.class)
    public void testValidateMessageTooLong()
    {
        runTest(StringUtils.repeat("123456789012345678901234567890", 9), null, "message");
    }

    /**
     * Tests validate.
     */
    @Test(expected = ValidationException.class)
    public void testValidateMessageNull()
    {
        runTest(null, null, "message");
    }

    /**
     * Tests validate.
     */
    @Test(expected = ValidationException.class)
    public void testValidateMessageEmpty()
    {
        runTest("", null, "message");
    }

    /**
     * Tests validate.
     */
    @Test(expected = ValidationException.class)
    public void testValidateUrlTooLong()
    {
        runTest(MESSAGE, URL + StringUtils.repeat("/123456789012345678901234567890", 9 * 9), "url");
    }

    /**
     * Runs the test and validates results.
     *
     * @param message
     *            Message to use.
     * @param url
     *            URL to use.
     * @param errorKeys
     *            Fields to expect in the validation error.
     */
    private void runTest(final String message, final String url, final String... errorKeys)
    {
        try
        {

            Serializable params = new SendPrebuiltNotificationRequest(true, null, message, url);
            PrincipalActionContext ctx = TestContextCreator.createPrincipalActionContext(params, null);
            sut.validate(ctx);
        }
        catch (ValidationException ex)
        {
            assertEquals(errorKeys.length, ex.getErrors().size());
            for (int i = 0; i < errorKeys.length; i++)
            {
                String errorKey = errorKeys[i];
                assertNotNull("Expected error for field " + errorKey, ex.getErrors().get(errorKey));
            }
            throw ex;
        }

        context.assertIsSatisfied();
    }
}
