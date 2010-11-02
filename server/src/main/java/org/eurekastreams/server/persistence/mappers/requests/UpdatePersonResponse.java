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
package org.eurekastreams.server.persistence.mappers.requests;

import org.eurekastreams.server.domain.Person;

/**
 * Respose object for UpdatePerson mapper.
 */
public class UpdatePersonResponse
{
    /**
     * Updated person.
     */
    private Person person;

    /**
     * Flag indicating if the user was updated or not.
     */
    private boolean wasUserUpdated;

    /**
     * Constructor.
     * 
     * @param inPerson
     *            Person.
     * @param inWasUserUpdated
     *            flag indicating user updated status.
     */
    public UpdatePersonResponse(final Person inPerson, final boolean inWasUserUpdated)
    {
        person = inPerson;
        wasUserUpdated = inWasUserUpdated;
    }

    /**
     * @return the person
     */
    public Person getPerson()
    {
        return person;
    }

    /**
     * @return wasUserUpdated.
     */
    public boolean wasUserUpdated()
    {
        return wasUserUpdated;
    }
}

