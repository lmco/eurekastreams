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
import org.eurekastreams.web.client.ui.Bindable;
import org.eurekastreams.web.client.ui.PropertyMapper;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Template for a login dialog.
 */
public class LoginDialogContent implements DialogContent, Bindable
{
    /**
     * The login form.
     */
    FormPanel loginForm;

    /**
     * The close command.
     */
    private WidgetCommand closeCommand = null;

    /**
     * The show command.
     */
    private WidgetCommand showCommand = null;

    /**
     * The error label.
     */
    Label errorMessage = new Label("");

    /**
     * The submit button.
     */
    Hyperlink submitButton = new Hyperlink("Submit", History.getToken());

    /**
     * The cancel button.
     */
    Hyperlink cancelButton = new Hyperlink("Cancel", History.getToken());

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
     * Container for login elements.
     */
    private FlowPanel loginContentContainer;

    /**
     * Default constructor. Builds up widgets.
     */
    @SuppressWarnings("static-access")
    public LoginDialogContent()
    {
        // Setup the widgets.

        loginForm = new FormPanel();
        loginForm.setAction("/j_spring_security_check");
        loginForm.setMethod(loginForm.METHOD_POST);

        loginContentContainer = new FlowPanel();
        loginContentContainer.addStyleName("login-content-container");

        FlowPanel loginPanel = new FlowPanel();
        loginPanel.addStyleName("login-content");
        loginForm.setWidget(loginPanel);

        FlowPanel navPanel = new FlowPanel();
        navPanel.addStyleName("login-nav-panel");
        navPanel.add(errorMessage);
        loginContentContainer.add(navPanel);

        submitButton.addStyleName("login-button");
        cancelButton.addStyleName("cancel-button");

        final FlowPanel usernamePanel = new FlowPanel();
        usernamePanel.addStyleName("form-element");
        Label usernameLabel = new Label("Username: ");
        usernameLabel.addStyleName("form-label");
        usernamePanel.add(usernameLabel);

        username = new TextBox();
        username.setName("j_username");
        usernamePanel.add(username);

        loginPanel.add(usernamePanel);

        final FlowPanel passwordPanel = new FlowPanel();
        Label passwordLabel = new Label("Password: ");
        passwordPanel.addStyleName("form-element");
        passwordLabel.addStyleName("form-label");
        passwordPanel.add(passwordLabel);

        password = new PasswordTextBox();
        password.setName("j_password");
        passwordPanel.add(password);

        Hidden returnTo = new Hidden("spring-security-redirect", "/login.html");
        returnTo.setName("spring-security-redirect");
        loginPanel.add(returnTo);

        rememberMe = new CheckBox("Keep me logged in.");
        rememberMe.setName("_spring_security_remember_me");

        loginPanel.add(passwordPanel);
        loginPanel.add(rememberMe);
        errorMessage.addStyleName("form-error-box");

        loginContentContainer.add(loginPanel);

        FlowPanel buttonPanel = new FlowPanel();
        buttonPanel.addStyleName("login-button-panel");
        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);
        loginContentContainer.add(buttonPanel);

        loginForm.add(loginContentContainer);

        // Create and initialize the controller.
        WidgetController controller = new LoginDialogController(this, EventBus
                .getInstance(), Session.getInstance());

        PropertyMapper mapper = new PropertyMapper(GWT
                .create(LoginDialogContent.class), GWT
                .create(LoginDialogController.class));

        mapper.bind(this, (Bindable) controller);

        controller.init();
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
     * The command to call to close the dialog.
     *
     * @param command
     *            the close command.
     */
    public void setCloseCommand(final WidgetCommand command)
    {
        closeCommand = command;
    }

    /**
     * The CSS class to use for this dialog.
     *
     * @return the name of the CSS class to use.
     */
    public String getCssName()
    {
        return "login-dialog";
    }

    /**
     * Call the close command.
     */
    public void close()
    {
        if (closeCommand != null)
        {
            closeCommand.execute();
        }
    }

    /**
     * Sets the show command.
     *
     * @param inShowCommand
     *            the command to use.
     */
    public void setShowCommand(final WidgetCommand inShowCommand)
    {
        showCommand = inShowCommand;
    }

    /**
     * Provides a hook to fire off events when the dialog is shown.
     */
    public void show()
    {
        showCommand.execute();
    }
}
