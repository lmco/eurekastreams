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
package org.eurekastreams.web.client.ui.common;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotSystemSettingsResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.SystemSettingsModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;

/**
 * This class creates the Composite for the main footer displayed on the page.
 * 
 */
public class FooterComposite extends Composite
{

    /**
     * The Site Labeling Panel.
     */
    FlowPanel siteLabelingContainer = new FlowPanel();

    /**
     * Primary constructor for the FooterComposite Widget.
     */
    public FooterComposite()
    {
        FlowPanel panel = new FlowPanel();
        initWidget(panel);

        final FlowPanel navPanel = new FlowPanel();

        EventBus.getInstance().addObserver(GotSystemSettingsResponseEvent.class,
                new Observer<GotSystemSettingsResponseEvent>()
                {
                    public void update(final GotSystemSettingsResponseEvent event)
                    {
                        if (Session.getInstance().getHistoryHandler() != null && navPanel.getWidgetCount() == 0
                                && event.getResponse().getSupportStreamGroupShortName() != null
                                && event.getResponse().getSupportStreamGroupShortName().length() > 0)
                        {
                            navPanel.add(new Hyperlink("HELP", Session.getInstance().generateUrl(
                                    new CreateUrlRequest(Page.GROUPS, event.getResponse()
                                            .getSupportStreamGroupShortName()))));

                            if (event.getResponse().getSupportStreamWebsite() != null
                                    && event.getResponse().getSupportStreamWebsite().length() > 0)
                            {

                                navPanel.add(new Label("|"));
                                navPanel.add(new Anchor("LEARN MORE", event.getResponse().getSupportStreamWebsite(),
                                        "_blank"));
                            }
                        }
                    }
                });

        panel.add(navPanel);
        panel.add(siteLabelingContainer);

        navPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().footerNav());
        siteLabelingContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().siteLabeling());
        panel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().footerBar());
        SystemSettingsModel.getInstance().fetch(null, true);
    }

    /**
     * Sets Site labeling.
     * 
     * @param inTemplate
     *            HTML template content to insert in the footer.
     * @param inSiteLabel
     *            The text for Site Labeling.
     */
    public void setSiteLabelTemplate(final String inTemplate, final String inSiteLabel)
    {
        String siteLabel = inSiteLabel == null ? "" : inSiteLabel;
        String template = inTemplate.replace("%SITELABEL%", siteLabel);
        siteLabelingContainer.getElement().setInnerHTML(template);
    }
}
