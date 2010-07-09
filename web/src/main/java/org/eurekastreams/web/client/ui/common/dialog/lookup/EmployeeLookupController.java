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

import org.eurekastreams.commons.client.ui.WidgetCommand;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Controller for employee lookup modal.
 */
public class EmployeeLookupController
{
    /**
     * Model.
     */
    private EmployeeLookupModel model;

    /**
     * Last name text box.
     */
    private TextBox lastNameBox;

    /**
     * Constructor.
     * 
     * @param inModel
     *            the model.
     */
    public EmployeeLookupController(final EmployeeLookupModel inModel)
    {
        model = inModel;
    }

    /**
     * Wire up a listener to the results list.
     * 
     * @param results
     *            the ListBox that contains the results of a search
     */
    public void registerResults(final ListBox results)
    {
        results.addChangeListener(new ChangeListener()
        {
            public void onChange(final Widget resultsBox)
            {
                model.setSelectedPersonByAccountId(results.getValue(results.getSelectedIndex()));
            }
        });
    }

    /**
     * Register the search button.
     * 
     * @param searchButton
     *            the search button.
     */
    public void registerSearchButton(final Hyperlink searchButton)
    {
        searchButton.addClickListener(new ClickListener()
        {
            public void onClick(final Widget sender)
            {
                model.setLastName(lastNameBox.getText());
                model.updateResults();
            }
        });
    }

    /**
     * Register the last name text box.
     * 
     * @param inLastNameBox
     *            the last name text box.
     */
    public void registerLastNameTextBox(final TextBox inLastNameBox)
    {
        lastNameBox = inLastNameBox;

        inLastNameBox.addKeyboardListener(new KeyboardListenerAdapter()
        {
            /**
             * Calls submit when enter is pressed.
             * 
             * @param sender
             *            the events sender.
             * @param keyCode
             *            the key that was pressed.
             * @param modifiers
             *            other keys pressed.
             */
            public void onKeyUp(final Widget sender, final char keyCode, final int modifiers)
            {
                model.setLastName(lastNameBox.getText());

                if ((keyCode == KEY_ENTER) && (modifiers == 0))
                {
                    model.updateResults();
                }
            }
        });
    }

    /**
     * Wire up a listener to the Cancel button.
     * 
     * @param cancel
     *            the cancel link (button)
     * @param closeCommand
     *            the command to be invoked when the cancel button is clicked
     */
    public void registerCancelButton(final Hyperlink cancel, final WidgetCommand closeCommand)
    {
        cancel.addClickListener(new ClickListener()
        {
            public void onClick(final Widget arg0)
            {
                closeCommand.execute();
            }
        });
    }

    /**
     * Wire up a listener to the select button.
     * 
     * @param select
     *            the select link (button)
     * @param closeCommand
     *            a command object for closing the dialog
     * @param saveCommand
     *            a command object for saving the results of the search
     */
    public void registerSelectButton(final Hyperlink select, final WidgetCommand closeCommand, 
            final Command saveCommand)
    {
        select.addClickListener(new ClickListener()
        {
            public void onClick(final Widget arg0)
            {
                if (model.isSelectButtonActive())
                {
                    closeCommand.execute();
                    saveCommand.execute();
                }
            }
        });
    }
}
