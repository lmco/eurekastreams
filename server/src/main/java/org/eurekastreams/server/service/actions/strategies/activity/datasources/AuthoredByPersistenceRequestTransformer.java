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

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;

/**
 * Transforms an authored by request.
 */
public class AuthoredByPersistenceRequestTransformer implements PersistenceDataSourceRequestTransformer
{
    /**
     * Person mapper for getting entity ID from short name.
     */
    private GetPeopleByAccountIds personMapper;

    /**
     * Group mapper for getting entity ID from short name.
     */
    private GetDomainGroupsByShortNames groupMapper;

    /**
     * Constructor.
     * 
     * @param inPersonMapper
     *            the person mapper.
     * @param inGroupMapper
     *            the group mapper.
     */
    public AuthoredByPersistenceRequestTransformer(final GetPeopleByAccountIds inPersonMapper,
            final GetDomainGroupsByShortNames inGroupMapper)
    {
        personMapper = inPersonMapper;
        groupMapper = inGroupMapper;
    }

    /**
     * Transform.
     * 
     * @param request
     *            the request.
     * @param userEntityId
     *            the user entity id.
     * @return the transformed request.
     */
    @Override
    public Serializable transform(final JSONObject request, final Long userEntityId)
    {
        JSONArray authors = request.getJSONArray("authoredBy");

        StringBuilder authorsRequest = new StringBuilder();

        for (int i = 0; i < authors.size(); i++)
        {
            if (i > 0)
            {
                authorsRequest.append(" ");
            }

            JSONObject author = authors.getJSONObject(i);
            EntityType type = EntityType.valueOf(author.getString("type"));

            switch (type)
            {
            case PERSON:
                authorsRequest.append("p");
                authorsRequest.append(personMapper.fetchId(author.getString("name")));
                break;
            case GROUP:
                authorsRequest.append("g");
                authorsRequest.append(groupMapper.fetchId(author.getString("name")));
                break;
            default:
                throw new RuntimeException("Unhandled type.");
            }
        }

        return authorsRequest.toString();
    }
}
