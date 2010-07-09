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
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.exceptions.ValidationException;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.commons.test.IsEqualInternally;
import org.eurekastreams.server.action.execution.profile.SetFollowingGroupStatusExecution;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.Organization;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverser;
import org.eurekastreams.server.domain.strategies.OrganizationHierarchyTraverserBuilder;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.OrganizationMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.cache.OrganizationHierarchyCache;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the CreateOrganizationAction.
 */
public class GroupCreatorTest
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

    /** Test data. */
    private static final long ORG_ID = 98765L;

    /**
     * The current user's account id.
     */
    private String accountId = "sdlkfjsdlfjs";

    /**
     * The current user's id.
     */
    private final Long personId = 23423L;

    /**
     * The current user's open social id.
     */
    private String openSocialId = "sdflkjsd-sdlfk-sdflkj-lskdf";

    /**
     * The mock org mapper to be used by the action.
     */
    private OrganizationMapper orgMapperMock = context.mock(OrganizationMapper.class);

    /**
     * The mock for the person mapper.
     */
    private PersonMapper personMapperMock = context.mock(PersonMapper.class);
    /**
     * The mock group.
     */
    private DomainGroup groupMock = context.mock(DomainGroup.class);

    /**
     * The mock group mapper to be used by the action.
     */
    private DomainGroupMapper groupMapperMock = context.mock(DomainGroupMapper.class);

    /**
     * task handler action context.
     */
    private TaskHandlerActionContext<PrincipalActionContext> taskHandlerActionContext =
            context.mock(TaskHandlerActionContext.class);

    /**
     * Action context.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Collection to hold action requests queued up for async processing.
     */
    private List<UserActionRequest> userActionRequests = new ArrayList<UserActionRequest>();

    /**
     * Current user principal.
     */
    private Principal userPrincipal = context.mock(Principal.class);

    /**
     * The mock of the organization hierachy traverser.
     */
    private OrganizationHierarchyTraverser orgTraverserMock = context.mock(OrganizationHierarchyTraverser.class);

    /**
     * The mock of the organization hierachy traverser builder.
     */
    private OrganizationHierarchyTraverserBuilder orgTraverserBuilderMock =
            context.mock(OrganizationHierarchyTraverserBuilder.class);

    /**
     * Organization cache mock to look up parent orgs.
     */
    private final OrganizationHierarchyCache orgHierarchyCacheMock = context.mock(OrganizationHierarchyCache.class);

    /**
     * Mocked following group strategy to add new group followers.
     */
    private final SetFollowingGroupStatusExecution followStrategyMock =
            context.mock(SetFollowingGroupStatusExecution.class);

    /**
     * The Mock for the person object. Used for created By person.
     */
    private Person personMock;

    /**
     * Org mock to check what the pending bit should be set to.
     */
    private Organization orgMock;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        userActionRequests = new ArrayList<UserActionRequest>();

        context.checking(new Expectations()
        {
            {
                allowing(taskHandlerActionContext).getActionContext();
                will(returnValue(actionContext));

                allowing(taskHandlerActionContext).getUserActionRequests();
                will(returnValue(userActionRequests));

                allowing(actionContext).getPrincipal();
                will(returnValue(userPrincipal));

                allowing(userPrincipal).getAccountId();
                will(returnValue(accountId));

                allowing(userPrincipal).getOpenSocialId();
                will(returnValue(openSocialId));

                allowing(userPrincipal).getId();
                will(returnValue(personId));
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
        formData.put("orgParent", "FACE");

        context.checking(new Expectations()
        {
            {
                oneOf(orgMapperMock).findByShortName(with(any(String.class)));
            }
        });

        GroupCreator sut =
                new GroupCreator(groupMapperMock, orgMapperMock, personMapperMock, orgTraverserBuilderMock,
                        orgHierarchyCacheMock, followStrategyMock);
        Assert.assertNotNull(sut.get(taskHandlerActionContext, formData));
        context.assertIsSatisfied();
    }

    /**
     * Build an organization when the org is not supplied.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void getNoOrg() throws Exception
    {
        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("orgParent", "");

        GroupCreator sut =
                new GroupCreator(groupMapperMock, orgMapperMock, personMapperMock, orgTraverserBuilderMock,
                        orgHierarchyCacheMock, followStrategyMock);
        DomainGroup newGroup = sut.get(taskHandlerActionContext, formData);
        Assert.assertNotNull(newGroup);
        Assert.assertNull(newGroup.getParentOrganization());
        context.assertIsSatisfied();

    }

    /**
     * Build an organization based on the input form being fully filled out with valid data. Group will be put into
     * pending because the org requires approval.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void persistSuccessButPending() throws Exception
    {
        final List<Person> coordinators = new ArrayList<Person>();
        coordinators.add(new Person("id2", "Homer", "Jay", "Simpson", "Homey"));
        coordinators.add(new Person("id3", "Max", "X", "Power", "Homer"));

        personMock = context.mock(Person.class);
        orgMock = context.mock(Organization.class);

        final long id = 1L;
        String newName = "NEW org name here";

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("id", Long.toString(id));
        formData.put("name", newName);

        context.checking(new Expectations()
        {
            {
                oneOf(groupMock).getShortName();
                will(returnValue("blah"));

                oneOf(groupMock).getCoordinators();

                oneOf(groupMapperMock).findByShortName("blah");
                will(returnValue(null));

                oneOf(groupMock).getParentOrganization();
                will(returnValue(orgMock));
                oneOf(orgMock).getAllUsersCanCreateGroups();
                will(returnValue(false));
                oneOf(orgMock).isCoordinator(with(any(String.class)));
                will(returnValue(false));

                allowing(orgMock).getId();
                will(returnValue(ORG_ID));

                oneOf(orgHierarchyCacheMock).getParentOrganizations(with(any(Long.class)));

                oneOf(groupMock).setPending(true);
                oneOf(personMapperMock).findByAccountId(accountId);
                will(returnValue(personMock));
                oneOf(groupMock).setCreatedBy(personMock);

                oneOf(groupMapperMock).insert(groupMock);
                oneOf(orgTraverserBuilderMock).getOrganizationHierarchyTraverser();
                will(returnValue(orgTraverserMock));
                oneOf(orgTraverserMock).traverseHierarchy(groupMock);
                oneOf(orgMapperMock).updateOrganizationStatistics(orgTraverserMock);

                allowing(groupMock).getId();
                will(returnValue(id));

                // TODO: consider making a fake so we can make sure the right
                // action was called
                // oneOf(taskHandler).handleTask(with(any(UserActionRequest.class)));

                Set<Person> coordinators = new HashSet<Person>();
                coordinators.add(personMock);
                oneOf(groupMock).getCoordinators();
                will(returnValue(coordinators));

                oneOf(personMock).getId();
                will(returnValue(1L));

                oneOf(followStrategyMock).execute(with(any(TaskHandlerActionContext.class)));
            }
        });

        assertEquals(0, userActionRequests.size());

        GroupCreator sut =
                new GroupCreator(groupMapperMock, orgMapperMock, personMapperMock, orgTraverserBuilderMock,
                        orgHierarchyCacheMock, followStrategyMock);
        sut.persist(taskHandlerActionContext, formData, groupMock);
        context.assertIsSatisfied();

        assertEquals(2, userActionRequests.size());

        assertEquals("Second request has wrong key", "createNotificationsAction", userActionRequests.get(1)
                .getActionKey());
        CreateNotificationsRequest request2 =
                new CreateNotificationsRequest(RequestType.REQUEST_NEW_GROUP, personId, ORG_ID, id);
        assertTrue("Second request has wrong content", IsEqualInternally.areEqualInternally(request2,
                userActionRequests.get(1).getParams()));
    }

    /**
     * Build an group based on the input form being fully filled out with valid data. group should be automatically
     * approved if user is a org coordinator.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void persistSuccessPendingButOrgCoordinator() throws Exception
    {
        final List<Person> coordinators = new ArrayList<Person>();
        coordinators.add(new Person("id2", "Homer", "Jay", "Simpson", "Homey"));
        coordinators.add(new Person("id3", "Max", "X", "Power", "Homer"));

        personMock = context.mock(Person.class);
        orgMock = context.mock(Organization.class);

        final long id = 1L;
        String newName = "NEW org name here";

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("id", Long.toString(id));
        formData.put("name", newName);

        context.checking(new Expectations()
        {
            {
                oneOf(groupMock).getShortName();
                will(returnValue("blah"));

                oneOf(groupMock).getCoordinators();

                oneOf(groupMapperMock).findByShortName("blah");
                will(returnValue(null));

                oneOf(groupMock).getParentOrganization();
                will(returnValue(orgMock));
                oneOf(orgMock).getAllUsersCanCreateGroups();
                will(returnValue(false));
                oneOf(orgMock).isCoordinator(with(any(String.class)));
                will(returnValue(true));
                oneOf(orgMock).getId();
                oneOf(orgHierarchyCacheMock).getParentOrganizations(with(any(Long.class)));

                oneOf(groupMock).setPending(false);
                oneOf(personMapperMock).findByAccountId(accountId);
                will(returnValue(personMock));
                oneOf(groupMock).setCreatedBy(personMock);

                oneOf(groupMapperMock).insert(groupMock);
                oneOf(orgTraverserBuilderMock).getOrganizationHierarchyTraverser();
                will(returnValue(orgTraverserMock));
                oneOf(orgTraverserMock).traverseHierarchy(groupMock);
                oneOf(orgMapperMock).updateOrganizationStatistics(orgTraverserMock);

                allowing(groupMock).getId();
                will(returnValue(id));

                // TODO: consider making a fake so we can make sure the right
                // action was called
                // oneOf(taskHandler).handleTask(with(any(UserActionRequest.class)));

                Set<Person> coordinators = new HashSet<Person>();
                coordinators.add(personMock);
                oneOf(groupMock).getCoordinators();
                will(returnValue(coordinators));

                oneOf(personMock).getId();
                will(returnValue(1L));

                oneOf(followStrategyMock).execute(with(any(TaskHandlerActionContext.class)));
            }
        });

        assertEquals(0, userActionRequests.size());

        GroupCreator sut =
                new GroupCreator(groupMapperMock, orgMapperMock, personMapperMock, orgTraverserBuilderMock,
                        orgHierarchyCacheMock, followStrategyMock);
        sut.persist(taskHandlerActionContext, formData, groupMock);
        context.assertIsSatisfied();

        assertEquals(1, userActionRequests.size());

    }

    /**
     * Build an group based on the input form being fully filled out with valid data. Group should be automatically
     * approved because the org does not require approval.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void persistSuccessNotPending() throws Exception
    {
        final List<Person> coordinators = new ArrayList<Person>();
        coordinators.add(new Person("id2", "Homer", "Jay", "Simpson", "Homey"));
        coordinators.add(new Person("id3", "Max", "X", "Power", "Homer"));

        personMock = context.mock(Person.class);
        orgMock = context.mock(Organization.class);

        final long id = 1L;
        String newName = "NEW org name here";

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("id", Long.toString(id));
        formData.put("name", newName);

        context.checking(new Expectations()
        {
            {
                oneOf(groupMock).getShortName();
                will(returnValue("blah"));

                oneOf(groupMock).getCoordinators();

                oneOf(groupMapperMock).findByShortName("blah");
                will(returnValue(null));

                oneOf(groupMock).getParentOrganization();
                will(returnValue(orgMock));
                oneOf(orgMock).getAllUsersCanCreateGroups();
                will(returnValue(true));
                oneOf(groupMock).setPending(false);

                oneOf(personMapperMock).findByAccountId(accountId);
                will(returnValue(personMock));
                oneOf(groupMock).setCreatedBy(personMock);

                oneOf(groupMapperMock).insert(groupMock);
                oneOf(orgTraverserBuilderMock).getOrganizationHierarchyTraverser();
                will(returnValue(orgTraverserMock));
                oneOf(orgTraverserMock).traverseHierarchy(groupMock);
                oneOf(orgMapperMock).updateOrganizationStatistics(orgTraverserMock);

                allowing(groupMock).getId();
                will(returnValue(id));
                //
                // // TODO: consider making a fake so we can make sure the right
                // // action was called
                // oneOf(taskHandler).handleTask(with(any(UserActionRequest.class)));

                Set<Person> coordinators = new HashSet<Person>();
                coordinators.add(personMock);
                oneOf(groupMock).getCoordinators();
                will(returnValue(coordinators));

                oneOf(personMock).getId();
                will(returnValue(1L));

                oneOf(followStrategyMock).execute(with(any(TaskHandlerActionContext.class)));
            }
        });

        assertEquals(0, userActionRequests.size());

        GroupCreator sut =
                new GroupCreator(groupMapperMock, orgMapperMock, personMapperMock, orgTraverserBuilderMock,
                        orgHierarchyCacheMock, followStrategyMock);
        sut.persist(taskHandlerActionContext, formData, groupMock);
        context.assertIsSatisfied();

        assertEquals(1, userActionRequests.size());
    }

    /**
     * Test persist when no org was provided. valid data.
     *
     * @throws Exception
     *             Validation error.
     */
    @Test(expected = ValidationException.class)
    public void persistFailedNoOrg() throws Exception
    {
        final Set<Person> coordinators = new HashSet<Person>();
        coordinators.add(new Person("id2", "Homer", "Jay", "Simpson", "Homey"));
        final DomainGroup dupGroup = context.mock(DomainGroup.class, "dupGroup");
        final long id = 1L;
        String newName = "NEW org name here";

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("id", Long.toString(id));
        formData.put("name", newName);
        formData.put("shortName", "group1");

        context.checking(new Expectations()
        {
            {
                oneOf(groupMock).getParentOrganization();
                will(returnValue(null));
                oneOf(orgTraverserBuilderMock).getOrganizationHierarchyTraverser();
                will(returnValue(orgTraverserMock));
                oneOf(groupMock).getShortName();
                will(returnValue("group1"));

                oneOf(groupMock).getCoordinators();
                will(returnValue(coordinators));

                oneOf(groupMapperMock).findByShortName("group1");
                will(returnValue(dupGroup));

            }
        });

        assertEquals(0, userActionRequests.size());

        GroupCreator sut =
                new GroupCreator(groupMapperMock, orgMapperMock, personMapperMock, orgTraverserBuilderMock,
                        orgHierarchyCacheMock, followStrategyMock);
        sut.persist(taskHandlerActionContext, formData, groupMock);

        assertEquals(0, userActionRequests.size());
    }

    /**
     * Build an organization based on the input form being fully filled out with valid data.
     *
     * @throws Exception
     *             not expected
     */
    @Test(expected = ValidationException.class)
    public void persistFailedDupGroup() throws Exception
    {
        final Set<Person> coordinators = new HashSet<Person>();
        coordinators.add(new Person("id2", "Homer", "Jay", "Simpson", "Homey"));
        final DomainGroup dupGroup = context.mock(DomainGroup.class, "dupGroup");
        final long id = 1L;
        String newName = "NEW org name here";

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("id", Long.toString(id));
        formData.put("name", newName);
        formData.put("shortName", "group1");

        context.checking(new Expectations()
        {
            {
                oneOf(groupMock).getParentOrganization();
                will(returnValue(orgMock));
                oneOf(orgTraverserBuilderMock).getOrganizationHierarchyTraverser();
                will(returnValue(orgTraverserMock));
                oneOf(groupMock).getShortName();
                will(returnValue("group1"));

                oneOf(groupMock).getCoordinators();
                will(returnValue(coordinators));

                oneOf(groupMapperMock).findByShortName("group1");
                will(returnValue(dupGroup));

            }
        });

        assertEquals(0, userActionRequests.size());

        GroupCreator sut =
                new GroupCreator(groupMapperMock, orgMapperMock, personMapperMock, orgTraverserBuilderMock,
                        orgHierarchyCacheMock, followStrategyMock);
        sut.persist(taskHandlerActionContext, formData, groupMock);

        assertEquals(0, userActionRequests.size());
    }

    /**
     * Build an organization based on the input form being fully filled out with valid data.
     *
     * @throws Exception
     *             not expected
     */
    @Test(expected = ValidationException.class)
    public void persistFailedCoordinators() throws Exception
    {

        final DomainGroup dupGroup = context.mock(DomainGroup.class, "dupGroup");
        final Set<Person> coordinators = new HashSet<Person>();

        final long id = 1L;
        String newName = "NEW org name here";

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("id", Long.toString(id));
        formData.put("name", newName);
        formData.put("shortName", "group1");

        context.checking(new Expectations()
        {
            {
                oneOf(groupMock).getParentOrganization();
                will(returnValue(orgMock));
                oneOf(orgTraverserBuilderMock).getOrganizationHierarchyTraverser();
                will(returnValue(orgTraverserMock));
                oneOf(groupMock).getShortName();
                will(returnValue("group1"));

                oneOf(groupMock).getCoordinators();
                will(returnValue(coordinators));

                oneOf(groupMapperMock).findByShortName("group1");
                will(returnValue(dupGroup));
            }
        });

        assertEquals(0, userActionRequests.size());

        GroupCreator sut =
                new GroupCreator(groupMapperMock, orgMapperMock, personMapperMock, orgTraverserBuilderMock,
                        orgHierarchyCacheMock, followStrategyMock);
        sut.persist(taskHandlerActionContext, formData, groupMock);

        assertEquals(0, userActionRequests.size());
    }

}
