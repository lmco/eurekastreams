/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import java.util.List;

import org.eurekastreams.server.domain.FollowerStatusable;
import org.eurekastreams.server.domain.Follower.FollowerStatus;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Strategy to populate FollowerStatus of items for a user.
 * 
 * @param <T>
 *            Item type to set FollowerStatus for.
 */
public class FollowerStatusPopulator<T extends FollowerStatusable>
{
    /**
     * Person id followed by Principal mapper.
     */
    private DomainMapper<Long, List<Long>> personIdsFollowedByPrincipalMapper;

    /**
     * Group id followed by Principal mapper.
     */
    private DomainMapper<Long, List<Long>> groupIdsFollowedByPrincipalMapper;

    /**
     * Constructor.
     * 
     * @param inPersonIdsFollowedByPrincipalMapper
     *            Person id followed by Principal mapper.
     * @param inGroupIdsFollowedByPrincipalMapper
     *            Group id followed by Principal mapper.
     */
    public FollowerStatusPopulator(final DomainMapper<Long, List<Long>> inPersonIdsFollowedByPrincipalMapper,
            final DomainMapper<Long, List<Long>> inGroupIdsFollowedByPrincipalMapper)
    {
        personIdsFollowedByPrincipalMapper = inPersonIdsFollowedByPrincipalMapper;
        groupIdsFollowedByPrincipalMapper = inGroupIdsFollowedByPrincipalMapper;
    }

    /**
     * Set follower status on param items.
     * 
     * @param inCurrentUserId
     *            Current User id.
     * @param inFollowerStatusables
     *            items to set follower status on.
     * @param inDefaultStatus
     *            Status to use if unable to determine follower status, NOTSPECIFIED is used if value is null.
     * @return List of param items with status set.
     */
    public List<T> execute(final Long inCurrentUserId, final List<T> inFollowerStatusables,
            final FollowerStatus inDefaultStatus)
    {
        // set default status or use not specified if not specified (imagine that!).
        FollowerStatus defaultStatus = inDefaultStatus == null ? FollowerStatus.NOTSPECIFIED : inDefaultStatus;
        List<Long> personIds = null;
        List<Long> groupIds = null;

        for (FollowerStatusable fs : inFollowerStatusables)
        {
            switch (fs.getEntityType())
            {
            case PERSON:
                personIds = personIds == null ? personIdsFollowedByPrincipalMapper.execute(inCurrentUserId) : personIds;
                fs.setFollowerStatus(personIds.contains(fs.getEntityId()) ? FollowerStatus.FOLLOWING
                        : FollowerStatus.NOTFOLLOWING);
                break;
            case GROUP:
                groupIds = groupIds == null ? groupIdsFollowedByPrincipalMapper.execute(inCurrentUserId) : groupIds;
                fs.setFollowerStatus(groupIds.contains(fs.getEntityId()) ? FollowerStatus.FOLLOWING
                        : FollowerStatus.NOTFOLLOWING);
                break;
            default:
                fs.setFollowerStatus(defaultStatus);
                break;
            }
        }

        return inFollowerStatusables;
    }

}
