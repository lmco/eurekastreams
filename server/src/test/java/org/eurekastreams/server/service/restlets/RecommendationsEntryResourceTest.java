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

import net.sf.json.JSONObject;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;
import org.restlet.data.Request;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.Variant;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.Recommendation;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.RecommendationMapper;

/**
 * Tests for Recommendation Entries.
 *
 */
public class RecommendationsEntryResourceTest
{
    /**
     * Subject under test. 
     */
    private RecommendationsEntryResource sut;
    
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
     * Format of the date that will be returned to the caller.
     */
    private static String formattedJSONDate = "";

    /**
     * The id for the recommendation represented by this resource. Arbitrary. 
     */
    private static final Long RECOMMENDATION_ID = 38L;

    /**
     * Sample text for a recommendation. 
     */
    private static final String RECO_TEXT = "recommendation text";
    
    /**
     * Mocked mapper for deleting a recommendation. 
     */
    private RecommendationMapper recoMapper = context.mock(RecommendationMapper.class);

    /**
     * Person mapper instance.
     */
    private PersonMapper personMapper = context.mock(PersonMapper.class);
    
    /**
     * Set up the SUT. 
     */
    @Before
    public void setup()
    {
        final Request request = context.mock(Request.class);
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("openSocialId", SUBJECT_OPENSOCIAL_ID);
        attributes.put("recommendationId", RECOMMENDATION_ID.toString());
        SimpleDateFormat formater = new SimpleDateFormat(RecommendationsResource.DATE_FORMAT);
        formattedJSONDate = formater.format(RECO_DATE);
        
        buildPeople();
        context.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(attributes));
            }
        });

        sut = new RecommendationsEntryResource();
        sut.setRecommendationMapper(recoMapper);
        sut.setPersonMapper(personMapper);
        sut.initParams(request);
    }
    
    /**
     * Test GET. 
     * @throws ResourceException not expected.
     * @throws IOException unexpected.
     */
    @Test
    public void representation() throws ResourceException, IOException
    {
        Variant variant = context.mock(Variant.class);
        
        final Recommendation recommendation = 
            new Recommendation(SUBJECT_OPENSOCIAL_ID, AUTHOR_OPENSOCIAL_ID, RECO_TEXT);
        recommendation.setDate(RECO_DATE);
        final Recommendation recoMock = context.mock(Recommendation.class);
        final List<String> openSocialIds = new ArrayList<String>();
        openSocialIds.add(AUTHOR_OPENSOCIAL_ID);
        openSocialIds.add(SUBJECT_OPENSOCIAL_ID);
        
        context.checking(new Expectations()
        {
            {
                oneOf(recoMapper).findById(RECOMMENDATION_ID);
                will(returnValue(recommendation));
                
                allowing(recoMock).getId();
                will(returnValue(RECOMMENDATION_ID));
                
                allowing(recoMock).getAuthorOpenSocialId();
                will(returnValue(AUTHOR_OPENSOCIAL_ID));
                
                allowing(recoMock).getSubjectOpenSocialId();
                will(returnValue(SUBJECT_OPENSOCIAL_ID));
                
                allowing(recoMock).getDate();
                will(returnValue(RECO_DATE));
                
                allowing(recoMock).getText();
                will(returnValue(RECO_TEXT));
                
                oneOf(personMapper).findPeopleByOpenSocialIds(openSocialIds);
                will(returnValue(people));
                
            }
        });
        
        Representation actual = sut.represent(variant);
        
        JSONObject jsonActual = JSONObject.fromObject(actual.getText());
        
        assertEquals("0", jsonActual.getString(RecommendationsResource.ID_KEY));
        assertEquals(AUTHOR_OPENSOCIAL_ID, jsonActual.getJSONObject(RecommendationsResource.AUTHOR_KEY)
                .getString(RecommendationsResource.ID_KEY));
        assertEquals(SUBJECT_OPENSOCIAL_ID, jsonActual.getJSONObject(RecommendationsResource.SUBJECT_KEY)
                .getString(RecommendationsResource.ID_KEY));
        assertEquals(formattedJSONDate, jsonActual.getString(RecommendationsResource.DATE_KEY));
        assertEquals(RECO_TEXT, jsonActual.getString(RecommendationsResource.TEXT_KEY));
        
        context.assertIsSatisfied();
    }
    
    /**
     * Test DELETE. 
     * 
     * @throws ResourceException not expected
     */
    @Test
    public void removeRepresentations() throws ResourceException
    {
        context.checking(new Expectations()
        {
            {
                oneOf(recoMapper).delete(RECOMMENDATION_ID);
            }
        });
        
        sut.removeRepresentations();
        
        context.assertIsSatisfied();
    }

    /**
     * Populate a collection of people that will be used during tests.
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
}
