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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Mapper to get a set of domain group ids that include all groups that are private that a user can view activity for.
 * 
 * This includes all of the private groups that a user has access to view activity for based on either group or org
 * coordinator relationships, as well as the group ids (public or private) that a user is following.
 * 
 * Since the list contains all groups that the the user follows, some of these ids might be public groups.
 */
public class GetPrivateCoordinatedAndFollowedGroupIdsForUser extends CachedDomainMapper
{
    /**
     * Logger.
     */
    private Log log = LogFactory.getLog(GetPrivateCoordinatedAndFollowedGroupIdsForUser.class);

    /**
     * Mapper to get all private group ids that a user can view with org or group coordinator access.
     */
    private GetPrivateGroupsByUserId getPrivateGroupIdsMapper;

    /**
     * Mapper to get the group ids followed by a person.
     */
    private DomainMapper<Long, List<Long>> getFollowedGroupIdsMapper;

    /**
     * Constructor.
     * 
     * @param inGetPrivateGroupIdsMapper
     *            mapper to get a set of group ids that a user has access to see activity for with org or group
     *            coordinator access
     * @param inGetFollowedGroupIdsMapper
     *            mapper to get a list of ids of groups followed by a user
     */
    public GetPrivateCoordinatedAndFollowedGroupIdsForUser(final GetPrivateGroupsByUserId inGetPrivateGroupIdsMapper,
            final DomainMapper<Long, List<Long>> inGetFollowedGroupIdsMapper)
    {
        getPrivateGroupIdsMapper = inGetPrivateGroupIdsMapper;
        getFollowedGroupIdsMapper = inGetFollowedGroupIdsMapper;
    }

    /**
     * Get the set of group ids that a user can view activity for.
     * 
     * @param inUserPersonId
     *            the user id to get a list of domain group ids for
     * @return a set of group ids that includes all private groups that a user can view activity for
     */
    @SuppressWarnings("unchecked")
    public Set<Long> execute(final Long inUserPersonId)
    {
        log.info("Requesting both lists from cache in parallel for user #" + inUserPersonId);
        String privateCoordinatedGroupIdsKey = CacheKeys.PRIVATE_GROUP_IDS_VIEWABLE_BY_PERSON_AS_COORDINATOR
                + inUserPersonId;
        String followedGroupIdsKey = CacheKeys.GROUPS_FOLLOWED_BY_PERSON + inUserPersonId;

        List<String> cacheKeys = new ArrayList<String>();
        cacheKeys.add(privateCoordinatedGroupIdsKey);
        cacheKeys.add(followedGroupIdsKey);

        // ask for the keys in parallel and look for the results
        Set<Long> privateGroupIds = (Set<Long>) getCache().get(privateCoordinatedGroupIdsKey);
        if (privateGroupIds == null)
        {
            log.info("Couldn't find the private group ids via org/group coordinator access in cache for user #"
                    + inUserPersonId + ", using GetPrivateGroupsByUserId");

            privateGroupIds = getPrivateGroupIdsMapper.execute(inUserPersonId);
        }

        List<Long> followedGroupIds = getCache().getList(followedGroupIdsKey);
        if (followedGroupIds == null)
        {
            log.info("Couldn't find the followed group ids via org/group coordinator access in cache for user #"
                    + inUserPersonId + ", using GetFollowedGroupIds");
            followedGroupIds = getFollowedGroupIdsMapper.execute(inUserPersonId);
        }

        Set<Long> groupIds = new HashSet<Long>();
        groupIds.addAll(privateGroupIds);
        groupIds.addAll(followedGroupIds);
        return groupIds;
    }
}
