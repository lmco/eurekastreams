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
package org.eurekastreams.server.persistence.mappers.requests.opensocial;

import java.util.Set;

/**
 * Action Request for getting any Domain Entities owned by a set of users.
 *
 */
public class UserSetDomainEntitiesRequest
{
    /**
     * user ids of the requested domain entities.
     */
    private Set<String> userIds;
    
    /**
     * Default constructor responsible for assembling the job item.
     * 
     * @param inUserIds
     *            the user ids of the domain entities to get 
     */
    public UserSetDomainEntitiesRequest(
            final Set<String> inUserIds)
    {
        userIds = inUserIds;
    }

    /**
     * Get the user ids of the domain entities to get.
     * 
     * @return the domain entities
     */
    public Set<String> getUserIds()
    {
        return this.userIds;
    }
}
