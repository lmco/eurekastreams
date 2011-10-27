/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.actions.strategies;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.InlineExecutionStrategyExecutor;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.DefaultPrincipal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusByGroupCreatorRequest;
import org.eurekastreams.server.domain.DomainFormatUtility;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.hibernate.validator.InvalidStateException;
import org.hibernate.validator.InvalidValue;

/**
 * Class to update a Group.
 *
 */
public class GroupUpdater extends GroupPersister
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * The key to use to store the original domain group name into the fields map between Get and Persist.
     */
    private static final String ORIGINAL_GROUP_NAME_KEY = "__KEY_ORIGINAL_GROUP_NAME_KEY";

    /**
     * The key to use to store the original domain group coordinators into the fields map between Get and Persist.
     */
    private static final String ORIGINAL_GROUP_COORDINATORS_KEY = "__KEY_ORIGINAL_GROUP_COORDINATORS_KEY";

    /**
     * Strategy for adding group followers (coordinators are automatically added as followers/members).
     */
    private final TaskHandlerExecutionStrategy followStrategy;

    /**
     * Mapper to clear the existing coordinators and followers search text.
     */
    private final DomainMapper<Long, Void> clearActivityStreamSearchStringForUsersMapper;

    /**
     * Constructor.
     *
     * @param inGroupMapper
     *            The group mapper.
     * @param inAccessCheckerMapper
     *            mapper to determine permissions
     * @param inClearActivityStreamSearchStringForUsersMapper
     *            the mapper to clear out activity search strings for users
     * @param inFollowStrategy
     *            used to automatically add coordinators as group followers/members.
     */
    public GroupUpdater(final DomainGroupMapper inGroupMapper,
            final GetAllPersonIdsWhoHaveGroupCoordinatorAccess inAccessCheckerMapper,
            final DomainMapper<Long, Void> inClearActivityStreamSearchStringForUsersMapper,
            final TaskHandlerExecutionStrategy inFollowStrategy)
    {
        super(inGroupMapper);
        clearActivityStreamSearchStringForUsersMapper = inClearActivityStreamSearchStringForUsersMapper;
        followStrategy = inFollowStrategy;
    }

    /**
     * Returns Group base on id passed in inFields.
     *
     * @param inActionContext
     *            action context
     * @param inFields
     *            the property map.
     * @return group base on id passed in inFields.
     */
    @Override
    public DomainGroup get(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields)
    {
        long id = Long.parseLong(inFields.get("id").toString());
        DomainGroup entity = getGroupMapper().findById(id);

        // store the original domain group name between get and persist
        inFields.put(ORIGINAL_GROUP_NAME_KEY, entity.getName());
        inFields.put(ORIGINAL_GROUP_COORDINATORS_KEY, (Serializable) entity.getCoordinators());

        // clear out the search text for the group coordinators now, before we
        // commit to the db
        if (!entity.isPublicGroup())
        {
            clearActivityStreamSearchStringForUsersMapper.execute(entity.getId());
        }

        return entity;
    }

    /**
     * Updates the Group Data.
     *
     * @param inActionContext
     *            The action context.
     * @param inFields
     *            The property map.
     * @param inGroup
     *            The group.
     * @throws Exception
     *             If error occurs.
     */
    @Override
    public void persist(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields, final DomainGroup inGroup) throws Exception
    {
        try
        {
            inGroup.setCapabilities(DomainFormatUtility.splitCapabilitiesString((String) inFields
                    .get(DomainGroupModelView.KEYWORDS_KEY)));

            String creatorUserName = inActionContext.getActionContext().getPrincipal().getAccountId();

            Set<Person> oldCoordinators = (Set<Person>) inFields.get(ORIGINAL_GROUP_COORDINATORS_KEY);

            Set<Person> newCoordinators = new HashSet<Person>();
            newCoordinators.addAll(inGroup.getCoordinators());
            newCoordinators.removeAll(oldCoordinators);

            // Make all coordinators follow/join the new group
            for (Person coordinator : newCoordinators)
            {
                SetFollowingStatusByGroupCreatorRequest currentRequest = new SetFollowingStatusByGroupCreatorRequest(
                        coordinator.getId(), inGroup.getId(), Follower.FollowerStatus.FOLLOWING, inGroup.getName(),
                        inGroup.getShortName(), false);
                new InlineExecutionStrategyExecutor().execute(followStrategy, currentRequest, new DefaultPrincipal(
                        creatorUserName, inActionContext.getActionContext().getPrincipal().getOpenSocialId(),
                        inActionContext.getActionContext().getPrincipal().getId()), inActionContext
                        .getUserActionRequests());
            }

            getGroupMapper().flush();
            queueAsyncAction(inActionContext, inGroup, true);

            // see if the group name changed - if so, kick off a task to update all activities that were posted to the
            // group
            String previousGroupName = (String) inFields.get(ORIGINAL_GROUP_NAME_KEY);
            String newGroupName = inGroup.getName();
            if (previousGroupName == null || !previousGroupName.equals(newGroupName))
            {
                log.info("The name for domain group with short name " + inGroup.getShortName()
                        + " has been changed from '" + previousGroupName + "' to '" + newGroupName
                        + "'.  Queuing up activityRecipientDomainGroupNameUpdaterAsyncAction async task to update "
                        + "all activities posted to this group.");

                // group name updated - kick off a task to update all activities posted to the group
                inActionContext.getUserActionRequests().add(
                        new UserActionRequest("activityRecipientDomainGroupNameUpdaterAsyncAction", null, inGroup
                                .getShortName()));
            }
        }
        catch (InvalidStateException e)
        {
            log.error("Failed to persist Group", e);
            InvalidValue[] invalidValues = e.getInvalidValues();
            ValidationException validationException = new ValidationException();

            for (InvalidValue invalidValue : invalidValues)
            {
                validationException.addError(invalidValue.getPropertyName(), invalidValue.getMessage());
            }

            throw validationException;
        }
        catch (PersistenceException e)
        {
            log.error("Failed to persist Group", e);
        }
    }
}
