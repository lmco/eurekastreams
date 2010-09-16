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

import java.io.Serializable;

/**
 * Request for MoveOrganizationPeopleDBMapper.
 * 
 */
public class MoveOrganizationPeopleRequest implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = -4940145217023167318L;

    /**
     * Source organization key.
     */
    private String sourceOrganizationKey;

    /**
     * Destination organization key.
     */
    private String destinationOrganizationKey;

    /**
     * Constructor.
     * 
     * @param inSourceOrganizationKey
     *            Source organization key.
     * @param inDestinationOrganizationKey
     *            Destination organization key.
     */
    public MoveOrganizationPeopleRequest(final String inSourceOrganizationKey, // n\
            final String inDestinationOrganizationKey)
    {
        sourceOrganizationKey = inSourceOrganizationKey;
        destinationOrganizationKey = inDestinationOrganizationKey;
    }

    /**
     * @return the sourceOrganizationKey
     */
    public String getSourceOrganizationKey()
    {
        return sourceOrganizationKey;
    }

    /**
     * @return the destinationOrganizationKey
     */
    public String getDestinationOrganizationKey()
    {
        return destinationOrganizationKey;
    }

}
