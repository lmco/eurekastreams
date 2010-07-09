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
import java.util.LinkedList;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.domain.Job;
import org.eurekastreams.server.persistence.JobMapper;

/**
 * Allows users to get job for a given user.
 * 
 */
public class GetEmploymentExecution implements ExecutionStrategy<ServiceActionContext>
{

    /**
     * mapper to get job.
     */
    private final JobMapper jobMapper;

    /**
     * @param inJobMapper
     *            mapper to get jobs.
     */
    public GetEmploymentExecution(final JobMapper inJobMapper)
    {
        jobMapper = inJobMapper;
    }

    /**
     * @param inActionContext
     *            the context for the action.
     * @return List of Jobs.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Serializable execute(final ServiceActionContext inActionContext)
    {
        Long idToLookup = (Long) inActionContext.getParams();
        return new LinkedList<Job>(jobMapper.findPersonJobsById(idToLookup));
    }

}
