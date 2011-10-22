/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.service.utility.authorization;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.GetAllPersonIdsWhoHaveGroupCoordinatorAccess;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.ActivityInteractionType;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests ActivityInteractionAuthorizationStrategy.
 *
 * Note that post and comment authorization checks are almost alike; so testing both for each type of entity would be
 * redundant. Thus testing post with person and comment with groups.
 */
public class ActivityInteractionAuthorizationStrategyTest
{
    /** Test data. */
    private static final long PERSON_STREAM_OWNER = 100L;

    /** Test data. */
    private static final long PERSON_COORD_ADMIN = 101L;

    /** Test data. */
    private static final long PERSON_MEMBER = 102L;

    /** Test data. */
    private static final long PERSON_ANYONE = 103L;

    /** Test data. */
    private static final long GROUP_ID = 104L;

    /** Used for mocking objects. */
    private final Mockery mockery = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** DAO to get person by ID. */
    private final DomainMapper<Long, PersonModelView> getPersonByIdDAO = mockery.mock(DomainMapper.class,
            "getPersonByIdDAO");

    /** DAO to get group by ID. */
    private final DomainMapper<Long, DomainGroupModelView> getGroupByIdDAO = mockery.mock(DomainMapper.class,
            "getGroupByIdDAO");

    /** DAO to get group follower IDs. */
    private final DomainMapper<Long, List<Long>> groupFollowersDAO = mockery.mock(DomainMapper.class,
            "groupFollowersDAO");

    /** DAO to get all coordinators of a group. */
    private final GetAllPersonIdsWhoHaveGroupCoordinatorAccess groupCoordDAO = mockery.mock(
            GetAllPersonIdsWhoHaveGroupCoordinatorAccess.class, "groupCoordDAO");

    // getPersonByIdMapper

    /** DAO to get all system administrators. */
    private final DomainMapper<Serializable, List<Long>> getSystemAdministratorIdsDAO = mockery.mock(
            DomainMapper.class, "getSystemAdministratorIdsDAO");

    /** Fixture: stream. */
    private final StreamEntityDTO stream = mockery.mock(StreamEntityDTO.class, "stream");

    /** Fixture: activity. */
    private final ActivityDTO activity = mockery.mock(ActivityDTO.class, "activity");

    /** SUT. */
    private ActivityInteractionAuthorizationStrategy sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new ActivityInteractionAuthorizationStrategy(getPersonByIdDAO, getGroupByIdDAO, groupFollowersDAO,
                groupCoordDAO, getSystemAdministratorIdsDAO);
        mockery.checking(new Expectations()
        {
            {
                allowing(activity).getDestinationStream();
                will(returnValue(stream));
            }
        });
    }

    // ---------- PERSON TESTS ----------

    /**
     * Sets up expectations common to all person stream tests.
     *
     * @return Person.
     */
    private PersonModelView expectPersonStream()
    {
        final PersonModelView person = mockery.mock(PersonModelView.class);
        mockery.checking(new Expectations()
        {
            {
                allowing(stream).getType();
                will(returnValue(EntityType.PERSON));

                allowing(stream).getEntityId();
                will(returnValue(PERSON_STREAM_OWNER));

                allowing(getPersonByIdDAO).execute(PERSON_STREAM_OWNER);
                will(returnValue(person));

                allowing(getSystemAdministratorIdsDAO).execute(with(any(Serializable.class)));
                will(returnValue(Collections.singletonList(PERSON_COORD_ADMIN)));
            }
        });
        return person;
    }

    // ---------- PERSON VIEW TESTS ----------

    /**
     * Core of all person stream view tests.
     *
     * @param testUser
     *            User ID to run test with.
     * @return Result of SUT.
     */
    private boolean corePersonStreamViewTest(final long testUser)
    {
        expectPersonStream();
        boolean result = sut.authorize(testUser, activity, ActivityInteractionType.VIEW);
        mockery.assertIsSatisfied();
        return result;
    }

    /**
     * Test.
     */
    @Test
    public void testPersonStreamAnyoneView()
    {
        assertTrue(corePersonStreamViewTest(PERSON_ANYONE));
    }

    /**
     * Test.
     */
    @Test
    public void testPersonStreamSelfView()
    {
        assertTrue(corePersonStreamViewTest(PERSON_STREAM_OWNER));
    }

    /**
     * Test.
     */
    @Test
    public void testPersonStreamAdminView()
    {
        assertTrue(corePersonStreamViewTest(PERSON_COORD_ADMIN));
    }

    // ---------- PERSON POST TESTS ----------

    /**
     * Core of all person stream action tests.
     *
     * @param testUser
     *            User ID to run test with.
     * @param streamAllowsAction
     *            If the stream should allow posting.
     * @return Result of SUT.
     */
    private boolean corePersonStreamActionTest(final long testUser, final boolean streamAllowsAction)
    {
        final PersonModelView person = expectPersonStream();
        mockery.checking(new Expectations()
        {
            {
                allowing(person).isStreamPostable();
                will(returnValue(streamAllowsAction));
            }
        });
        boolean result = sut.authorize(testUser, activity, ActivityInteractionType.POST);
        mockery.assertIsSatisfied();
        return result;
    }

    /**
     * Test.
     */
    @Test
    public void testPersonStreamAnyoneAllow()
    {
        assertTrue(corePersonStreamActionTest(PERSON_ANYONE, true));
    }

    /**
     * Test.
     */
    @Test
    public void testPersonStreamAnyoneForbid()
    {
        assertFalse(corePersonStreamActionTest(PERSON_ANYONE, false));
    }

    /**
     * Test.
     */
    @Test
    public void testPersonStreamSelfAllow()
    {
        assertTrue(corePersonStreamActionTest(PERSON_STREAM_OWNER, true));
    }

    /**
     * Test.
     */
    @Test
    public void testPersonStreamSelfForbid()
    {
        assertTrue(corePersonStreamActionTest(PERSON_STREAM_OWNER, false));
    }

    /**
     * Test.
     */
    @Test
    public void testPersonStreamAdminAllow()
    {
        assertTrue(corePersonStreamActionTest(PERSON_COORD_ADMIN, true));
    }

    /**
     * Test.
     */
    @Test
    public void testPersonStreamAdminForbid()
    {
        assertTrue(corePersonStreamActionTest(PERSON_COORD_ADMIN, false));
    }

    // ---------- GROUP TESTS ----------

    /**
     * Sets up expectations common to all group stream tests.
     *
     * @param isStreamPublic
     *            If stream is public.
     * @return The group.
     */
    private DomainGroupModelView expectGroupStream(final boolean isStreamPublic)
    {
        final DomainGroupModelView group = mockery.mock(DomainGroupModelView.class);
        mockery.checking(new Expectations()
        {
            {
                allowing(stream).getType();
                will(returnValue(EntityType.GROUP));

                allowing(stream).getEntityId();
                will(returnValue(GROUP_ID));

                allowing(getGroupByIdDAO).execute(GROUP_ID);
                will(returnValue(group));

                allowing(groupFollowersDAO).execute(GROUP_ID);
                will(returnValue(Collections.singletonList(PERSON_MEMBER)));

                allowing(groupCoordDAO).execute(GROUP_ID);
                will(returnValue(Collections.singleton(PERSON_COORD_ADMIN)));

                allowing(group).isPublic();
                will(returnValue(isStreamPublic));
            }
        });
        return group;
    }

    // ---------- GROUP VIEW TESTS ----------

    /**
     * Core of all group stream view tests.
     *
     * @param testUser
     *            User ID to run test with.
     * @param isStreamPublic
     *            If stream is public.
     * @return Result of SUT.
     */
    private boolean coreGroupStreamViewTest(final long testUser, final boolean isStreamPublic)
    {
        expectGroupStream(isStreamPublic);
        boolean result = sut.authorize(testUser, activity, ActivityInteractionType.VIEW);
        mockery.assertIsSatisfied();
        return result;
    }

    /**
     * Test.
     */
    @Test
    public void testPublicGroupAnyoneView()
    {
        assertTrue(coreGroupStreamViewTest(PERSON_ANYONE, true));
    }

    /**
     * Test.
     */
    @Test
    public void testPublicGroupMemberView()
    {
        assertTrue(coreGroupStreamViewTest(PERSON_MEMBER, true));
    }

    /**
     * Test.
     */
    @Test
    public void testPublicGroupCoordView()
    {
        assertTrue(coreGroupStreamViewTest(PERSON_COORD_ADMIN, true));
    }

    /**
     * Test.
     */
    @Test
    public void testPrivateGroupAnyoneView()
    {
        assertFalse(coreGroupStreamViewTest(PERSON_ANYONE, false));
    }

    /**
     * Test.
     */
    @Test
    public void testPrivateGroupMemberView()
    {
        assertTrue(coreGroupStreamViewTest(PERSON_MEMBER, false));
    }

    /**
     * Test.
     */
    @Test
    public void testPrivateGroupCoordView()
    {
        assertTrue(coreGroupStreamViewTest(PERSON_COORD_ADMIN, false));
    }

    // ---------- GROUP COMMENT TESTS ----------

    /**
     * Core of all group stream action tests.
     *
     * @param testUser
     *            User ID to run test with.
     * @param isStreamPublic
     *            If stream is public.
     * @param streamAllowsAction
     *            If the stream should allow posting.
     * @return Result of SUT.
     */
    private boolean coreGroupStreamActionTest(final long testUser, final boolean isStreamPublic,
            final boolean streamAllowsAction)
    {
        final DomainGroupModelView group = expectGroupStream(isStreamPublic);
        mockery.checking(new Expectations()
        {
            {
                allowing(group).isCommentable();
                will(returnValue(streamAllowsAction));
            }
        });
        boolean result = sut.authorize(testUser, activity, ActivityInteractionType.COMMENT);
        mockery.assertIsSatisfied();
        return result;
    }

    /**
     * Test.
     */
    @Test
    public void testPublicGroupAnyoneAllow()
    {
        assertTrue(coreGroupStreamActionTest(PERSON_ANYONE, true, true));
    }

    /**
     * Test.
     */
    @Test
    public void testPublicGroupAnyoneForbid()
    {
        assertFalse(coreGroupStreamActionTest(PERSON_ANYONE, true, false));
    }

    /**
     * Test.
     */
    @Test
    public void testPublicGroupMemberAllow()
    {
        assertTrue(coreGroupStreamActionTest(PERSON_MEMBER, true, true));
    }

    /**
     * Test.
     */
    @Test
    public void testPublicGroupMemberForbid()
    {
        assertFalse(coreGroupStreamActionTest(PERSON_MEMBER, true, false));
    }

    /**
     * Test.
     */
    @Test
    public void testPublicGroupCoordAllow()
    {
        assertTrue(coreGroupStreamActionTest(PERSON_COORD_ADMIN, true, true));
    }

    /**
     * Test.
     */
    @Test
    public void testPublicGroupCoordForbid()
    {
        assertTrue(coreGroupStreamActionTest(PERSON_COORD_ADMIN, true, false));
    }

    /**
     * Test.
     */
    @Test
    public void testPrivateGroupAnyoneAllow()
    {
        assertFalse(coreGroupStreamActionTest(PERSON_ANYONE, false, true));
    }

    /**
     * Test.
     */
    @Test
    public void testPrivateGroupAnyoneForbid()
    {
        assertFalse(coreGroupStreamActionTest(PERSON_ANYONE, false, false));
    }

    /**
     * Test.
     */
    @Test
    public void testPrivateGroupMemberAllow()
    {
        assertTrue(coreGroupStreamActionTest(PERSON_MEMBER, false, true));
    }

    /**
     * Test.
     */
    @Test
    public void testPrivateGroupMemberForbid()
    {
        assertFalse(coreGroupStreamActionTest(PERSON_MEMBER, false, false));
    }

    /**
     * Test.
     */
    @Test
    public void testPrivateGroupCoordAllow()
    {
        assertTrue(coreGroupStreamActionTest(PERSON_COORD_ADMIN, false, true));
    }

    /**
     * Test.
     */
    @Test
    public void testPrivateGroupCoordForbid()
    {
        assertTrue(coreGroupStreamActionTest(PERSON_COORD_ADMIN, false, false));
    }

    // ---------- OTHER TESTS ----------

    /**
     * Test.
     */
    @Test
    public void testResource()
    {
        mockery.checking(new Expectations()
        {
            {
                allowing(stream).getType();
                will(returnValue(EntityType.RESOURCE));
            }
        });
        boolean result = sut.authorize(PERSON_ANYONE, activity, ActivityInteractionType.COMMENT);
        mockery.assertIsSatisfied();
        assertTrue(result);
    }

    // ---------- ERROR CASE TESTS ----------

    /**
     * Test.
     */
    @Test
    public void testUnhandledStreamType()
    {
        mockery.checking(new Expectations()
        {
            {
                allowing(stream).getType();
                will(returnValue(EntityType.APPLICATION));
            }
        });
        boolean result = sut.authorize(PERSON_ANYONE, activity, ActivityInteractionType.COMMENT);
        mockery.assertIsSatisfied();
        assertFalse(result);
    }

    /**
     * Test.
     */
    @Test
    public void testPersonError()
    {
        mockery.checking(new Expectations()
        {
            {
                allowing(stream).getType();
                will(returnValue(EntityType.PERSON));

                allowing(stream).getEntityId();
                will(throwException(new RuntimeException("BAD")));
            }
        });
        boolean result = sut.authorize(PERSON_ANYONE, activity, ActivityInteractionType.COMMENT);
        mockery.assertIsSatisfied();
        assertFalse(result);
    }

    /**
     * Test.
     */
    @Test
    public void testGroupError()
    {
        mockery.checking(new Expectations()
        {
            {
                allowing(stream).getType();
                will(returnValue(EntityType.GROUP));

                allowing(stream).getEntityId();
                will(throwException(new RuntimeException("BAD")));
            }
        });
        boolean result = sut.authorize(PERSON_ANYONE, activity, ActivityInteractionType.POST);
        mockery.assertIsSatisfied();
        assertFalse(result);
    }

}
