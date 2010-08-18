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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;

/**
 * Transformers a list of recipient short names into entity stream IDs.
 */
public class RecipientPersistenceRequestTransformer implements PersistenceDataSourceRequestTransformer
{
    /**
     * Steam ID mapper.
     */
    private DomainMapper<Map<Long, EntityType>, List<Long>> streamIdMapper;

    /**
     * Person mapper for getting entity ID from short name.
     */
    private GetPeopleByAccountIds personMapper;

    /**
     * Group mapper for getting entity ID from short name.
     */
    private GetDomainGroupsByShortNames groupMapper;

    /**
     * Org mapper for getting entity ID from short name.
     */
    private GetOrganizationsByShortNames orgMapper;

    /**
     * Constructor.
     * 
     * @param inPersonMapper
     *            the person mapper.
     * @param inGroupMapper
     *            the group mapper.
     * @param inOrgMapper
     *            the org mapper.
     * @param inStreamIdMapper
     *            the stream mapper.
     */
    public RecipientPersistenceRequestTransformer(final GetPeopleByAccountIds inPersonMapper,
            final GetDomainGroupsByShortNames inGroupMapper, final GetOrganizationsByShortNames inOrgMapper,
            final DomainMapper<Map<Long, EntityType>, List<Long>> inStreamIdMapper)
    {
        personMapper = inPersonMapper;
        groupMapper = inGroupMapper;
        orgMapper = inOrgMapper;
        streamIdMapper = inStreamIdMapper;
    }

    /**
     * Transform the request into a list of entity stream IDs.
     * 
     * @param request
     *            the request.
     * @param userEntityId
     *            the user entity ID.
     * @return a list of entity stream IDs.
     */
    public ArrayList<Long> transform(final JSONObject request, final Long userEntityId)
    {
        JSONArray recipients = request.getJSONArray("recipient");

        Map<Long, EntityType> mapperRequest = new HashMap<Long, EntityType>();

        for (int i = 0; i < recipients.size(); i++)
        {
            JSONObject req = recipients.getJSONObject(i);
            EntityType type = EntityType.valueOf(req.getString("type"));

            switch (type)
            {
            case PERSON:
                mapperRequest.put(personMapper.fetchId(req.getString("name")), type);
                break;
            case GROUP:
                mapperRequest.put(groupMapper.fetchId(req.getString("name")), type);
                break;
            case ORGANIZATION:
                mapperRequest.put(orgMapper.fetchId(req.getString("name")), type);
                break;
            default:
                throw new RuntimeException("Unhandled type.");
            }
        }

        return (ArrayList<Long>) streamIdMapper.execute(mapperRequest);
    }
}
