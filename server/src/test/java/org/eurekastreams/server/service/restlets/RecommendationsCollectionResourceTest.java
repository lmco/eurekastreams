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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Recommendation;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.RecommendationMapper;

/**
 * Test class for the RecommendationsCollectionResource.
 */
public class RecommendationsCollectionResourceTest
{
    /**
     * Subject under test.
     */
    RecommendationsCollectionResource sut;

    /**
     * List of people expected to be returned from the search for users by
     * opensocial id.
     */
    private List<Person> people = new ArrayList<Person>();
    
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
     * Mocked mapper for retrieving recommendations.
     */
    private RecommendationMapper recoMapper = context.mock(RecommendationMapper.class);
    
    /**
     * Mocked mapper for retrieving author information.
     */
    private PersonMapper personMapper = context.mock(PersonMapper.class);

    /**
     * Adapter mock for the Response object.
     */
    private ResponseAdapter responseAdapter = context.mock(ResponseAdapter.class);
    
    /**
     * An Open Social id to use for testing. Arbitrary.
     */
    private static final String SUBJECT_OPENSOCIAL_ID = UUID.randomUUID().toString();
    
    /**
     * Another Open Social id. Arbitrary. 
     */
    private static final String AUTHOR_OPENSOCIAL_ID = UUID.randomUUID().toString();

    /**
     * An arbitrary date/time at which the recommendations where made.
     */
    private static final Date RECO_DATE = new Date();

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        Context restContext = context.mock(Context.class);
        final Request request = context.mock(Request.class);
        Response response = context.mock(Response.class);

        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("openSocialId", SUBJECT_OPENSOCIAL_ID);
        attributes.put("maxResults", "5");

        buildPeople();
        
        context.checking(new Expectations()
        {
            {
                atLeast(2).of(request).getAttributes();
                will(returnValue(attributes));
            }
        });

        sut = new RecommendationsCollectionResource();
        sut.init(restContext, request, response);
        sut.setRecommendationMapper(recoMapper);
        sut.setPersonMapper(personMapper);
        sut.setAdaptedResponse(responseAdapter);
    }

    /**
     * Test the GET call.
     * 
     * @throws ResourceException
     *             should not occur.
     * @throws IOException
     *             should not occur
     */
    @Test
    public void represent() throws ResourceException, IOException
    {
        Variant variant = context.mock(Variant.class);

        final List<Recommendation> recos = buildRecommendationList();
        
        final List<String> openSocialIds = new ArrayList<String>();
        openSocialIds.add(SUBJECT_OPENSOCIAL_ID);
        openSocialIds.add(AUTHOR_OPENSOCIAL_ID);
        openSocialIds.add(AUTHOR_OPENSOCIAL_ID);
        openSocialIds.add(AUTHOR_OPENSOCIAL_ID);
        
        context.checking(new Expectations()
        {
            {
                oneOf(recoMapper).findBySubjectOpenSocialId(SUBJECT_OPENSOCIAL_ID, 5);
                will(returnValue(recos));
                
                allowing(personMapper).findPeopleByOpenSocialIds(openSocialIds);
                will(returnValue(people));
            }
        });

        Representation actual = sut.represent(variant);

        assertEquals(MediaType.APPLICATION_JSON, actual.getMediaType());

        JSONObject json = JSONObject.fromObject(actual.getText());
        JSONArray recommendations = json.getJSONArray(RecommendationsCollectionResource.RECOMMENDATIONS_KEY);
        
        assertEquals(recos.size(), recommendations.size());
        
        context.assertIsSatisfied();
    }

    /**
     * Utility method to set up some recommendations.
     * 
     * @return a list of recommendations with expectations set up.
     */
    private List<Recommendation> buildRecommendationList()
    {
        final List<Recommendation> list = new ArrayList<Recommendation>();

        context.checking(new Expectations()
        {
            {
                setupRecommendation(1, "reco1");
                setupRecommendation(2, "reco2");
                setupRecommendation(3, "reco3");
            }

            private void setupRecommendation(final long id, final String recoName)
            {
                final Recommendation reco = context.mock(Recommendation.class, recoName);
                list.add(reco);
                
                allowing(reco).getId();
                will(returnValue(id));

                allowing(reco).getSubjectOpenSocialId();
                will(returnValue(SUBJECT_OPENSOCIAL_ID));

                allowing(reco).getAuthorOpenSocialId();
                will(returnValue(AUTHOR_OPENSOCIAL_ID));

                allowing(reco).getDate();
                will(returnValue(RECO_DATE));

                allowing(reco).getText();
                will(returnValue(recoName));
            }
        });

        return list;
    }
    
    /**
     * Test the POST functionality. 
     * @throws IOException not expected 
     * @throws ResourceException not expected
     */
    @Test
    public void acceptRepresentation() throws IOException, ResourceException
    {
        final Representation entity = context.mock(Representation.class);
        final String json = buildJSONRecommendation();
        final List<String> openSocialIds = new ArrayList<String>();
        openSocialIds.add(AUTHOR_OPENSOCIAL_ID);
        openSocialIds.add(SUBJECT_OPENSOCIAL_ID);
        context.checking(new Expectations()
        {
            {
                // note: this really needs to be called only one time
                oneOf(entity).getText();
                will(returnValue(json));
                
                oneOf(recoMapper).insert(with(any(Recommendation.class)));
                
                allowing(personMapper).findPeopleByOpenSocialIds(openSocialIds);
                will(returnValue(people));
                
                oneOf(responseAdapter).setEntity(buildJSONResponse(), 
                        MediaType.APPLICATION_JSON);
            }
        });
        
        sut.acceptRepresentation(entity);
        
        context.assertIsSatisfied();
    }
    
    /**
     * Populate a global collection of people to be used in tests.
     */
    private void buildPeople()
    {
        Person authorPerson = new Person();
        authorPerson.setOpenSocialId(AUTHOR_OPENSOCIAL_ID);
        Person subjectPerson = new Person();
        subjectPerson.setOpenSocialId(SUBJECT_OPENSOCIAL_ID);
        
        people.add(authorPerson);
        people.add(subjectPerson);
    }
    
    /**
     * Assemble a JSON string that represents a test response from the Restlet.
     * @return - string representation of a JSON Recommendation.
     */
    private String buildJSONResponse()
    {
        JSONObject json = new JSONObject();
        JSONObject jsonAuthor = new JSONObject();
        JSONObject jsonSubject = new JSONObject();
        
        jsonAuthor.put(RecommendationsResource.ID_KEY, AUTHOR_OPENSOCIAL_ID);
        jsonAuthor.put(RecommendationsResource.AUTHOR_NAME_KEY, "null null");
        jsonAuthor.put(RecommendationsResource.PERSON_IMAGE_KEY, "/style/images/noPhoto50.png");
        
        jsonSubject.put(RecommendationsResource.ID_KEY, SUBJECT_OPENSOCIAL_ID);
        jsonSubject.put(RecommendationsResource.AUTHOR_NAME_KEY, "null null");
        jsonSubject.put(RecommendationsResource.PERSON_IMAGE_KEY, "/style/images/noPhoto50.png");
        
        json.put(RecommendationsResource.ID_KEY, 0);
        json.put(RecommendationsResource.AUTHOR_KEY, jsonAuthor);
        json.put(RecommendationsResource.SUBJECT_KEY, jsonSubject);
        SimpleDateFormat formater = new SimpleDateFormat(RecommendationsResource.DATE_FORMAT);
        json.put(RecommendationsResource.DATE_KEY, formater.format(RECO_DATE));
        json.put(RecommendationsResource.TEXT_KEY, "recommendation text");
        
        return json.toString();
    }
    
    /**
     * Utility method to build a JSON string representing a Recommendation being posted to the collection.  
     * @return JSON string
     */
    private String buildJSONRecommendation()
    {
        JSONObject json = new JSONObject();
        JSONObject jsonAuthor = new JSONObject();
        JSONObject jsonSubject = new JSONObject();
        
        jsonAuthor.put(RecommendationsResource.ID_KEY, AUTHOR_OPENSOCIAL_ID);
        jsonAuthor.put(RecommendationsResource.AUTHOR_NAME_KEY, "Joe Smith");
        jsonAuthor.put(RecommendationsResource.PERSON_IMAGE_KEY, "/image/image.png");
        
        jsonSubject.put(RecommendationsResource.ID_KEY, SUBJECT_OPENSOCIAL_ID);
        jsonSubject.put(RecommendationsResource.AUTHOR_NAME_KEY, "John Smith");
        jsonSubject.put(RecommendationsResource.PERSON_IMAGE_KEY, "/image/image.png");
        
        json.put(RecommendationsResource.ID_KEY, "34");
        json.put(RecommendationsResource.AUTHOR_KEY, jsonAuthor);
        json.put(RecommendationsResource.SUBJECT_KEY, jsonSubject);
        SimpleDateFormat formater = new SimpleDateFormat(RecommendationsResource.DATE_FORMAT);
        json.put(RecommendationsResource.DATE_KEY, formater.format(RECO_DATE));
        json.put(RecommendationsResource.TEXT_KEY, "recommendation text");
        
        return json.toString();
    }
}
