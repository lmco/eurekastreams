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
package org.eurekastreams.server.action.request;

import java.io.Serializable;

import org.eurekastreams.server.domain.Person;

/**
 * Request for create person.
 * 
 */
public class CreatePersonRequest implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = -3244032516772158019L;

    /**
     * Person to add to system.
     */
    private Person person;

    /**
     * Flag if email should be sent.
     */
    private Boolean sendEmail;

    /**
     * Constructor.
     * 
     * @param inPerson
     *            Person to add to system.
     * @param inSendEmail
     *            Flag if email should be sent.
     */
    public CreatePersonRequest(final Person inPerson, final Boolean inSendEmail)
    {
        person = inPerson;
        sendEmail = inSendEmail;
    }

    /**
     * @return the person
     */
    public Person getPerson()
    {
        return person;
    }

    /**
     * @return the sendEmail
     */
    public Boolean getSendEmail()
    {
        return sendEmail;
    }
}
