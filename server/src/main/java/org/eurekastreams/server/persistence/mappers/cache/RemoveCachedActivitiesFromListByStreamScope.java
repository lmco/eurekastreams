/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.List;

import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.requests.RemoveCachedActivitiesFromListByStreamScopeRequest;
import org.eurekastreams.server.persistence.mappers.requests.RemoveCachedActivitiesFromListRequest;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * This class removes activities from a given list based on a streamscope provided.
 *
 */
public class RemoveCachedActivitiesFromListByStreamScope extends CachedDomainMapper
{
    /**
     * Instance of the RemoveCachedActivitiesFromList mapper configured for removing 
     * activities created by a person.
     */
    private final RemoveCachedActivitiesFromList removeCachedPersonActivitiesMapper;
    
    /**
     * Instance of the RemoveCachedActivitiesFromList mapper configured for removing 
     * activities created by a group.
     */
    private final RemoveCachedActivitiesFromList removeCachedGroupActivitiesMapper;
    
    /**
     * Instance of the GetPeopleByAccountIds mapper to retrieve information about people
     * based on their account ids.
     */
    private final GetPeopleByAccountIds peopleMapper;
    
    /**
     * Instance of the GetDomainGroupsByShortNames mapper to retrieve information about groups
     * based on their shortnames.
     */
    private final GetDomainGroupsByShortNames groupMapper;
    
    /**
     * Constructor.
     * @param inRemoveCachedPersonActivitiesMapper - instance of the RemoveCachedActivitiesFromList mapper
     * configured for removing activities created by a person.
     * @param inRemoveCachedGroupActivitiesMapper - instance of the RemoveCachedActivitiesFromList mapper
     * configured for removing activities created by a group.
     * @param inPeopleMapper - instance of the GetPeopleByAccountIds mapper.
     * @param inGroupMapper - instance of the GetDomainGroupsByShortNames mapper.
     */
    public RemoveCachedActivitiesFromListByStreamScope(
            final RemoveCachedActivitiesFromList inRemoveCachedPersonActivitiesMapper,
            final RemoveCachedActivitiesFromList inRemoveCachedGroupActivitiesMapper,
            final GetPeopleByAccountIds inPeopleMapper,
            final GetDomainGroupsByShortNames inGroupMapper)
    {
        removeCachedPersonActivitiesMapper = inRemoveCachedPersonActivitiesMapper;
        removeCachedGroupActivitiesMapper = inRemoveCachedGroupActivitiesMapper;
        peopleMapper = inPeopleMapper;
        groupMapper = inGroupMapper;
        
    }
    
    /**
     * This method Finds the appropriate activities under the correct Group or Person context and
     * removes them from the list.
     * @param inRequest - request representing the context for removing activities from a list.
     * @return - List of activity ids that make up the list after the targeted activities have
     * been removed.
     */
    public List<Long> execute(final RemoveCachedActivitiesFromListByStreamScopeRequest inRequest)
    {
        RemoveCachedActivitiesFromListRequest request;

        if (inRequest.getStreamScope().getScopeType().equals(ScopeType.PERSON))
        {
            PersonModelView scopeOwner = peopleMapper.fetchUniqueResult(inRequest.getStreamScope().getUniqueKey());
            request = new RemoveCachedActivitiesFromListRequest(
                    inRequest.getListId(), inRequest.getListOwnerId(), scopeOwner.getEntityId());
            return removeCachedPersonActivitiesMapper.execute(request);
        }
        else if (inRequest.getStreamScope().getScopeType().equals(ScopeType.GROUP))
        {
            DomainGroupModelView scopeOwner = groupMapper.fetchUniqueResult(inRequest.getStreamScope().getUniqueKey());
            request = new RemoveCachedActivitiesFromListRequest(
                    inRequest.getListId(), inRequest.getListOwnerId(), scopeOwner.getEntityId());
            return removeCachedGroupActivitiesMapper.execute(request);
        }
        else
        {
            throw new UnsupportedOperationException(
                    "This mapper only supports removing activities from a List by ScopeType of PERSON or GROUP");
        }
    }
}
