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
import org.eurekastreams.commons.client.ui.WidgetController;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.FormLoginCompleteEvent;
import org.eurekastreams.web.client.ui.Bindable;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * The controller for a LoginContentFacade.
 */
public class LoginDialogController implements WidgetController, Bindable
{

    /**
     * The login content to control.
     */
    private LoginDialogContent content = null;

    /**
     * The login form.
     */
    FormPanel loginForm;

    /**
     * The error label.
     */
    Label errorMessage;

    /**
     * The submit button.
     */
    Hyperlink submitButton;

    /**
     * The cancel button.
     */
    Hyperlink cancelButton;

    /**
     * The username text box.
     */
    TextBox username;

    /**
     * The password text box.
     */
    PasswordTextBox password;

    /**
     * Remember me checkbox.
     */
    CheckBox rememberMe;

    /**
     * The session.
     */
    private Session session;

    /**
     * The event bus.
     */
    private EventBus eventBus;

    /**
     * Default constructor.
     *
     * @param inContent
     *            the login content to use.
     * @param inEventBus
     *            the event bus.
     * @param inSession
     *            the session.
     */
    public LoginDialogController(final LoginDialogContent inContent, final EventBus inEventBus, final Session inSession)
    {
        content = inContent;
        eventBus = inEventBus;
        session = inSession;
    }

    /**
     * Initalize the controller.
     */
    public void init()
    {
        content.setShowCommand(new WidgetCommand()
        {

            public void execute()
            {
                username.setText("");
                password.setText("");
                displayError("");
                username.setFocus(true);
            }

        });

        submitButton.addClickListener(new ClickListener()
        {
            public void onClick(final Widget arg0)
            {
                loginForm.submit();
            }
        });

        KeyboardListener onEnterPressListener = new KeyboardListenerAdapter()
        {
            /**
             * Calls submit when enter is pressed.
             *
             * @param sender
             *            the events sender.
             * @param keyCode
             *            the key that was pressed.
             * @param modifiers
             *            other keys pressed.
             */
            @Override
            public void onKeyPress(final Widget sender, final char keyCode, final int modifiers)
            {
                if ((keyCode == KEY_ENTER) && (modifiers == 0))
                {
                    loginForm.submit();
                }
            }
        };

        password.addKeyboardListener(onEnterPressListener);
        rememberMe.addKeyboardListener(onEnterPressListener);

        cancelButton.addClickListener(new ClickListener()
        {
            public void onClick(final Widget arg0)
            {
                content.close();
            }
        });

        loginForm.addFormHandler(new FormHandler()
        {

            /**
             * Does nothing, will perhaps validate in the future.
             *
             * @param arg0
             *            not used.
             */
            public void onSubmit(final FormSubmitEvent arg0)
            {
            }

            /**
             * Reacts to authentication response.
             *
             * @param arg0
             *            response.
             */
            public void onSubmitComplete(final FormSubmitCompleteEvent arg0)
            {
                loginComplete(arg0.getResults());
            }

        });

        // Initially hide error message
        errorMessage.setVisible(false);
    }

    /**
     * Determines the appropriate response to the completed login message.
     *
     * @param formReturnValue
     *            the value returned from the login service
     */
    public void loginComplete(final String formReturnValue)
    {
        /*
         * TODO Seems to be a bug in GWT here. String.compareTo didn't work right, always returned false even when
         * values were apparently the same.
         */
        if (formReturnValue.contains("LOGIN_SUCCESS"))
        {
            errorMessage.setVisible(false);
            content.close();
            eventBus.notifyObservers(new FormLoginCompleteEvent());
        }
        else if (formReturnValue.contains("LOGIN_DISABLED"))
        {
            errorMessage.setVisible(false);
            content.close();
            Window.Location.assign("/requestaccess");
        }
        else
        {
            displayError("The user name or password you entered was incorrect.");
        }
    }

    /**
     * Displays an error.
     *
     * @param error
     *            the error to display.
     */
    public void displayError(final String error)
    {
        errorMessage.setText(error);
        errorMessage.setVisible(error.length() > 0);
    }
}
