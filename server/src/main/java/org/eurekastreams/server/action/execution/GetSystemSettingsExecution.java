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
package org.eurekastreams.server.action.execution;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Gets the system settings.
 * 
 */
public class GetSystemSettingsExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * the SystemSettings mapper.
     * 
     */
    private DomainMapper<MapperRequest, SystemSettings> systemSettingsDAO;

    /**
     * Mapper to get the system administrator ids.
     */
    private DomainMapper<Serializable, List<PersonModelView>> systemAdminsMapper;

    /**
     * Constructor.
     * 
     * @param inSystemSettingsDAO
     *            used to look up the system settings.
     * @param inSystemAdminsMapper
     *            mapper to get the system administrators
     */
    public GetSystemSettingsExecution(final DomainMapper<MapperRequest, SystemSettings> inSystemSettingsDAO,
            final DomainMapper<Serializable, List<PersonModelView>> inSystemAdminsMapper)
    {
        systemSettingsDAO = inSystemSettingsDAO;
        systemAdminsMapper = inSystemAdminsMapper;
    }

    /**
     * Return system settings.
     * 
     * @param inActionContext
     *            the ActionContext, with a boolean parameter - whether to load the admins or not
     * @return {@link SystemSettings}
     */
    @Override
    public SystemSettings execute(final ActionContext inActionContext)
    {
        SystemSettings settings = systemSettingsDAO.execute(null);
        if ((inActionContext.getParams() instanceof Boolean) && (Boolean) inActionContext.getParams())
        {
            log.debug("User wants admins - fetch them.");

            List<PersonModelView> adminsList = systemAdminsMapper.execute(null);
            log.debug("Found system admins: " + adminsList);

            Set<PersonModelView> adminsSet = new HashSet<PersonModelView>();
            adminsSet.addAll(adminsList);

            // get the people and convert it to a set, which is what the client expects
            settings.setSystemAdministrators(adminsSet);
        }
        return settings;
    }

}
