/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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

import org.eurekastreams.server.domain.TermsOfServiceDTO;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotSystemSettingsResponseEvent;
import org.eurekastreams.web.client.model.SystemSettingsModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.dialog.tos.TermsOfServiceDialogContent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * This class creates the Composite for the main footer displayed on the page.
 * 
 */
public class FooterComposite extends Composite
{

    /**
     * The Site Labeling Panel.
     */
    FlowPanel siteLabeling = new FlowPanel();

    /**
     * Primary constructor for the FooterComposite Widget.
     */
    public FooterComposite()
    {
        FlowPanel panel = new FlowPanel();

        FlowPanel navPanel = new FlowPanel();

        Anchor termsOfService = new Anchor("Terms of Service");

        termsOfService.addClickHandler(new ClickHandler()
        {
            public void onClick(ClickEvent arg0)
            {
                Session.getInstance().getEventBus().addObserver(GotSystemSettingsResponseEvent.class,
                        new Observer<GotSystemSettingsResponseEvent>()
                        {
                            public void update(final GotSystemSettingsResponseEvent event)
                            {
                                TermsOfServiceDialogContent tosDialog = new TermsOfServiceDialogContent(
                                        new TermsOfServiceDTO(event.getResponse().getTermsOfService()), true);

                                final Dialog dialog = new Dialog(tosDialog);
                                dialog.setBgVisible(true);
                                dialog.center();
                                dialog.getContent().show();
                            }
                        });

                SystemSettingsModel.getInstance().fetch(null, true);
            }
        });

        navPanel.add(termsOfService);

        Anchor poweredBy = new Anchor("", "http://www.eurekastreams.org", "_blank");
        poweredBy.addStyleName("powered-by-eureka");
        navPanel.add(poweredBy);

        panel.add(navPanel);
        panel.add(siteLabeling);

        navPanel.addStyleName("footer-nav");
        siteLabeling.addStyleName("site-labeling");
        panel.addStyleName("footer-bar");
        initWidget(panel);
    }

    /**
     * Sets Site labeling.
     * 
     * @param siteLabelingTxt
     *            The text for Site Labeling.
     */
    public void setSiteLabel(final String siteLabelingTxt)
    {
        siteLabeling.getElement().setInnerHTML(siteLabelingTxt);
    }
}
