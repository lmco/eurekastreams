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
package org.eurekastreams.web.client.ui.pages.help;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;

/**
 * Support Stream panel, showing the support group icon, mission statement, and link to go to the activity stream.
 */
public class SupportStreamHelpPanel extends FlowPanel
{
    /**
     * The header panel.
     */
    private FlowPanel headerPanel = new FlowPanel();

    /**
     * The content panel.
     */
    private FlowPanel contentPanel = new FlowPanel();

    /**
     * The logo panel.
     */
    private FlowPanel logoPanel = new FlowPanel();

    /**
     * Description panel.
     */
    private FlowPanel descriptionPanel = new FlowPanel();

    /**
     * Constructor.
     * 
     * @param inSettings
     *            the system settings
     * @param inSupportGroup
     *            the support domain group
     */
    public SupportStreamHelpPanel(final SystemSettings inSettings, final DomainGroupModelView inSupportGroup)
    {
        Label headerLabel = new Label("Support Stream");
        headerPanel.add(headerLabel);
        headerPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().header());
        contentPanel.add(descriptionPanel);
        add(headerPanel);
        add(contentPanel);

        logoPanel.add(new AvatarWidget(inSupportGroup.getId(), inSupportGroup.getAvatarId(), EntityType.GROUP,
                Size.Normal));
        logoPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().supportGroupLogoPanel());

        contentPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().contentPanel());
        contentPanel.add(logoPanel);
        descriptionPanel.add(new Label(inSupportGroup.getDescription()));

        Hyperlink gotoStreamLink = new Hyperlink("Go to Stream", Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.GROUPS, inSettings.getSupportStreamGroupShortName())));
        descriptionPanel.add(gotoStreamLink);
        gotoStreamLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().goToSupportGroupStreamLink());

        contentPanel.add(descriptionPanel);
        descriptionPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().supportGroupDescriptionPanel());
    }
}
