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

import java.util.Set;

import org.eurekastreams.server.persistence.mappers.GetOrgCoordinators;
import org.eurekastreams.server.persistence.mappers.GetRootOrganizationIdAndShortName;

/**
 * Strategy for determining if user is root organizaiton coordinator.
 * 
 */
public class IsRootOrganizationCoordinator
{
    /**
     * Root org id DAO.
     */
    private GetRootOrganizationIdAndShortName rootOrgIdDAO;

    /**
     * Org coordinator ids DAO.
     */
    private GetOrgCoordinators orgCoordinatorIdsDAO;

    /**
     * Constructor.
     * 
     * @param inRootOrgIdDAO
     *            Root org id DAO.
     * @param inOrgCoordinatorIdsDAO
     *            Org coordinator ids DAO.
     */
    public IsRootOrganizationCoordinator(final GetRootOrganizationIdAndShortName inRootOrgIdDAO,
            final GetOrgCoordinators inOrgCoordinatorIdsDAO)
    {
        rootOrgIdDAO = inRootOrgIdDAO;
        orgCoordinatorIdsDAO = inOrgCoordinatorIdsDAO;
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
        Set<Long> coordinatorIds = orgCoordinatorIdsDAO.execute(rootOrgIdDAO.getRootOrganizationId());
        return coordinatorIds.contains(inUserEntityId);
    }

}
