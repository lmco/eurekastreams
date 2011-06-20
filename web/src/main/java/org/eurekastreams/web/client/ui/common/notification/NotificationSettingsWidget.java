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
package org.eurekastreams.web.client.ui.common.notification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.data.GotNotificationFilterPreferencesResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedNotificationFilterPreferencesResponseEvent;
import org.eurekastreams.web.client.model.NotificationFilterPreferencesModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.notifier.Notification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Personal Settings Panel Composite.
 */
public class NotificationSettingsWidget extends Composite
{
    /** Binder for building UI. */
    private static LocalUiBinder binder = GWT.create(LocalUiBinder.class);

    /** List of notification preference categories. */
    private static final Map<String, String> PREF_CATEGORIES = new LinkedHashMap<String, String>();

    static
    {
        PREF_CATEGORIES.put("POST_TO_PERSONAL_STREAM", "Someone posts on my stream");
        PREF_CATEGORIES.put("LIKE", "Someone likes one of my posts");
        PREF_CATEGORIES.put("COMMENT", "Someone comments on one of my posts or a post I've commented on");
        PREF_CATEGORIES.put("FOLLOW", "Someone follows my stream or a group I coordinate");
    }

    /** Index of all the checkboxes by controlling preference. */
    private final Map<NotificationFilterPreferenceDTO, HasValue<Boolean>> checkboxIndex = // \n
    new TreeMap<NotificationFilterPreferenceDTO, HasValue<Boolean>>(new Comparator<NotificationFilterPreferenceDTO>()
    {
        public int compare(final NotificationFilterPreferenceDTO inO1, final NotificationFilterPreferenceDTO inO2)
        {
            int cmp = inO1.getNotifierType().compareTo(inO2.getNotifierType());
            return cmp != 0 ? cmp : inO1.getNotificationCategory().compareTo(inO2.getNotificationCategory());
        }
    });

    /** Local styles. */
    @UiField
    LocalStyle style;

    /** UI element acting as the back button. */
    @UiField
    Label backButton;

    /** UI element acting as the save button. */
    @UiField
    Label saveButton;

    /** UI element acting as the cancel button. */
    @UiField
    Label cancelButton;

    /** Main settings grid. */
    @UiField
    Grid settingsGrid;

    /** Command widget will invoke to request it be closed/removed. */
    private Command closeCommand;

    /** Observer (for unwiring). */
    private final Observer<UpdatedNotificationFilterPreferencesResponseEvent> settingsSavedObserver = // \n
    new Observer<UpdatedNotificationFilterPreferencesResponseEvent>()
    {
        public void update(final UpdatedNotificationFilterPreferencesResponseEvent arg1)
        {
            EventBus.getInstance().notifyObservers(new ShowNotificationEvent(new Notification("Settings saved")));
            closeCommand.execute();
        }
    };
    /** Observer (for unwiring). */
    private final Observer<GotNotificationFilterPreferencesResponseEvent> gotDataObserver = // \n
    new Observer<GotNotificationFilterPreferencesResponseEvent>()
    {
        public void update(final GotNotificationFilterPreferencesResponseEvent ev)
        {
            EventBus.getInstance().removeObserver(ev, this);
            buildNotificationFilterGrid(ev.getResponse().getNotifierTypes(), PREF_CATEGORIES, ev.getResponse()
                    .getPreferences());
        }
    };

    /**
     * Constructor.
     *
     * @param showBack
     *            If the back button should be shown.
     */
    public NotificationSettingsWidget(final boolean showBack)
    {
        initWidget(binder.createAndBindUi(this));
        if (!showBack)
        {
            backButton.removeFromParent();
        }

        // listen for model events
        final EventBus eventBus = Session.getInstance().getEventBus();

        eventBus.addObserver(UpdatedNotificationFilterPreferencesResponseEvent.class, settingsSavedObserver);
        eventBus.addObserver(GotNotificationFilterPreferencesResponseEvent.class, gotDataObserver);

        // request data
        NotificationFilterPreferencesModel.getInstance().fetch(null, true);
    }

    /**
     * Provides the command the widget will invoke to request it be closed/removed.
     *
     * @param inCloseCommand
     *            the command.
     */
    public void setCloseCommand(final Command inCloseCommand)
    {
        closeCommand = inCloseCommand;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDetach()
    {
        super.onDetach();
        final EventBus eventBus = EventBus.getInstance();
        eventBus.removeObserver(UpdatedNotificationFilterPreferencesResponseEvent.class, settingsSavedObserver);
        eventBus.removeObserver(GotNotificationFilterPreferencesResponseEvent.class, gotDataObserver);
    }

    /**
     * Gathers settings and sends to model to send to server.
     *
     * @param ev
     *            Event.
     */
    @UiHandler("saveButton")
    void saveChanges(final ClickEvent ev)
    {
        ArrayList<NotificationFilterPreferenceDTO> selected = new ArrayList<NotificationFilterPreferenceDTO>();
        for (Entry<NotificationFilterPreferenceDTO, HasValue<Boolean>> entry : checkboxIndex.entrySet())
        {
            if (!entry.getValue().getValue())
            {
                selected.add(entry.getKey());
            }
        }

        NotificationFilterPreferencesModel.getInstance().update(selected);
    }

    /**
     * Requests the widget be closed.
     *
     * @param ev
     *            Event.
     */
    @UiHandler({ "cancelButton", "backButton" })
    void cancel(final ClickEvent ev)
    {
        closeCommand.execute();
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
            final Map<String, String> categories, final Collection<NotificationFilterPreferenceDTO> filters)
    {
        // Grid grid = new Grid(1 + categories.size(), 1 + notifiers.size());
        // grid.addStyleName(StaticResourceBundle.INSTANCE.coreCss().notifGrid());
        Grid grid = settingsGrid;
        grid.resize(1 + categories.size(), 1 + notifiers.size());

        // display each category name (one per row)
        int row = 0;
        for (String categoryName : categories.values())
        {
            row++;
            grid.setText(row, 0, categoryName);
        }

        // display each notifier column
        int col = 0;
        for (Map.Entry<String, String> entry : notifiers.entrySet())
        {
            col++;
            final String notifierType = entry.getKey();

            // display the names of the notifiers
            grid.setText(0, col, entry.getValue());
            grid.getColumnFormatter().addStyleName(col, style.gridColumn());

            // create the checkboxes for that notifier per category
            row = 0;
            for (String category : categories.keySet())
            {
                row++;

                CheckBox checkBox = new CheckBox();
                checkBox.setValue(true);
                grid.setWidget(row, col, checkBox);
                checkboxIndex.put(new NotificationFilterPreferenceDTO(notifierType, category), checkBox);
            }
        }

        // uncheck checkboxes for suppressed entries
        for (NotificationFilterPreferenceDTO pref : filters)
        {
            pref.setPersonId(0);
            HasValue<Boolean> checkBox = checkboxIndex.get(pref);
            if (checkBox != null)
            {
                checkBox.setValue(false);
            }
        }

        return grid;
    }

    /**
     * Local styles.
     */
    interface LocalStyle extends CssResource
    {
        /** @return Settings grid column style. */
        @ClassName("grid-column")
        String gridColumn();
    }

    /**
     * Binder for building UI.
     */
    interface LocalUiBinder extends UiBinder<Widget, NotificationSettingsWidget>
    {
    }
}
