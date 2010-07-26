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
 * Request object holding a person and stream view id.
 */
public class PersonAndStreamViewIdRequest
{
    /**
     * Constructor.
     *
     * @param inPerson
     *            person owning the stream view
     * @param inStreamViewId
     *            the id of the stream view
     */
    public PersonAndStreamViewIdRequest(final Person inPerson, final Long inStreamViewId)
    {
        person = inPerson;
        streamViewId = inStreamViewId;
    }

    /**
     * The person owning the stream view.
     */
    private Person person;

    /**
     * Id of the stream view.
     */
    private Long streamViewId;

    /**
     * Get the person.
     *
     * @return the person
     */
    public Person getPerson()
    {
        return person;
    }

    /**
     * Set the person.
     *
     * @param inPerson
     *            the person to set
     */
    public void setPerson(final Person inPerson)
    {
        person = inPerson;
    }

    /**
     * Get the ID of the stream view.
     *
     * @return the ID of the stream view.
     */
    public Long getStreamViewId()
    {
        return streamViewId;
    }

    /**
     * Set the ID of the stream view.
     *
     * @param inStreamViewId
     *            the ID of the stream view
     */
    public void setStreamViewId(final Long inStreamViewId)
    {
        streamViewId = inStreamViewId;
    }
}
