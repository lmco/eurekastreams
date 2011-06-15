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
package org.eurekastreams.web.client.ui.pages.settings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eurekastreams.server.action.response.notification.GetUserNotificationFilterPreferencesResponse;
import org.eurekastreams.server.domain.NotificationFilterPreferenceDTO;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ShowNotificationEvent;
import org.eurekastreams.web.client.events.data.GotNotificationFilterPreferencesResponseEvent;
import org.eurekastreams.web.client.events.data.UpdatedNotificationFilterPreferencesResponseEvent;
import org.eurekastreams.web.client.model.NotificationFilterPreferencesModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.notifier.Notification;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;

/**
 * Personal Settings Panel Composite.
 */
public class NotificationsSettingsPanelComposite extends FlowPanel
{
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

    /**
     * Constructor.
     */
    public NotificationsSettingsPanelComposite()
    {
        // UI setup
        addStyleName(StaticResourceBundle.INSTANCE.coreCss().personalSettings());

        // listen for model events
        final EventBus eventBus = Session.getInstance().getEventBus();

        eventBus.addObserver(UpdatedNotificationFilterPreferencesResponseEvent.class,
                new Observer<UpdatedNotificationFilterPreferencesResponseEvent>()
                {
                    public void update(final UpdatedNotificationFilterPreferencesResponseEvent arg1)
                    {
                        eventBus.notifyObservers(new ShowNotificationEvent(new Notification("Settings saved")));
                    }

                });

        eventBus.addObserver(GotNotificationFilterPreferencesResponseEvent.class,
                new Observer<GotNotificationFilterPreferencesResponseEvent>()
                {
                    public void update(final GotNotificationFilterPreferencesResponseEvent ev)
                    {
                        eventBus.removeObserver(ev, this);
                        populatePage(ev.getResponse());
                    }
                });

        // request data
        NotificationFilterPreferencesModel.getInstance().fetch(null, true);
    }

    /**
     * Populates the page with preference options.
     *
     * @param response
     *            The preference information response from the server.
     */
    private void populatePage(final GetUserNotificationFilterPreferencesResponse response)
    {
        // response.getNotifierTypes()
        add(buildNotificationFilterGrid(response.getNotifierTypes(), PREF_CATEGORIES, response.getPreferences()));

        Label saveButton = new Label("Save Changes");
        saveButton.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent inEvent)
            {
                saveChanges();
            }
        });
        add(saveButton);
    }

    /**
     * Gathers settings and sends to model to send to server.
     */
    private void saveChanges()
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
        Grid grid = new Grid(1 + categories.size(), 1 + notifiers.size());
        grid.addStyleName(StaticResourceBundle.INSTANCE.coreCss().notifGrid());

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
            grid.getColumnFormatter().addStyleName(col, "notif-selection-column");

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
}
