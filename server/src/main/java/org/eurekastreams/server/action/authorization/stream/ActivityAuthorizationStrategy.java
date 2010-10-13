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

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.ActivityRestrictionEntity;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.ActivityInteractionType;
import org.eurekastreams.server.service.actions.strategies.activity.ActivityDTOFromParamsStrategy;
import org.eurekastreams.server.service.actions.strategies.activity.ActorRetrievalStrategy;

/**
 * Authorization Strategy for an Activity - Determines if user has permission to modify (Post|Comment|View on) an
 * activity.
 */
public class ActivityAuthorizationStrategy implements AuthorizationStrategy<ServiceActionContext>
{
    // TODO refactoring commenting to be able to tell the activityAuthorization strat that it is a comment. This will
    // get rid of the two entity mapper and can be replaced with the generic version.

    /**
     * Local logger instance.
     */
    private Log logger = LogFactory.make();

    /**
     * Groups by shortName DAO.
     */
    private final GetDomainGroupsByShortNames groupByShortNameDAO;

    /**
     * People by accountId DAO.
     */
    private final GetPeopleByAccountIds personByAccountDAO;

    /**
     * Group follower ids DAO.
     */
    private final DomainMapper<Long, List<Long>> groupFollowersDAO;

    /**
     * Local instance of actor retrieval strategy for determining who made the request to interact with the activity.
     */
    private final ActorRetrievalStrategy actorRetrievalStrategy;

    /**
     * Strategy for getting ActivityDTO from incoming params array.
     */
    @SuppressWarnings("unchecked")
    private final ActivityDTOFromParamsStrategy activityDTOStrategy;

    /**
     * The mapper to get all coordinators of a group.
     */
    private final GetAllPersonIdsWhoHaveGroupCoordinatorAccess groupCoordMapper;

    /**
     * The type of Action you are taking on an activity.
     */
    private final ActivityInteractionType type;

    /**
     * Constructor.
     * 
     * @param inGroupByShortNameDAO
     *            Groups by shortName DAO.
     * @param inGroupFollowersDAO
     *            Group follower ids DAO.
     * @param inActorRetrievalStrategy
     *            Actor retrieval strategy.
     * @param inGroupCoordMapper
     *            GetAllPersonIdsWhoHaveGroupCoordinatorAccess mapper instance.
     * @param inActivityDTOStrategy
     *            ActivityDTOFromParamsStrategy instance.
     * @param inType
     *            The type of interaction on an activity.
     * @param inPersonByAccountDAO
     *            DAO to get a person by nt account.
     */
    @SuppressWarnings("unchecked")
    public ActivityAuthorizationStrategy(final GetDomainGroupsByShortNames inGroupByShortNameDAO,
            final DomainMapper<Long, List<Long>> inGroupFollowersDAO,
            final ActorRetrievalStrategy inActorRetrievalStrategy,
            final GetAllPersonIdsWhoHaveGroupCoordinatorAccess inGroupCoordMapper,
            final ActivityDTOFromParamsStrategy inActivityDTOStrategy, final ActivityInteractionType inType,
            final GetPeopleByAccountIds inPersonByAccountDAO)
    {
        groupByShortNameDAO = inGroupByShortNameDAO;
        groupFollowersDAO = inGroupFollowersDAO;
        actorRetrievalStrategy = inActorRetrievalStrategy;
        groupCoordMapper = inGroupCoordMapper;
        activityDTOStrategy = inActivityDTOStrategy;
        personByAccountDAO = inPersonByAccountDAO;
        type = inType;
    }

    /**
     * Determines if user has permission to modify (Post|Comment|View on) an activity.
     * 
     * @param inActionContext
     *            the action context
     */
    @SuppressWarnings("unchecked")
    @Override
    public void authorize(final ServiceActionContext inActionContext)
    {
        ActivityDTO activity = null;
        try
        {
            activity = activityDTOStrategy.execute(inActionContext.getPrincipal(), inActionContext.getParams());
        }
        catch (Exception ex)
        {
            logger.error("Error occurred retrieving the activity dto params.", ex);
            throw new AuthorizationException(
                    "This action could not authorize the request due to failure retrieving parameters.", ex);
        }

        if (activity != null)
        {
            switch (activity.getDestinationStream().getType())
            {
            case PERSON:
                performPersonAuthorization(inActionContext.getPrincipal(), activity);
                break;
            case GROUP:
                performGroupAuthorization(inActionContext.getPrincipal(), activity);
                break;
            default:
                throw new AuthorizationException("This Action only accepts activities for accepted destination types.");
            }
        }

    }

    /**
     * Helper method to perform group authorization.
     * 
     * @param inUser
     *            - UserDetails of the user making the request.
     * @param inActivity
     *            - instance of ActivityDTO.
     */
    private void performGroupAuthorization(final Principal inUser, final ActivityDTO inActivity)
    {
        try
        {
            // get the group info from cache:
            StreamEntityDTO theStreamScope = inActivity.getDestinationStream();

            DomainGroupModelView cachedGroup = groupByShortNameDAO.execute(
                    Collections.singletonList(theStreamScope.getUniqueIdentifier())).get(0);

            long senderId = actorRetrievalStrategy.getActorId(inUser, inActivity);
            boolean isUserCoordinator = groupCoordMapper.execute(cachedGroup.getId()).contains(senderId);

            // if group is public check to see if the current stream interaction is allowed based on configuration,
            // if so then short-circuit.
            if (cachedGroup.isPublic())
            {
                if (isStreamInteractionAuthorized(cachedGroup, type) || isUserCoordinator)
                {
                    return;
                }
                else
                {
                    throw new AuthorizationException("Group is public but the poster is not a "
                            + "coordinator and the group is configured to not allow stream interaction: " + type);
                }
            }

            // The group is private, continue forward testing private group authorization.
            if (isUserCoordinator)
            {
                // user is a coordinator
                return;
            }

            if (groupFollowersDAO.execute(cachedGroup.getId()).contains(senderId) && cachedGroup.isStreamPostable())
            {
                // user is a follower
                return;
            }
        }
        catch (Exception ex)
        {
            logger.error("Error occurred authorizing the activity post.", ex);
        }
        throw new AuthorizationException("Current user does not have access right to modify activity.");
    }

    /**
     * Returns the Entity's configuration for stream posting or commenting based on the passed in entity context.
     * 
     * @param inEntity
     *            The entity to check.
     * @param inInteractionType
     *            The type of interaction to check if the Entity allows.
     * @return if the entity is restricted.
     */
    private boolean isStreamInteractionAuthorized(final ActivityRestrictionEntity inEntity,
            final ActivityInteractionType inInteractionType)
    {
        switch (inInteractionType)
        {
        case POST:
            return inEntity.isStreamPostable();
        case COMMENT:
            return inEntity.isCommentable();
        case VIEW:
            return true;
        default:
            throw new RuntimeException("Type of Activity not set.");
        }
    }

    /**
     * Helper method to perform Person authorization.
     * 
     * @param inUser
     *            - UserDetails of the user making the request.
     * @param inActivity
     *            - instance of ActivityDTO.
     */
    private void performPersonAuthorization(final Principal inUser, final ActivityDTO inActivity)
    {
        try
        {
            // Check to see if the stream is locked down and if they have permission to post to it.
            String targetStreamOwnerAccountId = inActivity.getDestinationStream().getUniqueIdentifier();

            PersonModelView targetStreamOwner = personByAccountDAO.execute(
                    Collections.singletonList(targetStreamOwnerAccountId)).get(0);

            boolean isActorTheStreamOwner = actorRetrievalStrategy.getActorAccountId(inUser, inActivity).equals(
                    targetStreamOwnerAccountId);

            // Test if the user is the owner of the stream being posted to or the stream
            // has been authorized for this type of interaction.
            if (isActorTheStreamOwner || isStreamInteractionAuthorized(targetStreamOwner, type))
            {
                return;
            }
        }
        catch (Exception ex)
        {
            logger.error("Error occurred authorizing the activity post.", ex);
        }
        throw new AuthorizationException("Current user does not have access rights to modify activity.");
    }

}
