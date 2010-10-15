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
package org.eurekastreams.server.action.execution;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Get a bunch of person model views.
 * 
 */
public class GetBulkEntitiesExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Mapper to get a list of PersonModelViews from a list of AccountIds.
     */
    private DomainMapper<List<String>, List<PersonModelView>> getPersonModelViewsByAccountIdsMapper;

    /**
     * Group mapper.
     */
    private GetDomainGroupsByShortNames groupMapper;

    /**
     * Default Constructor.
     * 
     * @param inGetPersonModelViewsByAccountIdsMapper
     *            mapper to get a list of PersonModelViews from a list of account ids.
     * @param inGroupMapper
     *            the group mapper.
     */
    public GetBulkEntitiesExecution(
            final DomainMapper<List<String>, List<PersonModelView>> inGetPersonModelViewsByAccountIdsMapper,
            final GetDomainGroupsByShortNames inGroupMapper)
    {
        getPersonModelViewsByAccountIdsMapper = inGetPersonModelViewsByAccountIdsMapper;
        groupMapper = inGroupMapper;
    }

    /**
     * Get a list entities.
     * 
     * @param inActionContext
     *            action context.
     * @return the peoples.
     * @throws ExecutionException
     *             exception.
     */
    @Override
    public ArrayList<Serializable> execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        ArrayList<Serializable> entities = new ArrayList<Serializable>();
        List<String> personIds = new ArrayList<String>();
        List<String> groupIds = new ArrayList<String>();

        for (StreamEntityDTO entity : (List<StreamEntityDTO>) inActionContext.getParams())
        {
            if (entity.getType().equals(EntityType.PERSON))
            {
                personIds.add(entity.getUniqueIdentifier());
            }
            else
            {
                groupIds.add(entity.getUniqueIdentifier());
            }
        }

        entities.addAll(getPersonModelViewsByAccountIdsMapper.execute(personIds));
        entities.addAll(groupMapper.execute(groupIds));

        return entities;
    }

}
