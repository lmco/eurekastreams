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

import org.eurekastreams.commons.client.ui.WidgetCommand;
import org.eurekastreams.web.client.AnonymousClassInterceptor;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.ui.Session;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Tests the login controller.
 */
public class LoginControllerTest
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
     * The submit button.
     */
    private Hyperlink submitButtonMock = null;

    /**
     * The cancel button.
     */
    private Hyperlink cancelButtonMock = null;

    /**
     * The login form.
     */
    private FormPanel loginFormMock = null;

    /**
     * The password text box.
     */
    private PasswordTextBox passwordMock = null;

    /**
     * The error label.
     */
    private Label errorMessageMock = null;

    /**
     * The username text box.
     */
    private TextBox usernameMock = null;

    /**
     * Mock remember me checkbox.
     */
    private CheckBox rememberMeMock = null;

    /**
     * Mock WidgetCommand.
     */
    private WidgetCommand widgetCmdMock = context.mock(WidgetCommand.class);

    /**
     * Intercepts the login click listener.
     */
    final AnonymousClassInterceptor<ClickListener> loginClickInt = new AnonymousClassInterceptor<ClickListener>();

    /**
     * Intercepts the close click listener.
     */
    final AnonymousClassInterceptor<ClickListener> closeDialogClickInt = new AnonymousClassInterceptor<ClickListener>();

    /**
     * Intercepts the keyboard listener (for closing on escape, submit on
     * enter).
     */
    final AnonymousClassInterceptor<KeyboardListenerAdapter> keyboardInt =
        new AnonymousClassInterceptor<KeyboardListenerAdapter>();

    /**
     * Intercepts the form handler.
     */
    final AnonymousClassInterceptor<FormHandler> formHandlerInt = new AnonymousClassInterceptor<FormHandler>();

    /**
     * Intercepts show command.
     */
    final AnonymousClassInterceptor<WidgetCommand> showCommandInt = new AnonymousClassInterceptor<WidgetCommand>();

    /**
     * The event bus.
     */
    private EventBus eventBusMock = context.mock(EventBus.class);

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

        submitButtonMock = context.mock(Hyperlink.class);
        cancelButtonMock = submitButtonMock;
        passwordMock = context.mock(PasswordTextBox.class);
        usernameMock = context.mock(TextBox.class);
        loginFormMock = context.mock(FormPanel.class);
        errorMessageMock = context.mock(Label.class);
        rememberMeMock = context.mock(CheckBox.class);

        /* set the view expections */
        context.checking(new Expectations()
        {
            {
                oneOf(submitButtonMock).addClickListener(
                        with(any(ClickListener.class)));
                will(loginClickInt);

                oneOf(cancelButtonMock).addClickListener(
                        with(any(ClickListener.class)));
                will(closeDialogClickInt);

                oneOf(passwordMock).addKeyboardListener(
                        with(any(KeyboardListenerAdapter.class)));
                will(keyboardInt);

                oneOf(rememberMeMock).addKeyboardListener(
                        with(any(KeyboardListenerAdapter.class)));
                will(keyboardInt);

                oneOf(loginFormMock).addFormHandler(
                        with(any(FormHandler.class)));
                will(formHandlerInt);

                oneOf(loginContentMock).setShowCommand(
                        with(any(WidgetCommand.class)));
                will(showCommandInt);

                oneOf(errorMessageMock).setVisible(false);
            }
        });

        sut = new LoginDialogController(loginContentMock, eventBusMock, sessionMock);
        sut.submitButton = submitButtonMock;
        sut.cancelButton = cancelButtonMock;
        sut.password = passwordMock;
        sut.username = usernameMock;
        sut.loginForm = loginFormMock;
        sut.errorMessage = errorMessageMock;
        sut.rememberMe =  rememberMeMock;

    }

    /**
     * Tests the controller initialization.
     */
    @Test
    public final void testInit()
    {
        sut.init();

        context.assertIsSatisfied();
    }

    /**
     * Tests the show command.
     */
    @Test
    public final void testShowCommand()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(usernameMock).setText("");
                oneOf(passwordMock).setText("");
                oneOf(errorMessageMock).setText("");
                oneOf(errorMessageMock).setVisible(false);
                oneOf(usernameMock).setFocus(true);
            }
        });

        sut.init();

        WidgetCommand showCommand = showCommandInt.getObject();
        showCommand.execute();

        context.assertIsSatisfied();
    }

    /**
     * Tests the login click listener.
     */
    @Test
    public final void testLoginClick()
    {
        /* set the view expections */
        context.checking(new Expectations()
        {
            {
                oneOf(loginFormMock).submit();
            }
        });

        sut.init();

        ClickListener loginListener = loginClickInt.getObject();
        loginListener.onClick(null);

        context.assertIsSatisfied();
    }

    /**
     * Tests the cancel click listener.
     */
    @Test
    public final void testCancelClick()
    {
        /* set the view expections */
        context.checking(new Expectations()
        {
            {
                oneOf(loginContentMock).close();
            }
        });

        sut.init();

        ClickListener closeListener = closeDialogClickInt.getObject();
        closeListener.onClick(null);

        context.assertIsSatisfied();
    }

    /**
     * Tests the keyboard listener successfully.
     */
    @Test
    public final void testKeyboardListenerSuccess()
    {
        /* set the view expections */
        context.checking(new Expectations()
        {
            {
                oneOf(loginFormMock).submit();
            }
        });

        sut.init();

        KeyboardListenerAdapter kbListener = keyboardInt.getObject();
        // Enter submits the form.
        kbListener
                .onKeyPress(null, (char) KeyboardListenerAdapter.KEY_ENTER, 0);

        context.assertIsSatisfied();
    }

    /**
     * Tests the keyboard listener unsuccessfully.
     */
    @Test
    public final void testKeyboardListenerFailure()
    {
        final WidgetCommand loginCommand = widgetCmdMock;

        /* set the view expections */
        context.checking(new Expectations()
        {
            {
                never(loginCommand).execute();
            }
        });

        sut.init();

        KeyboardListenerAdapter kbListener = keyboardInt.getObject();
        // press del instead of enter.
        kbListener.onKeyPress(null, (char) KeyboardListenerAdapter.KEY_DELETE,
                0);

        context.assertIsSatisfied();
    }

    /**
     * Tests the form handler.
     */
    @Test
    public final void testFormHandler()
    {
        final FormSubmitCompleteEvent subComEvent = context
                .mock(FormSubmitCompleteEvent.class);

        /* set the view expections */
        context.checking(new Expectations()
        {
            {
                oneOf(subComEvent).getResults();
                will(returnValue("unknown"));

                oneOf(errorMessageMock).setText(with(any(String.class)));
                oneOf(errorMessageMock).setVisible(true);
            }
        });

        sut.init();

        FormHandler handler = formHandlerInt.getObject();
        handler.onSubmit(null);
        handler.onSubmitComplete(subComEvent);

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
