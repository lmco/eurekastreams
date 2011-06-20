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
import static org.junit.Assert.assertSame;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eurekastreams.commons.actions.ValidationStrategy;
import org.eurekastreams.commons.actions.context.ClientPrincipalActionContext;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.server.action.request.notification.SendPrebuiltNotificationRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests SendPrebuiltNotificationValidation.
 */
public class SendPrebuiltNotificationValidationTest
{
    /** Test data. */
    private static final String CLIENT_ID = "CLIENT_ID";

    /** Test data. */
    private static final String RECIPIENT_ACCOUNT_ID = "jdoe";

    /** Test data. */
    private static final String MESSAGE = "This is a message";

    /** Test data. */
    private static final String URL = "http://www.eurekastreams.org";

    /** Max length. */
    private static final int MAX_MESSAGE_LENGTH = 250;

    /** Max length. */
    private static final int MAX_URL_LENGTH = 2048;

    /** Used for mocking objects. */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Mapper to get recipient id. */
    private final DomainMapper<String, PersonModelView> personMapper = context.mock(DomainMapper.class);

    /** Mock person. */
    private final PersonModelView person = context.mock(PersonModelView.class);

    /** SUT. */
    private ValidationStrategy sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new SendPrebuiltNotificationValidation(personMapper, MAX_MESSAGE_LENGTH, MAX_URL_LENGTH);
    }

    /**
     * Sets up for an acceptable recipient.
     */
    private void setupOkRecipientMapping()
    {
        context.checking(new Expectations()
        {
            {
                allowing(personMapper).execute(RECIPIENT_ACCOUNT_ID);
                will(returnValue(person));
                allowing(person).isAccountLocked();
                will(returnValue(false));
            }
        });
    }

    /**
     * Tests validate.
     */
    @Test
    public void testValidateOkNoUrl()
    {
        setupOkRecipientMapping();

        ClientPrincipalActionContext ctx = runTest(MESSAGE, null);

        assertSame(person, ctx.getState().get("recipient"));
    }

    /**
     * Tests validate.
     */
    @Test
    public void testValidateOkWithUrl()
    {
        setupOkRecipientMapping();

        ClientPrincipalActionContext ctx = runTest(MESSAGE, URL);

        assertSame(person, ctx.getState().get("recipient"));
    }

    /**
     * Tests validate.
     */
    @Test(expected = ValidationException.class)
    public void testValidateBadPersonUnknown()
    {
        context.checking(new Expectations()
        {
            {
                allowing(personMapper).execute(RECIPIENT_ACCOUNT_ID);
                will(returnValue(null));
            }
        });

        runTest(MESSAGE, URL, "recipientAccountId");
    }

    /**
     * Tests validate.
     */
    @Test(expected = ValidationException.class)
    public void testValidateBadPersonLocked()
    {
        context.checking(new Expectations()
        {
            {
                allowing(personMapper).execute(RECIPIENT_ACCOUNT_ID);
                will(returnValue(person));
                allowing(person).isAccountLocked();
                will(returnValue(true));
            }
        });
        runTest(MESSAGE, URL, "recipientAccountId");
    }

    /**
     * Tests validate.
     */
    @Test(expected = ValidationException.class)
    public void testValidateMessageTooLong()
    {
        setupOkRecipientMapping();

        runTest(StringUtils.repeat("123456789012345678901234567890", 9), null, "message");
    }

    /**
     * Tests validate.
     */
    @Test(expected = ValidationException.class)
    public void testValidateMessageNull()
    {
        setupOkRecipientMapping();

        runTest(null, null, "message");
    }

    /**
     * Tests validate.
     */
    @Test(expected = ValidationException.class)
    public void testValidateMessageEmpty()
    {
        setupOkRecipientMapping();

        runTest("", null, "message");
    }

    /**
     * Tests validate.
     */
    @Test(expected = ValidationException.class)
    public void testValidateUrlTooLong()
    {
        setupOkRecipientMapping();

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
     * @return The context.
     */
    private ClientPrincipalActionContext runTest(final String message, final String url, final String... errorKeys)
    {
        ClientPrincipalActionContext ctx = new ClientPrincipalActionContext()
        {
            private final Map<String, Object> state = new HashMap<String, Object>();

            @Override
            public void setActionId(final String inActionId)
            {
            }

            @Override
            public Map<String, Object> getState()
            {
                return state;
            }

            @Override
            public Serializable getParams()
            {
                return new SendPrebuiltNotificationRequest(true, RECIPIENT_ACCOUNT_ID, message, url);
            }

            @Override
            public String getActionId()
            {
                return null;
            }

            @Override
            public Principal getPrincipal()
            {
                return null;
            }

            @Override
            public String getClientUniqueId()
            {
                return CLIENT_ID;
            }
        };

        try
        {
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

        return ctx;
    }
}
