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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.DomainGroupMapper;
import org.eurekastreams.server.persistence.PersonMapper;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.persistence.mappers.stream.GetGroupFollowerIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.service.actions.strategies.ActivityInteractionType;
import org.eurekastreams.server.service.actions.strategies.activity.ActivityDTOFromParamsStrategy;
import org.eurekastreams.server.service.actions.strategies.activity.ActorRetrievalStrategy;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for ActivityAuthorizationStrategy.
 */
public class ActivityAuthorizationStrategyTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Groups by shortName DAO.
     */
    private GetDomainGroupsByShortNames groupByShortNameDAO = context.mock(GetDomainGroupsByShortNames.class);

    /**
     * Group follower ids DAO.
     */
    private GetGroupFollowerIds groupFollowersDAO = context.mock(GetGroupFollowerIds.class);

    /**
     * ActivityDTO.
     */
    private ActivityDTO activityDTO = context.mock(ActivityDTO.class);

    /**
     * Service action context.
     */
    private ServiceActionContext serviceActionContext = context.mock(ServiceActionContext.class);

    /**
     * user details.
     */
    private Principal userPrincipal = context.mock(Principal.class);

    /**
     * StreamEntityDTO.
     */
    private StreamEntityDTO streamDTO = context.mock(StreamEntityDTO.class);

    /**
     * StreamScope.
     */
    private final StreamScope streamScope = context.mock(StreamScope.class);

    /**
     * DomainGroupModelView.
     */
    private final DomainGroupModelView groupDTO = context.mock(DomainGroupModelView.class);

    /**
     * Actor retrieval strategy mock.
     */
    private final ActorRetrievalStrategy actorRetrievalStrat = context.mock(ActorRetrievalStrategy.class);

    /**
     * Person id.
     */
    private final long personId = 5L;

    /**
     * StreamDTO id.
     */
    private final String streamDTOUniqueId = "uniqueid";

    /**
     * Group id.
     */
    private final long groupId = 6L;

    /**
     * StreamScope unique key.
     */
    private final String scopeKey = "key";

    /**
     * Actor account id.
     */
    private static final String ACTOR_ACCOUNT_ID = "accountid";

    /**
     * System under test.
     */
    private ActivityAuthorizationStrategy sut;

    /**
     * A person Mapper mock.
     */
    private final PersonMapper personMapperMock = context.mock(PersonMapper.class);

    /**
     * A domain group Mock.
     */
    private final DomainGroupMapper groupMapperMock = context.mock(DomainGroupMapper.class);

    /**
     * The Mock for getting all coordinators.
     */
    private final GetAllPersonIdsWhoHaveGroupCoordinatorAccess coordinatorMapperMock = context
            .mock(GetAllPersonIdsWhoHaveGroupCoordinatorAccess.class);

    /**
     * Mock instance of {@link ActivityDTOFromParamsStrategy} for retrieving the activity dto from the params.
     */
    private final ActivityDTOFromParamsStrategy activityDTOStrategyMock = context
            .mock(ActivityDTOFromParamsStrategy.class);

    /**
     * A real person object to use.
     */
    private Person realPersonObject;

    /**
     * A real group object to use.
     */
    private DomainGroup realGroupObject;

    /**
     * Activity id.
     */
    private final Long activityId = new Long(38271);

    /**
     * Setup sut before each test.
     */
    @Before
    public void setUp()
    {
        sut = new ActivityAuthorizationStrategy(groupByShortNameDAO, groupFollowersDAO, actorRetrievalStrat,
                personMapperMock, groupMapperMock, coordinatorMapperMock, activityDTOStrategyMock,
                ActivityInteractionType.POST);

        context.checking(new Expectations()
        {
            {
                allowing(serviceActionContext).getPrincipal();
                will(returnValue(userPrincipal));

                oneOf(serviceActionContext).getParams();
                will(returnValue(activityId));
            }
        });

        realPersonObject = new Person("IOwnThis", "I", "Own", "This", "Stream");
        realGroupObject = new DomainGroup("This is the group", streamDTOUniqueId, realPersonObject);
    }

    /**
     * Execute with Person as recipient.
     *
     * @throws Exception
     *             - on error.
     */
    @Test
    public void testExecutePersonStreamRecipient() throws Exception
    {
        final List<StreamScope> streamScopes = new ArrayList<StreamScope>(1);
        streamScopes.add(streamScope);

        context.checking(new Expectations()
        {
            {
                oneOf(activityDTOStrategyMock).execute(userPrincipal, activityId);
                will(returnValue(activityDTO));

                oneOf(activityDTO).getDestinationStream();
                will(returnValue(streamDTO));

                oneOf(streamDTO).getType();
                will(returnValue(EntityType.PERSON));

                oneOf(activityDTO).getDestinationStream();
                will(returnValue(streamDTO));

                oneOf(streamDTO).getUniqueIdentifier();
                will(returnValue("IOwnThis"));

                oneOf(personMapperMock).findByAccountId("IOwnThis");
                will(returnValue(realPersonObject));

                oneOf(actorRetrievalStrat).getActorAccountId(userPrincipal, activityDTO);
                will(returnValue(ACTOR_ACCOUNT_ID));
            }
        });

        sut.authorize(serviceActionContext);
        context.assertIsSatisfied();
    }

    /**
     * Execute with Person as recipient.
     *
     * @throws Exception
     *             - on error.
     */
    @Test(expected = AuthorizationException.class)
    public void testExecutePersonStreamRecipientWithError() throws Exception
    {
        final Serializable[] params = { activityDTO };
        final List<StreamScope> streamScopes = new ArrayList<StreamScope>(1);
        streamScopes.add(streamScope);

        context.checking(new Expectations()
        {
            {
                oneOf(activityDTOStrategyMock).execute(userPrincipal, activityId);
                will(returnValue(activityDTO));

                oneOf(activityDTO).getDestinationStream();
                will(returnValue(streamDTO));

                oneOf(streamDTO).getType();
                will(returnValue(EntityType.PERSON));

                oneOf(activityDTO).getDestinationStream();
                will(returnValue(streamDTO));

                oneOf(streamDTO).getUniqueIdentifier();
                will(returnValue("IOwnThis"));

                oneOf(personMapperMock).findByAccountId("IOwnThis");
                will(throwException(new Exception()));
            }
        });

        sut.authorize(serviceActionContext);
        context.assertIsSatisfied();
    }

    /**
     * Execute with public group as recipient.
     *
     * @throws Exception
     *             - on error.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecutePublicGroupStreamRecipient() throws Exception
    {

        final Serializable[] params = { activityDTO };
        final List<StreamScope> streamScopes = new ArrayList<StreamScope>(1);
        streamScopes.add(streamScope);

        final List<DomainGroupModelView> groupDTOs = new ArrayList<DomainGroupModelView>(1);
        groupDTOs.add(groupDTO);

        context.checking(new Expectations()
        {
            {
                oneOf(activityDTOStrategyMock).execute(userPrincipal, activityId);
                will(returnValue(activityDTO));

                oneOf(activityDTO).getDestinationStream();
                will(returnValue(streamDTO));

                oneOf(streamDTO).getType();
                will(returnValue(EntityType.GROUP));

                oneOf(actorRetrievalStrat).getActorId(userPrincipal, activityDTO);
                will(returnValue(personId));

                oneOf(coordinatorMapperMock).execute(0L);

                oneOf(streamDTO).getUniqueIdentifier();
                will(returnValue(streamDTOUniqueId));

                oneOf(groupByShortNameDAO).execute(with(any(ArrayList.class)));
                will(returnValue(groupDTOs));

                oneOf(groupDTO).isPublic();
                will(returnValue(true));

                oneOf(activityDTO).getDestinationStream();
                will(returnValue(streamDTO));

                oneOf(streamDTO).getUniqueIdentifier();
                will(returnValue(streamDTOUniqueId));

                oneOf(groupMapperMock).findByShortName(streamDTOUniqueId);
                will(returnValue(realGroupObject));
            }
        });

        sut.authorize(serviceActionContext);
        context.assertIsSatisfied();
    }

    /**
     * Execute with private group as recipient, user not follower or coordinator.
     *
     * @throws Exception
     *             - on error.
     */
    @SuppressWarnings("unchecked")
    @Test(expected = AuthorizationException.class)
    public void testExecutePrivateGroupStreamRecipientIsNotFollowerOrCoordinator() throws Exception
    {
        final Serializable[] params = { activityDTO };
        final List<StreamScope> streamScopes = new ArrayList<StreamScope>(1);
        streamScopes.add(streamScope);

        final List<DomainGroupModelView> groupDTOs = new ArrayList<DomainGroupModelView>(1);
        groupDTOs.add(groupDTO);

        context.checking(new Expectations()
        {
            {
                oneOf(activityDTOStrategyMock).execute(userPrincipal, activityId);
                will(returnValue(activityDTO));

                oneOf(activityDTO).getDestinationStream();
                will(returnValue(streamDTO));

                oneOf(streamDTO).getType();
                will(returnValue(EntityType.GROUP));

                oneOf(activityDTO).getDestinationStream();
                will(returnValue(streamDTO));

                oneOf(streamDTO).getUniqueIdentifier();
                will(returnValue(streamDTOUniqueId));

                oneOf(activityDTO).getDestinationStream();
                will(returnValue(streamDTO));

                oneOf(streamDTO).getUniqueIdentifier();
                will(returnValue(streamDTOUniqueId));

                oneOf(actorRetrievalStrat).getActorAccountId(userPrincipal, activityDTO);
                will(returnValue(ACTOR_ACCOUNT_ID));

                oneOf(streamScope).getScopeType();
                will(returnValue(ScopeType.GROUP));

                oneOf(streamScope).getUniqueKey();
                will(returnValue(scopeKey));

                oneOf(groupByShortNameDAO).execute(with(any(ArrayList.class)));
                will(returnValue(groupDTOs));

                oneOf(groupDTO).isPublic();
                will(returnValue(false));

                oneOf(actorRetrievalStrat).getActorId(userPrincipal, activityDTO);
                will(returnValue(personId));

                oneOf(groupDTO).getEntityId();
                will(returnValue(groupId));

                oneOf(groupFollowersDAO).execute(groupId);
                will(returnValue(new ArrayList<Long>(0)));

                oneOf(groupMapperMock).findByShortName(streamDTOUniqueId);
                will(returnValue(realGroupObject));

                oneOf(coordinatorMapperMock).execute(0L);

                oneOf(actorRetrievalStrat).getActorId(userPrincipal, activityDTO);
                will(returnValue(personId));
            }
        });

        sut.authorize(serviceActionContext);
        context.assertIsSatisfied();
    }

    /**
     * Execute with private group as recipient, user is follower, not coordinator.
     *
     * @throws Exception
     *             - on error.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecutePrivateGroupStreamRecipientIsFollower() throws Exception
    {

        final Serializable[] params = { activityDTO };
        final List<StreamScope> streamScopes = new ArrayList<StreamScope>(1);
        streamScopes.add(streamScope);

        final List<DomainGroupModelView> groupDTOs = new ArrayList<DomainGroupModelView>(1);
        groupDTOs.add(groupDTO);

        final List<Long> followers = new ArrayList<Long>(1);
        followers.add(personId);

        context.checking(new Expectations()
        {
            {
                oneOf(activityDTOStrategyMock).execute(userPrincipal, activityId);
                will(returnValue(activityDTO));

                oneOf(activityDTO).getDestinationStream();
                will(returnValue(streamDTO));

                oneOf(streamDTO).getType();
                will(returnValue(EntityType.GROUP));

                oneOf(streamDTO).getUniqueIdentifier();
                will(returnValue(streamDTOUniqueId));

                oneOf(groupMapperMock).findByShortName(streamDTOUniqueId);
                will(returnValue(realGroupObject));

                oneOf(coordinatorMapperMock).execute(0L);

                oneOf(actorRetrievalStrat).getActorId(userPrincipal, activityDTO);
                will(returnValue(personId));

                oneOf(activityDTO).getDestinationStream();
                will(returnValue(streamDTO));

                oneOf(streamDTO).getUniqueIdentifier();
                will(returnValue(streamDTOUniqueId));

                oneOf(groupByShortNameDAO).execute(with(any(ArrayList.class)));
                will(returnValue(groupDTOs));

                oneOf(groupDTO).isPublic();
                will(returnValue(false));

                oneOf(groupDTO).getEntityId();
                will(returnValue(groupId));

                oneOf(groupFollowersDAO).execute(groupId);
                will(returnValue(followers));
            }
        });

        sut.authorize(serviceActionContext);
        context.assertIsSatisfied();
    }

    /**
     * Execute with private group as recipient, user is coordinator, not follower.
     *
     * @throws Exception
     *             - on error.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecutePrivateGroupStreamRecipientIsCoordinator() throws Exception
    {
        final Serializable[] params = { activityDTO };
        final List<StreamScope> streamScopes = new ArrayList<StreamScope>(1);
        streamScopes.add(streamScope);

        final List<DomainGroupModelView> groupDTOs = new ArrayList<DomainGroupModelView>(1);
        groupDTOs.add(groupDTO);

        final Set<Long> coordinators = new HashSet<Long>(1);
        coordinators.add(personId);

        context.checking(new Expectations()
        {
            {
                oneOf(activityDTOStrategyMock).execute(userPrincipal, activityId);
                will(returnValue(activityDTO));

                oneOf(activityDTO).getDestinationStream();
                will(returnValue(streamDTO));

                oneOf(streamDTO).getType();
                will(returnValue(EntityType.GROUP));

                oneOf(activityDTO).getDestinationStream();
                will(returnValue(streamDTO));

                oneOf(streamDTO).getUniqueIdentifier();
                will(returnValue(streamDTOUniqueId));

                oneOf(streamDTO).getUniqueIdentifier();
                will(returnValue(streamDTOUniqueId));

                oneOf(groupMapperMock).findByShortName(streamDTOUniqueId);
                will(returnValue(realGroupObject));

                oneOf(coordinatorMapperMock).execute(0L);
                will(returnValue(coordinators));

                oneOf(actorRetrievalStrat).getActorId(userPrincipal, activityDTO);
                will(returnValue(personId));

                oneOf(groupByShortNameDAO).execute(with(any(ArrayList.class)));
                will(returnValue(groupDTOs));

                oneOf(groupDTO).isPublic();
                will(returnValue(false));
            }
        });

        sut.authorize(serviceActionContext);
        context.assertIsSatisfied();
    }

    /**
     * Test method to test the case where retrieving the activity dto fails.
     */
    @Test(expected = AuthorizationException.class)
    public void testFailedActivityDTOParamsRetrieval()
    {
        context.checking(new Expectations()
        {
            {
                oneOf(activityDTOStrategyMock).execute(userPrincipal, activityId);
                will(throwException(new Exception()));
            }
        });

        sut.authorize(serviceActionContext);
        context.assertIsSatisfied();
    }
}
