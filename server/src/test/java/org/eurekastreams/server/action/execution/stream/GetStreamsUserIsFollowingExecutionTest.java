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
package org.eurekastreams.server.action.execution.stream;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.action.request.stream.GetStreamsUserIsFollowingRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.Follower.FollowerStatus;
import org.eurekastreams.server.domain.dto.StreamDTO;
import org.eurekastreams.server.domain.strategies.FollowerStatusPopulator;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetStreamsUserIsFollowingExecution.
 * 
 */
@SuppressWarnings("unchecked")
public class GetStreamsUserIsFollowingExecutionTest
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
     * Action context.
     */
    private PrincipalActionContext ac = context.mock(PrincipalActionContext.class);

    /**
     * Principal.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * Current user id.
     */
    private long currentUserId = 6L;

    /**
     * Requested user id.
     */
    private long userId = 5L;

    /**
     * User account id.
     */
    private String userAccountId = "userAccountId";

    /**
     * Mapper to get user id by accountid.
     */
    private DomainMapper<String, Long> getPersonIdByAccountIdMapper = context.mock(DomainMapper.class,
            "getPersonIdByAccountIdMapper");

    /**
     * Mapper to get person ids for all persons current user is following.
     */
    private DomainMapper<Long, List<Long>> personIdsUserIsFollowingMapper = context.mock(DomainMapper.class,
            "personIdsUserIsFollowingMapper");

    /**
     * Mapper to get group ids for all persons current user is following.
     */
    private DomainMapper<Long, List<Long>> groupIdsUserIsFollowingMapper = context.mock(DomainMapper.class,
            "groupIdsUserIsFollowingMapper");

    /**
     * Mapper to get Person model views.
     */
    private DomainMapper<List<Long>, List<PersonModelView>> personModelViewsMapper = context.mock(DomainMapper.class,
            "personModelViewsMapper");

    /**
     * Mapper to get group model views.
     */
    private DomainMapper<List<Long>, List<DomainGroupModelView>> groupModelViewsMapper = context.mock(
            DomainMapper.class, "groupModelViewsMapper");

    /**
     * Populator for follower status of results.
     */
    private FollowerStatusPopulator<StreamDTO> followerStatusPopulator = context.mock(FollowerStatusPopulator.class);

    /**
     * System under test.
     */
    private GetStreamsUserIsFollowingExecution sut = new GetStreamsUserIsFollowingExecution(
            getPersonIdByAccountIdMapper, personIdsUserIsFollowingMapper, groupIdsUserIsFollowingMapper,
            personModelViewsMapper, groupModelViewsMapper, followerStatusPopulator);

    /**
     * List of ids for mock id mappers to return.
     */
    private List<Long> mvIds = new ArrayList<Long>(Arrays.asList(1L, 2L, 3L));

    /**
     * PersonModelViews used in test.
     */
    private PersonModelView pmv1, pmv2, pmv3;

    /**
     * DomainGroupModelView used in test.
     */
    private DomainGroupModelView gmv1, gmv2, gmv3;

    /**
     * List of {@link PersonModelView}.
     */
    private List<PersonModelView> pmvList;

    /**
     * List of {@link DomainGroupModelView}.
     */
    private List<DomainGroupModelView> gmvList;

    /**
     * Action context param.
     */
    private GetStreamsUserIsFollowingRequest request = context.mock(GetStreamsUserIsFollowingRequest.class);

    /**
     * Setup.
     */
    @Before
    public void setup()
    {
        pmv1 = new PersonModelView();
        pmv1.setDisplayName("a");
        pmv2 = new PersonModelView();
        pmv2.setDisplayName("c");
        pmv3 = new PersonModelView();
        pmv3.setDisplayName("e");
        gmv1 = new DomainGroupModelView();
        gmv1.setName("b");
        gmv2 = new DomainGroupModelView();
        gmv2.setName("d");
        gmv3 = new DomainGroupModelView();
        gmv3.setName("f");
        pmvList = new ArrayList<PersonModelView>(Arrays.asList(pmv1, pmv2, pmv3));
        gmvList = new ArrayList<DomainGroupModelView>(Arrays.asList(gmv1, gmv2, gmv3));
    }

    /**
     * Test.
     */
    @Test
    public final void testAsk6Get6()
    {
        final int startIndex = 0;
        final int endIndex = 5;

        context.checking(new Expectations()
        {
            {
                oneOf(ac).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(currentUserId));

                oneOf(ac).getParams();
                will(returnValue(request));

                oneOf(request).getAccountId();
                will(returnValue(userAccountId));

                oneOf(getPersonIdByAccountIdMapper).execute(userAccountId);
                will(returnValue(userId));

                oneOf(personIdsUserIsFollowingMapper).execute(userId);
                will(returnValue(mvIds));

                oneOf(personModelViewsMapper).execute(mvIds);
                will(returnValue(pmvList));

                oneOf(groupIdsUserIsFollowingMapper).execute(userId);
                will(returnValue(mvIds));

                oneOf(groupModelViewsMapper).execute(mvIds);
                will(returnValue(gmvList));

                oneOf(followerStatusPopulator).execute(with(any(Long.class)), with(any(List.class)),
                        with(any(FollowerStatus.class)));

                oneOf(request).getStartIndex();
                will(returnValue(startIndex));

                allowing(request).getEndIndex();
                will(returnValue(endIndex));
            }
        });

        List<StreamDTO> results = sut.execute(ac).getPagedSet();
        assertEquals(6, results.size());
        assertEquals("a", results.get(0).getDisplayName());
        assertEquals("b", results.get(1).getDisplayName());

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public final void testAsk10Get6()
    {
        final int startIndex = 0;
        final int endIndex = 9;

        context.checking(new Expectations()
        {
            {
                oneOf(ac).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(currentUserId));

                oneOf(ac).getParams();
                will(returnValue(request));

                oneOf(request).getAccountId();
                will(returnValue(userAccountId));

                oneOf(getPersonIdByAccountIdMapper).execute(userAccountId);
                will(returnValue(userId));

                oneOf(personIdsUserIsFollowingMapper).execute(userId);
                will(returnValue(mvIds));

                oneOf(personModelViewsMapper).execute(mvIds);
                will(returnValue(pmvList));

                oneOf(groupIdsUserIsFollowingMapper).execute(userId);
                will(returnValue(mvIds));

                oneOf(groupModelViewsMapper).execute(mvIds);
                will(returnValue(gmvList));

                oneOf(followerStatusPopulator).execute(with(any(Long.class)), with(any(List.class)),
                        with(any(FollowerStatus.class)));

                oneOf(request).getStartIndex();
                will(returnValue(startIndex));

                allowing(request).getEndIndex();
                will(returnValue(endIndex));
            }
        });

        PagedSet<StreamDTO> pageResults = sut.execute(ac);
        List<StreamDTO> results = pageResults.getPagedSet();
        assertEquals(6, pageResults.getTotal());
        assertEquals(6, results.size());
        assertEquals("a", results.get(0).getDisplayName());
        assertEquals("b", results.get(1).getDisplayName());

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public final void testAskPage1()
    {
        final int startIndex = 0;
        final int endIndex = 2;

        context.checking(new Expectations()
        {
            {
                oneOf(ac).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(currentUserId));

                oneOf(ac).getParams();
                will(returnValue(request));

                oneOf(request).getAccountId();
                will(returnValue(userAccountId));

                oneOf(getPersonIdByAccountIdMapper).execute(userAccountId);
                will(returnValue(userId));

                oneOf(personIdsUserIsFollowingMapper).execute(userId);
                will(returnValue(mvIds));

                oneOf(personModelViewsMapper).execute(mvIds);
                will(returnValue(pmvList));

                oneOf(groupIdsUserIsFollowingMapper).execute(userId);
                will(returnValue(mvIds));

                oneOf(groupModelViewsMapper).execute(mvIds);
                will(returnValue(gmvList));

                oneOf(followerStatusPopulator).execute(with(any(Long.class)), with(any(List.class)),
                        with(any(FollowerStatus.class)));

                oneOf(request).getStartIndex();
                will(returnValue(startIndex));

                allowing(request).getEndIndex();
                will(returnValue(endIndex));
            }
        });

        PagedSet<StreamDTO> pageResults = sut.execute(ac);
        List<StreamDTO> results = pageResults.getPagedSet();
        assertEquals(6, pageResults.getTotal());
        assertEquals(3, results.size());
        assertEquals("a", results.get(0).getDisplayName());
        assertEquals("b", results.get(1).getDisplayName());
        assertEquals("c", results.get(2).getDisplayName());

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public final void testAskPage2()
    {
        final int startIndex = 3;
        final int endIndex = 5;

        context.checking(new Expectations()
        {
            {
                oneOf(ac).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(currentUserId));

                oneOf(ac).getParams();
                will(returnValue(request));

                oneOf(request).getAccountId();
                will(returnValue(userAccountId));

                oneOf(getPersonIdByAccountIdMapper).execute(userAccountId);
                will(returnValue(userId));

                oneOf(personIdsUserIsFollowingMapper).execute(userId);
                will(returnValue(mvIds));

                oneOf(personModelViewsMapper).execute(mvIds);
                will(returnValue(pmvList));

                oneOf(groupIdsUserIsFollowingMapper).execute(userId);
                will(returnValue(mvIds));

                oneOf(groupModelViewsMapper).execute(mvIds);
                will(returnValue(gmvList));

                oneOf(followerStatusPopulator).execute(with(any(Long.class)), with(any(List.class)),
                        with(any(FollowerStatus.class)));

                oneOf(request).getStartIndex();
                will(returnValue(startIndex));

                allowing(request).getEndIndex();
                will(returnValue(endIndex));
            }
        });

        PagedSet<StreamDTO> pageResults = sut.execute(ac);
        List<StreamDTO> results = pageResults.getPagedSet();
        assertEquals(6, pageResults.getTotal());
        assertEquals(3, results.size());
        assertEquals("d", results.get(0).getDisplayName());
        assertEquals("e", results.get(1).getDisplayName());
        assertEquals("f", results.get(2).getDisplayName());

        context.assertIsSatisfied();
    }

    /**
     * Test.
     */
    @Test
    public final void testNoGroupFollowed()
    {
        final int startIndex = 0;
        final int endIndex = 9;
        final List empty = new ArrayList();

        context.checking(new Expectations()
        {
            {
                oneOf(ac).getPrincipal();
                will(returnValue(principal));

                oneOf(principal).getId();
                will(returnValue(currentUserId));

                oneOf(ac).getParams();
                will(returnValue(request));

                oneOf(request).getAccountId();
                will(returnValue(userAccountId));

                oneOf(getPersonIdByAccountIdMapper).execute(userAccountId);
                will(returnValue(userId));

                oneOf(personIdsUserIsFollowingMapper).execute(userId);
                will(returnValue(mvIds));

                oneOf(personModelViewsMapper).execute(mvIds);
                will(returnValue(pmvList));

                oneOf(groupIdsUserIsFollowingMapper).execute(userId);
                will(returnValue(empty));

                oneOf(groupModelViewsMapper).execute(empty);
                will(returnValue(empty));

                oneOf(followerStatusPopulator).execute(with(any(Long.class)), with(any(List.class)),
                        with(any(FollowerStatus.class)));

                oneOf(request).getStartIndex();
                will(returnValue(startIndex));

                allowing(request).getEndIndex();
                will(returnValue(endIndex));
            }
        });

        PagedSet<StreamDTO> pageResults = sut.execute(ac);
        List<StreamDTO> results = pageResults.getPagedSet();
        assertEquals(3, pageResults.getTotal());
        assertEquals(3, results.size());
        assertEquals("a", results.get(0).getDisplayName());
        assertEquals("c", results.get(1).getDisplayName());
        assertEquals("e", results.get(2).getDisplayName());

        context.assertIsSatisfied();
    }

}
