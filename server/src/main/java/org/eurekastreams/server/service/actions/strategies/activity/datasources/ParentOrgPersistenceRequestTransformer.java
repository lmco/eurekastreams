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

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Parent org request transformer.
 */
public class ParentOrgPersistenceRequestTransformer implements PersistenceDataSourceRequestTransformer
{
    /**
     * Person mapper for getting a person model view from account id.
     */
    private DomainMapper<String, PersonModelView> getPersonModelViewByAccountIdMapper;

    /**
     * Constructor.
     * 
     * @param inGetPersonModelViewByAccountIdMapper
     *            Person mapper for getting a person model view from account id
     */
    public ParentOrgPersistenceRequestTransformer(
            final DomainMapper<String, PersonModelView> inGetPersonModelViewByAccountIdMapper)
    {
        getPersonModelViewByAccountIdMapper = inGetPersonModelViewByAccountIdMapper;
    }

    /**
     * Transform the request to get the parent org short name for a person by account id.
     * 
     * @param request
     *            JSON request.
     * @param userEntityId
     *            user entity Id.
     * @return org ID.
     */
    public Serializable transform(final JSONObject request, final Long userEntityId)
    {
        return getPersonModelViewByAccountIdMapper.execute(request.getString("parentOrg"))
                .getParentOrganizationShortName();
    }
}
