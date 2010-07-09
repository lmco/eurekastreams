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
package org.eurekastreams.server.domain.strategies;

import java.util.HashSet;
import java.util.Set;

import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.OrganizationChild;

/**
 * Build a collection of parent organizations, traversing up the tree, ignoring
 * previously-climbed paths for optimization.
 */
public class OrganizationHierarchyTraverser
{
    /**
     * Empty constructor.
     */
    public OrganizationHierarchyTraverser()
    {
    }

    /**
     * Constructor that starts the traversing at an OrganizationChild.
     * 
     * @param organizationChild
     *            the starting point.
     */
    public OrganizationHierarchyTraverser(final OrganizationChild organizationChild)
    {
        traverseHierarchy(organizationChild);
    }

    /**
     * Set to keep track of the organizations.
     */
    private Set<Organization> organizations = new HashSet<Organization>();

    /**
     * Traverse the organization hierarchy for the input OrganizationChild,
     * collecting the parent organizations, and itself as well if an
     * Organization.
     * 
     * @param organizationChild
     *            the OrganizationChild to get the parent hierarchy for
     */
    public void traverseHierarchy(final OrganizationChild organizationChild)
    {
        Organization parent;
        if (organizationChild instanceof Organization && !organizations.contains(organizationChild))
        {
            // start from the organization child - it's an org
            parent = (Organization) organizationChild;
        }
        else
        {
            // start with the parent - it's not an org
            parent = organizationChild.getParentOrganization();
        }
        while (parent != null && !organizations.contains(parent))
        {
            organizations.add(parent);
            parent = parent.getParentOrganization();
        }
    }

    /**
     * Get the organizations found while traversing up the hierarchy.
     * 
     * @return the organizations found while traversing up the hierarchy
     */
    public Set<Organization> getOrganizations()
    {
        return organizations;
    }
}
