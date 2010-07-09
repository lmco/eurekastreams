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
package org.eurekastreams.web.client.ui.common.dialog.lookup;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ActionRequestImpl;
import org.eurekastreams.server.action.request.PersonLookupRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.web.client.ui.AbstractModel;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Model for employee lookup modal.
 */
public class EmployeeLookupModel extends AbstractModel
{
    /**
     * The maximum number of results to bring back from the server.
     */
    private static final int MAX_RESULTS = 100;

    /**
     * Number of results to show.
     */
    private static final int RESULT_LIMIT = 50;

    /**
     * The events.
     */
    public enum PropertyChangeEvent
    {
        /**
         * Last name changed.
         */
        LAST_NAME_CHANGED,
        /**
         * Results changes.
         */
        RESULTS_CHANGED,
        /**
         * Selected person changed.
         */
        SELECTION_CHANGED,
        /**
         * Change to search button active status.
         */
        SEARCH_BUTTON_STATUS_CHANGED,
        /**
         * Change to select button active status.
         */
        SELECT_BUTTON_STATUS_CHANGED
    }

    /**
     * Last name to search for.
     */
    private String lastName = "";

    /**
     * Action processor.
     */
    private ActionProcessor processor;

    /**
     * Selected person.
     */
    private Person selectedPerson = null;

    /**
     * Results of search.
     */
    private List<Person> results = new ArrayList<Person>();

    /**
     * Status of the select button.
     */
    boolean selectButtonActive = false;

    /**
     * Status of the search (go) button.
     */
    boolean searchButtonActive = false;

    /**
     * Constructor.
     * 
     * @param inProcessor
     *            action processor.
     */
    public EmployeeLookupModel(final ActionProcessor inProcessor)
    {
        processor = inProcessor;
    }

    /**
     * Changes the active status of the search button.
     * 
     * @param active
     *            true for active.
     */
    public void changeSearchButtonStatus(final boolean active)
    {
        searchButtonActive = active;
        notifyChangeListeners(PropertyChangeEvent.SEARCH_BUTTON_STATUS_CHANGED);
    }

    /**
     * Changes the active status of the select button.
     * 
     * @param active
     *            true for active.
     */
    public void changeSelectButtonStatus(final boolean active)
    {
        selectButtonActive = active;
        notifyChangeListeners(PropertyChangeEvent.SELECT_BUTTON_STATUS_CHANGED);
    }

    /**
     * Set the last name.
     * 
     * @param inLastName
     *            the new value.
     */
    public void setLastName(final String inLastName)
    {
        lastName = inLastName;

        changeSearchButtonStatus(lastName.length() > 0);

        notifyChangeListeners(PropertyChangeEvent.LAST_NAME_CHANGED);
    }

    /**
     * Get the last name.
     * 
     * @return the value.
     */
    public String getLastName()
    {
        return lastName;
    }

    /**
     * Get the results.
     * 
     * @return the value.
     */
    public List<Person> getPeopleResults()
    {
        /**
         * List.subList not implemented in GWT :(
         */

        List<Person> people = new ArrayList<Person>();

        for (Person p : results)
        {
            if (people.size() >= RESULT_LIMIT)
            {
                break;
            }

            people.add(p);
        }

        return people;
    }

    /**
     * Get the selected person.
     * 
     * @return the selected person.
     */
    public Person getSelectedPerson()
    {
        return selectedPerson;
    }

    /**
     * Get the status of the search button.
     * 
     * @return the search button status.
     */
    public boolean getSearchButtonStatus()
    {
        return searchButtonActive;
    }

    /**
     * Get the status of the select button.
     * 
     * @return the select button status.
     */
    public boolean isSelectButtonActive()
    {
        return selectButtonActive;
    }

    /**
     * Sets the selected person.
     * 
     * @param inAccountId
     *            the account ID of the selected person.
     */
    public void setSelectedPersonByAccountId(final String inAccountId)
    {
        String accountId = inAccountId;

        for (Person person : results)
        {
            if (person.getAccountId().equals(accountId))
            {
                selectedPerson = person;
                notifyChangeListeners(PropertyChangeEvent.SELECTION_CHANGED);

                changeSelectButtonStatus(true);

                return;
            }
        }

        changeSelectButtonStatus(false);
    }

    /**
     * Returns true if more results exist.
     * 
     * @return true for more results existing.
     */
    public boolean getMoreResultsExist()
    {
        return results.size() > RESULT_LIMIT;
    }

    /**
     * Gets the limit on the number of results.
     * 
     * @return the limit on the number of results.
     */
    public int getResultLimit()
    {
        return RESULT_LIMIT;
    }

    /**
     * Update the search results.
     */
    public void updateResults()
    {
        if (searchButtonActive)
        {
            PersonLookupRequest request = new PersonLookupRequest(lastName, new Integer(MAX_RESULTS));
            processor.makeRequest(new ActionRequestImpl<Person>("personLookup", request),
                    new AsyncCallback<List<Person>>()
                    {
                        public void onFailure(final Throwable caught)
                        {
                        }

                        public void onSuccess(final List<Person> people)
                        {
                            results = people;

                            if (results == null)
                            {
                                results = new ArrayList<Person>();
                            }

                            notifyChangeListeners(PropertyChangeEvent.RESULTS_CHANGED);
                        }
                    });
        }
    }
}
