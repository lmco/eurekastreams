/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.OrganizationTreeDTO;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByIds;
import org.eurekastreams.server.search.modelview.OrganizationModelView;

/**
 * Mapper to get the Organization Tree DTO from the root downward, using cache.
 */
public class GetOrganizationTreeDTO extends CachedDomainMapper
{
    /**
     * Logger instance.
     */
    private Log log = LogFactory.getLog(GetOrganizationTreeDTO.class);

    /**
     * Mapper to get orgs by id, using cache.
     */
    private GetOrganizationsByIds getOrganizationsByIdMapper;

    /**
     * Fetch the OrganizationTreeDTO from cache, or load from DB and put into cache if not present.
     * 
     * @return the OrganizationTreeDTO from the root, downward
     */
    public OrganizationTreeDTO execute()
    {
        OrganizationTreeDTO orgTree = (OrganizationTreeDTO) getCache().get(CacheKeys.ORGANIZATION_TREE_DTO);
        if (orgTree == null)
        {
            log.info("Building Organization Tree for cache.");

            // build a hash of all orgs, grouped by parent org id
            HashMap<Long, Set<OrganizationModelView>> orgHash = new HashMap<Long, Set<OrganizationModelView>>();
            OrganizationModelView rootOrg = buildOrgTreeHash(orgHash);

            // build the org tree
            orgTree = populateTree(rootOrg, orgHash);

            // store in cache
            getCache().set(CacheKeys.ORGANIZATION_TREE_DTO, orgTree);

            log.info("Organization Tree built and stored in cache.");
        }
        return orgTree;
    }

    /**
     * Build a Map of org id -> children OrganizationModelViews for all OrganizationModelViews.
     * 
     * @param inOrgHash
     *            the org hash to store org id -> OrganizationModelViews
     * @return the root OrganizationModelView
     */
    private OrganizationModelView buildOrgTreeHash(final Map<Long, Set<OrganizationModelView>> inOrgHash)
    {
        OrganizationModelView rootOrg = null;
        
        // get all of the organization model views
        List<OrganizationModelView> orgs = getOrganizationsByIdMapper.execute();

        // loop through the orgs, grouping them by parent org id for quicker looping while building the tree
        for (OrganizationModelView org : orgs)
        {
            if (org.getParentOrganizationId() == org.getEntityId())
            {
                // don't add the root org to its parent list (self)
                rootOrg = org;
                continue;
            }
            Set<OrganizationModelView> children;
            if (!inOrgHash.containsKey(org.getParentOrganizationId()))
            {
                children = new HashSet<OrganizationModelView>();
                inOrgHash.put(org.getParentOrganizationId(), children);
            }
            else
            {
                children = inOrgHash.get(org.getParentOrganizationId());
            }
            children.add(org);
        }
        return rootOrg;
    }

    /**
     * Populate the tree of organizations recursively.
     * 
     * @param inOrg
     *            the starting point
     * @param inOrgMap
     *            all OrganizationModelViews in the system
     * @return the tree.
     */
    private OrganizationTreeDTO populateTree(final OrganizationModelView inOrg,
            final Map<Long, Set<OrganizationModelView>> inOrgMap)
    {
        OrganizationTreeDTO orgTree = new OrganizationTreeDTO();
        orgTree.setDisplayName(inOrg.getName());
        orgTree.setOrgId(inOrg.getEntityId());
        orgTree.setShortName(inOrg.getShortName());

        List<OrganizationTreeDTO> children = new ArrayList<OrganizationTreeDTO>();

        if (inOrgMap.containsKey(inOrg.getEntityId()))
        {
            // has children
            for (OrganizationModelView childOrg : inOrgMap.get(inOrg.getEntityId()))
            {
                children.add(populateTree(childOrg, inOrgMap));
            }
        }
        orgTree.setChildren(children);

        return orgTree;
    }

    /**
     * @param inGetOrganizationsByIdMapper
     *            the getOrganizationsByIdMapper to set
     */
    public void setGetOrganizationsByIdMapper(final GetOrganizationsByIds inGetOrganizationsByIdMapper)
    {
        this.getOrganizationsByIdMapper = inGetOrganizationsByIdMapper;
    }
}
