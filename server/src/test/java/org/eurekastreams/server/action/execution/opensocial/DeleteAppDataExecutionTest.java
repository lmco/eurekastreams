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
package org.eurekastreams.server.action.execution.opensocial;

import static junit.framework.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
//import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.opensocial.DeleteAppDataRequest;
import org.eurekastreams.server.domain.AppData;
import org.eurekastreams.server.persistence.AppDataMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.userdetails.UserDetails;

/**
 * Test suite for the {@link GetAppDataExecution} class.
 *
 */
public class DeleteAppDataExecutionTest
{
    /**
     * System under test.
     */
    private DeleteAppDataExecution sut;

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
     * Mocked instance of the principal object.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Mocked mapper for testing.
     */
    private AppDataMapper jpaAppDataMapper = context.mock(AppDataMapper.class);
    
    /**
     * Nulled user details object for test.
     */
    private UserDetails user = context.mock(UserDetails.class);
    
    /**
     * Mocked instance of AppData.
     */
    private AppData testAppData = context.mock(AppData.class);

    /**
     * Test Application Id.
     */
    final Long testApplicationId = 3487L;
    
    
    /**
     * Test App Data Id.
     */
    final Long testAppDataId = 1234L;
    /**
     * Test OpenSocial Id.
     */
    final String testOpenSocialId = "213-423142314-231421-342134";
    
    /**
     * Test App Data Key.
     */
    final Set<String> testAppDataThreeOfFourKeys = new HashSet<String>(Arrays.asList("key1", "key2", "key3"));
    
    
    /**
     * Test App Data Star.
     */
    final Set<String> testAppDataStar = new HashSet<String>(Arrays.asList("*"));
    
    /**
     * Test app data key value pairs.
     */
    Map<String, String> testAppDataValues;
    
    /**
     * Basic, success parameters to use during testing the action.
     */
    //final String[] testParams = {testApplicationId, testPersonId, "name1", "name2", "name3"};
    
    /**
     * Basic success parameters using a wildcard to delete all appdata entries.
     */
    //final String[] testParamsWithStar = {testApplicationId, testPersonId, "*"};
    
    /**
     * Setup the test.
     */
    @Before
    public final void setUp()
    {
        sut = new DeleteAppDataExecution(jpaAppDataMapper);
        
        testAppDataValues = new HashMap<String, String>();
        testAppDataValues.put("key1", "value1");
        testAppDataValues.put("key2", "value2");
        testAppDataValues.put("key3", "value3");
        testAppDataValues.put("key4", "value4");
    }
    
    /**
     * THis method tests the wildcard parameters for the perform action.
     * @throws Exception errors for the caller to handler.
     */
    @Test
    public final void testExeuctionWithWildCardKey() throws Exception
    {
        // Set up the call parameters
        DeleteAppDataRequest currentRequest = 
            new DeleteAppDataRequest(testApplicationId, testOpenSocialId, testAppDataStar);

        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);
        
        context.checking(new Expectations()
        {
            {
                oneOf(jpaAppDataMapper).findOrCreateByPersonAndGadgetDefinitionIds(testApplicationId.longValue(),
                        testOpenSocialId);
                will(returnValue(testAppData));
                
                oneOf(testAppData).getValues();
                will(returnValue(testAppDataValues));
                
                oneOf(testAppData).getId();
                will(returnValue(testAppDataId));
                oneOf(jpaAppDataMapper).deleteAppDataValueByKey(testAppDataId, "key1");
                
                oneOf(testAppData).getId();
                will(returnValue(testAppDataId));
                oneOf(jpaAppDataMapper).deleteAppDataValueByKey(testAppDataId, "key2");
                
                oneOf(testAppData).getId();
                will(returnValue(testAppDataId));
                oneOf(jpaAppDataMapper).deleteAppDataValueByKey(testAppDataId, "key3");
                
                oneOf(testAppData).getId();
                will(returnValue(testAppDataId));
                oneOf(jpaAppDataMapper).deleteAppDataValueByKey(testAppDataId, "key4");
                
                oneOf(jpaAppDataMapper).flush();
                
                oneOf(jpaAppDataMapper).findOrCreateByPersonAndGadgetDefinitionIds(testApplicationId.longValue(),
                        testOpenSocialId);
                will(returnValue(testAppData));
            }
        });
        
        // Make the call
        AppData actual = sut.execute(currentContext);

        assertEquals(testAppData, actual);

        context.assertIsSatisfied();
    }
    
    /**
     * THis method tests the wildcard parameters for the perform action.
     * @throws Exception errors for the caller to handler.
     */
    @Test
    public final void testExeuctionWithThreeOfFourKeys() throws Exception
    {
        // Set up the call parameters
        DeleteAppDataRequest currentRequest = 
            new DeleteAppDataRequest(testApplicationId, testOpenSocialId, testAppDataThreeOfFourKeys);

        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);
        
        context.checking(new Expectations()
        {
            {
                oneOf(jpaAppDataMapper).findOrCreateByPersonAndGadgetDefinitionIds(testApplicationId.longValue(),
                        testOpenSocialId);
                will(returnValue(testAppData));
                
                oneOf(testAppData).getValues();
                will(returnValue(testAppDataValues));
                
                oneOf(testAppData).getId();
                will(returnValue(testAppDataId));
                oneOf(jpaAppDataMapper).deleteAppDataValueByKey(testAppDataId, "key1");
                
                oneOf(testAppData).getId();
                will(returnValue(testAppDataId));
                oneOf(jpaAppDataMapper).deleteAppDataValueByKey(testAppDataId, "key2");
                
                oneOf(testAppData).getId();
                will(returnValue(testAppDataId));
                oneOf(jpaAppDataMapper).deleteAppDataValueByKey(testAppDataId, "key3");
                
                oneOf(jpaAppDataMapper).flush();
                
                oneOf(jpaAppDataMapper).findOrCreateByPersonAndGadgetDefinitionIds(testApplicationId.longValue(),
                        testOpenSocialId);
                will(returnValue(testAppData));
            }
        });
        
        // Make the call
        AppData actual = sut.execute(currentContext);

        assertEquals(testAppData, actual);

        context.assertIsSatisfied();
    }    

    /**
     * THis method tests the wildcard parameters for the perform action.
     */
    @Test(expected = ExecutionException.class)
    public final void testExeuctionWithException()
    {
        // Set up the call parameters
        DeleteAppDataRequest currentRequest = 
            new DeleteAppDataRequest(testApplicationId, testOpenSocialId, testAppDataThreeOfFourKeys);

        ServiceActionContext currentContext = new ServiceActionContext(currentRequest, principalMock);
        
        context.checking(new Expectations()
        {
            {
                oneOf(jpaAppDataMapper).findOrCreateByPersonAndGadgetDefinitionIds(testApplicationId.longValue(),
                        testOpenSocialId);
                will(throwException(new Exception()));
            }
        });
        
        // Make the call
        AppData actual = sut.execute(currentContext);

        assertEquals(testAppData, actual);

        context.assertIsSatisfied();
    }    
    
}
