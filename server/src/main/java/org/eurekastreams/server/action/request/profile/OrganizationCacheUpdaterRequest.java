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
package org.eurekastreams.server.action.request.profile;

import java.io.Serializable;
import java.util.Set;

/**
 * Request for updating cache for organization changes.
 * 
 */
public class OrganizationCacheUpdaterRequest implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = 8660403722579087295L;

    /**
     * Organization id.
     */
    private Long organizationId;

    /**
     * Set of coordinator ids.
     */
    private Set<Long> coordinatorIds;

    /**
     * Set of original coordinator ids.
     */
    private Set<Long> originalCoordinatorIds;

    /**
     * Flag to determine if should clear the recursive org children id cache for every org up the tree from input org.
     */
    private boolean clearRecursiveOrgChildernUpTree = false;

    /**
     * Constructor.
     * 
     * @param inOrganizationId
     *            Organization id.
     * @param inCoordinatorIds
     *            Set of coordinator ids.
     * @param inClearRecursiveOrgChildernUpTree
     *            Flag to determine if should clear the recursive org children id cache for every org up the tree from
     *            input org.
     * @param inOrigCoodinatorIds
     *            original cooridnator ids.
     */
    public OrganizationCacheUpdaterRequest(final Long inOrganizationId, final Set<Long> inCoordinatorIds,
            final boolean inClearRecursiveOrgChildernUpTree, final Set<Long> inOrigCoodinatorIds)
    {
        organizationId = inOrganizationId;
        coordinatorIds = inCoordinatorIds;
        clearRecursiveOrgChildernUpTree = inClearRecursiveOrgChildernUpTree;
        originalCoordinatorIds = inOrigCoodinatorIds;
    }

    /**
     * Constructor.
     * 
     * @param inOrganizationId
     *            Organization id.
     * @param inCoordinatorIds
     *            Set of coordinator ids.
     * @param inOrigCoodinatorIds
     *            original cooridnator ids.
     */
    public OrganizationCacheUpdaterRequest(final Long inOrganizationId, final Set<Long> inCoordinatorIds,
            final Set<Long> inOrigCoodinatorIds)
    {
        this(inOrganizationId, inCoordinatorIds, false, inOrigCoodinatorIds);
    }

    /**
     * Constructor. (Serialization).
     */
    @SuppressWarnings("unused")
    private OrganizationCacheUpdaterRequest()
    {
        // no-op.
    }

    /**
     * @return the organizationId
     */
    public Long getOrganizationId()
    {
        return organizationId;
    }

    /**
     * @return the coordinatorIds
     */
    public Set<Long> getCoordinatorIds()
    {
        return coordinatorIds;
    }

    /**
     * @param inOrganizationId
     *            the organizationId to set
     */
    @SuppressWarnings("unused")
    private void setOrganizationId(final Long inOrganizationId)
    {
        organizationId = inOrganizationId;
    }

    /**
     * @param inCoordinatorIds
     *            the coordinatorIds to set
     */
    @SuppressWarnings("unused")
    private void setCoordinatorIds(final Set<Long> inCoordinatorIds)
    {
        coordinatorIds = inCoordinatorIds;
    }

    /**
     * @return the clearRecursiveOrgChildernUpTree
     */
    public boolean getClearRecursiveOrgChildernUpTree()
    {
        return clearRecursiveOrgChildernUpTree;
    }

    /**
     * @param inClearRecursiveOrgChildernUpTree
     *            the clearRecursiveOrgChildernUpTree to set
     */
    @SuppressWarnings("unused")
    private void setClearRecursiveOrgChildernUpTree(final boolean inClearRecursiveOrgChildernUpTree)
    {
        clearRecursiveOrgChildernUpTree = inClearRecursiveOrgChildernUpTree;
    }

    /**
     * @return the originalCoordinatorIds
     */
    public Set<Long> getOriginalCoordinatorIds()
    {
        return originalCoordinatorIds;
    }

    /**
     * @param inOriginalCoordinatorIds
     *            the originalCoordinatorIds to set
     */
    @SuppressWarnings("unused")
    private void setOriginalCoordinatorIds(final Set<Long> inOriginalCoordinatorIds)
    {
        originalCoordinatorIds = inOriginalCoordinatorIds;
    }

}
