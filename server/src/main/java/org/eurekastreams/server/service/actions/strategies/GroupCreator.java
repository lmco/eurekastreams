/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
import java.util.Map;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.DefaultPrincipal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.action.request.profile.SetFollowingStatusByGroupCreatorRequest;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.Follower;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverser;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverserBuilder;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.cache.OrganizationHierarchyCache;

/**
 * Organization Creator.
 */
public class GroupCreator extends GroupPersister
{
    /**
     * Key value for organization shortName.
     */
    private static final String SHORTNAME_KEY = "shortName";

    /**
     * Message for duplicate organization short name.
     */
    private static final String DUP_SHORTNAME_MSG = "Group short name already present in system.";

    /** Key value for parent organization field. */
    private static final String PARENT_ORG_KEY = "orgParent";

    /**
     * The organization hierarchy traverser builder - needed because this class is reused by all threads, we can't share
     * OrganizationHierarchyTraversers.
     */
    private final OrganizationHierarchyTraverserBuilder orgTraverserBuilder;

    /**
     * Organization cache to look up parent orgs.
     */
    private final OrganizationHierarchyCache orgHierarchyCache;

    /**
     * Strategy for adding group followers (coordinators are automatically added as followers/members).
     */
    private TaskHandlerExecutionStrategy followStrategy;

    /**
     * used to lookup person creating the group.
     */
    private final PersonMapper personMapper;

    /**
     * Constructor.
     *
     * @param inGroupMapper
     *            The Group Mapper
     * @param inOrgMapper
     *            The org mapper
     * @param inPersonMapper
     *            used to lookup person creating the group.
     * @param inOrgTraverserBuilder
     *            used to update the Org statistics.
     * @param inOrganizationHierarchyCache
     *            used to get the parent orgs.
     * @param inFollowStrategy
     *            used to automatically add coordinators as group followers/members.
     */
    public GroupCreator(final DomainGroupMapper inGroupMapper, final OrganizationMapper inOrgMapper,
            final PersonMapper inPersonMapper, final OrganizationHierarchyTraverserBuilder inOrgTraverserBuilder,
            final OrganizationHierarchyCache inOrganizationHierarchyCache,
            final TaskHandlerExecutionStrategy inFollowStrategy)
    {
        super(inGroupMapper, inOrgMapper);

        personMapper = inPersonMapper;
        orgTraverserBuilder = inOrgTraverserBuilder;
        orgHierarchyCache = inOrganizationHierarchyCache;
        followStrategy = inFollowStrategy;
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
        String parentOrgShortName;

        // create the group
        DomainGroup group = new DomainGroup();

        // set the parent org if specified (persist will catch as an error if not specified)
        parentOrgShortName = inFields.get(PARENT_ORG_KEY).toString();
        if (parentOrgShortName != null && !parentOrgShortName.isEmpty())
        {
            Organization parentOrg = getOrgMapper().findByShortName(parentOrgShortName);
            group.setParentOrganization(parentOrg);
        }

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

        // verify group has a parent org
        Organization parentOrg = inGroup.getParentOrganization();
        if (parentOrg == null)
        {
            ve.addError(PARENT_ORG_KEY, "Group must have a parent organization.");
        }

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

        // if the groups parent organization requires approval to create groups,
        // set the pending state to true.
        boolean isPending = !(parentOrg.getAllUsersCanCreateGroups() || isACoordinator(parentOrg, creatorUserName));
        inGroup.setPending(isPending);

        // Set the current user to the createdby person.
        inGroup.setCreatedBy(personMapper.findByAccountId(creatorUserName));

        getGroupMapper().insert(inGroup);
        
        // sets the destination entity id for the group's stream scope
        inGroup.getStreamScope().setDestinationEntityId(inGroup.getId());

        OrganizationHierarchyTraverser orgTraverser = orgTraverserBuilder.getOrganizationHierarchyTraverser();
        orgTraverser.traverseHierarchy(inGroup);
        getOrgMapper().updateOrganizationStatistics(orgTraverser);

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
                    inActionContext.getActionContext().getPrincipal().getId(), parentOrg.getId(), inGroup.getId());
            inActionContext.getUserActionRequests().add(
                    new UserActionRequest("createNotificationsAction", null, request));
        }
    }

    // TODO remove this if we refactor to change the domains org object's
    // isCoordinator method to recursively look.
    /**
     * Get whiter the user is a coordinator for this org or it's sub orgs.
     *
     * @param org
     *            The org object you are currently on.
     * @param accountId
     *            The account ID of the person you want to check.
     * @return a boolean is the account ID is a coordinator of this org or any of it's parents.
     */
    public boolean isACoordinator(final Organization org, final String accountId)
    {
        boolean isCoordinator = org.isCoordinator(accountId);

        for (Long subOrgId : orgHierarchyCache.getParentOrganizations(org.getId()))
        {
            if (isCoordinator)
            {
                break;
            }
            isCoordinator = getOrgMapper().findById(subOrgId).isCoordinator(accountId);
        }

        return isCoordinator;
    }
}
