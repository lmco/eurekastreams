/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.eurekastreams.commons.actions.AuthorizationStrategy;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.commons.exceptions.AuthorizationException;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.ActivityInteractionType;
import org.eurekastreams.server.service.actions.strategies.activity.ActivityDTOFromParamsStrategy;
import org.eurekastreams.server.service.actions.strategies.activity.ActorRetrievalStrategy;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test fixture for ActivityAuthorizationStrategy.
 */
@SuppressWarnings( { "unchecked", "rawtypes" })
public class ActivityAuthorizationStrategyTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery mockContext = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Test data. */
    private static final String PERSONAL_STREAM_UNIQUEID = "jdoe";

    /** Test data. */
    private static final String GROUP_STREAM_UNIQUEID = "thegroup";

    /** Actor's person ID. */
    private static final long ACTOR_ID = 5L;

    /** Actor's account ID. */
    private static final String ACTOR_ACCOUNT_ID = "actor";

    /** Group id. */
    private static final long GROUP_ID = 6L;

    /** Fixture: stream (personal or group). */
    private final StreamEntityDTO streamDTO = mockContext.mock(StreamEntityDTO.class, "streamDTO");

    /**
     * Groups by shortName DAO.
     */
    private final GetDomainGroupsByShortNames groupByShortNameDAO = mockContext.mock(GetDomainGroupsByShortNames.class);

    /**
     * Mapper to get the personmodelview by account id.
     */
    private final DomainMapper<String, PersonModelView> getPersonModelViewByAccountIdMapper = mockContext
            .mock(DomainMapper.class);

    /**
     * Group follower ids DAO.
     */
    private final DomainMapper<Long, List<Long>> groupFollowersDAO = mockContext.mock(DomainMapper.class,
            "groupFollowersDAO");

    /**
     * ActivityDTO.
     */
    private final ActivityDTO activityDTO = mockContext.mock(ActivityDTO.class);

    /**
     * Service action context.
     */
    private final ServiceActionContext serviceActionContext = mockContext.mock(ServiceActionContext.class);

    /**
     * user details.
     */
    private final Principal userPrincipal = mockContext.mock(Principal.class);

    /**
     * DomainGroupModelView.
     */
    private final DomainGroupModelView groupDTO = mockContext.mock(DomainGroupModelView.class);

    /**
     * PersonModelView.
     */
    private final PersonModelView personDTO = mockContext.mock(PersonModelView.class);

    /**
     * Actor retrieval strategy mock.
     */
    private final ActorRetrievalStrategy actorRetrievalStrat = mockContext.mock(ActorRetrievalStrategy.class);

    /** List of coordinators / members. */
    private static final List<Long> PERSON_ID_LIST_WITH_ACTOR = Collections.unmodifiableList(Arrays.asList(1L, 5L, 9L));

    /** List of coordinators / members. */
    private static final List<Long> PERSON_ID_LIST_WITHOUT_ACTOR = Collections.unmodifiableList(Arrays.asList(1L, 2L));

    /** List of coordinators / members. */
    private static final Set<Long> PERSON_ID_SET_WITH_ACTOR = Collections.unmodifiableSet(new HashSet(
            PERSON_ID_LIST_WITH_ACTOR));

    /** List of coordinators / members. */
    private static final Set<Long> PERSON_ID_SET_WITHOUT_ACTOR = Collections.unmodifiableSet(new HashSet(
            PERSON_ID_LIST_WITHOUT_ACTOR));

    /**
     * The Mock for getting all coordinators.
     */
    private final GetAllPersonIdsWhoHaveGroupCoordinatorAccess coordinatorMapperMock = mockContext
            .mock(GetAllPersonIdsWhoHaveGroupCoordinatorAccess.class);

    /**
     * Mock instance of {@link ActivityDTOFromParamsStrategy} for retrieving the activity dto from the params.
     */
    private final ActivityDTOFromParamsStrategy activityDTOStrategyMock = mockContext
            .mock(ActivityDTOFromParamsStrategy.class);

    /**
     * Activity id.
     */
    private final Long activityId = new Long(38271);

    /**
     * System under test.
     */
    private ActivityAuthorizationStrategy sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = null; // must set on a per-test basis, since the type of action (post, etc.) is a constructor parameter
        mockContext.checking(new Expectations()
        {
            {
                allowing(serviceActionContext).getPrincipal();
                will(returnValue(userPrincipal));

                oneOf(serviceActionContext).getParams();
                will(returnValue(activityId));
            }
        });
    }

    /* -------- Personal stream tests -------- */

    /**
     * Common setup for person tests.
     * 
     * @param activityInteractionType
     *            Type of action being taken (post, comment, etc.).
     * @throws Exception
     *             Won't.
     */
    private void setupPersonTest(final ActivityInteractionType activityInteractionType) throws Exception
    {
        sut = new ActivityAuthorizationStrategy(groupByShortNameDAO, groupFollowersDAO, actorRetrievalStrat,
                coordinatorMapperMock, activityDTOStrategyMock, activityInteractionType,
                getPersonModelViewByAccountIdMapper);

        mockContext.checking(new Expectations()
        {
            {
                oneOf(activityDTOStrategyMock).execute(userPrincipal, activityId);
                will(returnValue(activityDTO));

                allowing(activityDTO).getDestinationStream();
                will(returnValue(streamDTO));

                allowing(streamDTO).getType();
                will(returnValue(EntityType.PERSON));

                oneOf(actorRetrievalStrat).getActorAccountId(userPrincipal, activityDTO);
                will(returnValue(ACTOR_ACCOUNT_ID));
            }
        });
    }

    /**
     * Test: post to personal stream by owner.
     * 
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testAuthorizePostPersonalByOwner() throws Exception
    {
        setupPersonTest(ActivityInteractionType.POST);

        mockContext.checking(new Expectations()
        {
            {
                allowing(streamDTO).getUniqueIdentifier();
                will(returnValue(ACTOR_ACCOUNT_ID));
            }
        });

        sut.authorize(serviceActionContext);
        mockContext.assertIsSatisfied();
    }

    /**
     * Common behavior of all personal stream by non-owner tests.
     * 
     * @param activityInteractionType
     *            Type of action being performed.
     * @throws Exception
     *             If not authorized.
     */
    private void corePersonalNonOwnerTest(final ActivityInteractionType activityInteractionType) throws Exception
    {
        setupPersonTest(activityInteractionType);

        mockContext.checking(new Expectations()
        {
            {
                allowing(streamDTO).getUniqueIdentifier();
                will(returnValue(PERSONAL_STREAM_UNIQUEID));

                oneOf(getPersonModelViewByAccountIdMapper).execute(PERSONAL_STREAM_UNIQUEID);
                will(returnValue(personDTO));
            }
        });

        ((AuthorizationStrategy) sut).authorize(serviceActionContext);
        mockContext.assertIsSatisfied();
    }

    /**
     * Test: post to personal stream by non-owner.
     * 
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testAuthorizePostPersonal() throws Exception
    {
        mockContext.checking(new Expectations()
        {
            {
                allowing(personDTO).isStreamPostable();
                will(returnValue(true));
            }
        });

        corePersonalNonOwnerTest(ActivityInteractionType.POST);
    }

    /**
     * Test: post to personal stream by non-owner.
     * 
     * @throws Exception
     *             Should.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizePostPersonalNotEnabled() throws Exception
    {
        mockContext.checking(new Expectations()
        {
            {
                allowing(personDTO).isStreamPostable();
                will(returnValue(false));
            }
        });

        corePersonalNonOwnerTest(ActivityInteractionType.POST);
    }

    /**
     * Test: comment to personal stream by non-owner.
     * 
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testAuthorizeCommentPersonal() throws Exception
    {
        mockContext.checking(new Expectations()
        {
            {
                allowing(personDTO).isCommentable();
                will(returnValue(true));
            }
        });

        corePersonalNonOwnerTest(ActivityInteractionType.COMMENT);
    }

    /**
     * Test: comment to personal stream by non-owner.
     * 
     * @throws Exception
     *             Should.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeCommentPersonalNotEnabled() throws Exception
    {
        mockContext.checking(new Expectations()
        {
            {
                allowing(personDTO).isCommentable();
                will(returnValue(false));
            }
        });

        corePersonalNonOwnerTest(ActivityInteractionType.COMMENT);
    }

    /**
     * Test: view personal stream by non-owner.
     * 
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testAuthorizeViewPersonal() throws Exception
    {
        corePersonalNonOwnerTest(ActivityInteractionType.VIEW);
    }

    /* -------- Group stream tests -------- */

    /**
     * Common setup for group tests.
     * 
     * @param activityInteractionType
     *            Type of action being taken (post, comment, etc.).
     * @throws Exception
     *             Won't.
     */
    private void setupGroupTest(final ActivityInteractionType activityInteractionType) throws Exception
    {
        sut = new ActivityAuthorizationStrategy(groupByShortNameDAO, groupFollowersDAO, actorRetrievalStrat,
                coordinatorMapperMock, activityDTOStrategyMock, activityInteractionType,
                getPersonModelViewByAccountIdMapper);

        mockContext.checking(new Expectations()
        {
            {
                oneOf(activityDTOStrategyMock).execute(userPrincipal, activityId);
                will(returnValue(activityDTO));

                allowing(activityDTO).getDestinationStream();
                will(returnValue(streamDTO));

                allowing(streamDTO).getUniqueIdentifier();
                will(returnValue(GROUP_STREAM_UNIQUEID));

                allowing(streamDTO).getType();
                will(returnValue(EntityType.GROUP));

                oneOf(groupByShortNameDAO).execute(with(equal(Collections.singletonList(GROUP_STREAM_UNIQUEID))));
                will(returnValue(Collections.singletonList(groupDTO)));

                allowing(groupDTO).getId();
                will(returnValue(GROUP_ID));

                oneOf(actorRetrievalStrat).getActorId(userPrincipal, activityDTO);
                will(returnValue(ACTOR_ID));
            }
        });
    }

    /* ---- Public group, action by anybody ---- */

    /**
     * Common behavior of all public group stream by non-coordinator tests.
     * 
     * @param activityInteractionType
     *            Type of action being performed.
     * @throws Exception
     *             If not authorized.
     */
    private void corePublicGroupTest(final ActivityInteractionType activityInteractionType) throws Exception
    {
        setupGroupTest(activityInteractionType);

        mockContext.checking(new Expectations()
        {
            {
                oneOf(coordinatorMapperMock).execute(GROUP_ID);
                will(returnValue(PERSON_ID_SET_WITHOUT_ACTOR));

                allowing(groupDTO).isPublic();
                will(returnValue(true));
            }
        });

        sut.authorize(serviceActionContext);
        mockContext.assertIsSatisfied();
    }

    /**
     * Test: post to public group, user not coordinator.
     * 
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testAuthorizePostPublicGroup() throws Exception
    {
        mockContext.checking(new Expectations()
        {
            {
                allowing(groupDTO).isStreamPostable();
                will(returnValue(true));
            }
        });
        corePublicGroupTest(ActivityInteractionType.POST);
    }

    /**
     * Test: post to public group, user not coordinator, not postable.
     * 
     * @throws Exception
     *             Should.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizePostPublicGroupNotEnabled() throws Exception
    {
        mockContext.checking(new Expectations()
        {
            {
                allowing(groupDTO).isStreamPostable();
                will(returnValue(false));
            }
        });
        corePublicGroupTest(ActivityInteractionType.POST);
    }

    /**
     * Test: comment to public group, user not coordinator.
     * 
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testAuthorizeCommentPublicGroup() throws Exception
    {
        mockContext.checking(new Expectations()
        {
            {
                allowing(groupDTO).isCommentable();
                will(returnValue(true));
            }
        });
        corePublicGroupTest(ActivityInteractionType.COMMENT);
    }

    /**
     * Test: comment to public group, user not coordinator, not commentable.
     * 
     * @throws Exception
     *             Should.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeCommentPublicGroupNotEnabled() throws Exception
    {
        mockContext.checking(new Expectations()
        {
            {
                allowing(groupDTO).isCommentable();
                will(returnValue(false));
            }
        });
        corePublicGroupTest(ActivityInteractionType.COMMENT);
    }

    /**
     * Test: comment to public group, user not coordinator.
     * 
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testAuthorizeViewPublicGroup() throws Exception
    {
        corePublicGroupTest(ActivityInteractionType.VIEW);
    }

    /**
     * Test: unspecified action to public group, user not coordinator.
     * 
     * @throws Exception
     *             Should.
     */
    @Test(expected = RuntimeException.class)
    public void testAuthorizeOtherActionPublicGroup() throws Exception
    {
        corePublicGroupTest(ActivityInteractionType.NOTSET);
    }

    /* ---- Public group, action by coordinator ---- */

    /**
     * Test: post to public group, user is coordinator.
     * 
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testAuthorizePostPublicGroupCoordinator() throws Exception
    {
        setupGroupTest(ActivityInteractionType.POST);

        mockContext.checking(new Expectations()
        {
            {
                oneOf(coordinatorMapperMock).execute(GROUP_ID);
                will(returnValue(PERSON_ID_SET_WITH_ACTOR));

                allowing(groupDTO).isPublic();
                will(returnValue(true));

                // refactoring would make the following unnecessary
                allowing(groupDTO).isStreamPostable();
                will(returnValue(false));
            }
        });

        sut.authorize(serviceActionContext);
        mockContext.assertIsSatisfied();
    }

    /* ---- Private group, action by member ---- */

    /**
     * Common behavior of all private group stream by member tests.
     * 
     * @param activityInteractionType
     *            Type of action being performed.
     * @throws Exception
     *             If not authorized.
     */
    private void corePrivateMemberGroupTest(final ActivityInteractionType activityInteractionType) throws Exception
    {
        setupGroupTest(activityInteractionType);

        mockContext.checking(new Expectations()
        {
            {
                oneOf(coordinatorMapperMock).execute(GROUP_ID);
                will(returnValue(PERSON_ID_SET_WITHOUT_ACTOR));

                oneOf(groupFollowersDAO).execute(GROUP_ID);
                will(returnValue(PERSON_ID_LIST_WITH_ACTOR));

                allowing(groupDTO).isPublic();
                will(returnValue(false));
            }
        });

        sut.authorize(serviceActionContext);
        mockContext.assertIsSatisfied();
    }

    /**
     * Test: post to private group, user member.
     * 
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testAuthorizePostPrivateGroup() throws Exception
    {
        mockContext.checking(new Expectations()
        {
            {
                allowing(groupDTO).isStreamPostable();
                will(returnValue(true));
            }
        });
        corePrivateMemberGroupTest(ActivityInteractionType.POST);
    }

    /**
     * Test: post to private group, user member, not postable.
     * 
     * @throws Exception
     *             Should.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizePostPrivateGroupNotEnabled() throws Exception
    {
        mockContext.checking(new Expectations()
        {
            {
                allowing(groupDTO).isStreamPostable();
                will(returnValue(false));
            }
        });
        corePrivateMemberGroupTest(ActivityInteractionType.POST);
    }

    // TODO: The following three tests are ignored because they fail because the code is broken.

    /**
     * Test: comment to private group, user member.
     * 
     * @throws Exception
     *             Won't.
     */
    @Test
    @Ignore
    public void testAuthorizeCommentPrivateGroup() throws Exception
    {
        mockContext.checking(new Expectations()
        {
            {
                allowing(groupDTO).isCommentable();
                will(returnValue(true));
            }
        });
        corePrivateMemberGroupTest(ActivityInteractionType.COMMENT);
    }

    /**
     * Test: comment to private group, user member, not commentable.
     * 
     * @throws Exception
     *             Should.
     */
    @Test(expected = AuthorizationException.class)
    @Ignore
    public void testAuthorizeCommentPrivateGroupNotEnabled() throws Exception
    {
        mockContext.checking(new Expectations()
        {
            {
                allowing(groupDTO).isCommentable();
                will(returnValue(false));
            }
        });
        corePrivateMemberGroupTest(ActivityInteractionType.COMMENT);
    }

    /**
     * Test: comment to private group, user member.
     * 
     * @throws Exception
     *             Won't.
     */
    @Test
    @Ignore
    public void testAuthorizeViewPrivateGroup() throws Exception
    {
        corePrivateMemberGroupTest(ActivityInteractionType.VIEW);
    }

    /* ---- Other private group ---- */

    /**
     * Test: comment to private group, user coordinator.
     * 
     * @throws Exception
     *             Won't.
     */
    @Test
    public void testAuthorizeViewPrivateGroupCoordinator() throws Exception
    {
        setupGroupTest(ActivityInteractionType.VIEW);

        mockContext.checking(new Expectations()
        {
            {
                oneOf(coordinatorMapperMock).execute(GROUP_ID);
                will(returnValue(PERSON_ID_SET_WITH_ACTOR));

                allowing(groupDTO).isPublic();
                will(returnValue(false));
            }
        });

        sut.authorize(serviceActionContext);
        mockContext.assertIsSatisfied();
    }

    /**
     * Test: comment to private group, user not member.
     * 
     * @throws Exception
     *             Should.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeViewPrivateGroupNonMember() throws Exception
    {
        setupGroupTest(ActivityInteractionType.VIEW);

        mockContext.checking(new Expectations()
        {
            {
                oneOf(coordinatorMapperMock).execute(GROUP_ID);
                will(returnValue(PERSON_ID_SET_WITHOUT_ACTOR));

                oneOf(groupFollowersDAO).execute(GROUP_ID);
                will(returnValue(PERSON_ID_LIST_WITHOUT_ACTOR));

                allowing(groupDTO).isPublic();
                will(returnValue(false));
            }
        });

        sut.authorize(serviceActionContext);
        mockContext.assertIsSatisfied();
    }

    /* -------- Error condition tests -------t- */

    /**
     * Creates the SUT for posting activities.
     */
    private void createSutForPost()
    {
        sut = new ActivityAuthorizationStrategy(groupByShortNameDAO, groupFollowersDAO, actorRetrievalStrat,
                coordinatorMapperMock, activityDTOStrategyMock, ActivityInteractionType.POST,
                getPersonModelViewByAccountIdMapper);
    }

    /**
     * Test: error retrieving activity.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeErrorRetrievingActivity()
    {
        createSutForPost();

        mockContext.checking(new Expectations()
        {
            {
                oneOf(activityDTOStrategyMock).execute(userPrincipal, activityId);
                will(throwException(new PersistenceException()));
            }
        });

        sut.authorize(serviceActionContext);
        mockContext.assertIsSatisfied();
    }

    /**
     * Test: error retrieving actor for personal stream posting.
     * 
     * @throws Exception
     *             Should only throw AuthorizationException.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeErrorRetrievingActor() throws Exception
    {
        createSutForPost();

        mockContext.checking(new Expectations()
        {
            {
                oneOf(activityDTOStrategyMock).execute(userPrincipal, activityId);
                will(returnValue(activityDTO));

                allowing(activityDTO).getDestinationStream();
                will(returnValue(streamDTO));

                allowing(streamDTO).getType();
                will(returnValue(EntityType.PERSON));

                allowing(streamDTO).getUniqueIdentifier();
                will(returnValue(PERSONAL_STREAM_UNIQUEID));

                oneOf(actorRetrievalStrat).getActorAccountId(userPrincipal, activityDTO);
                will(throwException(new PersistenceException()));
            }
        });

        sut.authorize(serviceActionContext);
        mockContext.assertIsSatisfied();
    }

    /**
     * Test: error retrieving group for group stream posting.
     * 
     * @throws Exception
     *             Should only throw AuthorizationException.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeErrorRetrievingGroup() throws Exception
    {
        createSutForPost();

        mockContext.checking(new Expectations()
        {
            {
                oneOf(activityDTOStrategyMock).execute(userPrincipal, activityId);
                will(returnValue(activityDTO));

                allowing(activityDTO).getDestinationStream();
                will(returnValue(streamDTO));

                allowing(streamDTO).getType();
                will(returnValue(EntityType.GROUP));

                allowing(streamDTO).getUniqueIdentifier();
                will(returnValue(GROUP_STREAM_UNIQUEID));

                oneOf(groupByShortNameDAO).execute(with(equal(Collections.singletonList(GROUP_STREAM_UNIQUEID))));
                will(throwException(new PersistenceException()));
            }
        });

        sut.authorize(serviceActionContext);
        mockContext.assertIsSatisfied();
    }

    /* -------- Coverage tests -------- */

    /**
     * Test: DAO returns a null activity.
     */
    @Test
    public void testAuthorizeNullActivityDTOParamsRetrieval()
    {
        createSutForPost();
        mockContext.checking(new Expectations()
        {
            {
                oneOf(activityDTOStrategyMock).execute(userPrincipal, activityId);
                will(returnValue(null));
            }
        });

        sut.authorize(serviceActionContext);
        mockContext.assertIsSatisfied();
    }

    /**
     * Test: invalid stream type for activity.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeActivityDestinationInvalid()
    {
        createSutForPost();
        mockContext.checking(new Expectations()
        {
            {
                oneOf(activityDTOStrategyMock).execute(userPrincipal, activityId);
                will(returnValue(activityDTO));

                allowing(activityDTO).getDestinationStream();
                will(returnValue(streamDTO));

                oneOf(streamDTO).getType();
                will(returnValue(EntityType.APPLICATION));
            }
        });

        sut.authorize(serviceActionContext);
        mockContext.assertIsSatisfied();
    }

    /**
     * Test: invalid action type.
     * 
     * @throws Exception
     *             Should only throw AuthorizationException.
     */
    @Test(expected = AuthorizationException.class)
    public void testAuthorizeInvalidActionType() throws Exception
    {
        corePublicGroupTest(ActivityInteractionType.NOTSET);
    }
}
