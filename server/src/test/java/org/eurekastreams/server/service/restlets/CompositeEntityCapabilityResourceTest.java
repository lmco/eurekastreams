/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.CompositeEntity;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.CompositeEntityMapper;
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
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

/**
 * Test the CompositeizationCapabilityResource.
 * 
 */
public class CompositeEntityCapabilityResourceTest
{
    /**
     * Subject under test.
     */
    private CompositeEntityCapabilityResource sut;

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
    private CompositeEntityMapper entityMapper = context.mock(CompositeEntityMapper.class);

    /**
     * Mock composite entity.
     */
    private CompositeEntity orgMock = context.mock(CompositeEntity.class);

    /**
     * Mock BackgroundItem.
     */
    private BackgroundItem backgrounItemMock = context.mock(BackgroundItem.class);

    /**
     * Mock BackgroundItem.
     */
    private Person personMock = context.mock(Person.class);

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
        sut = new CompositeEntityCapabilityResource();
        sut.setEntityMapper(entityMapper);
    }

    /**
     * Test represent method with empty org name.
     * 
     * @throws ResourceException
     *             expected.
     */
    @Test(expected = ResourceException.class)
    public void testRepresentEmptyOrgName() throws ResourceException
    {
        final Variant variant = context.mock(Variant.class);
        setupCommonInitExpectations("  ");

        sut.init(restContext, request, response);
        sut.represent(variant);
        context.assertIsSatisfied();
    }

    /**
     * Test represent method with null org name.
     * 
     * @throws ResourceException
     *             expected.
     */
    @Test(expected = ResourceException.class)
    public void testRepresentNullOrgName() throws ResourceException
    {
        final Variant variant = context.mock(Variant.class);
        setupCommonInitExpectations(null);
        sut.init(restContext, request, response);
        sut.represent(variant);
        context.assertIsSatisfied();
    }

    /**
     * Test represent method with valid org name, but org not found.
     * 
     * @throws ResourceException
     *             expected.
     */
    @Test(expected = ResourceException.class)
    public void testRepresentOrgNotFound() throws ResourceException
    {
        final Variant variant = context.mock(Variant.class);
        setupCommonInitExpectations("validOrg");

        context.checking(new Expectations()
        {
            {
                oneOf(entityMapper).findByShortName(with(any(String.class)));
                will(returnValue(null));
            }
        });

        sut.init(restContext, request, response);
        sut.represent(variant);
        context.assertIsSatisfied();
    }

    /**
     * Test the represent method with valid org.
     * 
     * @throws ResourceException
     *             not expected.
     * @throws IOException
     *             not expected.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testRepresentValidOrg() throws ResourceException, IOException
    {
        final Variant variant = context.mock(Variant.class);
        final List<BackgroundItem> capabilities = new ArrayList(1);
        capabilities.add(backgrounItemMock);
        final Set<Person> coordinators = new HashSet<Person>();
        coordinators.add(personMock);

        setupCommonInitExpectations("validOrg");

        context.checking(new Expectations()
        {
            {
                oneOf(entityMapper).findByShortName(with(any(String.class)));
                will(returnValue(orgMock));

                oneOf(orgMock).getCapabilities();
                will(returnValue(capabilities));

                oneOf(backgrounItemMock).getName();
                will(returnValue("testCapability"));

                oneOf(orgMock).getCoordinators();
                will(returnValue(coordinators));

                oneOf(personMock).getOpenSocialId();
                will(returnValue("testAcctountId"));
            }
        });

        sut.init(restContext, request, response);
        Representation actual = sut.represent(variant);

        assertEquals("MediaType should be application/json", MediaType.APPLICATION_JSON, actual.getMediaType());

        JSONObject json = JSONObject.fromObject(actual.getText());

        JSONArray results = json.getJSONArray(CompositeEntityCapabilityResource.CAP_ARRAY_KEY);
        assertNotNull("JSON text isn't right", results);
        assertEquals("JSON array size isn't right", 1, results.size());

        results = json.getJSONArray(CompositeEntityCapabilityResource.COORD_ARRAY_KEY);
        assertNotNull("JSON text isn't right", results);
        assertEquals("JSON array size isn't right", 1, results.size());

        context.assertIsSatisfied();
    }

    /**
     * Test the storeRepresentation method.
     * 
     * @throws ResourceException
     *             not expected.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testStoreRepresentation() throws ResourceException
    {
        final List<BackgroundItem> capabilities = new ArrayList(1);
        capabilities.add(backgrounItemMock);
        final Set<Person> coordinators = new HashSet<Person>();
        coordinators.add(personMock);

        setupCommonInitExpectations("validOrg");
        context.checking(new Expectations()
        {
            {
                // These are for the convertOrgInfoToJSONObject method.
                oneOf(orgMock).getCapabilities();
                will(returnValue(capabilities));

                oneOf(backgrounItemMock).getName();
                will(returnValue("testCapability"));

                oneOf(orgMock).getCoordinators();
                will(returnValue(coordinators));

                oneOf(personMock).getOpenSocialId();
                will(returnValue("testAcctountId"));

                // start storeRepresentation method expectations.
                oneOf(entityMapper).findByShortName(with(any(String.class)));
                will(returnValue(orgMock));

                oneOf(orgMock).setCapabilities(with(any(ArrayList.class)));

                oneOf(entityMapper).flush();
            }
        });

        StringRepresentation jsonRep = new StringRepresentation(sut.convertEntityToJSONObject(orgMock).toString());

        sut.init(restContext, request, response);
        sut.storeRepresentation(jsonRep);

    }

    /**
     * This sets up the expectations if init() method is going to be called on sut. This is pulled out so it can be
     * resused independently of restlet type calls.
     * 
     * @param targetString
     *            The target string to search on.
     */
    @SuppressWarnings("static-access")
    private void setupCommonInitExpectations(final String targetString)
    {
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(sut.URL_SHORTNAME_KEY, targetString);

        context.checking(new Expectations()
        {
            {
                oneOf(request).getAttributes();
                will(returnValue(attributes));
            }
        });
    }

}
