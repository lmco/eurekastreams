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
package org.eurekastreams.web.client.ui.pages.settings;

import java.util.LinkedList;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.MembershipCriteriaAddedEvent;
import org.eurekastreams.web.client.events.MembershipCriteriaRemovedEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.data.GotSystemSettingsResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedSystemSettingsResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.SystemSettingsModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.form.FormBuilder;
import org.eurekastreams.web.client.ui.common.form.FormBuilder.Method;
import org.eurekastreams.web.client.ui.common.form.elements.ActivityExpirationFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.BasicCheckBoxFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.BasicTextBoxFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.HideableRichTextAreaFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.HideableTextAreaFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.PersonModelViewLookupFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.StreamScopeFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.TermsOfServicePromptIntervalFormElement;
import org.eurekastreams.web.client.ui.common.form.elements.userassociation.UserAssociationFormElement;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

// TODO form needs to handle return input from action. it is not displaying errors back to the user. example.
// add more then 255 char into the TOS or a String where it should be an Int.
/**
 * SystemSettings panel.
 * 
 */
public class SystemSettingsPanelComposite extends FlowPanel
{
    /**
     * Maximum phone length.
     */
    private static final int MAX_PHONE = 50;

    /**
     * Maximum group name length.
     */
    private static final int MAX_GROUP_NAME = 50;

    /**
     * Maximum email length.
     */
    private static final int MAX_EMAIL = 50;

    /**
     * Maximum email length.
     */
    private static final int MAX_SITELABEL = 2000;

    /**
     * Maximum content warning length.
     */
    private static final int MAX_CONTENT_WARNING = 200;

    /**
     * the processor.
     */
    private ActionProcessor processor;

    /**
     * the site Label CheckBox.
     */
    private HideableTextAreaFormElement hideableSiteLabel;
    /**
     * The content Warning element.
     */
    private HideableTextAreaFormElement hideableContentWarning;

    /**
     * The content Warning element.
     */
    private HideableRichTextAreaFormElement hideablePluginWarning;

    /**
     * The activity expiration element.
     */
    private ActivityExpirationFormElement activityExp;
    /**
     * The TOS text element.
     */
    private HideableRichTextAreaFormElement tosElement;

    /**
     * The extra CL to hide the prompt Int form element.
     */
    private ClickListener hideTOSIntPanel;

    /**
     * The membership refresh button.
     */
    private Label membershipRefreshButton;

    /**
     * The ToS prompt interval form element.
     */
    private TermsOfServicePromptIntervalFormElement promptInterval;

    /**
     * Scopes.
     */
    private LinkedList<StreamScope> scopes = new LinkedList<StreamScope>();

    /**
     * Constructor.
     * 
     * @param inProcessor
     *            The action processor.
     */
    public SystemSettingsPanelComposite(final ActionProcessor inProcessor)
    {
        processor = inProcessor;

        addStyleName(StaticResourceBundle.INSTANCE.coreCss().systemSettings());

        hideTOSIntPanel = new ClickListener()
        {
            public void onClick(final Widget arg0)
            {
                if (tosElement.isChecked())
                {

                    promptInterval.setVisible(true);
                }
                else
                {

                    promptInterval.setVisible(false);
                }
            }
        };

        addObservers();
        getSystemSettingsData();
    }

    /**
     * Add event bus observers.
     */
    private void addObservers()
    {
        EventBus eventBus = Session.getInstance().getEventBus();

        eventBus.addObserver(MembershipCriteriaAddedEvent.class, new Observer<MembershipCriteriaAddedEvent>()
        {
            public void update(final MembershipCriteriaAddedEvent event)
            {
                if (event.isNew())
                {
                    // TODO: Refactor to use new client models
                    processor.makeRequest(new ActionRequestImpl<SystemSettings>("addMembershipCriteria", event
                            .getMembershipCriteria()), new AsyncCallback<SystemSettings>()
                    {
                        public void onFailure(final Throwable caught)
                        {
                        }

                        public void onSuccess(final SystemSettings systemSettings)
                        {
                            SystemSettingsModel.getInstance().clearCache();

                            Session.getInstance().getEventBus().notifyObservers(
                                    new ShowNotificationEvent(new Notification("Access List Saved")));
                            History.newItem(History.getToken());
                        }
                    });
                }
            }
        });

        eventBus.addObserver(MembershipCriteriaRemovedEvent.class, new Observer<MembershipCriteriaRemovedEvent>()
        {
            public void update(final MembershipCriteriaRemovedEvent event)
            {
                processor.makeRequest(new ActionRequestImpl<SystemSettings>("removeMembershipCriteria", event
                        .getMembershipCriteria()), new AsyncCallback<SystemSettings>()
                {
                    public void onFailure(final Throwable caught)
                    {
                    }

                    public void onSuccess(final SystemSettings systemSettings)
                    {
                        // TODO: Refactor to use new client models
                        SystemSettingsModel.getInstance().clearCache();

                        Session.getInstance().getEventBus().notifyObservers(
                                new ShowNotificationEvent(new Notification("Item Deleted")));
                        History.newItem(History.getToken());
                    }
                });
            }
        });
    }

    /**
     * Gets the system settings data and then builds the form.
     */
    private void getSystemSettingsData()
    {
        processor.setQueueRequests(false);

        Session.getInstance().getEventBus().addObserver(GotSystemSettingsResponseEvent.class,
                new Observer<GotSystemSettingsResponseEvent>()
                {
                    public void update(final GotSystemSettingsResponseEvent event)
                    {
                        // got the system settings - remove the observer
                        Session.getInstance().getEventBus().removeObserver(GotSystemSettingsResponseEvent.class, this);
                        generateForm(event.getResponse());
                    }

                });

        SystemSettingsModel.getInstance().fetch(new Boolean(true), false);
    }

    /**
     * method to clear all retained data from elements.
     */
    private void clearRetainedValues()
    {
        hideableSiteLabel.clearRetainedValue();
        hideableContentWarning.clearRetainedValue();
        activityExp.clearRetainedValue();
        tosElement.clearRetainedValue();

        if (!tosElement.isChecked())
        {
            promptInterval.setValue(1);
        }
    }

    /**
     * Builds the form.
     * 
     * @param systemSettingValues
     *            The system settings to use when preloading the form.
     */
    private void generateForm(final SystemSettings systemSettingValues)
    {
        this.clear();
        final FormBuilder form = new FormBuilder("", SystemSettingsModel.getInstance(), Method.UPDATE);

        Session.getInstance().getEventBus().addObserver(UpdatedSystemSettingsResponseEvent.class,
                new Observer<UpdatedSystemSettingsResponseEvent>()
                {

                    public void update(final UpdatedSystemSettingsResponseEvent arg1)
                    {
                        form.onSuccess();
                        Session.getInstance().getEventBus().notifyObservers(
                                new ShowNotificationEvent(new Notification("Settings saved")));

                        clearRetainedValues();
                    }

                });

        form.setOnCancelHistoryToken(Session.getInstance().generateUrl(new CreateUrlRequest(Page.START)));
        form.turnOffChangeCheck();
        hideableSiteLabel = new HideableTextAreaFormElement(MAX_SITELABEL, "Site Labeling", "siteLabel",
                systemSettingValues.getSiteLabel(), "I would like to add a label to the system.",
                "The site label will be displayed above the global navigation bar and below the "
                        + "footer on all pages in the system. If left blank, no label area will "
                        + "appear on your pages.", true);

        hideablePluginWarning = new HideableRichTextAreaFormElement("Plugin Configuration Warning", "pluginWarning",
                systemSettingValues.getPluginWarning(),
                "I would like to setup a plugin configuration warning for the system.",
                "This text will be displayed everytime a user configures a plugin for an individual or group", true);

        hideableContentWarning = new HideableTextAreaFormElement(MAX_CONTENT_WARNING, "Content Warning",
                "contentWarningText", systemSettingValues.getContentWarningText(),
                "I would like to add a warning to Activity Stream post boxes.",
                "This text will be displayed on the Activity Stream post boxes throughout the system.", true);

        activityExp = new ActivityExpirationFormElement(systemSettingValues.getContentExpiration(),
                "contentExpiration", true);

        tosElement = new HideableRichTextAreaFormElement("Terms Of Service", "termsOfService", systemSettingValues
                .getTermsOfService(), "I would like to setup the Terms of Service for the system.",
                "Users will be prompted to agree or disagree with a Terms of Service message "
                        + "on an interval of your choice.  You can also add a link to the full "
                        + "Terms of Service document.", true);

        tosElement.addStyleName(StaticResourceBundle.INSTANCE.coreCss().hideableTextarea());

        form.addFormElement(new PersonModelViewLookupFormElement("Eureka Administrators", "Add Administrator", "",
                "admins", systemSettingValues.getSystemAdministrators(), true));

        form.addFormDivider();

        Integer promptVal = systemSettingValues.getTosPromptInterval();

        if (null == promptVal)
        {
            promptVal = 1;
        }

        promptInterval = new TermsOfServicePromptIntervalFormElement(promptVal, "tosPromptInterval");
        form.addFormElement(new UserAssociationFormElement(systemSettingValues));

        BasicCheckBoxFormElement sendEmails = new BasicCheckBoxFormElement("", "sendWelcomeEmails",
                "Send email invitations to new users as their accounts are created.", false, systemSettingValues
                        .getSendWelcomeEmails());

        sendEmails.addStyleName(StaticResourceBundle.INSTANCE.coreCss().welcomeEmailCheckbox());

        membershipRefreshButton = initializeRefreshButton();

        FlowPanel clearPanel = new FlowPanel();

        clearPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().clear());

        sendEmails.add(membershipRefreshButton);
        form.addFormElement(sendEmails);

        form.addFormDivider();
        form.addFormElement(hideableSiteLabel);
        form.addFormDivider();
        form.addFormElement(hideableContentWarning);
        form.addFormDivider();
        form.addFormElement(hideablePluginWarning);
        form.addFormDivider();
        form.addFormElement(tosElement);
        form.addFormElement(promptInterval);
        form.addFormDivider();

        // Help Page
        addHelpPageElementsToForm(systemSettingValues, form);
        form.addFormDivider();

        form.addFormElement(activityExp);
        form.addFormDivider();

        BasicCheckBoxFormElement groupCreationPolicy = new BasicCheckBoxFormElement("New Group Moderation",
                "allUsersCanCreateGroups", "Enable Moderation.",
                "By enabling moderation, system administrators will be required to review new group requests.  "
                        + "Groups pending approval will be listed under the pending tab of system settings.", false,
                !systemSettingValues.getAllUsersCanCreateGroups());

        // The key is true for "allowing group creation" and the checkbox displays "allowing moderation". Since
        // these are opposites, the value needs to be reversed when the form gets submitted.
        groupCreationPolicy.setReverseValue(true);

        groupCreationPolicy.addStyleName(StaticResourceBundle.INSTANCE.coreCss().orgGroupPolicy());
        form.addFormElement(groupCreationPolicy);
        form.addFormDivider();

        if (!tosElement.isChecked())
        {
            promptInterval.setVisible(false);
        }

        this.add(form);

        tosElement.addCheckBoxClickListener(hideTOSIntPanel);
    }

    /**
     * Add the help page elements to the form builder.
     * 
     * @param systemSettingValues
     *            the system settings
     * @param form
     *            the form builder to add the help page elements to
     */
    private void addHelpPageElementsToForm(final SystemSettings systemSettingValues, final FormBuilder form)
    {
        FlowPanel container = new FlowPanel();
        container.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formElement());
        container.addStyleName(StaticResourceBundle.INSTANCE.coreCss().helpPageSettings());

        Label helpPageHeaderLabel = new Label("Help Page");
        helpPageHeaderLabel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formLabel());
        container.add(helpPageHeaderLabel);

        FlowPanel instructionsPanel = new FlowPanel();
        instructionsPanel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formInstructions());
        instructionsPanel.add(new InlineLabel("The information listed below will appear on the "));
        instructionsPanel.add(new InlineHyperlink("help", Session.getInstance().generateUrl(
                new CreateUrlRequest(Page.HELP))));
        instructionsPanel.add(new InlineLabel(" page."));
        container.add(instructionsPanel);

        form.addWidget(container);

        BasicTextBoxFormElement helpSupportPhoneNumber = new BasicTextBoxFormElement(MAX_PHONE, false,
                "Support Phone Number", "supportPhoneNumber", "", "", false);
        helpSupportPhoneNumber.setValue(systemSettingValues.getSupportPhoneNumber() == null ? "" : systemSettingValues
                .getSupportPhoneNumber());

        BasicTextBoxFormElement helpSupportEmailAddress = new BasicTextBoxFormElement(MAX_EMAIL, false,
                "Support Email Address", "supportEmailAddress", "", "", false);
        helpSupportEmailAddress.setValue(systemSettingValues.getSupportEmailAddress() == null ? ""
                : systemSettingValues.getSupportEmailAddress());

        helpSupportPhoneNumber.addStyleName(StaticResourceBundle.INSTANCE.coreCss().helpFormElement());
        helpSupportEmailAddress.addStyleName(StaticResourceBundle.INSTANCE.coreCss().helpFormElement());

        if (systemSettingValues.getSupportStreamGroupShortName() != null)
        {
            StreamScope scope = new StreamScope(ScopeType.GROUP, systemSettingValues.getSupportStreamGroupShortName());
            scope.setDisplayName(systemSettingValues.getSupportStreamGroupDisplayName());
            scopes.add(scope);
        }
        form.addFormElement(new StreamScopeFormElement("supportStreamGroupShortName", scopes, "Support Stream",
                "Enter the name of the stream you want to display on the help page", false, false,
                "/resources/autocomplete/groups/", MAX_GROUP_NAME, 1));
        form.addFormElement(helpSupportPhoneNumber);
        form.addFormElement(helpSupportEmailAddress);
    }

    /**
     * Setup for refresh button.
     * 
     * @return newly configured label/button.
     */
    private Label initializeRefreshButton()
    {
        Label button = new Label();
        button.addStyleName(StaticResourceBundle.INSTANCE.coreCss().accessListRefreshButton());
        button.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent event)
            {
                processor.makeRequest(new ActionRequestImpl<String>("updateMembershipTaskHandler", null),
                        new AsyncCallback<String>()
                        {
                            public void onFailure(final Throwable caught)
                            {
                                Session.getInstance().getEventBus().notifyObservers(
                                        new ShowNotificationEvent(new Notification(
                                                "An error occurred refreshing Access List")));
                                History.newItem(History.getToken());
                            }

                            public void onSuccess(final String value)
                            {
                            }
                        });

                Session.getInstance().getEventBus().notifyObservers(
                        new ShowNotificationEvent(new Notification("Access List Refresh is now processing")));
                History.newItem(History.getToken());
            }
        });
        return button;
    }
}
