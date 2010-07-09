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
package org.eurekastreams.web.client.ui.common.pagedlist;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.avatar.AvatarLinkPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Render an org model view.
 *
 */
public class OrganizationRenderer implements ItemRenderer<OrganizationModelView>
{
    /**
     * {@inheritDoc}
     */
    public Panel render(final OrganizationModelView org)
    {
        FlowPanel orgPanel = new FlowPanel();
        orgPanel.addStyleName("connection-item list-item");
        orgPanel.addStyleName("organization-item");

        orgPanel.add(new AvatarLinkPanel(EntityType.ORGANIZATION, org.getShortName(), org.getEntityId(), org
                .getAvatarId(), Size.Small));

        FlowPanel organizationAbout = new FlowPanel();
        organizationAbout.addStyleName("connection-item-info");

        String url = Session.getInstance().generateUrl(new CreateUrlRequest(Page.ORGANIZATIONS, org.getShortName()));
        Hyperlink orgTitleLink = new Hyperlink(org.getName(), url);
        orgTitleLink.addStyleName("organization-title");
        organizationAbout.add(orgTitleLink);

        if (null != org.getDescription())
        {
            Label description = new Label(org.getDescription());
            description.addStyleName("org-overview");
            organizationAbout.add(description);
        }

        FlowPanel orgMetaData = new FlowPanel();
        orgMetaData.addStyleName("connection-item-followers");
        
        orgMetaData.add(new InlineLabel("Emp: "));
        InlineLabel descendentEmpLabel = new InlineLabel("" + org.getDescendantEmployeeCount());
        descendentEmpLabel.addStyleName("connection-item-followers-data");
        orgMetaData.add(descendentEmpLabel);
        insertActionSeparator(orgMetaData);
        
        orgMetaData.add(new InlineLabel("Groups: "));
        InlineLabel descendentGroupLabel = new InlineLabel("" + org.getDescendantGroupCount());
        descendentGroupLabel.addStyleName("connection-item-followers-data");
        orgMetaData.add(descendentGroupLabel);
        insertActionSeparator(orgMetaData);
        
        orgMetaData.add(new InlineLabel("Sub Orgs: "));
        InlineLabel childOrgLabel = new InlineLabel("" + org.getChildOrganizationCount());
        childOrgLabel.addStyleName("connection-item-followers-data");
        orgMetaData.add(childOrgLabel);

        organizationAbout.add(orgMetaData);

        orgPanel.add(organizationAbout);

        return orgPanel;
    }
    

    /**
     * Adds a separator (dot).
     *
     * @param panel
     *            Panel to put the separator in.
     */
    private void insertActionSeparator(final Panel panel)
    {
        Label sep = new InlineLabel("\u2219");
        sep.addStyleName("action-link-separator");
        panel.add(sep);
    }
}
