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
package org.eurekastreams.server.service.utility.authorization;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.ActivityRestrictionEntity;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.ActivityInteractionType;

/**
 * Authorization Strategy for an Activity - Determines if user has permission to modify (Post|Comment|View on) an
 * activity.
 */
public class ActivityInteractionAuthorizationStrategy
{
    // TODO refactoring commenting to be able to tell the activityAuthorization strat that it is a comment. This will
    // get rid of the two entity mapper and can be replaced with the generic version.

    /** Log. */
    private final Log log = LogFactory.make();

    /** DAO to get person by ID. */
    private final DomainMapper<Long, PersonModelView> getPersonByIdDAO;

    /** DAO to get group by ID. */
    private final DomainMapper<Long, DomainGroupModelView> getGroupByIdDAO;

    /** DAO to get group follower IDs. */
    private final DomainMapper<Long, List<Long>> groupFollowersDAO;

    /** DAO to get all coordinators of a group. */
    private final GetAllPersonIdsWhoHaveGroupCoordinatorAccess groupCoordDAO;

    /** DAO to get all system administrators. */
    private final DomainMapper<Serializable, List<Long>> getSystemAdministratorIdsDAO;

    /**
     * Constructor.
     *
     * @param inGetPersonByIdDAO
     *            DAO to get person by ID.
     * @param inGetGroupByIdDAO
     *            DAO to get group by ID.
     * @param inGroupFollowersDAO
     *            DAO to get group follower IDs.
     * @param inGroupCoordDAO
     *            DAO to get all coordinators of a group.
     * @param inGetSystemAdministratorIdsDAO
     *            DAO to get all system administrators.
     */
    public ActivityInteractionAuthorizationStrategy(final DomainMapper<Long, PersonModelView> inGetPersonByIdDAO,
            final DomainMapper<Long, DomainGroupModelView> inGetGroupByIdDAO,
            final DomainMapper<Long, List<Long>> inGroupFollowersDAO,
            final GetAllPersonIdsWhoHaveGroupCoordinatorAccess inGroupCoordDAO,
            final DomainMapper<Serializable, List<Long>> inGetSystemAdministratorIdsDAO)
    {
        getPersonByIdDAO = inGetPersonByIdDAO;
        getGroupByIdDAO = inGetGroupByIdDAO;
        groupFollowersDAO = inGroupFollowersDAO;
        groupCoordDAO = inGroupCoordDAO;
        getSystemAdministratorIdsDAO = inGetSystemAdministratorIdsDAO;
    }

    /**
     * Determines if user has permission to interact with (post/comment/view) an activity.
     *
     * @param userId
     *            Person ID of user attempting the interaction.
     * @param activity
     *            Activity being interacted with.
     * @param interactionType
     *            Type of interaction.
     * @return true if allowed, false if not.
     */
    public boolean authorize(final long userId, final ActivityDTO activity,
            final ActivityInteractionType interactionType)
    {
        switch (activity.getDestinationStream().getType())
        {
        case PERSON:
            return authorizeForPersonStream(userId, activity, interactionType);
        case GROUP:
            return authorizeForGroupStream(userId, activity, interactionType);
        case RESOURCE:
            // anyone can post comment to resource stream activity.
            return true;
        default:
            return false;
        }
    }

    /**
     * Determines if the given entity allows the given type of interaction with activities in its stream.
     *
     * @param entity
     *            Entity containing a stream.
     * @param interactionType
     *            Type of interaction.
     * @return true if allowed, false if not.
     */
    private boolean isStreamInteractionAuthorized(final ActivityRestrictionEntity entity,
            final ActivityInteractionType interactionType)
    {
        switch (interactionType)
        {
        case POST:
            return entity.isStreamPostable();
        case COMMENT:
            return entity.isCommentable();
        case VIEW:
            return true;
        default:
            throw new RuntimeException("Unknown/unhandled activity interaction type.");
        }
    }

    /**
     * Performs authorization for personal streams.
     *
     * @param userId
     *            Person ID of user attempting the interaction.
     * @param activity
     *            Activity being interacted with.
     * @param interactionType
     *            Type of interaction.
     * @return true if allowed, false if not.
     */
    private boolean authorizeForPersonStream(final long userId, final ActivityDTO activity,
            final ActivityInteractionType interactionType)
    {
        try
        {
            // check if the user is the owner of the stream being posted to
            final long streamOwnerId = activity.getDestinationStream().getEntityId();
            if (streamOwnerId == userId)
            {
                return true;
            }

            // check if the stream has been authorized for this type of interaction
            PersonModelView targetStreamOwner = getPersonByIdDAO.execute(streamOwnerId);
            if (isStreamInteractionAuthorized(targetStreamOwner, interactionType))
            {
                return true;
            }

            // system admins are allowed to do anything
            return getSystemAdministratorIdsDAO.execute(null).contains(userId);
        }
        catch (Exception ex)
        {
            log.error("Error occurred authorizing the activity interaction.", ex);
            return false;
        }
    }

    /**
     * Performs authorization for group streams.
     *
     * @param userId
     *            Person ID of user attempting the interaction.
     * @param inActivity
     *            Activity being interacted with.
     * @param interactionType
     *            Type of interaction.
     * @return true if allowed, false if not.
     */
    private boolean authorizeForGroupStream(final long userId, final ActivityDTO inActivity,
            final ActivityInteractionType interactionType)
    {
        try
        {
            final long groupId = inActivity.getDestinationStream().getEntityId();
            DomainGroupModelView group = getGroupByIdDAO.execute(groupId);
            if (group.isPublic())
            {
                // first see if user is allowed to take action in general
                if (isStreamInteractionAuthorized(group, interactionType))
                {
                    return true;
                }
            }
            else
            {
                // first see if user is allowed to take action in general, then verify they are a member of the group
                if (isStreamInteractionAuthorized(group, interactionType)
                        && groupFollowersDAO.execute(groupId).contains(userId))
                {
                    return true;
                }
            }

            // if user is a group coordinator or a system admin, they're allowed to do anything
            // Note that this DAO also includes the list of system admins (not just the specified group coordinators)
            return groupCoordDAO.execute(groupId).contains(userId);
        }
        catch (Exception ex)
        {
            log.error("Error occurred authorizing the activity interaction.", ex);
            return false;
        }
    }
}
