/*
 * Copyright (c) 2011-2012 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.connect.widget;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotPersonalInformationResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.PersonalInformationModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.avatar.AvatarLinkPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * The Eureka Connect "profile badge widget" - displays a person's avatar and information.
 */
public class UserProfileBadgeWidget extends Composite
{
    /**
     * Constructor.
     *
     * @param accountId
     *            Unique ID of person to display.
     */
    public UserProfileBadgeWidget(final String accountId)
    {
        final FlowPanel widget = new FlowPanel();
        widget.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectBadgeContainer());
        initWidget(widget);

        widget.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectLoading());

        EventBus.getInstance().addObserver(GotPersonalInformationResponseEvent.class,
                new Observer<GotPersonalInformationResponseEvent>()
                {
                    public void update(final GotPersonalInformationResponseEvent event)
                    {
                        widget.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectLoading());
                        PersonModelView entity = event.getResponse();

                        if (entity == null)
                        {
                            final AvatarWidget blankAvatar = new AvatarWidget(EntityType.PERSON, Size.Normal);
                            blankAvatar.addStyleName(StaticResourceBundle.INSTANCE.coreCss()
                                    .eurekaConnectBadgeAvatar());

                            widget.add(blankAvatar);

                            final Label blankName = new Label(accountId);
                            blankName.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectBadgeName());

                            widget.add(blankName);
                        }
                        else
                        {
                            Widget linkPanel = AvatarLinkPanel.create(entity, Size.Normal, false);
                            linkPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectBadgeAvatar());
                            widget.add(linkPanel);

                            String linkUrl = "/#"
                                    + Session.getInstance().generateUrl(
                                            new CreateUrlRequest(Page.PEOPLE, entity.getAccountId()));

                            Anchor name = new Anchor(entity.getDisplayName(), linkUrl, "_BLANK");
                            name.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectBadgeName());

                            Label title = new Label(entity.getTitle());
                            title.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectBadgeTitle());

                            Label company = new Label(entity.getCompanyName());
                            company.addStyleName(StaticResourceBundle.INSTANCE.coreCss().eurekaConnectBadgeCompany());

                            widget.add(name);
                            widget.add(title);
                            widget.add(company);
                        }
                    }
                });

        PersonalInformationModel.getInstance().fetch(accountId, false);
    }
}
