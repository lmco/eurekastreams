/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.Set;

import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * This mapper is responsible for adding a group id to the list of private group ids
 * maintained for each user that has either group/org coord access to that group.
 *
 */
public class AddPrivateGroupIdToCachedCoordinatorAccessList extends CachedDomainMapper
{
    /**
     * Mapper used to retrieve all of the users that have group/org coordinator access to the
     * target group.
     */
    private final GetAllPersonIdsWhoHaveGroupCoordinatorAccess personIdsWithGroupCoordAccessMapper;
    
    /**
     * Constructor.
     * @param inPersonIdsWithGroupCoordAccessMapper - instance of the 
     *          {@link GetAllPersonIdsWhoHaveGroupCoordinatorAccess} mapper.
     */
    public AddPrivateGroupIdToCachedCoordinatorAccessList(
            final GetAllPersonIdsWhoHaveGroupCoordinatorAccess inPersonIdsWithGroupCoordAccessMapper)
    {
        personIdsWithGroupCoordAccessMapper = inPersonIdsWithGroupCoordAccessMapper;
    }
    
    /**
     * Execution method for this mapper.  Performs the addition of the group to all of the user's cache lists
     * that have group/org coordinator access to the target group.
     * @param inPrivateDomainGroupId - target private group id.
     */
    public void execute(final Long inPrivateDomainGroupId)
    {
        Set<Long> userIds = personIdsWithGroupCoordAccessMapper.execute(inPrivateDomainGroupId);
        
        for (Long currentUserId : userIds)
        {
            getCache().addToSet(
                    CacheKeys.PRIVATE_GROUP_IDS_VIEWABLE_BY_PERSON_AS_COORDINATOR + currentUserId, 
                    inPrivateDomainGroupId);
        }
    }

}
