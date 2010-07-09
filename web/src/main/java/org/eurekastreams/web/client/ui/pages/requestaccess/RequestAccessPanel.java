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
package org.eurekastreams.web.client.ui.pages.requestaccess;

import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.data.InsertedRequestForGroupMembershipResponseEvent;
import org.eurekastreams.web.client.model.GroupMembershipRequestModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.notifier.Notification;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Displays a Request Access link, which fires off an action request. The Link changes to a text message after the
 * click.
 */
public class RequestAccessPanel extends FlowPanel
{
    /**
     * The request link on the display.
     */
    Anchor request = new Anchor("group coordinators");

    /**
     * @param groupShortName
     *            identifies the group the user wants to join
     */
    public RequestAccessPanel(final String groupShortName)
    {
        final EventBus eventBus = Session.getInstance().getEventBus();

        // TODO make it look like the wireframe
        this.addStyleName("request-group-access");
        this.add(new Label("private group: please contact the "));
        this.add(request);
        this.add(new Label(" to request access"));

        eventBus.addObserver(InsertedRequestForGroupMembershipResponseEvent.class,
                new Observer<InsertedRequestForGroupMembershipResponseEvent>()
                {
                    public void update(final InsertedRequestForGroupMembershipResponseEvent inArg1)
                    {
                        eventBus.notifyObservers(new ShowNotificationEvent(new Notification(
                                "Your request for access has been sent")));

                        // TODO disable button instead
                        clear();
                        Label sent = new Label("Request Sent");
                        sent.addStyleName("response");
                        add(sent);
                    }
                });

        request.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent inArg0)
            {
                GroupMembershipRequestModel.getInstance().insert(groupShortName);
            }
        });
    }
}
