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
package org.eurekastreams.web.client.ui.common;

import java.util.Date;

import org.eurekastreams.commons.formatting.DateFormatter;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.avatar.AvatarLinkPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Displays a person's picture, name, title, and optionally the number of followers.
 */
public class PersonPanel extends FlowPanel
{
    /**
     * Constructor.
     *
     * @param inPerson
     *            to display
     * @param showFollowers
     *            to display
     * @param makeLinkable
     *            whether to display it or not
     */
    @Deprecated
    public PersonPanel(final PersonModelView inPerson, final boolean showFollowers, final boolean makeLinkable)
    {
        this(inPerson, makeLinkable, showFollowers, false, false);
    }

    /**
     * Constructor.
     *
     * @param person
     *            to display
     * @param showFollowers
     *            to display
     * @param makeLinkable
     *            whether to display it or not
     * @param showDescription
     *            Whether to display the description line.
     * @param showEmail
     *            Whether or not the email address should be shown.
     */
    public PersonPanel(final PersonModelView person, final boolean makeLinkable, final boolean showFollowers,
            final boolean showDescription, final boolean showEmail)
    {
        addStyleName("connection-item");
        addStyleName("list-item");
        addStyleName("person");

        add(new AvatarLinkPanel(EntityType.PERSON, person.getUniqueId(), person.getId(), person.getAvatarId(),
                Size.Small));

        FlowPanel infoPanel = new FlowPanel();
        infoPanel.setStyleName("connection-item-info");

        Widget name;
        Widget org;
        if (makeLinkable)
        {
            String nameUrl =
                    Session.getInstance().generateUrl(new CreateUrlRequest(Page.PEOPLE, person.getAccountId()));
            String orgUrl =
                    Session.getInstance().generateUrl(
                            new CreateUrlRequest(Page.ORGANIZATIONS, person.getParentOrganizationShortName()));
            name = new Hyperlink(person.getDisplayName(), nameUrl);
            org = new Hyperlink(person.getParentOrganizationName(), orgUrl);
        }
        else
        {
            name = new Label(person.getDisplayName());
            org = new Label(person.getParentOrganizationName());
        }
        name.setStyleName("connection-item-name");
        org.setStyleName("connection-item-organization");

        Label title = new Label(person.getTitle());
        title.setStyleName("connection-item-title");

        infoPanel.add(name);
        infoPanel.add(title);
        infoPanel.add(org);

        if (showDescription)
        {
            String descriptionText = person.getDescription();
            if (descriptionText != null && !descriptionText.isEmpty())
            {
                Label about = new Label(descriptionText);
                about.addStyleName("short-bio extended-info");
                infoPanel.add(about);
            }
        }

        if (showFollowers)
        {
            FlowPanel followersPanel = new FlowPanel();
            followersPanel.addStyleName("connection-item-followers");

            InlineLabel followers = new InlineLabel("Followers: ");
            followersPanel.add(followers);

            InlineLabel followersCount = new InlineLabel(Integer.toString(person.getFollowersCount()));
            followersCount.addStyleName("connection-item-followers-data");
            followersPanel.add(followersCount);

            insertActionSeparator(followersPanel);

            followersPanel.add(new InlineLabel("Added: "));

            DateFormatter dateFormatter = new DateFormatter(new Date());
            InlineLabel dateAdded = new InlineLabel(dateFormatter.timeAgo(person.getDateAdded(), true));
            dateAdded.addStyleName("connection-item-followers-data");
            followersPanel.add(dateAdded);

            infoPanel.add(followersPanel);
        }

        if (showEmail)
        {
            String emailText = person.getEmail();
            if (emailText != null && !emailText.isEmpty())
            {
                Label email = new Label(emailText);
                email.addStyleName("email extended-info");
                infoPanel.add(email);
            }
        }
        
        this.add(infoPanel);

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
