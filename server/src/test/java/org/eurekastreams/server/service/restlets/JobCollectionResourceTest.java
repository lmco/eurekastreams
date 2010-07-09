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

import junit.framework.Assert;
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
import org.eurekastreams.server.domain.Job;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.JobMapper;
import org.eurekastreams.server.persistence.PersonMapper;

/**
 * Test class for JobCollectionResource.
 */
public class JobCollectionResourceTest
{
    /**
     * Subject under test.
     */
    private JobsCollectionResource sut;

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
     * Mocked context object.
     */
    private Context restContext = context.mock(Context.class);
    
    /**
     * Mocked response object.
     */
    private Response response = context.mock(Response.class);
    
    /**
     * Mocked response adapter object.
     */
    private ResponseAdapter adaptedResponse = context.mock(ResponseAdapter.class);
    
    /**
     * Mocked request.
     */
    private Request request = context.mock(Request.class);

    /**
     * Mocked job mapper.
     */
    private JobMapper jobMapper = context.mock(JobMapper.class);

    /**
     * Mocked person mapper.
     */
    private PersonMapper personMapper = context.mock(PersonMapper.class);

    
    /**
     * User's UUID.
     */
    private final String uuid = UUID.randomUUID().toString();
    
    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        final Map<String, Object> attributes = new HashMap<String, Object>();
        attributes.put("shortName", (Object) "job overview");
        attributes.put("uuid", uuid);

        context.checking(new Expectations()
        {
            {
                allowing(request).getAttributes();
                will(returnValue(attributes));
            }
        });

        sut = new JobsCollectionResource();
        sut.setJobMapper(jobMapper);
        sut.setPersonMapper(personMapper);
        sut.initParams(request);
        sut.init(restContext, request, response);
        sut.setAdaptedResponse(adaptedResponse);
    }

    /**
     * 
     * @throws ParseException not expected.
     */
    @Test 
    public void testDateFormatting() throws ParseException
    {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date input = formatter.parse("2009-01-01");
        
        String y2 = sut.getYear(input);
        String m2 = sut.getMonth(input);
        
        Assert.assertEquals("2009", y2);
        Assert.assertEquals("01", m2);
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
     *             
     */
    @Test
    public void represent() throws ResourceException, IOException, ParseException
    {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        final Variant variant = context.mock(Variant.class);

        final Job job = context.mock(Job.class);
        final List<Job> jobs = new ArrayList<Job>();
        jobs.add(job);

        final long jobId = 2L;
        final String companyName = "company name";
        final String industry = "industry";
        final String title = "engineer";
        final Date dateFrom = df.parse("01/01/2008");
        final Date dateTo = df.parse("01/01/2009");
        final String description = "this that and the other thing";

        context.checking(new Expectations()
        {
            {
                oneOf(jobMapper).findPersonJobsByOpenSocialId(with(any(String.class)));
                will(returnValue(jobs));

                oneOf(job).getId();
                will(returnValue(jobId));

                oneOf(job).getCompanyName();
                will(returnValue(companyName));

                oneOf(job).getIndustry();
                will(returnValue(industry));

                oneOf(job).getTitle();
                will(returnValue(title));

                oneOf(job).getDateFrom();
                will(returnValue(dateFrom));

                oneOf(job).getDateTo();
                will(returnValue(dateTo));

                oneOf(job).getDescription();
                will(returnValue(description));
            }
        });

        Representation actual = sut.represent(variant);

        assertEquals("MediaType doesn't match", MediaType.APPLICATION_JSON, actual.getMediaType());

        JSONObject json = JSONObject.fromObject(actual.getText());

        assertEquals("Got wrong number of jobs", 
                    jobs.size(),
                    json.getJSONArray(JobsCollectionResource.JOBS_KEY).size());

        // Check the first job returned
        JSONArray jsonJobs = json.getJSONArray(JobsCollectionResource.JOBS_KEY);
        JSONObject firstJob = JSONObject.fromObject(jsonJobs.getJSONObject(0));
        String actualCompanyName = (String) firstJob.get(JobsCollectionResource.COMPANY_NAME_KEY);
        assertTrue("Got a wrong job", actualCompanyName.equals(companyName));

        context.assertIsSatisfied();
    }

    /**
     * Helper method for assembling the test json string.
     * @param presentJob - toggle to say whether the test is testing a present job or not.
     * @return String json representation of a job.
     */
    private String buildJsonString(final boolean presentJob)
    {
        final Long jobId = 0L;
        final String companyName = "company name";
        final String industry = "industry";
        final String title = "engineer";
        final String description = "this that and the other thing";
        
        JSONObject jsonJob = new JSONObject();
        jsonJob.put(JobsResource.JOB_ID_KEY, jobId);
        jsonJob.put(JobsResource.COMPANY_NAME_KEY, companyName);
        jsonJob.put(JobsResource.INDUSTRY_KEY, industry);
        jsonJob.put(JobsResource.TITLE_KEY, title);
        jsonJob.put(JobsResource.DATE_FROM_MONTH_KEY, "01");
        jsonJob.put(JobsResource.DATE_FROM_YEAR_KEY, "2008");
        jsonJob.put(JobsResource.DATE_TO_MONTH_KEY, presentJob ? "" : "03");
        jsonJob.put(JobsResource.DATE_TO_YEAR_KEY, presentJob ? "" : "2009");
        jsonJob.put(JobsResource.DESCRIPTION_KEY, description);
        
        return jsonJob.toString();
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
        final Person person = context.mock(Person.class);

        final Representation entity = context.mock(Representation.class);

        context.checking(new Expectations()
        {
            {
                oneOf(personMapper).findByOpenSocialId(with(any(String.class)));
                will(returnValue(person));

                oneOf(entity).getText();
                will(returnValue(buildJsonString(false)));

                oneOf(jobMapper).insert(with(any(Job.class)));
                
                oneOf(adaptedResponse).setEntity(buildJsonString(false), 
                        MediaType.APPLICATION_JSON);
                
                oneOf(jobMapper).flush(uuid);
            }
        });

        sut.acceptRepresentation(entity);

        context.assertIsSatisfied();
    }

    /**
     * Test the POST functionality when the client has sent no "To" date, 
     * indicating that this is the presently held job. 
     * 
     * @throws ResourceException
     *             not expected
     * @throws IOException
     *             not expected
     * @throws ParseException
     *             not expected
     */
    @Test
    public void acceptRepresentationForPresentJob() throws ResourceException, IOException, ParseException
    {
        final Person person = context.mock(Person.class);

        final Representation entity = context.mock(Representation.class);

        context.checking(new Expectations()
        {
            {
                oneOf(personMapper).findByOpenSocialId(with(any(String.class)));
                will(returnValue(person));

                oneOf(entity).getText();
                will(returnValue(buildJsonString(true)));

                oneOf(jobMapper).insert(with(any(Job.class)));

                oneOf(adaptedResponse).setEntity(buildJsonString(true), 
                        MediaType.APPLICATION_JSON);
                
                oneOf(jobMapper).flush(uuid);
            }
        });

        sut.acceptRepresentation(entity);

        context.assertIsSatisfied();
    }
}
