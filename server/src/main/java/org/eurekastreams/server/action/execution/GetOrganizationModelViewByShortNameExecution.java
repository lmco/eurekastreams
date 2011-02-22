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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetOrgCoordinators;
import org.eurekastreams.server.persistence.mappers.GetRootOrganizationIdAndShortName;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByShortNames;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Return OrganizationModelView by short name.
 */
public class GetOrganizationModelViewByShortNameExecution implements ExecutionStrategy<ActionContext>
{

    /**
     * {@link GetOrganizationsByShortNames}.
     */
    private GetOrganizationsByShortNames mapper;

    /**
     * {@link GetRootOrganizationIdAndShortName}.
     */
    private GetRootOrganizationIdAndShortName rootOrgNameMapper;

    /**
     * Get org leader ids for an org.
     */
    private DomainMapper<Long, Set<Long>> orgLeaderIdsMapper;

    /**
     * Get org coordinator ids for an org.
     */
    private GetOrgCoordinators orgCoordinatorIdsMapper;

    /**
     * Get PersonModelViews by id.
     */
    private DomainMapper<List<Long>, List<PersonModelView>> personModelViewsByIdMapper;

    /**
     * Mapper to retrieve the banner id if it is not directly configured.
     */
    private GetBannerIdByParentOrganizationStrategy getBannerIdStrategy;

    /**
     * Constructor.
     * 
     * @param inMapper
     *            {@link GetOrganizationsByShortNames}.
     * @param inRootOrgNameMapper
     *            {@link GetRootOrganizationIdAndShortName}.
     * @param inOrgLeaderIdsMapper
     *            Get org leader ids for an org.
     * @param inOrgCoordinatorIdsMapper
     *            Get org coordinator ids for an org.
     * @param inPersonModelViewsByIdMapper
     *            Get PersonModelViews by id.
     * @param inGetBannerIdStrategy
     *            Mapper to retrieve the banner id if it is not directly configured.
     */
    public GetOrganizationModelViewByShortNameExecution(final GetOrganizationsByShortNames inMapper,
            final GetRootOrganizationIdAndShortName inRootOrgNameMapper,
            final DomainMapper<Long, Set<Long>> inOrgLeaderIdsMapper,
            final GetOrgCoordinators inOrgCoordinatorIdsMapper,
            final DomainMapper<List<Long>, List<PersonModelView>> inPersonModelViewsByIdMapper,
            final GetBannerIdByParentOrganizationStrategy inGetBannerIdStrategy)
    {
        mapper = inMapper;
        rootOrgNameMapper = inRootOrgNameMapper;
        orgLeaderIdsMapper = inOrgLeaderIdsMapper;
        orgCoordinatorIdsMapper = inOrgCoordinatorIdsMapper;
        personModelViewsByIdMapper = inPersonModelViewsByIdMapper;
        getBannerIdStrategy = inGetBannerIdStrategy;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Serializable execute(final ActionContext inActionContext)
    {
        String orgShortName = (String) inActionContext.getParams();

        if (orgShortName == null || orgShortName.equals(""))
        {
            orgShortName = rootOrgNameMapper.getRootOrganizationShortName();
        }

        OrganizationModelView result = mapper.execute(Collections.singletonList(orgShortName)).get(0);

        long orgId = result.getEntityId();

        // get leader/coordinator ids
        Set<Long> leaderIds = orgLeaderIdsMapper.execute(orgId);
        Set<Long> coordIds = orgCoordinatorIdsMapper.execute(orgId);

        // combine them to remove dups and make single call to get model views.
        Set<Long> allIds = new HashSet<Long>();
        allIds.addAll(leaderIds);
        allIds.addAll(coordIds);

        List<PersonModelView> personModelViews = personModelViewsByIdMapper.execute(new ArrayList<Long>(allIds));

        // create model view lists from results.
        List<PersonModelView> leaders = new ArrayList<PersonModelView>();
        List<PersonModelView> coordinators = new ArrayList<PersonModelView>();
        for (PersonModelView pmv : personModelViews)
        {
            Long id = pmv.getEntityId();

            // if id in leaders, add to leaders, then check if coordinator too, If not in leader, must have come from
            // coordinator so skip the check and just add it.
            if (leaderIds.contains(id))
            {
                leaders.add(pmv);
                if (coordIds.contains(id))
                {
                    coordinators.add(pmv);
                }
            }
            else
            {
                coordinators.add(pmv);
            }
        }

        result.setLeaders(leaders);
        result.setCoordinators(coordinators);

        result.setBannerEntityId(result.getEntityId());
        if (result.getBannerId() == null)
        {
            getBannerIdStrategy.getBannerId(result.getParentOrganizationId(), result);
        }

        return result;
    }
}
