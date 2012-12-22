/*
 * Copyright (c) 2009-2012 Lockheed Martin Corporation
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
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.avatar.AvatarLinkPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.pages.discover.FollowPanel;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

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
     * @param showDescription
     *            Whether to display the description line.
     * @param showEmail
     *            Whether or not the email address should be shown.
     */
    public PersonPanel(final PersonModelView person, final boolean showFollowers,
            final boolean showDescription, final boolean showEmail)
    {
        this(person, !person.isAccountLocked(), showFollowers, showDescription, showEmail);
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
        addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItem());
        addStyleName(StaticResourceBundle.INSTANCE.coreCss().listItem());
        addStyleName(StaticResourceBundle.INSTANCE.coreCss().person());

        add(AvatarLinkPanel.create(person, Size.Small));

        FlowPanel infoPanel = new FlowPanel();
        infoPanel.setStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemInfo());

        Widget name;
        if (makeLinkable)
        {
            String nameUrl = Session.getInstance().generateUrl(
                    new CreateUrlRequest(Page.PEOPLE, person.getAccountId()));
            name = new Hyperlink(person.getDisplayName(), nameUrl);
        }
        else
        {
            name = new Label(person.getDisplayName());
        }

        name.setTitle(person.getDisplayName());

        name.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemName());
        name.addStyleName(StaticResourceBundle.INSTANCE.coreCss().ellipsisChild());

        Label title = new Label(person.getTitle());
        title.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemTitle());
        title.addStyleName(StaticResourceBundle.INSTANCE.coreCss().ellipsis());

        infoPanel.add(name);
        infoPanel.add(title);

        if (showDescription)
        {
            String descriptionText = person.getDescription();
            if (descriptionText != null && !descriptionText.isEmpty())
            {
                Label about = new Label(descriptionText);
                about.setTitle(descriptionText);
                about.addStyleName(StaticResourceBundle.INSTANCE.coreCss().shortBio());
                about.addStyleName(StaticResourceBundle.INSTANCE.coreCss().ellipsis());
                infoPanel.add(about);
            }
        }

        if (showFollowers)
        {
            FlowPanel followersPanel = new FlowPanel();
            followersPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemFollowers());

            InlineLabel followers = new InlineLabel("Followers: ");
            followersPanel.add(followers);

            InlineLabel followersCount = new InlineLabel(Integer.toString(person.getFollowersCount()));
            followersCount.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemFollowersData());
            followersPanel.add(followersCount);

            insertActionSeparator(followersPanel);

            followersPanel.add(new InlineLabel("Added: "));

            DateFormatter dateFormatter = new DateFormatter(new Date());
            InlineLabel dateAdded = new InlineLabel(dateFormatter.timeAgo(person.getDateAdded(), true));
            dateAdded.addStyleName(StaticResourceBundle.INSTANCE.coreCss().connectionItemFollowersData());
            followersPanel.add(dateAdded);

            infoPanel.add(followersPanel);
        }

        if (showEmail)
        {
            String emailText = person.getEmail();
            if (emailText != null && !emailText.isEmpty())
            {
                Label email = new Label(emailText);
                email.addStyleName(StaticResourceBundle.INSTANCE.coreCss().email());
                email.addStyleName(StaticResourceBundle.INSTANCE.coreCss().extendedInfo());
                infoPanel.add(email);
            }
        }

        if (Session.getInstance().getCurrentPerson().getId() != person.getId())
        {
            infoPanel.add(new FollowPanel(person));
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
        sep.addStyleName(StaticResourceBundle.INSTANCE.coreCss().actionLinkSeparator());
        panel.add(sep);
    }
}
