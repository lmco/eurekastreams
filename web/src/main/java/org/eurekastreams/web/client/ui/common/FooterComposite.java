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

import org.eurekastreams.server.domain.TermsOfServiceDTO;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotSystemSettingsResponseEvent;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.dialog.tos.TermsOfServiceDialogContent;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

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
    FlowPanel siteLabelingContainer = new FlowPanel();

    /**
     * ToS Dialog.
     */
    private Dialog dialog;

    /**
     * Primary constructor for the FooterComposite Widget.
     */
    public FooterComposite()
    {
        FlowPanel panel = new FlowPanel();

        FlowPanel navPanel = new FlowPanel();

        Anchor termsOfService = new Anchor("Terms of Service");
        termsOfService.addStyleName(StaticResourceBundle.INSTANCE.coreCss().termsOfServiceLink());

        Session.getInstance().getEventBus().addObserver(GotSystemSettingsResponseEvent.class,
                new Observer<GotSystemSettingsResponseEvent>()
                {
                    public void update(final GotSystemSettingsResponseEvent event)
                    {
                        TermsOfServiceDialogContent tosDialog = new TermsOfServiceDialogContent(new TermsOfServiceDTO(
                                event.getResponse().getTermsOfService()), true);

                        dialog = new Dialog(tosDialog);
                    }
                });

        termsOfService.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent arg0)
            {
                dialog.showCentered();
            }
        });

        navPanel.add(termsOfService);

        Anchor poweredBy = new Anchor("", "http://www.eurekastreams.org", "_blank");
        poweredBy.addStyleName(StaticResourceBundle.INSTANCE.coreCss().poweredByEureka());
        navPanel.add(poweredBy);

        panel.add(navPanel);
        panel.add(siteLabelingContainer);

        navPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().footerNav());
        siteLabelingContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().siteLabeling());
        panel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().footerBar());
        initWidget(panel);
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
