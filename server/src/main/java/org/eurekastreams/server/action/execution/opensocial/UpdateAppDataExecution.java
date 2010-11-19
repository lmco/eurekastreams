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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.opensocial.UpdateAppDataRequest;
import org.eurekastreams.server.domain.AppData;
import org.eurekastreams.server.persistence.AppDataDTOCacheMapper;
import org.eurekastreams.server.persistence.AppDataMapper;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * Update application data.
 * 
 */
public class UpdateAppDataExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger instance.
     */
    private Log log = LogFactory.make();

    /**
     * Local instance of AppDataMapper for update procedure.
     */
    private AppDataMapper mapper;

    /**
     * Mapper to get/update the cache.
     */
    private AppDataDTOCacheMapper cacheMapper;

    /**
     * Cache.
     */
    private Cache cache;

    /**
     * Basic constructor for setting up the mapper.
     * 
     * @param inMapper
     *            - instance of the App Data mapper.
     * @param inCacheMapper
     *            cache mapper - used for updating cache
     * @param inCache
     *            the cache
     */
    public UpdateAppDataExecution(final AppDataMapper inMapper, final AppDataDTOCacheMapper inCacheMapper,
            final Cache inCache)
    {
        mapper = inMapper;
        cacheMapper = inCacheMapper;
        cache = inCache;
    }

    /**
     * This method updates the Current Application's Data values with the data that is passed in.
     * 
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     * @return null
     */
    @Override
    public Integer execute(final PrincipalActionContext inActionContext)
    {
        UpdateAppDataRequest parameters = (UpdateAppDataRequest) inActionContext.getParams();
        long applicationId = parameters.getApplicationId();
        String personId = parameters.getOpenSocialId();

        AppData outputAppData = null;
        AppData currentAppData = mapper.findOrCreateByPersonAndGadgetDefinitionIds(applicationId, personId);
        if (currentAppData != null)
        {
            HashMap<String, String> inputAppDataVals = parameters.getAppDataValues();
            if (inputAppDataVals != null)
            {
                Map<String, String> appDataVals = new HashMap<String, String>(currentAppData.getValues());
                for (Entry<String, String> currentAppDataValue : inputAppDataVals.entrySet())
                {
                    appDataVals.put(currentAppDataValue.getKey(), currentAppDataValue.getValue());
                }
                currentAppData.setValues(appDataVals);
                mapper.flush();
            }

            // delete cache
            log.info("Deleting the AppDataDTO cache for gadDef " + applicationId + ", open social id: " + personId);
            cache.delete(CacheKeys.APPDATA_BY_GADGET_DEFINITION_ID_AND_UNDERSCORE_AND_PERSON_OPEN_SOCIAL_ID
                    + applicationId + "_" + personId);

            // reget the data to put it back in cache
            cacheMapper.findOrCreateByPersonAndGadgetDefinitionIds(applicationId, personId);
        }

        return null;
    }

}
