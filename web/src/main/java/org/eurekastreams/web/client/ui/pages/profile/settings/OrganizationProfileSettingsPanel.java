/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.pages.profile.settings;

import java.util.HashSet;
import java.util.Set;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.OrganizationModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.SettingsPanel;
import org.eurekastreams.web.client.ui.common.form.FormBuilder;
import org.eurekastreams.web.client.ui.common.form.FormBuilder.Method;
import org.eurekastreams.web.client.ui.common.form.elements.BasicCheckBoxFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.PersonModelViewLookupFormElement;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * A panel for changing the settings of an organization.
 */
public class OrganizationProfileSettingsPanel extends SettingsPanel
{
    /**
     * The panel.
     */
    static FlowPanel panel = new FlowPanel();

    /**
     * Default constructor.
     * 
     * @param orgName
     *            org.
     */
    public OrganizationProfileSettingsPanel(final String orgName)
    {
        super(panel, "Configure Profile");

        this.clearContentPanel();

        this.setPreviousPage(new CreateUrlRequest(Page.ORGANIZATIONS, orgName), "< Return to Profile");

        panel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().orgProfileSettingsPanel());

        if (Session.getInstance().getCurrentPersonRoles().contains(Role.SYSTEM_ADMIN))
        {
            setEntity();
        }
    }

    /**
     * Setter.
     */
    public void setEntity()
    {
        FormBuilder form = new FormBuilder("", OrganizationModel.getInstance(), Method.UPDATE);

        form.addFormDivider();

        String coordinstructions = "The organization coordinators will be responsible "
                + "for setting up the organization profile, setting org policy " + "and managing adoption campaigns.";

        Set<PersonModelView> coordinatorList = new HashSet<PersonModelView>(); // coordinators should be passed into
        // this set
        form.addFormElement(new PersonModelViewLookupFormElement("Organization Coordinators", "Add Coordinator",
                coordinstructions, "macaroni salad", coordinatorList, true));

        form.addFormDivider();

        // final AvatarUploadFormElement banner = new AvatarUploadFormElement("Banner",
        // "Select a JPG, PNG or GIF image from your computer. "
        // + "The maximum file size is 4MB and will be cropped to 120 pixels high.",
        // "/eurekastreams/bannerupload?type=Organization&entityName=" + entity.getShortName(), processor,
        // new BannerUploadStrategy<OrganizationModelView>(entity, entity.getId()));
        //
        // banner.addStyleName(StaticResourceBundle.INSTANCE.coreCss().bannerUploadFormElement());
        //
        // form.addWidget(banner);
        //
        // form.addFormDivider();

        BasicCheckBoxFormElement groupCreationPolicy = new BasicCheckBoxFormElement("New Group Moderation",
                "potato salad", "Enable Moderation.",
                "By enabling moderation, organization coordinators will be required to review new group requests.  "
                        + "Groups pending approval will be listed under the admin tab of your organization's profile.",
                false, true);

        // The key is true for "allowing group creation" and the checkbox displays "allowing moderation". Since
        // these are opposites, the value needs to be reversed when the form gets submitted.
        groupCreationPolicy.setReverseValue(true);

        groupCreationPolicy.addStyleName(StaticResourceBundle.INSTANCE.coreCss().orgGroupPolicy());
        form.addFormElement(groupCreationPolicy);

        form.addFormDivider();

        form.setOnCancelHistoryToken(Session.getInstance().generateUrl(new CreateUrlRequest(Page.ORGANIZATIONS, "")));

        panel.add(form);
    }

}
