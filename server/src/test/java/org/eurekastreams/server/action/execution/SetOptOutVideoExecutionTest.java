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
package org.eurekastreams.server.action.execution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is responsible for testing SetOptoutVideoExecution class.
 *
 */
public class SetOptOutVideoExecutionTest extends MapperTest
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
     * System under test.
     */
    private SetOptOutVideoExecution sut;
    
    /**
     * The mapper.
     */
    @Autowired
    private PersonMapper personMapper;    
    
    
    /**
     * Mocked principal object for test.
     */
    private Principal principalMock = context.mock(Principal.class);
    
   /**
    * Test setup.
    */
   @Before
   public void setup()
   {

       sut = new SetOptOutVideoExecution(personMapper); 
   }
   
   /**
    * Test validating Execute.
    */
   @Test
   public void testExecute()
   {
       
       final Long testId = 1L;
       
       final ServiceActionContext currentContext = new ServiceActionContext(testId, principalMock);
       
       context.checking(new Expectations()
       {
           {
               oneOf(principalMock).getAccountId();
               will(returnValue("fordp"));
               
               
               
           }
       });
       
       Long testReturn = (Long) sut.execute(currentContext);
       context.assertIsSatisfied();
       assertEquals(testId, testReturn);
              
       Person personTest = personMapper.findByAccountId("fordp");
       
       assertTrue(personTest.getOptOutVideos().contains(testId));
       
   }
}
