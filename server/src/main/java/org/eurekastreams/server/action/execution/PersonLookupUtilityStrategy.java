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

import java.util.List;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.service.actions.strategies.PersonLookupStrategy;

/**
 * Common strategy for person lookup.
 * 
 */
public class PersonLookupUtilityStrategy
{
    /**
     * Strategy to use for lookup.
     */
    private PersonLookupStrategy lookupStrategy;

    /**
     * Constructor.
     * 
     * @param inLookupStrategy
     *            lookup strategy.
     */
    public PersonLookupUtilityStrategy(final PersonLookupStrategy inLookupStrategy)
    {
        lookupStrategy = inLookupStrategy;
    }

    /**
     * Get people.
     * 
     * @param inQueryString
     *            Query string.
     * @param inMaxResults
     *            Max results.
     * @return List of People.
     */
    public List<Person> getPeople(final String inQueryString, final int inMaxResults)
    {
        return lookupStrategy.findPeople(inQueryString, inMaxResults);
    }

}
