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
package org.eurekastreams.web.client.ui.common.dialog;

import org.eurekastreams.commons.client.ui.WidgetCommand;

/**
 * Base dialog content. Allows boilerplate code to not need to be reimplemented.
 */
public abstract class BaseDialogContent implements DialogContent
{
    /** Close command. */
    WidgetCommand closeCommand;

    /**
     * Injects the command to call to close the dialog.
     * 
     * @param inCommand
     *            the close command.
     */
    public void setCloseCommand(final WidgetCommand inCommand)
    {
        closeCommand = inCommand;
    }

    /**
     * Closes the dialog via the close command.
     */
    public void close()
    {
        if (closeCommand != null)
        {
            closeCommand.execute();
        }
    }

    /**
     * Provides a do-nothing implementation of show for dialogs which do not need to take any action on show.
     */
    public void show()
    {
    }
}
