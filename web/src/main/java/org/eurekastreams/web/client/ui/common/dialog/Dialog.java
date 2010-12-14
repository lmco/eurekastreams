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

import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Implementation of Dialog.
 */
public class Dialog extends PopupPanel
{
    /**
     * The dialog controller.
     */
    private DialogController controller = null;

    /**
     * Close button.
     */
    private Anchor closeButton = new Anchor("Close");

    /**
     * The dialog's content.
     */
    private DialogContent dialogContent = null;

    /**
     * The title of the dialog.
     */
    private Label title = new Label();

    /**
     * Command executed on escape.
     */
    private WidgetCommand escapeCommand;

    /**
     * The transparent background..
     */
    private static Panel bgPanel = null;

    /**
     * The content panel.
     */
    private FlowPanel modalPanel = new FlowPanel();

    /**
     * Adds the transparent background for the dialog to the document.
     */
    private static void addBg()
    {
        if (bgPanel == null)
        {
            bgPanel = new FlowPanel();
            bgPanel.addStyleName("modal-bg");

            bgPanel.setVisible(false);

            RootPanel.get().add(bgPanel);
        }
    }

    /**
     * Default constructor.
     * 
     * @param content
     *            The content of the dialog.
     */
    public Dialog(final DialogContent content)
    {
        super(false, true);

        addBg();

        DialogFactory factory = new DialogFactory(this);
        controller = (DialogController) factory.getController();

        setContent(content);

        modalPanel.addStyleName("modal");
        setWidget(modalPanel);

        controller.init();
    }

    /**
     * Set the content.
     * 
     * @param inDialogContent
     *            the content.
     */
    public void setContent(final DialogContent inDialogContent)
    {
        dialogContent = inDialogContent;
        modalPanel.clear();

        dialogContent.setCloseCommand(controller.getCloseCommand());

        modalPanel.setStyleName(dialogContent.getCssName());

        // Title Panel
        FlowPanel titlePanel = new FlowPanel();
        titlePanel.addStyleName("modal-title");

        closeButton.addStyleName("modal-close");
        titlePanel.add(closeButton);

        title.setText(dialogContent.getTitle());
        titlePanel.add(title);

        modalPanel.add(titlePanel);

        // Content Panel
        FlowPanel bodyContainer = new FlowPanel();
        bodyContainer.addStyleName("modal-content-panel");
        bodyContainer.add(dialogContent.getBody());
        modalPanel.add(bodyContainer);
    }

    /**
     * Add an event to the close button.
     * 
     * @param listener
     *            a click listener for the button.
     */
    public void addCloseButtonListener(final ClickListener listener)
    {
        closeButton.addClickListener(listener);
    }

    /**
     * Adds a keyboard listener to the dialog.
     * 
     * @param inEscapeCommand
     *            the command to execute on escape.
     */
    public void setEscapeCommand(final WidgetCommand inEscapeCommand)
    {
        escapeCommand = inEscapeCommand;
    }

    /**
     * Fires escape command on escape key press.
     * 
     * @param event
     *            the relevant event.
     * @return defers to superclass.
     */
    @Override
    public boolean onEventPreview(final Event event)
    {
        if (event.getKeyCode() == KeyboardListenerAdapter.KEY_ESCAPE)
        {
            escapeCommand.execute();
        }

        return super.onEventPreview(event);
    }

    /**
     * Toggles the background.
     * 
     * @param visible
     *            true to show, false to hide.
     */
    public void setBgVisible(final boolean visible)
    {
        bgPanel.setVisible(visible);
    }

    /**
     * Show the dilaog.
     */
    @Override
    public void show()
    {
        super.show();
        dialogContent.show();
    }

    /**
     * Returns the content.
     * 
     * @return the dialog's content.
     */
    public DialogContent getContent()
    {
        return dialogContent;
    }

    /**
     * Centers the modal for fixed positioning, and shows it.
     */
    @Override
    public void center()
    {
        this.addStyleName("hidden");
        super.show();
        dialogContent.show();
        int offsetTop = (Window.getClientHeight() - this.getElement().getScrollHeight()) / 2;
        int offsetLeft = (Window.getClientWidth() - this.getElement().getScrollWidth()) / 2;
        this.setPopupPosition(offsetLeft, offsetTop);
        this.removeStyleName("hidden");
    }

    /**
     * Show a dialog.
     * 
     * @param dialogContent
     *            the content.
     */
    public static void showDialog(final DialogContent dialogContent)
    {
        Dialog newDialog = new Dialog(dialogContent);
        newDialog.setBgVisible(true);
        newDialog.center();
        newDialog.getContent().show();
    }
}
