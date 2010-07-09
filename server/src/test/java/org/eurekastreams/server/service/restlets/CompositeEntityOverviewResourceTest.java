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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.eurekastreams.server.domain.CompositeEntity;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.CompositeEntityMapper;

/**
 * Test class for CompositeEntityOverviewResource.
 */
public class CompositeEntityOverviewResourceTest
{
    /**
     * Subject under test.
     */
    private CompositeEntityOverviewResource sut;

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
     * Mocked mapper.
     */
    private CompositeEntityMapper entityMapper = context.mock(CompositeEntityMapper.class);

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("shortName", (Object) "entity overview");

        context.checking(new Expectations()
        {
            {
                oneOf(request).getAttributes();
                will(returnValue(attributes));
            }
        });

        sut = new CompositeEntityOverviewResource();
        sut.init(restContext, request, response);
        sut.setEntityMapper(entityMapper);
    }

    /**
     * Test the GET call.
     * 
     * @throws ResourceException
     *             not expected
     * @throws IOException
     *             not expected
     */
    @Test
    public void represent() throws ResourceException, IOException
    {
        final Variant variant = context.mock(Variant.class);

        final CompositeEntity cmpEntity = context.mock(CompositeEntity.class);

        final String overview = "Entity overview";

        final Set<Person> coordinators = new HashSet<Person>();

        final Person person1 = context.mock(Person.class, "person1");
        final Person person2 = context.mock(Person.class, "person2");

        coordinators.add(person1);
        coordinators.add(person2);

        context.checking(new Expectations()
        {
            {
                oneOf(entityMapper).findByShortName(with(any(String.class)));
                will(returnValue(cmpEntity));

                oneOf(cmpEntity).getOverview();
                will(returnValue(overview));

                oneOf(cmpEntity).getCoordinators();
                will(returnValue(coordinators));

                oneOf(person1).getOpenSocialId();
                will(returnValue("person1"));

                oneOf(person2).getOpenSocialId();
                will(returnValue("person2"));
            }
        });

        Representation actual = sut.represent(variant);

        assertEquals("MediaType doesn't match", MediaType.APPLICATION_JSON, actual.getMediaType());

        JSONObject json = JSONObject.fromObject(actual.getText());

        assertEquals("JSON text isn't right", overview, json.get(CompositeEntityOverviewResource.OVERVIEW_KEY));

        assertEquals("Got wrong number of coordinators", coordinators.size(), json
                .getJSONArray(CompositeEntityOverviewResource.COORDINATORS_KEY).size());

        // Check the first coordinator returned
        String accountId = json.getJSONArray(CompositeEntityOverviewResource.COORDINATORS_KEY).getString(0);
        assertTrue("Got a wrong coordinator", accountId.equals("person1") || accountId.equals("person2"));

        // Check the first coordinator returned
        accountId = json.getJSONArray(CompositeEntityOverviewResource.COORDINATORS_KEY).getString(1);
        assertTrue("Got a wrong coordinator", accountId.equals("person1") || accountId.equals("person2"));

        context.assertIsSatisfied();
    }

    /**
     * Test the POST functionality.
     * 
     * @throws ResourceException
     *             not expected
     * @throws IOException
     *             not expected
     */
    @Test
    public void storeRepresentation() throws ResourceException, IOException
    {
        final CompositeEntity cmpEntity = context.mock(CompositeEntity.class);

        final Representation entity = context.mock(Representation.class);

        final String overview = "This is the overview.";

        context.checking(new Expectations()
        {
            {
                oneOf(entityMapper).findByShortName(with(any(String.class)));
                will(returnValue(cmpEntity));

                oneOf(entity).getText();
                will(returnValue("{\"" + CompositeEntityOverviewResource.OVERVIEW_KEY + "\":\"" + overview + "\"}"));

                oneOf(cmpEntity).setOverview(overview);

                oneOf(entityMapper).flush();
            }
        });

        sut.storeRepresentation(entity);

        context.assertIsSatisfied();
    }

    /**
     * Send in a bad representation to trigger an error case.
     * 
     * @throws IOException
     *             should be handled by the storeRepresentation method.
     * @throws ResourceException
     *             should be thrown because of the IOException
     */
    @Test
    public void storeRepresentationWithBadData() throws IOException, ResourceException
    {
        final Representation rep = context.mock(Representation.class);

        final CompositeEntity cmpEntity = context.mock(CompositeEntity.class);

        context.checking(new Expectations()
        {
            {
                oneOf(entityMapper).findByShortName(with(any(String.class)));
                will(returnValue(cmpEntity));

                oneOf(rep).getText();
                will(throwException(new IOException()));
            }
        });

        try
        {
            sut.storeRepresentation(rep);
            fail("Should have thrown ResourceException");
        }
        catch (ResourceException resExc)
        {
            assertEquals(Status.CLIENT_ERROR_BAD_REQUEST, resExc.getStatus());
        }
    }
}
