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
package org.eurekastreams.server.action.execution.opensocial;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.opensocial.GetAppDataRequest;
import org.eurekastreams.server.domain.dto.AppDataDTO;
import org.eurekastreams.server.persistence.AppDataDTOCacheMapper;

/**
 * Retrieve the Application Data for the supplied credentials.
 * 
 */
public class GetAppDataExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Instance of the mapper to use for this action.
     */
    private AppDataDTOCacheMapper mapper;

    /**
     * Constructor for the GetAppDataExecution strategy.
     * 
     * @param inMapper
     *            - instance of the {@link AppDataDTOCacheMapper} for this execution strategy.
     */
    public GetAppDataExecution(final AppDataDTOCacheMapper inMapper)
    {
        mapper = inMapper;
    }

    /**
     * {@inheritDoc}.
     * 
     * Retrieve the application data for the supplied application id and opensocial id.
     */
    @Override
    public AppDataDTO execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        try
        {
            GetAppDataRequest currentRequest = (GetAppDataRequest) inActionContext.getParams();
            return mapper.findOrCreateByPersonAndGadgetDefinitionIds(currentRequest.getApplicationId(), currentRequest
                    .getOpenSocialId());
        }
        catch (Exception ex)
        {
            log.error("Error occurred retrieving AppData", ex);
            throw new ExecutionException("Error occurred retrieving AppData.", ex);
        }
    }

}
