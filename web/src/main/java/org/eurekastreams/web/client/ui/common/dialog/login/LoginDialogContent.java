/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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

import org.eurekastreams.web.client.events.FormLoginCompleteEvent;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.BaseDialogContent;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Template for a login dialog.
 */
public class LoginDialogContent extends BaseDialogContent
{
    /** The login form. */
    private final FormPanel loginForm = new FormPanel();

    /** The error label. */
    private final Label errorMessage = new Label("");

    /** The submit button. */
    private final Hyperlink submitButton = new Hyperlink("Submit", History.getToken());

    /** The cancel button. */
    private final Hyperlink cancelButton = new Hyperlink("Cancel", History.getToken());

    /** The user name text box. */
    private final TextBox username = new TextBox();

    /** The password text box. */
    private final PasswordTextBox password = new PasswordTextBox();

    /** Container for login elements. */
    private final FlowPanel loginContentContainer = new FlowPanel();

    /*
     * Note: Current approach to handling the problem where a user keeps the app up in the background for a long
     * duration during which the session expires (since we no longer do any polling when the app goes inactive) and thus
     * renders the app dead is to *always* use the "remember me" (persistent logon) feature. (Always for form-based
     * logon; not applicable with pre-auth.) Leaving the checkbox commented out for now, since it is highly conceivable
     * we'll come up with a different approach.
     */
    // /** Remember me checkbox. */
    // private final CheckBox rememberMe = new CheckBox("Keep me logged in.");

    /**
     * Default constructor. Builds up widgets.
     */
    public LoginDialogContent()
    {
        setupWidgets();
        setupEvents();
    }

    /**
     * Builds the UI.
     */
    private void setupWidgets()
    {
        loginForm.setAction("/j_spring_security_check");
        loginForm.setMethod(FormPanel.METHOD_POST);

        loginContentContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().loginContentContainer());

        FlowPanel loginPanel = new FlowPanel();
        loginPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().loginContent());
        loginForm.setWidget(loginPanel);

        FlowPanel navPanel = new FlowPanel();
        navPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().loginNavPanel());
        navPanel.add(errorMessage);
        loginContentContainer.add(navPanel);

        submitButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().loginButton());
        cancelButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().cancelButton());

        final FlowPanel usernamePanel = new FlowPanel();
        usernamePanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formElement());
        Label usernameLabel = new Label("Username: ");
        usernameLabel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formLabel());
        usernamePanel.add(usernameLabel);

        username.setName("j_username");
        usernamePanel.add(username);

        loginPanel.add(usernamePanel);

        final FlowPanel passwordPanel = new FlowPanel();
        Label passwordLabel = new Label("Password: ");
        passwordPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formElement());
        passwordLabel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formLabel());
        passwordPanel.add(passwordLabel);

        password.setName("j_password");
        passwordPanel.add(password);

        Hidden returnTo = new Hidden("spring-security-redirect", "/login.html");
        returnTo.setName("spring-security-redirect");
        loginPanel.add(returnTo);

        loginPanel.add(passwordPanel);
        // rememberMe.setName("_spring_security_remember_me");
        // loginPanel.add(rememberMe);
        Hidden usePersistentLogon = new Hidden("_spring_security_remember_me", "on");
        usePersistentLogon.setName("_spring_security_remember_me");
        loginPanel.add(usePersistentLogon);

        errorMessage.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formErrorBox());
        errorMessage.setVisible(false);

        loginContentContainer.add(loginPanel);

        FlowPanel buttonPanel = new FlowPanel();
        buttonPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().loginButtonPanel());
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        loginContentContainer.add(buttonPanel);

        loginForm.add(loginContentContainer);
    }

    /**
     * Wires up events.
     */
    private void setupEvents()
    {
        submitButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent inArg0)
            {
                loginForm.submit();
            }
        });

        KeyUpHandler enterKeyHandler = new KeyUpHandler()
        {
            public void onKeyUp(final KeyUpEvent ev)
            {
                if (ev.getNativeKeyCode() == KeyCodes.KEY_ENTER && !ev.isAnyModifierKeyDown())
                {
                    loginForm.submit();
                }
            }
        };
        username.addKeyUpHandler(enterKeyHandler);
        password.addKeyUpHandler(enterKeyHandler);
        // rememberMe.addKeyUpHandler(enterKeyHandler);

        cancelButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent inArg0)
            {
                close();
            }
        });

        loginForm.addSubmitCompleteHandler(new SubmitCompleteHandler()
        {
            public void onSubmitComplete(final SubmitCompleteEvent ev)
            {
                // result ends (or may end) with a newline, so check using startsWith
                String formReturnValue = ev.getResults();
                if (formReturnValue.startsWith("LOGIN_SUCCESS"))
                {
                    errorMessage.setVisible(false);
                    close();
                    Session.getInstance().getEventBus().notifyObservers(new FormLoginCompleteEvent());
                }
                else if (formReturnValue.startsWith("LOGIN_DISABLED"))
                {
                    errorMessage.setVisible(false);
                    close();
                    Window.Location.assign("/requestaccess.html");
                }
                else
                {
                    errorMessage.setText("The user name or password you entered was incorrect.");
                    errorMessage.setVisible(true);
                }
            }
        });
    }

    /**
     * The title of the login dialog.
     *
     * @return the title.
     */
    public final String getTitle()
    {
        return "Login";
    }

    /**
     * The login form.
     *
     * @return the login form.
     */
    public final Widget getBody()
    {
        return loginForm;
    }

    /**
     * The CSS class to use for this dialog.
     *
     * @return the name of the CSS class to use.
     */
    public String getCssName()
    {
        return StaticResourceBundle.INSTANCE.coreCss().loginDialog();
    }

    /**
     * Provides a hook to fire off events when the dialog is shown.
     */
    @Override
    public void show()
    {
        username.setText("");
        password.setText("");
        errorMessage.setVisible(false);
        username.setFocus(true);
    }
}
