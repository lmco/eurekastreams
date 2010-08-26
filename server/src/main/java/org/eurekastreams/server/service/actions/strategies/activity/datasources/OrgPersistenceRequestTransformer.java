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
package org.eurekastreams.server.service.actions.strategies.activity.datasources;

import java.io.Serializable;

import net.sf.json.JSONObject;

import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;

/**
 * Transforms an org request.
 */
public class OrgPersistenceRequestTransformer implements PersistenceDataSourceRequestTransformer
{
    /**
     * Org mapper for getting ID from short name.
     */
    private GetOrganizationsByShortNames bulkOrgMapper;

    /**
     * Constructor.
     * 
     * @param inBulkOrgMapperMapper
     *            bulk org mapper.
     */
    public OrgPersistenceRequestTransformer(final GetOrganizationsByShortNames inBulkOrgMapperMapper)
    {
        bulkOrgMapper = inBulkOrgMapperMapper;
    }

    /**
     * Transofrm the request.
     * 
     * @param request
     *            JSON request.
     * @param userEntityId
     *            user entity Id.
     * @return org ID.
     */
    public Serializable transform(final JSONObject request, final Long userEntityId)
    {
        return bulkOrgMapper.fetchId(request.getString("organization"));
    }
}
