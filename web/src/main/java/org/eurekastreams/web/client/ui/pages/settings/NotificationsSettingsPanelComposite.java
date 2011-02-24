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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eurekastreams.server.action.response.settings.RetrieveSettingsResponse;
import org.eurekastreams.server.domain.NotificationFilterPreference;
import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.NotificationFilterPreference.Category;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.data.GotPersonalSettingsResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedPersonalSettingsResponseEvent;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.model.PersonalSettingsModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.form.FormBuilder;
import org.eurekastreams.web.client.ui.common.form.FormBuilder.Method;
import org.eurekastreams.web.client.ui.common.form.elements.FormElement;
import org.eurekastreams.web.client.ui.common.notifier.Notification;

import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

/**
 * Personal Settings Panel Composite.
 *
 */
public class NotificationsSettingsPanelComposite extends FlowPanel
{
    /** Form builder. */
    private FormBuilder form;

    /** List of notification preference categories for personal activities. */
    private static final Map<String, Category> PERSONAL_PREF_CATEGORIES = new LinkedHashMap<String, Category>();

    /** List of notification preference categories for group activities. */
    private static final Map<String, Category> GROUP_PREF_CATEGORIES = new LinkedHashMap<String, Category>();

    /** List of notification preference categories for organization activities. */
    private static final Map<String, Category> ORG_PREF_CATEGORIES = new LinkedHashMap<String, Category>();

    static
    {
        PERSONAL_PREF_CATEGORIES.put("Activity posted to your stream", Category.POST_TO_PERSONAL_STREAM);
        PERSONAL_PREF_CATEGORIES.put("Colleague likes activity you posted to your stream or a group stream",
		Category.LIKE);
        PERSONAL_PREF_CATEGORIES.put("Comment is posted to an activity in your stream or an activity "
                + "you posted to a group stream", Category.COMMENT);
        PERSONAL_PREF_CATEGORIES.put("Comment is posted to an activity you saved", Category.COMMENT_TO_SAVED_ACTIVITY);
        PERSONAL_PREF_CATEGORIES.put("New follower is added to your stream", Category.FOLLOW_PERSON);

        GROUP_PREF_CATEGORIES.put("Activity is posted to a group you joined", Category.POST_TO_JOINED_GROUP);
        GROUP_PREF_CATEGORIES.put("Activity is posted to a group you coordinate", Category.POST_TO_GROUP_STREAM);
        // GROUP_PREF_CATEGORIES.put("Comments", Category.COMMENT_IN_GROUP_STREAM);
        GROUP_PREF_CATEGORIES.put("New member joins a group you coordinate", Category.FOLLOW_GROUP);
        GROUP_PREF_CATEGORIES.put("Group Membership is requested in a private group you coordinate",
                Category.REQUEST_GROUP_ACCESS);
        GROUP_PREF_CATEGORIES.put("Your request for membership in a private group has been approved or denied",
                Category.REQUEST_GROUP_ACCESS_RESPONSE);

        ORG_PREF_CATEGORIES.put("Activity is Flagged in an organization you coordinate", Category.FLAG_ACTIVITY);
        ORG_PREF_CATEGORIES
                .put("New group is requested in an organization you coordinate", Category.REQUEST_NEW_GROUP);
    }

    /**
     * Constructor.
     */
    public NotificationsSettingsPanelComposite()
    {
        // UI setup
        addStyleName("personal-settings");

        // listen for model events
        Session.getInstance().getEventBus().addObserver(GotPersonalSettingsResponseEvent.class,
                new Observer<GotPersonalSettingsResponseEvent>()
                {
                    public void update(final GotPersonalSettingsResponseEvent ev)
                    {
                        generateForm(ev.getSettings(), ev.getSupport());
                    }
                });
        // request data
        PersonalSettingsModel.getInstance().fetch(null, true);
    }

    /**
     * Builds the form using the data supplied.
     *
     * @param inSettings
     *            User's settings.
     * @param inSupport
     *            Supporting data.
     */
    public void generateForm(final Map<String, Object> inSettings, final Map<String, Object> inSupport)
    {
        form = new FormBuilder("", PersonalSettingsModel.getInstance(), Method.UPDATE);
        form.addStyleName("notif-settings-form");
        form.turnOffChangeCheck();
        setupFormCommands();

        buildNotificationPreferencesSection((Collection<NotificationFilterPreferenceDTO>) inSettings
                .get(RetrieveSettingsResponse.SETTINGS_NOTIFICATION_FILTERS), (HashMap<String, String>) inSupport
                .get(RetrieveSettingsResponse.SUPPORT_NOTIFIER_TYPES));

        add(form);
    }

    /**
     * Builds the notification filter preference part of the form using the supplied data.
     *
     * @param filters
     *            User's filter selections.
     * @param notifiers
     *            List of available notifiers.
     */
    private void buildNotificationPreferencesSection(final Collection<NotificationFilterPreferenceDTO> filters,
            final HashMap<String, String> notifiers)
    {
        Label label;

        Panel panel = new FlowPanel();
        panel.addStyleName("notif-settings-panel");
        form.addWidget(panel);

        label = new Label("My Activity and Connections");
        label.addStyleName("form-label");
        panel.add(label);

        label = new Label("Eureka Streams will notify you when new activity has taken place that involves you.");
        label.addStyleName("instructions");
        panel.add(label);

        panel.add(buildNotificationFilterGrid(notifiers, PERSONAL_PREF_CATEGORIES, filters));

        label = new Label("My Groups' Activity and Connections");
        label.addStyleName("form-label");
        panel.add(label);

        label =
                new Label("Eureka Streams will notify you when new activity has taken place in the groups that "
                        + "you coordinate or groups that you have joined.");
        label.addStyleName("instructions");
        panel.add(label);

        panel.add(buildNotificationFilterGrid(notifiers, GROUP_PREF_CATEGORIES, filters));

        label = new Label("My Organizations' Activity and Connections");
        label.addStyleName("form-label");
        panel.add(label);

        label =
                new Label("Eureka Streams will notify you when new activity has "
                        + "taken place in the organizations that you coordinate.");
        label.addStyleName("instructions");
        panel.add(label);

        panel.add(buildNotificationFilterGrid(notifiers, ORG_PREF_CATEGORIES, filters));

        label = new InlineLabel("Email notifications will be sent to: ");
        label.addStyleName("notif-email-caption");
        panel.add(label);

        label = new InlineLabel(Session.getInstance().getCurrentPerson().getEmail());
        label.addStyleName("notif-email-value");
        panel.add(label);
    }

    /**
     * Builds the personal/group grid for notification preferences.
     *
     * @param notifiers
     *            List of notifiers.
     * @param categories
     *            Names and enum values of the notification preference categories to include in this grid.
     * @param filters
     *            User's current notification preferences.
     * @return Grid.
     */
    private Grid buildNotificationFilterGrid(final Map<String, String> notifiers,
            final Map<String, Category> categories, final Collection<NotificationFilterPreferenceDTO> filters)
    {
        Grid grid = new Grid(1 + categories.size(), 1 + notifiers.size());
        grid.addStyleName("notif-grid");

        // display each category name (one per row)
        int row = 0;
        for (String categoryName : categories.keySet())
        {
            row++;
            grid.setText(row, 0, categoryName);
        }

        // display each notifier column
        int col = 0;
        for (Map.Entry<String, String> entry : notifiers.entrySet())
        {
            col++;

            // display the names of the notifiers
            grid.setText(0, col, entry.getValue());
            grid.getColumnFormatter().addStyleName(col, "notif-selection-column");

            // create the checkboxes for that notifier per category
            row = 0;
            for (Category category : categories.values())
            {
                row++;
                setupNotificationFilterCheckbox(grid, row, col, entry.getKey(), category, filters);
            }
        }

        return grid;
    }

    /**
     * Creates an sets up a given checkbox.
     *
     * @param grid
     *            The grid in which the checkbox goes.
     * @param col
     *            The column in which the checkbox goes.
     * @param row
     *            The row in which the checkbox goes.
     * @param notifierType
     *            The notifier type the checkbox represents.
     * @param category
     *            The category the checkbox represents
     * @param prefs
     *            The list of preferences.
     */
    private void setupNotificationFilterCheckbox(final Grid grid, final int row, final int col,
            final String notifierType, final NotificationFilterPreference.Category category,
            final Collection<NotificationFilterPreferenceDTO> prefs)
    {
        NotificationPreferenceFormElement elem = new NotificationPreferenceFormElement(notifierType, category);
        grid.setWidget(row, col, elem.getWidget());
        form.addFormElement(elem);

        // determine initial state - certainly not the most efficient, but the list should be really short
        for (NotificationFilterPreferenceDTO pref : prefs)
        {
            if (pref.getNotificationCategory().equals(category) && pref.getNotifierType().equals(notifierType))
            {
                elem.getWidget().setValue(false);
                break;
            }
        }
    }

    /**
     * Configures the form builder for submit and cancel.
     */
    private void setupFormCommands()
    {
        if (form == null)
        {
            return;
        }

        Session.getInstance().getEventBus().addObserver(UpdatedPersonalSettingsResponseEvent.class,
                new Observer<UpdatedPersonalSettingsResponseEvent>()
                {
                    public void update(final UpdatedPersonalSettingsResponseEvent arg1)
                    {
                        form.onSuccess();
                        Session.getInstance().getEventBus().notifyObservers(
                                new ShowNotificationEvent(new Notification("Settings saved")));
                    }

                });

        form.setOnCancelHistoryToken(Session.getInstance().generateUrl(new CreateUrlRequest(Page.START)));
    }

    /**
     * Form element specific to notification preference checkboxes to allow prefs to be set via FormBuilder approach.
     * Does not inherit from a widget so that the FormBuilder will not try to add it to the panel.
     */
    public static class NotificationPreferenceFormElement implements FormElement
    {
        /** Notifier type. */
        private final String notifierType;

        /** Category. */
        private final NotificationFilterPreference.Category category;

        /** Checkbox. */
        private final CheckBox checkbox = new CheckBox();

        /**
         * Constructor.
         *
         * @param inNotifierType
         *            Notifier type.
         * @param inCategory
         *            Category.
         */
        public NotificationPreferenceFormElement(final String inNotifierType, final Category inCategory)
        {
            notifierType = inNotifierType;
            category = inCategory;
            checkbox.setValue(true);
        }

        /**
         * {@inheritDoc}
         */
        public String getKey()
        {
            return "notif-" + notifierType + "-" + category.name();
        }

        /**
         * {@inheritDoc}
         */
        public Serializable getValue()
        {
            return checkbox.getValue() ? null : new NotificationFilterPreferenceDTO(notifierType, category);
        }

        /**
         * {@inheritDoc}
         */
        public void onError(final String inErrMessage)
        {
        }

        /**
         * {@inheritDoc}
         */
        public void onSuccess()
        {
            // nothing to do
        }

        /**
         * @return The widget.
         */
        CheckBox getWidget()
        {
            return checkbox;
        }
    }

}
