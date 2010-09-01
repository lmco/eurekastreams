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
import java.util.Arrays;
import java.util.Collection;
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
import org.eurekastreams.server.domain.stream.ActivitySecurityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.GetPrivateCoordinatedAndFollowedGroupIdsForUser;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByAccountIds;
import org.eurekastreams.server.search.modelview.PersonModelView;
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
     * Person mapper.
     */
    private GetPeopleByAccountIds peopleMapper = context.mock(GetPeopleByAccountIds.class);
    
    /**
     * bulk mapper mock.
     */
    private DomainMapper<List<Long>, List<ActivityDTO>>  bulkMapper = context.mock(DomainMapper.class);

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
     * Security mapper.
     */

    private DomainMapper<List<Long>, Collection<ActivitySecurityDTO>> securityMapper = context.mock(DomainMapper.class,
            "securitymapper");

    /**
     * AND Collider.
     */
    private ListCollider andCollider = context.mock(ListCollider.class);

    /**
     * The activity DTOs.
     */
    private ArrayList<ActivityDTO> activityDTOs;

    /**
     * Security for the activity DTOs.
     */
    private ArrayList<ActivitySecurityDTO> secResults;

    /**
     * 10.
     */
    private static final int THENUMBERTEN = 10;

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
                getVisibleGroupsForUserMapper, securityMapper, peopleMapper);

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
        destinationStream.setDestinationEntityId(1L);

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

        activityDTOs = new ArrayList<ActivityDTO>();
        activityDTOs.add(activity9Public); // public
        activityDTOs.add(activity8);
        activityDTOs.add(activity7);
        activityDTOs.add(activity6);
        activityDTOs.add(activity5);
        activityDTOs.add(activity4);
        activityDTOs.add(activity3Public);
        activityDTOs.add(activity2);
        activityDTOs.add(activity1Public);

        secResults = new ArrayList<ActivitySecurityDTO>();

        for (ActivityDTO activity : activityDTOs)
        {
            ActivitySecurityDTO secDTO = new ActivitySecurityDTO();

            secDTO.setId(activity.getId());
            secDTO.setDestinationEntityId(activity.getDestinationStream().getDestinationEntityId());
            secDTO.setDestinationStreamId(activity.getDestinationStream().getId());
            secDTO.setDestinationStreamPublic(activity.getIsDestinationStreamPublic());

            secResults.add(secDTO);
        }

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
        final PersonModelView personModel = new PersonModelView();

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

                ArrayList<Long> combinedIds = new ArrayList<Long>();
                combinedIds.add(2L);

                ArrayList<ActivityDTO> activities = new ArrayList<ActivityDTO>();
                activities.add(dto);

                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                allowing(principal).getAccountId();
                will(returnValue(personAccountId));

                allowing(principal).getId();
                will(returnValue(personId));

                allowing(actionContext).getParams();
                will(returnValue(request));

                oneOf(memcacheDS).fetch(with(any(JSONObject.class)), with(any(Long.class)));
                will(returnValue(memcacheIds));

                oneOf(luceneDS).fetch(with(any(JSONObject.class)), with(any(Long.class)));
                will(returnValue(luceneIds));

                oneOf(andCollider).collide(with(equalInternally(memcacheIds)), with(equalInternally(luceneIds)),
                        with(equalInternally(THENUMBERTEN)));
                will(returnValue(combinedIds));

                oneOf(securityMapper).execute(combinedIds);
                will(returnValue(secResults));

                oneOf(bulkMapper).execute(with(any(ArrayList.class)));
                will(returnValue(activities));

                allowing(filterMock).filter(with(activities), with(any(PersonModelView.class)));

                oneOf(peopleMapper).execute(Arrays.asList(personAccountId));
                will(returnValue(Arrays.asList(personModel)));

                oneOf(getVisibleGroupsForUserMapper).execute(personId);
                will(returnValue(new HashSet<Long>()));                
            }
        });

        PagedSet<ActivityDTO> results = (PagedSet<ActivityDTO>) sut.execute(actionContext);

        context.assertIsSatisfied();
        assertEquals(1, results.getPagedSet().size());
    }

    /**
     * Test executing a page of data where the user needs three batches to get a full page of activities he can see.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testPerformActionRequiringMultipleBatches()
    {
        final String request = "{ \"count\": 2, \"maxId\": 2817 }";
        final PersonModelView personModel = new PersonModelView();

        final List<ActivityDTO> expectedResults = new ArrayList<ActivityDTO>();
        expectedResults.add(activity9Public);
        expectedResults.add(activity3Public);

        final List<Long> expectedResultsIds = new ArrayList<Long>();
        expectedResultsIds.add(activity9Public.getId());
        expectedResultsIds.add(activity3Public.getId());

        final ArrayList<Long> activityIdsFirstPass = new ArrayList<Long>();
        activityIdsFirstPass.add(allActivityIds.get(0));
        activityIdsFirstPass.add(allActivityIds.get(1));

        final ArrayList<ActivitySecurityDTO> activitySecurityFirstPass = new ArrayList<ActivitySecurityDTO>();
        activitySecurityFirstPass.add(secResults.get(0));
        activitySecurityFirstPass.add(secResults.get(1));

        final ArrayList<Long> activityIdsSecondPass = new ArrayList<Long>();
        activityIdsSecondPass.add(allActivityIds.get(0));
        activityIdsSecondPass.add(allActivityIds.get(1));
        activityIdsSecondPass.add(allActivityIds.get(2));
        activityIdsSecondPass.add(allActivityIds.get(3));

        final ArrayList<Long> activityIdsSecondPassNewItems = new ArrayList<Long>();
        activityIdsSecondPassNewItems.add(allActivityIds.get(2));
        activityIdsSecondPassNewItems.add(allActivityIds.get(3));

        final ArrayList<ActivitySecurityDTO> activitySecuritySecondPass = new ArrayList<ActivitySecurityDTO>();
        activitySecuritySecondPass.add(secResults.get(2));
        activitySecuritySecondPass.add(secResults.get(3));

        final ArrayList<Long> activityIdsThirdPass = new ArrayList<Long>();
        activityIdsThirdPass.add(allActivityIds.get(0));
        activityIdsThirdPass.add(allActivityIds.get(1));
        activityIdsThirdPass.add(allActivityIds.get(2));
        activityIdsThirdPass.add(allActivityIds.get(3));
        activityIdsThirdPass.add(allActivityIds.get(4));
        activityIdsThirdPass.add(allActivityIds.get(5));
        activityIdsThirdPass.add(allActivityIds.get(6));
        activityIdsThirdPass.add(allActivityIds.get(7));

        final ArrayList<Long> activityIdsThirdPassNewItems = new ArrayList<Long>();
        activityIdsThirdPassNewItems.add(allActivityIds.get(4));
        activityIdsThirdPassNewItems.add(allActivityIds.get(5));
        activityIdsThirdPassNewItems.add(allActivityIds.get(6));
        activityIdsThirdPassNewItems.add(allActivityIds.get(7));

        final ArrayList<ActivitySecurityDTO> activitySecurityThirdPass = new ArrayList<ActivitySecurityDTO>();
        activitySecurityThirdPass.add(secResults.get(4));
        activitySecurityThirdPass.add(secResults.get(5));
        activitySecurityThirdPass.add(secResults.get(6));
        activitySecurityThirdPass.add(secResults.get(7));

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

                allowing(luceneDS).fetch(with(any(JSONObject.class)), with(any(Long.class)));
                will(returnValue(null));

                oneOf(memcacheDS).fetch(with(any(JSONObject.class)), with(any(Long.class)));
                will(returnValue(activityIdsFirstPass));

                oneOf(securityMapper).execute(activityIdsFirstPass);
                will(returnValue(activitySecurityFirstPass));

                oneOf(memcacheDS).fetch(with(any(JSONObject.class)), with(any(Long.class)));
                will(returnValue(activityIdsSecondPass));

                oneOf(securityMapper).execute(activityIdsSecondPassNewItems);
                will(returnValue(activitySecuritySecondPass));

                oneOf(memcacheDS).fetch(with(any(JSONObject.class)), with(any(Long.class)));
                will(returnValue(activityIdsThirdPass));

                oneOf(securityMapper).execute(activityIdsThirdPassNewItems);
                will(returnValue(activitySecurityThirdPass));

                oneOf(getVisibleGroupsForUserMapper).execute(personId);
                will(returnValue(new HashSet<Long>()));

                oneOf(bulkMapper).execute(with(expectedResultsIds));
                will(returnValue(expectedResults));

                allowing(filterMock).filter(with(expectedResults), with(any(PersonModelView.class)));

                oneOf(peopleMapper).execute(Arrays.asList(personAccountId));
                will(returnValue(Arrays.asList(personModel)));
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

        final PersonModelView personModel = new PersonModelView();
        
        final HashSet<Long> usersGroups = new HashSet<Long>();
        usersGroups.add(personPrivateGroup);

        final List<ActivityDTO> expectedResults = new ArrayList<ActivityDTO>();
        expectedResults.add(activity6);
        expectedResults.add(activity5);
        expectedResults.add(activity4);

        final List<Long> expectedResultsIds = new ArrayList<Long>();
        expectedResultsIds.add(activity6.getId());
        expectedResultsIds.add(activity5.getId());
        expectedResultsIds.add(activity4.getId());

        final ArrayList<Long> activityIds = new ArrayList<Long>();
        activityIds.add(allActivityIds.get(3));
        activityIds.add(allActivityIds.get(4));
        activityIds.add(allActivityIds.get(5));


        final ArrayList<ActivitySecurityDTO> activitySecurity = new ArrayList<ActivitySecurityDTO>();
        activitySecurity.add(secResults.get(3));
        activitySecurity.add(secResults.get(4));
        activitySecurity.add(secResults.get(5));
        
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

                allowing(luceneDS).fetch(with(any(JSONObject.class)), with(any(Long.class)));
                will(returnValue(null));

                oneOf(memcacheDS).fetch(with(any(JSONObject.class)), with(any(Long.class)));
                will(returnValue(activityIds));

                oneOf(securityMapper).execute(activityIds);
                will(returnValue(activitySecurity));

                oneOf(getVisibleGroupsForUserMapper).execute(personId);
                will(returnValue(usersGroups));

                oneOf(bulkMapper).execute(with(expectedResultsIds));
                will(returnValue(expectedResults));

                allowing(filterMock).filter(with(expectedResults), with(any(PersonModelView.class)));

                oneOf(peopleMapper).execute(Arrays.asList(personAccountId));
                will(returnValue(Arrays.asList(personModel)));
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
