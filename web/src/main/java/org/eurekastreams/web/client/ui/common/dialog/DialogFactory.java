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

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ui.DefaultCommand;
import org.eurekastreams.commons.client.ui.WidgetCommand;
import org.eurekastreams.commons.client.ui.WidgetController;
import org.eurekastreams.commons.client.ui.WidgetFactory;
import org.eurekastreams.web.client.ui.common.dialog.login.LoginDialogContent;

/**
 * Used to obtain controllera for the dialog and commands.
 */
public final class DialogFactory implements WidgetFactory
{
    /**
     * The dialog to use.
     */
    private Dialog dialog = null;

    /**
     * The dialog controller.
     */
    private DialogController controller = null;

    /**
     * Creates the factory.
     *
     * @param inDialog
     *            the dialog for use in the factory.
     */
    public DialogFactory(final Dialog inDialog)
    {
        dialog = inDialog;
        controller = new DialogController(dialog, this);
    }

    /**
     * Returns a command.
     *
     * @param inCommandType
     *            the type of command.
     * @return the requested command.
     */
    public WidgetCommand getCommand(final String inCommandType)
    {
        if (inCommandType.equals("showCenteredModalDialog"))
        {
            return controller.new ShowCenteredModalDialog(dialog);
        }
        else if (inCommandType.equals("hideModalDialog"))
        {
            return controller.new HideModalDialog(dialog);
        }

        return new DefaultCommand();
    }

    /**
     * Gets the controller.
     *
     * @return the controller.
     */
    public WidgetController getController()
    {
        return controller;
    }

    /**
     * Gets a type of dialog.
     *
     * @param inDialogType
     *            the type of dialog requested.
     * @param inProcessor
     *            the action processor.
     * @return the requested dialog.
     */
    public static WidgetCommand getDialog(final String inDialogType, final ActionProcessor inProcessor)
    {
        Dialog dialog = null;

        if (inDialogType.equals("login"))
        {
            dialog = new Dialog(new LoginDialogContent());
        }

        DialogFactory factory = new DialogFactory(dialog);

        return factory.getCommand("showCenteredModalDialog");
    }
}
