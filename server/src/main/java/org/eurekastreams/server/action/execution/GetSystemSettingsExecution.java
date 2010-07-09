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

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.persistence.mappers.FindSystemSettings;

/**
 * Gets the system settings.
 * 
 */
public class GetSystemSettingsExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * the SystemSettings mapper.
     * 
     */
    private FindSystemSettings systemSettingsDAO;

    /**
     * Constructor.
     * 
     * @param inSystemSettingsDAO
     *            used to look up the system settings.
     */
    public GetSystemSettingsExecution(final FindSystemSettings inSystemSettingsDAO)
    {
        systemSettingsDAO = inSystemSettingsDAO;
    }

    /**
     * Return system settings.
     * 
     * @param inActionContext
     *            the {@link ActionContext}.
     * @return {@link SystemSettings}
     */
    @Override
    public SystemSettings execute(final ActionContext inActionContext)
    {
        return systemSettingsDAO.execute(null);
    }

}
