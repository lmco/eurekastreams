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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.execution.profile.SetFollowingGroupStatusExecution;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.persistence.mappers.cache.ClearPrivateGroupIdsViewableByCoordinatorCacheOnGroupUpdate;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the CreateOrganizationAction.
 */
public class GroupUpdaterTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mocked person in user.
     */
    private Person userPerson = context.mock(Person.class);

    /**
     * task handler action context.
     */
    private TaskHandlerActionContext<PrincipalActionContext> taskHandlerActionContext = context
            .mock(TaskHandlerActionContext.class);

    /**
     * Action context.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Current user principal.
     */
    private Principal userPrincipal = context.mock(Principal.class);
    /**
     * Collection to hold action requests queued up for async processing.
     */
    private List<UserActionRequest> userActionRequests = new ArrayList<UserActionRequest>();

    /**
     * Account id of the current user.
     */
    private String accountId = "slkjfsdljf";

    /**
     * the user's person id.
     */
    private static final long USER_PERSON_ID = 3892872L;

    /**
     * Org short name.
     */
    private String origOrgShortName = "origOrgShortName";

    /**
     * Parent org mock.
     */
    private Organization origParentOrg = context.mock(Organization.class, "origParentOrg");

    /**
     * The mock group mapper to be used by the action.
     */
    private DomainGroupMapper groupMapperMock = context.mock(DomainGroupMapper.class);

    /**
     * Mapper to determine if a user has access to update a group.
     */
    private GetAllPersonIdsWhoHaveGroupCoordinatorAccess accessCheckerMapper = context
            .mock(GetAllPersonIdsWhoHaveGroupCoordinatorAccess.class);

    /**
     * The mock org mapper to be used by the action.
     */
    private OrganizationMapper orgMapperMock = context.mock(OrganizationMapper.class);

    /**
     * mocked mapper to clear user search strings.
     */
    private ClearPrivateGroupIdsViewableByCoordinatorCacheOnGroupUpdate activityStreamSearchClearer = context
            .mock(ClearPrivateGroupIdsViewableByCoordinatorCacheOnGroupUpdate.class);

    /**
     * Mocked following group strategy to add new group followers.
     */
    private final SetFollowingGroupStatusExecution followStrategyMock = context
            .mock(SetFollowingGroupStatusExecution.class);

    /**
     * The subject under test.
     */
    private GroupUpdater sut;

    /**
     * Setup sut for each test.
     */
    @Before
    public void setup()
    {
        sut = new GroupUpdater(groupMapperMock, orgMapperMock, accessCheckerMapper, activityStreamSearchClearer,
                followStrategyMock);

        userActionRequests = new ArrayList<UserActionRequest>();

        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getUserActionRequests();
                will(returnValue(userActionRequests));

                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(userPrincipal).getId();
                will(returnValue(USER_PERSON_ID));

                allowing(actionContext).getPrincipal();
                will(returnValue(userPrincipal));

                allowing(userPrincipal).getAccountId();
                will(returnValue(accountId));
            }
        });
    }

    /**
     * Build an organization based on the input form being fully filled out with valid data.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public void getSuccess() throws Exception
    {
        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        final long id = 1L;
        final Person testPerson = new Person();
        formData.put(DomainGroupModelView.ID_KEY, Long.toString(id));
        final DomainGroup expectedGroup = new DomainGroup("newOrg", "newOrg", testPerson);
        expectedGroup.setParentOrganization(origParentOrg);
        final String accessingUser = "jschmoe";
        expectedGroup.addCoordinator(new Person(accessingUser, "b", "c", "d", "e"));

        context.checking(new Expectations()
        {
            {
                oneOf(groupMapperMock).findById(with(any(Long.class)));
                will(returnValue(expectedGroup));

                allowing(origParentOrg).getShortName();
                will(returnValue(origOrgShortName));

                oneOf(activityStreamSearchClearer).execute(with(any(Long.class)));
            }
        });

        DomainGroup returnedGroup = sut.get(taskHandlerActionContext, formData);
        context.assertIsSatisfied();
        assertEquals(expectedGroup, returnedGroup);
    }

    /**
     * Build an organization based on the input form being fully filled out with valid data.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public void persistSuccessWithNoGroupNameUpdate() throws Exception
    {
        final DomainGroup group = new DomainGroup("Group Name", "shortName", new Person("id", "Homer", "Jay",
                "Simpson", "Homey"));

        final List<Person> attribMembers = new ArrayList<Person>();
        attribMembers.add(new Person("id", "Homer", "Jay", "Simpson", "Homey"));
        attribMembers.add(new Person("id", "Max", "X", "Power", "Homer"));

        group.setCoordinators(new HashSet(attribMembers));

        final long id = 1L;
        String newName = "NEW org name here";
        final String orgName = "TheParentOrgName";
        final Organization org = context.mock(Organization.class);
        group.setParentOrganization(org);

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put(DomainGroupModelView.ID_KEY, Long.toString(id));
        formData.put(DomainGroupModelView.NAME_KEY, newName);
        formData.put(DomainGroupModelView.KEYWORDS_KEY, "good,idea");
        formData.put(DomainGroupModelView.ORG_PARENT_KEY, orgName);
        formData.put("__KEY_ORIGINAL_PARENT_ORG_KEY", orgName);
        formData.put("__KEY_ORIGINAL_GROUP_NAME_KEY", "Group Name");
        formData.put("__KEY_ORIGINAL_GROUP_COORDINATORS_KEY", (Serializable) group.getCoordinators());

        context.checking(new Expectations()
        {
            {
                oneOf(groupMapperMock).flush();

                // TODO: consider making a fake so we can make sure the right
                // action was called
                // oneOf(taskHandler).handleTask(with(any(UserActionRequest.class)));
            }
        });

        sut.persist(taskHandlerActionContext, formData, group);
        context.assertIsSatisfied();
        assertEquals("Group Name", group.getName());
        assertSame(org, group.getParentOrganization());

        // TODO Could be more thorough - a list compare utility would help
        assertEquals(2, group.getCapabilities().size());

        // make sure the only queued task is the domain group cache update
        assertEquals(1, taskHandlerActionContext.getUserActionRequests().size());
        assertEquals("domainGroupCacheUpdaterAsyncAction", taskHandlerActionContext.getUserActionRequests().get(0)
                .getActionKey());
    }

    /**
     * Same as previous test, but with org update.
     * 
     * @throws Exception
     *             not expected
     */
    @SuppressWarnings("deprecation")
    @Test
    public void persistSuccessWithOrgUpdate() throws Exception
    {
        final DomainGroup group = new DomainGroup("Group Name", "shortName", new Person("id", "Homer", "Jay",
                "Simpson", "Homey"));

        final List<Person> attribMembers = new ArrayList<Person>();
        attribMembers.add(new Person("id", "Homer", "Jay", "Simpson", "Homey"));
        attribMembers.add(new Person("id", "Max", "X", "Power", "Homer"));

        group.setCoordinators(new HashSet(attribMembers));

        final long id = 1L;
        String newName = "NEW name here";
        final String orgName = "TheParentOrgName";
        final Organization org = context.mock(Organization.class);
        group.setParentOrganization(org);

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put(DomainGroupModelView.ID_KEY, Long.toString(id));
        formData.put(DomainGroupModelView.NAME_KEY, newName);
        formData.put(DomainGroupModelView.KEYWORDS_KEY, "good,idea");
        formData.put(DomainGroupModelView.ORG_PARENT_KEY, orgName);
        formData.put("__KEY_ORIGINAL_PARENT_ORG_KEY", origOrgShortName);
        formData.put("__KEY_ORIGINAL_GROUP_NAME_KEY", "Group Name");
        formData.put("__KEY_ORIGINAL_GROUP_COORDINATORS_KEY", (Serializable) group.getCoordinators());

        context.checking(new Expectations()
        {
            {
                oneOf(groupMapperMock).flush();

                allowing(orgMapperMock).findByShortName(orgName);
                will(returnValue(org));

                allowing(orgMapperMock).findByShortName(origOrgShortName);
                will(returnValue(origParentOrg));
            }
        });

        sut.persist(taskHandlerActionContext, formData, group);
        context.assertIsSatisfied();
        assertEquals("Group Name", group.getName());
        assertSame(org, group.getParentOrganization());

        // TODO Could be more thorough - a list compare utility would help
        assertEquals(2, group.getCapabilities().size());

        // make sure the only queued tasks are the domain group cache update and activity parent org sync.
        assertEquals(2, taskHandlerActionContext.getUserActionRequests().size());
    }

    /**
     * Build an organization based on the input form being fully filled out with valid data.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public void persistSuccessWithUpdatedGroupName() throws Exception
    {
        final DomainGroup newGroup = new DomainGroup("Group Name", "shortName", new Person("id", "Homer", "Jay",
                "Simpson", "Homey"));

        final List<Person> attribMembers = new ArrayList<Person>();
        attribMembers.add(new Person("id", "Homer", "Jay", "Simpson", "Homey"));
        attribMembers.add(new Person("id", "Max", "X", "Power", "Homer"));

        newGroup.setCoordinators(new HashSet(attribMembers));

        final long id = 1L;
        String newName = "NEW org name here";
        final String orgName = "TheParentOrgName";
        final Organization org = context.mock(Organization.class);
        newGroup.setParentOrganization(org);

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put(DomainGroupModelView.ID_KEY, Long.toString(id));
        formData.put(DomainGroupModelView.NAME_KEY, newName);
        formData.put(DomainGroupModelView.KEYWORDS_KEY, "good,idea");
        formData.put(DomainGroupModelView.ORG_PARENT_KEY, orgName);
        formData.put("__KEY_ORIGINAL_PARENT_ORG_KEY", orgName);
        formData.put("__KEY_ORIGINAL_GROUP_NAME_KEY", "Group Name Musta Changed");
        formData.put("__KEY_ORIGINAL_GROUP_COORDINATORS_KEY", (Serializable) newGroup.getCoordinators());

        context.checking(new Expectations()
        {
            {

                oneOf(groupMapperMock).flush();

                // TODO: consider making a fake so we can make sure the right
                // action was called
                // oneOf(taskHandler).handleTask(with(any(UserActionRequest.class)));
            }
        });

        sut.persist(taskHandlerActionContext, formData, newGroup);
        context.assertIsSatisfied();
        assertEquals("Group Name", newGroup.getName());
        assertSame(org, newGroup.getParentOrganization());

        // TODO Could be more thorough - a list compare utility would help
        assertEquals(2, newGroup.getCapabilities().size());

        // make sure two queued tasks are queued - the domain group cache update task, and the activity update for all
        // activities posted to this group
        assertEquals(3, taskHandlerActionContext.getUserActionRequests().size());
        assertEquals("domainGroupCacheUpdaterAsyncAction", taskHandlerActionContext.getUserActionRequests().get(0)
                .getActionKey());
        assertEquals("activityRecipientDomainGroupNameUpdaterAsyncAction", taskHandlerActionContext
                .getUserActionRequests().get(1).getActionKey());
        assertEquals("shortname", taskHandlerActionContext.getUserActionRequests().get(1).getParams());
    }

    /**
     * Build an group with new coordinators. Should see new coordinators added to the group.
     * 
     * @throws Exception
     *             not expected
     */
    @Test
    public void persistSuccessWithNewCoordinators() throws Exception
    {
        final DomainGroup group = new DomainGroup("Group Name", "shortName", new Person("id1", "Homer", "Jay",
                "Simpson", "Homey"));

        final Set<Person> attribMembers = new HashSet<Person>();
        attribMembers.add(new Person("id1", "Homer", "Jay", "Simpson", "Homey"));
        attribMembers.add(new Person("id2", "Max", "X", "Power", "Homer"));

        group.setCoordinators(new HashSet(attribMembers));

        group.getCoordinators().add(new Person("id3", "A", "New", "Member", "Guy"));

        final long id = 1L;
        String newName = "NEW org name here";
        final String orgName = "TheParentOrgName";
        final Organization org = context.mock(Organization.class);
        group.setParentOrganization(org);

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put(DomainGroupModelView.ID_KEY, Long.toString(id));
        formData.put(DomainGroupModelView.NAME_KEY, newName);
        formData.put(DomainGroupModelView.KEYWORDS_KEY, "good,idea");
        formData.put(DomainGroupModelView.ORG_PARENT_KEY, orgName);
        formData.put("__KEY_ORIGINAL_PARENT_ORG_KEY", orgName);
        formData.put(DomainGroupModelView.COORDINATORS_KEY, (Serializable) group.getCoordinators());
        formData.put("__KEY_ORIGINAL_GROUP_NAME_KEY", "Group Name");
        formData.put("__KEY_ORIGINAL_GROUP_COORDINATORS_KEY", (Serializable) attribMembers);

        context.checking(new Expectations()
        {
            {
                ignoring(taskHandlerActionContext);
                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                ignoring(actionContext);
                allowing(actionContext).getPrincipal();
                will(returnValue(userPrincipal));

                ignoring(userPrincipal);

                oneOf(groupMapperMock).flush();

                oneOf(followStrategyMock).execute(with(any(TaskHandlerActionContext.class)));

                // TODO: consider making a fake so we can make sure the right
                // action was called
                // oneOf(taskHandler).handleTask(with(any(UserActionRequest.class)));
            }
        });

        sut.persist(taskHandlerActionContext, formData, group);
        context.assertIsSatisfied();
        assertEquals("Group Name", group.getName());
        assertSame(org, group.getParentOrganization());

        // TODO Could be more thorough - a list compare utility would help
        assertEquals(2, group.getCapabilities().size());

        // make sure the only queued task is the domain group cache update
        assertEquals(1, taskHandlerActionContext.getUserActionRequests().size());
        assertEquals("domainGroupCacheUpdaterAsyncAction", taskHandlerActionContext.getUserActionRequests().get(0)
                .getActionKey());
    }
}
