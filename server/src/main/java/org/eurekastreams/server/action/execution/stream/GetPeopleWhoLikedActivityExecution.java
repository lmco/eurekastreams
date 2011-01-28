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
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.chained.DecoratedPartialResponseDomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;

import edu.emory.mathcs.backport.java.util.Collections;

/**
 * Get all the people who like a particular activity.
 * 
 */
public class GetPeopleWhoLikedActivityExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * The mapper.
     */
    private DecoratedPartialResponseDomainMapper<List<Long>, List<List<Long>>> mapper;

    /**
     * People mapper.
     */
    private DomainMapper<List<Long>, List<PersonModelView>> peopleMapper;

    /**
     * Default constructor.
     * 
     * @param inMapper
     *            the mapper.
     * @param inPeopleMapper
     *            the people mapper.
     */
    public GetPeopleWhoLikedActivityExecution(
            final DecoratedPartialResponseDomainMapper<List<Long>, List<List<Long>>> inMapper,
            final DomainMapper<List<Long>, List<PersonModelView>> inPeopleMapper)
    {
        mapper = inMapper;
        peopleMapper = inPeopleMapper;
    }

    /**
     * Just execute the mapper, it does all the heavy lifting for this action.
     * 
     * @param inActionContext
     *            the action context.
     * @return the list of people
     * @throws ExecutionException
     *             the exception.
     */
    @Override
    public ArrayList<PersonModelView> execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        List<Long> activityIdList = Collections.singletonList(inActionContext.getParams());
        List<Long> peopleIdsInterested = mapper.execute(activityIdList).get(0);
        List<PersonModelView> people = peopleMapper.execute(peopleIdsInterested);
        return new ArrayList<PersonModelView>(people);
    }
}
