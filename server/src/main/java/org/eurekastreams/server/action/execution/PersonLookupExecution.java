/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.PersonLookupRequest;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Person lookup execution strategy.
 */
public class PersonLookupExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Strategy to use for lookup.
     */
    private final PersonLookupUtilityStrategy lookupStrategy;

    /**
     * Constructor.
     *
     * @param inLookupStrategy
     *            lookup strategy.
     */
    public PersonLookupExecution(final PersonLookupUtilityStrategy inLookupStrategy)
    {
        lookupStrategy = inLookupStrategy;
    }

    @Override
    public Serializable execute(final ActionContext inActionContext) throws ExecutionException
    {
        // get the people
        PersonLookupRequest params = (PersonLookupRequest) inActionContext.getParams();

        // convert to DTOs
        ArrayList<PersonModelView> people = new ArrayList<PersonModelView>();
        for (Person person : lookupStrategy.getPeople(params.getQueryString(), params.getMaxResults()))
        {
            people.add(person.toPersonModelView());
        }
        return people;
    }

}
