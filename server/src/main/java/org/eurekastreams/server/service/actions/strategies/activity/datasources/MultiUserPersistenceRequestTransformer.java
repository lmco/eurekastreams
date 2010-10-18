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
package org.eurekastreams.server.service.actions.strategies.activity.datasources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Transforms JSON request to a request for multiple people buy their IDs.
 */
public class MultiUserPersistenceRequestTransformer implements PersistenceDataSourceRequestTransformer
{
    /**
     * Mapper to get a list of people ids by a list of account ids.
     */
    private DomainMapper<List<String>, List<Long>> getPeopleIdsByAccountIdsMapper;

    /**
     * The request key.
     */
    private String reqKey;

    /**
     * Default constructor.
     * 
     * @param inGetPeopleIdsByAccountIdsMapper
     *            mapper to get a person id by account id
     * @param inReqKey
     *            the relevant request key.
     */
    public MultiUserPersistenceRequestTransformer(
            final DomainMapper<List<String>, List<Long>> inGetPeopleIdsByAccountIdsMapper, final String inReqKey)
    {
        getPeopleIdsByAccountIdsMapper = inGetPeopleIdsByAccountIdsMapper;
        reqKey = inReqKey;
    }

    /**
     * Transforms JSON request to a request for multiple people buy their IDs.
     * 
     * @param request
     *            the JSON request.
     * @param userEntityId
     *            the user entity ID.
     * @return the request for the mapper.
     */
    public Serializable transform(final JSONObject request, final Long userEntityId)
    {
        JSONArray entities = request.getJSONArray(reqKey);

        List<String> accountIds = new ArrayList<String>();
        for (int i = 0; i < entities.size(); i++)
        {
            JSONObject req = entities.getJSONObject(i);
            EntityType type = EntityType.valueOf(req.getString("type"));

            switch (type)
            {
            case PERSON:
                accountIds.add(req.getString("name"));
                break;
            default:
                throw new IllegalArgumentException("Unhandled type.");
            }
        }

        return new ArrayList<Long>(getPeopleIdsByAccountIdsMapper.execute(accountIds));
    }
}
