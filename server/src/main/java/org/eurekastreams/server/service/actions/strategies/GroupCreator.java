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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.DefaultPrincipal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusByGroupCreatorRequest;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;

/**
 * Group Creator.
 */
public class GroupCreator extends GroupPersister
{

    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Key value for group shortName.
     */
    private static final String SHORTNAME_KEY = "shortName";

    /**
     * Message for duplicate group short name.
     */
    private static final String DUP_SHORTNAME_MSG = "Group short name already present in system.";

    /**
     * Strategy for adding group followers (coordinators are automatically added as followers/members).
     */
    private TaskHandlerExecutionStrategy followStrategy;

    /**
     * used to lookup person creating the group.
     */
    private final PersonMapper personMapper;

    /**
     * Mapper to get all the system administrator ids.
     */
    private DomainMapper<Serializable, List<Long>> getSystemAdministratorIdsMapper;

    /**
     * Mapper to get system settings.
     */
    private DomainMapper<MapperRequest, SystemSettings> getSystemSettingsMapper;

    /**
     * Constructor.
     * 
     * @param inGroupMapper
     *            The Group Mapper
     * @param inPersonMapper
     *            used to lookup person creating the group.
     * @param inGetSystemAdministratorIdsMapper
     *            mapper to get system administrator ids
     * @param inFollowStrategy
     *            used to automatically add coordinators as group followers/members.
     * @param inGetSystemSettingsMapper
     *            mapper to get the system settings
     */
    public GroupCreator(final DomainGroupMapper inGroupMapper, final PersonMapper inPersonMapper,
            final DomainMapper<Serializable, List<Long>> inGetSystemAdministratorIdsMapper,
            final TaskHandlerExecutionStrategy inFollowStrategy,
            final DomainMapper<MapperRequest, SystemSettings> inGetSystemSettingsMapper)
    {
        super(inGroupMapper);

        personMapper = inPersonMapper;
        followStrategy = inFollowStrategy;
        getSystemAdministratorIdsMapper = inGetSystemAdministratorIdsMapper;
        getSystemSettingsMapper = inGetSystemSettingsMapper;
    }

    /**
     * Returns DomainGroup based on id passed in inFields.
     * 
     * @param inActionContext
     *            The action context.
     * @param inFields
     *            the property map.
     * @return new group.
     */
    @Override
    public DomainGroup get(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields)
    {
        // create the group
        DomainGroup group = new DomainGroup();

        StreamScope groupScope = new StreamScope(ScopeType.GROUP, (String) inFields.get("shortName"));
        group.setStreamScope(groupScope);

        // set the capabilities as a new list to avoid search indexing problems
        group.setCapabilities(new ArrayList<BackgroundItem>());

        return group;
    }

    /**
     * Persists new group object.
     * 
     * @param inGroup
     *            The group.
     * @param inFields
     *            the property map.
     * @param inActionContext
     *            the action context
     * @throws Exception
     *             on error
     */
    @Override
    public void persist(final TaskHandlerActionContext<PrincipalActionContext> inActionContext,
            final Map<String, Serializable> inFields, final DomainGroup inGroup) throws Exception
    {
        ValidationException ve = new ValidationException();
        String creatorUserName = inActionContext.getActionContext().getPrincipal().getAccountId();
        Long creatorPersonId = inActionContext.getActionContext().getPrincipal().getId();
        SystemSettings settings = getSystemSettingsMapper.execute(null);

        // Verify that group with given short name doesn't already exist.
        if (getGroupMapper().findByShortName(inGroup.getShortName()) != null)
        {
            ve.addError(SHORTNAME_KEY, DUP_SHORTNAME_MSG);
        }

        // Verify that group has on coordinator.
        if (inGroup.getCoordinators().isEmpty())
        {
            ve.addError("coordinators", "Group must have at least one coordinator");
        }

        if (!ve.getErrors().isEmpty())
        {
            throw ve;
        }

        // if the system requires approval to create groups, set the pending state to true.
        boolean isPending = true;
        if (settings.getAllUsersCanCreateGroups()
                || getSystemAdministratorIdsMapper.execute(null).contains(creatorPersonId))
        {
            isPending = false;
        }
        inGroup.setPending(isPending);

        // Set the current user to the createdby person.
        inGroup.setCreatedBy(personMapper.findByAccountId(creatorUserName));

        getGroupMapper().insert(inGroup);

        // sets the destination entity id for the group's stream scope
        inGroup.getStreamScope().setDestinationEntityId(inGroup.getId());

        queueAsyncAction(inActionContext, inGroup, false);

        // Make all coordinators follow/join the new group
        for (Person coordinator : inGroup.getCoordinators())
        {
            SetFollowingStatusByGroupCreatorRequest currentRequest = new SetFollowingStatusByGroupCreatorRequest(
                    coordinator.getId(), inGroup.getId(), Follower.FollowerStatus.FOLLOWING, inGroup.getName(),
                    isPending);
            ServiceActionContext currentContext = new ServiceActionContext(currentRequest, new DefaultPrincipal(
                    creatorUserName, inActionContext.getActionContext().getPrincipal().getOpenSocialId(),
                    inActionContext.getActionContext().getPrincipal().getId()));
            TaskHandlerActionContext<PrincipalActionContext> currentTaskHandlerActionContext =
            // line break
            new TaskHandlerActionContext<PrincipalActionContext>(currentContext, inActionContext
                    .getUserActionRequests());
            followStrategy.execute(currentTaskHandlerActionContext);
        }

        // trigger notification if group will be pending approval
        if (isPending)
        {
            CreateNotificationsRequest request = new CreateNotificationsRequest(RequestType.REQUEST_NEW_GROUP,
                    inActionContext.getActionContext().getPrincipal().getId(), 0, inGroup.getId());
            inActionContext.getUserActionRequests().add(
                    new UserActionRequest("createNotificationsAction", null, request));
        }
    }
}
