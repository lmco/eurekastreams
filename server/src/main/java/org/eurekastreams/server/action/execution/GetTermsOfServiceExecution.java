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
import org.eurekastreams.server.domain.TermsOfServiceDTO;
import org.eurekastreams.server.persistence.mappers.FindSystemSettings;

/**
 * Get the terms of service.
 */
public class GetTermsOfServiceExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * The system settings mapper.
     */
    private FindSystemSettings settingsMapper;

    /**
     * Constructor.
     * 
     * @param inSettingsMapper
     *            the settings. mapper.
     */
    public GetTermsOfServiceExecution(final FindSystemSettings inSettingsMapper)
    {
        settingsMapper = inSettingsMapper;
    }

    /**
     * Get Terms of Service.
     * 
     * @param inActionContext
     *            the {@link ActionContext}.
     * @return Terms of Service.
     */
    @Override
    public TermsOfServiceDTO execute(final ActionContext inActionContext)
    {
        // TODO: #Performance: System settings don't change much, consider caching to avoid DB
        // hit each time.
        SystemSettings settings = settingsMapper.execute(null);
        return new TermsOfServiceDTO(settings.getTermsOfService());
    }

}
