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
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;

/**
 * Transforms a JSON request into a space separated list of author ids, prepended with 'p' for Person, 'g' for Group.
 */
public class AuthoredByPersistenceRequestTransformer implements PersistenceDataSourceRequestTransformer
{
    /**
     * Person mapper for getting entity ID from short name.
     */
    private DomainMapper<String, Long> getPersonIdByAccountIdMapper;

    /**
     * Group mapper for getting entity ID from short name.
     */
    private GetDomainGroupsByShortNames groupMapper;

    /**
     * Constructor.
     *
     * @param inGetPersonIdByAccountIdMapper
     *            the mapper to get person id by account id
     * @param inGroupMapper
     *            the group mapper.
     */
    public AuthoredByPersistenceRequestTransformer(final DomainMapper<String, Long> inGetPersonIdByAccountIdMapper,
            final GetDomainGroupsByShortNames inGroupMapper)
    {
        getPersonIdByAccountIdMapper = inGetPersonIdByAccountIdMapper;
        groupMapper = inGroupMapper;
    }

    /**
     * Transforms a JSON request into a space separated list of author ids, prepended with 'p' for Person, 'g' for
     * Group.
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
                authorsRequest.append(getPersonIdByAccountIdMapper.execute(author.getString("name")));
                break;
            case GROUP:
                authorsRequest.append("g");
                authorsRequest.append(groupMapper.fetchId(author.getString("name")));
                break;
            default:
                throw new IllegalArgumentException("Unhandled type.");
            }
        }

        return authorsRequest.toString();
    }
}
