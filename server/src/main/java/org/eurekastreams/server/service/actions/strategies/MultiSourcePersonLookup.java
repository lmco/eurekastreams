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
package org.eurekastreams.server.service.actions.strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eurekastreams.server.domain.Person;

/**
 * Combines results from multiple sources.
 */
public class MultiSourcePersonLookup implements PersonLookupStrategy
{
    /**
     * Primary source to use.
     */
    PersonLookupStrategy primary;

    /**
     * Secondary source to use.
     */
    PersonLookupStrategy secondary;

    /**
     * Constructor.
     * 
     * @param inPrimary
     *            primary source.
     * @param inSecondary
     *            secondary source.
     */
    public MultiSourcePersonLookup(final PersonLookupStrategy inPrimary, final PersonLookupStrategy inSecondary)
    {
        primary = inPrimary;
        secondary = inSecondary;
    }

    /**
     * Gets a list of people.
     * 
     * @param searchString
     *            the string to search for.
     * 
     * @param searchLimit
     *            limit the number of results.
     * @return a list of people.
     */
    public List<Person> findPeople(final String searchString, final int searchLimit)
    {
        List<Person> primaryList = primary.findPeople(searchString, searchLimit);
        List<Person> secondaryList = secondary.findPeople(searchString, searchLimit);

        HashMap<String, Person> personMap = new HashMap<String, Person>();

        if (null != primaryList)
        {
            for (Person p : primaryList)
            {
                personMap.put(p.getAccountId(), p);
            }
        }

        if (null != secondaryList)
        {
            for (Person p : secondaryList)
            {
                if (!(personMap.containsKey(p.getAccountId())))
                {
                    personMap.put(p.getAccountId(), p);
                }
            }
        }

        List<Person> mergedList = new ArrayList<Person>();
        mergedList.addAll(personMap.values());

        return mergedList;

    }
}
