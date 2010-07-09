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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.commons.actions.context.DefaultPrincipal;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetGroupFollowerIds;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * This class tests the authorization strategy for the PostActionAction.
 * 
 */
public class PostActivityAuthorizationStrategyTest
{
    /**
     * System under test.
     */
    private PostActivityAuthorizationStrategy sut;

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
     * Mocked instance of {@link GetDomainGroupsByShortNames}.
     */
    private final GetDomainGroupsByShortNames getDomainGroupsMapperMock = context
            .mock(GetDomainGroupsByShortNames.class);

    /**
     * Mocked instance of the {@link GetAllPersonIdsWhoHaveGroupCoordinatorAccess}.
     */
    private final GetAllPersonIdsWhoHaveGroupCoordinatorAccess getGroupCoordMapperMock = context
            .mock(GetAllPersonIdsWhoHaveGroupCoordinatorAccess.class);

    /**
     * Mocked instance of the {@link GetGroupFollowerIds}.
     */
    private final GetGroupFollowerIds getGroupFollowerIdsMapperMock = context.mock(GetGroupFollowerIds.class);

    /**
     * Mocked instance of the {@link GetPeopleByAccountIds}.
     */
    private final GetPeopleByAccountIds getPeopleByAccountIdsMapperMock = context.mock(GetPeopleByAccountIds.class);

    /**
     * Test activity.
     */
    private final ActivityDTO testActivity = context.mock(ActivityDTO.class);

    /**
     * Test destination.
     */
    private final StreamEntityDTO testDestination = context.mock(StreamEntityDTO.class, "testDestination");

    /**
     * Test actor.
     */
    private final StreamEntityDTO testActor = context.mock(StreamEntityDTO.class, "testActor");

    /**
     * Test domain group.
     */
    private final DomainGroupModelView testGroup = context.mock(DomainGroupModelView.class);

    /**
     * Test person.
     */
    private final PersonModelView testPerson = context.mock(PersonModelView.class);

    /**
     * Test principal.
     */
    private final Principal testPrincipal = context.mock(Principal.class);

    /**
     * Constant value of a destination stream database id.
     */
    private static final Long DESTINATION_ID = 123L;

    /**
     * Test account id.
     */
    private static final String ACCOUNT_ID = "testaccount";

    /**
     * Test opensocial id.
     */
    private static final String OPENSOCIAL_ID = "testopensocial";

    /**
     * Test entity id.
     */
    private static final Long ID = 1L;

    /**
     * Test entity id for failure situations.
     */
    private static final Long BAD_ID = 2L;

    /**
     * Setup the test suite.
     */
    @Before
    public void setup()
    {
        sut = new PostActivityAuthorizationStrategy(getDomainGroupsMapperMock, getGroupCoordMapperMock,
                getGroupFollowerIdsMapperMock, getPeopleByAccountIdsMapperMock);
    }

    /**
     * Test the successful authorization of an activity posting into a public group and the group is configured to allow
     * posting.
     */
    @Test
    public void testPublicGroupDestinationActivityPostSuccessfulAuthorization()
    {
        PostActivityRequest currentRequest = new PostActivityRequest(testActivity);
        Principal currentPrincipal = new DefaultPrincipal(ACCOUNT_ID, OPENSOCIAL_ID, ID);
        ServiceActionContext currentActionContext = new ServiceActionContext(currentRequest, currentPrincipal);

        final Set<Long> groupCoords = new HashSet<Long>();
        groupCoords.add(ID);

        context.checking(new Expectations()
        {
            {
                allowing(testActivity).getDestinationStream();
                will(returnValue(testDestination));

                oneOf(testDestination).getType();
                will(returnValue(EntityType.GROUP));

                allowing(testDestination).getUniqueIdentifier();

                oneOf(getDomainGroupsMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testGroup));

                oneOf(testGroup).getEntityId();
                will(returnValue(DESTINATION_ID));

                oneOf(getGroupCoordMapperMock).execute(DESTINATION_ID);
                will(returnValue(groupCoords));

                oneOf(testGroup).isPublic();
                will(returnValue(true));

                oneOf(testGroup).isStreamPostable();
                will(returnValue(true));
            }
        });

        sut.authorize(currentActionContext);

        context.assertIsSatisfied();
    }

    /**
     * Test the successful authorization of an activity posting into a person's stream.
     */
    @Test
    public void testPersonDestinationActivityPostSuccessfulAuthorization()
    {
        PostActivityRequest currentRequest = new PostActivityRequest(testActivity);
        Principal currentPrincipal = new DefaultPrincipal(ACCOUNT_ID, OPENSOCIAL_ID, ID);
        ServiceActionContext currentActionContext = new ServiceActionContext(currentRequest, currentPrincipal);

        context.checking(new Expectations()
        {
            {
                allowing(testActivity).getDestinationStream();
                will(returnValue(testDestination));

                allowing(testDestination).getUniqueIdentifier();
                will(returnValue(ACCOUNT_ID));

                oneOf(testDestination).getType();
                will(returnValue(EntityType.PERSON));

                oneOf(getPeopleByAccountIdsMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testPerson));
            }
        });

        sut.authorize(currentActionContext);

        context.assertIsSatisfied();
    }

    /**
     * Test the successful authorization of an activity posting into a person's stream.
     */
    @Test
    public void testPersonDestinationActivityPostSuccessfulStreamPostableAuthorization()
    {
        PostActivityRequest currentRequest = new PostActivityRequest(testActivity);
        Principal currentPrincipal = new DefaultPrincipal(ACCOUNT_ID, OPENSOCIAL_ID, ID);
        ServiceActionContext currentActionContext = new ServiceActionContext(currentRequest, currentPrincipal);

        context.checking(new Expectations()
        {
            {
                allowing(testActivity).getDestinationStream();
                will(returnValue(testDestination));

                oneOf(testDestination).getType();
                will(returnValue(EntityType.PERSON));

                allowing(testDestination).getUniqueIdentifier();
                will(returnValue("NOT_PRINCIPAL_ID"));

                oneOf(getPeopleByAccountIdsMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testPerson));

                oneOf(testPerson).isStreamPostable();
                will(returnValue(true));
            }
        });

        sut.authorize(currentActionContext);

        context.assertIsSatisfied();
    }

    /**
     * Test the failure authorization of an activity posting into an unhandled entity type's stream.
     */
    @Test(expected = AuthorizationException.class)
    public void testInvalidDestinationActivityPostFailureAuthorization()
    {
        PostActivityRequest currentRequest = new PostActivityRequest(testActivity);
        Principal currentPrincipal = new DefaultPrincipal(ACCOUNT_ID, OPENSOCIAL_ID, ID);
        ServiceActionContext currentActionContext = new ServiceActionContext(currentRequest, currentPrincipal);

        context.checking(new Expectations()
        {
            {
                allowing(testActivity).getDestinationStream();
                will(returnValue(testDestination));

                oneOf(testDestination).getType();
                will(returnValue(EntityType.NOTSET));
            }
        });

        sut.authorize(currentActionContext);

        context.assertIsSatisfied();
    }

    /**
     * Test the failure authorization when the group is public, but the user is not a coordinator and the group is
     * configured to non-postable.
     */
    @Test(expected = AuthorizationException.class)
    public void testPublicGroupDestinationActivityPostFailureAuthorization()
    {
        PostActivityRequest currentRequest = new PostActivityRequest(testActivity);
        Principal currentPrincipal = new DefaultPrincipal(ACCOUNT_ID, OPENSOCIAL_ID, BAD_ID);
        ServiceActionContext currentActionContext = new ServiceActionContext(currentRequest, currentPrincipal);

        final Set<Long> groupCoords = new HashSet<Long>();
        groupCoords.add(ID);

        context.checking(new Expectations()
        {
            {
                allowing(testActivity).getDestinationStream();
                will(returnValue(testDestination));

                oneOf(testDestination).getType();
                will(returnValue(EntityType.GROUP));

                allowing(testDestination).getUniqueIdentifier();

                oneOf(getDomainGroupsMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testGroup));

                oneOf(testGroup).getEntityId();
                will(returnValue(DESTINATION_ID));

                oneOf(getGroupCoordMapperMock).execute(DESTINATION_ID);
                will(returnValue(groupCoords));

                oneOf(testGroup).isPublic();
                will(returnValue(true));

                oneOf(testGroup).isStreamPostable();
                will(returnValue(false));
            }
        });

        sut.authorize(currentActionContext);

        context.assertIsSatisfied();
    }

    /**
     * Test the failure authorization of an activity posting into a person's stream.
     */
    @Test(expected = AuthorizationException.class)
    public void testPersonDestinationActivityPostFailureAuthorization()
    {
        PostActivityRequest currentRequest = new PostActivityRequest(testActivity);
        Principal currentPrincipal = new DefaultPrincipal(ACCOUNT_ID, OPENSOCIAL_ID, ID);
        ServiceActionContext currentActionContext = new ServiceActionContext(currentRequest, currentPrincipal);

        context.checking(new Expectations()
        {
            {
                allowing(testActivity).getDestinationStream();
                will(returnValue(testDestination));

                oneOf(testDestination).getType();
                will(returnValue(EntityType.PERSON));

                allowing(testDestination).getUniqueIdentifier();

                oneOf(getPeopleByAccountIdsMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testPerson));

                oneOf(testActivity).getActor();
                will(returnValue(testActor));

                oneOf(testActor).getId();
                will(returnValue(BAD_ID));

                oneOf(testPerson).isStreamPostable();
                will(returnValue(false));
            }
        });

        sut.authorize(currentActionContext);

        context.assertIsSatisfied();
    }

    /**
     * Test the successful authorization when the group is private and the poster is a coordinator.
     */
    @Test
    public void testPrivateGroupDestinationActivityPostSuccessCoordinatorAuthorization()
    {
        PostActivityRequest currentRequest = new PostActivityRequest(testActivity);
        Principal currentPrincipal = new DefaultPrincipal(ACCOUNT_ID, OPENSOCIAL_ID, ID);
        ServiceActionContext currentActionContext = new ServiceActionContext(currentRequest, currentPrincipal);

        final Set<Long> groupCoords = new HashSet<Long>();
        groupCoords.add(ID);

        context.checking(new Expectations()
        {
            {
                allowing(testActivity).getDestinationStream();
                will(returnValue(testDestination));

                oneOf(testDestination).getType();
                will(returnValue(EntityType.GROUP));

                allowing(testDestination).getUniqueIdentifier();

                oneOf(getDomainGroupsMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testGroup));

                oneOf(testGroup).getEntityId();
                will(returnValue(DESTINATION_ID));

                oneOf(getGroupCoordMapperMock).execute(DESTINATION_ID);
                will(returnValue(groupCoords));

                oneOf(testGroup).isPublic();
                will(returnValue(false));
            }
        });

        sut.authorize(currentActionContext);

        context.assertIsSatisfied();
    }

    /**
     * Test the successful authorization when the group is private and the poster is a follower of the group.
     */
    @Test
    public void testPrivateGroupDestinationActivityPostSuccessFollowerAuthorization()
    {
        PostActivityRequest currentRequest = new PostActivityRequest(testActivity);
        Principal currentPrincipal = new DefaultPrincipal(ACCOUNT_ID, OPENSOCIAL_ID, BAD_ID);
        ServiceActionContext currentActionContext = new ServiceActionContext(currentRequest, currentPrincipal);

        final Set<Long> groupCoords = new HashSet<Long>();
        groupCoords.add(ID);

        final List<Long> groupFollowers = new ArrayList<Long>();
        groupFollowers.add(BAD_ID);

        context.checking(new Expectations()
        {
            {
                allowing(testActivity).getDestinationStream();
                will(returnValue(testDestination));

                oneOf(testDestination).getType();
                will(returnValue(EntityType.GROUP));

                allowing(testDestination).getUniqueIdentifier();

                oneOf(getDomainGroupsMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testGroup));

                oneOf(testGroup).getEntityId();
                will(returnValue(DESTINATION_ID));

                oneOf(getGroupCoordMapperMock).execute(DESTINATION_ID);
                will(returnValue(groupCoords));

                oneOf(testGroup).isPublic();
                will(returnValue(false));

                oneOf(getGroupFollowerIdsMapperMock).execute(DESTINATION_ID);
                will(returnValue(groupFollowers));

                oneOf(testGroup).getEntityId();
                will(returnValue(DESTINATION_ID));

                oneOf(testGroup).isStreamPostable();
                will(returnValue(true));
            }
        });

        sut.authorize(currentActionContext);

        context.assertIsSatisfied();
    }

    /**
     * Test the successful authorization when the group is private and the poster is not a follower or coordinator of
     * the group.
     */
    @Test(expected = AuthorizationException.class)
    public void testPrivateGroupDestinationActivityPostFailureAuthorization()
    {
        PostActivityRequest currentRequest = new PostActivityRequest(testActivity);
        Principal currentPrincipal = new DefaultPrincipal(ACCOUNT_ID, OPENSOCIAL_ID, BAD_ID);
        ServiceActionContext currentActionContext = new ServiceActionContext(currentRequest, currentPrincipal);

        final Set<Long> groupCoords = new HashSet<Long>();
        groupCoords.add(ID);

        final List<Long> groupFollowers = new ArrayList<Long>();
        groupFollowers.add(ID);

        context.checking(new Expectations()
        {
            {
                allowing(testActivity).getDestinationStream();
                will(returnValue(testDestination));

                oneOf(testDestination).getType();
                will(returnValue(EntityType.GROUP));

                allowing(testDestination).getUniqueIdentifier();

                oneOf(getDomainGroupsMapperMock).fetchUniqueResult(with(any(String.class)));
                will(returnValue(testGroup));

                oneOf(testGroup).getEntityId();
                will(returnValue(DESTINATION_ID));

                oneOf(getGroupCoordMapperMock).execute(DESTINATION_ID);
                will(returnValue(groupCoords));

                oneOf(testGroup).isPublic();
                will(returnValue(false));

                oneOf(getGroupFollowerIdsMapperMock).execute(DESTINATION_ID);
                will(returnValue(groupFollowers));

                oneOf(testGroup).getEntityId();
                will(returnValue(DESTINATION_ID));
            }
        });

        sut.authorize(currentActionContext);

        context.assertIsSatisfied();
    }
}
