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
package org.eurekastreams.server.action.authorization.stream;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.strategies.ActivityDeletePropertyStrategy;

/**
 * Authorization strategy for deleting an activity.
 *
 */
public class DeleteActivityAuthorization implements AuthorizationStrategy<PrincipalActionContext>
{
    /** logger instance. */
    private static Log log = LogFactory.make();

    /**
     * DAO for looking up activity by id.
     */
    private DomainMapper<List<Long>, List<ActivityDTO>>  activityDAO;

    /**
     * Strategy used to set deletable property of an activityDTO.
     */
    private ActivityDeletePropertyStrategy activityDeletePropertySetter;

    /**
     * Constructor.
     *
     * @param inActivityByIdDAO
     *            DAO for looking up activity by id.
     * @param inActivityDeletePropertySetter
     *            Strategy used to set deletable property of an activityDTO.
     */
    public DeleteActivityAuthorization(final DomainMapper<List<Long>, List<ActivityDTO>>  inActivityByIdDAO,
            final ActivityDeletePropertyStrategy inActivityDeletePropertySetter)
    {
        activityDAO = inActivityByIdDAO;
        activityDeletePropertySetter = inActivityDeletePropertySetter;
    }

    /**
     * Authorize.
     *
     * @param inActionContext
     *            {@link PrincipalActionContext}.
     */
    @Override
    public void authorize(final PrincipalActionContext inActionContext)
    {
        Long activityId = (Long) inActionContext.getParams();
        ActivityDTO activity = getActivityById(activityId);
        try
        {
            String currentUserAccountId = inActionContext.getPrincipal().getAccountId();
            Long currentUserId = inActionContext.getPrincipal().getId();
            activityDeletePropertySetter.execute(currentUserAccountId, currentUserId, activity);
        }
        catch (Exception ex)
        {
            log.error("Error occurred determining access rights for activity delete.", ex);
            throw new AuthorizationException("Unable to determine access rights.");
        }

        // If unable to delete, throw access exception.
        if (!activity.isDeletable())
        {
            // if you get to this point, "No soup for you!".
            throw new AuthorizationException("Current user does not have permissions to modify activity id: "
                    + activityId);
        }
    }

    /**
     * Get {@link ActivityDTO}.
     *
     * @param inActivityId
     *            The activity id.
     * @return {@link ActivityDTO}.
     */
    private ActivityDTO getActivityById(final long inActivityId)
    {
        List<ActivityDTO> activities = activityDAO.execute(Arrays.asList(inActivityId));
        if (activities.size() == 0)
        {
            log.error("Unable to locate activity with id: " + inActivityId);
            throw new AuthorizationException("Current user does not have permissions to delete activity id: "
                    + inActivityId);
        }
        return activities.get(0);
    }

}
