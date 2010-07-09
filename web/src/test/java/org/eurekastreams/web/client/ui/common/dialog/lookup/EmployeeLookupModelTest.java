/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.web.client.AnonymousClassInterceptor;
import org.eurekastreams.web.client.ui.ModelChangeListener;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.eurekastreams.commons.client.ActionProcessor;
import org.eurekastreams.commons.client.ActionRequest;
import org.eurekastreams.server.domain.Person;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Tests the employee lookup model.
 */
public class EmployeeLookupModelTest
{
    /**
     * System under test.
     */
    private EmployeeLookupModel sut;

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
     * Results change listener mock.
     */
    private ModelChangeListener resultsListener = context.mock(ModelChangeListener.class, "results");

    /**
     * Last name change listener mock.
     */
    private ModelChangeListener lastNameListner = context.mock(ModelChangeListener.class, "last name");

    /**
     * Search status change listener mock.
     */
    private ModelChangeListener searchStatusListener = context.mock(ModelChangeListener.class, "search status");

    /**
     * Select status change listener mock.
     */
    private ModelChangeListener selectStatusListener = context.mock(ModelChangeListener.class, "select status");

    /**
     * Mock action processor.
     */
    private ActionProcessor actionProcessorMock = context.mock(ActionProcessor.class);

    /**
     * Set up test fixtures.
     */
    @Before
    public final void setUp()
    {
        sut = new EmployeeLookupModel(actionProcessorMock);

        sut.addChangeListener(EmployeeLookupModel.PropertyChangeEvent.LAST_NAME_CHANGED, lastNameListner);
        sut.addChangeListener(EmployeeLookupModel.PropertyChangeEvent.RESULTS_CHANGED, resultsListener);
        sut.addChangeListener(EmployeeLookupModel.PropertyChangeEvent.SEARCH_BUTTON_STATUS_CHANGED,
                searchStatusListener);
        sut.addChangeListener(EmployeeLookupModel.PropertyChangeEvent.SELECT_BUTTON_STATUS_CHANGED,
                selectStatusListener);
    }

    /**
     * Tests the results property.
     */
    @Test
    public final void testResultsProperty()
    {
        final AnonymousClassInterceptor<AsyncCallback<List<Person>>> cbInt = 
            new AnonymousClassInterceptor<AsyncCallback<List<Person>>>();
        
        context.checking(new Expectations()
        {
            {
                oneOf(resultsListener).onChange();
                oneOf(actionProcessorMock).makeRequest(with(any(ActionRequest.class)), with(any(AsyncCallback.class)));
                will(cbInt);
            }
        });
        
        sut.searchButtonActive = true;

        sut.updateResults();

        cbInt.getObject().onSuccess(new ArrayList<Person>());

        context.assertIsSatisfied();
        
        assertNotNull(sut.getPeopleResults());        
    }

    /**
     * Tests the results property on fail.
     */
    @Test
    public final void testResultsPropertyFailure()
    {
        final AnonymousClassInterceptor<AsyncCallback<List<Person>>> cbInt = 
            new AnonymousClassInterceptor<AsyncCallback<List<Person>>>();

        context.checking(new Expectations()
        {
            {
                never(resultsListener).onChange();
                oneOf(actionProcessorMock).makeRequest(with(any(ActionRequest.class)), with(any(AsyncCallback.class)));
                will(cbInt);
            }
        });

        sut.searchButtonActive = true;
        sut.updateResults();

        cbInt.getObject().onFailure(null);

        context.assertIsSatisfied();
    }

    /**
     * Test that when someone clicks on the disabled search button, nothing happens. 
     */
    @Test
    public final void updateResultsWithInactiveSearch()
    {
        context.checking(new Expectations()
        {
            {
                never(resultsListener).onChange();
            }
        });

        sut.searchButtonActive = false;
        sut.updateResults();
        
        context.assertIsSatisfied();
    }

    /**
     * Tests the last name property.
     */
    @Test
    public final void testLastNameProperty()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(lastNameListner).onChange();
                oneOf(searchStatusListener).onChange();
            }
        });

        sut.setLastName("test");

        assertEquals("test", sut.getLastName());

        context.assertIsSatisfied();
    }

    /**
     * Test searchButtonActive property.
     */
    @Test
    public final void testSearchButtonActiveProperty()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(searchStatusListener).onChange();
            }
        });

        sut.changeSearchButtonStatus(true);

        assertEquals(true, sut.getSearchButtonStatus());

        context.assertIsSatisfied();
    }

    /**
     * Test selectButtonActive property.
     */
    @Test
    public final void testSelectButtonActiveProperty()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(selectStatusListener).onChange();
            }
        });

        sut.changeSelectButtonStatus(true);

        assertEquals(true, sut.isSelectButtonActive());

        context.assertIsSatisfied();
    }
    

    /**
     * Test selectPerson property.
     */
    @Test
    public final void testSelectPersonProperty()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(selectStatusListener).onChange();
            }
        });

        sut.setSelectedPersonByAccountId("someId");

        context.assertIsSatisfied();
    }
    
    /**
     * Test setSelectedPersonByAccountId() with multiple people in the results list. 
     */
    @Test
    public final void selectPersonWithMultiplePeople()
    {
        final AnonymousClassInterceptor<AsyncCallback<List<Person>>> cbInt = 
            new AnonymousClassInterceptor<AsyncCallback<List<Person>>>();
        
        ArrayList<Person> people = new ArrayList<Person>();
        final Person p1 = context.mock(Person.class, "person1");
        final Person p2 = context.mock(Person.class, "person2");
        people.add(p1);
        people.add(p2);
        
        context.checking(new Expectations()
        {
            {
                oneOf(resultsListener).onChange();
                oneOf(actionProcessorMock).makeRequest(with(any(ActionRequest.class)), with(any(AsyncCallback.class)));
                will(cbInt);

                oneOf(selectStatusListener).onChange();
                
                allowing(p1).getAccountId();
                will(returnValue("person1"));

                allowing(p2).getAccountId();
                will(returnValue("person2"));
            }
        });
        
        sut.searchButtonActive = true;

        sut.updateResults();

        cbInt.getObject().onSuccess(people);

        sut.setSelectedPersonByAccountId("person2");

        context.assertIsSatisfied();
    }

    /**
     * Test the getMoreResultsExist() method. 
     */
    @Test
    public final void getMoreResultsExist()
    {
        assertFalse(sut.getMoreResultsExist());
    }
}
