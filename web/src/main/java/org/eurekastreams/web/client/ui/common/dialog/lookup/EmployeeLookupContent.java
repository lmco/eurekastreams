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
package org.eurekastreams.web.client.ui.common.dialog.lookup;

import org.eurekastreams.commons.client.ui.WidgetCommand;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.model.PersonLookupModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.PersonPanel;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog for looking up an employee.
 */
public class EmployeeLookupContent implements DialogContent
{
    /** The lookup form. */
    private final FormPanel lookupForm = new FormPanel();

    /** The close command. */
    private WidgetCommand closeCommand = null;

    /** The last name text box. */
    private final TextBox lastName = new TextBox();

    /** The results of the search. */
    private final ListBox results = new ListBox();

    /** Search button. */
    private Hyperlink search;

    /** Search button. */
    private Hyperlink select;

    /** Search button. */
    private Hyperlink cancel;

    /** Results description. */
    private final Label resultsDesc = new Label();

    /** The person container. */
    private final FlowPanel personContainer = new FlowPanel();

    /** Command to save the dialog contents. */
    private final Command saveCommand;

    /** The ViewModel (MVVM). */
    private final EmployeeLookupViewModel viewModel;

    /**
     * Default constructor. Builds up widgets.
     *
     * @param inSaveCommand
     *            command object that is invoked once an employee is selected
     */
    public EmployeeLookupContent(final Command inSaveCommand)
    {
        saveCommand = inSaveCommand;

        setupWidgets();
        setupEvents();

        viewModel = new EmployeeLookupViewModel(this, PersonLookupModel.getInstance(), Session.getInstance()
                .getEventBus());
        viewModel.init();
    }

    /**
     * Builds the UI.
     */
    private void setupWidgets()
    {
        final FlowPanel lookupPanelContainer = new FlowPanel();
        lookupPanelContainer.addStyleName("lookup-container");

        final FlowPanel lookupPanel = new FlowPanel();

        Label lookupDesc = new Label("Enter the last name, first name of the user you want to lookup. "
                + "If there are greater than 50 results, please enter more of the last name and first name.");
        lookupDesc.addStyleName("lookup-description");

        lookupPanelContainer.add(lookupDesc);

        lookupPanel.addStyleName("lookup");

        final FlowPanel lastNamePanel = new FlowPanel();
        lastNamePanel.addStyleName("search-list");

        lastNamePanel.add(lastName);

        search = new Hyperlink("Search", History.getToken());
        search.addStyleName("search-list-button");
        lastNamePanel.add(search);

        lookupPanel.add(lastNamePanel);

        results.setName("results");
        results.addStyleName("results");
        results.setVisibleItemCount(5);
        lookupPanel.add(results);

        final FlowPanel buttonArea = new FlowPanel();
        buttonArea.addStyleName("button-area");

        select = new Hyperlink("Select", History.getToken());
        select.addStyleName("lookup-select-button-inactive");
        buttonArea.add(select);

        cancel = new Hyperlink("Cancel", History.getToken());
        cancel.addStyleName("lookup-cancel-button");
        buttonArea.add(cancel);

        lookupPanelContainer.add(lookupPanel);

        personContainer.addStyleName("person-container");

        lookupPanelContainer.add(personContainer);

        resultsDesc.addStyleName("results-description");

        lookupPanelContainer.add(resultsDesc);

        lookupPanelContainer.add(buttonArea);

        lookupForm.add(lookupPanelContainer);
    }

    /**
     * Wires up events.
     */
    private void setupEvents()
    {
        cancel.addClickHandler(new ClickHandler()
        {
            public void onClick(final ClickEvent inArg0)
            {
                close();
            }
        });
    }

    /**
     * The title of the login dialog.
     *
     * @return the title.
     */
    public final String getTitle()
    {
        return "Employee Lookup";
    }

    /**
     * The login form.
     *
     * @return the login form.
     */
    public final Widget getBody()
    {
        return lookupForm;
    }

    /**
     * The CSS class to use for this dialog.
     *
     * @return the name of the CSS class to use.
     */
    public String getCssName()
    {
        return "employee-lookup-dialog";
    }

    /**
     * Returns the form panel.
     *
     * @return the form panel.
     */
    public FormPanel getFormPanel()
    {
        return lookupForm;
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
    }

    /**
     * Call the close command.
     */
    public void close()
    {
        closeCommand.execute();
    }

    /**
     * Provides a hook to fire off events when the dialog is shown.
     */
    public void show()
    {
        DeferredCommand.addCommand(new Command()
        {
            public void execute()
            {
                lastName.setFocus(true);
            }
        });
    }

    /**
     * Gets the selected person.
     *
     * @return the selected Person
     */
    public PersonModelView getPerson()
    {
        return viewModel.getSelectedPerson();
    }

    /**
     * Update the selected person area to display the given person.
     *
     * @param personModelView
     *            Person to display.
     */
    public void showSelectedPerson(final PersonModelView personModelView)
    {
        personContainer.clear();
        if (personModelView != null)
        {
            personContainer.add(new PersonPanel(personModelView, false, false, false, true));
        }
    }

    /**
     * @return the lastName
     */
    TextBox getLastName()
    {
        return lastName;
    }

    /**
     * @return the results
     */
    ListBox getResults()
    {
        return results;
    }

    /**
     * @return the search
     */
    Hyperlink getSearch()
    {
        return search;
    }

    /**
     * @return the select
     */
    Hyperlink getSelect()
    {
        return select;
    }

    /**
     * @return the resultsDesc
     */
    Label getResultsDesc()
    {
        return resultsDesc;
    }

    /**
     * @return the saveCommand
     */
    Command getSaveCommand()
    {
        return saveCommand;
    }
}
