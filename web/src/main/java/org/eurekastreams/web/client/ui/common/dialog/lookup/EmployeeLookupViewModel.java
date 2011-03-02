/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.web.client.ui.common.dialog.lookup;

import java.util.List;

import org.eurekastreams.server.action.request.PersonLookupRequest;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.events.EventBus;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.data.GotPersonLookupResponseEvent;
import org.eurekastreams.web.client.model.Fetchable;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Manages state and logic for the Employee Lookup control (adapted from MVVM pattern).
 */
public class EmployeeLookupViewModel
{
    /** The maximum number of results to display. */
    private static final int MAX_RESULTS = 50;

    /** The view. */
    private final EmployeeLookupContent view;

    /** The model. */
    private final Fetchable<PersonLookupRequest> model;

    /** The event bus. */
    private final EventBus eventBus;

    /** Currently-selected person. */
    private PersonModelView selectedPerson;

    /** List of people returned from the last query. */
    private List<PersonModelView> people;

    /**
     * Constructor.
     *
     * @param inView
     *            The view.
     * @param inModel
     *            The model.
     * @param inEventBus
     *            The event bus.
     */
    public EmployeeLookupViewModel(final EmployeeLookupContent inView, final Fetchable<PersonLookupRequest> inModel,
            final EventBus inEventBus)
    {
        view = inView;
        model = inModel;
        eventBus = inEventBus;
    }

    /**
     * Initialize.
     */
    public void init()
    {
        // user clicked search button
        view.getSearch().addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent inArg0)
            {
                String searchText = view.getLastName().getText();
                if (!searchText.isEmpty())
                {
                    model.fetch(new PersonLookupRequest(searchText, MAX_RESULTS + 1), true);
                }
            }
        });

        // user pressed a key
        view.getLastName().addKeyUpHandler(new KeyUpHandler()
        {
            public void onKeyUp(final KeyUpEvent ev)
            {
                String searchText = view.getLastName().getText();
                boolean anyText = !searchText.isEmpty();
                setSearchButtonStatus(anyText);
                if (anyText && ev.getNativeKeyCode() == KeyCodes.KEY_ENTER && !ev.isAnyModifierKeyDown())
                {
                    model.fetch(new PersonLookupRequest(searchText, MAX_RESULTS + 1), true);
                }
            }
        });

        // user selected a person in the list
        view.getResults().addChangeHandler(new ChangeHandler()
        {
            public void onChange(final ChangeEvent ev)
            {
                selectedPerson = null;
                if (people != null)
                {
                    final int selectedIndex = view.getResults().getSelectedIndex();
                    if (selectedIndex >= 0 && selectedIndex < people.size())
                    {
                        selectedPerson = people.get(selectedIndex);
                    }
                }
                updateOnPersonSelected();
            }
        });

        // user clicked the select button
        view.getSelect().addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent inArg0)
            {
                if (selectedPerson != null)
                {
                    view.close();
                    view.getSaveCommand().execute();
                }
            }
        });

        // received query results from server
        eventBus.addObserver(GotPersonLookupResponseEvent.class, new Observer<GotPersonLookupResponseEvent>()
        {
            public void update(final GotPersonLookupResponseEvent ev)
            {
                people = ev.getResponse();

                int count;
                String message;
                if (people.size() > MAX_RESULTS)
                {
                    count = MAX_RESULTS;
                    message = "Greater than " + MAX_RESULTS + " results.";
                }
                else
                {
                    count = people.size();
                    message = "Displaying " + count + " matches";
                }
                view.getResultsDesc().setText(message);

                ListBox results = view.getResults();
                results.clear();

                for (int i = 0; i < count; i++)
                {
                    PersonModelView person = people.get(i);
                    results.addItem(person.getLastName() + ", " + person.getPreferredName(), person.getAccountId());
                }

                if (count == 1)
                {
                    results.setItemSelected(0, true);
                    selectedPerson = people.get(0);
                }
                else
                {
                    selectedPerson = null;
                }
                updateOnPersonSelected();
            }
        });
    }

    /**
     * @return The selected person.
     */
    public PersonModelView getSelectedPerson()
    {
        return selectedPerson;
    }

    /**
     * Updates the view to reflect the currently-selected person.
     */
    private void updateOnPersonSelected()
    {
        view.showSelectedPerson(selectedPerson);
        Widget select = view.getSelect();
        if (selectedPerson != null)
        {
            select.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().lookupSelectButtonInactive());
            select.addStyleName(StaticResourceBundle.INSTANCE.coreCss().lookupSelectButtonActive());
        }
        else
        {
            select.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().lookupSelectButtonActive());
            select.addStyleName(StaticResourceBundle.INSTANCE.coreCss().lookupSelectButtonInactive());
        }
    }

    /**
     * Sets the enabled/disabled state of the search button.
     *
     * @param active
     *            If new state is active.
     */
    private void setSearchButtonStatus(final boolean active)
    {
        Widget search = view.getSearch();
        if (active)
        {
            search.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().lookupSelectButtonInactive());
            search.addStyleName(StaticResourceBundle.INSTANCE.coreCss().lookupSelectButtonActive());
        }
        else
        {
            search.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().lookupSelectButtonActive());
            search.addStyleName(StaticResourceBundle.INSTANCE.coreCss().lookupSelectButtonInactive());
        }
    }
}
