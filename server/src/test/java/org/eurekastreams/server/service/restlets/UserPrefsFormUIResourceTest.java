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
package org.eurekastreams.server.service.restlets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityNotFoundException;

import org.eurekastreams.server.domain.Gadget;
import org.eurekastreams.server.domain.GadgetDefinition;
import org.eurekastreams.server.domain.GeneralGadgetDefinition;
import org.eurekastreams.server.domain.gadgetspec.GadgetMetaDataDTO;
import org.eurekastreams.server.persistence.GadgetMapper;
import org.eurekastreams.server.service.opensocial.gadgets.spec.GadgetMetaDataFetcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.Request;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;

/**
 * This class provides the tests for the User Preferences
 * RESTlet.
 *
 */
public class UserPrefsFormUIResourceTest
{
    
    /**
     * Key for retrieving the Gadget Id from the rest url.
     */
    private static final String MODULE_ID_KEY = "moduleid";
    
    /**
     * Test module id to test against.
     */
    private static final Long MODULE_ID = 27L;
    
    /**
     * Key for retrieving the Gadget Definition Url from the rest url.
     */
    private static final String GADGET_DEF_URL_KEY = "url";
    
    /**
     * Test gadget def url to use for testing.
     */
    private static final String GADGET_DEF_URL = "http://www.example.com/gadget.xml";
    
    /**
     * Key for retrieving the saved user preferences.
     */
    private static final String SAVED_USER_PREFS_KEY = "saveduserprefs";
    
    /**
     * Test user prefs keys.
     */
    private static final String SAVED_USER_PREFS = "userpref1=1&userpref2=2&userpref3=3";
    
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
     * Mocked gadget mapper for testing.
     */
    private GadgetMapper gadgetMapper = context.mock(GadgetMapper.class);
    
    /**
     * Mocked GadgetMetaDataFetcher for testing.
     */
    private GadgetMetaDataFetcher gadgetMetaFetcher = context.mock(GadgetMetaDataFetcher.class);
    
    /**
     * UserPrefsResource System under Test.
     */
    private UserPrefsFormUIResource sut;
    
    /**
     * Mocked gadget for testing.
     */
    private final Gadget testGadgetMock = context.mock(Gadget.class);
    
    /**
     * Mocked gadget definition for testing.
     */
    private final GadgetDefinition testGadgetDefinitionMock = context.mock(GadgetDefinition.class);
    
    /**
     * Mocked GadgetMetaData for testing.
     */
    private final GadgetMetaDataDTO testGadgetMetaDataMock = context.mock(GadgetMetaDataDTO.class);
    
    /**
     * Set up the SUT. 
     */
    @Before
    public void setup()
    {
        final Request request = context.mock(Request.class);
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(MODULE_ID_KEY, MODULE_ID.toString());
        attributes.put(GADGET_DEF_URL_KEY, GADGET_DEF_URL);
        attributes.put(SAVED_USER_PREFS_KEY, SAVED_USER_PREFS);
        
        context.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(attributes));
            }
        });

        sut = new UserPrefsFormUIResource();
        sut.setGadgetMapper(gadgetMapper);
        sut.setGadgetMetaDataFetcher(gadgetMetaFetcher);
        sut.initParams(request);
        
        context.assertIsSatisfied();
    }
    
    /**
     * Provides the happy sequence for getting the gadget metadata.
     * @throws Exception on error.
     */
    @Test
    public void testRepresentation() throws Exception
    {
        Variant variant = context.mock(Variant.class);
        final Map<String, GeneralGadgetDefinition> gadgetDefs = new HashMap<String, GeneralGadgetDefinition>();
        gadgetDefs.put(GADGET_DEF_URL, testGadgetDefinitionMock);
        final List<GadgetMetaDataDTO> gadgetMetaList = new ArrayList<GadgetMetaDataDTO>();
        gadgetMetaList.add(testGadgetMetaDataMock);
        
        context.checking(new Expectations()
        {
            {
                oneOf(gadgetMapper).findById(MODULE_ID);
                will(returnValue(testGadgetMock));
                
                oneOf(testGadgetMock).getGadgetDefinition();
                will(returnValue(testGadgetDefinitionMock));
                
                oneOf(gadgetMetaFetcher).getGadgetsMetaData(gadgetDefs);
                will(returnValue(gadgetMetaList));
                
                oneOf(testGadgetDefinitionMock).getId();
                will(returnValue(any(Long.class)));
                
            }
        });
        
        sut.represent(variant);
        
        context.assertIsSatisfied();
    }
    
    /**
     * This test simulates an exception being thrown from the mapper.
     * @throws ResourceException - thrown when error occurs
     */
    @Test
    public void testRepresentationGadgetDefNotFound() throws ResourceException
    {
        Variant variant = context.mock(Variant.class);
        
        context.checking(new Expectations()
        {
            {
                oneOf(gadgetMapper).findById(MODULE_ID);
                will(throwException(new EntityNotFoundException()));                
            }
        });
        
        sut.represent(variant);
        
        context.assertIsSatisfied();
    }
}
