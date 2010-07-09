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
package org.eurekastreams.web.client.ui.pages.setup;

import java.util.ArrayList;

import org.eurekastreams.server.domain.MembershipCriteria;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.events.data.InsertedRootOrganizationResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.RootOrganizationModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.form.FormBuilder;
import org.eurekastreams.web.client.ui.common.form.FormBuilder.Method;
import org.eurekastreams.web.client.ui.common.form.elements.BasicTextBoxFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.PersonLookupFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.ValueOnlyFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.userassociation.UserAssociationFormElement;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Initial system setup panel.
 */
public class SystemSetupPanel extends FlowPanel
{
    /**
     * Constructor.
     */
    public SystemSetupPanel()
    {
        Session session = Session.getInstance();

        RootPanel.get().setStyleName("setup");

        FlowPanel welcome = new FlowPanel();
        welcome.addStyleName("welcome-to-eureka");

        welcome.add(new Label("Your installation is almost complete!"));
        welcome.add(new Label("Please complete the following steps."));

        this.add(welcome);

        SystemSettings settings = new SystemSettings();
        settings.setMembershipCriteria(new ArrayList<MembershipCriteria>());
        settings.setSendWelcomeEmails(true);

        FormBuilder form = new FormBuilder("", RootOrganizationModel.getInstance(), Method.INSERT);

        Session.getInstance().getEventBus().addObserver(InsertedRootOrganizationResponseEvent.class,
                new Observer<InsertedRootOrganizationResponseEvent>()
                {
                    public void update(final InsertedRootOrganizationResponseEvent arg1)
                    {
                        Session.getInstance().getEventBus().notifyObservers(
                                new UpdateHistoryEvent(new CreateUrlRequest(Page.START)));
                    }

                });

        final ValueOnlyFormElement orgParent = new ValueOnlyFormElement("orgParent", "");
        form.addFormElement(orgParent);

        Label step1 = form.addFormLabel("");
        step1.addStyleName("step-1");

        final BasicTextBoxFormElement orgTitle = new BasicTextBoxFormElement("Name your organization.", "name", "",
                "This will be the root of the directory structure.", true);
        form.addFormElement(orgTitle);

        final BasicTextBoxFormElement shortName = new BasicTextBoxFormElement("Choose your organization short name.",
                "shortName", "", "http://yoururl.com/organizations/shortName", true);
        form.addFormElement(shortName);

        Label step2 = form.addFormLabel("");
        step2.addStyleName("step-2");

        final UserAssociationFormElement userAssoc = new UserAssociationFormElement(settings);
        form.addFormElement(userAssoc);

        Label step3 = form.addFormLabel("");
        step3.addStyleName("step-3");

        final ArrayList<Person> coordinators = new ArrayList<Person>();

        final PersonLookupFormElement personLookup = new PersonLookupFormElement("Define your coordinators",
                "Add Coordinator", "Identify who will create your organization structure and make policy decisions",
                "coordinators", coordinators, true, session.getActionProcessor());

        form.addFormElement(personLookup);

        Label step4 = form.addFormLabel("");
        step4.addStyleName("step-4");

        Label step4Label = form.addFormLabel("Save these settings.");
        step4Label.addStyleName("form-label step4-label");

        Label step4Instructions = form.addFormLabel("Organization coordinators can always edit these settings from "
                + " the 'System' tab of the settings page.");
        step4Instructions.addStyleName("step4-instructions form-instructions");
        step4Instructions.removeStyleName("form-label");
        step4Instructions.removeStyleName("form-standalone-label");

        this.add(form);
    }
}
