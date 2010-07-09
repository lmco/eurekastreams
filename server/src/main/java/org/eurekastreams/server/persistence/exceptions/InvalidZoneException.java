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
package org.eurekastreams.server.persistence.exceptions;

/**
 * Used to indicate that an operation failed because it referred to an invalid
 * zone.
 * 
 */
public class InvalidZoneException extends Exception
{
    /**
     * Serializer version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The invalid zone that some operator referred to.
     */
    private int invalidZone;

    /**
     * Constructor.
     * @param zone The zone.
     * @param message The message.
     */
    public InvalidZoneException(final int zone, final String message)
    {
        super(message);
        invalidZone = zone;
    }

    /**
     * Getter for invalid zone value.
     * @return Invalid zone value.
     */
    public int getInvalidZone()
    {
        return invalidZone;
    }
}
