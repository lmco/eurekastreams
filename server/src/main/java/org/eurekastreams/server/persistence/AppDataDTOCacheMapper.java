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
package org.eurekastreams.server.persistence;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.AppData;
import org.eurekastreams.server.domain.dto.AppDataDTO;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Mapper to get AppDataDTO objects from cache/database.
 */
public class AppDataDTOCacheMapper extends CachedDomainMapper
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Database app data mapper.
     */
    private AppDataMapper dbMapper;

    /**
     * Constructor.
     * 
     * @param inDbMapper
     *            the database mapper
     */
    public AppDataDTOCacheMapper(final AppDataMapper inDbMapper)
    {
        dbMapper = inDbMapper;
    }

    /**
     * Get the app data dto from cache/database.
     * 
     * @param gadgetDefinitionId
     *            the gadget definition id
     * @param openSocialId
     *            the open social id
     * @return the app data dto found or created
     */
    public AppDataDTO findOrCreateByPersonAndGadgetDefinitionIds(final long gadgetDefinitionId,
            final String openSocialId)
    {
        // Look in cache first
        String key = CacheKeys.APPDATA_BY_GADGET_DEFINITION_ID_AND_UNDERSCORE_AND_PERSON_OPEN_SOCIAL_ID
                + gadgetDefinitionId + "_" + openSocialId;

        String logText = "GadgetDef#" + gadgetDefinitionId + ", open social id: " + openSocialId;
        log.info("Looking for AppData for " + logText + " in cache");
        AppDataDTO appDataDTO = (AppDataDTO) getCache().get(key);
        if (appDataDTO == null)
        {
            log.info("Didn't find AppData for " + logText + " in cache - loading it from the database");
            AppData appData = dbMapper.findOrCreateByPersonAndGadgetDefinitionIds(gadgetDefinitionId, openSocialId);

            // create the DTO
            appDataDTO = new AppDataDTO(openSocialId, gadgetDefinitionId, appData.getValues());

            log.info("Found AppData for " + logText + " in database - storing in cache");

            // store the DTO in cache
            getCache().set(key, appDataDTO);
        }
        return appDataDTO;
    }
}
