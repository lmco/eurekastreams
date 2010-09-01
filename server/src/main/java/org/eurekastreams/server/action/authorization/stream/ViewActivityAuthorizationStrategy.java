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
package org.eurekastreams.server.action.authorization.stream;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.domain.stream.ActivitySecurityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.GetPrivateCoordinatedAndFollowedGroupIdsForUser;

/**
 * Authorize if current user has permissions to view given activity id. This ONLY restricts viewing activities posted to
 * private groups, activities posted to public group or any personal stream are visible to anyone.
 * 
 */
public class ViewActivityAuthorizationStrategy implements AuthorizationStrategy<ServiceActionContext>
{

    /**
     * {@link ActivitySecurityDTO} DAO.
     */
    private DomainMapper<List<Long>, Collection<ActivitySecurityDTO>> securityMapper;

    /**
     * Mapper to get the list of group ids that includes private groups the current user can see activity for.
     */
    private GetPrivateCoordinatedAndFollowedGroupIdsForUser getVisibleGroupsForUserMapper;

    /**
     * Constructor.
     * 
     * @param inSecurityMapper
     *            {@link ActivitySecurityDTO} DAO.
     * @param inGetVisibleGroupsForUserMapper
     *            Mapper to get the list of group ids that includes private groups the current user can see activity
     *            for.
     */
    public ViewActivityAuthorizationStrategy(
            final DomainMapper<List<Long>, Collection<ActivitySecurityDTO>> inSecurityMapper,
            final GetPrivateCoordinatedAndFollowedGroupIdsForUser inGetVisibleGroupsForUserMapper)
    {
        securityMapper = inSecurityMapper;
        getVisibleGroupsForUserMapper = inGetVisibleGroupsForUserMapper;
    }

    /**
     * Authorize if current user has permissions to view given activity id. Currently only restriction on viewing
     * activities is on activities posted to private groups, activities post to public group or any personal stream are
     * visible to anyone.
     * 
     * @param inActionContext
     *            ActionContext for request.
     */
    @Override
    public void authorize(final ServiceActionContext inActionContext)
    {
        Collection<ActivitySecurityDTO> securityDTOs = securityMapper.execute(Arrays.asList((Long) inActionContext
                .getParams()));

        if (securityDTOs.size() != 1)
        {
            throw new AuthorizationException("Unable to determine access rights to view activity.");
        }

        for (ActivitySecurityDTO actSec : securityDTOs)
        {
            if (!actSec.isDestinationStreamPublic()
                    && !getVisibleGroupsForUserMapper.execute(inActionContext.getPrincipal().getId()).contains(
                            actSec.getDestinationEntityId()))
            {
                throw new AuthorizationException("Current user does not have access right to view activity.");
            }
        }
    }

}
