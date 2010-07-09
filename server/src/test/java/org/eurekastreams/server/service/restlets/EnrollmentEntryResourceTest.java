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

import javax.persistence.NoResultException;

import net.sf.json.JSONObject;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
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
import org.eurekastreams.server.persistence.EnrollmentMapper;

/**
 * Test class for EnrollmentEntryResource.
 */
public class EnrollmentEntryResourceTest
{
    /**
     * Subject under test.
     */
    private EnrollmentsEntryResource sut;

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
     * Mocked enrollment mapper.
     */
    private EnrollmentMapper enrollmentMapper = context.mock(EnrollmentMapper.class);

    /**
     * Mock enrollment.
     */
    private final Enrollment enrollment = context.mock(Enrollment.class);

    /**
     * UUID to represent a Person. Arbitrary.
     */
    private String uuid = UUID.randomUUID().toString();

    /**
     * the school name used for test.
     */
    private final String schoolName = "school name";

    /**
     * A enrollment id for testing. Arbitrary.
     */
    private static final String ENROLLMENT_ID = "235";

    /**
     * Set up the SUT.
     * 
     * @param inUuid
     *            uuid to load into attributes.
     * @param inEnrollmentId
     *            enrollmentId to load into attributes.
     * @throws ParseException
     *             should not occur.
     */

    private void setupSutAttributesExpectations(final String inUuid, final String inEnrollmentId) throws ParseException
    {
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("uuid", inUuid);
        attributes.put("enrollmentId", inEnrollmentId);

        context.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(attributes));
            }
        });

        sut = new EnrollmentsEntryResource();
        sut.init(restContext, request, response);
        sut.setEnrollmentMapper(enrollmentMapper);
        sut.setAdaptedResponse(responseAdapter);
    }

    /**
     * Test the GET call.
     * 
     * @throws ResourceException
     *             not expected.
     * @throws IOException
     *             not expected.
     * @throws ParseException
     *             not expected.
     */
    @Test
    public void represent() throws ResourceException, IOException, ParseException
    {
        final Variant variant = context.mock(Variant.class);
        setupSutAttributesExpectations(uuid, ENROLLMENT_ID);
        setEnrollmentExpecations();

        context.checking(new Expectations()
        {
            {
                oneOf(enrollmentMapper).findById(Long.valueOf(ENROLLMENT_ID));
                will(returnValue(enrollment));
            }
        });

        Representation actual = sut.represent(variant);

        assertEquals("MediaType doesn't match", MediaType.APPLICATION_JSON, actual.getMediaType());

        JSONObject json = JSONObject.fromObject(actual.getText());

        String actualSchoolName = (String) json.get(EnrollmentsCollectionResource.SCHOOL_NAME_KEY);
        assertTrue("Incorrect school name", actualSchoolName.equals(schoolName));
        assertEquals("Incorrect number of activities", 3, json.getJSONArray(
                EnrollmentsCollectionResource.ACTIVITIES_KEY).size());
        assertEquals("Incorrect number of areas of study", 4, json.getJSONArray(
                EnrollmentsCollectionResource.AREAS_OF_STUDY_KEY).size());

        context.assertIsSatisfied();
    }

    /**
     * Test represent method with bad id. Expected exception.
     * 
     * @throws ResourceException
     *             Expected.
     * @throws ParseException
     *             Not expected.
     */
    @Test(expected = ResourceException.class)
    public void testRepresentBadId() throws ResourceException, ParseException
    {
        setupSutAttributesExpectations(uuid, ENROLLMENT_ID);

        final Variant variant = context.mock(Variant.class);
        context.checking(new Expectations()
        {
            {
                oneOf(enrollmentMapper).findById(Long.valueOf(ENROLLMENT_ID));
                will(throwException(new NoResultException()));
            }
        });
        sut.represent(variant);
    }

    /**
     * Test the PUT functionality.
     * 
     * @throws ResourceException
     *             not expected
     * @throws IOException
     *             not expected
     * @throws ParseException
     *             not expected.
     */
    @Test
    public void storeRepresentation() throws ResourceException, IOException, ParseException
    {
        setupSutAttributesExpectations(uuid, ENROLLMENT_ID);
        setEnrollmentExpecations();
        Representation entity = setupPUTExpectations(false);

        context.checking(new Expectations()
        {
            {
                oneOf(enrollmentMapper).flush(uuid);
            }
        });

        sut.storeRepresentation(entity);

        context.assertIsSatisfied();
    }

    /**
     * Test method when entity.getText throws IOException.
     * 
     * @throws ResourceException
     *             expected.
     * @throws IOException
     *             not expected.
     * @throws ParseException
     *             not expected.
     */
    @Test(expected = ResourceException.class)
    public void storeRepresentationIOException() throws ResourceException, IOException, ParseException
    {
        setupSutAttributesExpectations(uuid, ENROLLMENT_ID);
        setEnrollmentExpecations();
        Representation entity = setupPUTExpectations(true);

        sut.storeRepresentation(entity);

        context.assertIsSatisfied();
    }

    /**
     * Test the PUT functionality when the client does not provide a closing date.
     * 
     * @throws ResourceException
     *             not expected
     * @throws IOException
     *             not expected
     * @throws ParseException
     *             not expected.
     */
    @Test
    public void storeRepresentationForPresentEnrollment() throws ResourceException, IOException, ParseException
    {
        setupSutAttributesExpectations(uuid, ENROLLMENT_ID);
        setEnrollmentExpecations();
        Representation entity = setupPUTExpectations(false);

        context.checking(new Expectations()
        {
            {
                oneOf(enrollmentMapper).flush(uuid);
            }
        });

        
        sut.storeRepresentation(entity);

        context.assertIsSatisfied();
    }

    /**
     * Test the PUT functionality when the provided enrollment id does not correspond to a enrollment in the db. Should
     * throw a ResourceException with a 404.
     * 
     * @throws ResourceException
     *             not expected
     * @throws IOException
     *             not expected
     * @throws ParseException
     *             not expected
     */
    @Test(expected = ResourceException.class)
    public void storeRepresentationWithBadId() throws ResourceException, IOException, ParseException
    {
        setupSutAttributesExpectations(uuid, ENROLLMENT_ID);
        final Representation entity = context.mock(Representation.class);

        context.checking(new Expectations()
        {
            {
                oneOf(enrollmentMapper).findById(Long.valueOf(ENROLLMENT_ID));
                will(returnValue(null));
            }
        });

        sut.storeRepresentation(entity);
        context.assertIsSatisfied();
    }

    /**
     * Thest the PUT function and excercise validation with a bad enrollment JSON string. Should throw a Resource
     * Exception.
     * 
     * @throws ResourceException
     *             - Expected with a validation error.
     * @throws IOException
     *             - non expected.
     * @throws ParseException
     *             - not expected.
     */
    @Test(expected = ResourceException.class)
    public void testValidationResourceException() throws ResourceException, IOException, ParseException
    {
        setupSutAttributesExpectations(uuid, ENROLLMENT_ID);
        setEnrollmentExpecations();
        final Representation entity = context.mock(Representation.class);
        final String jsonString = "{\"areasofstudy\":[],\"id\":\"235\",\"graddate\":\"baddate\",\"degree\""
                + ":\"Bachelors\",\"schoolname\":\"\",\"activities\":[\"stuff\"],\"additionaldetails\":\"some stuff\"}";

        context.checking(new Expectations()
        {
            {
                oneOf(enrollmentMapper).findById(Long.valueOf(ENROLLMENT_ID));
                will(returnValue(enrollment));

                oneOf(entity).getText();
                will(returnValue(jsonString));
            }
        });

        sut.storeRepresentation(entity);
        context.assertIsSatisfied();
    }

    /**
     * Test the DELETE functionality.
     * 
     * @throws ParseException
     *             on error.
     * 
     */
    @Test
    public void removeRepresentations() throws ParseException
    {
        setupSutAttributesExpectations(uuid, ENROLLMENT_ID);

        context.checking(new Expectations()
        {
            {
                oneOf(enrollmentMapper).delete(Long.valueOf(ENROLLMENT_ID));
                oneOf(enrollmentMapper).flush(uuid);                
            }
        });

        sut.removeRepresentations();

        context.assertIsSatisfied();
    }

    /**
     * Helper method to set up enrollment mock so it can respond to calls.
     * 
     * @throws ParseException
     *             not expected.
     */
    private void setEnrollmentExpecations() throws ParseException
    {

        final long enrollmentId = 2L;

        final String degree = "BS Computer Sciences";

        DateFormat df = new SimpleDateFormat("yyyy");
        final Date gradDate = df.parse("2008");

        final String additionalDetails = "this that and the other thing";

        final List<BackgroundItem> areasOfStudy = new ArrayList<BackgroundItem>();
        areasOfStudy.add(new BackgroundItem("electrical engineering", BackgroundItemType.AREA_OF_STUDY));
        areasOfStudy.add(new BackgroundItem("computer scientce", BackgroundItemType.AREA_OF_STUDY));
        areasOfStudy.add(new BackgroundItem("music", BackgroundItemType.AREA_OF_STUDY));
        areasOfStudy.add(new BackgroundItem("goofing off", BackgroundItemType.AREA_OF_STUDY));

        final List<BackgroundItem> activities = new ArrayList<BackgroundItem>();
        activities.add(new BackgroundItem("marching band", BackgroundItemType.ACTIVITY_OR_SOCIETY));
        activities.add(new BackgroundItem("computer geek club", BackgroundItemType.ACTIVITY_OR_SOCIETY));
        activities.add(new BackgroundItem("ieee", BackgroundItemType.ACTIVITY_OR_SOCIETY));

        context.checking(new Expectations()
        {
            {
                // create JSON representation
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

    /**
     * Set up the expectations common to the two PUT tests.
     * 
     * @param throwIOException
     *            If entity.getText should throw IOException or not.
     * @return a representation to pass to the storeRepresentation method
     * @throws IOException
     *             not expected
     * @throws ParseException
     *             not expected.
     */
    @SuppressWarnings("unchecked")
    private Representation setupPUTExpectations(final boolean throwIOException) throws IOException, ParseException
    {
        this.setEnrollmentExpecations();
        final Representation entity = context.mock(Representation.class);
        final String jsonString = sut.convertEnrollmentToJSONObject(enrollment).toString();
        DateFormat df = new SimpleDateFormat("yyyy");
        final Date gradDate = df.parse("2008");
        context.checking(new Expectations()
        {
            {
                oneOf(enrollmentMapper).findById(Long.valueOf(ENROLLMENT_ID));
                will(returnValue(enrollment));

                if (throwIOException)
                {
                    oneOf(entity).getText();
                    will(throwException(new IOException()));
                }
                else
                {
                    oneOf(entity).getText();
                    will(returnValue(jsonString));
                }

                oneOf(enrollment).setActivities(with(any(List.class)));

                oneOf(enrollment).setAreasOfStudy(with(any(List.class)));

                oneOf(enrollment).setSchoolName(with(any(String.class)));

                oneOf(enrollment).setDegree(with(any(String.class)));

                oneOf(enrollment).setGradDate(with(gradDate));

                oneOf(enrollment).setAdditionalDetails(with(any(String.class)));

                oneOf(responseAdapter).setEntity(with(any(String.class)), with(any(MediaType.class)));
            }
        });

        return entity;
    }
}
