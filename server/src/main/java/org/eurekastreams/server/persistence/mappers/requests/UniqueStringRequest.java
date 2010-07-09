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
package org.eurekastreams.server.persistence.mappers.requests;

/**
 * Request for any mapper that requires a String unique Id (shortname, accountid, etc).
 *
 */
public class UniqueStringRequest
{
    /**
     * unique id.
     */
    private String uniqueId;
    
    /**
     * Constructor.
     * @param inUniqueId the unique id of the object (i.e. accountId for person).
     */
    public UniqueStringRequest(final String inUniqueId)
    {
        uniqueId = inUniqueId;
    }
    
    /**
     * Getter for the unique id.
     * @return The unique id.
     */
    public String getUniqueId()
    {
        return uniqueId;
    }
}
