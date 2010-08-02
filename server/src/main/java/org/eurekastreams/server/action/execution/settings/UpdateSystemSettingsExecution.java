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
import java.util.Map;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.UpdateMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.eurekastreams.server.service.actions.strategies.UpdaterStrategy;

/**
 * Updates the system settings.
 */
public class UpdateSystemSettingsExecution implements ExecutionStrategy<ActionContext>
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
     * Constructor.
     *
     * @param inFinder
     *            mapper that finds the system settings.
     * @param inUpdater
     *            The UpdaterStrategy.
     * @param inupdateMapper
     *            The update mapper.
     */
    public UpdateSystemSettingsExecution(final DomainMapper<MapperRequest, SystemSettings> inFinder,
            final UpdaterStrategy inUpdater, final UpdateMapper<SystemSettings> inupdateMapper)
    {
        finder = inFinder;
        updater = inUpdater;
        updateMapper = inupdateMapper;

    }

    /**
     * This method updates the system settings.
     *
     * @param inActionContext
     *            {@link ActionContext}.
     * @return {@link SystemSettings}.
     */
    @SuppressWarnings("unchecked")
    @Override
    public SystemSettings execute(final ActionContext inActionContext)
    {
        log.info("updating system settings");
        // convert the params to a map
        Map<String, Serializable> fields = (Map<String, Serializable>) inActionContext.getParams();

        // Even though the membership criteria (a.k.a. ldapGroups form field) are passed in, they don't need
        // to be processed here since they are persisted to the database immediately when added in the UI.
        fields.remove("ldapGroups");
        SystemSettings systemSettings = finder.execute(null);

        // set the properties on the system settings
        updater.setProperties(systemSettings, fields);

        // persist the settings
        updateMapper.execute(null);

        return systemSettings;
    }

}
