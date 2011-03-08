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

import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

/**
 * Provides a summary of a person's contact info.
 */
public class ContactInfoPanel extends FlowPanel
{
    /**
     * Constructor.
     * 
     * @param person
     *            the person whose information is to be displayed.
     */
    public ContactInfoPanel(final PersonModelView person)
    {
        Label label = new Label("Contact Information");
        label.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileSubheader());
        this.add(label);

        if (null != person)
        {
            // Email should never be null, but just in case
            if (person.getEmail() != null)
            {
                FlowPanel emailWrapper = new FlowPanel();
                HTML emailLink = new HTML("<a href=\"mailto:" + person.getEmail() + "\">" + person.getPreferredName()
                        + " " + person.getLastName() + "</a>");
                emailWrapper.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileEmail());
                emailLink.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileContactInfo());
                emailWrapper.add(new HTML(""));
                emailWrapper.add(emailLink);
                this.add(emailWrapper);
            }

            if (person.getWorkPhone() != null)
            {
                this.add(buildPhoneLabel(person.getWorkPhone(), StaticResourceBundle.INSTANCE.coreCss()
                        .profileWorkPhone()));
            }

            if (person.getCellPhone() != null)
            {
                this.add(buildPhoneLabel(person.getCellPhone(), StaticResourceBundle.INSTANCE.coreCss()
                        .profileCellPhone()));
            }

            if (person.getFax() != null)
            {
                this.add(buildPhoneLabel(person.getFax(), StaticResourceBundle.INSTANCE.coreCss().profileFax()));
            }
        }
    }

    /**
     * Utility method to apply text and style for a new label.
     * 
     * @param text
     *            the label's text
     * @param style
     *            the label's style
     * @return new label
     */
    private FlowPanel buildPhoneLabel(final String text, final String style)
    {
        FlowPanel labelWrapper = new FlowPanel();
        Label label = new Label(text);
        labelWrapper.addStyleName(style);
        label.addStyleName(StaticResourceBundle.INSTANCE.coreCss().profileContactInfo());
        labelWrapper.add(new HTML(""));
        labelWrapper.add(label);
        return labelWrapper;
    }
}
