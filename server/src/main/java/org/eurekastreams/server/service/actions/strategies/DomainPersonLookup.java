/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.actions.strategies;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.PersonMapper;

/**
 * Lookup people in the database, returning People entities without parent Organizations.
 */
public class DomainPersonLookup implements PersonLookupStrategy
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(DomainPersonLookup.class);

    /**
     * The mapper to use.
     */
    private PersonMapper mapper;

    /**
     * Constructor.
     * 
     * @param inMapper
     *            the mapper to use.
     */
    public DomainPersonLookup(final PersonMapper inMapper)
    {
        mapper = inMapper;
    }

    /**
     * Finds a list of people.
     * 
     * @param searchString
     *            the string to search for.
     * 
     * @param resultsLimit
     *            limit the number of results. TODO Doesn't work yet.
     * 
     * @return a list of people.
     */
    public List<Person> findPeople(final String searchString, final int resultsLimit)
    {
        String prefix = searchString;
        log.debug("DomainPersonLookup looking up " + searchString);

        List<Person> people = mapper.findPeopleByPrefix(prefix);
        // NOTE: this mapper is not responsible for loading parent organization. If the caller wants it, let them use
        // the PopulatePeopleWithSkeletonParentOrgsCacheMapper
        return people;

    }

}
