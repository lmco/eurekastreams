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

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.action.request.profile.UpdateOrganizationNameRequest;
import org.eurekastreams.server.persistence.UpdateCachedOrganizationName;

/**
 * Action to update an renamed organization in the cache.
 */
public class UpdateCachedOrganizationNameExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Mapper to perform cache update.
     */
    private UpdateCachedOrganizationName mapper;

    /**
     * Constructor.
     *
     * @param inMapper
     *            the post mapper.
     */
    public UpdateCachedOrganizationNameExecution(final UpdateCachedOrganizationName inMapper)
    {
        mapper = inMapper;
    }

    /**
     * Updates the organization name.
     *
     * @param inActionContext
     *            {@link ActionContext}.
     * @return true if successful.
     */
    @Override
    public Boolean execute(final ActionContext inActionContext)
    {
        UpdateOrganizationNameRequest request = (UpdateOrganizationNameRequest) inActionContext.getParams();
        mapper.execute(request.getOrganizationId(), request.getNewOrganizationName());
        return Boolean.TRUE;
    }

}
