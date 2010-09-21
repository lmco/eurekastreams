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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.HashSet;
import java.util.Set;

import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverser;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverserBuilder;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.GetRecursiveParentOrgIds;
import org.eurekastreams.server.persistence.mappers.requests.DeleteGroupResponse;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;

/**
 * Deletes a group and adjusts denormalized organization statistics accordingly.
 */
public class DeleteGroup extends BaseArgDomainMapper<Long, DeleteGroupResponse>
{
    /**
     * {@link FindByIdMapper}.
     */
    private FindByIdMapper<DomainGroup> groupMapper;

    /**
     * {@link OrganizationMapper}.
     */
    private OrganizationMapper organizationMapper;

    /**
     * {@link GetRecursiveParentOrgIds}.
     */
    private GetRecursiveParentOrgIds parentOrgIdMapper;

    /**
     * The organization hierarchy traverser builder - needed because this class is reused by all threads, we can't share
     * OrganizationHierarchyTraversers.
     */
    private final OrganizationHierarchyTraverserBuilder orgTraverserBuilder;

    /**
     * Constructor.
     *
     * @param inGroupMapper
     *            {@link FindByIdMapper}.
     * @param inOrganizationMapper
     *            {@link OrganizationMapper}.
     * @param inOrgTraverserBuilder
     *            {@link OrganizationHierarchyTraverserBuilder}.
     * @param inParentOrgIdMapper
     *            {@link GetRecursiveParentOrgIds}.
     */
    public DeleteGroup(final FindByIdMapper<DomainGroup> inGroupMapper, final OrganizationMapper inOrganizationMapper,
            final OrganizationHierarchyTraverserBuilder inOrgTraverserBuilder,
            final GetRecursiveParentOrgIds inParentOrgIdMapper)
    {
        groupMapper = inGroupMapper;
        organizationMapper = inOrganizationMapper;
        orgTraverserBuilder = inOrgTraverserBuilder;
        parentOrgIdMapper = inParentOrgIdMapper;
    }

    /**
     * Deletes a group and adjusts denormalized organization statistics accordingly.
     *
     * @param inRequest
     *            group id.
     * @return Set of organization ids representing parent orgs all the way up the tree, will need these to adjust
     *         cache.
     */
    @SuppressWarnings("unchecked")
    @Override
    public DeleteGroupResponse execute(final Long inRequest)
    {
        DomainGroup group = groupMapper.execute(new FindByIdRequest("DomainGroup", inRequest));
        Long groupId = group.getId();
        Organization parentOrg = group.getParentOrganization();
        Long parentOrgId = group.getParentOrgId();

        // get set of parentOrgs
        Set<Long> parentOrgIds = new HashSet<Long>(parentOrgIdMapper.execute(parentOrgId));
        parentOrgIds.add(parentOrgId);

        DeleteGroupResponse response = new DeleteGroupResponse(groupId, group.getShortName(), new Long(group
                .getStreamScope().getId()), parentOrgIds);

        // delete the group hibernate should take care of following since we are deleting via entity manager.
        // Hibernate: delete from Group_Capability where domainGroupId=?
        // Hibernate: delete from Group_Task where groupId=?
        // Hibernate: delete from Group_Coordinators where DomainGroup_id=?
        // Hibernate: delete from StreamView_StreamScope where StreamView_id=?
        // Hibernate: delete from GroupFollower where followingId=? (this should be gone already).
        // Hibernate: delete from DomainGroup where id=? and version=?
        // Hibernate: delete from StreamView where id=? and version=?
        getEntityManager().remove(group);

        OrganizationHierarchyTraverser orgTraverser = orgTraverserBuilder.getOrganizationHierarchyTraverser();
        orgTraverser.traverseHierarchy(parentOrg);
        organizationMapper.updateOrganizationStatistics(orgTraverser);

        return response;

    }
}
