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

import java.util.HashMap;
import java.util.Map;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.SwitchedToActivityDetailViewEvent;
import org.eurekastreams.web.client.events.data.GotOrganizationModelViewResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.OrganizationModelViewModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.stream.renderers.ShowRecipient;
import org.eurekastreams.web.client.ui.common.stream.renderers.StreamMessageItemRenderer;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
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
        final FlowPanel thisBuffered = this;

        boolean manageFlagged = "true".equals(Session.getInstance().getParameterValue("manageFlagged"));
        boolean showComment = "true".equals(Session.getInstance().getParameterValue("showComment"));

        final EventBus bus = Session.getInstance().getEventBus();

        StreamMessageItemRenderer renderer = new StreamMessageItemRenderer(showRecipient);
        renderer.setShowComment(showComment);
        renderer.setShowManageFlagged(manageFlagged);
        renderer.setSingleView(true);

        if (activity != null)
        {
            this.add(renderer.render(activity));
            bus.notifyObservers(new SwitchedToActivityDetailViewEvent());
        }
        else
        {
            showNotFound();
        }

        final Panel linkPanel = new FlowPanel();
        linkPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().manageFlaggedLinksPanel());
        thisBuffered.insert(linkPanel, 0);

        Map<String, String> params = new HashMap<String, String>();
        params.put("activityId", null);
        params.put("manageFlagged", null);
        linkPanel.add(new InlineHyperlink("View all activities", Session.getInstance().generateUrl(
                new CreateUrlRequest(params, false))));

        if (manageFlagged && activity != null)
        {
            bus.addObserver(GotOrganizationModelViewResponseEvent.class,
                    new Observer<GotOrganizationModelViewResponseEvent>()
                    {
                        public void update(final GotOrganizationModelViewResponseEvent ev)
                        {
                            bus.removeObserver(ev, this);

                            linkPanel.add(new InlineLabel("|"));

                            OrganizationModelView org = ev.getResponse();
                            String url =
                                    Session.getInstance().generateUrl(
                                            new CreateUrlRequest(Page.ORGANIZATIONS, org.getShortName(), "tab",
                                                    "Admin"));
                            linkPanel.add(new InlineHyperlink("Manage flagged content for organization "
                                    + org.getName(), url));
                        }
                    });
            OrganizationModelViewModel.getInstance().fetch(activity.getRecipientParentOrgId(), true);
        }
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

        Label msgText = new Label("The activity you were looking for could not be found.");
        FlowPanel text = new FlowPanel();
        text.add(msgText);
        text.addStyleName(StaticResourceBundle.INSTANCE.coreCss().errorMessageText());

        msgPanel.add(msgHeader);
        msgPanel.add(msgText);

        errorReport.add(msgPanel);
        Session.getInstance().getTimer().removeTimerJob("getUnseenActivityJob");

    }
}
