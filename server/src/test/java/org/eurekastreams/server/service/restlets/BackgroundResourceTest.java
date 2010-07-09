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
package org.eurekastreams.server.service.restlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.restlet.Context;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.persistence.BackgroundMapper;

/**
 * Test class for BackgroundResource.
 */
public class BackgroundResourceTest
{
    /**
     * Subject under test.
     */
    private BackgroundResource sut;

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
     * Mocked mapper the SUT will use to look up background data.
     */
    private BackgroundMapper mapper = context.mock(BackgroundMapper.class);

    /**
     * The mocked context of the request.
     */
    private Context restContext = context.mock(Context.class);

    /**
     * The mocked web request.
     */
    private Request request = context.mock(Request.class);

    /**
     * The mocked response.
     */
    private Response response = context.mock(Response.class);

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new BackgroundResource();
        sut.setBackgroundMapper(mapper);
    }

    /**
     * Test background get/set (and init() for setup).
     * 
     * @throws ResourceException
     *             not expected
     */
    @Test
    public void testBackgroundTypeGetSet() throws ResourceException
    {
        setupCommonInitExpectations(BackgroundItemType.AFFILIATION, "abc");

        sut.init(restContext, request, response);
        assertEquals(BackgroundItemType.AFFILIATION, sut.getbackgroundType());
        
        sut.setBackgroundType(BackgroundItemType.SKILL);
        assertEquals(BackgroundItemType.SKILL, sut.getbackgroundType()); 
        
        context.assertIsSatisfied();
    }
    
    /**
     * Test the represent method.
     * @throws ResourceException Not expected.
     * @throws IOException Not expected.
     */
    @Test
    public void testRepresent() throws ResourceException, IOException
    {
        final Variant variant = context.mock(Variant.class);
        
        //this has default value of 10 for now, but resource
        //could set this in future.
        final int maxResults = 10;

        setupCommonInitExpectations(BackgroundItemType.SKILL, "targetString");
        setupCommonRepresentExpectations(BackgroundItemType.SKILL, "targetString");

        sut.init(restContext, request, response);
        Representation actual = sut.represent(variant);

        assertEquals("MediaType should be application/json", MediaType.APPLICATION_JSON, actual.getMediaType());

        JSONObject json = JSONObject.fromObject(actual.getText());

        JSONArray results = json.getJSONArray(BackgroundResource.ITEM_NAMES_KEY);
        assertNotNull("JSON text isn't right", results);
        assertEquals("JSON text isn't right", maxResults, results.size());

        context.assertIsSatisfied();
    }


    /**
     * Test the represent method: backgroundtype is null or not handled,
     * or target string is null or empty.
     * 
     * @throws ResourceException Not expected.
     * @throws IOException Not expected.
     */
    @Test
    public void testRepresentBadInput() throws ResourceException, IOException
    {

        final Variant variant = context.mock(Variant.class);
        
        setupCommonInitExpectations(BackgroundItemType.NOT_SET, "");
        setupCommonRepresentExpectations(BackgroundItemType.NOT_SET, "");

        sut.init(restContext, request, response);
        Representation actual = sut.represent(variant);
        
        assertEquals("MediaType should be application/json", MediaType.TEXT_PLAIN, actual.getMediaType());
        assertEquals("Response should be empty", actual.getText(), "");
    }
    
    /**
     * This sets up the expectations if represent method is going to be called on sut.
     * @param backgroundItemtype The background item type.
     * @param targetString The target string to search on.
     */
    private void setupCommonRepresentExpectations(final BackgroundItemType backgroundItemtype, 
                                                  final String targetString)
    {        
        //generate the list to be returned by mock mapper.
        String baseResult = "bgName_";
        
        //this has default value of 10 for now, but resource
        //could set this in future.
        final int maxResults = 10;        
        final ArrayList<String> results = new ArrayList<String>(10);
        for (int i = 0; i < maxResults; i++)
        {
            results.add(baseResult + i);
        }

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).findBackgroundItemNamesByType(
                        backgroundItemtype, targetString, maxResults);
                will(returnValue(results));
            }
        });

    }
    
    /**
     * This sets up the expectations if init() method is going to be called on sut.
     * This is pulled out so it can be resused independently of restlet type calls.
     * @param backgroundItemtype The background item type.
     * @param targetString The target string to search on.
     */
    private void setupCommonInitExpectations(final BackgroundItemType backgroundItemtype, final String targetString)
    {
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("type", (Object) backgroundItemtype.name());
        attributes.put("query", targetString);        

        context.checking(new Expectations()
        {
            {
                oneOf(request).getAttributes();
                will(returnValue(attributes));
            }
        });
    }
}
