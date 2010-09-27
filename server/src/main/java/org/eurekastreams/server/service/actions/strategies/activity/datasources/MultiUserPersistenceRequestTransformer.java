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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;

/**
 * Transforms JSON request to a request for a single person.
 */
public class MultiUserPersistenceRequestTransformer implements PersistenceDataSourceRequestTransformer
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Person mapper.
     */
    private GetPeopleByAccountIds personMapper;

    /**
     * The request key.
     */
    private String reqKey;

    /**
     * Default constructor.
     * 
     * @param inPersonMapper
     *            person mapper.
     * @param inReqKey
     *            the relevant request key.
     */
    public MultiUserPersistenceRequestTransformer(final GetPeopleByAccountIds inPersonMapper, final String inReqKey)
    {
        personMapper = inPersonMapper;
        reqKey = inReqKey;
    }

    /**
     * Transforms the request.
     * 
     * @param request
     *            the JSON request.
     * @param userEntityId
     *            the user entity ID.
     * @return the request for the saved activity mapper.
     */
    public Serializable transform(final JSONObject request, final Long userEntityId)
    {
        String accountId = request.getString(reqKey);

        JSONArray entities = request.getJSONArray(reqKey);

        ArrayList<Long> peopleIds = new ArrayList<Long>();

        for (int i = 0; i < entities.size(); i++)
        {
            JSONObject req = entities.getJSONObject(i);
            EntityType type = EntityType.valueOf(req.getString("type"));

            switch (type)
            {
            case PERSON:
                peopleIds.add(personMapper.fetchId(req.getString("name")));
                break;
            default:
                throw new RuntimeException("Unhandled type.");
            }
        }

        return (ArrayList<Long>) peopleIds;
    }
}
