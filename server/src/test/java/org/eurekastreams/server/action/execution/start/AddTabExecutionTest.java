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
package org.eurekastreams.server.action.execution.start;

import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabGroup;
import org.eurekastreams.server.domain.TabGroupType;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.TabMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.userdetails.UserDetails;

/**
 * Tests the AddTabAction.
 */
public class AddTabExecutionTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * The mock user information/parameters from the session.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Mock.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * Mocked person who will get the new tab.
     */
    private Person person = context.mock(Person.class);

    /**
     * Mocked Tab that's returned by the tabMapper.findById(long) after the insert.
     */
    private Tab tab = context.mock(Tab.class);

    /**
     * The mock mapper to be used by the action.
     */
    private PersonMapper personMapper = context.mock(PersonMapper.class);

    /**
     * The mock tabMapper to be used by the action.
     */
    private TabMapper tabMapper = context.mock(TabMapper.class);

    /**
     * Mocked TabGroup that the tab will be added to.
     */
    private TabGroup page = context.mock(TabGroup.class);

    /**
     * Subject under test.
     */
    private AddTabExecution sut = null;

    /**
     * The mock user information from the session.
     */
    private UserDetails userDetails = context.mock(UserDetails.class);

    /**
     * 
     */
    @Before
    public final void setup()
    {
        sut = new AddTabExecution(personMapper, tabMapper);
    }

    /**
     * Call the execute method and make sure it produces what it should.
     */
    @Test
    public final void testExecuteValidUser()
    {
        final List<TabGroup> tabGroups = new ArrayList<TabGroup>();
        tabGroups.add(page);

        // Set up expectations
        context.checking(new Expectations()
        {
            {
                oneOf(actionContext).getParams();
                will(returnValue("tabName"));

                oneOf(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getAccountId();
                will(returnValue("userName"));

                oneOf(personMapper).findByAccountId("userName");
                will(returnValue(person));

                oneOf(person).addTab(with(any(Tab.class)), with(TabGroupType.START));

                one(personMapper).clear();

                oneOf(personMapper).flush();

                oneOf(person).getDisplayName();
                will(returnValue("userName"));

                one(tabMapper).findById(with(any(long.class)));
                will(returnValue(tab));
            }
        });

        Tab actual = null;
        try
        {
            actual = (Tab) sut.execute(actionContext);
        }
        catch (Exception e)
        {
            fail("Caught an exception");
        }

        assertSame(tab, actual);

        context.assertIsSatisfied();
    }
}
