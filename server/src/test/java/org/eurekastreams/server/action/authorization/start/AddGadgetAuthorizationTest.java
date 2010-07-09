/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.authorization.start;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.request.start.AddGadgetRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Tab;
import org.eurekastreams.server.domain.TabGroup;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.TabMapper;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Add gadget auth test.
 *
 */
public class AddGadgetAuthorizationTest 
{
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
     * Sut.
     */
    private AddGadgetAuthorization sut;    
    
    /**
     * Mock.
     */
    private final TabMapper tabMapper = 
        context.mock(TabMapper.class);     

    /**
     * Mock.
     */
    private final TabPermission tabPermission = 
        context.mock(TabPermission.class);    
    
    /**
     * Mock.
     */
    private final PersonMapper personMapper = 
        context.mock(PersonMapper.class);    

    /**
     * Mock.
     */
    private final AddGadgetRequest addGadgetRequest = 
        context.mock(AddGadgetRequest.class);    

    /**
     * Mock.
     */
    private final Principal principal = 
        context.mock(Principal.class);       
    
    /**
     * Mock.
     */
    private final Person person = 
        context.mock(Person.class);        
    
    /**
     * Mock.
     */
    private final TabGroup tabGroup = 
        context.mock(TabGroup.class);        

    /**
     * Mock.
     */
    private final Tab tab = 
        context.mock(Tab.class);    
    
    /**
     * Mock.
     */
    private final PrincipalActionContext actionContext = 
        context.mock(PrincipalActionContext.class);     
    
    /**
     * Mock.
     */
    private final AuthorizationException authorizationException = 
        context.mock(AuthorizationException.class);     
    
    /**
     * Setup for each test.
     */
    @Before
    public void set()
    {
        sut = new AddGadgetAuthorization(tabMapper, tabPermission, personMapper);
    }    
    
    /**
     * Verify that tabPermission is called correctly in this method.
     */
    @Test
    public void testPerformSecurityCheckHappyPath()
    {
    	
        final Long tabId = new Long(1);

        context.checking(new Expectations()
        {
            {
            	allowing(actionContext).getParams();
            	will(returnValue(addGadgetRequest));
            	
            	allowing(addGadgetRequest).getTabId();
            	will(returnValue(tabId));
            	
            	oneOf(actionContext).getPrincipal();
            	will(returnValue(principal));
            	
            	oneOf(principal).getAccountId();
            	will(returnValue("testUser"));
            	
                oneOf(tabPermission).canModifyGadgets("testUser", tabId, true);
                will(returnValue(true));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }
    
    /**
     * Verify that tabPermission is called correctly in this method.
     */
    @Test(expected = AuthorizationException.class)    
    public void testPerformSecurityCheckWithNoPermission()
    {
        final Long tabId = new Long(1);

        context.checking(new Expectations()
        {
            {
            	allowing(actionContext).getParams();
            	will(returnValue(addGadgetRequest));
            	
            	allowing(addGadgetRequest).getTabId();
            	will(returnValue(tabId));
            	
            	oneOf(actionContext).getPrincipal();
            	will(returnValue(principal));
            	
            	oneOf(principal).getAccountId();
            	will(returnValue("testUser"));                        
            	
                oneOf(tabPermission).canModifyGadgets("testUser", tabId, true);
                will(returnValue(false));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }
    
    /**
     * Verify that tabPermission is called correctly in this method.
     */
    @Test(expected = AuthorizationException.class)    
    public void testPerformSecurityCheckWithTabNull()
    {
        final Long tabId = null;
        final List<Tab> tabs = new ArrayList<Tab>();
        tabs.add(tab);

        context.checking(new Expectations()
        {
            {
            	allowing(actionContext).getParams();
            	will(returnValue(addGadgetRequest));
            	
            	allowing(addGadgetRequest).getTabId();
            	will(returnValue(tabId));
            	
                oneOf(personMapper).findByAccountId("testUser");
                will(returnValue(person));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));
                
                allowing(principal).getAccountId();
                will(returnValue("testUser"));
                
                oneOf(person).getStartTabGroup();
                will(returnValue(tabGroup));
                
                oneOf(tabGroup).getTabs();
                will(returnValue(tabs));
                
                oneOf(tab).getId();
                will(returnValue(1L));
                
                oneOf(tabPermission).canModifyGadgets("testUser", 1L, true);
                will(returnValue(false));
            }
        });

        sut.authorize(actionContext);
        context.assertIsSatisfied();
    }    
    
}
