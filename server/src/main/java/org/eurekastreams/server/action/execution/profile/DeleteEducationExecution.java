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

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.persistence.EnrollmentMapper;

/**
 * Delete education execution.
 *
 */
public class DeleteEducationExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Enrollment mapper.
     */
    private EnrollmentMapper mapper;

    /**
     * Default constructor.
     * @param inMapper mapper.
     */
    public DeleteEducationExecution(final EnrollmentMapper inMapper)
    {
        mapper = inMapper;
    }


    /**
     * Execute.
     * @param inActionContext context.
     * @return TRUE;
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext)
    {
        mapper.delete((Long) inActionContext.getParams());
        mapper.flush(inActionContext.getPrincipal().getOpenSocialId());
        return Boolean.TRUE;
    }
}
