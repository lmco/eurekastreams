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
package org.eurekastreams.server.persistence.mappers.db;

import org.eurekastreams.server.action.request.opensocial.GetAppDataRequest;
import org.eurekastreams.server.domain.AppData;
import org.eurekastreams.server.domain.dto.AppDataDTO;
import org.eurekastreams.server.persistence.AppDataMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Mapper to get AppDataDTO from db.
 */
public class GetAppDataDTODbMapper implements DomainMapper<GetAppDataRequest, AppDataDTO>
{
    /**
     * AppData entity mapper.
     */
    private AppDataMapper entityMapper;

    /**
     * Constructor.
     * 
     * @param inEntityMapper
     *            AppData entity mapper.
     */
    public GetAppDataDTODbMapper(final AppDataMapper inEntityMapper)
    {
        entityMapper = inEntityMapper;
    }

    /**
     * Return AppDataDTO from db for provided params.
     * 
     * @param inRequest
     *            The params for getting the AppDataDTO.
     * @return AppDataDTO from db for provided params.
     */
    @Override
    public AppDataDTO execute(final GetAppDataRequest inRequest)
    {
        long gadgetDefinitionId = inRequest.getApplicationId();
        String openSocialId = inRequest.getOpenSocialId();

        AppData appData = entityMapper.findOrCreateByPersonAndGadgetDefinitionIds(gadgetDefinitionId, openSocialId);
        return new AppDataDTO(openSocialId, gadgetDefinitionId, appData.getValues());
    }

}
