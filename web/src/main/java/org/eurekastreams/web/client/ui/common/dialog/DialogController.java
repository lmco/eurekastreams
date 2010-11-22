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
import org.eurekastreams.commons.client.ui.WidgetController;
import org.eurekastreams.commons.client.ui.WidgetFactory;
import org.eurekastreams.web.client.events.PreDialogHideEvent;
import org.eurekastreams.web.client.ui.Session;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog Controller.
 */
final class DialogController implements WidgetController
{
    /**
     * The dialog to control.
     */
    private Dialog dialog = null;

    /**
     * Widget factory.
     */
    private final WidgetFactory factory;

    /**
     * Constructor.
     *
     * @param inDialog
     *            the dialog to control.
     * @param inFactory
     *            factory used to obtain commands.
     */
    public DialogController(final Dialog inDialog, final WidgetFactory inFactory)
    {
        this.dialog = inDialog;
        this.factory = inFactory;
    }

    /**
     * Initialize the controller.
     */
    public void init()
    {
        final WidgetCommand closeDialog = factory.getCommand("hideModalDialog");

        dialog.addCloseButtonListener(new ClickListener()
        {
            public void onClick(final Widget arg0)
            {
                closeDialog.execute();
            }

        });

        // Close the dialog when escape is pressed.
        dialog.setEscapeCommand(closeDialog);
    }

    /**
     * Shows a centered dialog.
     */
    public class ShowCenteredModalDialog implements WidgetCommand
    {
        /**
         * The dialog to show.
         */
        private Dialog dialog = null;

        /**
         * Default constructor. Initializes command with dialog instance.
         *
         * @param inDialog
         *            the dialog.
         */
        public ShowCenteredModalDialog(final Dialog inDialog)
        {
            this.dialog = inDialog;
        }

        /**
         * Shows the dialog.
         */
        public void execute()
        {
            dialog.setBgVisible(true);
            dialog.show();
            dialog.center();
            dialog.getContent().show();
        }

    }

    /**
     * Hides a dialog.
     */
    public class HideModalDialog implements WidgetCommand
    {

        /**
         * The dialog to hide.
         */
        private Dialog dialog = null;

        /**
         * Default constructor. Initializes command with dialog instance.
         *
         * @param inDialog
         *            the dialog.
         */
        public HideModalDialog(final Dialog inDialog)
        {
            this.dialog = inDialog;
        }

        /**
         * Hides the dialog.
         */
        public void execute()
        {
            Session.getInstance().getEventBus().notifyObservers(new PreDialogHideEvent(dialog));
            dialog.hide();
            dialog.setBgVisible(false);
        }
    }

    /**
     * Get the close command.
     *
     * @return the close command.
     */
    public WidgetCommand getCloseCommand()
    {
        return factory.getCommand("hideModalDialog");
    }

}
