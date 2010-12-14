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
package org.eurekastreams.web.client.ui.common.dialog.message;

import org.eurekastreams.commons.client.ui.WidgetCommand;
import org.eurekastreams.web.client.ui.Bindable;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Template for a login dialog.
 */
public class MessageDialogContent implements DialogContent, Bindable
{
    /**
     * The close command.
     */
    private WidgetCommand closeCommand = null;

    /**
     * The show command.
     */
    private WidgetCommand showCommand = null;

    /**
     * The message.
     */
    private Widget body;

    /**
     * The message.
     */
    private String title;

    /**
     * Default constructor. Builds up widgets.
     * 
     * @param inTitle
     *            the title.
     * @param inBody
     *            the message.
     */
    @SuppressWarnings("static-access")
    public MessageDialogContent(final String inTitle, final Widget inBody)
    {
        title = inTitle;
        body = inBody;
    }

    /**
     * Shortcut constructor for a simple string.
     * 
     * @param inTitle
     *            the title.
     * @param inMessage
     *            the message.
     */
    public MessageDialogContent(final String inTitle, final String inMessage)
    {
        final Label messageLabel = new Label(inMessage);
        messageLabel.addStyleName("dialog-message-text");

        title = inTitle;
        body = messageLabel;
    }

    /**
     * The title of the login dialog.
     * 
     * @return the title.
     */
    public final String getTitle()
    {
        return title;
    }

    /**
     * The login form.
     * 
     * @return the login form.
     */
    public final Widget getBody()
    {
        return body;
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
        if (showCommand != null)
        {
            showCommand.execute();
        }
    }
}
