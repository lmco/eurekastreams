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
package org.eurekastreams.web.client.ui.common.dialog;

import org.eurekastreams.web.client.events.PreDialogHideEvent;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Implementation of Dialog.
 */
public class Dialog implements DialogContentHost
{
    /** The actual dialog widget. */
    private final PopupPanel popupPanel;

    /** The dialog's content. */
    private DialogContent dialogContent = null;

    /**
     * Default constructor.
     *
     * @param inDialogContent
     *            The content of the dialog.
     */
    public Dialog(final DialogContent inDialogContent)
    {
        dialogContent = inDialogContent;

        final Dialog thisBuffered = this;

        popupPanel = new PopupPanel(false, false)
        {
            @Override
            protected void onPreviewNativeEvent(final com.google.gwt.user.client.Event.NativePreviewEvent event)
            {
                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ESCAPE)
                {
                    thisBuffered.hide();
                }
                super.onPreviewNativeEvent(event);
            };
        };
        popupPanel.setGlassEnabled(true);
        popupPanel.setGlassStyleName(StaticResourceBundle.INSTANCE.coreCss().modalBg());

        FlowPanel modalPanel = new FlowPanel();
        final String cssName = dialogContent.getCssName();
        if (cssName != null && !cssName.isEmpty())
        {
            modalPanel.addStyleName(cssName);
        }
        modalPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().modal());

        // title panel
        FlowPanel titlePanel = new FlowPanel();
        titlePanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().modalTitle());

        Label closeButton = new Label("Close");
        closeButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().modalClose());
        titlePanel.add(closeButton);
        closeButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent inEvent)
            {
                hide();
            }
        });

        Label title = new Label(dialogContent.getTitle());
        title.addStyleName(StaticResourceBundle.INSTANCE.coreCss().modalTitleText());
        titlePanel.add(title);

        modalPanel.add(titlePanel);

        // Content Panel
        FlowPanel bodyContainer = new FlowPanel();
        bodyContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().modalContentPanel());
        bodyContainer.add(dialogContent.getBody());
        modalPanel.add(bodyContainer);

        popupPanel.setWidget(modalPanel);

        dialogContent.setHost(this);
    }

    /**
     * Actions to take when the dialog should be hidden.
     */
    public void hide()
    {
        Session.getInstance().getEventBus().notifyObservers(new PreDialogHideEvent(this));
        dialogContent.beforeHide();
        popupPanel.hide();
    }

    /**
     * Show the dialog.
     */
    @Deprecated
    public void showUncentered()
    {
        popupPanel.show();
        dialogContent.show();
    }

    /**
     * Shows the modal centered.
     */
    public void showCentered()
    {
        popupPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().hidden());
        popupPanel.show();
        dialogContent.show();
        Scheduler.get().scheduleDeferred(new ScheduledCommand()
        {
            public void execute()
            {
                center();
                popupPanel.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().hidden());
                dialogContent.afterShow();
            }
        });
    }

    /**
     * Centers the modal for fixed positioning.
     */
    public void center()
    {
        int offsetTop = (Window.getClientHeight() - popupPanel.getElement().getScrollHeight()) / 2;
        int offsetLeft = (Window.getClientWidth() - popupPanel.getElement().getScrollWidth()) / 2;
        popupPanel.setPopupPosition(offsetLeft, offsetTop);
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
     * @return The PopupPanel used to implement the dialog.
     */
    protected PopupPanel getPopupPanel()
    {
        return popupPanel;
    }

    /**
     * Adds a style to the dialog panel.
     *
     * @param styleName
     *            CSS style class name.
     */
    public void addStyleName(final String styleName)
    {
        popupPanel.addStyleName(styleName);
    }

    /**
     * Show a dialog.
     *
     * @param dialogContent
     *            the content.
     */
    public static void showCentered(final DialogContent dialogContent)
    {
        Dialog newDialog = new Dialog(dialogContent);
        newDialog.showCentered();
    }
}
