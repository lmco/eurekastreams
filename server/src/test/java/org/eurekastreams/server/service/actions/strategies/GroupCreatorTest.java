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
import org.eurekastreams.server.action.request.notification.TargetEntityNotificationsRequest;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.SystemSettings;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for the GroupCreator.
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

    /**
     * The current user's account id.
     */
    private final String accountId = "sdlkfjsdlfjs";

    /**
     * The current user's id.
     */
    private final Long personId = 23423L;

    /**
     * The current user's open social id.
     */
    private final String openSocialId = "sdflkjsd-sdlfk-sdflkj-lskdf";

    /**
     * The mock for the person mapper.
     */
    private final PersonMapper personMapperMock = context.mock(PersonMapper.class);
    /**
     * The mock group.
     */
    private final DomainGroup groupMock = context.mock(DomainGroup.class);

    /**
     * The mock group mapper to be used by the action.
     */
    private final DomainGroupMapper groupMapperMock = context.mock(DomainGroupMapper.class);

    /**
     * task handler action context.
     */
    private final TaskHandlerActionContext<PrincipalActionContext> taskHandlerActionContext = context
            .mock(TaskHandlerActionContext.class);

    /**
     * Action context.
     */
    private final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Collection to hold action requests queued up for async processing.
     */
    private List<UserActionRequest> userActionRequests = new ArrayList<UserActionRequest>();

    /**
     * Current user principal.
     */
    private final Principal userPrincipal = context.mock(Principal.class);

    /**
     * Mocked following group strategy to add new group followers.
     */
    private final SetFollowingGroupStatusExecution followStrategyMock = context
            .mock(SetFollowingGroupStatusExecution.class);

    /**
     * Mapper to get the system admin ids.
     */
    private final DomainMapper<Serializable, List<Long>> getSystemAdminIdsMapper = context.mock(DomainMapper.class,
            "getSystemAdminIdsMapper");

    /**
     * Mapper to get system settings.
     */
    private final DomainMapper<MapperRequest, SystemSettings> getSystemSettingsMapper = context.mock(DomainMapper.class,
            "getSystemSettings");

    /**
     * The Mock for the person object. Used for created By person.
     */
    private Person personMock;

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
     * Build a group based on the input form being fully filled out with valid data.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void getSuccess() throws Exception
    {
        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("orgParent", "FACE");

        GroupCreator sut = new GroupCreator(groupMapperMock, personMapperMock, getSystemAdminIdsMapper,
                followStrategyMock, getSystemSettingsMapper);
        Assert.assertNotNull(sut.get(taskHandlerActionContext, formData));
        context.assertIsSatisfied();
    }

    /**
     * Build a group based on the input form being fully filled out with valid data. Group will be put into pending
     * because the org requires approval.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void persistSuccessButPending() throws Exception
    {
        final List<Long> adminIds = new ArrayList<Long>();
        final List<Person> coordinators = new ArrayList<Person>();
        coordinators.add(new Person("id2", "Homer", "Jay", "Simpson", "Homey"));
        coordinators.add(new Person("id3", "Max", "X", "Power", "Homer"));

        personMock = context.mock(Person.class);
        final StreamScope streamScope = context.mock(StreamScope.class);

        final long id = 1L;
        final String newName = "NEW org name here";

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("id", Long.toString(id));
        formData.put("name", newName);

        final SystemSettings settings = new SystemSettings();
        settings.setAllUsersCanCreateGroups(false);

        context.checking(new Expectations()
        {
            {
                oneOf(getSystemSettingsMapper).execute(null);
                will(returnValue(settings));

                oneOf(getSystemAdminIdsMapper).execute(null);
                will(returnValue(adminIds));

                oneOf(groupMock).getShortName();
                will(returnValue("blah"));

                oneOf(groupMock).getCoordinators();

                oneOf(groupMapperMock).findByShortName("blah");
                will(returnValue(null));

                oneOf(groupMock).setPending(true);
                oneOf(personMapperMock).findByAccountId(accountId);
                will(returnValue(personMock));
                oneOf(groupMock).setCreatedBy(personMock);

                oneOf(groupMapperMock).insert(groupMock);

                allowing(groupMock).getId();
                will(returnValue(id));

                oneOf(groupMock).getName();
                will(returnValue(newName));

                oneOf(groupMock).getStreamScope();
                will(returnValue(streamScope));

                oneOf(streamScope).setDestinationEntityId(id);

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

        GroupCreator sut = new GroupCreator(groupMapperMock, personMapperMock, getSystemAdminIdsMapper,
                followStrategyMock, getSystemSettingsMapper);
        sut.persist(taskHandlerActionContext, formData, groupMock);
        context.assertIsSatisfied();

        assertEquals(2, userActionRequests.size());

        assertEquals("Second request has wrong key", "createNotificationsAction", userActionRequests.get(1)
                .getActionKey());
        CreateNotificationsRequest request2 = new TargetEntityNotificationsRequest(RequestType.REQUEST_NEW_GROUP,
                personId, id);
        assertTrue("Second request has wrong content", IsEqualInternally.areEqualInternally(request2,
                userActionRequests.get(1).getParams()));
    }

    /**
     * Build an group based on the input form being fully filled out with valid data. group should be automatically
     * approved if user is an admin.
     *
     * @throws Exception
     *             not expected
     */
    @Test
    public void persistSuccessRequiresPermissionButSystemAdmin() throws Exception
    {
        final List<Long> adminIds = new ArrayList<Long>();
        adminIds.add(personId);
        final List<Person> coordinators = new ArrayList<Person>();
        coordinators.add(new Person("id2", "Homer", "Jay", "Simpson", "Homey"));
        coordinators.add(new Person("id3", "Max", "X", "Power", "Homer"));

        personMock = context.mock(Person.class);
        final StreamScope streamScope = context.mock(StreamScope.class);

        final long id = 1L;
        final String newName = "NEW org name here";

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("id", Long.toString(id));
        formData.put("name", newName);

        final SystemSettings settings = new SystemSettings();
        settings.setAllUsersCanCreateGroups(false);

        context.checking(new Expectations()
        {
            {
                oneOf(getSystemSettingsMapper).execute(null);
                will(returnValue(settings));

                oneOf(getSystemAdminIdsMapper).execute(null);
                will(returnValue(adminIds));

                oneOf(groupMock).getShortName();
                will(returnValue("blah"));

                oneOf(groupMock).getCoordinators();

                oneOf(groupMapperMock).findByShortName("blah");
                will(returnValue(null));

                oneOf(groupMock).setPending(false);
                oneOf(personMapperMock).findByAccountId(accountId);

                will(returnValue(personMock));
                oneOf(groupMock).setCreatedBy(personMock);

                oneOf(groupMapperMock).insert(groupMock);

                allowing(groupMock).getId();
                will(returnValue(id));

                oneOf(groupMock).getName();
                will(returnValue(newName));

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

                oneOf(groupMock).getStreamScope();
                will(returnValue(streamScope));

                oneOf(streamScope).setDestinationEntityId(id);
            }
        });

        assertEquals(0, userActionRequests.size());

        GroupCreator sut = new GroupCreator(groupMapperMock, personMapperMock, getSystemAdminIdsMapper,
                followStrategyMock, getSystemSettingsMapper);
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
        final StreamScope streamScope = context.mock(StreamScope.class);

        final long id = 1L;
        final String newName = "NEW org name here";

        final HashMap<String, Serializable> formData = new HashMap<String, Serializable>();
        formData.put("id", Long.toString(id));
        formData.put("name", newName);

        final SystemSettings settings = new SystemSettings();
        settings.setAllUsersCanCreateGroups(true);

        context.checking(new Expectations()
        {
            {
                oneOf(getSystemSettingsMapper).execute(null);
                will(returnValue(settings));

                oneOf(groupMock).getShortName();
                will(returnValue("blah"));

                oneOf(groupMock).getCoordinators();

                oneOf(groupMapperMock).findByShortName("blah");
                will(returnValue(null));

                oneOf(groupMock).setPending(false);

                oneOf(personMapperMock).findByAccountId(accountId);
                will(returnValue(personMock));
                oneOf(groupMock).setCreatedBy(personMock);

                oneOf(groupMapperMock).insert(groupMock);

                allowing(groupMock).getId();
                will(returnValue(id));

                oneOf(groupMock).getName();
                will(returnValue(newName));
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

                oneOf(groupMock).getStreamScope();
                will(returnValue(streamScope));

                oneOf(streamScope).setDestinationEntityId(id);
            }
        });

        assertEquals(0, userActionRequests.size());

        GroupCreator sut = new GroupCreator(groupMapperMock, personMapperMock, getSystemAdminIdsMapper,
                followStrategyMock, getSystemSettingsMapper);
        sut.persist(taskHandlerActionContext, formData, groupMock);
        context.assertIsSatisfied();

        assertEquals(1, userActionRequests.size());
    }

    /**
     * Build a group based on the input form being fully filled out with valid data.
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

        final SystemSettings settings = new SystemSettings();
        settings.setAllUsersCanCreateGroups(false);

        context.checking(new Expectations()
        {
            {
                oneOf(getSystemSettingsMapper).execute(null);
                will(returnValue(settings));

                oneOf(groupMock).getShortName();
                will(returnValue("group1"));

                oneOf(groupMock).getCoordinators();
                will(returnValue(coordinators));

                oneOf(groupMapperMock).findByShortName("group1");
                will(returnValue(dupGroup));

            }
        });

        assertEquals(0, userActionRequests.size());

        GroupCreator sut = new GroupCreator(groupMapperMock, personMapperMock, getSystemAdminIdsMapper,
                followStrategyMock, getSystemSettingsMapper);
        sut.persist(taskHandlerActionContext, formData, groupMock);

        assertEquals(0, userActionRequests.size());
    }

    /**
     * Build a group based on the input form being fully filled out with valid data.
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

        final SystemSettings settings = new SystemSettings();
        settings.setAllUsersCanCreateGroups(false);

        context.checking(new Expectations()
        {
            {
                oneOf(getSystemSettingsMapper).execute(null);
                will(returnValue(settings));

                oneOf(groupMock).getShortName();
                will(returnValue("group1"));

                oneOf(groupMock).getCoordinators();
                will(returnValue(coordinators));

                oneOf(groupMapperMock).findByShortName("group1");
                will(returnValue(dupGroup));
            }
        });

        assertEquals(0, userActionRequests.size());

        GroupCreator sut = new GroupCreator(groupMapperMock, personMapperMock, getSystemAdminIdsMapper,
                followStrategyMock, getSystemSettingsMapper);
        sut.persist(taskHandlerActionContext, formData, groupMock);

        assertEquals(0, userActionRequests.size());
    }

}
