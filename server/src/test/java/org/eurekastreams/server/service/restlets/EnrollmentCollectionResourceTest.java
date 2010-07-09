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

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
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
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.domain.Enrollment;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.EnrollmentMapper;
import org.eurekastreams.server.persistence.PersonMapper;

/**
 * Test class for EnrollmentCollectionResource.
 */
public class EnrollmentCollectionResourceTest
{
    /**
     * Subject under test.
     */
    private EnrollmentsCollectionResource sut;

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
     * Mocked enrollment mapper.
     */
    private EnrollmentMapper enrollmentMapper = context.mock(EnrollmentMapper.class);

    /**
     * Mocked person mapper.
     */
    private PersonMapper personMapper = context.mock(PersonMapper.class);
    
    /**
     * Adapter mock for the Response object.
     */
    private ResponseAdapter responseAdapter = context.mock(ResponseAdapter.class);
    
    /**
     * Mock enrollment.
     */
    final Enrollment enrollment = context.mock(Enrollment.class);
    
    /**
     * Mock person.
     */
    final Person person = context.mock(Person.class);
    
    /**
     * UUID to represent a Person. Arbitrary.
     */
    private String uuid = UUID.randomUUID().toString();
    
    /**
     * School name.
     */
    final String schoolName = "school name";

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("shortName", (Object) "enrollment overview");
        attributes.put("uuid", uuid);
        
        context.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(attributes));
            }
        });
        
        sut = new EnrollmentsCollectionResource();
        sut.init(restContext, request, response);
        sut.setEnrollmentMapper(enrollmentMapper);
        sut.setPersonMapper(personMapper);
        sut.setAdaptedResponse(responseAdapter);
    }

    /**
     * Test the GET call.
     * 
     * @throws ResourceException
     *             not expected
     * @throws IOException
     *             not expected
     * @throws ParseException
     *             not expected
     */
    @Test
    public void represent() throws ResourceException, IOException, ParseException
    {
        this.setEnrollmentExpecations(false);
        final Variant variant = context.mock(Variant.class);
        final List<Enrollment> enrollments = new ArrayList<Enrollment>();
        enrollments.add(enrollment);

        context.checking(new Expectations()
        {
            {
                oneOf(enrollmentMapper).findPersonEnrollmentsByOpenSocialId(with(any(String.class)));
                will(returnValue(enrollments));
            }
        });

        Representation actual = sut.represent(variant);
        assertEquals("MediaType doesn't match", MediaType.APPLICATION_JSON, actual.getMediaType());

        JSONObject json = JSONObject.fromObject(actual.getText());
        assertEquals("Got wrong number of enrollments", enrollments.size(), 
                json.getJSONArray(EnrollmentsCollectionResource.ENROLLMENTS_KEY).size());

        // Check the first enrollment returned
        JSONArray jsonEnrollments = json.getJSONArray(EnrollmentsCollectionResource.ENROLLMENTS_KEY);
        JSONObject firstEnrollment = JSONObject.fromObject(jsonEnrollments.getJSONObject(0));
        String actualSchoolName = (String) firstEnrollment.get(EnrollmentsCollectionResource.SCHOOL_NAME_KEY);
        assertTrue("Incorrect school name", actualSchoolName.equals(schoolName));
        assertEquals("Incorrect number of activities", 3, 
                firstEnrollment.getJSONArray(EnrollmentsCollectionResource.ACTIVITIES_KEY).size());
        assertEquals("Incorrect number of areas of study", 4, 
                firstEnrollment.getJSONArray(EnrollmentsCollectionResource.AREAS_OF_STUDY_KEY).size());

        context.assertIsSatisfied();
    }

    /**
     * Test the POST functionality.
     * 
     * @throws ResourceException
     *             not expected
     * @throws IOException
     *             not expected
     * @throws ParseException
     *             not expected
     */
    @Test
    public void acceptRepresentation() throws ResourceException, IOException, ParseException
    {        
        final Representation entity = context.mock(Representation.class);
        
        setEnrollmentExpecations(false);
        
        final String jsonString = sut.convertEnrollmentToJSONObject(enrollment).toString();

        context.checking(new Expectations()
        {
            {
                oneOf(personMapper).findByOpenSocialId(with(any(String.class)));
                will(returnValue(person));

                oneOf(entity).getText();
                will(returnValue(jsonString));

                oneOf(enrollmentMapper).insert(with(any(Enrollment.class)));
                
                oneOf(responseAdapter).setEntity(with(any(String.class)), 
                        with(any(MediaType.class)));
                
                oneOf(enrollmentMapper).flush(uuid);
            }
        });

        sut.acceptRepresentation(entity);

        context.assertIsSatisfied();
    }
    
    /**
     * Test the POST functionality with IOException.
     * 
     * @throws ResourceException
     *             not expected
     * @throws IOException
     *             not expected
     * @throws ParseException
     *             not expected
     */
    @Test(expected = ResourceException.class)
    public void acceptRepresentationIOException() throws ResourceException, IOException, ParseException
    {        
        final Representation entity = context.mock(Representation.class);        

        context.checking(new Expectations()
        {
            {
                oneOf(personMapper).findByOpenSocialId(with(any(String.class)));
                will(returnValue(person));

                oneOf(entity).getText();
                will(throwException(new IOException()));
            }
        });

        sut.acceptRepresentation(entity);

        context.assertIsSatisfied();
    }
    
    /**
     *TODO:THIS TEST NEEDS TO BE REWRITTEN - we no longer have more than one date and this test needs to verify format.
     * Test the POST functionality when the client has sent no "To" date, indicating that this is the presently held
     * enrollment.
     * @throws ParseException 
     * 
     * @throws ResourceException
     *             not expected
     * @throws IOException
     *             not expected
     * @throws ParseException
     *             not expected.
     */
//    @Test(expected = ResourceException.class)
//    public void acceptRepresentationForInvalidInrollmentDates() throws ResourceException, IOException, ParseException
//    {
//        final Representation entity = context.mock(Representation.class);
//        
//        setEnrollmentExpecations(true);
//        
//        final String jsonString = sut.convertEnrollmentToJSONObject(enrollment).toString();
//
//        context.checking(new Expectations()
//        {
//            {
//                oneOf(personMapper).findByOpenSocialId(with(any(String.class)));
//                will(returnValue(person));
//
//                oneOf(entity).getText();
//                will(returnValue(jsonString));
//
//                oneOf(enrollmentMapper).insert(with(any(Enrollment.class)));
//
//                oneOf(responseAdapter).setEntity(with(any(String.class)), 
//                        with(any(MediaType.class)));
//            }
//        });
//
//        sut.acceptRepresentation(entity);
//
//        context.assertIsSatisfied();
//    }
    
    /**
     * Helper method to set up enrollment mock so it can respond to calls.
     * @param invalidDateTo True if use an invalid date for dateTo, false otherwise.
     * @throws ParseException not expected.
     */
    private void setEnrollmentExpecations(final boolean invalidDateTo) throws ParseException
    {
        
        final long enrollmentId = 2L;
        
        final String degree = "BS Computer Sciences";
        
        DateFormat df = new SimpleDateFormat("yyyy");
        final Date gradDate = df.parse("2008");
        
        final String additionalDetails = "this that and the other thing";
              
        final List<BackgroundItem> areasOfStudy = new ArrayList<BackgroundItem>();
        areasOfStudy.add(new BackgroundItem("electrical engineering", BackgroundItemType.AREA_OF_STUDY));
        areasOfStudy.add(new BackgroundItem("computer science", BackgroundItemType.AREA_OF_STUDY));
        areasOfStudy.add(new BackgroundItem("music", BackgroundItemType.AREA_OF_STUDY));
        areasOfStudy.add(new BackgroundItem("goofing off", BackgroundItemType.AREA_OF_STUDY));
        
        final List<BackgroundItem> activities = new ArrayList<BackgroundItem>();
        activities.add(new BackgroundItem("marching band", BackgroundItemType.ACTIVITY_OR_SOCIETY));
        activities.add(new BackgroundItem("computer geek club", BackgroundItemType.ACTIVITY_OR_SOCIETY));
        activities.add(new BackgroundItem("ieee", BackgroundItemType.ACTIVITY_OR_SOCIETY));

        context.checking(new Expectations()
        {
            {
                //create JSON representation
                allowing(enrollment).getId();
                will(returnValue(enrollmentId));

                oneOf(enrollment).getSchoolName();
                will(returnValue(schoolName));

                oneOf(enrollment).getDegree();
                will(returnValue(degree));

                oneOf(enrollment).getAreasOfStudy();
                will(returnValue(areasOfStudy));

                oneOf(enrollment).getGradDate();
                will(returnValue(gradDate));

                oneOf(enrollment).getActivities();
                will(returnValue(activities));

                oneOf(enrollment).getAdditionalDetails();
                will(returnValue(additionalDetails));                              
            }
        });
    }
}
