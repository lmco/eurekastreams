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

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Job;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Status;
import org.restlet.resource.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.StringRepresentation;
import org.restlet.resource.Variant;

/**
 * REST endpoint for an job entry.
 */
public class JobsEntryResource extends JobsResource
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(JobsEntryResource.class);

    /**
     * The id of the job that is being posted.
     */
    private Long jobId;

    /**
     * The UUID.
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
        jobId = Long.valueOf((String) request.getAttributes().get("jobId"));
        uuid = (String) request.getAttributes().get("uuid");
    }

    /**
     * Handles GET request.
     * 
     * @param variant
     *            the variant whose representation must be returned
     * @return representation of the job description
     * @throws ResourceException
     *             on error
     */
    @Override
    public Representation represent(final Variant variant) throws ResourceException
    {
        Job job = getJobMapper().findById(jobId);

        JSONObject jsonJobObject = convertJobToJSON(job);

        log.debug("JobsEntryResource: json =   " + jsonJobObject.toString());

        Representation rep = new StringRepresentation(jsonJobObject.toString(), MediaType.APPLICATION_JSON);
        rep.setExpirationDate(new Date(0L));
        return rep;
    }

    /**
     * Handle PUT requests.
     * 
     * @param entity
     *            the resource's new representation
     * @throws ResourceException
     *             hopefully not
     */
    @Override
    public void storeRepresentation(final Representation entity) throws ResourceException
    {
        try
        {
            Job job = getJobMapper().findById(jobId);
            if (null == job)
            {
                throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
            }

            JSONObject jsonObject = JSONObject.fromObject(entity.getText());

            validateJobHistory(jsonObject);

            Job inJob = convertJSONObjectToJob(jsonObject, job.getOwner());

            job.setCompanyName(inJob.getCompanyName());
            job.setIndustry(inJob.getIndustry());
            job.setTitle(inJob.getTitle());
            job.setDateFrom(inJob.getDateFrom());
            job.setDateTo(inJob.getDateTo());
            job.setDescription(inJob.getDescription());

            getJobMapper().flush(uuid);

            getAdaptedResponse().setEntity(convertJobToJSON(job).toString(), MediaType.APPLICATION_JSON);
        }
        catch (IOException ioe)
        {
            log.error("Unable to find job for update" + jobId, ioe);
        }
        catch (ParseException pe)
        {
            log.error("Unable to parse date for job update " + jobId, pe);
        }
    }

    /**
     * Handle DELETE requests.
     * 
     */
    @Override
    public void removeRepresentations()
    {
        getJobMapper().delete(jobId);
        getJobMapper().flush(uuid);
    }
}
