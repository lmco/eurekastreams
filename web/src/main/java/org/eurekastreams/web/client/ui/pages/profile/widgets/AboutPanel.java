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

import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;

/**
 * Display some information about a Person.
 */
public class AboutPanel extends FlowPanel
{
    /**
     * Hyperlink for follow/unfollow me button.
     */
    FollowPanel followMe = null;

    /**
     * The Person whose data are being shown.
     */
    private Person person = null;

    /**
     * Constructor.
     *
     * @param personAccountId
     *            the person whose being describe by this AboutPanel.
     */
    public AboutPanel(final String personAccountId)
    {
        followMe = new FollowPanel(personAccountId, EntityType.PERSON);
    }

    /**
     * Loads up the person.
     *
     * @param inPerson
     *            the person.
     */
    public void setPerson(final Person inPerson)
    {
        this.clear();
        person = inPerson;
        String nameStr = "";
        String titleStr = "";

        Organization org = null;
        List<Organization> relatedOrgs = null;

        String jobDescriptionStr = "";
        if (null != person)
        {
            nameStr = person.getPreferredName() + " " + person.getLastName();
            titleStr = person.getTitle();
            org = person.getParentOrganization();
            jobDescriptionStr = person.getJobDescription();
            relatedOrgs = person.getRelatedOrganizations();
        }

        AvatarWidget photo = new AvatarWidget(person, EntityType.PERSON, AvatarWidget.Size.Normal,
                AvatarWidget.Background.White);
        photo.addStyleName("profile-photo");
        this.add(photo);

        if (!person.getAccountId().equals(Session.getInstance().getCurrentPerson().getAccountId()))
        {
            this.add(followMe);
        }

        Label name = new Label(nameStr);
        name.addStyleName("profile-name");
        this.add(name);

        Label title = new Label(titleStr);
        title.addStyleName("profile-title");
        this.add(title);

        if (jobDescriptionStr != null && !jobDescriptionStr.equals(""))
        {
            Label jobDescription = new Label(jobDescriptionStr);
            jobDescription.addStyleName("profile-quote");
            this.add(jobDescription);
        }


        if (null != relatedOrgs && relatedOrgs.size() > 0)
        {
            Label orgLabel = new Label("Also supporting");
            orgLabel.addStyleName("also-supporting-org");
            this.add(orgLabel);

            for (Organization relatedOrg : relatedOrgs)
            {
                Hyperlink orgLink = new Hyperlink(relatedOrg.getName(), Session.getInstance().generateUrl(
                        new CreateUrlRequest(Page.ORGANIZATIONS, relatedOrg.getShortName())));
                orgLink.addStyleName("profile-org");
                this.add(orgLink);
            }
        }

    }




}
