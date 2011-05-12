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
package org.eurekastreams.server.persistence.strategies;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;

/**
 * Strategy for setting "deletable" property on ActivityDTO.
 */
public class ActivityDeletePropertyStrategy
{
    /** logger instance. */
    private static Log log = LogFactory.getLog(ActivityDeletePropertyStrategy.class);

    /**
     * DAO for looking up group by short name.
     */
    private GetDomainGroupsByShortNames groupByShortNameDAO;

    /**
     * The mapper to get all coordinators of a group.
     */
    private GetAllPersonIdsWhoHaveGroupCoordinatorAccess groupCoordinatorIdsDAO;

    /**
     * Mapper to get all system administrator ids.
     */
    private DomainMapper<Serializable, List<Long>> systemAdminIdsMapper;

    /**
     * Constructor.
     * 
     * @param inGroupByShortNameDAO
     *            DAO for looking up group by short name.
     * @param inGroupCoordinatorIdsDAO
     *            DAO for looking up group coordinators by group.
     * @param inSystemAdminIdsMapper
     *            Mapper to get all system admin ids.
     */
    public ActivityDeletePropertyStrategy(final GetDomainGroupsByShortNames inGroupByShortNameDAO,
            final GetAllPersonIdsWhoHaveGroupCoordinatorAccess inGroupCoordinatorIdsDAO,
            final DomainMapper<Serializable, List<Long>> inSystemAdminIdsMapper)
    {
        groupByShortNameDAO = inGroupByShortNameDAO;
        groupCoordinatorIdsDAO = inGroupCoordinatorIdsDAO;
        systemAdminIdsMapper = inSystemAdminIdsMapper;
    }

    /**
     * Sets Deletable property on activity for a given activity and user.
     * 
     * @param inUserAccountId
     *            The current user's account id.
     * @param inUserPersonId
     *            the current user's person id
     * @param inActivity
     *            The activity.
     */
    public void execute(final String inUserAccountId, final Long inUserPersonId, final ActivityDTO inActivity)
    {
        // short-circuit right if no userAccountId is provided.
        if (inUserAccountId == null || inUserPersonId == null)
        {
            inActivity.setDeletable(false);
            return;
        }

        // if current user is activity author, allow delete.
        if (inActivity.getActor().getType() == EntityType.PERSON
                && inUserAccountId.equalsIgnoreCase(inActivity.getActor().getUniqueIdentifier()))
        {
            inActivity.setDeletable(true);
            return;
        }

        // if activity is on current user's stream , then allow delete
        if (inActivity.getDestinationStream().getType() == EntityType.PERSON
                && inUserAccountId.equalsIgnoreCase(inActivity.getDestinationStream().getUniqueIdentifier()))
        {
            inActivity.setDeletable(true);
            return;
        }

        // if activity is on group stream, and current user is group coordinator, then allow delete
        if (inActivity.getDestinationStream().getType() == EntityType.GROUP
                && isCurrentUserGroupCoordinator(inUserPersonId, inActivity.getDestinationStream()
                        .getUniqueIdentifier()))
        {
            inActivity.setDeletable(true);
            return;
        }

        // if current user is admin - anything goes.
        List<Long> adminIds = systemAdminIdsMapper.execute(null);
        if (adminIds.contains(inUserPersonId))
        {
            inActivity.setDeletable(true);
            return;
        }

        // specifically set to false in case item was cached with value set.
        inActivity.setDeletable(false);
    }

    /**
     * Return true if user is coordinator of group activity is posted to, or authority to act as group coordinator.
     * 
     * @param inUserPersonId
     *            The current user's person id
     * @param inGroupShortName
     *            Short name of the group.
     * @return True if user is coordinator of group activity is posted to, or authority to act as group coordinator,
     *         false otherwise.
     */
    private boolean isCurrentUserGroupCoordinator(final Long inUserPersonId, final String inGroupShortName)
    {
        Long groupId = groupByShortNameDAO.fetchId(inGroupShortName);
        if (groupId == null)
        {
            String msg = "Unable to locate group with shortName: " + inGroupShortName;
            log.error(msg);
            throw new RuntimeException(msg);
        }

        return groupCoordinatorIdsDAO.hasGroupCoordinatorAccessRecursively(inUserPersonId, groupId);
    }

}
