/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.action.execution.profile;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.validation.profile.PersistEmploymentValidation;
import org.eurekastreams.server.domain.Job;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.IndexEntity;
import org.eurekastreams.server.persistence.mappers.InsertMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;

/**
 * Execution for persisting education information.
 * 
 */
public class PersistEmploymentExecution implements ExecutionStrategy<ServiceActionContext>
{
    /**
     * One day in milliseconds, used to alleviate time zone issues.
     */
    private static final long DAY_IN_MILLISECONDS = 86400000L;

    /**
     * Person Mapper to lookup current user.
     */
    private final PersonMapper personMapper;

    /**
     * The mapper to get the job.
     */
    private final FindByIdMapper<Job> findMapper;

    /**
     * mapper to insert a new job.
     */
    private final InsertMapper<Job> insertMapper;

    /**
     * Mapper to reindex person in search index.
     */
    private final IndexEntity<Person> personIndexer;

    /**
     * @param inPersonMapper
     *            person mapper to use.
     * @param inFindMapper
     *            the find mapper for the jobs.
     * @param inInsertMapper
     *            the insert mapper for the jobs.
     * @param inPersonIndexer
     *            {@link IndexEntity}.
     */
    public PersistEmploymentExecution(final PersonMapper inPersonMapper, final FindByIdMapper<Job> inFindMapper,
            final InsertMapper<Job> inInsertMapper, final IndexEntity<Person> inPersonIndexer)
    {
        personMapper = inPersonMapper;
        findMapper = inFindMapper;
        insertMapper = inInsertMapper;
        personIndexer = inPersonIndexer;
    }

    /**
     * @param inActionContext
     *            the context for the action.
     * @return the job that was persisted.
     * @throws ExecutionException
     *             not expected.
     */
    public Serializable execute(final ServiceActionContext inActionContext) throws ExecutionException
    {
        Job job;

        HashMap<String, Serializable> formdata = (HashMap<String, Serializable>) inActionContext.getParams();
        DateFormat df = new SimpleDateFormat("MM/yyyy");

        Person currentPerson = personMapper.findByAccountId(inActionContext.getPrincipal().getAccountId());
        Date startDate;
        Date endDate = null;

        String dates = (String) formdata.get(PersistEmploymentValidation.DATE_KEY);
        String[] datearray = dates.split(";");

        try
        {
            startDate = new Date(df.parse(datearray[0]).getTime() + DAY_IN_MILLISECONDS);

            if (datearray.length == 2)
            {
                endDate = new Date(df.parse(datearray[1]).getTime() + DAY_IN_MILLISECONDS);
            }

        }
        catch (ParseException e)
        {
            // This should never happen since validation should caught this.
            return new ExecutionException("Validation did not caught exception: " + e);
        }
        String companyName = ((String) formdata.get(PersistEmploymentValidation.COMPANY_NAME_KEY));
        String title = ((String) formdata.get(PersistEmploymentValidation.TITLE_KEY));
        String industry = ((String) formdata.get(PersistEmploymentValidation.INDUSTRY_KEY));
        String desc = ((String) formdata.get(PersistEmploymentValidation.DESCRIPTION_KEY));

        if (formdata.containsKey(PersistEmploymentValidation.JOB_ID_KEY))
        {
            Long id = ((Long) formdata.get(PersistEmploymentValidation.JOB_ID_KEY));

            if (inActionContext.getState().containsKey("JobToUpdate"))
            {
                job = (Job) inActionContext.getState().get("JobToUpdate");
            }
            else
            {
                job = findMapper.execute(new FindByIdRequest("Job", id));
                inActionContext.getState().put("JobToUpdate", job);
            }

            job.setCompanyName(companyName);
            job.setDateFrom(startDate);
            job.setDateTo(endDate);
            job.setDescription(desc);
            job.setIndustry(industry);
            job.setTitle(title);

        }
        else
        {
            job = new Job(currentPerson, companyName, industry, title, startDate, endDate, desc);
            insertMapper.execute(new PersistenceRequest<Job>(job));
        }

        personIndexer.execute(currentPerson);

        return job;
    }

}
