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
package org.eurekastreams.server.action.execution.profile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.profile.GetBreadcrumbsListRequest;
import org.eurekastreams.server.domain.BreadcrumbDTO;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.persistence.mappers.GetRecursiveParentOrgIds;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByIds;
import org.eurekastreams.server.search.modelview.OrganizationModelView;

/**
 * Generates a list of breadcrumbs for a given organization's parents.
 * 
 */
public class GetBreadcrumbsListExecution implements ExecutionStrategy<ServiceActionContext>
{
    /**
     * Local logger instance.
     */
    private final Log log = LogFactory.make();

    /**
     * Mapper to get the ids of this organization's parent hierarchy.
     */
    private final GetRecursiveParentOrgIds idsMapper;

    /**
     * Mapper to get org details in bulk.
     */
    private final GetOrganizationsByIds orgMapper;

    /**
     * Constructor.
     * 
     * @param inIdsMapper
     *            - instance of the {@link GetRecursiveParentOrgIds} mapper for this action.
     * @param inOrgMapper
     *            - instance of the {@link GetOrganizationsByIds} mapper for this action.
     */
    public GetBreadcrumbsListExecution(final GetRecursiveParentOrgIds inIdsMapper,
            final GetOrganizationsByIds inOrgMapper)
    {
        idsMapper = inIdsMapper;
        orgMapper = inOrgMapper;
    }

    /**
     * {@inheritDoc}. Uses cache mappers to generate breadcrumbs of recursive parents of an org.
     * 
     * @return List of {@link BreadcrumbDTO} objects relating to the passed in org.
     */
    @Override
    public Serializable execute(final ServiceActionContext inActionContext) throws ExecutionException
    {
        ArrayList<BreadcrumbDTO> breadcrumbs = new ArrayList<BreadcrumbDTO>();
        GetBreadcrumbsListRequest request = (GetBreadcrumbsListRequest) inActionContext.getParams();

        long orgId = request.getOrganizationId();
        if (log.isInfoEnabled())
        {
            log.info("Creating breadcrumbs for organization: " + orgId);
        }

        // get ordered hierarchy of org ids
        List<Long> orgIds = idsMapper.execute(orgId);

        // get Org model views that represent the hierarchy (unordered)
        List<OrganizationModelView> orgs = orgMapper.execute(orgIds);

        // build map of orgModelViews keyed by id.
        Map<Long, OrganizationModelView> orgModelViews = new HashMap<Long, OrganizationModelView>(orgIds.size());
        for (OrganizationModelView org : orgs)
        {
            orgModelViews.put(org.getEntityId(), org);
        }

        // build ordered results.
        OrganizationModelView tempOrg = null;
        int orgIdsSize = orgIds.size();
        for (int i = 0; i < orgIdsSize; i++)
        {
            tempOrg = orgModelViews.get(orgIds.get(i));
            breadcrumbs.add(new BreadcrumbDTO(tempOrg.getName(), Page.ORGANIZATIONS, tempOrg.getShortName()));
            log.debug("Creating breadcrumb: " + tempOrg.getName());
        }

        if (log.isInfoEnabled())
        {
            log.info("Returning " + breadcrumbs.size() + " breadcrumbs");
        }

        return breadcrumbs;
    }

}
