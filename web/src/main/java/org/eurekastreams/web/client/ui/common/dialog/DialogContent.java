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

import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog content.
 */
public interface DialogContent
{
    /**
     * The title of the dialog.
     *
     * @return the title of the dialog.
     */
    String getTitle();

    /**
     * The dialogs body widget.
     *
     * @return the dialogs body widget.
     */
    Widget getBody();

    /**
     * The command to call to close the dialog.
     *
     * @param command
     *            the close command.
     */
    void setCloseCommand(WidgetCommand command);

    /**
     * Call the close command.
     */
    void close();

    /**
     * Returns the CSS class name to use for this dialog.
     *
     * @return the name of the CSS class.
     */
    String getCssName();

    /**
     * Provides a hook to fire off events when the dialog is shown.
     */
    void show();
}
