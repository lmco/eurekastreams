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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.activity.ActivityFilter;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GetActivitiesByRequestExecution class.
 * 
 */
@SuppressWarnings("unchecked")
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
     * nt Mapper to get a person model view by account id.
     */
    private DomainMapper<String, PersonModelView> getPersonModelViewByAccountIdMapper = context.mock(
            DomainMapper.class, "getPersonModelViewsByAccountIdMapper");

    /**
     * bulk mapper mock.
     */
    private DomainMapper<List<Long>, List<ActivityDTO>> bulkMapper = context.mock(DomainMapper.class);

    /**
     * Account id of the person.
     */
    private final String personAccountId = "sldkjfsdlfjsdjf";

    /**
     * Person id.
     */
    private final Long personId = 82377L;

    /**
     * Filter Mock.
     */
    private ActivityFilter filterMock = context.mock(ActivityFilter.class);

    /**
     * Get activity by JSON request mock.
     */
    private GetActivityIdsByJson getActivityIdsByJsonRequest = context.mock(GetActivityIdsByJson.class);

    /**
     * Setup text fixtures.
     */
    @Before
    public final void setUp()
    {
        List<ActivityFilter> filters = new LinkedList<ActivityFilter>();
        filters.add(filterMock);

        sut = new GetActivitiesByRequestExecution(bulkMapper, filters, getPersonModelViewByAccountIdMapper,
                getActivityIdsByJsonRequest);
    }

    /**
     * Perform action test with one item in the list.
     * 
     * @throws Exception
     *             on failure.
     */
    @Test
    public final void performActionTest() throws Exception
    {
        final String request = "{ }";
        final PersonModelView personModel = new PersonModelView();

        context.checking(new Expectations()
        {
            {
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

                // Count should be 11, since max ID is 10 by default.
                // Used for paging
                oneOf(getActivityIdsByJsonRequest).execute(with("{\"count\":11}"), with(personId));
                will(returnValue(combinedIds));

                oneOf(bulkMapper).execute(with(any(ArrayList.class)));
                will(returnValue(activities));

                allowing(filterMock).filter(with(activities), with(any(PersonModelView.class)));

                oneOf(getPersonModelViewByAccountIdMapper).execute(personAccountId);
                will(returnValue(personModel));
            }
        });

        PagedSet<ActivityDTO> results = (PagedSet<ActivityDTO>) sut.execute(actionContext);

        context.assertIsSatisfied();
        assertEquals(1, results.getPagedSet().size());
    }

    /**
     * Perform action test with one item in the list. Asks for sorted by date with a keyword.
     * 
     * @throws Exception
     *             on failure.
     */
    @Test
    public final void performActionDateSortWithKeywordTest() throws Exception
    {
        final String request = "{ query : { sortBy:\"date\", keywords:\"test\"} }";
        final PersonModelView personModel = new PersonModelView();

        context.checking(new Expectations()
        {
            {
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

                oneOf(getActivityIdsByJsonRequest).execute(
                        with("{\"count\":11,\"query\":{\"keywords\":\"test\",\"sortBy\":\"date\"}}"), with(personId));
                will(returnValue(combinedIds));

                oneOf(bulkMapper).execute(with(any(ArrayList.class)));
                will(returnValue(activities));

                allowing(filterMock).filter(with(activities), with(any(PersonModelView.class)));

                oneOf(getPersonModelViewByAccountIdMapper).execute(personAccountId);
                will(returnValue(personModel));
            }
        });

        PagedSet<ActivityDTO> results = (PagedSet<ActivityDTO>) sut.execute(actionContext);

        context.assertIsSatisfied();
        assertEquals(1, results.getPagedSet().size());
    }

    /**
     * Perform action test with one item in the list. Asks a specific number of items (not default).
     * 
     * @throws Exception
     *             on failure.
     */
    @Test
    public final void performActionWithCount() throws Exception
    {
        final String request = "{ count:1 }";
        final PersonModelView personModel = new PersonModelView();

        context.checking(new Expectations()
        {
            {
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

                oneOf(getActivityIdsByJsonRequest).execute(with("{\"count\":2}"), with(personId));
                will(returnValue(combinedIds));

                oneOf(bulkMapper).execute(with(any(ArrayList.class)));
                will(returnValue(activities));

                allowing(filterMock).filter(with(activities), with(any(PersonModelView.class)));

                oneOf(getPersonModelViewByAccountIdMapper).execute(personAccountId);
                will(returnValue(personModel));
            }
        });

        PagedSet<ActivityDTO> results = (PagedSet<ActivityDTO>) sut.execute(actionContext);

        context.assertIsSatisfied();
        assertEquals(1, results.getPagedSet().size());
    }
}
