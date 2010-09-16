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
package org.eurekastreams.web.client.ui.pages.profile.settings;

import java.io.Serializable;
import java.util.Set;

import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.server.domain.DomainFormatUtility;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.DomainGroupEntity;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.SetBannerEvent;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.UpdateHistoryEvent;
import org.eurekastreams.web.client.events.data.AuthorizeUpdateGroupResponseEvent;
import org.eurekastreams.web.client.events.data.GotGroupInformationResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedGroupResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.jsni.WidgetJSNIFacadeImpl;
import org.eurekastreams.web.client.model.GroupModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.autocomplete.AutoCompleteItemDropDownFormElement;
import org.eurekastreams.web.client.ui.common.form.FormBuilder;
import org.eurekastreams.web.client.ui.common.form.FormBuilder.Method;
import org.eurekastreams.web.client.ui.common.form.elements.BasicCheckBoxFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.BasicRadioButtonGroupFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.BasicTextAreaFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.BasicTextBoxFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.OrgLookupFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.PersonLookupFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.RichTextAreaFormElement;
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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * The basic group settings.
 *
 */
public class GroupProfileSettingsTabContent extends FlowPanel
{
    /** The width of the text editor. */
    private static final int TEXT_EDITOR_WIDTH = 430;

    /**
     * Maximum name length.
     */
    private static final int MAX_NAME = 50;

    /**
     * Maximum keywords length.
     */
    private static final int MAX_KEYWORDS = 2000;

    /**
     * The panel.
     */
    private FlowPanel panel = new FlowPanel();

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
     * @param groupName the group name.
     */
    public GroupProfileSettingsTabContent(final String groupName)
    {
        this.add(panel);

        EventBus.getInstance().addObserver(GotGroupInformationResponseEvent.class,
                new Observer<GotGroupInformationResponseEvent>()
                {
                    public void update(final GotGroupInformationResponseEvent event)
                    {
                        setEntity(event.getResponse());
                    }
                });

        Session.getInstance().getEventBus().addObserver(AuthorizeUpdateGroupResponseEvent.class,
                new Observer<AuthorizeUpdateGroupResponseEvent>()
                {
                    public void update(final AuthorizeUpdateGroupResponseEvent event)
                    {
                        GroupModel.getInstance().fetch(groupName, true);
                    }
                });

        GroupModel.getInstance().authorize(groupName, true);


    }

    /**
     * Setter.
     *
     * @param entity
     *            the group whose settings will be changed
     */
    public void setEntity(final DomainGroupEntity entity)
    {
        DomainGroup group = (DomainGroup) entity;

        // Set the banner.
        Session.getInstance().getEventBus().notifyObservers(new SetBannerEvent(group));
        RootPanel.get().addStyleName("form-body");

        final FormBuilder form = new FormBuilder("", GroupModel.getInstance(), Method.UPDATE);

        EventBus.getInstance().addObserver(UpdatedGroupResponseEvent.class, new Observer<UpdatedGroupResponseEvent>()
        {
            public void update(final UpdatedGroupResponseEvent arg1)
            {
                Session.getInstance().getEventBus().notifyObservers(
                        new UpdateHistoryEvent(new CreateUrlRequest(Page.GROUPS, arg1.getResponse().getShortName())));

                Session.getInstance().getEventBus().notifyObservers(
                        new ShowNotificationEvent(new Notification("Your group has been successfully saved")));
            }
        });

        form.addFormElement(new ValueOnlyFormElement(DomainGroupModelView.ID_KEY, Long.toString(group.getId())));
        form.addFormElement(new ValueOnlyFormElement(DomainGroupModelView.SHORT_NAME_KEY, group.getShortName()));

        AvatarUploadFormElement avatarFormEl = new AvatarUploadFormElement("Avatar",
                "Select a JPG, PNG or GIF image from your computer. The maximum file size is 4MB.",
                "/eurekastreams/groupavatarupload?groupName=" + group.getShortName(), Session.getInstance()
                        .getActionProcessor(), new AvatarUploadStrategy<DomainGroup>(group, "resizeGroupAvatar",
                        EntityType.GROUP));
        form.addWidget(avatarFormEl);

        form.addFormDivider();

        form.addFormElement(new BasicTextBoxFormElement(MAX_NAME, false, "Group Name", DomainGroupModelView.NAME_KEY,
                group.getName(), "", true));
        form.addFormDivider();

        form.addFormElement(new BasicTextAreaFormElement(DomainGroup.MAX_DESCRIPTION_LENGTH, "Description",
                DomainGroupModelView.DESCRIPTION_KEY, group.getDescription(),
                "Enter a few sentences that describe the purpose of your group's stream.  "
                        + "This description will appear beneath your avatar "
                        + "and in the profile search results pages.", false));

        form.addFormDivider();

        form.addFormElement(new RichTextAreaFormElement("Overview", DomainGroupModelView.OVERVIEW_KEY, group
                .getOverview(), "Enter an overview of your group.  This description will appear under the About tab.",
                TEXT_EDITOR_WIDTH, false));
        HTML html = new HTML("<strong>Tip:</strong> Include more detailed information about your group, "
                + "type of content you would like people to post, or other relevant guidelines.");
        html.addStyleName("form-instructions");
        Panel instructionPanel = new SimplePanel();
        instructionPanel.add(html);
        instructionPanel.addStyleName("form-element");
        form.addWidget(instructionPanel);

        form.addFormDivider();

        AutoCompleteItemDropDownFormElement keywords = new AutoCompleteItemDropDownFormElement("Keywords",
                DomainGroupModelView.KEYWORDS_KEY,
                DomainFormatUtility.buildCapabilitiesString(group.getCapabilities()),
                "Add keywords that describe your group and the topics your members will be talking about. Separate "
                        + "keywords with a comma. Including keywords helps others find your group when searching "
                        + "profiles.", false,
                "/resources/autocomplete/capability/", "itemNames", ",");
        keywords.setMaxLength(MAX_KEYWORDS);

        form.addFormElement(keywords);
        form.addFormDivider();

        form.addFormElement(new BasicTextBoxFormElement("Website URL", "url", group.getUrl(),
                "If your group has a website, you can enter the URL above", false));
        form.addFormDivider();

        form.addFormElement(new OrgLookupFormElement("Parent Organization", "",
                "Please use the lookup to select the organization that this group is associated with.",
                DomainGroupModelView.ORG_PARENT_KEY, "", true, Session.getInstance().getActionProcessor(), group
                        .getParentOrganization(), false));
        form.addFormDivider();

        String coordinstructions = "The group coordinators will be responsible for managing the organization profile, "
                + "and moderating the group's actiity stream";
        Set<Person> coordinatorList = group.getCoordinators();
        form.addFormElement(new PersonLookupFormElement("Group Coordinators", "Add Coordinator", coordinstructions,
                DomainGroupModelView.COORDINATORS_KEY, coordinatorList, true, Session.getInstance()
                        .getActionProcessor()));

        form.addFormDivider();

        final AvatarUploadFormElement avatarBanner = new AvatarUploadFormElement("Banner",
                "Select a JPG, PNG or GIF image from your computer. "
                        + "The maximum file size is 4MB and will be cropped to 120 pixels high.",
                "/eurekastreams/bannerupload?type=DomainGroup&entityName=" + entity.getShortName(), Session
                        .getInstance().getActionProcessor(), new BannerUploadStrategy<DomainGroup>(
                        (DomainGroup) entity, entity.getId()));
        form.addWidget(avatarBanner);

        form.addFormDivider();

        final Label currentPrivacySettingLabel = new Label();
        currentPrivacySettingLabel.setText("Privacy Settings");
        currentPrivacySettingLabel.addStyleName("form-label");
        final FlowPanel currentPrivacySettingDescription = new FlowPanel();
        final Label currentPrivacySettingDescriptionTitle = new Label();
        currentPrivacySettingDescriptionTitle.setText(group.isPublicGroup() ? "Public" : "Private");
        currentPrivacySettingDescriptionTitle.addStyleName("form-static-value");
        final Label currentPrivacySettingDescriptionInfo = new Label();
        if (group.isPublicGroup())
        {
            currentPrivacySettingDescriptionInfo.setText("Public groups are visible to all users and accessible "
                    + "through a profile search.");
        }
        else
        {
            currentPrivacySettingDescriptionInfo.setText("Access to private groups is restricted to employees"
                    + " approved by the group's coordinators.  Group coordinators can view a list of pending "
                    + "requests by going to the admin tab on the group's profile.");
        }
        currentPrivacySettingDescriptionInfo.addStyleName("form-instructions");
        currentPrivacySettingDescription.add(currentPrivacySettingDescriptionTitle);
        currentPrivacySettingDescription.add(currentPrivacySettingDescriptionInfo);
        currentPrivacySettingDescription.addStyleName("privacy-settings-value");
        final FlowPanel currentPrivacySettingPanel = new FlowPanel();
        currentPrivacySettingPanel.addStyleName("form-element");
        currentPrivacySettingPanel.addStyleName("privacy-settings");
        currentPrivacySettingPanel.add(currentPrivacySettingLabel);
        currentPrivacySettingPanel.add(currentPrivacySettingDescription);
        form.addWidget(currentPrivacySettingPanel);

        if (!group.isPublicGroup())
        {
            final HTML privateNote = new HTML("<span class=\"form-static-value\">Please Note:</span> "
                    + "This group's name and description will be visible whenever employees browse or"
                    + " search profiles.");
            privateNote.addStyleName("privacy-settings-note form-instructions");
            currentPrivacySettingPanel.add(privateNote);
        }

        form.addFormDivider();

        // TODO: evidently this is supposed to go away
        BasicCheckBoxFormElement blockWallPost = new BasicCheckBoxFormElement("Stream Moderation",
                DomainGroupModelView.STREAM_POSTABLE_KEY, "Allow others to post to your group's stream", false, group
                        .isStreamPostable());
        BasicCheckBoxFormElement blockCommentPost = new BasicCheckBoxFormElement(null,
                DomainGroupModelView.STREAM_COMMENTABLE_KEY,
                "Allow others to comment on activity in your group's stream", false, group.isCommentable());

        blockWallPost.addStyleName("stream-moderation");
        blockCommentPost.addStyleName("stream-moderation");
        blockCommentPost.addStyleName("comment-moderation");

        form.addFormElement(blockWallPost);
        form.addFormElement(blockCommentPost);
        form.addFormDivider();

        // ---- Notification suppression ----

        BasicCheckBoxFormElement noMemberPostNotif =
                new BasicCheckBoxFormElement(
                        "Notification Settings",
                        DomainGroupModelView.SUPPRESS_POST_NOTIF_TO_MEMBER_KEY,
                        "Allow group members to receive emails and in-app notifications when activity is posted "
                                + "to this group",
                        "Eureka Streams will notify group members and coordinators when new activity has taken place "
                                + "in this group", false, !group.isSuppressPostNotifToMember());
        noMemberPostNotif.setReverseValue(true);
        noMemberPostNotif.addStyleName("group-notif-suppress");
        form.addFormElement(noMemberPostNotif);

        BasicCheckBoxFormElement noCoordPostNotif =
                new BasicCheckBoxFormElement(null,
                        DomainGroupModelView.SUPPRESS_POST_NOTIF_TO_COORDINATOR_KEY,
                        "Allow group coordinators to receive emails and in-app notifications when activity is posted "
                                + "to this group", false, !group.isSuppressPostNotifToCoordinator());
        noCoordPostNotif.setReverseValue(true);
        noCoordPostNotif.addStyleName("group-notif-suppress");
        form.addFormElement(noCoordPostNotif);

        form.addFormDivider();

        // ---- Action buttons ----

        deleteButton.addStyleName("form-delete-group-button");
        deleteButton.addStyleName("form-button");

        deleteButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                if (new WidgetJSNIFacadeImpl().confirm("Are sure that you want to delete this group? "
                        + "Deleting the group will remove the profile from the system along with "
                        + "all of the activity that has been posted to its stream."))
                {
                    form.turnOffChangeCheck();
                    processingSpinny.setVisible(true);
                    deleteButton.setVisible(false);

                    // TODO - might should put this in GroupModel (and mark it as Deletable) but there's no
                    // custom onFailure ability there yet.
                    Session.getInstance().getActionProcessor().makeRequest(
                            new ActionRequestImpl<Boolean>("deleteGroupAction", entity.getId()),
                            new AsyncCallback<Boolean>()
                            {
                                public void onSuccess(final Boolean result)
                                {
                                    // adds notification to top of page
                                    Session.getInstance().getEventBus().notifyObservers(
                                            new ShowNotificationEvent(new Notification("The group '" + entity.getName()
                                                    + "' has been deleted")));

                                    // navigates away from settings page to the parent org profile page
                                    Session.getInstance().getEventBus().notifyObservers(
                                            new UpdateHistoryEvent(new CreateUrlRequest(Page.ORGANIZATIONS, entity
                                                    .getParentOrganization().getShortName())));
                                }

                                public void onFailure(final Throwable caught)
                                {
                                    // adds notification to top of page
                                    Session.getInstance().getEventBus().notifyObservers(
                                            new ShowNotificationEvent(new Notification(
                                                    "An error has occured and the group '" + entity.getName()
                                                            + "' was not deleted")));
                                }
                            });
                }
            }
        });

        form.addWidgetToFormContainer(deleteButton);

        processingSpinny.setVisible(false);
        processingSpinny.addStyleName("form-submit-spinny");
        form.addWidgetToFormContainer(processingSpinny);

        form.setOnCancelHistoryToken(Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.GROUPS, entity.getShortName())));

        panel.add(form);
    }

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
}
