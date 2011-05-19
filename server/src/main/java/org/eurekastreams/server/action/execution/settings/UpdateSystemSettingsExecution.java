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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.UpdateMapper;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.eurekastreams.server.service.actions.strategies.UpdaterStrategy;

/**
 * Updates the system settings.
 */
public class UpdateSystemSettingsExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * the update mapper.
     * 
     */
    private UpdateMapper<SystemSettings> updateMapper;

    /**
     * the finder mapper.
     * 
     */
    private DomainMapper<MapperRequest, SystemSettings> finder;

    /**
     * the strategy used to set the resource's properties.
     */
    private UpdaterStrategy updater;

    /**
     * Mapper to set the system administrators by account ids.
     */
    private DomainMapper<List<String>, Boolean> setSystemAdministratorsMapper;

    /**
     * Mapper to get the system administrator ids.
     */
    private DomainMapper<Serializable, List<Long>> getSystemAdministratorIdsMapper;

    /**
     * Mapper to get person ids by account ids.
     */
    private DomainMapper<List<String>, List<Long>> peopleIdsByAccountIdsMapper;

    /**
     * The cache.
     */
    private Cache cache;

    /**
     * Constructor.
     * 
     * @param inFinder
     *            mapper that finds the system settings.
     * @param inUpdater
     *            The UpdaterStrategy.
     * @param inupdateMapper
     *            The update mapper.
     * @param inSetSystemAdministratorsMapper
     *            mapper to set the system administrators by account ids
     * @param inGetSystemAdministratorIdsMapper
     *            mapper to get the system administrator person ids
     * @param inPeopleIdsByAccountIdsMapper
     *            mapper to get people ids by account ids
     * @param inCache
     *            the cache
     */
    public UpdateSystemSettingsExecution(final DomainMapper<MapperRequest, SystemSettings> inFinder,
            final UpdaterStrategy inUpdater, final UpdateMapper<SystemSettings> inupdateMapper,
            final DomainMapper<List<String>, Boolean> inSetSystemAdministratorsMapper,
            final DomainMapper<Serializable, List<Long>> inGetSystemAdministratorIdsMapper,
            final DomainMapper<List<String>, List<Long>> inPeopleIdsByAccountIdsMapper, final Cache inCache)
    {
        finder = inFinder;
        updater = inUpdater;
        updateMapper = inupdateMapper;
        setSystemAdministratorsMapper = inSetSystemAdministratorsMapper;
        getSystemAdministratorIdsMapper = inGetSystemAdministratorIdsMapper;
        peopleIdsByAccountIdsMapper = inPeopleIdsByAccountIdsMapper;
        cache = inCache;
    }

    /**
     * This method updates the system settings.
     * 
     * @param inActionContext
     *            the action context
     * @return the system settings.
     */
    @SuppressWarnings("unchecked")
    @Override
    public SystemSettings execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
    {
        log.info("updating system settings");

        Set<String> cacheKeysToClear = new HashSet<String>();

        // convert the params to a map
        Map<String, Serializable> fields = (Map<String, Serializable>) inActionContext.getActionContext().getParams();

        HashSet<Person> admins = (HashSet<Person>) fields.get("admins");

        // Even though the membership criteria (a.k.a. ldapGroups form field) are passed in, they don't need
        // to be processed here since they are persisted to the database immediately when added in the UI.
        fields.remove("ldapGroups");
        fields.remove("admins");
        SystemSettings systemSettings = finder.execute(null);

        // set the properties on the system settings
        updater.setProperties(systemSettings, fields);

        // persist the settings
        updateMapper.execute(null);

        // get the existing system admins
        for (Long personId : getSystemAdministratorIdsMapper.execute(null))
        {
            cacheKeysToClear.add(CacheKeys.PERSON_BY_ID + personId);

            log.debug("Deleting existing system administrator with person id " + personId
                    + " from cache before updating admin settings");
            cache.delete(CacheKeys.PERSON_BY_ID + personId);
        }

        // get a list of the account ids of the administrators.
        ArrayList<String> adminAccountIds = new ArrayList<String>();
        for (Person person : admins)
        {
            adminAccountIds.add(person.getAccountId());
        }

        // ask the system for the ids of these users
        List<Long> peopleIds = peopleIdsByAccountIdsMapper.execute(adminAccountIds);

        // set the system admins
        log.info("Setting system administrators to " + adminAccountIds);
        setSystemAdministratorsMapper.execute(adminAccountIds);

        // clear the cache for the new system admins
        for (Long personId : peopleIds)
        {
            log.debug("Deleting new system administrator with person id " + personId
                    + " from cache before updating admin settings");
            cacheKeysToClear.add(CacheKeys.PERSON_BY_ID + personId);
            cache.delete(CacheKeys.PERSON_BY_ID + personId);
        }

        log.debug("Deleting the system administrator ids from cache, now that they've changed.");
        cache.delete(CacheKeys.SYSTEM_ADMINISTRATOR_IDS);
        cacheKeysToClear.add(CacheKeys.SYSTEM_ADMINISTRATOR_IDS);

        // put the cache keys deletes on the task queue to avoid the (yeah, i know... tiny) race condition
        inActionContext.getUserActionRequests().add(
                new UserActionRequest("deleteCacheKeysAction", null, (Serializable) cacheKeysToClear));

        return systemSettings;
    }
}
