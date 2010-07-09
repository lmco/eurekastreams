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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;
import org.eurekastreams.server.domain.Background;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.domain.CollectionFormat;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.BackgroundMapper;

/**
 * Test class for OrgOverviewResource.
 */
public class PersonBackgroundResourceTest
{
    /**
     * Subject under test.
     */
    private PersonBackgroundResource sut;

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
     * Adapter mock for the Response object.
     */
    private ResponseAdapter responseAdapter = context.mock(ResponseAdapter.class);
    
    /**
     * An accountId to use for testing. Arbitrary.
     */
    private final String uuid = UUID.randomUUID().toString();

    /**
     * Mocked mapper.
     */
    private BackgroundMapper mapper = context.mock(BackgroundMapper.class);

    /**
     * A Background used to test the resource.
     */
    private Background background;

    /**
     * convenience method.
     * 
     * @param toParse - string of the Background Item to parse.
     * @param type - Background type of the Background Item to parse.
     * @return items.
     */
    private List<BackgroundItem> parseItems(final String toParse, final BackgroundItemType type)
    {
        CollectionFormat formatter = new CollectionFormat();
        List<BackgroundItem> backgroundItems = new ArrayList<BackgroundItem>();
        Collection<String> elements = formatter.parse(toParse);
        for (String token : elements)
        {
            backgroundItems.add(new BackgroundItem(token, type));
        }
        return backgroundItems;
    }
    
    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {

        background = new Background(new Person());


        background.setBackgroundItems(parseItems("a,b,c", BackgroundItemType.AFFILIATION), 
                BackgroundItemType.AFFILIATION);
        background.setBackgroundItems(parseItems("d,e,f", BackgroundItemType.HONOR), 
                BackgroundItemType.HONOR);
        background.setBackgroundItems(parseItems("1,2,3", BackgroundItemType.INTEREST), 
                BackgroundItemType.INTEREST);
        background.setBackgroundItems(parseItems("4,5,6", BackgroundItemType.SKILL), 
                BackgroundItemType.SKILL);

        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put(PersonBackgroundResource.UUID_KEY, uuid);

        context.checking(new Expectations()
        {
            {
                oneOf(request).getAttributes();
                will(returnValue(attributes));
            }
        });

        sut = new PersonBackgroundResource();
        sut.init(restContext, request, response);
        sut.setMapper(mapper);
        sut.setAdaptedResponse(responseAdapter);

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
    public void testRepresent() throws ResourceException, IOException
    {

        final Variant variant = context.mock(Variant.class);

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).findOrCreatePersonBackground(with(uuid));
                will(returnValue(background));
            }
        });

        Representation actual = sut.represent(variant);

        assertEquals("MediaType should be application/json", MediaType.APPLICATION_JSON, actual.getMediaType());

        JSONObject json = JSONObject.fromObject(actual.getText());

        // TODO test the returned text a little better
        JSONArray affiliationsArray = json.getJSONArray(PersonBackgroundResource.AFFILIATIONS_KEY);
        JSONArray honorsawardsArray = json.getJSONArray(PersonBackgroundResource.HONORSAWARDS_KEY);
        JSONArray interestshobbiesArray = json.getJSONArray(PersonBackgroundResource.INTERESTSHOBBIES_KEY);
        JSONArray skillsspecialtiesArray = json.getJSONArray(PersonBackgroundResource.SKILLSSPECIALTIES_KEY);
        
        assertNotNull("Affiliations array is null and should not be", affiliationsArray);
        assertNotNull("Honors Awards array is null and should not be", honorsawardsArray);
        assertNotNull("Interests Hobbies array is null and should not be", interestshobbiesArray);
        assertNotNull("Skills Specialties array is null and should not be", skillsspecialtiesArray);
        
        assertEquals("Affiliations array should contain 3 items and does not", 3, affiliationsArray.size());
        assertEquals("Honors Awards array should contain 3 items and does not", 3, honorsawardsArray.size());
        assertEquals("Interests Hobbies array should contain 3 items and does not", 3, interestshobbiesArray.size());
        assertEquals("Skills Specialties array should contain 3 items and does not", 3, skillsspecialtiesArray.size());

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
    public void testStoreRepresentation() throws ResourceException, IOException
    {
        
        String json = sut.convertBackgroundToJSON(background).toString();
        
        StringRepresentation jsonRep = new StringRepresentation(json);
        
        final Background bg = new Background(new Person());
        bg.setBackgroundItems(new ArrayList<BackgroundItem>(), BackgroundItemType.AFFILIATION);
        bg.setBackgroundItems(new ArrayList<BackgroundItem>(), BackgroundItemType.HONOR);
        bg.setBackgroundItems(new ArrayList<BackgroundItem>(), BackgroundItemType.INTEREST);
        bg.setBackgroundItems(new ArrayList<BackgroundItem>(), BackgroundItemType.SKILL);

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).findOrCreatePersonBackground(with(uuid));
                will(returnValue(bg));

                oneOf(mapper).flush(uuid);
            }
        });

        // precondition, the background is empty
        assertEquals(0, bg.getBackgroundItems(BackgroundItemType.AFFILIATION).size());
        assertEquals(0, bg.getBackgroundItems(BackgroundItemType.HONOR).size());
        assertEquals(0, bg.getBackgroundItems(BackgroundItemType.INTEREST).size());
        assertEquals(0, bg.getBackgroundItems(BackgroundItemType.SKILL).size());
        
        // perform the action to be tested
        sut.storeRepresentation(jsonRep);

        // post condition, make sure mapper was called and items were set
        context.assertIsSatisfied();
        assertEquals(3, bg.getBackgroundItems(BackgroundItemType.AFFILIATION).size());
        assertEquals(3, bg.getBackgroundItems(BackgroundItemType.HONOR).size());
        assertEquals(3, bg.getBackgroundItems(BackgroundItemType.INTEREST).size());
        assertEquals(3, bg.getBackgroundItems(BackgroundItemType.SKILL).size());
        assertEquals("a", bg.getBackgroundItems(BackgroundItemType.AFFILIATION).get(0).toString());
        assertEquals("d", bg.getBackgroundItems(BackgroundItemType.HONOR).get(0).toString());
        assertEquals("1", bg.getBackgroundItems(BackgroundItemType.INTEREST).get(0).toString());
        assertEquals("4", bg.getBackgroundItems(BackgroundItemType.SKILL).get(0).toString());

        
    }

    /**
     * Test the POST functionality where the client has sent over and invalid type of background data. 
     * 
     * @throws ResourceException
     *             not expected
     * @throws IOException
     *             not expected
     */
    @Test
    public void testStoreRepresentationWithBadType() throws ResourceException, IOException
    {
        //Dataset with invalid number and type of keys.
        String json = "{\"affiliations\":[\"title\"], \"Invalid Type\":[\"text\"]}";
        
        StringRepresentation jsonRep = new StringRepresentation(json);
        
        final Background mockBg = context.mock(Background.class);

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).findOrCreatePersonBackground(with(uuid));
                will(returnValue(mockBg));

                oneOf(responseAdapter).setEntity(with(any(String.class)), 
                        with(any(MediaType.class)));

                oneOf(responseAdapter).setStatus(with(any(Status.class)));
            }
        });

        // Should throw exception due to bad JSON
        sut.storeRepresentation(jsonRep);
    }
    

    /**
     * Test the POST functionality where the client has sent over and invalid content
     * triggerring a validation error. 
     * 
     * @throws ResourceException
     *             not expected
     * @throws IOException
     *             not expected
     */
    @Test
    public void testStoreRepresentationWithInvalidToken() throws ResourceException, IOException
    {
        //Dataset with bad content type in the interestshobbies array.
        String json = "{\"affiliations\":[\"badcharacter>\"], \"skillsspecialties\":[\"text\"], "
            + "\"interestshobbies\":[\"title\"], \"honorsawards\":[\"text\"]}";
        
        StringRepresentation jsonRep = new StringRepresentation(json);
        
        final Background mockBg = context.mock(Background.class);

        context.checking(new Expectations()
        {
            {
                oneOf(mapper).findOrCreatePersonBackground(with(uuid));
                will(returnValue(mockBg));

                oneOf(responseAdapter).setEntity(with(any(String.class)), 
                        with(any(MediaType.class)));

                oneOf(responseAdapter).setStatus(with(any(Status.class)));
            }
        });

        // Should throw exception due to bad JSON
        sut.storeRepresentation(jsonRep);
    }
}
