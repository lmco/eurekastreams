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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.client.ui.WidgetCommand;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.AnonymousClassInterceptor;
import org.eurekastreams.web.client.ui.ModelChangeListener;
import org.eurekastreams.web.client.ui.common.PersonPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget;
import org.jmock.Expectations;
import org.jmock.Sequence;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Tests the employee lookup view.
 */
public class EmployeeLookupViewTest
{
    /**
     * System under test.
     */
    EmployeeLookupView sut;

    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mock model.
     */
    private EmployeeLookupModel modelMock;

    /**
     * Mock controller.
     */
    private EmployeeLookupController controllerMock;

    /**
     * Last name textbox mock. Used to search.
     */
    private TextBox lastNameMock;

    /**
     * Mock results.
     */
    private ListBox resultsMock;

    /**
     * Search button mock.
     */
    private Hyperlink searchButtonMock;

    /**
     * User full name label mock.
     */
    private Label userFullNameMock;

    /**
     * User image mock.
     */
    private AvatarWidget userImageMock;

    /**
     * Mock user title.
     */
    private Label userTitleMock;

    /**
     * Mock user org.
     */
    private Label userOrgMock;

    /**
     * Email mock.
     */
    private Label userEmailMock;

    /**
     * Results desc mock.
     */
    private Label resultsDescMock;

    /**
     * Cancel button mock.
     */
    private Hyperlink cancelButtonMock;

    /**
     * Select button mock.
     */
    private Hyperlink selectButtonMock;

    /**
     * Close command mock.
     */
    private WidgetCommand closeCommandMock;

    /**
     * Save command.
     */
    private Command saveCommandMock;

    /**
     * Person container.
     */
    private FlowPanel personContainerMock;

    /**
     * Mock widget.
     */
    private EmployeeLookupContent widgetMock = context.mock(EmployeeLookupContent.class);

    /**
     * Intercepter for results change listener.
     */
    private AnonymousClassInterceptor<ModelChangeListener> resultsChangeListenerInt = 
        new AnonymousClassInterceptor<ModelChangeListener>();

    /**
     * Intercepter for select change listener.
     */
    private AnonymousClassInterceptor<ModelChangeListener> selectChangeListenerInt = 
        new AnonymousClassInterceptor<ModelChangeListener>();

    /**
     * Intercepter for search button change listener.
     */
    private AnonymousClassInterceptor<ModelChangeListener> searchButtonChangeListenerInt = 
        new AnonymousClassInterceptor<ModelChangeListener>();

    /**
     * Intercepter for select button change listener.
     */
    private AnonymousClassInterceptor<ModelChangeListener> selectButtonChangeListenerInt = 
        new AnonymousClassInterceptor<ModelChangeListener>();

    /**
     * Set up test fixtures.
     */
    @Before
    public final void setUp()
    {
        GWTMockUtilities.disarm();

        controllerMock = context.mock(EmployeeLookupController.class);
        modelMock = context.mock(EmployeeLookupModel.class);

        lastNameMock = context.mock(TextBox.class);
        searchButtonMock = context.mock(Hyperlink.class);
        resultsMock = context.mock(ListBox.class);
        resultsDescMock = context.mock(Label.class, "results desc");
        cancelButtonMock = context.mock(Hyperlink.class, "cancel button");
        selectButtonMock = context.mock(Hyperlink.class, "select button");
        closeCommandMock = context.mock(WidgetCommand.class);
        saveCommandMock = context.mock(Command.class);
        personContainerMock = context.mock(FlowPanel.class, "personContainer");

        sut = new EmployeeLookupView(widgetMock, modelMock, controllerMock, saveCommandMock);
        sut.lastName = lastNameMock;
        sut.search = searchButtonMock;
        sut.results = resultsMock;
        sut.resultsDesc = resultsDescMock;
        sut.cancel = cancelButtonMock;
        sut.select = selectButtonMock;
        sut.personContainer = personContainerMock;

        context.checking(new Expectations()
        {
            {
                oneOf(modelMock).addChangeListener(
                        with(equal(EmployeeLookupModel.PropertyChangeEvent.RESULTS_CHANGED)),
                        with(any(ModelChangeListener.class)));
                will(resultsChangeListenerInt);

                oneOf(modelMock).addChangeListener(
                        with(equal(EmployeeLookupModel.PropertyChangeEvent.SELECTION_CHANGED)),
                        with(any(ModelChangeListener.class)));
                will(selectChangeListenerInt);

                oneOf(modelMock).addChangeListener(
                        with(equal(EmployeeLookupModel.PropertyChangeEvent.SEARCH_BUTTON_STATUS_CHANGED)),
                        with(any(ModelChangeListener.class)));
                will(searchButtonChangeListenerInt);

                oneOf(modelMock).addChangeListener(
                        with(equal(EmployeeLookupModel.PropertyChangeEvent.SELECT_BUTTON_STATUS_CHANGED)),
                        with(any(ModelChangeListener.class)));
                will(selectButtonChangeListenerInt);

                oneOf(controllerMock).registerResults(resultsMock);

                oneOf(controllerMock).registerSearchButton(searchButtonMock);

                oneOf(controllerMock).registerLastNameTextBox(lastNameMock);

                oneOf(controllerMock).registerCancelButton(cancelButtonMock, closeCommandMock);
                oneOf(controllerMock).registerSelectButton(selectButtonMock, closeCommandMock, saveCommandMock);

            }
        });

        sut.init();
        sut.setCloseCommand(closeCommandMock);
    }

    /**
     * Tear down fixtures.
     */
    @After
    public final void tearDown()
    {
        GWTMockUtilities.restore();
    }

    /**
     * Tests the last name property used for searching.
     */
    @Test
    public final void testSearchBoxProperty()
    {
        assertEquals(lastNameMock, sut.getSearchBox());
    }

    /**
     * Tests view init.
     */
    @Test
    public final void testInit()
    {
        context.assertIsSatisfied();
    }

    /**
     * Tests on results updated.
     */
    @Test
    public final void testOnResultsUpdated()
    {
        final Sequence execSequence = context.sequence("Execution sequence");

        final List<Person> results = new ArrayList<Person>();
        results.add(new Person("a", "b", "c", "d", "e"));
        results.add(new Person("a", "b", "c", "d", "e"));
        results.add(new Person("a", "b", "c", "d", "e"));

        context.checking(new Expectations()
        {
            {
                oneOf(resultsMock).clear();
                inSequence(execSequence);

                oneOf(modelMock).getPeopleResults();
                will(returnValue(results));
                inSequence(execSequence);

                exactly(3).of(resultsMock).addItem(with(any(String.class)), with(any(String.class)));
                inSequence(execSequence);

                oneOf(modelMock).getMoreResultsExist();
                will(returnValue(true));
                inSequence(execSequence);

                oneOf(modelMock).getResultLimit();
                will(returnValue(3));
                inSequence(execSequence);

                oneOf(resultsDescMock).setText(with(any(String.class)));
            }
        });

        resultsChangeListenerInt.getObject().onChange();

        context.assertIsSatisfied();
    }

    /**
     * Test get the search box.
     */
    @Test
    public final void testGetSearchBox()
    {
        assertEquals(lastNameMock, sut.getSearchBox());

        context.assertIsSatisfied();
    }

    /**
     * Tests on the selected person changed.
     */
    @Test
    public final void testOnSelectedChanged()
    {
        final Person personMock = context.mock(Person.class);
        final Organization orgMock = context.mock(Organization.class);

        final Sequence execSequence = context.sequence("Execution sequence");

        final PersonPanel panelMock = context.mock(PersonPanel.class);

        context.checking(new Expectations()
        {
            {
                oneOf(modelMock).getSelectedPerson();
                will(returnValue(personMock));

                oneOf(personMock).toPersonModelView();

                oneOf(widgetMock).getPerson(with(any(PersonModelView.class)));
                will(returnValue(panelMock));

                oneOf(personContainerMock).clear();

                oneOf(personContainerMock).add(panelMock);
            }
        });

        selectChangeListenerInt.getObject().onChange();

        context.assertIsSatisfied();
    }

    /**
     * Tests on results updated with no results.
     */
    @Test
    public final void testOnResultsUpdatedNoResults()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(resultsMock).clear();

                exactly(2).of(modelMock).getPeopleResults();
                will(returnValue(new ArrayList<Person>()));

                oneOf(modelMock).getMoreResultsExist();
                will(returnValue(false));

                oneOf(resultsDescMock).setText(with(any(String.class)));

                never(resultsMock).addItem(with(any(String.class)));
            }
        });

        sut.onPeopleResultsUpdated();

        context.assertIsSatisfied();
    }

    /**
     * Tests on results updated with one result.
     */
    @Test
    public final void testOnResultsUpdatedOneResult()
    {
        final List<Person> results = new ArrayList<Person>();
        results.add(new Person("a", "b", "c", "d", "e"));

        context.checking(new Expectations()
        {
            {
                final Sequence execSequence = context.sequence("Execution sequence");
                
                oneOf(resultsMock).clear();

                exactly(2).of(modelMock).getPeopleResults();
                will(returnValue(results));
                
                exactly(1).of(resultsMock).addItem(with(any(String.class)), with(any(String.class)));
                inSequence(execSequence);

                oneOf(modelMock).getMoreResultsExist();
                will(returnValue(false));

                oneOf(resultsDescMock).setText(with(any(String.class)));
                
                oneOf(resultsMock).setItemSelected(0, true);
                
                oneOf(resultsMock).getValue(0);
                will(returnValue("a"));
                
                oneOf(modelMock).setSelectedPersonByAccountId("a");
            }
        });

        sut.onPeopleResultsUpdated();

        context.assertIsSatisfied();
    }

    /**
     * Tests when select button active state changes.
     */
    @Test
    public final void testSelectActiveChangedActive()
    {

        context.checking(new Expectations()
        {
            {
                oneOf(modelMock).isSelectButtonActive();
                will(returnValue(true));

                oneOf(selectButtonMock).removeStyleName("lookup-select-button-inactive");
                oneOf(selectButtonMock).addStyleName("lookup-select-button-active");

            }
        });

        selectButtonChangeListenerInt.getObject().onChange();

        context.assertIsSatisfied();
    }

    /**
     * Tests when select button active state changes.
     */
    @Test
    public final void testSelectActiveChangedInactive()
    {

        context.checking(new Expectations()
        {
            {
                oneOf(modelMock).isSelectButtonActive();
                will(returnValue(false));

                oneOf(selectButtonMock).removeStyleName("lookup-select-button-active");
                oneOf(selectButtonMock).addStyleName("lookup-select-button-inactive");

            }
        });

        selectButtonChangeListenerInt.getObject().onChange();

        context.assertIsSatisfied();
    }

    /**
     * Tests when search button active state changes.
     */
    @Test
    public final void testSearchActiveChangedActive()
    {

        context.checking(new Expectations()
        {
            {
                oneOf(modelMock).getSearchButtonStatus();
                will(returnValue(true));

                oneOf(searchButtonMock).removeStyleName("lookup-search-button-inactive");
                oneOf(searchButtonMock).addStyleName("lookup-search-button-active");
            }
        });

        searchButtonChangeListenerInt.getObject().onChange();

        context.assertIsSatisfied();
    }

    /**
     * Tests when search button active state changes.
     */
    @Test
    public final void testSearchActiveChangedInactive()
    {

        context.checking(new Expectations()
        {
            {
                oneOf(modelMock).getSearchButtonStatus();
                will(returnValue(false));

                oneOf(searchButtonMock).removeStyleName("lookup-search-button-active");
                oneOf(searchButtonMock).addStyleName("lookup-search-button-inactive");
            }
        });

        searchButtonChangeListenerInt.getObject().onChange();

        context.assertIsSatisfied();
    }
}
