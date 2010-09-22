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
package org.eurekastreams.web.client.ui.pages.profile.settings;

import java.util.Set;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.server.search.modelview.PersonModelView.Role;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.events.data.AuthorizeUpdateGroupResponseEvent;
import org.eurekastreams.web.client.events.data.AuthorizeUpdateOrganizationResponseEvent;
import org.eurekastreams.web.client.events.data.GotOrganizationInformationResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedOrganizationResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.OrganizationModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.SettingsPanel;
import org.eurekastreams.web.client.ui.common.form.FormBuilder;
import org.eurekastreams.web.client.ui.common.form.FormBuilder.Method;
import org.eurekastreams.web.client.ui.common.form.elements.BasicCheckBoxFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.BasicTextAreaFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.BasicTextBoxFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.PersonLookupFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.ValueOnlyFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.avatar.AvatarUploadFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.avatar.strategies.AvatarUploadStrategy;
import org.eurekastreams.web.client.ui.common.form.elements.avatar.strategies.BannerUploadStrategy;
import org.eurekastreams.web.client.ui.common.notifier.Notification;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * A panel for changing the settings of an organization.
 */
public class OrganizationProfileSettingsPanel extends SettingsPanel
{
    /**
     * The panel.
     */
    static FlowPanel panel = new FlowPanel();

    /**
     * Maximum name length.
     */
    private static final int MAX_NAME = 50;

    /**
     * The delete-group button.
     */
    private Anchor deleteButton = new Anchor("");

    /**
     * The processing spinner.
     */
    private Label processingSpinny = new Label("Processing...");

    /**
     * Default constructor.
     * 
     * @param orgName
     *            org.
     */
    public OrganizationProfileSettingsPanel(final String orgName)
    {
        super(panel, "Configure Profile");

        this.clearContentPanel();

        this.setPreviousPage(new CreateUrlRequest(Page.ORGANIZATIONS, orgName), "< Return to Profile");

        panel.addStyleName("org-profile-settings-panel");

        EventBus.getInstance().addObserver(GotOrganizationInformationResponseEvent.class,
                new Observer<GotOrganizationInformationResponseEvent>()
                {
                    public void update(final GotOrganizationInformationResponseEvent event)
                    {
                        setEntity(event.getResponse());
                    }
                });

        Session.getInstance().getEventBus().addObserver(AuthorizeUpdateOrganizationResponseEvent.class,
                new Observer<AuthorizeUpdateGroupResponseEvent>()
                {
                    public void update(final AuthorizeUpdateGroupResponseEvent event)
                    {
                        OrganizationModel.getInstance().fetch(orgName, true);
                    }
                });

        OrganizationModel.getInstance().authorize(orgName, true);
    }

    /**
     * Setter.
     * 
     * @param entity
     *            the organization whose settings will be changed
     */
    public void setEntity(final Organization entity)
    {
        ActionProcessor processor = Session.getInstance().getActionProcessor();
        RootPanel.get().addStyleName("form-body");

        FormBuilder form = new FormBuilder("", OrganizationModel.getInstance(), Method.UPDATE);

        Session.getInstance().getEventBus().addObserver(UpdatedOrganizationResponseEvent.class,
                new Observer<UpdatedOrganizationResponseEvent>()
                {

                    public void update(final UpdatedOrganizationResponseEvent arg1)
                    {
                        Session.getInstance().getEventBus().notifyObservers(
                                new UpdateHistoryEvent(new CreateUrlRequest(Page.ORGANIZATIONS, arg1.getResponse()
                                        .getShortName())));

                        Session.getInstance().getEventBus().notifyObservers(
                                new ShowNotificationEvent(new Notification(
                                        "Your organization has been successfully saved")));
                    }

                });

        form.addFormElement(new ValueOnlyFormElement(OrganizationModelView.ID_KEY, Long.toString(entity.getId())));
        form.addFormElement(new ValueOnlyFormElement(OrganizationModelView.SHORT_NAME_KEY, entity.getShortName()));

        form.addWidget(new AvatarUploadFormElement("Avatar",
                "Select a JPG, PNG or GIF image from your computer. The maximum file size is 4MB.",
                "/eurekastreams/orgavatarupload?orgName=" + entity.getShortName(), processor,
                new AvatarUploadStrategy<Organization>(entity, "resizeOrgAvatar", EntityType.ORGANIZATION)));

        form.addFormDivider();

        form.addFormElement(new BasicTextBoxFormElement(MAX_NAME, false, "Organization Name",
                OrganizationModelView.NAME_KEY, entity.getName(), "", true));
        form.addFormElement(new BasicTextAreaFormElement(Organization.MAX_DESCRIPTION_LENGTH, "Description",
                OrganizationModelView.DESCRIPTION_KEY, entity.getDescription(),
                "Enter a few sentences that describe the purpose of your organization's stream.  "
                        + "This description will appear beneath your avatar "
                        + "and in search results pages in the directory.", false));

        form.addFormDivider();

        form.addFormElement(new BasicTextBoxFormElement("Website URL", OrganizationModelView.URL_KEY, entity.getUrl(),
                "If your organization has a website, you can enter the URL above", false));

        form.addFormDivider();

        String leaderinstructions = "Add the organization's leaders "
                + "in the order you would like them to appear on the profile.";
        Set<Person> leaderList = entity.getLeaders();
        leaderList = entity.getLeaders();
        form.addFormElement(new PersonLookupFormElement("Leadership", "Add Leader", leaderinstructions,
                OrganizationModelView.LEADERSHIP_KEY, leaderList, false, processor));

        form.addFormDivider();

        String coordinstructions = "The organization coordinators will be responsible "
                + "for setting up the organization profile, setting org policy " + "and managing adoption campaigns.";
        Set<Person> coordinatorList = entity.getCoordinators();
        form.addFormElement(new PersonLookupFormElement("Organization Coordinators", "Add Coordinator",
                coordinstructions, OrganizationModelView.COORDINATORS_KEY, coordinatorList, true, processor));

        form.addFormDivider();

        final AvatarUploadFormElement banner = new AvatarUploadFormElement("Banner",
                "Select a JPG, PNG or GIF image from your computer. "
                        + "The maximum file size is 4MB and will be cropped to 120 pixels high.",
                "/eurekastreams/bannerupload?type=Organization&entityName=" + entity.getShortName(), processor,
                new BannerUploadStrategy<Organization>(entity, entity.getId()));

        banner.addStyleName("banner-upload-form-element");

        form.addWidget(banner);

        form.addFormDivider();

        BasicCheckBoxFormElement groupCreationPolicy = new BasicCheckBoxFormElement("New Group Moderation",
                OrganizationModelView.ALLOW_GROUP_CREATION_KEY, "Enable Moderation.",
                "By enabling moderation, organization coordinators will be required to review new group requests.  "
                        + "Groups pending approval will be listed under the admin tab of your organization's profile.",
                false, !entity.getAllUsersCanCreateGroups());

        // The key is true for "allowing group creation" and the checkbox displays "allowing moderation". Since
        // these are opposites, the value needs to be reversed when the form gets submitted.
        groupCreationPolicy.setReverseValue(true);

        groupCreationPolicy.addStyleName("org-group-policy");
        form.addFormElement(groupCreationPolicy);

        form.addFormDivider();

        form.setOnCancelHistoryToken(Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.ORGANIZATIONS, entity.getShortName())));

        if (Session.getInstance().getCurrentPersonRoles().contains(Role.ROOT_ORG_COORDINATOR))
        {
            deleteButton.addStyleName("form-delete-org-button");
            deleteButton.addStyleName("form-button");

            deleteButton.addClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent event)
                {
                    if (new WidgetJSNIFacadeImpl().confirm("Are sure you want to delete this organization? "
                            + "Deleting the organization will remove the profile from the system "
                            + "and will move all employees reporting to this organization to it’s parent organization"))
                    {
                        processingSpinny.setVisible(true);
                        deleteButton.setVisible(false);

                        // TODO - should put this in OrganizationModel (and mark it as Deletable) but there's no
                        // custom onFailure ability there yet.
                        Session.getInstance().getActionProcessor().makeRequest(
                                new ActionRequestImpl<String>("deleteOrganizationAction", entity.getId()),
                                new AsyncCallback<String>()
                                {
                                    public void onSuccess(final String result)
                                    {
                                        // adds notification to top of page
                                        Session.getInstance().getEventBus().notifyObservers(
                                                new ShowNotificationEvent(new Notification("The organization '"
                                                        + entity.getName() + "' has been deleted")));

                                        // navigates away from settings page to the parent org profile page
                                        Session.getInstance().getEventBus()
                                                .notifyObservers(
                                                        new UpdateHistoryEvent(new CreateUrlRequest(Page.ORGANIZATIONS,
                                                                result)));
                                    }

                                    public void onFailure(final Throwable caught)
                                    {
                                        // adds notification to top of page
                                        Session.getInstance().getEventBus().notifyObservers(
                                                new ShowNotificationEvent(new Notification(
                                                        "An error has occured and the organization '"
                                                                + entity.getName() + "' was not deleted")));
                                    }
                                });
                    }
                }
            });

            form.addWidgetToFormContainer(deleteButton);

            processingSpinny.setVisible(false);
            processingSpinny.addStyleName("form-submit-spinny");
            form.addWidgetToFormContainer(processingSpinny);
        }

        panel.add(form);
    }

}
