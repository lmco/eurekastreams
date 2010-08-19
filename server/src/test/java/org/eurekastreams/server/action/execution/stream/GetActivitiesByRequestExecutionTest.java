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
package org.eurekastreams.server.action.execution.stream;

import static org.eurekastreams.commons.test.IsEqualInternally.equalInternally;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import net.sf.json.JSONObject;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.cache.GetPrivateCoordinatedAndFollowedGroupIdsForUser;
import org.eurekastreams.server.persistence.mappers.stream.BulkActivitiesMapper;
import org.eurekastreams.server.service.actions.strategies.activity.ActivityFilter;
import org.eurekastreams.server.service.actions.strategies.activity.ListCollider;
import org.eurekastreams.server.service.actions.strategies.activity.datasources.DescendingOrderDataSource;
import org.eurekastreams.server.service.actions.strategies.activity.datasources.SortedDataSource;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetActivitiesByCompositeStreamExecution class.
 * 
 */
public class GetActivitiesByRequestExecutionTest
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
     * System under test.
     */
    private GetActivitiesByRequestExecution sut;

    /**
     * ActionContext mock.
     */
    private PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

    /**
     * Principal mock.
     */
    private Principal principal = context.mock(Principal.class);

    /**
     * bulk mapper mock.
     */
    private BulkActivitiesMapper bulkMapper = context.mock(BulkActivitiesMapper.class);

    /**
     * Mocked GetPrivateCoordinatedAndFollowedGroupIdsForUser.
     */
    private final GetPrivateCoordinatedAndFollowedGroupIdsForUser getVisibleGroupsForUserMapper = context
            .mock(GetPrivateCoordinatedAndFollowedGroupIdsForUser.class);

    /**
     * Account id of the person.
     */
    private final String personAccountId = "sldkjfsdlfjsdjf";

    /**
     * Person id.
     */
    private final Long personId = 82377L;

    /**
     * Person mock.
     */
    private final Person person = context.mock(Person.class);

    /**
     * Filter Mock.
     */
    private ActivityFilter filterMock = context.mock(ActivityFilter.class);

    /**
     * List of all activity ids.
     */
    private final ArrayList<Long> allActivityIds = new ArrayList<Long>();

    /**
     * Activity #1.
     */
    private final ActivityDTO activity1Public = new ActivityDTO();

    /**
     * Activity #2.
     */
    private final ActivityDTO activity2 = new ActivityDTO();

    /**
     * Activity #3.
     */
    private final ActivityDTO activity3Public = new ActivityDTO();

    /**
     * Activity #4.
     */
    private final ActivityDTO activity4 = new ActivityDTO();

    /**
     * Activity #5.
     */
    private final ActivityDTO activity5 = new ActivityDTO();

    /**
     * Activity #6.
     */
    private final ActivityDTO activity6 = new ActivityDTO();

    /**
     * Activity #7.
     */
    private final ActivityDTO activity7 = new ActivityDTO();

    /**
     * Activity #8.
     */
    private final ActivityDTO activity8 = new ActivityDTO();

    /**
     * Activity #9.
     */
    private final ActivityDTO activity9Public = new ActivityDTO();

    /**
     * Group that the person has access to see private activity for.
     */
    private final Long personPrivateGroup = 3827L;

    /**
     * Memcache data source.
     */
    private DescendingOrderDataSource memcacheDS = context.mock(DescendingOrderDataSource.class, "memcache");

    /**
     * Lucene Data source.
     */
    private SortedDataSource luceneDS = context.mock(SortedDataSource.class, "lucene");

    /**
     * AND Collider.
     */
    private ListCollider andCollider = context.mock(ListCollider.class);

    /**
     * 19+1. 120%100 pi+16.8584073....
     */
    private static final int THENUMBERTWENTY = 20;

    /**
     * Setup text fixtures.
     */
    @Before
    public final void setUp()
    {
        List<ActivityFilter> filters = new LinkedList<ActivityFilter>();
        filters.add(filterMock);

        sut = new GetActivitiesByRequestExecution(memcacheDS, luceneDS, bulkMapper, filters, andCollider,
                getVisibleGroupsForUserMapper, 2.0f);

        // set the activity ids
        activity1Public.setId(1L);
        activity2.setId(2L);
        activity3Public.setId(3L);
        activity4.setId(4L);
        activity5.setId(5L);
        activity6.setId(6L);
        activity7.setId(7L);
        activity8.setId(8L);
        activity9Public.setId(9L);

        // set the dates
        activity1Public.setPostedTime(new Date());
        activity2.setPostedTime(new Date());
        activity3Public.setPostedTime(new Date());
        activity4.setPostedTime(new Date());
        activity5.setPostedTime(new Date());
        activity6.setPostedTime(new Date());
        activity7.setPostedTime(new Date());
        activity8.setPostedTime(new Date());
        activity9Public.setPostedTime(new Date());

        // set the public/privates
        activity9Public.setIsDestinationStreamPublic(true);
        activity8.setIsDestinationStreamPublic(false);
        activity7.setIsDestinationStreamPublic(false);
        activity6.setIsDestinationStreamPublic(false);
        activity5.setIsDestinationStreamPublic(false);
        activity4.setIsDestinationStreamPublic(false);
        activity3Public.setIsDestinationStreamPublic(true);
        activity2.setIsDestinationStreamPublic(false);
        activity1Public.setIsDestinationStreamPublic(true);

        // for the private activities, set the destination streams
        StreamEntityDTO destinationStream = new StreamEntityDTO();
        destinationStream.setDestinationEntityId(personPrivateGroup);
        activity2.setDestinationStream(destinationStream);
        activity3Public.setDestinationStream(destinationStream);
        activity4.setDestinationStream(destinationStream);
        activity5.setDestinationStream(destinationStream);
        activity6.setDestinationStream(destinationStream);
        activity8.setDestinationStream(destinationStream);

        // create the activity ids list
        allActivityIds.add(9L);
        allActivityIds.add(8L);
        allActivityIds.add(7L);
        allActivityIds.add(6L);
        allActivityIds.add(5L);
        allActivityIds.add(4L);
        allActivityIds.add(3L);
        allActivityIds.add(2L);
        allActivityIds.add(1L);
    }

    /**
     * Perform action test with one item in the list.
     * 
     * @throws Exception
     *             on failure.
     */
    @Test
    @SuppressWarnings("unchecked")
    public final void performActionTest() throws Exception
    {
        final String request = "{ }";

        context.checking(new Expectations()
        {
            {
                ArrayList<Long> memcacheIds = new ArrayList<Long>();
                memcacheIds.add(2L);

                ArrayList<Long> luceneIds = new ArrayList<Long>();

                ActivityDTO dto = new ActivityDTO();
                dto.setId(3);
                dto.setPostedTime(new Date());
                dto.setIsDestinationStreamPublic(true);

                ArrayList<ActivityDTO> activities = new ArrayList<ActivityDTO>();
                activities.add(dto);

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getAccountId();
                will(returnValue(personAccountId));

                allowing(principal).getId();

                allowing(actionContext).getParams();
                will(returnValue(request));

                oneOf(memcacheDS).fetch(with(any(JSONObject.class)), with(any(Long.class)));
                will(returnValue(memcacheIds));

                oneOf(luceneDS).fetch(with(any(JSONObject.class)), with(any(Long.class)));
                will(returnValue(luceneIds));

                oneOf(andCollider).collide(with(equalInternally(memcacheIds)), with(equalInternally(luceneIds)),
                        with(equalInternally(THENUMBERTWENTY)));

                oneOf(bulkMapper).execute(with(any(ArrayList.class)), with(any(String.class)));
                will(returnValue(activities));

                allowing(filterMock).filter(activities, personAccountId);
                will(returnValue(activities));
            }
        });

        PagedSet<ActivityDTO> results = (PagedSet<ActivityDTO>) sut.execute(actionContext);

        context.assertIsSatisfied();
        assertEquals(1, results.getPagedSet().size());
    }

    /**
     * Test executing a page of data where the user needs two batches to get a full page of activities he can see.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testPerformActionRequiringTwoBatches()
    {
        final String request = "{ \"count\": 2, \"maxId\": 2817 }";

        // batch 1 request
        final List<Long> batch1Request = new ArrayList<Long>();
        batch1Request.add(9L);
        batch1Request.add(8L);
        batch1Request.add(7L);
        batch1Request.add(6L);

        // batch size is 2, so we'll get 4
        final List<ActivityDTO> batch1 = new ArrayList<ActivityDTO>();
        batch1.add(activity9Public); // public
        batch1.add(activity8);
        batch1.add(activity7);
        batch1.add(activity6);

        // batch 2 request
        final List<Long> batch2Response = new ArrayList<Long>();
        batch2Response.add(9L);
        batch2Response.add(8L);
        batch2Response.add(7L);
        batch2Response.add(6L);
        batch2Response.add(5L);
        batch2Response.add(4L);
        batch2Response.add(3L);
        batch2Response.add(2L);
        batch2Response.add(1L);

        // batch 2 request
        final List<Long> batch2Request = new ArrayList<Long>();
        batch2Request.add(5L);
        batch2Request.add(4L);
        batch2Request.add(3L);
        batch2Request.add(2L);
        batch2Request.add(1L);

        // batch size is 4 at this point, so we'll get 8, but that hits the end
        // of the list
        final List<ActivityDTO> batch2 = new ArrayList<ActivityDTO>();
        batch2.add(activity5);
        batch2.add(activity4);
        batch2.add(activity3Public); // public
        batch2.add(activity2);
        batch2.add(activity1Public); // public

        final List<ActivityDTO> expectedResults = new ArrayList<ActivityDTO>();
        expectedResults.add(activity9Public);
        expectedResults.add(activity3Public);

        context.checking(new Expectations()
        {
            {

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getAccountId();
                will(returnValue(personAccountId));

                allowing(principal).getId();
                will(returnValue(personId));

                allowing(actionContext).getParams();
                will(returnValue(request));

                oneOf(memcacheDS).fetch(with(any(JSONObject.class)), with(any(Long.class)));
                will(returnValue(batch1Request));

                oneOf(luceneDS).fetch(with(any(JSONObject.class)), with(any(Long.class)));
                will(returnValue(null));

                oneOf(memcacheDS).fetch(with(any(JSONObject.class)), with(any(Long.class)));
                will(returnValue(batch2Response));

                oneOf(luceneDS).fetch(with(any(JSONObject.class)), with(any(Long.class)));
                will(returnValue(null));

                // requesting the first batch
                oneOf(bulkMapper).execute(with(batch1Request), with(personAccountId));
                will(returnValue(batch1));

                // run through the filters on batch 1
                allowing(filterMock).filter(batch1, personAccountId);
                will(returnValue(batch1));

                // requesting the second batch
                oneOf(bulkMapper).execute(with(batch2Request), with(personAccountId));
                will(returnValue(batch2));

                // run through the filters on batch 2
                allowing(filterMock).filter(expectedResults, personAccountId);
                will(returnValue(batch2));

                // at some point, we need the action to get the list of groups
                // the user can see activity for
                oneOf(getVisibleGroupsForUserMapper).execute(personId);
                will(returnValue(new HashSet<Long>()));
            }
        });

        PagedSet<ActivityDTO> results = (PagedSet<ActivityDTO>) sut.execute(actionContext);

        // we're asking for 2 results - we should have gotten back 1 and 7
        assertEquals(2, results.getPagedSet().size());
        assertSame(activity9Public, results.getPagedSet().get(0));
        assertSame(activity3Public, results.getPagedSet().get(1));

        context.assertIsSatisfied();
    }

    /**
     * Test executing a page of data where the user needs two batches to get a full page of activities he can see.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testPerformActionWithPrivateActivitiesUserCanSee()
    {
        final String request = "{ \"count\": 3, \"maxId\": 7 }";

        // batch 1 request
        final List<Long> batch1Request = new ArrayList<Long>();
        batch1Request.add(6L);
        batch1Request.add(5L);
        batch1Request.add(4L);
        batch1Request.add(3L);
        batch1Request.add(2L);
        batch1Request.add(1L);

        // batch size is 2, so we'll get 4
        final List<ActivityDTO> batch1 = new ArrayList<ActivityDTO>();
        batch1.add(activity6); // private, but user has access
        batch1.add(activity5); // private, but user has access
        batch1.add(activity4); // private, but user has access
        batch1.add(activity3Public); // public
        batch1.add(activity2); // private, but user has access
        batch1.add(activity1Public); // public

        final List<ActivityDTO> expectedResults = new ArrayList<ActivityDTO>();
        expectedResults.add(activity6);
        expectedResults.add(activity5);
        expectedResults.add(activity4);

        final HashSet<Long> usersGroups = new HashSet<Long>();
        usersGroups.add(personPrivateGroup);

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getAccountId();
                will(returnValue(personAccountId));

                allowing(principal).getId();
                will(returnValue(personId));

                allowing(actionContext).getParams();
                will(returnValue(request));

                oneOf(memcacheDS).fetch(with(any(JSONObject.class)), with(any(Long.class)));
                will(returnValue(null));

                oneOf(luceneDS).fetch(with(any(JSONObject.class)), with(any(Long.class)));
                will(returnValue(batch1Request));

                allowing(person).getAccountId();
                will(returnValue(personAccountId));

                // requesting the first batch
                oneOf(bulkMapper).execute(with(batch1Request), with(personAccountId));
                will(returnValue(batch1));

                // run through the filters on batch 1
                allowing(filterMock).filter(expectedResults, personAccountId);
                will(returnValue(batch1));

                // at some point, we need the action to get the list of groups
                // the user can see activity for
                oneOf(getVisibleGroupsForUserMapper).execute(personId);
                will(returnValue(usersGroups));
            }
        });

        PagedSet<ActivityDTO> results = (PagedSet<ActivityDTO>) sut.execute(actionContext);

        // we're asking for 2 results - we should have gotten back 1 and 7
        assertEquals(3, results.getPagedSet().size());
        assertSame(activity6, results.getPagedSet().get(0));
        assertSame(activity5, results.getPagedSet().get(1));
        assertSame(activity4, results.getPagedSet().get(2));

        context.assertIsSatisfied();
    }

}
