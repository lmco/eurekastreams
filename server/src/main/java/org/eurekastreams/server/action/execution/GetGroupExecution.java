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
package org.eurekastreams.server.action.execution;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.DomainGroupEntity;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.OrganizationChild;
import org.eurekastreams.server.domain.RestrictedDomainGroup;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.persistence.mappers.cache.PopulateOrgChildWithSkeletonParentOrgsCacheMapper;

/**
 * Retrieve a DomainGroup based on its short name.
 */
public class GetGroupExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Mapper used to look up the group.
     */
    private DomainGroupMapper mapper;

    /**
     * Mapper to populate the parent org of people with skeleton orgs from cache.
     */
    private PopulateOrgChildWithSkeletonParentOrgsCacheMapper populateOrgChildWithSkeletonParentOrgsCacheMapper;

    /**
     * Mapper to get all person ids that have group coordinator access for a given group.
     */
    private GetAllPersonIdsWhoHaveGroupCoordinatorAccess groupCoordinatorIdsDAO;

    /**
     * Strategy to retrieve the banner id if it is not directly configured.
     */
    @SuppressWarnings("unchecked")
    private GetBannerIdByParentOrganizationStrategy getBannerIdStrategy;

    /**
     * Mapper to get followers for a group.
     */
    private DomainMapper<Long, List<Long>> groupFollowerIdsMapper;

    /**
     * Constructor.
     * 
     * @param inMapper
     *            injecting the mapper.
     * @param inPopulateOrgChildWithSkeletonParentOrgsCacheMapper
     *            mapper to populate parent orgs with skeleton
     * @param inGroupCoordinatorIdsDAO
     *            Mapper to get all person ids that have group coordinator access for a given group.
     * @param inGetBannerIdStrategy
     *            Instance of the {@link GetBannerIdByParentOrganizationStrategy}.
     * @param inGroupFollowerIdsMapper
     *            Instance of the {@link GetGroupFollowerIds}.
     */
    @SuppressWarnings("unchecked")
    public GetGroupExecution(
            final DomainGroupMapper inMapper,
            final PopulateOrgChildWithSkeletonParentOrgsCacheMapper inPopulateOrgChildWithSkeletonParentOrgsCacheMapper,
            final GetAllPersonIdsWhoHaveGroupCoordinatorAccess inGroupCoordinatorIdsDAO,
            final GetBannerIdByParentOrganizationStrategy inGetBannerIdStrategy,
            final DomainMapper<Long, List<Long>> inGroupFollowerIdsMapper)
    {
        mapper = inMapper;
        populateOrgChildWithSkeletonParentOrgsCacheMapper = inPopulateOrgChildWithSkeletonParentOrgsCacheMapper;
        groupCoordinatorIdsDAO = inGroupCoordinatorIdsDAO;
        getBannerIdStrategy = inGetBannerIdStrategy;
        groupFollowerIdsMapper = inGroupFollowerIdsMapper;
    }

    /**
     * Load up the group specified in the parameter.
     * 
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     * @return the specified DomainGroup or null if not found.
     */
    @Override
    public DomainGroupEntity execute(final PrincipalActionContext inActionContext)
    {
        String shortName = (String) inActionContext.getParams();

        DomainGroupEntity group = mapper.findByShortName(shortName);

        if (null != group)
        {
            if (isAccessPermitted(inActionContext.getPrincipal(), group))
            {
                touchDataToLoad(group);
                // Set the banner id for the group (if not already configured) with the first org in the
                // group's parent org hierarchy that has the banner configured.
                group.setBannerEntityId(group.getId());
                if (group.getBannerId() == null)
                {
                    getBannerIdStrategy.getBannerId(group.getParentOrganization().getId(), group);
                }
            }
            else
            {
                // If this user is not allowed to view this group, return a restricted group instead
                group = new RestrictedDomainGroup(group);
            }

            // Both regular groups and restricted ones will need their org hierarchies, so touch them to make sure they
            // are loaded
            Organization currentOrg = group.getParentOrganization();
            while (currentOrg.getId() != currentOrg.getParentOrganization().getId())
            {
                currentOrg = currentOrg.getParentOrganization();
            }

        }

        return group;
    }

    /**
     * Touch those parts of the group that need to be accessed by the client to make sure they are eagerly loaded.
     * 
     * @param group
     *            the group to touch
     */
    private void touchDataToLoad(final DomainGroupEntity group)
    {
        // touch the capabilities (aka keywords)
        if (group instanceof DomainGroup)
        {
            ((DomainGroup) group).getCapabilities().size();
        }

        // touch the coordinators' groups so they will get loaded
        List<OrganizationChild> coordinators = new ArrayList<OrganizationChild>();
        coordinators.addAll(group.getCoordinators());
        populateOrgChildWithSkeletonParentOrgsCacheMapper.populateParentOrgSkeletons(coordinators);
    }

    /**
     * Check whether this group has restricted access and whether the current user is allowed access.
     * 
     * @param inPrincipal
     *            user principal.
     * @param inGroup
     *            the group the user wants to view
     * @return true if this person is allowed to see this group, false otherwise
     */
    private boolean isAccessPermitted(final Principal inPrincipal, final DomainGroupEntity inGroup)
    {
        // if group is public or user is coordinator recursively or follower, return true, otherwise false.
        return (inGroup.isPublicGroup()
                || groupCoordinatorIdsDAO.execute(inGroup.getId()).contains(inPrincipal.getId()) //
        || isUserFollowingGroup(inPrincipal.getId(), inGroup.getId()));

    }

    /**
     * Checks to see if user is following a group.
     * 
     * @param userId
     *            the user id being checked.
     * @param groupId
     *            the group being checked.
     * @return true if user is a follower, false otherwise.
     */
    private boolean isUserFollowingGroup(final long userId, final long groupId)
    {
        List<Long> ids = groupFollowerIdsMapper.execute(groupId);
        if (ids.contains(userId))
        {
            return true;
        }
        return false;
    }
}
