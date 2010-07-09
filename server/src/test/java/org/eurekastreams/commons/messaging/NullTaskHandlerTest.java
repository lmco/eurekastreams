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
package org.eurekastreams.commons.messaging;

import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.task.NullTaskHandler;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for NullProducer class.
 *
 */
public class NullTaskHandlerTest 
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
     * Mock objects.
     */
    
    /**
     * The User Action Request mock.
     */
    private UserActionRequest userActionRequestMock = context.mock(UserActionRequest.class);   
    
    /**
     * SUT.
     */
    private NullTaskHandler sut = null;
    
    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        sut = new NullTaskHandler();
    }
    
    /**
     * Test submit(UserActionRequest).
     * @throws Exception  not expected
     */
    @Test
    public void testSubmit() throws Exception
    {
        context.checking(new Expectations()
        {
            {
            	oneOf(userActionRequestMock).getActionKey();
            	
            	oneOf(userActionRequestMock).getParams().toString();            	
            }
        });
        
    	sut.handleTask(userActionRequestMock);        
        context.assertIsSatisfied();    	

    }    
    
}
