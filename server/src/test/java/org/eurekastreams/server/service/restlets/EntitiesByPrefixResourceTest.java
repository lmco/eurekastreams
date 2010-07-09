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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.SearchPeopleAndGroupsByPrefix;
import org.eurekastreams.server.persistence.mappers.requests.GetEntitiesByPrefixRequest;
import org.eurekastreams.server.search.modelview.DisplayEntityModelView;
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
import org.springframework.security.Authentication;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;

/**
 * Test for EntitiesByPrefixResource restlet.
 * 
 */
public class EntitiesByPrefixResourceTest
{
    /**
     * Subject under test.
     */
    private EntitiesByPrefixResource sut;

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
     * Mocked restlet context.
     */
    private Context restContext = context.mock(Context.class);

    /**
     * Mocked request.
     */
    private Request request = context.mock(Request.class);

    /**
     * Mocked response.
     */
    private Response response = context.mock(Response.class);

    /**
     * Mocked security context.
     */
    private SecurityContext securityContext = context.mock(SecurityContext.class);

    /**
     * Mocked authentication.
     */
    private Authentication auth = context.mock(Authentication.class);

    /**
     * Followed entity DAO.
     */
    private SearchPeopleAndGroupsByPrefix entitiesDAO = context.mock(SearchPeopleAndGroupsByPrefix.class);

    /**
     * Set up the SUT.
     * 
     */
    @Before
    public void setup()
    {
        sut = new EntitiesByPrefixResource();
        sut.setEntitiesDAO(entitiesDAO);
    }

    /**
     * Test the represent method.
     * 
     * @throws ResourceException
     *             on error.
     * @throws IOException
     *             on error.
     */
    @Test
    public void testRepresent() throws ResourceException, IOException
    {
        final Variant variant = context.mock(Variant.class);
        setupCommonInitExpectations("abc");
        final DisplayEntityModelView femv = new DisplayEntityModelView();
        femv.setDisplayName("NameGoesHere");
        femv.setType(EntityType.PERSON);
        femv.setUniqueKey("KeyGoesHere");
        femv.setStreamScopeId(1L);
        final ArrayList<DisplayEntityModelView> results = new ArrayList<DisplayEntityModelView>(1);
        results.add(femv);

        SecurityContext originalContext = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(securityContext);

        context.checking(new Expectations()
        {
            {
                oneOf(securityContext).getAuthentication();
                will(returnValue(auth));

                oneOf(auth).getName();
                will(returnValue("name"));

                oneOf(entitiesDAO).execute(with(any(GetEntitiesByPrefixRequest.class)));
                will(returnValue(results));
            }
        });

        sut.init(restContext, request, response);
        Representation actual = sut.represent(variant);

        assertEquals("MediaType should be application/json", MediaType.APPLICATION_JSON, actual.getMediaType());

        JSONObject json = JSONObject.fromObject(actual.getText());

        JSONArray jsonResults = json.getJSONArray(EntitiesByPrefixResource.ENTITIES_KEY);
        assertNotNull("JSON text isn't right", jsonResults);
        assertEquals(1, results.size());

        SecurityContextHolder.setContext(originalContext);

        context.assertIsSatisfied();
    }

    /**
     * Test the represent method failure handling.
     * 
     * @throws ResourceException
     *             on error.
     * @throws IOException
     *             on error.
     */
    @Test(expected = ResourceException.class)
    public void testRepresentFailure() throws ResourceException, IOException
    {
        final Variant variant = context.mock(Variant.class);
        setupCommonInitExpectations("abc");

        SecurityContext originalContext = SecurityContextHolder.getContext();
        SecurityContextHolder.setContext(securityContext);

        context.checking(new Expectations()
        {
            {
                oneOf(securityContext).getAuthentication();
                will(returnValue(auth));

                oneOf(auth).getName();
                will(returnValue("name"));

                oneOf(entitiesDAO).execute(with(any(GetEntitiesByPrefixRequest.class)));
                will(throwException(new Exception()));
            }
        });

        sut.init(restContext, request, response);
        sut.represent(variant);

        context.assertIsSatisfied();
        SecurityContextHolder.setContext(originalContext);
    }

    /**
     * This sets up the expectations if init() method is going to be called on sut. This is pulled out so it can be
     * resused independently of restlet type calls.
     * 
     * @param targetString
     *            The target string to search on.
     */
    private void setupCommonInitExpectations(final String targetString)
    {
        final Map<String, Object> attributes = new HashMap<String, Object>();
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
