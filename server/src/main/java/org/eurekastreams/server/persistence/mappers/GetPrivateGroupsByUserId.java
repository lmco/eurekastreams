/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class retrieves a set of private group ids from cache that a user has access to view activities for through
 * either a direct group coordinator role or a system admin role.
 * 
 */
public class GetPrivateGroupsByUserId extends ReadMapper<Long, Set<Long>>
{
    /**
     * Mapper to get all private groups.
     */
    private final DomainMapper<Serializable, List<Long>> allPrivateGroupsMapper;

    /**
     * Local instance of mapper to retrieve the private group ids from the db.
     */
    private final GetPrivateGroupIdsCoordinatedByPerson privateGroupIdsMapper;

    /**
     * Mapper to get a list of all ids for system administrators.
     */
    private final DomainMapper<Serializable, List<Long>> systemAdminIdsMapper;

    /**
     * Constructor.
     * 
     * @param inPrivateGroupIdsMapper
     *            - instance of the {@link GetPrivateGroupIdsCoordinatedByPerson} mapper.
     * @param inAllPrivateGroupIdsMapper
     *            mapper to get all private domain groups
     * @param inSystemAdminIdsMapper
     *            mapper to get ids of system administrators
     */
    public GetPrivateGroupsByUserId(final GetPrivateGroupIdsCoordinatedByPerson inPrivateGroupIdsMapper,
            final DomainMapper<Serializable, List<Long>> inAllPrivateGroupIdsMapper,
            final DomainMapper<Serializable, List<Long>> inSystemAdminIdsMapper)
    {
        privateGroupIdsMapper = inPrivateGroupIdsMapper;
        allPrivateGroupsMapper = inAllPrivateGroupIdsMapper;
        systemAdminIdsMapper = inSystemAdminIdsMapper;
    }

    /**
     * Retrieve the Set of ids for the private groups that the supplied user has the ability to view either through
     * group/admin coordinator access.
     * 
     * @param inUserId
     *            - user id of the context to bring back private group ids.
     * @return - Set of private group ids based on the user id context.
     */
    @Override
    public Set<Long> execute(final Long inUserId)
    {
        Set<Long> groupIds = new HashSet<Long>();

        // get the administrator ids
        List<Long> adminPersonIds = systemAdminIdsMapper.execute(null);

        if (adminPersonIds.contains(inUserId))
        {
            // Retrieve all private groups
            groupIds.addAll(allPrivateGroupsMapper.execute(null));
        }
        else
        {
            // just the groups for this person
            groupIds.addAll(privateGroupIdsMapper.execute(inUserId));
        }
        return groupIds;
    }
}
