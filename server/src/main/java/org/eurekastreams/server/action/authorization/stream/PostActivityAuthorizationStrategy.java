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

import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Authorization Strategy for Posting an activity.
 * 
 */
public class PostActivityAuthorizationStrategy implements AuthorizationStrategy<ServiceActionContext>
{
    /**
     * Local logger instance.
     */
    private final Log logger = LogFactory.make();

    /**
     * Local instance of the {@link GetDomainGroupsByShortNames} mapper.
     */
    private final GetDomainGroupsByShortNames domainGroupsByShortNameMapper;

    /**
     * Mapper to get a PersonModelView from an accountid.
     */
    private final DomainMapper<String, PersonModelView> getPersonModelViewByAccountIdMapper;

    /**
     * The mapper to get all coordinators of a group.
     */
    private final GetAllPersonIdsWhoHaveGroupCoordinatorAccess groupCoordMapper;

    /**
     * Group follower ids DAO.
     */
    private final DomainMapper<Long, List<Long>> groupFollowersDAO;

    /**
     * Constructor for the PostActivityAuthorizationStrategy.
     * 
     * @param inDomainGroupsByShortNameMapper
     *            - instance of the {@link GetDomainGroupsByShortNames} mapper.
     * @param inGroupCoordMapper
     *            - instance of the {@link GetAllPersonIdsWhoHaveGroupCoordinatorAccess} mapper.
     * @param inGroupFollowersDAO
     *            - instance of the {@link GetGroupFollowerIds} mapper.
     * @param inGetPersonModelViewByAccountIdMapper
     *            mapper to get a personmodelview by accountid
     */
    public PostActivityAuthorizationStrategy(final GetDomainGroupsByShortNames inDomainGroupsByShortNameMapper,
            final GetAllPersonIdsWhoHaveGroupCoordinatorAccess inGroupCoordMapper,
            final DomainMapper<Long, List<Long>> inGroupFollowersDAO,
            final DomainMapper<String, PersonModelView> inGetPersonModelViewByAccountIdMapper)
    {
        domainGroupsByShortNameMapper = inDomainGroupsByShortNameMapper;
        groupCoordMapper = inGroupCoordMapper;
        groupFollowersDAO = inGroupFollowersDAO;
        getPersonModelViewByAccountIdMapper = inGetPersonModelViewByAccountIdMapper;
    }

    /**
     * Check the DestinationStream type and perform the appropriate authorization strategy. {@inheritDoc}.
     */
    public void authorize(final ServiceActionContext inActionContext)
    {
        Principal currentPrincipal = inActionContext.getPrincipal();
        PostActivityRequest currentRequest = (PostActivityRequest) inActionContext.getParams();

        switch (currentRequest.getActivityDTO().getDestinationStream().getType())
        {
        case PERSON:
            if (logger.isTraceEnabled())
            {
                logger.trace("Performing authorization to post to a Person based stream: "
                        + currentRequest.getActivityDTO().getDestinationStream().getUniqueIdentifier());
            }
            performPersonAuthorization(currentPrincipal, currentRequest.getActivityDTO());
            break;
        case GROUP:
            if (logger.isTraceEnabled())
            {
                logger.trace("Performing authorization to post to a Group based stream: "
                        + currentRequest.getActivityDTO().getDestinationStream().getUniqueIdentifier());
            }
            performGroupAuthorization(currentPrincipal, currentRequest.getActivityDTO());
            break;
        case RESOURCE:
            // anyone can post to resource stream.
            break;
        default:
            logger.error("Error occurred Performing authorization to post an Activity to stream, unsupported"
                    + "destination stream type.");
            throw new AuthorizationException("Unsupported Destination Stream Type.");

        }
    }

    /**
     * Helper method to perform Authorization on activity when the destination stream is a Person type.
     * 
     * @param inPrincipal
     *            - current {@link Principal} for the Action Context.
     * @param inActivityDTO
     *            - current {@link ActivityDTO} to be posted.
     */
    private void performPersonAuthorization(final Principal inPrincipal, final ActivityDTO inActivityDTO)
    {
        PersonModelView currentPerson = getPersonModelViewByAccountIdMapper.execute(inActivityDTO
                .getDestinationStream().getUniqueIdentifier());

        if (currentPerson == null)
        {
            throw new AuthorizationException("Cannot locate current person.  The activity destination stream id is "
                    + inActivityDTO.getDestinationStream().getUniqueIdentifier());
        }

        boolean isActorTheStreamOwner = inPrincipal.getAccountId().equalsIgnoreCase(
                inActivityDTO.getDestinationStream().getUniqueIdentifier());

        // Test if the user is the owner of the stream being posted to or the stream
        // has been authorized for this type of interaction.
        if (isActorTheStreamOwner || currentPerson.isStreamPostable())
        {
            return;
        }

        throw new AuthorizationException("Current user does not have access rights to post this activity.");
    }

    /**
     * Helper method to perform Authorization on activity when the destination stream is a Group type.
     * 
     * @param inPrincipal
     *            - current {@link Principal} for the Action Context.
     * @param inActivityDTO
     *            - current {@link ActivityDTO} to be posted.
     */
    private void performGroupAuthorization(final Principal inPrincipal, final ActivityDTO inActivityDTO)
    {
        DomainGroupModelView currentDomainGroup = domainGroupsByShortNameMapper.fetchUniqueResult(inActivityDTO
                .getDestinationStream().getUniqueIdentifier());

        boolean isUserCoordinator = groupCoordMapper.execute(currentDomainGroup.getEntityId()).contains(
                inPrincipal.getId());

        // if group is public check to see if the current stream interaction is allowed based on configuration,
        // if so then short-circuit.
        if (currentDomainGroup.isPublic())
        {
            if (currentDomainGroup.isStreamPostable() || isUserCoordinator)
            {
                return;
            }
            else
            {
                throw new AuthorizationException("Group is public but the poster is not a "
                        + "coordinator and the group is configured to not allow stream posts.");
            }
        }

        // The group is private, continue forward testing private group authorization.

        if (isUserCoordinator)
        {
            // user is a coordinator
            return;
        }

        if (groupFollowersDAO.execute(currentDomainGroup.getEntityId()).contains(inPrincipal.getId())
                && currentDomainGroup.isStreamPostable())
        {
            // user is a follower
            return;
        }

        throw new AuthorizationException("Current user does not have access rights to post this activity.");
    }
}
