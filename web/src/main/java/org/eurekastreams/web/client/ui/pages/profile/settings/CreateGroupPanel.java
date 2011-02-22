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

import java.io.Serializable;
import java.util.HashSet;

import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.SaveSelectedOrgEvent;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.events.data.GotOrganizationModelViewInformationResponseEvent;
import org.eurekastreams.web.client.events.data.InsertedGroupResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.GroupModel;
import org.eurekastreams.web.client.model.OrganizationModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.SettingsPanel;
import org.eurekastreams.web.client.ui.common.form.FormBuilder;
import org.eurekastreams.web.client.ui.common.form.FormBuilder.Method;
import org.eurekastreams.web.client.ui.common.form.elements.BasicRadioButtonFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.BasicRadioButtonGroupFormElement;
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
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Organization panel.
 * 
 */
public class CreateGroupPanel extends SettingsPanel
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
     * innerClass for the radioButtonGroup.
     */
    public class GroupPrivacySettings extends BasicRadioButtonGroupFormElement
    {
        /**
         * @param labelVal
         *            label for group.
         * @param inKey
         *            key for group.
         * @param groupName
         *            name of group.
         * @param inInstructions
         *            instructions for group.
         */
        public GroupPrivacySettings(final String labelVal, final String inKey, final String groupName,
                final String inInstructions)
        {
            super(labelVal, inKey, groupName, inInstructions);
        }

        /**
         * @return value of group.
         */
        @Override
        public Serializable getValue()
        {
            return Boolean.parseBoolean((String) super.getValue());
        }
    }

    /**
     * Constructor.
     * 
     * @param parentOrgShortName
     *            parent org shortname.
     */
    public CreateGroupPanel(final String parentOrgShortName)
    {
        super(panel, "Create a Group");

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
        final EventBus eventBus = Session.getInstance().getEventBus();

        this.clearContentPanel();
        this.setPreviousPage(new CreateUrlRequest(Page.ORGANIZATIONS, parentOrg.getShortName()), "< Return to Profile");

        PersonModelView currentPerson = Session.getInstance().getCurrentPerson();
        String coordinstructions = "The group coordinators"
                + " will be responsible for setting up the group profile, setting group permissions, "
                + "and managing group access";
        PersonLookupFormElement personLookupFormElement = new PersonLookupFormElement("Group Coordinators",
                "Add Coordinator", coordinstructions, DomainGroupModelView.COORDINATORS_KEY,
                new HashSet<PersonModelView>(), true);

        personLookupFormElement.addPerson(currentPerson);

        RootPanel.get().addStyleName("form-body");
        final FormBuilder form = new FormBuilder("", GroupModel.getInstance(), Method.INSERT);

        eventBus.addObserver(SaveSelectedOrgEvent.class, new Observer<SaveSelectedOrgEvent>()
        {
            public void update(final SaveSelectedOrgEvent event)
            {
                eventBus.addObserver(InsertedGroupResponseEvent.class, new Observer<InsertedGroupResponseEvent>()
                {
                    public void update(final InsertedGroupResponseEvent ev)
                    {
                        DomainGroupModelView group = ev.getResponse();

                        // destination depends on whether org allows immediate creation of groups
                        CreateUrlRequest urlRqst = !group.isPending() ? new CreateUrlRequest(Page.GROUPS, group
                                .getShortName()) : new CreateUrlRequest(Page.ORGANIZATIONS, group
                                .getParentOrganizationShortName());
                        eventBus.notifyObservers(new UpdateHistoryEvent(urlRqst));

                        // tell the user what just happened
                        eventBus.notifyObservers(new ShowNotificationEvent(new Notification(group.isPending() ? // \n
                        "Your group has been submitted to an organization coordinator for approval"
                                : "Your group has been successfully created")));
                    }
                });
            }
        });

        OrgLookupFormElement parentOrgLookup = new OrgLookupFormElement("Parent Organization", "", "",
                DomainGroupModelView.ORG_PARENT_KEY, "Parent Organization", false, parentOrg, true);
        form.addFormElement(parentOrgLookup);

        form.addFormDivider();

        final BasicTextBoxFormElement groupName = new BasicTextBoxFormElement(50, false, "Group Name",
                DomainGroupModelView.NAME_KEY, "", "", true);

        DeferredCommand.addCommand(new Command()
        {

            public void execute()
            {
                groupName.setFocus();
            }
        });

        groupName.addStyleName("group-name");

        form.addFormElement(groupName);

        form.addFormDivider();

        ShortnameFormElement shortName = new ShortnameFormElement("Group Web Address",
                DomainGroupModelView.SHORT_NAME_KEY, "", "http://" + Window.Location.getHost() + "/groups/",
                "Please restrict your group's name in the web address "
                        + "to 20 lower case alpha numeric characters without spaces.", true);

        shortName.addStyleName("group-short-name");

        form.addFormElement(shortName);
        form.addOnCancelCommand(new Command()
        {
            public void execute()
            {
                History.back();
            }
        });

        form.addFormDivider();

        form.addFormElement(personLookupFormElement);
        form.addFormDivider();

        /**
         * CheckBox for Form.
         */

        final GroupPrivacySettings radioButtonGroup = new GroupPrivacySettings("Privacy Settings",
                DomainGroupModelView.PRIVACY_KEY, "privacySettings", "");

        radioButtonGroup.clearGroup();
        radioButtonGroup.addRadioButton("Public", "Allow all users to view this profile.", "true", Boolean.TRUE);
        BasicRadioButtonFormElement privateButton = radioButtonGroup.addRadioButton("Private",
                "Restrict access to users approved by this group's coordinators.", "false", Boolean.FALSE);

        Panel extraInstructions = new FlowPanel();
        extraInstructions.addStyleName("group-private-extra-note");
        extraInstructions.add(new InlineLabel("Please Note: "));
        extraInstructions.add(new InlineLabel(
                "This group's name and description will be visible whenever employees browse or search profiles."));
        privateButton.addToInstructions(extraInstructions);

        form.addFormElement(radioButtonGroup);
        form.addClear();

        form.addFormDivider();
        panel.add(form);

    }
}
