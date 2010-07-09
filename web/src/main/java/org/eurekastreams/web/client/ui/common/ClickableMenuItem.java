/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;

/**
 * This MenuItem subclass accepts multiple ClickListeners. Note that because of its parent, we can't do JUnit tests on
 * this class.
 */
public class ClickableMenuItem extends MenuItem
{
    /**
     * Constructor.
     * 
     * @param text
     *            the item's text
     * @param senderWidget
     *            the widget to act on when clicked.
     */
    public ClickableMenuItem(final String text, final Widget senderWidget)
    {
        super(text, new ClickListenerCommand(senderWidget));
    }

    /**
     * Constructor.
     * 
     * @param text
     *            the item's text
     * @param asHTML
     *            true to treat the specified text as HTML
     * @param senderWidget
     *            the widget to act on when clicked.
     */
    public ClickableMenuItem(final String text, final boolean asHTML, final Widget senderWidget)
    {
        super(text, asHTML, new ClickListenerCommand(senderWidget));
    }

    /**
     * Override the command setter to make sure we're getting a ClickListenerCommand.
     * 
     * @param cmd
     *            the command to be executed when the item is clicked
     */
    public void setCommand(final Command cmd)
    {
        if (cmd instanceof ClickListenerCommand)
        {
            super.setCommand((ClickListenerCommand) cmd);
        }
        else
        {
            throw new IllegalArgumentException("ClickableMenuItem.setCommand() must be given a ClickListenerCommand");
        }
    }

    /**
     * Add another ClickListener to this menu item.
     * 
     * @param listener
     *            the listener to add.
     */
    public void addClickListener(final ClickListener listener)
    {
        ClickListenerCommand command = (ClickListenerCommand) getCommand();
        command.addClickListener(listener);
    }

    /**
     * Remove a ClickListener from this menu item.
     * 
     * @param listener
     *            the listener to remove
     */
    public void removeClickListener(final ClickListener listener)
    {
        ClickListenerCommand command = (ClickListenerCommand) getCommand();
        command.removeClickListener(listener);
    }
}
