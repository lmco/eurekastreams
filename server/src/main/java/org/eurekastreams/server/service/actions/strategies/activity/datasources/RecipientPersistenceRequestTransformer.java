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
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Transformers a list of recipient short names into entity stream IDs.
 */
public class RecipientPersistenceRequestTransformer implements PersistenceDataSourceRequestTransformer
{
    /**
     * Mapper for getting PersonModelViews from a list of account ids.
     */
    private DomainMapper<List<String>, List<PersonModelView>> getPersonModelViewsByAccountIdsMapper;

    /**
     * Group mapper for getting entity ID from short name.
     */
    private GetDomainGroupsByShortNames groupMapper;

    /**
     * Resource StreamScope id mapper.
     */
    private DomainMapper<String, Long> resourceStreamScopeIdMapper;

    /**
     * Constructor.
     * 
     * @param inGetPersonModelViewsByAccountIdsMapper
     *            Mapper for getting PersonModelViews from a list of account ids.
     * @param inGroupMapper
     *            the group mapper.
     * @param inResourceStreamScopeIdMapper
     *            Resource StreamScope id mapper.
     */
    public RecipientPersistenceRequestTransformer(
            final DomainMapper<List<String>, List<PersonModelView>> inGetPersonModelViewsByAccountIdsMapper,
            final GetDomainGroupsByShortNames inGroupMapper,
            final DomainMapper<String, Long> inResourceStreamScopeIdMapper)
    {
        getPersonModelViewsByAccountIdsMapper = inGetPersonModelViewsByAccountIdsMapper;
        groupMapper = inGroupMapper;
        resourceStreamScopeIdMapper = inResourceStreamScopeIdMapper;
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

        List<String> personIds = new ArrayList<String>();
        List<String> groupIds = new ArrayList<String>();
        String resourceKey = null;

        for (int i = 0; i < recipients.size(); i++)
        {
            JSONObject req = recipients.getJSONObject(i);
            EntityType type = EntityType.valueOf(req.getString("type"));

            switch (type)
            {
            case PERSON:
                personIds.add(req.getString("name"));
                break;
            case GROUP:
                groupIds.add(req.getString("name"));
                break;
            case RESOURCE:
                resourceKey = req.getString("name");
                break;
            default:
                throw new RuntimeException("Unhandled type.");
            }
        }

        final List<PersonModelView> people = getPersonModelViewsByAccountIdsMapper.execute(personIds);
        final List<DomainGroupModelView> groups = groupMapper.execute(groupIds);

        final ArrayList<Long> streamScopeIds = new ArrayList<Long>();

        if (resourceKey != null && !resourceKey.equals(""))
        {
            Long streamScopeId = resourceStreamScopeIdMapper.execute(resourceKey);
            if (streamScopeId != null && streamScopeId > 0)
            {
                streamScopeIds.add(streamScopeId);
            }
        }

        for (PersonModelView person : people)
        {
            streamScopeIds.add(person.getStreamId());
        }

        for (DomainGroupModelView group : groups)
        {
            streamScopeIds.add(group.getStreamId());
        }

        return streamScopeIds;
    }
}
