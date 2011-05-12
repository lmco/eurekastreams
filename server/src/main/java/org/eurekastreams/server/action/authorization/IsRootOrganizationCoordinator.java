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
package org.eurekastreams.server.action.authorization;

import java.io.Serializable;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Strategy for determining if user is root organizaiton coordinator.
 * 
 */
public class IsRootOrganizationCoordinator
{
    /**
     * Mapper to get the ids of the system admins.
     */
    private DomainMapper<Serializable, List<Long>> systemAdminIdsMapper;

    /**
     * Constructor.
     * 
     * @param inSystemAdminIdsMapper
     *            mapper to get a list of the ids of the system admins
     */
    public IsRootOrganizationCoordinator(final DomainMapper<Serializable, List<Long>> inSystemAdminIdsMapper)
    {
        systemAdminIdsMapper = inSystemAdminIdsMapper;
    }

    /**
     * Return true if current user is root org coordinator, false otherwise.
     * 
     * @param inUserEntityId
     *            User id to check..
     * @return true if current user is root org coordinator, false otherwise.
     */
    public Boolean isRootOrganizationCoordinator(final Long inUserEntityId)
    {
        return systemAdminIdsMapper.execute(null).contains(inUserEntityId);
    }

}
