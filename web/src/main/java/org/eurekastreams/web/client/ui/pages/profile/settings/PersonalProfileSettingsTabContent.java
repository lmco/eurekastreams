/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import java.util.HashMap;

import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.domain.DomainFormatUtility;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.SetBannerEvent;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.data.GotPersonalInformationResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedPersonalInformationResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.PersonalInformationModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.autocomplete.AutoCompleteItemDropDownFormElement;
import org.eurekastreams.web.client.ui.common.autocomplete.AutoCompleteDropDownPanel.ElementType;
import org.eurekastreams.web.client.ui.common.form.FormBuilder;
import org.eurekastreams.web.client.ui.common.form.FormBuilder.Method;
import org.eurekastreams.web.client.ui.common.form.elements.BasicCheckBoxFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.BasicTextAreaFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.BasicTextBoxFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.MultiOrgLookupFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.OrgLookupFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.ValueOnlyFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.avatar.AvatarUploadFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.avatar.strategies.AvatarUploadStrategy;
import org.eurekastreams.web.client.ui.common.notifier.Notification;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * The Display for Personal Profile Settings Tab.
 */
public class PersonalProfileSettingsTabContent extends Composite
{
    /**
     * The person.
     */
    Person person;

    /**
     * The Flow panel.
     */
    FlowPanel panel = new FlowPanel();

    /**
     * Maximum textbox length.
     */
    private static final int MAX_LENGTH = 50;

    /**
     * Maximum email length.
     */
    private static final int MAX_EMAIL = 100;

    /**
     * The default constructor.
     */
    public PersonalProfileSettingsTabContent()
    {
        final EventBus eventBus = Session.getInstance().getEventBus();
        eventBus.addObserver(GotPersonalInformationResponseEvent.class,
                new Observer<GotPersonalInformationResponseEvent>()
                {
                    public void update(final GotPersonalInformationResponseEvent event)
                    {
                        person = event.getResponse();

                        eventBus.notifyObservers(new SetBannerEvent(person));

                        final FormBuilder form = new FormBuilder("", PersonalInformationModel.getInstance(),
                                Method.UPDATE);

                        eventBus.addObserver(UpdatedPersonalInformationResponseEvent.class,
                                new Observer<UpdatedPersonalInformationResponseEvent>()
                                {
                                    public void update(final UpdatedPersonalInformationResponseEvent arg1)
                                    {
                                        eventBus.notifyObservers(new ShowNotificationEvent(new Notification(
                                                "Your profile has been updated.")));
                                        form.onSuccess();
                                    }
                                });

                        form.addFormElement(new ValueOnlyFormElement("accountId", person.getAccountId()));

                        form.addWidget(new AvatarUploadFormElement("Photo", "/eurekastreams/personavatarupload",
                                Session.getInstance().getActionProcessor(), new AvatarUploadStrategy<Person>(person,
                                        "resizePersonAvatar", EntityType.PERSON)));
                        form.addFormDivider();

                        // create OrgModelView from groupParentOrg
                        Organization parentOrg = person.getParentOrganization();
                        OrganizationModelView parentOrgModelView = null;
                        if (parentOrg != null)
                        {
                            parentOrgModelView = new OrganizationModelView();
                            parentOrgModelView.setName(parentOrg.getName());
                            parentOrgModelView.setShortName(parentOrg.getShortName());
                            parentOrgModelView.setEntityId(parentOrg.getId());
                        }

                        form.addFormElement(new OrgLookupFormElement("Organization Affiliation",
                                "Primary Organization",
                                "Select the organization that you are most closely associated with.",
                                "parentOrganization", "", true, parentOrgModelView, false));
                        form.addFormElement(new MultiOrgLookupFormElement("", "Secondary Organization(s)",
                                "Select the other organization(s) you actively support.", "relatedOrganizations",
                                "Secondary", false, person.getRelatedOrganizations()));
                        form.addFormDivider();

                        form.addFormElement(new BasicTextBoxFormElement(MAX_LENGTH, false, "Title",
                                PersonModelView.TITILE_KEY, person.getTitle(),
                                "Your title will appear below your photo on the profile page", true));
                        form.addFormDivider();
                        form.addFormElement(new BasicTextBoxFormElement(MAX_LENGTH, false, "Display Name",
                                PersonModelView.PREFERREDNAME_KEY, person.getPreferredName(),
                                "Entering a display name will replace your first name anywhere your name "
                                        + "appears in the system", true));
                        form.addFormDivider();
                        form.addFormElement(new BasicTextAreaFormElement(Person.MAX_JOB_DESCRIPTION_LENGTH,
                                "Job Description", PersonModelView.DESCRIPTION_KEY, person.getJobDescription(),
                                "Enter a brief description of your job responsibilities.", false));
                        form.addFormDivider();

                        String skills = "";
                        if (person.getBackground() != null)
                        {
                            skills = DomainFormatUtility.buildCapabilitiesString(person.getBackground()
                                    .getBackgroundItems(BackgroundItemType.SKILL));
                        }
                        form.addFormElement(new AutoCompleteItemDropDownFormElement("Interests",
                                PersonModelView.SKILLS_KEY, skills,
                                "Add keywords that describe your work experience, skills, interests, or "
                                        + "hobbies. Separate keywords with a comma. Including tags increases your "
                                        + "chances of being found in a profile search.", false,
                                "/resources/autocomplete/skill/", "itemNames", ElementType.TEXTAREA, ","));
                        form.addFormDivider();

                        form.addFormElement(new BasicTextBoxFormElement(MAX_LENGTH, false, "Work Phone",
                                PersonModelView.WORKPHONE_KEY, person.getWorkPhone(), null, false));
                        form.addFormDivider();
                        form.addFormElement(new BasicTextBoxFormElement(MAX_LENGTH, false, "Cell",
                                PersonModelView.CELLPHONE_KEY, person.getCellPhone(), null, false));
                        form.addFormDivider();
                        form.addFormElement(new BasicTextBoxFormElement(MAX_LENGTH, false, "Fax",
                                PersonModelView.FAX_KEY, person.getFax(), null, false));
                        form.addFormDivider();
                        form.addFormElement(new BasicTextBoxFormElement(MAX_EMAIL, false, "Email",
                                PersonModelView.EMAIL_KEY, person.getEmail(), "(ex. user@example.com)", true));

                        form.addFormDivider();
                        BasicCheckBoxFormElement blockWallPost = new BasicCheckBoxFormElement("Stream Moderation",
                                "streamPostable", " Allow others to post to your stream", false, person
                                        .isStreamPostable());
                        BasicCheckBoxFormElement blockCommentPost = new BasicCheckBoxFormElement(null, "commentable",
                                " Allow others to comment on activity in your stream", false, person.isCommentable());

                        blockWallPost.addStyleName("stream-moderation");
                        blockCommentPost.addStyleName("stream-moderation");
                        blockCommentPost.addStyleName("comment-moderation");

                        form.addFormElement(blockWallPost);
                        form.addFormElement(blockCommentPost);
                        form.addFormDivider();

                        HashMap<String, String> currentPageParams = new HashMap<String, String>();
                        currentPageParams.put("tab", "Basic+Info");

                        form.setOnCancelHistoryToken(Session.getInstance().generateUrl(
                                new CreateUrlRequest(Page.PEOPLE, person.getAccountId())));

                        panel.add(form);

                    }
                });

        initWidget(panel);
        PersonalInformationModel.getInstance().fetch(Session.getInstance().getCurrentPerson().getAccountId(), true);
    }

}
