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
package org.eurekastreams.web.client.ui.pages.profile.widgets;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Displays an Organization's title, website, and mission.
 */
public class OrgAboutPanel extends FlowPanel
{
    /**
     * Constructor.
     * 
     * @param inOrgName
     *            Org Name.
     * @param inOrgEntityId
     *            Org id.
     * @param inAvatarId
     *            Avatar id.
     * @param inOrgUrl
     *            Org url.
     * @param inOrgDescription
     *            Org description.
     */
    public OrgAboutPanel(final String inOrgName, final long inOrgEntityId, final String inAvatarId,
            final String inOrgUrl, final String inOrgDescription)
    {
        add(new AvatarWidget(inOrgEntityId, inAvatarId, EntityType.ORGANIZATION, AvatarWidget.Size.Normal));

        Label title = new Label(inOrgName);
        title.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileOrgTitle());
        this.add(title);

        Anchor url = new Anchor("Website", inOrgUrl, "_NEW");
        url.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileWebsite());
        if (inOrgUrl == null)
        {
            url.addStyleName(StaticResourceBundle.INSTANCE.coreCss().noValue());
        }
        this.add(url);

        if (inOrgDescription != null && !inOrgDescription.equals(""))
        {
            Label subheader = new Label("");
            subheader.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileSubheader());
            this.add(subheader);

            Label mission = new Label(inOrgDescription);
            mission.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileMission());
            this.add(mission);
        }
    }
}
