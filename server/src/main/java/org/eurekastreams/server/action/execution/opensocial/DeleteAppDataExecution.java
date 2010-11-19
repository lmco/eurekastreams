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
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.opensocial.DeleteAppDataRequest;
import org.eurekastreams.server.domain.AppData;
import org.eurekastreams.server.persistence.AppDataDTOCacheMapper;
import org.eurekastreams.server.persistence.AppDataMapper;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * Retrieve the Application Data for the supplied credentials.
 * 
 */
public class DeleteAppDataExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Instance of the mapper to use for this action.
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
     * Constructor for the GetAppDataExecution strategy.
     * 
     * @param inMapper
     *            - instance of the {@link AppDataMapper} for this execution strategy.
     * @param inCacheMapper
     *            mapper to get/update the cache
     * @param inCache
     *            the cache
     */
    public DeleteAppDataExecution(final AppDataMapper inMapper, final AppDataDTOCacheMapper inCacheMapper,
            final Cache inCache)
    {
        mapper = inMapper;
        cacheMapper = inCacheMapper;
        cache = inCache;
    }

    /**
     * Delete the app data values for the input user/gadget.
     * 
     * @param inActionContext
     *            the action context
     * @return null
     * @throws ExecutionException
     *             when anything goes wrong
     */
    @Override
    public Integer execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        // get the request
        DeleteAppDataRequest inRequest = (DeleteAppDataRequest) inActionContext.getParams();

        long applicationId = inRequest.getApplicationId();
        String openSocialId = inRequest.getOpenSocialId();

        Map<String, String> currentAppDataValues;

        try
        {
            AppData currentAppData = mapper.findOrCreateByPersonAndGadgetDefinitionIds(applicationId, openSocialId);
            if (currentAppData != null)
            {
                currentAppDataValues = new HashMap<String, String>(currentAppData.getValues());

                Iterator<String> appDataValueKeysIterator = inRequest.getAppDataValueKeys().iterator();

                while (appDataValueKeysIterator.hasNext())
                {
                    final String appDataValueKey = appDataValueKeysIterator.next();
                    // This is an implementation of the OpenSocial Spec 5.3.12.2.11
                    if (appDataValueKey == "*")
                    {
                        // Remove all of the AppDataValues for this AppData Instance.
                        for (Entry<String, String> entry : currentAppDataValues.entrySet())
                        {
                            mapper.deleteAppDataValueByKey(currentAppData.getId(), entry.getKey());
                        }
                        break;
                    }
                    if (currentAppDataValues.containsKey(appDataValueKey))
                    {
                        mapper.deleteAppDataValueByKey(currentAppData.getId(), appDataValueKey);
                    }

                }
                mapper.flush();

                // delete cache
                log.info("Deleting the AppDataDTO cache for gadDef " + applicationId + ", open social id: "
                        + openSocialId);
                cache.delete(CacheKeys.APPDATA_BY_GADGET_DEFINITION_ID_AND_UNDERSCORE_AND_PERSON_OPEN_SOCIAL_ID
                        + applicationId + "_" + openSocialId);

                // update the cache
                cacheMapper.findOrCreateByPersonAndGadgetDefinitionIds(applicationId, openSocialId);
            }
        }
        catch (Exception ex)
        {
            log.error("Error occurred deleting app data.", ex);
            throw new ExecutionException("Error occurred deleting app data.", ex);
        }
        return null;
    }
}
