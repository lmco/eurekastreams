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

import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
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
     * Constructor.
     * 
     * @param inGroupMapper
     *            {@link FindByIdMapper}.
     */
    public DeleteGroup(final FindByIdMapper<DomainGroup> inGroupMapper)
    {
        groupMapper = inGroupMapper;
    }

    /**
     * Deletes a group.
     * 
     * @param inRequest
     *            group id.
     * @return Set of organization ids representing parent orgs all the way up the tree, will need these to adjust
     *         cache.
     */
    @Override
    public DeleteGroupResponse execute(final Long inRequest)
    {
        DomainGroup group = groupMapper.execute(new FindByIdRequest("DomainGroup", inRequest));
        Long groupId = group.getId();

        DeleteGroupResponse response = new DeleteGroupResponse(groupId, group.getShortName(), new Long(group
                .getStreamScope().getId()));

        // delete the group hibernate should take care of following since we are deleting via entity manager.
        // Hibernate: delete from Group_Capability where domainGroupId=?
        // Hibernate: delete from Group_Task where groupId=?
        // Hibernate: delete from Group_Coordinators where DomainGroup_id=?
        // Hibernate: delete from StreamView_StreamScope where StreamView_id=?
        // Hibernate: delete from GroupFollower where followingId=? (this should be gone already).
        // Hibernate: delete from DomainGroup where id=? and version=?
        // Hibernate: delete from StreamView where id=? and version=?
        getEntityManager().remove(group);

        return response;

    }
}
