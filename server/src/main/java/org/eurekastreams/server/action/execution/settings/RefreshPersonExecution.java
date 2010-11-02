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
package org.eurekastreams.server.action.execution.settings;

import java.io.Serializable;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.cache.RemovePersonFromCacheMapper;
import org.eurekastreams.server.persistence.mappers.db.UpdatePersonMapper;
import org.eurekastreams.server.persistence.mappers.requests.UpdatePersonResponse;

/**
 * Strategy for updating person record in the system.
 */
public class RefreshPersonExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * Mapper to update person in database.
     */
    private UpdatePersonMapper personMapper;
    
    /**
     * Mapper to remove a person from cache.
     */
    private RemovePersonFromCacheMapper removePersonFromCacheMapper;

    /**
     * Constructor.
     * 
     * @param inPersonMapper
     *            mapper to update a person.
     * @param inRemovePersonFromCacheMapper
     * 			  mapper to remove person from cache.
     */
    public RefreshPersonExecution(final UpdatePersonMapper inPersonMapper,
    		final RemovePersonFromCacheMapper inRemovePersonFromCacheMapper)
    {
        personMapper = inPersonMapper;
        removePersonFromCacheMapper = inRemovePersonFromCacheMapper;
    }

    /**
     * Refresh person in the system.
     * 
     * @param inActionContext
     *            The action context
     * 
     * @return true on success.
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        Person ldapPerson = (Person) inActionContext.getActionContext().getParams();
        UpdatePersonResponse response = personMapper.execute(ldapPerson);

        // Remove the person from cache if necessary
        if (response.wasUserUpdated())
        {
        	removePersonFromCacheMapper.execute(response.getPerson());
        }

        return Boolean.TRUE;
    }
}
