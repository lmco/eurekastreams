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

import java.io.Serializable;
import java.util.List;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.search.modelview.ModelView;
import org.eurekastreams.server.action.request.directory.GetDirectorySearchResultsRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.ResourceSortCriteria;
import org.eurekastreams.server.persistence.mappers.GetRootOrganizationIdAndShortName;
import org.eurekastreams.server.service.actions.strategies.directory.OrgChildrenGetter;
import org.eurekastreams.server.service.actions.strategies.directory.TransientPropertyPopulator;

/**
 * Strategy to return child ModelView objects of an organization.
 * 
 * @param <T>
 *            the type of ModelViews to return
 */
public class GetOrganizationChildrenExecution<T extends ModelView> implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * The strategy to use to get the root organization.
     */
    private GetRootOrganizationIdAndShortName rootOrgGetter;

    /**
     * Strategy to get the org's children.
     */
    private OrgChildrenGetter<T> orgChildrenGetter;

    /**
     * The transient property populator for search results.
     */
    private TransientPropertyPopulator transientPropertyPopulator;

    /**
     * Constructor.
     * 
     * @param inRootOrgGetter
     *            the strategy to get the root organization short name
     * @param inOrgChildrenGetter
     *            the strategy to get the org's children
     * @param inTransientPropertyPopulator
     *            the strategy to populate additional properties on the search results as they return
     */
    public GetOrganizationChildrenExecution(final GetRootOrganizationIdAndShortName inRootOrgGetter,
            final OrgChildrenGetter<T> inOrgChildrenGetter,
            final TransientPropertyPopulator inTransientPropertyPopulator)
    {
        rootOrgGetter = inRootOrgGetter;
        orgChildrenGetter = inOrgChildrenGetter;
        transientPropertyPopulator = inTransientPropertyPopulator;
    }

    /**
     * Get the list of child modelViews.
     * 
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     * @return List of child modelViews.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext)
    {
        GetDirectorySearchResultsRequest request = (GetDirectorySearchResultsRequest) inActionContext.getParams();
        String shortName = request.getOrgShortName();

        Integer from = request.getStartIndex();
        Integer to = request.getEndIndex();
        ResourceSortCriteria sortCriteria = request.getSortCriteria();

        if (shortName == null || shortName.trim().equals(""))
        {
            shortName = rootOrgGetter.getRootOrganizationShortName();
        }

        // get the current user's Person id.
        long userPersonId = inActionContext.getPrincipal().getId();

        PagedSet<T> results = orgChildrenGetter.getOrgChildren(shortName, from, to, sortCriteria, userPersonId);

        // populate any transient properties
        transientPropertyPopulator.populateTransientProperties((List<ModelView>) results.getPagedSet(), userPersonId,
                shortName);

        return results;
    }
}
