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

import org.eurekastreams.server.domain.Person;

/**
 * Looks up employees by a search string.
 */
public interface PersonLookupStrategy
{
    /**
     * Finds a list of people by a search string.
     *
     * @param searchString
     *            the string to search for.
     * @param resultsUpperBound
     *            limit the number of results, 0 for unlimited.
     * @return a list of people.
     */
    List<Person> findPeople(String searchString, int resultsUpperBound);
}
