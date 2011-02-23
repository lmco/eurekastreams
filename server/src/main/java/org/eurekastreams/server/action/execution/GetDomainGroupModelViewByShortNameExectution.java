/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import java.io.Serializable;
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.persistence.mappers.cache.PopulateOrgChildWithSkeletonParentOrgsCacheMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;

/**
 * Return DomainGroupModelView for provided group shortName.
 * 
 */
public class GetDomainGroupModelViewByShortNameExectution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Mapper used to look up the group.
     */
    private GetDomainGroupsByShortNames groupByShortNameMapper;

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
     * @param inGroupByShortNameMapper
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
    public GetDomainGroupModelViewByShortNameExectution(
            final GetDomainGroupsByShortNames inGroupByShortNameMapper,
            final PopulateOrgChildWithSkeletonParentOrgsCacheMapper inPopulateOrgChildWithSkeletonParentOrgsCacheMapper,
            final GetAllPersonIdsWhoHaveGroupCoordinatorAccess inGroupCoordinatorIdsDAO,
            final GetBannerIdByParentOrganizationStrategy inGetBannerIdStrategy,
            final DomainMapper<Long, List<Long>> inGroupFollowerIdsMapper)
    {
        groupByShortNameMapper = inGroupByShortNameMapper;
        populateOrgChildWithSkeletonParentOrgsCacheMapper = inPopulateOrgChildWithSkeletonParentOrgsCacheMapper;
        groupCoordinatorIdsDAO = inGroupCoordinatorIdsDAO;
        getBannerIdStrategy = inGetBannerIdStrategy;
        groupFollowerIdsMapper = inGroupFollowerIdsMapper;
    }

    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        String shortName = (String) inActionContext.getParams();
        DomainGroupModelView result = groupByShortNameMapper.fetchUniqueResult(shortName);

        return result;
    }

}
