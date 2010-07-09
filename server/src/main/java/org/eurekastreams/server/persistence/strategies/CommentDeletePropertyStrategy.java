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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.persistence.mappers.GetRecursiveOrgCoordinators;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.CommentDTO;

/**
 * Strategy for setting Deletable property on comments for a given activity and
 * user.
 * 
 */
public class CommentDeletePropertyStrategy
{
    /** logger instance. */
    private static Log log = LogFactory
            .getLog(CommentDeletePropertyStrategy.class);

    /**
     * Mapper to get person info.
     */
    private GetPeopleByAccountIds personByAccountIdDAO;

    /**
     * DAO for looking up group by short name.
     */
    private GetDomainGroupsByShortNames groupByShortNameDAO;

    /**
     * Mapper to check if the user has coordinator access to a group.
     */
    private GetAllPersonIdsWhoHaveGroupCoordinatorAccess groupAccessMapper;    
    
    /**
     * Mapper to get all coordinators of an org.
     */
    private GetRecursiveOrgCoordinators orgCoordinatorDAO;

    /**
     * Constructor.
     * 
     * @param inPersonByAccountIdDAO
     *            Mapper to get person info.
     * @param inGroupByShortNameDAO
     *            DAO for looking up group by short name.
     * @param inGroupAccessMapper
     *            Mapper to check if the user has coordinator access to a group.
     * @param inOrgCoordinatorDAO
     *            Mapper for determining org coordinators.
     */
    public CommentDeletePropertyStrategy(
            final GetPeopleByAccountIds inPersonByAccountIdDAO,
            final GetDomainGroupsByShortNames inGroupByShortNameDAO,
            final GetAllPersonIdsWhoHaveGroupCoordinatorAccess inGroupAccessMapper,
            final GetRecursiveOrgCoordinators inOrgCoordinatorDAO)
    {
        personByAccountIdDAO = inPersonByAccountIdDAO;
        groupByShortNameDAO = inGroupByShortNameDAO;
        groupAccessMapper = inGroupAccessMapper;
        orgCoordinatorDAO = inOrgCoordinatorDAO;
    }

    /**
     * Sets Deletable property on comments for a given activity and user.
     * 
     * @param inUserAccountId
     *            The current user's account id.
     * @param inParentActivity
     *            The parent activity of the comments.
     * @param inComments
     *            The list of comments to set property on.
     */
    public void execute(final String inUserAccountId,
            final ActivityDTO inParentActivity,
            final List<CommentDTO> inComments)
    {
        // short-circuit right if no userAccountId is provided.
        if (inUserAccountId == null)
        {
            setAll(inComments, false);
            return;
        }

        //TODO This should be refactored to take advantage of ActivityDeletePropertyStrategy
        //as the activity based logic is currently duplicated. Both should probably be refactored
        //to use activity permission based decorators to determine setting for max flexibility.
        
        // if comment is on person's stream and current user is that person,
        // then allow
        if (inParentActivity.getDestinationStream().getType() == EntityType.PERSON
                && inUserAccountId.equalsIgnoreCase(inParentActivity
                        .getDestinationStream().getUniqueIdentifier()))
        {
            setAll(inComments, true);
            return;
        }

        // need user's person id
        Long userPersonId = getUserPersonIdByAccountId(inUserAccountId);

        // if comment is on group stream, and current user is group coordinator,
        // then allow
        if (inParentActivity.getDestinationStream().getType() == EntityType.GROUP
                && isCurrentUserCoordinator(userPersonId, inParentActivity
                        .getDestinationStream().getUniqueIdentifier()))
        {
            setAll(inComments, true);
            return;
        }
        
        //if activity is on personal stream, and current user is org coordinator of personal 
        //stream's parent org (or up tree recursively), allow delete.
        if (inParentActivity.getDestinationStream().getType() == EntityType.PERSON
                && orgCoordinatorDAO.isOrgCoordinatorRecursively(
                        userPersonId,
                        personByAccountIdDAO.fetchUniqueResult(
                                inParentActivity.getDestinationStream().getUniqueIdentifier())
                                    .getParentOrganizationId()))
        {
            setAll(inComments, true);
            return;
            
        }

        // No bulk settings apply. If user is author of comment, allow delete
        for (CommentDTO comment : inComments)
        {
            // if user is author of comment they can delete.
            comment.setDeletable(comment.getAuthorId() == userPersonId);
        }
    }

    /**
     * Set deletable property to true on all comments.
     * 
     * @param inComments
     *            The comments to set.
     * @param inValueToSet
     *            the value to set on all comments.
     */
    private void setAll(final List<CommentDTO> inComments, final boolean inValueToSet)
    {
        for (CommentDTO comment : inComments)
        {
            comment.setDeletable(inValueToSet);
        }
    }

    /**
     * Return true if user is coordinator of group activity is posted to, or
     * authority to act as group coordinator.
     * 
     * @param inUserPersonId
     *            The current user's person id
     * @param inGroupShortName
     *            Short name of the group.
     * @return True if user is coordinator of group activity is posted to, or
     *         authority to act as group coordinator, false otherwise.
     */
    private boolean isCurrentUserCoordinator(final Long inUserPersonId,
            final String inGroupShortName)
    {
        Long groupId = groupByShortNameDAO.fetchId(inGroupShortName);
        if (groupId == null)
        {
            String msg = "Unable to locate group with shortName: "
                    + inGroupShortName;
            log.error(msg);
            throw new RuntimeException(msg);
        }

        return groupAccessMapper.hasGroupCoordinatorAccessRecursively(
                inUserPersonId, groupId);
    }

    /**
     * Returns user's person id.
     * 
     * @param inCurrentUserAccountId
     *            Account id.
     * @return user's person id
     */
    private Long getUserPersonIdByAccountId(final String inCurrentUserAccountId)
    {
        Long personId = personByAccountIdDAO.fetchId(inCurrentUserAccountId);
        if (personId == null)
        {
            String msg = "Unable to locate user with account id: "
                    + inCurrentUserAccountId;
            log.error(msg);
            throw new RuntimeException(msg);
        }
        return personId;
    }

}
