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
package org.eurekastreams.server.action.execution.profile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.AnonymousClassInterceptor;
import org.eurekastreams.server.action.request.profile.GetFollowersFollowingRequest;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.FollowerStatusable;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.Follower.FollowerStatus;
import org.eurekastreams.server.domain.strategies.FollowerStatusPopulator;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetCurrentUserFollowingStatusExecution.
 */
@SuppressWarnings("unchecked")
public class GetFollowingExecutionTest
{
    /** Test data. */
    private static final String ACCOUNT_ID = "abcdefgh";

    /** Test data. */
    private static final int START_INDEX = 5;

    /** Test data. */
    private static final int END_INDEX = 9;

    /** Test data. */
    private static final Long USER_ID = 99L;

    /** The current user id. **/
    private static final Long CURRENT_USER_ID = 8822L;

    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: Mapper to find the follower's id given their account id. */
    private final DomainMapper<String, Long> personAccountIdToIdLookupMapper = context.mock(DomainMapper.class,
            "personAccountIdToIdLookupMapper");

    /** Fixture: Mapper returning the ids of the entities being followed. */
    private final DomainMapper<Long, List<Long>> idsMapper = context.mock(DomainMapper.class, "idsMapper");

    /** Fixture: Mapper returning entities (people, groups) given their ids. */
    private final DomainMapper<List<Long>, List<FollowerStatusable>> bulkModelViewMapper = context.mock(
            DomainMapper.class, "bulkModelViewMapper");

    /** Fixture: action context. */
    private final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Populator for follower status of results.
     */
    private final FollowerStatusPopulator<FollowerStatusable> followerStatusPopulator = context
            .mock(FollowerStatusPopulator.class);

    /**
     * Principal.
     */
    private final Principal principal = context.mock(Principal.class);

    /** SUT. */
    private GetFollowingExecution sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new GetFollowingExecution(personAccountIdToIdLookupMapper, idsMapper, bulkModelViewMapper,
                followerStatusPopulator);

        final GetFollowersFollowingRequest rqst = new GetFollowersFollowingRequest(EntityType.PERSON, ACCOUNT_ID,
                START_INDEX, END_INDEX);
        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getParams();
                will(returnValue(rqst));
                allowing(personAccountIdToIdLookupMapper).execute(ACCOUNT_ID);
                will(returnValue(USER_ID));

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(CURRENT_USER_ID));
            }
        });
    }

    /**
     * Tests empty return list.
     */
    @Test
    public void testExecuteNone()
    {
        context.checking(new Expectations()
        {
            {
                allowing(idsMapper).execute(USER_ID);
                will(returnValue(Collections.EMPTY_LIST));
            }
        });

        PagedSet<FollowerStatusable> results = sut.execute(actionContext);
        context.assertIsSatisfied();
        assertEquals(-1, results.getFromIndex());
        assertEquals(-1, results.getToIndex());
        assertEquals(0, results.getTotal());
        assertTrue(results.getPagedSet().isEmpty());
    }

    /**
     * Tests full return list.
     */
    @Test
    public void testExecute()
    {
        final List<Long> list = Arrays.asList(0L, 0L, 0L, 0L, 0L, 8L, 7L, 6L, 5L, 4L, 0L, 0L, 0L, 0L, 0L);
        final List<FollowerStatusable> resultList = new ArrayList<FollowerStatusable>();

        final AnonymousClassInterceptor<List<Long>> parmCatch = new AnonymousClassInterceptor<List<Long>>(resultList);

        context.checking(new Expectations()
        {
            {
                allowing(idsMapper).execute(USER_ID);
                will(returnValue(list));
                allowing(bulkModelViewMapper).execute(with(any(List.class)));
                will(parmCatch);

                oneOf(followerStatusPopulator).execute(with(CURRENT_USER_ID), with(resultList),
                        with(FollowerStatus.NOTSPECIFIED));
            }
        });

        PagedSet<FollowerStatusable> results = sut.execute(actionContext);
        context.assertIsSatisfied();
        assertEquals(START_INDEX, results.getFromIndex());
        assertEquals(END_INDEX, results.getToIndex());
        assertEquals(5 * 3, results.getTotal());
        List<Long> idList = parmCatch.getObject();
        assertEquals(5, idList.size());
        for (int i = 0; i < 5; i++)
        {
            assertEquals(list.get(i + START_INDEX), idList.get(i));
        }
    }

    /**
     * Tests incomplete return list.
     */
    @Test
    public void testExecutePartil()
    {
        final List<Long> list = Arrays.asList(0L, 0L, 0L, 0L, 0L, 8L, 7L, 6L);
        final List<FollowerStatusable> resultList = new ArrayList<FollowerStatusable>();

        final AnonymousClassInterceptor<List<Long>> parmCatch = new AnonymousClassInterceptor<List<Long>>(resultList);

        context.checking(new Expectations()
        {
            {
                allowing(idsMapper).execute(USER_ID);
                will(returnValue(list));
                allowing(bulkModelViewMapper).execute(with(any(List.class)));
                will(parmCatch);

                oneOf(followerStatusPopulator).execute(with(CURRENT_USER_ID), with(resultList),
                        with(FollowerStatus.NOTSPECIFIED));
            }
        });

        PagedSet<FollowerStatusable> results = sut.execute(actionContext);
        context.assertIsSatisfied();
        assertEquals(START_INDEX, results.getFromIndex());
        assertEquals(START_INDEX + 2, results.getToIndex());
        assertEquals(8, results.getTotal());
        List<Long> idList = parmCatch.getObject();
        assertEquals(3, idList.size());
        assertEquals((Long) 8L, idList.get(0));
        assertEquals((Long) 7L, idList.get(1));
        assertEquals((Long) 6L, idList.get(2));
    }

    /**
     * Tests request past end of list.
     */
    @Test
    public void testExecutePastEnd()
    {
        final List<Long> list = Arrays.asList(0L, 0L);

        context.checking(new Expectations()
        {
            {
                allowing(idsMapper).execute(USER_ID);
                will(returnValue(list));
            }
        });

        PagedSet<FollowerStatusable> results = sut.execute(actionContext);
        context.assertIsSatisfied();
        assertEquals(-1, results.getFromIndex());
        assertEquals(-1, results.getToIndex());
        assertEquals(2, results.getTotal());
        assertTrue(results.getPagedSet().isEmpty());
    }

}
