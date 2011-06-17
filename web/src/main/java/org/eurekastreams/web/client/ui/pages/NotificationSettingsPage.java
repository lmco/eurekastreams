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
package org.eurekastreams.web.client.ui.pages;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.common.dialog.BaseDialogContent;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;
import org.eurekastreams.web.client.ui.common.notification.NotificationSettingsWidget;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Page providing a launch spot for the notification settings dialog.
 */
public class NotificationSettingsPage extends SimplePanel
{
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onAttach()
    {
        super.onAttach();
        final DialogContent dialogContent = new BaseDialogContent()
        {
            private NotificationSettingsWidget settingsWidget;

            {
                settingsWidget = new NotificationSettingsWidget(false);
                settingsWidget.setCloseCommand(new Command()
                {
                    public void execute()
                    {
                        close();
                    }
                });
            }

            public String getTitle()
            {
                return "Notification Settings";
            }

            public Widget getBody()
            {
                return settingsWidget;
            }
        };
        Dialog newDialog = new Dialog(dialogContent)
        {
            /**
             * When the dialog is closed, go to the start page.
             */
            @Override
            public void hide()
            {
                super.hide();
                EventBus.getInstance().notifyObservers(new UpdateHistoryEvent(new CreateUrlRequest(Page.START)));
            }
        };
        newDialog.showCentered();
    }
}
