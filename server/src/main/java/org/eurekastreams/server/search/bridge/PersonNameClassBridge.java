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
package org.eurekastreams.server.search.bridge;

import org.eurekastreams.server.domain.Person;
import org.hibernate.search.bridge.StringBridge;

/**
 * Get a searchable name from a person, combining the first, middle, preferred, and last name.
 */
public class PersonNameClassBridge implements StringBridge
{
    /**
     * Get a searchable name from a Person, combining the first, preferred, and last name.
     * 
     * @param personObj
     *            the person to get the name for
     * @return a string with the person's first, preferred, and last name
     */
    @Override
    public String objectToString(final Object personObj)
    {
        if (personObj == null || !(personObj instanceof Person))
        {
            return null;
        }
        Person person = (Person) personObj;
        return getValue(person.getFirstName()) + " " + getValue(person.getMiddleName()) + " "
                + getValue(person.getLastName()) + " " + getValue(person.getPreferredName());
    }

    /**
     * Get the value of the input string, or empty string if null.
     * 
     * @param input
     *            the input string
     * @return the input value, or empty string if null
     */
    private String getValue(final String input)
    {
        return input == null ? "" : input;
    }
}
