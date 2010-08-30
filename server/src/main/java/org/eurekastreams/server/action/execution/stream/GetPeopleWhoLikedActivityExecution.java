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
package org.eurekastreams.server.action.execution.stream;

import java.util.ArrayList;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.persistence.mappers.chained.DecoratedPartialResponseDomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByIds;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Get all the people who like a particular activity.
 *
 */
public class GetPeopleWhoLikedActivityExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * The mapper.
     */
    private DecoratedPartialResponseDomainMapper<Long, ArrayList<Long>> mapper;

    /**
     * People mapper.
     */
    private GetPeopleByIds peopleMapper;

    /**
     * Default constructor.
     * @param inMapper the mapper.
     * @param inPeopleMapper the people mapper.
     */
    public GetPeopleWhoLikedActivityExecution(
            final DecoratedPartialResponseDomainMapper<Long, ArrayList<Long>> inMapper,
            final GetPeopleByIds inPeopleMapper)
    {
        mapper = inMapper;
        peopleMapper = inPeopleMapper;
    }

    /**
     * Just execute the mapper, it does all the heavy lifting for this action.
     * @param inActionContext the action context.
     * @return the list of people
     * @throws ExecutionException the exception.
     */
    @Override
    public ArrayList<PersonModelView> execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        return new ArrayList<PersonModelView>(peopleMapper.execute(mapper.execute((Long) inActionContext.getParams())));
    }

}
