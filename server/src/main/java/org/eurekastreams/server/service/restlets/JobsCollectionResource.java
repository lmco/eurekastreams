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

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Job;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

/**
 * REST endpoint for a job collection.
 */
public class JobsCollectionResource extends JobsResource
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(JobsCollectionResource.class);

    /**
     * Mapper to retrieve a person.
     */
    private PersonMapper personMapper;

    /**
     * The account name of the person whose jobs are being requested.
     */
    private String uuid;

    /**
     * Initialize parameters from the request object. the context of the request
     * 
     * @param request
     *            the client's request
     */
    @Override
    protected void initParams(final Request request)
    {
        uuid = (String) request.getAttributes().get("uuid");
    }

    /**
     * The Person Mapper. Used by tests.
     * 
     * @param inPersonMapper
     *            mapper.
     */
    public void setPersonMapper(final PersonMapper inPersonMapper)
    {
        personMapper = inPersonMapper;
    }

    /**
     * Handles GET request.
     * 
     * @param variant
     *            the variant whose representation must be returned
     * @return representation of the jobs description
     * @throws ResourceException
     *             on error
     */
    @Override
    public Representation represent(final Variant variant) throws ResourceException
    {
        List<Job> jobs = getJobMapper().findPersonJobsByOpenSocialId(uuid);
        log.debug("JobsCollectionResource: number of jobs = " + jobs.size());

        JSONObject json = new JSONObject();

        JSONArray jsonJobs = new JSONArray();
        for (Job job : jobs)
        {
            jsonJobs.add(convertJobToJSON(job));
            log.debug("added object to array");
        }
        json.put(JOBS_KEY, jsonJobs);
        log.debug("JobsCollectionResource: json =   " + json.toString());

        Representation rep = new StringRepresentation(json.toString(), MediaType.APPLICATION_JSON);
        rep.setExpirationDate(new Date(0L));
        return rep;
    }

    /**
     * Handle POST requests.
     * 
     * @param entity
     *            the resource's new representation
     * @throws ResourceException
     *             hopefully not
     */
    @Override
    public void acceptRepresentation(final Representation entity) throws ResourceException
    {
        try
        {
            String jsonText = entity.getText();
            log.debug("POST text to JobsCollectionResource: " + jsonText);
            Person person = personMapper.findByOpenSocialId(uuid);
            JSONObject jsonObject = JSONObject.fromObject(jsonText);

            validateJobHistory(jsonObject);

            Job newJob = convertJSONObjectToJob(jsonObject, person);
            getJobMapper().insert(newJob);

            getJobMapper().flush(uuid);

            getAdaptedResponse().setEntity(convertJobToJSON(newJob).toString(), MediaType.APPLICATION_JSON);
        }
        catch (IOException ioe)
        {
            log.error("Unable to put new job for person" + uuid, ioe);
        }
        catch (ParseException pe)
        {
            log.error("Unable to parse the date of a new job for person " + uuid, pe);
        }
    }
}
