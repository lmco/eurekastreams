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
package org.eurekastreams.web.client.ui.common.form.elements;

import java.util.Collection;
import java.util.HashSet;

import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.ui.common.EditPanel;
import org.eurekastreams.web.client.ui.common.EditPanel.Mode;
import org.eurekastreams.web.client.ui.common.PersonPanel;
import org.eurekastreams.web.client.ui.common.dialog.Dialog;
import org.eurekastreams.web.client.ui.common.dialog.lookup.EmployeeLookupContent;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * An element that encapsulates the lookup and display of a person record. This element is put on a form.
 */
public class PersonLookupFormElement extends FlowPanel implements FormElement
{
    /**
     * The lookup dialog.
     */
    private EmployeeLookupContent dialogContent;

    /**
     * The key to be used in the map when data is sent to the server.
     */
    private String key = "";

    /**
     * Panel where the looked-up people are displayed.
     */
    private final FlowPanel resultPanel = new FlowPanel();

    /**
     * Collection of looked-up people.
     */
    private final HashSet<PersonModelView> persons = new HashSet<PersonModelView>();

    /**
     * The label.
     */
    private final Label label = new Label();

    /**
     * Constructor.
     *
     * @param inTitle
     *            title for the dialog
     * @param inLookupText
     *            the lookup text
     * @param inInstructions
     *            instructions for the dialog
     * @param inKey
     *            the key to be used in the map when data is sent to the server
     * @param inPersons
     *            people initially in the element
     * @param inRequired
     *            whether this element represents required data
     */
    public PersonLookupFormElement(final String inTitle, final String inLookupText, final String inInstructions,
            final String inKey, final Collection<PersonModelView> inPersons, final boolean inRequired)
    {
        this.addStyleName(StaticResourceBundle.INSTANCE.coreCss().personLookupFormElement());
        // persons will get populated below using the addPersonMethod()
        // persons = new HashSet<Person>(inPersons);
        key = inKey;
        label.setText(inTitle);
        label.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formLabel());
        Label requiredLabel = new Label();
        Label instructions = new Label();

        Label lookup = new Label(inLookupText);
        lookup.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formButton());
        lookup.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formLookupButton());
        lookup.addClickListener(new ClickListener()
        {
            public void onClick(final Widget arg0)
            {
                dialogContent = new EmployeeLookupContent(getSaveCommand());
                Dialog.showCentered(dialogContent);
            }
        });

        if (inRequired)
        {
            requiredLabel.addStyleName(StaticResourceBundle.INSTANCE.coreCss().requiredFormLabel());
            requiredLabel.setText("(required)");
        }

        instructions.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formInstructions());
        instructions.setText(inInstructions);

        this.add(label);
        this.add(instructions);
        this.add(requiredLabel);
        this.add(resultPanel);
        this.add(lookup);

        for (PersonModelView person : inPersons)
        {
            addPerson(person);
        }
    }

    /**
     * Add a person looked up in the modal to the display.
     *
     * @param inPerson
     *            the newly looked-up person
     */
    public void addPerson(final PersonModelView inPerson)
    {
        if (!persons.contains(inPerson))
        {
            final FlowPanel personContainer = new FlowPanel();
            personContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().personContainer());

            EditPanel editPanel = new EditPanel(personContainer, Mode.DELETE);
            personContainer.add(editPanel);

            final PersonPanel personPanel = new PersonPanel(inPerson, false, false, false, true);

            personContainer.add(personPanel);
            persons.add(inPerson);
            resultPanel.add(personContainer);

            editPanel.addDeleteClickHandler(new ClickHandler()
            {
                public void onClick(final ClickEvent arg0)
                {
                    removePerson(inPerson, personContainer);
                }
            });
        }
    }

    /**
     * Remove a person from the display panel.
     *
     * @param personToBeRemoved
     *            the person object
     * @param panelToBeRemoved
     *            the display panel
     */
    private void removePerson(final PersonModelView personToBeRemoved, final FlowPanel panelToBeRemoved)
    {
        resultPanel.remove(panelToBeRemoved);
        persons.remove(personToBeRemoved);
    }

    /**
     * Get the save command object.
     *
     * @return the save command
     */
    private Command getSaveCommand()
    {
        return new Command()
        {
            public void execute()
            {
                PersonModelView result = dialogContent.getPerson();
                addPerson(result);
            }
        };
    }

    /**
     * Getter.
     *
     * @return the key
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Returns the collection of Persons who were selected.
     *
     * @return the collection
     */
    public HashSet<PersonModelView> getValue()
    {
        return persons;
    }

    /**
     * Gets called if this element has an error.
     *
     * @param errMessage
     *            the error Message.
     */
    public void onError(final String errMessage)
    {
        label.addStyleName(StaticResourceBundle.INSTANCE.coreCss().formError());
    }

    /**
     * Gets called if this element was successful.
     */
    public void onSuccess()
    {
        label.removeStyleName(StaticResourceBundle.INSTANCE.coreCss().formError());
    }

}
