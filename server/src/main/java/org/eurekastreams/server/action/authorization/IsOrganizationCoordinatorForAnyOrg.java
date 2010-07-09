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

import org.eurekastreams.server.persistence.mappers.GetRecursiveOrgCoordinators;
import org.eurekastreams.server.persistence.mappers.GetRootOrganizationIdAndShortName;

/**
 * Strategy for determining if user is org coordinator for any org in tree.
 * 
 */
public class IsOrganizationCoordinatorForAnyOrg
{
    /**
     * Mapper to get the root org.
     */
    private GetRootOrganizationIdAndShortName rootOrgMapper;
    /**
     * The mapper to get back all the coordinators of the root org and below.
     */
    private GetRecursiveOrgCoordinators recursiveOrgMapper;

    /**
     * Default constructor.
     * 
     * @param inRecursiveOrgMapper
     *            recursive org mapper.
     * @param inRootOrgMapper
     *            root org mapper.
     */
    public IsOrganizationCoordinatorForAnyOrg(final GetRecursiveOrgCoordinators inRecursiveOrgMapper,
            final GetRootOrganizationIdAndShortName inRootOrgMapper)
    {
        rootOrgMapper = inRootOrgMapper;
        recursiveOrgMapper = inRecursiveOrgMapper;
    }

    /**
     * Authorize.
     * 
     * @param inUserId
     *            the user id.
     * @return True if user is org coordinator anywhere in tree, false otherwise.
     */
    public Boolean execute(final Long inUserId)
    {
        return recursiveOrgMapper.isOrgCoordinatorRecursively(inUserId, rootOrgMapper.getRootOrganizationId());
    }

}
