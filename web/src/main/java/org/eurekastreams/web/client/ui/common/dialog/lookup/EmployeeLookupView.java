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

import java.util.List;

import org.eurekastreams.commons.client.ui.WidgetCommand;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.web.client.ui.Bindable;
import org.eurekastreams.web.client.ui.ModelChangeListener;
import org.eurekastreams.web.client.ui.common.PersonPanel;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * The controller for a LoginContentFacade.
 */
public class EmployeeLookupView implements Bindable
{
    /**
     * The controller.
     */
    private EmployeeLookupController controller;

    /**
     * The model.
     */
    private EmployeeLookupModel model;

    /**
     * The close command.
     */
    private WidgetCommand closeCommand = null;

    /**
     * The last name text box.
     */
    TextBox lastName;

    /**
     * The results of the search.
     */
    ListBox results;

    /**
     * Search button.
     */
    Hyperlink search;

    /**
     * Search button.
     */
    Hyperlink select;

    /**
     * Search button.
     */
    Hyperlink cancel;

    /**
     * Results description.
     */
    Label resultsDesc;

    /**
     * The person container.
     */
    FlowPanel personContainer;

    /**
     * The save command.
     */
    private Command saveCommand;

    /**
     * The widget.
     */
    private EmployeeLookupContent widget;

    /**
     * Default constructor.
     * 
     * @param inWidget
     *            the widget.
     * @param inModel
     *            the model to use.
     * 
     * @param inController
     *            the controller.
     * @param inSaveCommand
     *            command object that saves the lookup results
     * 
     */
    public EmployeeLookupView(final EmployeeLookupContent inWidget, final EmployeeLookupModel inModel,
            final EmployeeLookupController inController, final Command inSaveCommand)
    {
        widget = inWidget;
        model = inModel;
        controller = inController;
        saveCommand = inSaveCommand;
    }

    /**
     * Initialize the view.
     */
    public void init()
    {
        model.addChangeListener(EmployeeLookupModel.PropertyChangeEvent.RESULTS_CHANGED, new ModelChangeListener()
        {

            public void onChange()
            {
                onPeopleResultsUpdated();
            }

        });

        model.addChangeListener(EmployeeLookupModel.PropertyChangeEvent.SELECTION_CHANGED, new ModelChangeListener()
        {

            public void onChange()
            {
                onSelectedPersonChanged();
            }

        });

        model.addChangeListener(EmployeeLookupModel.PropertyChangeEvent.SEARCH_BUTTON_STATUS_CHANGED,
                new ModelChangeListener()
                {
                    public void onChange()
                    {
                        onSearchActiveChanged();
                    }

                });

        model.addChangeListener(EmployeeLookupModel.PropertyChangeEvent.SELECT_BUTTON_STATUS_CHANGED,
                new ModelChangeListener()
                {

                    public void onChange()
                    {
                        onSelectActiveChanged();
                    }

                });

        controller.registerResults(results);
        controller.registerSearchButton(search);
        controller.registerLastNameTextBox(lastName);
    }

    /**
     * Called when the last name changes.
     */
    public void onSearchActiveChanged()
    {
        if (model.getSearchButtonStatus())
        {
            search.removeStyleName("lookup-search-button-inactive");
            search.addStyleName("lookup-search-button-active");
        }
        else
        {
            search.removeStyleName("lookup-search-button-active");
            search.addStyleName("lookup-search-button-inactive");
        }
    }

    /**
     * Called when select active status is changed.
     */
    void onSelectActiveChanged()
    {
        if (model.isSelectButtonActive())
        {
            select.removeStyleName("lookup-select-button-inactive");
            select.addStyleName("lookup-select-button-active");
        }
        else
        {
            select.removeStyleName("lookup-select-button-active");
            select.addStyleName("lookup-select-button-inactive");
        }
    }

    /**
     * Get the text box with the value to search for.
     * 
     * @return the text box.
     */
    public TextBox getSearchBox()
    {
        return lastName;
    }

    /**
     * Updates due to selected person changed.
     */
    public void onSelectedPersonChanged()
    {
        Person selected = model.getSelectedPerson();
        final PersonPanel personPanel = widget.getPerson(selected.toPersonModelView());
        personContainer.clear();
        personContainer.add(personPanel);
    }

    /**
     * Response to updated model results.
     */
    public void onPeopleResultsUpdated()
    {
        results.clear();

        List<Person> resultsItems = model.getPeopleResults();

        for (Person person : resultsItems)
        {
            results.addItem(person.getLastName()
                    + ", "
                    + person.getFirstName()
                    + " "
                    + ((person.getMiddleName() == null || person.getMiddleName().isEmpty()) ? "" : person
                            .getMiddleName()), person.getAccountId());
        }

        String message = "";

        if (model.getMoreResultsExist())
        {
            message = "Greater than " + model.getResultLimit() + " results.";
        }
        else
        {
            message = "Displaying " + model.getPeopleResults().size() + " matches";
        }

        resultsDesc.setText(message);
        
        if (resultsItems.size() == 1)
        {
            results.setItemSelected(0, true);
            model.setSelectedPersonByAccountId(results.getValue(0));
        }
    }

    /**
     * The command to call to close the dialog.
     * 
     * @param command
     *            the close command.
     */
    public void setCloseCommand(final WidgetCommand command)
    {
        closeCommand = command;
        controller.registerCancelButton(cancel, closeCommand);
        controller.registerSelectButton(select, closeCommand, saveCommand);
    }
}
