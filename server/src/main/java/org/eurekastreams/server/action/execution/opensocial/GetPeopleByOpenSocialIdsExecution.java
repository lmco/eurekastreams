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

import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.opensocial.GetPeopleByOpenSocialIdsRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;

/**
 * This class provides the execution strategy for retrieving a list of Person objects by OpenSocial id.
 *
 */
public class GetPeopleByOpenSocialIdsExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Local instance of the PersonMapper for retrieving People.
     */
    private final PersonMapper mapper;

    /**
     * Constructor.
     * @param inMapper - instance of {@link PersonMapper} for this execution.
     */
    public GetPeopleByOpenSocialIdsExecution(final PersonMapper inMapper)
    {
        mapper = inMapper;
    }

    /**
     * {@inheritDoc}
     *
     * Retrieve the list of {@link Person} objects based on OpenSocial id.
     */
    @Override
    public LinkedList<Person> execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        LinkedList<Person> listOfPeople = new LinkedList<Person>();
        GetPeopleByOpenSocialIdsRequest currentRequest = (GetPeopleByOpenSocialIdsRequest) inActionContext.getParams();
        List<String> openSocialIds = currentRequest.getOpenSocialIds();
        String typeofPeopleToReturn = currentRequest.getTypeOfRelationshipForPeopleReturned();
        List<Person> people = null;

        if (typeofPeopleToReturn.equals("self"))
        {
            people = mapper.findPeopleByOpenSocialIds(openSocialIds);
        }
        else if (typeofPeopleToReturn.equals("friends"))
        {
            people = mapper.findPeopleFollowedUsingFollowerOpenSocialIds(openSocialIds);
        }

        return new LinkedList<Person>(people);
    }

}
