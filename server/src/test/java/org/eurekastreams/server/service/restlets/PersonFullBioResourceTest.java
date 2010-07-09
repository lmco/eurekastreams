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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONObject;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;

/**
 * Test class for PersonFullBioResource.
 */
public class PersonFullBioResourceTest
{
    /**
     * Subject under test.
     */
    private PersonFullBioResource sut;

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
     * Mocked PersonMapper.
     */
    private PersonMapper personMapper = context.mock(PersonMapper.class);

    /**
     * An account id for testing. Arbitrary.
     */
    private static final String SAMPLE_UUID = UUID.randomUUID().toString();

    /**
     * A mocked person whose bio is being used.
     */
    private Person person = context.mock(Person.class);

    /**
     * Mocked request object.
     */
    private Request request = context.mock(Request.class);

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        Context requestContext = context.mock(Context.class);
        Response response = context.mock(Response.class);

        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("uuid", SAMPLE_UUID);

        context.checking(new Expectations()
        {
            {
                oneOf(request).getAttributes();
                will(returnValue(attributes));
            }
        });

        sut = new PersonFullBioResource();
        sut.init(requestContext, request, response);
        sut.setPersonMapper(personMapper);
    }

    /**
     * Test GET response.
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

        final String bio = "some bio";
        
        final String overview = "some overview";

        context.checking(new Expectations()
        {
            {
                oneOf(personMapper).findByOpenSocialId(SAMPLE_UUID);
                will(returnValue(person));

                oneOf(person).getBiography();
                will(returnValue(bio));
                
                oneOf(person).getOverview();
                will(returnValue(overview));
            }
        });

        Representation actual = sut.represent(variant);

        JSONObject json = JSONObject.fromObject(actual.getText());

        assertEquals(json.get(PersonFullBioResource.BIOGRAPHY_KEY), bio);
        
        assertEquals(json.get(PersonFullBioResource.OVERVIEW_KEY), overview);

        context.assertIsSatisfied();
    }

    /**
     * Test the response to a PUT operation.
     * 
     * @throws ResourceException
     *             not expected
     */
    @Test
    public void storeRepresentation() throws ResourceException
    {
        final String bio = "some bio";
        final String overview = "some overview";
        JSONObject json = new JSONObject();
        json.put(PersonFullBioResource.BIOGRAPHY_KEY, bio);
        json.put(PersonFullBioResource.OVERVIEW_KEY, overview);
        StringRepresentation rep = new StringRepresentation(json.toString());

        context.checking(new Expectations()
        {
            {
                oneOf(personMapper).findByOpenSocialId(SAMPLE_UUID);
                will(returnValue(person));

                oneOf(person).setBiography(bio);
                
                oneOf(person).setOverview(overview);

                oneOf(personMapper).flush();
            }
        });

        sut.storeRepresentation(rep);

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

        context.checking(new Expectations()
        {
            {
                oneOf(personMapper).findByOpenSocialId(SAMPLE_UUID);
                will(returnValue(person));

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
    

    /**
     * Simulate an exception.
     * 
     * @throws Exception
     *             should be handled by the storeRepresentation method.
     */
    @Test
    public void storeRepresentationRaiseException() throws Exception
    {
        final Representation rep = context.mock(Representation.class);

        context.checking(new Expectations()
        {
            {
                oneOf(personMapper).findByOpenSocialId(SAMPLE_UUID);
                will(returnValue(person));

                oneOf(rep).getText();
                will(throwException(new Exception()));
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
