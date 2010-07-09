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
package org.eurekastreams.web.client.ui.common.dialog.login;

import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.FormLoginCompleteEvent;
import org.eurekastreams.web.client.ui.Session;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.Label;

/**
 * Tests the login controller when a login is attempted.
 */
public class LoginControllerLoginTest
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
     * The login content mock.
     */
    private LoginDialogContent loginContentMock = null;

    /**
     * The controller to test.
     */
    private LoginDialogController sut = null;

    /**
     * The event bus.
     */
    private EventBus eventBusMock = context.mock(EventBus.class);

    /**
     * The error label.
     */
    private Label errorMessageMock = null;

    /**
     * Session mock.
     */
    private Session sessionMock = context.mock(Session.class);

    /**
     * Pre-test initialization.
     */
    @Before
    public final void setUp()
    {
        GWTMockUtilities.disarm();
        loginContentMock = context.mock(LoginDialogContent.class);
        errorMessageMock = context.mock(Label.class);

        sut = new LoginDialogController(loginContentMock, eventBusMock, sessionMock);
        sut.errorMessage = errorMessageMock;
    }

    /**
     * Test completed login with empty username.
     */
    @Test
    public final void testLoginEmptyUsername()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(errorMessageMock).setText(with(any(String.class)));
                oneOf(errorMessageMock).setVisible(true);
            }
        });

        sut.loginComplete("Reason: Empty username not allowed");

        context.assertIsSatisfied();
    }

    /**
     * Test completed login user not found.
     */
    @Test
    public final void testLoginUserNotFound()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(errorMessageMock).setText(with(any(String.class)));
                oneOf(errorMessageMock).setVisible(true);
            }
        });

        sut.loginComplete("not found in directory");

        context.assertIsSatisfied();
    }

    /**
     * Test completed login for bad credentials.
     */
    @Test
    public final void testLoginBadCredentials()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(errorMessageMock).setText(with(any(String.class)));
                oneOf(errorMessageMock).setVisible(true);
            }
        });

        sut.loginComplete("Reason: Bad credentials");

        context.assertIsSatisfied();
    }

    /**
     * Test completed login for a user that doesn't exist.
     */
    @Test
    public final void testLoginUserDoesNotExist()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(errorMessageMock).setText(with(any(String.class)));
                oneOf(errorMessageMock).setVisible(true);
            }
        });

        sut.loginComplete("User not registered in system");

        context.assertIsSatisfied();
    }

    /**
     * Test completed login for a successful login.
     */
    @Test
    public final void testLoginSuccess()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(sut.errorMessage).setVisible(false);
                oneOf(loginContentMock).close();
                oneOf(eventBusMock).notifyObservers(with(any(FormLoginCompleteEvent.class)));
            }
        });

        sut.loginComplete("LOGIN_SUCCESS");

        context.assertIsSatisfied();
    }

    /**
     * Test completed login for an unknown response.
     */
    @Test
    public final void testLoginUnknwon()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(errorMessageMock).setText(with(any(String.class)));
                oneOf(errorMessageMock).setVisible(true);
            }
        });

        sut.loginComplete("unkown string -- this is unknown...");

        context.assertIsSatisfied();
    }

    /**
     * Post-test tear down.
     */
    @After
    public final void tearDown()
    {
        GWTMockUtilities.restore();
    }
}
