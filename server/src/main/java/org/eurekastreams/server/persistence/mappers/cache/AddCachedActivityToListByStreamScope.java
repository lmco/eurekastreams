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
import org.eurekastreams.server.persistence.mappers.requests.AddCachedActivityToListByStreamScopeRequest;
import org.eurekastreams.server.persistence.mappers.requests.AddCachedActivityToListRequest;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;

/**
 * Mapper that will add activities to a list based on a stream scope.
 *
 */
public class AddCachedActivityToListByStreamScope
{
    /**
     * Local instance of the AddCachedActivityToList mapper configured for 
     * adding activities from a person to a list.
     */
    private final AddCachedActivityToList addCachedPersonActivityMapper;
    
    /**
     * Local instance of the AddCachedActivityToList mapper configured for 
     * adding activities from a group to a list.
     */
    private final AddCachedActivityToList addCachedGroupActivityMapper;
    
    /**
     * Local instance of the GetPeopleByAccountIds mapper which uses cache
     * to retrieve information about people based on their account ids.
     */
    private final GetPeopleByAccountIds peopleMapper;
    
    /**
     * Local instance of the GetDomainGroupsByShortNames mapper which uses cache
     * to retrieve information about groups based on their shortnames. 
     */
    private final GetDomainGroupsByShortNames groupMapper;
    
    /**
     * Constructor.
     * @param inAddCachedPersonActivityMapper - person configured AddCachedActivityToList mapper.
     * @param inAddCachedGroupActivityMapper - group configured AddCachedActivityToList mapper.
     * @param inPeopleMapper - GetPeopleByAccountIds mapper.
     * @param inGroupMapper - GetDomainGroupsByShortNames mapper.
     */
    public AddCachedActivityToListByStreamScope(
            final AddCachedActivityToList inAddCachedPersonActivityMapper,
            final AddCachedActivityToList inAddCachedGroupActivityMapper,
            final GetPeopleByAccountIds inPeopleMapper,
            final GetDomainGroupsByShortNames inGroupMapper)
    {
        addCachedPersonActivityMapper = inAddCachedPersonActivityMapper;
        addCachedGroupActivityMapper = inAddCachedGroupActivityMapper;
        peopleMapper = inPeopleMapper;
        groupMapper = inGroupMapper;
        
    }
    
    /**
     * This method Finds the appropriate activities under the correct Group or Person context and
     * adds to the list.
     * @param inRequest - request object containing the parameters for mapping.
     * @return - list of activity ids after adding new activities.
     */
    public List<Long> execute(final AddCachedActivityToListByStreamScopeRequest inRequest)
    {
        AddCachedActivityToListRequest request;

        if (inRequest.getStreamScope().getScopeType().equals(ScopeType.PERSON))
        {
            Long scopeOwnerId = peopleMapper.fetchId(inRequest.getStreamScope().getUniqueKey());
            request = new AddCachedActivityToListRequest(
                    inRequest.getListId(), inRequest.getListOwnerId(), scopeOwnerId);
            return addCachedPersonActivityMapper.execute(request);
        }
        else if (inRequest.getStreamScope().getScopeType().equals(ScopeType.GROUP))
        {
            Long scopeOwnerId = groupMapper.fetchId(inRequest.getStreamScope().getUniqueKey());
            request = new AddCachedActivityToListRequest(
                    inRequest.getListId(), inRequest.getListOwnerId(), scopeOwnerId);
            return addCachedGroupActivityMapper.execute(request);
        }
        else
        {
            throw new UnsupportedOperationException(
                    "This mapper only supports adding activities to a List by ScopeType of PERSON or GROUP");
        }
    }
}
