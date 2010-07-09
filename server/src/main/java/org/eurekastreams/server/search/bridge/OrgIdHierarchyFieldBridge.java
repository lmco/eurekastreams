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
package org.eurekastreams.server.search.bridge;

import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.persistence.mappers.cache.OrganizationHierarchyCache;
import org.hibernate.search.bridge.StringBridge;

/**
 * Field bridge to use the organization id cache to find all of the parent organization ids for quick recursive
 * retrieval.
 */
public class OrgIdHierarchyFieldBridge implements StringBridge
{
    /**
     * The organization cache to pull hierarchy from.
     */
    private static OrganizationHierarchyCache organizationHierarchyCache;

    /**
     * Set the organiation hierarchy cache to use for fetching parent orgs. This will eliminate org hierarchy traversal
     * on indexing of several entities.
     * 
     * @param inOrganizationHierarchyCache
     *            the org cache to use to get the org hierarchy
     */
    public static void setOrganizationHierarchyCache(final OrganizationHierarchyCache inOrganizationHierarchyCache)
    {
        organizationHierarchyCache = inOrganizationHierarchyCache;
    }

    /**
     * Convert the input Organization to a space-separated list of all of the parent organization ids up the hierarchy,
     * starting with the input Organization's ids.
     * 
     * @param orgObj
     *            an Organization to climb
     * @return a space-separated list of all of the parent organization id up the hierarchy
     */
    @Override
    public String objectToString(final Object orgObj)
    {
        if (organizationHierarchyCache == null)
        {
            throw new RuntimeException("Organization Hierarchy Cache was not set in the OrgIdHierarchyFieldBridge.");
        }

        Organization org = (Organization) orgObj;
        StringBuffer sb = new StringBuffer();
        for (long orgId : organizationHierarchyCache.getSelfAndParentOrganizations(org.getId()))
        {
            sb.append(orgId);
            sb.append(" ");
        }
        return sb.toString();
    }
}
