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
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Transformers a list of recipient short names into entity stream IDs.
 */
public class RecipientPersistenceRequestTransformer implements PersistenceDataSourceRequestTransformer
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
    public RecipientPersistenceRequestTransformer(final GetPeopleByAccountIds inPersonMapper,
            final GetDomainGroupsByShortNames inGroupMapper)
    {
        personMapper = inPersonMapper;
        groupMapper = inGroupMapper;
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
            default:
                throw new RuntimeException("Unhandled type.");
            }
        }

        final List<PersonModelView> people = personMapper.execute(personIds);
        final List<DomainGroupModelView> groups = groupMapper.execute(groupIds);

        final ArrayList<Long> streamScopeIds = new ArrayList<Long>();
        
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
