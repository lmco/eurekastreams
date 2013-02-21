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

/**
 * Respose object for UpdatePerson mapper.
 */
public class UpdatePersonResponse
{
    /**
     * Person id.
     */
    private Long personId;

    /**
     * Flag indicating if the user was updated or not.
     */
    private boolean wasUserUpdated;

    /**
     * Flag indicating whether the display name was updated.
     */
    private boolean displayNameWasUpdated;

    /**
     * Constructor.
     * 
     * @param inPersonId
     *            Person id.
     * @param inWasUserUpdated
     *            flag indicating user updated status.
     * @param inDisplayNameWasUpdated
     *            flag indicating whether the person's display name was updated
     */
    public UpdatePersonResponse(final Long inPersonId, final boolean inWasUserUpdated,
            final boolean inDisplayNameWasUpdated)
    {
        personId = inPersonId;
        wasUserUpdated = inWasUserUpdated;
        displayNameWasUpdated = inDisplayNameWasUpdated;
    }

    /**
     * @return the personId
     */
    public Long getPersonId()
    {
        return personId;
    }

    /**
     * @return wasUserUpdated.
     */
    public boolean wasUserUpdated()
    {
        return wasUserUpdated;
    }

    /**
     * @return whether the display name was updated.
     */
    public boolean wasDisplayNameUpdated()
    {
        return displayNameWasUpdated;
    }
}
