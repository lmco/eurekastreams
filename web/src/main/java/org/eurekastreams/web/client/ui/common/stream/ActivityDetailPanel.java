/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.stream;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.web.client.events.SwitchedToActivityDetailViewEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.stream.renderers.ShowRecipient;
import org.eurekastreams.web.client.ui.common.stream.renderers.StreamMessageItemRenderer;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Activity detail panel.
 * 
 */
public class ActivityDetailPanel extends FlowPanel
{
    /**
     * Default constructor.
     * 
     * @param activity
     *            activity.
     * @param showRecipient
     *            show the recipient.
     */
    public ActivityDetailPanel(final ActivityDTO activity, final ShowRecipient showRecipient)
    {
        if (activity == null)
        {
            showNotFound();
            return;
        }

        boolean manageFlagged = "true".equals(Session.getInstance().getParameterValue("manageFlagged"));
        boolean showComment = "true".equals(Session.getInstance().getParameterValue("showComment"));
        addStyleName(StaticResourceBundle.INSTANCE.coreCss().singleActivityPanel());

        // build link to show all activity in the destination stream
        StreamEntityDTO destinationStream = activity.getDestinationStream();
        EntityType entityType = destinationStream.getEntityType();
        String title = null;
        Page page = null;
        switch (entityType)
        {
        case PERSON:
            page = Page.PEOPLE;
            title = "Show all activity in " + destinationStream.getDisplayName() + "'s stream";
            break;
        case GROUP:
            page = Page.GROUPS;
            title = "Show all activity in the " + destinationStream.getDisplayName() + " stream";
            break;
        default:
            page = null; // make checkstyle shut up
        }
        if (page != null)
        {
            FlowPanel showAllPanel = new FlowPanel();
            showAllPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().singleActivityShowAll());
            showAllPanel.add(new Hyperlink(title, Session.getInstance().generateUrl(
                    new CreateUrlRequest(page, destinationStream.getUniqueId()))));
            add(showAllPanel);
        }

        StreamMessageItemRenderer renderer = new StreamMessageItemRenderer(showRecipient);
        renderer.setShowComment(showComment);
        renderer.setShowManageFlagged(manageFlagged);
        renderer.setSingleView(true);

        add(renderer.render(activity));
        Session.getInstance().getEventBus().notifyObservers(new SwitchedToActivityDetailViewEvent());
    }

    /**
     * Shows a "not found" message.
     */
    private void showNotFound()
    {
        Panel errorReport = new FlowPanel();
        errorReport.addStyleName(StaticResourceBundle.INSTANCE.coreCss().warningReport());

        FlowPanel centeringPanel = new FlowPanel();
        centeringPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().warningReportContainer());
        centeringPanel.add(errorReport);
        add(centeringPanel);

        FlowPanel msgPanel = new FlowPanel();

        Label msgHeader = new Label("Activity not found");
        msgHeader.addStyleName(StaticResourceBundle.INSTANCE.coreCss().warningMessage());

        Label msgText = new Label("The activity you were looking for has already been deleted or could not be found.");
        FlowPanel text = new FlowPanel();
        text.add(msgText);
        text.addStyleName(StaticResourceBundle.INSTANCE.coreCss().errorMessageText());

        msgPanel.add(msgHeader);
        msgPanel.add(msgText);

        errorReport.add(msgPanel);
        Session.getInstance().getTimer().removeTimerJob("getUnseenActivityJob");

    }
}
