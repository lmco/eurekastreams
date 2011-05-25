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
package org.eurekastreams.web.client.ui.pages.profile;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Displays a summary of a person's profile.
 */
public class OrganizationProfilePanel extends FlowPanel
{
    /**
     * Constructor.
     * 
     * @param accountId
     *            the account id.
     */
    public OrganizationProfilePanel(final String accountId)
    {
        final double oneHundred = 100;
        RootPanel.get().addStyleName(StaticResourceBundle.INSTANCE.coreCss().profile());

        FlowPanel fp = new FlowPanel();
        fp.getElement().getStyle().setWidth(oneHundred, Unit.PCT);
        fp.getElement().getStyle().setHeight(oneHundred, Unit.PX);
        this.add(fp);

        final Hyperlink addGroupLink = new Hyperlink("", Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.NEW_GROUP, accountId)));
        addGroupLink.setVisible(true);
        addGroupLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileAddGroup());
        this.add(addGroupLink);
    }
}
