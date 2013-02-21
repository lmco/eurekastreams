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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.db.UpdatePersonMapper;
import org.eurekastreams.server.persistence.mappers.requests.UpdatePersonResponse;
import org.eurekastreams.server.service.actions.strategies.CacheUpdater;

/**
 * Strategy for updating person record in the system.
 */
public class RefreshPersonExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Mapper to update person in database.
     */
    private UpdatePersonMapper personMapper;

    /**
     * Async updater for person activity caches.
     */
    private CacheUpdater personActivityCacheUpdater;

    /**
     * Constructor.
     * 
     * @param inPersonMapper
     *            mapper to update a person.
     * @param inPersonActivityCacheUpdater
     *            person activity cache updater
     */
    public RefreshPersonExecution(final UpdatePersonMapper inPersonMapper,
            final CacheUpdater inPersonActivityCacheUpdater)
    {
        personMapper = inPersonMapper;
        personActivityCacheUpdater = inPersonActivityCacheUpdater;
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

        // Queue async action to update cache if necessary
        if (response.wasUserUpdated())
        {
            log.debug("Person " + ldapPerson.getAccountId() + " was updated - updating cache");
            inActionContext.getUserActionRequests().add(
                    new UserActionRequest("cachePerson", null, response.getPersonId()));
        }

        if (response.wasDisplayNameUpdated())
        {
            log.debug("Person " + ldapPerson.getAccountId()
                    + " display name was updated - updating display name everywhere");

            inActionContext.getUserActionRequests().addAll(
                    personActivityCacheUpdater.getUpdateCacheRequests(null, response.getPersonId()));
        }

        return Boolean.TRUE;
    }
}
