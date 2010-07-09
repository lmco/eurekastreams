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

import java.util.List;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;

/**
 * Mapper to remove followers from a group. NOTE: This is only being used for Group Deletion, so the group's followers
 * count is not updated, as it will be deleted anyway. If group is NOT going to be deleted, group followers count should
 * be updated and the entity should be reindexed.
 * 
 */
public class RemoveGroupFollowers extends BaseArgDomainMapper<Long, List<Long>>
{
    /**
     * Removes followers from a given group.
     * 
     * @param inRequest
     *            The group id.
     * @return True if successful.
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Long> execute(final Long inRequest)
    {
        // get list of follower ids pre-delete, this is needed for cache clean-up.
        List<Long> followerIds = getEntityManager().createQuery(
                "SELECT gf1.pk.followerId FROM GroupFollower gf1 " + "WHERE gf1.pk.followingId=:followingId")
                .setParameter("followingId", inRequest).getResultList();

        // Decrement the groupStreamIndex for all users that are following the group
        getEntityManager().createQuery(// \n
                "UPDATE GroupFollower gf1 SET gf1.groupStreamIndex = gf1.groupStreamIndex - 1 "// \n
                        + "WHERE gf1.groupStreamIndex > " // \n
                        + "(SELECT gf2.groupStreamIndex FROM GroupFollower gf2 " // \n
                        + "WHERE gf2.pk.followerId = gf1.pk.followerId AND gf2.pk.followingId=:followingId)") // \n
                .setParameter("followingId", inRequest).executeUpdate();

        // now update the counts for persons.
        // NOTE: groupsCount is not indexed by search so we don't need to reindex all the Person entities updated here.
        getEntityManager().createQuery(// \n
                "UPDATE VERSIONED Person SET groupsCount = followingGroup.size - 1 WHERE id IN "// \n
                        + "(SELECT gf3.pk.followerId FROM GroupFollower gf3 WHERE gf3.pk.followingId=:followingId)")
                .setParameter("followingId", inRequest).executeUpdate();

        // Remove the actual entries from GroupFollower table. Have to do this last as these entires are
        // used for set the groupsCount in the previous query.
        getEntityManager().createQuery("DELETE FROM GroupFollower WHERE followingId=:followingId")// \n
                .setParameter("followingId", inRequest).executeUpdate();

        // NOTE: this is only being used for Group Deletion, so the group's followers count is not updated, as it
        // will be deleted anyway. If group is NOT going to be deleted, group followers count should be updated and
        // the entity should be reindexed.

        return followerIds;
    }

}
