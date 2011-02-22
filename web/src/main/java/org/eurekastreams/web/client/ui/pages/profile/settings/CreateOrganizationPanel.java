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

import java.util.LinkedList;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.events.data.GotOrganizationModelViewInformationResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedOrganizationResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.OrganizationModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.SettingsPanel;
import org.eurekastreams.web.client.ui.common.form.FormBuilder;
import org.eurekastreams.web.client.ui.common.form.FormBuilder.Method;
import org.eurekastreams.web.client.ui.common.form.elements.BasicTextBoxFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.OrgLookupFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.PersonLookupFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.ShortnameFormElement;
import org.eurekastreams.web.client.ui.common.notifier.Notification;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Organization panel.
 * 
 */
public class CreateOrganizationPanel extends SettingsPanel
{
    /**
     * The panel.
     */
    private static FlowPanel panel = new FlowPanel();

    /**
     * Maximum shortname length.
     */
    private static final int MAX_SHORTNAME = 20;

    /**
     * Default constructor.
     * 
     * @param parentOrgShortName
     *            parent org shortname.
     */
    public CreateOrganizationPanel(final String parentOrgShortName)
    {
        super(panel, "Create a Sub Organization");

        EventBus.getInstance().addObserver(GotOrganizationModelViewInformationResponseEvent.class,
                new Observer<GotOrganizationModelViewInformationResponseEvent>()
                {
                    public void update(final GotOrganizationModelViewInformationResponseEvent event)
                    {
                        setEntity(event.getResponse());
                    }
                });

        OrganizationModel.getInstance().fetch(parentOrgShortName, true);
    }

    /**
     * Set the parent org.
     * 
     * @param parentOrg
     *            parent org.
     */
    public void setEntity(final OrganizationModelView parentOrg)
    {
        this.clearContentPanel();
        this.setPreviousPage(new CreateUrlRequest(Page.ORGANIZATIONS, parentOrg.getShortName()), "< Return to Profile");

        RootPanel.get().addStyleName("form-body");
        FormBuilder form = new FormBuilder("", OrganizationModel.getInstance(), Method.INSERT);

        if (!Session.getInstance().getCurrentPersonRoles().contains(Role.ORG_COORDINATOR))
        {
            // shouldn't be here, send them to start page just as if they typed invalid url.
            Session.getInstance().getEventBus().notifyObservers(
                    new UpdateHistoryEvent(new CreateUrlRequest(Page.START)));
            return;
        }

        EventBus.getInstance().addObserver(InsertedOrganizationResponseEvent.class,
                new Observer<InsertedOrganizationResponseEvent>()
                {
                    public void update(final InsertedOrganizationResponseEvent arg1)
                    {
                        Session.getInstance().getEventBus().notifyObservers(
                                new UpdateHistoryEvent(new CreateUrlRequest(Page.ORGANIZATIONS, arg1.getResponse()
                                        .getShortName())));

                        Session.getInstance().getEventBus().notifyObservers(
                                new ShowNotificationEvent(new Notification(
                                        "Your sub organization has been successfully created")));
                    }
                });

        final BasicTextBoxFormElement orgName = new BasicTextBoxFormElement(50, false, "Organization Name",
                OrganizationModelView.NAME_KEY, "", "", true);

        DeferredCommand.addCommand(new Command()
        {
            public void execute()
            {
                orgName.setFocus();
            }
        });

        OrgLookupFormElement parentOrgLookup = new OrgLookupFormElement("Parent Organization", "", "",
                OrganizationModelView.ORG_PARENT_KEY, "", false, parentOrg, true);
        form.addFormElement(parentOrgLookup);

        form.addFormDivider();

        form.addFormElement(orgName);
        form.addOnCancelCommand(new Command()
        {
            public void execute()
            {
                History.back();
            }
        });
        form.addFormDivider();

        ShortnameFormElement shortName = new ShortnameFormElement("Organization Web Address",
                OrganizationModelView.SHORT_NAME_KEY, "", "http://" + Window.Location.getHost() + "/organizations/",
                "Please restrict your organization's name in the "
                        + "web address to 20 lower case alpha numeric characters without spaces.", true);

        form.addFormElement(shortName);

        form.addFormDivider();

        PersonModelView currentPerson = Session.getInstance().getCurrentPerson();

        PersonLookupFormElement personLookupFormElement = new PersonLookupFormElement("Organization Coordinators",
                "Add Coordinator",
                "The organization coordinators will be responsible for setting up the organization profile and "
                        + "policy.", OrganizationModelView.COORDINATORS_KEY, new LinkedList<PersonModelView>(), true);

        personLookupFormElement.addPerson(currentPerson);
        form.addFormElement(personLookupFormElement);

        form.addFormDivider();

        panel.add(form);
    }

}
