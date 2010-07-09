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

import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ui.WidgetCommand;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.ui.Bindable;
import org.eurekastreams.web.client.ui.PropertyMapper;
import org.eurekastreams.web.client.ui.common.PersonPanel;
import org.eurekastreams.web.client.ui.common.dialog.DialogContent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
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
public class EmployeeLookupContent implements DialogContent, Bindable
{
    /**
     * The lookup form.
     */
    FormPanel lookupForm;

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
     * The view.
     */
    private EmployeeLookupView view;

    /**
     * The model.
     */
    private EmployeeLookupModel model;

    /**
     * Default constructor. Builds up widgets.
     * 
     * @param saveCommand
     *            command object that is invoked once an employee is selected
     * @param inProcessor
     *            ActionProcessor for retrieving employee information from the server
     */
    public EmployeeLookupContent(final Command saveCommand, final ActionProcessor inProcessor)
    {

        lookupForm = new FormPanel();

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

        lastName = new TextBox();
        lastNamePanel.add(lastName);

        search = new Hyperlink("Search", History.getToken());
        search.addStyleName("search-list-button");
        lastNamePanel.add(search);

        lookupPanel.add(lastNamePanel);

        results = new ListBox();
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

        personContainer = new FlowPanel();
        personContainer.addStyleName("person-container");

        lookupPanelContainer.add(personContainer);

        resultsDesc = new Label("");
        resultsDesc.addStyleName("results-description");

        lookupPanelContainer.add(resultsDesc);

        lookupPanelContainer.add(buttonArea);

        lookupForm.add(lookupPanelContainer);

        // Create and initialize the controller.

        model = new EmployeeLookupModel(inProcessor);
        EmployeeLookupController controller = new EmployeeLookupController(model);
        view = new EmployeeLookupView(this, model, controller, saveCommand);

        PropertyMapper mapper = new PropertyMapper(GWT.create(EmployeeLookupContent.class), GWT
                .create(EmployeeLookupView.class));

        mapper.bind(this, view);

        view.init();
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
        view.setCloseCommand(command);
    }

    /**
     * Call the close command.
     */
    public void close()
    {
        closeCommand.execute();
    }

    /**
     * Sets the show command.
     * 
     * @param inShowCommand
     *            the command to use.
     */
    public void setShowCommand(final WidgetCommand inShowCommand)
    {

    }

    /**
     * Provides a hook to fire off events when the dialog is shown.
     */
    public void show()
    {
        lastName.setFocus(true);
    }

    /**
     * Gets the selected person.
     * 
     * @return the selected Person
     */
    public Person getPerson()
    {
        return model.getSelectedPerson();
    }

    /**
     * Return a person panel.
     * 
     * @param personModelView
     *            the model view.
     * @return the panel
     */
    public PersonPanel getPerson(final PersonModelView personModelView)
    {
        return new PersonPanel(personModelView, false, false, false, true);
    }
}
