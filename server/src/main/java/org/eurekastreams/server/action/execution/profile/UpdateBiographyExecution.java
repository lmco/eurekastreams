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
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;

/**
 * Execution for updating biography information.
 * 
 */
public class UpdateBiographyExecution implements ExecutionStrategy<ServiceActionContext>
{
    /**
     * Local logger instance.
     */
    private final Log log = LogFactory.make();

    /**
     * Person Mapper to lookup current user.
     */
    private final PersonMapper personMapper;
    
    /**
     * @param inPersonMapper
     *            person mapper to use.
     */
    public UpdateBiographyExecution(final PersonMapper inPersonMapper)
    {
        personMapper = inPersonMapper;
    }

    /**
     * Returns the biography back to user if save completes.
     * 
     * @param inActionContext
     *            The action context.
     * @return the biography it received.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Serializable execute(final ServiceActionContext inActionContext)
    {
        HashMap<String, Serializable> formdata = (HashMap<String, Serializable>) inActionContext.getParams();
        String biography = (String) formdata.get("biography");
        Person currentPerson = personMapper.findByAccountId(inActionContext.getPrincipal().getAccountId());
        currentPerson.setBiography(biography);
        return biography;
    }

}

