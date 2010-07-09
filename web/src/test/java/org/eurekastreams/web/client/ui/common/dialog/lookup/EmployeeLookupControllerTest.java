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

import org.eurekastreams.web.client.AnonymousClassInterceptor;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.eurekastreams.commons.client.ui.WidgetCommand;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;

/**
 * Tests the employee lookup controller.
 */
public class EmployeeLookupControllerTest
{
    /**
     * System under test.
     */
    EmployeeLookupController sut;

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
    private EmployeeLookupModel modelMock = context.mock(EmployeeLookupModel.class);

    /**
     * Set up test fixtures.
     */
    @Before
    public final void setUp()
    {
        GWTMockUtilities.disarm();

        sut = new EmployeeLookupController(modelMock);
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
     * Tests registering the results list box.
     */
    @Test
    public final void testRegisterResults()
    {
        final ListBox resultsMock = context.mock(ListBox.class);
        final TextBox lastNameBox = context.mock(TextBox.class);

        final AnonymousClassInterceptor<ChangeListener> changeInt = new AnonymousClassInterceptor<ChangeListener>();

        context.checking(new Expectations()
        {
            {
                oneOf(resultsMock).addChangeListener(with(any(ChangeListener.class)));
                will(changeInt);

                oneOf(lastNameBox).addKeyboardListener(with(any(KeyboardListener.class)));

                oneOf(resultsMock).getSelectedIndex();
                will(returnValue(0));

                oneOf(resultsMock).getValue(0);
                will(returnValue("a"));

                oneOf(modelMock).setSelectedPersonByAccountId("a");
            }
        });

        sut.registerLastNameTextBox(lastNameBox);
        sut.registerResults(resultsMock);

        changeInt.getObject().onChange(null);

        context.assertIsSatisfied();
    }

    /**
     * Tests registering the search text box.
     */
    @Test
    public final void testSearch()
    {
        final Hyperlink searchButton = context.mock(Hyperlink.class);
        final TextBox lastNameBox = context.mock(TextBox.class);

        final AnonymousClassInterceptor<ClickListener> clickInt = new AnonymousClassInterceptor<ClickListener>();

        context.checking(new Expectations()
        {
            {
                oneOf(searchButton).addClickListener(with(any(ClickListener.class)));
                will(clickInt);

                oneOf(lastNameBox).getText();
                will(returnValue("something"));

                oneOf(modelMock).setLastName("something");

                oneOf(modelMock).updateResults();

                oneOf(lastNameBox).addKeyboardListener(with(any(KeyboardListener.class)));
            }
        });

        sut.registerLastNameTextBox(lastNameBox);
        sut.registerSearchButton(searchButton);

        clickInt.getObject().onClick(null);

        context.assertIsSatisfied();
    }

    /**
     * Tests registering the last name text box.
     */
    @Test
    public final void testLastName()
    {
        final TextBox lastNameBox = context.mock(TextBox.class);

        final AnonymousClassInterceptor<KeyboardListener> keyInt = new AnonymousClassInterceptor<KeyboardListener>();

        context.checking(new Expectations()
        {
            {
                oneOf(lastNameBox).addKeyboardListener(with(any(KeyboardListener.class)));
                will(keyInt);

                oneOf(lastNameBox).getText();
                will(returnValue("something"));

                oneOf(modelMock).setLastName("something");

                oneOf(modelMock).updateResults();

            }
        });

        sut.registerLastNameTextBox(lastNameBox);

        keyInt.getObject().onKeyUp(null, (char) KeyboardListenerAdapter.KEY_ENTER, 0);

        context.assertIsSatisfied();
    }

    /**
     * Tests registering the last name text box.
     */
    @Test
    public final void testLastNameModifier()
    {
        final TextBox lastNameBox = context.mock(TextBox.class);

        final AnonymousClassInterceptor<KeyboardListener> keyInt = new AnonymousClassInterceptor<KeyboardListener>();

        context.checking(new Expectations()
        {
            {
                oneOf(lastNameBox).addKeyboardListener(with(any(KeyboardListener.class)));
                will(keyInt);

                oneOf(lastNameBox).getText();
                will(returnValue("something"));

                oneOf(modelMock).setLastName("something");

                never(modelMock).updateResults();

            }
        });

        sut.registerLastNameTextBox(lastNameBox);

        keyInt.getObject().onKeyUp(null, (char) KeyboardListenerAdapter.KEY_ENTER, 1);

        context.assertIsSatisfied();
    }

    /**
     * Tests registering the last name text box.
     */
    @Test
    public final void testLastNameNotEnter()
    {
        final TextBox lastNameBox = context.mock(TextBox.class);

        final AnonymousClassInterceptor<KeyboardListener> keyInt = new AnonymousClassInterceptor<KeyboardListener>();

        context.checking(new Expectations()
        {
            {
                oneOf(lastNameBox).addKeyboardListener(with(any(KeyboardListener.class)));
                will(keyInt);

                oneOf(lastNameBox).getText();
                will(returnValue("something"));

                oneOf(modelMock).setLastName("something");

                never(modelMock).updateResults();

            }
        });

        sut.registerLastNameTextBox(lastNameBox);

        keyInt.getObject().onKeyUp(null, (char) KeyboardListenerAdapter.KEY_CTRL, 0);

        context.assertIsSatisfied();
    }

    /**
     * Tests registering the cancel button.
     */
    @Test
    public final void testRegisterCancelButton()
    {
        final Hyperlink cancelMock = context.mock(Hyperlink.class, "cance button");
        final WidgetCommand closeCommand = context.mock(WidgetCommand.class);

        final AnonymousClassInterceptor<ClickListener> clickInt = new AnonymousClassInterceptor<ClickListener>();

        context.checking(new Expectations()
        {
            {
                oneOf(cancelMock).addClickListener(with(any(ClickListener.class)));
                will(clickInt);

                oneOf(closeCommand).execute();

            }
        });

        sut.registerCancelButton(cancelMock, closeCommand);

        clickInt.getObject().onClick(null);

        context.assertIsSatisfied();
    }
}
