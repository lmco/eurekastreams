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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
import org.eurekastreams.server.domain.Job;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.JobMapper;

/**
 * Test class for JobEntryResource.
 */
public class JobEntryResourceTest
{
    /**
     * Subject under test.
     */
    private JobsEntryResource sut;

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
     * Mocked ResponseAdapter.
     */
    private ResponseAdapter adaptedResponse = context.mock(ResponseAdapter.class);

    /**
     * Mocked job mapper.
     */
    private JobMapper jobMapper = context.mock(JobMapper.class);

    /**
     * UUID to represent a Person. Arbitrary.
     */
    private String uuid = UUID.randomUUID().toString();

    /**
     * A job id for testing. Arbitrary.
     */
    private static final Long JOB_ID = 235L;

    /**
     * The starting date for the job.
     */
    private static Date dateFrom;

    /**
     * The ending date for the job.
     */
    private static Date dateTo;

    /**
     * Company name value for tests.
     */
    private static final String COMPANY_NAME = "company name";

    /**
     * Industry for tests.
     */
    private static final String INDUSTRY = "industry";

    /**
     * Job title for tests.
     */
    private static final String TITLE = "engineer";

    /**
     * Job description for tests.
     */
    private static final String DESCRIPTION = "this that and the other thing";

    /**
     * Job from month for tests.
     */
    private static final String FROM_MONTH = "01";

    /**
     * Job from year for tests.
     */
    private static final String FROM_YEAR = "2008";

    /**
     * Job to year for tests.
     */
    private static final String TO_YEAR_PRESENT = "";

    /**
     * Job to year for tests.
     */
    private static final String TO_YEAR = "2009";

    /**
     * Job to month for tests.
     */
    private static final String TO_MONTH_PRESENT = "";

    /**
     * Job to month for tests.
     */
    private static final String TO_MONTH = "03";

    /**
     * Set up the SUT.
     * 
     * @throws ParseException
     *             should not occur.
     */
    @Before
    public void setup() throws ParseException
    {
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("uuid", uuid);
        attributes.put("jobId", JOB_ID.toString());

        final DateFormat df = new SimpleDateFormat("MM/yyyy");
        dateFrom = df.parse(FROM_MONTH + "/" + FROM_YEAR);
        dateTo = df.parse(TO_MONTH + "/" + TO_YEAR);

        context.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(attributes));
            }
        });

        sut = new JobsEntryResource();
        sut.init(restContext, request, response);
        sut.setJobMapper(jobMapper);
        sut.setAdaptedResponse(adaptedResponse);
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
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        final Variant variant = context.mock(Variant.class);

        final Job job = context.mock(Job.class);

        final Date fullDateFrom = df.parse("0/01/2008");
        final Date fullDateTo = df.parse("0/01/2009");

        context.checking(new Expectations()
        {
            {
                oneOf(jobMapper).findById(JOB_ID);
                will(returnValue(job));

                oneOf(job).getId();
                will(returnValue(JOB_ID));

                oneOf(job).getCompanyName();
                will(returnValue(COMPANY_NAME));

                oneOf(job).getIndustry();
                will(returnValue(INDUSTRY));

                oneOf(job).getTitle();
                will(returnValue(TITLE));

                oneOf(job).getDateFrom();
                will(returnValue(fullDateFrom));

                oneOf(job).getDateTo();
                will(returnValue(fullDateTo));

                oneOf(job).getDescription();
                will(returnValue(DESCRIPTION));
            }
        });

        Representation actual = sut.represent(variant);

        assertEquals("MediaType doesn't match", MediaType.APPLICATION_JSON, actual.getMediaType());

        JSONObject json = JSONObject.fromObject(actual.getText());

        String actualCompanyName = (String) json.get(JobsEntryResource.COMPANY_NAME_KEY);
        assertTrue("Got a wrong job", actualCompanyName.equals(COMPANY_NAME));

        context.assertIsSatisfied();
    }

    /**
     * Test the PUT functionality.
     * 
     * @throws ResourceException
     *             not expected
     * @throws IOException
     *             not expected
     * @throws ParseException
     *             not expected
     */
    @Test
    public void storeRepresentation() throws ResourceException, IOException, ParseException
    {
        Representation entity = setupPUTExpectations(false);

        context.checking(new Expectations()
        {
            {
                oneOf(jobMapper).flush(uuid);
            }
        });

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
     *             not expected
     */
    @Test
    public void storeRepresentationForPresentJob() throws ResourceException, IOException, ParseException
    {
        Representation entity = setupPUTExpectations(true);

        context.checking(new Expectations()
        {
            {
                oneOf(jobMapper).flush(uuid);
            }
        });

        sut.storeRepresentation(entity);

        context.assertIsSatisfied();
    }

    /**
     * Set up the expectations common to the two PUT tests.
     * 
     * @param present
     *            toggle to switch the expectations for testing a job that is current.
     * @return a representation to pass to the storeRepresentation method
     * @throws IOException
     *             not expected
     */
    private Representation setupPUTExpectations(final boolean present) throws IOException
    {
        final Job job = context.mock(Job.class);

        final Person owner = context.mock(Person.class);

        final Representation entity = context.mock(Representation.class);

        final String json = buildJSONString(present);

        context.checking(new Expectations()
        {
            {
                oneOf(jobMapper).findById(JOB_ID);
                will(returnValue(job));

                oneOf(entity).getText();
                will(returnValue(json));

                oneOf(job).getOwner();
                will(returnValue(owner));

                oneOf(job).setCompanyName(COMPANY_NAME);

                oneOf(job).setIndustry(INDUSTRY);

                oneOf(job).setTitle(TITLE);

                oneOf(job).setDateFrom(dateFrom);

                oneOf(job).setDateTo((present ? null : dateTo));

                oneOf(job).setDescription(DESCRIPTION);

                oneOf(job).getId();
                will(returnValue(JOB_ID));

                oneOf(job).getCompanyName();
                will(returnValue(COMPANY_NAME));

                oneOf(job).getIndustry();
                will(returnValue(INDUSTRY));

                oneOf(job).getTitle();
                will(returnValue(TITLE));

                oneOf(job).getDateFrom();
                will(returnValue(dateFrom));

                oneOf(job).getDateTo();
                will(returnValue((present ? null : dateTo)));

                oneOf(job).getDescription();
                will(returnValue(DESCRIPTION));

                oneOf(adaptedResponse).setEntity(json, MediaType.APPLICATION_JSON);
            }
        });

        return entity;
    }

    /**
     * Build up the JSON string that the storeRepresentation method will interpret.
     * 
     * @param presentJob
     *            toggle to change the json to a job that is current for testing.
     * @return a JSON string representing the input data
     */
    private String buildJSONString(final boolean presentJob)
    {
        JSONObject jsonJob = new JSONObject();
        jsonJob.put(JobsResource.JOB_ID_KEY, JOB_ID);
        jsonJob.put(JobsResource.COMPANY_NAME_KEY, COMPANY_NAME);
        jsonJob.put(JobsResource.INDUSTRY_KEY, INDUSTRY);
        jsonJob.put(JobsResource.TITLE_KEY, TITLE);
        jsonJob.put(JobsResource.DATE_FROM_YEAR_KEY, FROM_YEAR);
        jsonJob.put(JobsResource.DATE_FROM_MONTH_KEY, FROM_MONTH);
        jsonJob.put(JobsResource.DATE_TO_YEAR_KEY, presentJob ? TO_YEAR_PRESENT : TO_YEAR);
        jsonJob.put(JobsResource.DATE_TO_MONTH_KEY, presentJob ? TO_MONTH_PRESENT : TO_MONTH);
        jsonJob.put(JobsResource.DESCRIPTION_KEY, DESCRIPTION);

        return jsonJob.toString();
    }

    /**
     * Test the PUT functionality when the provided job id does not correspond to a job in the db. Should throw a
     * ResourceException with a 404.
     * 
     * @throws ResourceException
     *             not expected
     * @throws IOException
     *             not expected
     * @throws ParseException
     *             not expected
     */
    @Test
    public void storeRepresentationWithBadId() throws ResourceException, IOException, ParseException
    {
        final Representation entity = context.mock(Representation.class);

        context.checking(new Expectations()
        {
            {
                oneOf(jobMapper).findById(JOB_ID);
                will(returnValue(null));
            }
        });

        try
        {
            sut.storeRepresentation(entity);
            fail("Should have thrown exception");
        }
        catch (ResourceException ex)
        {
            assertEquals(Status.CLIENT_ERROR_NOT_FOUND, ex.getStatus());
        }

        context.assertIsSatisfied();
    }

    /**
     * Test the DELETE functionality.
     * 
     */
    @Test
    public void removeRepresentations()
    {

        context.checking(new Expectations()
        {
            {
                oneOf(jobMapper).delete(JOB_ID);
                oneOf(jobMapper).flush(uuid);
            }
        });

        sut.removeRepresentations();

        context.assertIsSatisfied();
    }
}
