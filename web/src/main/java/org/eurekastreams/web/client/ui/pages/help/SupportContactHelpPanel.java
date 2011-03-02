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

import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Support contact information panel, showing the phone number and email for support.
 */
public class SupportContactHelpPanel extends FlowPanel
{
    /**
     * Constructor.
     *
     * @param inSupportPhoneNumber
     *            the support phone number
     *
     * @param inSupportEmailAddress
     *            the support email address
     */
    public SupportContactHelpPanel(final String inSupportPhoneNumber, final String inSupportEmailAddress)
    {
        FlowPanel headerPanel = new FlowPanel();
        headerPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().header());
        headerPanel.add(new Label("Contact Information"));
        this.add(headerPanel);

        if (inSupportPhoneNumber != null && inSupportPhoneNumber.length() > 0)
        {
            Label supportPhoneNumberLabel = new Label(inSupportPhoneNumber);
            supportPhoneNumberLabel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().supportPhoneNumberLabel());
            this.add(supportPhoneNumberLabel);
        }

        if (inSupportEmailAddress != null && inSupportEmailAddress.length() > 0)
        {
            FlowPanel supportEmailPanel = new FlowPanel();
            Anchor supportEmailAnchor = new Anchor(inSupportEmailAddress, "mailto:" + inSupportEmailAddress);
            supportEmailPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().supportEmailLabel());
            supportEmailPanel.add(supportEmailAnchor);
            this.add(supportEmailPanel);
        }
    }
}
