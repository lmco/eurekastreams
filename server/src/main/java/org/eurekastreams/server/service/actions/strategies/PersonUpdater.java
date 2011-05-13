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
package org.eurekastreams.server.service.actions.strategies;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;

/**
 * Update person resource strategy.
 */
public class PersonUpdater implements ResourcePersistenceStrategy<Person>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * The person mapper.
     */
    private PersonMapper personMapper;

    /**
     * Async updater for person activity caches.
     */
    private CacheUpdater personActivityCacheUpdater;

    /**
     * The key to use to store the original domain group name into the fields map between Get and Persist.
     */
    protected static final String ORIGINAL_DISPLAY_NAME_KEY = "__KEY_ORIGINAL_DISPLAY_NAME_KEY";

    /**
     * Key used to store original parent org between get and Persist.
     */
    public static final String ORIGINAL_PARENT_ORG_KEY = "__KEY_ORIGINAL_PARENT_ORG_KEY";

    /**
     * Constructor.
     * 
     * @param inPersonMapper
     *            person mapper.
     * @param inPersonActivityCacheUpdater
     *            person activity cache updater
     */
    public PersonUpdater(final PersonMapper inPersonMapper, final CacheUpdater inPersonActivityCacheUpdater)
    {
        personMapper = inPersonMapper;
        personActivityCacheUpdater = inPersonActivityCacheUpdater;
    }

    /**
     * Gets an existing person.
     * 
     * @param inActionContext
     *            the action context
     * @param inFields
     *            the fields.
     * @return person matching the field.
     */
    public Person get(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields)
    {
        Person p = personMapper.findByAccountId((String) inFields.get("accountId"));

        // store the original display name between get and persist to see if we need to kick off a cache update
        inFields.put(ORIGINAL_DISPLAY_NAME_KEY, p.getDisplayName());

        return p;
    }

    /**
     * Persists a person.
     * 
     * @param inActionContext
     *            the action context
     * @param inFields
     *            Map of properties.
     * @param inUpdatedPerson
     *            The person to persist.
     * @throws Exception
     *             If there is an error.
     */
    public void persist(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields, final Person inUpdatedPerson) throws Exception
    {
        // kick off the cache update if the person's display name changed
        String originalDisplayName = (String) inFields.get(ORIGINAL_DISPLAY_NAME_KEY);
        String newDisplayName = inUpdatedPerson.getDisplayName();

        if (originalDisplayName == null || !originalDisplayName.equals(newDisplayName))
        {
            if (log.isInfoEnabled())
            {
                log.info("Person with account id " + inUpdatedPerson.getAccountId() + " display name is updated from "
                        + originalDisplayName + " to " + newDisplayName);
            }

            inActionContext.getUserActionRequests().addAll(
                    personActivityCacheUpdater.getUpdateCacheRequests(
                            inActionContext.getActionContext().getPrincipal(), inUpdatedPerson.getId()));
        }

        personMapper.flush();
    }
}
