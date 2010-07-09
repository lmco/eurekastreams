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
package org.eurekastreams.web.client.ui.common.dialog;


import org.eurekastreams.commons.client.ui.WidgetCommand;

import com.google.gwt.user.client.ui.ClickListener;

/**
 * Interface for a Dialog. A dialog is a modal pop-up.
 */
public interface DialogFacade
{
    /**
     * Show the dialog.
     */
    void show();

    /**
     * Hide the dialog.
     */
    void hide();

    /**
     * Center the dialog.
     */
    void center();

    /**
     * Add an event to the close button.
     *
     * @param listener
     *            a click listener for the button.
     */
    void addCloseButtonListener(ClickListener listener);

    /**
     * Sets the command to fire on the escape event.
     *
     * @param command
     *            the command.
     */
    void setEscapeCommand(WidgetCommand command);

    /**
     * Toggles the background.
     *
     * @param visible
     *            true to show, false to hide.
     */
    void setBgVisible(boolean visible);

    /**
     * Returns the content.
     *
     * @return the dialog's content.
     */
    DialogContent getContent();
}
