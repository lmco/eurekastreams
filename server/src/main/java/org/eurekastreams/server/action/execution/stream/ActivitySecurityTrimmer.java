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
package org.eurekastreams.server.action.execution.stream;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.stream.ActivitySecurityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.GetPrivateCoordinatedAndFollowedGroupIdsForUser;

/**
 * Trims activities that a user does not have permission to see.
 */
public class ActivitySecurityTrimmer
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Activity filter.
     */
    private DomainMapper<List<Long>, Collection<ActivitySecurityDTO>> securityMapper;

    /**
     * Mapper to get the list of group ids that includes private groups the current user can see activity for.
     */
    private GetPrivateCoordinatedAndFollowedGroupIdsForUser getVisibleGroupsForUserMapper;

    /**
     * Constructor.
     * 
     * @param inSecurityMapper
     *            security mapper.
     * @param inGetVisibleGroupsForUserMapper
     *            group visible mapper.
     */
    public ActivitySecurityTrimmer(final DomainMapper<List<Long>, Collection<ActivitySecurityDTO>> inSecurityMapper,
            final GetPrivateCoordinatedAndFollowedGroupIdsForUser inGetVisibleGroupsForUserMapper)
    {
        securityMapper = inSecurityMapper;
        getVisibleGroupsForUserMapper = inGetVisibleGroupsForUserMapper;
    }

    /**
     * Trim activities that the user does not have permission to see.
     * 
     * @param activityIds
     *            the activityIDs.
     * @param userPersonId
     *            the user's id.
     * @return the activityIDs that the user has permission to see.
     */
    public List<Long> trim(final List<Long> activityIds, final Long userPersonId)
    {
        Set<Long> accessibleGroupIds = getVisibleGroupsForUserMapper.execute(userPersonId);

        final Collection<ActivitySecurityDTO> securityDTOs = securityMapper.execute(activityIds);

        final List<Long> visibleActivities = new LinkedList<Long>();

        for (ActivitySecurityDTO actSec : securityDTOs)
        {
            if (actSec.getExists()
                    && (actSec.isDestinationStreamPublic() || accessibleGroupIds.contains(actSec
                            .getDestinationEntityId())))
            {
                log.debug("Activity with ID permitted: " + actSec.getId());
                visibleActivities.add(actSec.getId());
            }
            else
            {
                log.debug("Activity with ID NOT permitted: " + actSec.getId());
            }
        }

        // Preserve order
        final List<Long> orderedActivities = new LinkedList<Long>();

        for (int i = 0; i < activityIds.size(); i++)
        {
            if (visibleActivities.contains(activityIds.get(i)))
            {
                orderedActivities.add(activityIds.get(i));
            }
        }

        return orderedActivities;
    }
}
