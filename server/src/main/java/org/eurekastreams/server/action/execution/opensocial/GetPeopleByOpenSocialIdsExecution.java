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
package org.eurekastreams.server.action.execution.opensocial;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.opensocial.GetPeopleByOpenSocialIdsRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByOpenSocialIds;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.opensocial.spi.SPIUtils;

/**
 * This class provides the execution strategy for retrieving a list of Person objects by OpenSocial id.
 *
 */
public class GetPeopleByOpenSocialIdsExecution implements ExecutionStrategy<PrincipalActionContext>
{    
    /**
     * Local instance of the mapper for retrieving a list of PersonModelView objects based on a list
     * of account ids.
     */
    private final DomainMapper<List<String>, List<PersonModelView>> getPersonModelViewsByAccountIdsMapper;
    
    /**
     * Local instance of the mapper for retrieving a list of PersonModelView objects based on a list
     * of opensocial Ids.
     */
    private final GetPeopleByOpenSocialIds getPersonModelViewsByOpenSocialIdsMapper;
    
    /**
     * Constructor.
     * @param inGetPersonModelViewsByOpenSocialIdsMapper - mapper for retrieving users by opensocial ids from cache.
     * @param inGetPersonModelViewsByAccountIdsMapper - mapper for retrieving users by account ids from cache.
     */
    public GetPeopleByOpenSocialIdsExecution(final GetPeopleByOpenSocialIds inGetPersonModelViewsByOpenSocialIdsMapper, 
            final DomainMapper<List<String>, List<PersonModelView>> inGetPersonModelViewsByAccountIdsMapper)
    {
        getPersonModelViewsByAccountIdsMapper = inGetPersonModelViewsByAccountIdsMapper;
        getPersonModelViewsByOpenSocialIdsMapper = inGetPersonModelViewsByOpenSocialIdsMapper;
    }

    /**
     * {@inheritDoc}
     *
     * Retrieve the list of {@link PersonModelView} objects based on OpenSocial id.
     */
    @Override
    public LinkedList<PersonModelView> execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        GetPeopleByOpenSocialIdsRequest currentRequest = (GetPeopleByOpenSocialIdsRequest) inActionContext.getParams();
        List<String> requestedOpenSocialIds = currentRequest.getOpenSocialIds();
        List<String> openSocialIds = new ArrayList<String>();
        List<String> accountIds = new ArrayList<String>();
        List<PersonModelView> people = new ArrayList<PersonModelView>();

        for(String currentUserId : requestedOpenSocialIds)
        {
            if(SPIUtils.isOpenSocialId(currentUserId))
            {
                openSocialIds.add(currentUserId);
            }
            else
            {
                accountIds.add(currentUserId);
            }
        }
        if(openSocialIds.size() > 0)
        {
            people.addAll(getPersonModelViewsByOpenSocialIdsMapper.execute(requestedOpenSocialIds));
        }
        
        if(accountIds.size() > 0)
        {
            people.addAll(getPersonModelViewsByAccountIdsMapper.execute(accountIds));
        }

        return new LinkedList<PersonModelView>(people);
    }

}
