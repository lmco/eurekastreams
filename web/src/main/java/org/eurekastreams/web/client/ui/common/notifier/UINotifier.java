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
package org.eurekastreams.web.client.ui.common.notifier;

import org.eurekastreams.web.client.events.HideNotificationEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.events.UpdatedHistoryParametersEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Notifier for the UI.
 */
public class UINotifier extends FlowPanel
{
    /**
     * The close button.
     */
    private Anchor closeButton = new Anchor(StaticResourceBundle.INSTANCE.coreCss().close());

    /**
     * The content panel.
     */
    FlowPanel contentPanel = new FlowPanel();

    /**
     * url parameter for forcing notifications to be shown.
     */
    public static final String NOTIFICATION_PARAM = "notification";
    
    /**
     * Default constructor.
     */
    public UINotifier()
    {
        this.setVisible(false);
        contentPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().message());

        this.add(contentPanel);

        this.setStyleName(StaticResourceBundle.INSTANCE.coreCss().notificationBar());
        closeButton.addStyleName(StaticResourceBundle.INSTANCE.coreCss().closeNotification());
        closeButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                Session.getInstance().getEventBus().notifyObservers(new HideNotificationEvent());
            }
        });

        Session.getInstance().getEventBus().addObserver(ShowNotificationEvent.class,
                new Observer<ShowNotificationEvent>()
                {
                    public void update(final ShowNotificationEvent event)
                    {
                        Window.scrollTo(0, 0);
                        contentPanel.clear();
                        contentPanel.add(event.getNotification().getWidget());
                        contentPanel.add(closeButton);
                        setVisible(true);
                    }
                });

        Session.getInstance().getEventBus().addObserver(HideNotificationEvent.class,
                new Observer<HideNotificationEvent>()
                {
                    public void update(final HideNotificationEvent event)
                    {
                        setVisible(false);
                    }
                });
        
        Session.getInstance().getEventBus().addObserver(UpdatedHistoryParametersEvent.class,
                new Observer<UpdatedHistoryParametersEvent>()
                {
                    public void update(final UpdatedHistoryParametersEvent event)
                    {
                        if (event.getParameters().containsKey(NOTIFICATION_PARAM))
                        {
                            // shows the notification text passed in as a url parameter
                            Session.getInstance().getEventBus().notifyObservers(new ShowNotificationEvent(
                                    new Notification(event.getParameters().get(NOTIFICATION_PARAM))));
                            
                            // hides the notification param in the address bar
                            Session.getInstance().getEventBus().notifyObservers(
                                    new UpdateHistoryEvent(new CreateUrlRequest(NOTIFICATION_PARAM, null, false)));
                        }
                    }
                });
    }
}
