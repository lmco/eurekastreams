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
 * Request object for GetFollowedEntitiesByPrefix DAO.
 *
 */
public class GetEntitiesByPrefixRequest
{

    /**
     * The string prefix.
     */
    private String prefix;
    
    /**
     * The identifier for the user to find followed entities for.
     */
    private String userKey;
    
    /**
     * Constructor.
     * @param inUserKey The user to find followed entities for.
     * @param inPrefix string prefix.
     */
    public GetEntitiesByPrefixRequest(final String inUserKey, final String inPrefix)
    {
        userKey = inUserKey;
        prefix = inPrefix;
    }
    
    /**
     * Getter for the prefix.
     * @return The prefix.
     */
    public String getPrefix()
    {
        return prefix;
    }
    
    /**
     * Getter for the userKey.
     * @return The userKey.
     */
    public String getUserKey()
    {
        return userKey;
    }
}
