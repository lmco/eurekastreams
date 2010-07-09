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
package org.eurekastreams.server.persistence.mappers.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.OrganizationChild;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByIds;
import org.eurekastreams.server.search.modelview.OrganizationModelView;

/**
 * Cache mapper to build and populate the input org child's parent organization with a skeleton Org that should be
 * sufficient for most client-side uses.
 */
public class PopulateOrgChildWithSkeletonParentOrgsCacheMapper
{
    /**
     * Log.
     */
    Log log = LogFactory.make();

    /**
     * Mapper to get people by ids cached mappers.
     */
    private GetOrganizationsByIds getOrgsByIdsCacheMapper;

    /**
     * Constructor.
     *
     * @param inGetOrgsByIdsCacheMapper
     *            mapper to get a cached organization
     */
    public PopulateOrgChildWithSkeletonParentOrgsCacheMapper(final GetOrganizationsByIds inGetOrgsByIdsCacheMapper)
    {
        getOrgsByIdsCacheMapper = inGetOrgsByIdsCacheMapper;
    }

    /**
     * Populate the input child org with a skeleton Organization.
     *
     * @param inOrgChild
     *            the org child to populate
     */
    public void populateParentOrgSkeleton(final OrganizationChild inOrgChild)
    {
        ArrayList<OrganizationChild> orgChildren = new ArrayList<OrganizationChild>();
        orgChildren.add(inOrgChild);
        populateParentOrgSkeletons(orgChildren);
    }

    /**
     * Populate the input org child's parent organization with a skeleton Org that should be sufficient for most
     * client-side uses.
     *
     * - orgId, shortName, name, bannerId, backgroundColor
     *
     *
     * @param inOrgChildren
     *            the org children to populate skeleton orgs for
     */
    public void populateParentOrgSkeletons(final Collection<OrganizationChild> inOrgChildren)
    {
        // build up a list of org ids to fetch
        List<Long> orgIds = new ArrayList<Long>();
        for (OrganizationChild oc : inOrgChildren)
        {
            if (oc.getParentOrgId() != null && oc.getParentOrgId() > 0 && !orgIds.contains(oc.getParentOrgId()))
            {
                log.trace("queuing org id for cache retrieval: " + oc.getParentOrgId());
                orgIds.add(oc.getParentOrgId());
            }
        }

        // batch get
        List<OrganizationModelView> orgMvs = getOrgsByIdsCacheMapper.execute(orgIds);

        // hash group them for quick access
        HashMap<Long, OrganizationModelView> hash = new HashMap<Long, OrganizationModelView>();
        for (OrganizationModelView orgMv : orgMvs)
        {
            hash.put(orgMv.getEntityId(), orgMv);
        }

        for (OrganizationChild oc : inOrgChildren)
        {
            if (oc.getParentOrgId() != null && oc.getParentOrgId() > 0)
            {
                OrganizationModelView orgMv = hash.get(oc.getParentOrgId());
                log.info("Setting parent org id skeleton " + oc.toString() + " to " + orgMv.toString());
                oc.setParentOrganization(new Organization(orgMv));
            }
        }
    }
}
