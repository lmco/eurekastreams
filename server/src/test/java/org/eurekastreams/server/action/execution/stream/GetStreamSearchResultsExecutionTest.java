/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.service.ServiceActionContext;
import org.eurekastreams.server.action.request.stream.GetStreamSearchResultsRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.requests.StreamSearchRequest;
import org.eurekastreams.server.search.stream.SearchActivitiesMapper;
import org.eurekastreams.server.service.actions.strategies.activity.ActivityFilter;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for {@link GetStreamSearchResultsExecution} class.
 *
 */
public class GetStreamSearchResultsExecutionTest
{
    /**
     * System under test.
     */
    private GetStreamSearchResultsExecution sut;

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
     * Mock mapper.
     */
    private SearchActivitiesMapper mapper = context.mock(SearchActivitiesMapper.class);

    /**
     * Filter Mock.
     */
    private ActivityFilter filterMock = context.mock(ActivityFilter.class);

    /**
     * Principal mock.
     */
    private Principal principalMock = context.mock(Principal.class);

    /**
     * Setup the test fixtures.
     */
    @Before
    public final void setUp()
    {
        List<ActivityFilter> filters = new LinkedList<ActivityFilter>();
        filters.add(filterMock);

        sut = new GetStreamSearchResultsExecution(mapper, filters);
    }

    /**
     * Test the execution, basically just asserting that the mapper method is called correctly.
     *
     * @throws Exception
     *             shouldn't happen.
     */
    @Test
    public final void executeTest() throws Exception
    {

        final GetStreamSearchResultsRequest request = new GetStreamSearchResultsRequest();
        request.setLastSeenStreamItemId(2L);
        request.setPageSize(9);
        request.setSearchText("Some text");
        request.setStreamViewId(1L);

        final ActivityDTO result1 = context.mock(ActivityDTO.class, "result1");
        final ActivityDTO result2 = context.mock(ActivityDTO.class, "result2");
        final ActivityDTO result3 = context.mock(ActivityDTO.class, "result3");

        final List<ActivityDTO> results = new ArrayList<ActivityDTO>();
        results.add(result1);
        results.add(result2);
        results.add(result3);

        List<ActivityFilter> filters = new LinkedList<ActivityFilter>();
        filters.add(filterMock);

        SearchStreamMapperFake mapperFake = new SearchStreamMapperFake(results);

        sut = new GetStreamSearchResultsExecution(mapperFake, filters);

        context.checking(new Expectations()
        {
            {
                allowing(result1).getId();
                will(returnValue(1L));
                allowing(result2).getId();
                will(returnValue(2L));
                allowing(result3).getId();
                will(returnValue(3L));

                allowing(principalMock).getAccountId();
                will(returnValue("accountName"));

                allowing(filterMock).filter(results, "accountName");
                will(returnValue(results));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        // SUT
        sut.execute(currentContext);

        // expect one extra item to be fetched to check for the presence of a next page
        Assert.assertEquals(request.getPageSize() + 1, mapperFake.getRequest().getPageSize());

        // pass through the rest
        Assert.assertEquals(request.getLastSeenStreamItemId(), mapperFake.getRequest().getLastSeenStreamItemId());
        Assert.assertEquals(request.getSearchText(), mapperFake.getRequest().getSearchText());
        Assert.assertEquals(request.getStreamViewId(), mapperFake.getRequest().getStreamViewId());

    }

    /**
     * Test execute with multiple pages of data.
     *
     * @throws Exception
     *             on error
     */
    @Test
    public final void executeTestMultiplePages() throws Exception
    {
        final GetStreamSearchResultsRequest request = new GetStreamSearchResultsRequest();
        request.setLastSeenStreamItemId(2L);
        request.setPageSize(2);
        request.setSearchText("Some text");
        request.setStreamViewId(1L);

        final ActivityDTO result1 = context.mock(ActivityDTO.class, "result1");
        final ActivityDTO result2 = context.mock(ActivityDTO.class, "result2");
        final ActivityDTO result3 = context.mock(ActivityDTO.class, "result3");

        final List<ActivityDTO> results = new ArrayList<ActivityDTO>();
        results.add(result1);
        results.add(result2);
        results.add(result3);

        context.checking(new Expectations()
        {
            {
                allowing(result1).getId();
                will(returnValue(3L));
                allowing(result2).getId();
                will(returnValue(2L));
                allowing(result3).getId();
                will(returnValue(1L));


                oneOf(mapper).execute(with(any(StreamSearchRequest.class)));
                will(returnValue(results));

                allowing(principalMock).getAccountId();
                will(returnValue("accountName"));

                allowing(filterMock).filter(results, "accountName");
                will(returnValue(results));

            }
        });
        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        PagedSet<ActivityDTO> pagedResults = sut.execute(currentContext);

        assertEquals(3, pagedResults.getTotal());
        assertEquals(2, pagedResults.getPagedSet().size());
        assertSame(result1, pagedResults.getPagedSet().get(0));
        assertSame(result2, pagedResults.getPagedSet().get(1));

        context.assertIsSatisfied();
    }

    /**
     * Test execute with a single page of data.
     *
     * @throws Exception
     *             on error
     */
    @Test
    public final void executeTestSinglePage() throws Exception
    {
        final GetStreamSearchResultsRequest request = new GetStreamSearchResultsRequest();
        request.setLastSeenStreamItemId(2L);
        request.setPageSize(5);
        request.setSearchText("Some text");
        request.setStreamViewId(1L);

        final ActivityDTO result1 = context.mock(ActivityDTO.class, "result1");
        final ActivityDTO result2 = context.mock(ActivityDTO.class, "result2");
        final ActivityDTO result3 = context.mock(ActivityDTO.class, "result3");

        final List<ActivityDTO> results = new ArrayList<ActivityDTO>();
        results.add(result1);
        results.add(result2);
        results.add(result3);

        context.checking(new Expectations()
        {
            {
                allowing(result1).getId();
                will(returnValue(1L));
                allowing(result2).getId();
                will(returnValue(2L));
                allowing(result3).getId();
                will(returnValue(3L));


                oneOf(mapper).execute(with(any(StreamSearchRequest.class)));
                will(returnValue(results));

                allowing(principalMock).getAccountId();
                will(returnValue("accountName"));

                allowing(filterMock).filter(results, "accountName");
                will(returnValue(results));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        PagedSet<ActivityDTO> pagedResults = sut.execute(currentContext);
        assertEquals(3, pagedResults.getTotal());
        assertEquals(3, pagedResults.getPagedSet().size());
        assertSame(result1, pagedResults.getPagedSet().get(0));
        assertSame(result2, pagedResults.getPagedSet().get(1));
        assertSame(result3, pagedResults.getPagedSet().get(2));

        context.assertIsSatisfied();
    }

    /**
     * Test execute with a single page of data.
     *
     * @throws Exception
     *             on error
     */
    @Test
    public final void executeTestSinglePageWithMin() throws Exception
    {
        final GetStreamSearchResultsRequest request = new GetStreamSearchResultsRequest();
        request.setMinActivityId(2L);
        request.setPageSize(5);
        request.setSearchText("Some text");
        request.setStreamViewId(1L);

        final ActivityDTO result1 = context.mock(ActivityDTO.class, "result1");
        final ActivityDTO result2 = context.mock(ActivityDTO.class, "result2");
        final ActivityDTO result3 = context.mock(ActivityDTO.class, "result3");

        final List<ActivityDTO> results = new ArrayList<ActivityDTO>();
        results.add(result1);
        results.add(result2);
        results.add(result3);

        context.checking(new Expectations()
        {
            {
                allowing(result1).getId();
                will(returnValue(3L));
                allowing(result2).getId();
                will(returnValue(2L));
                allowing(result3).getId();
                will(returnValue(1L));


                oneOf(mapper).execute(with(any(StreamSearchRequest.class)));
                will(returnValue(results));

                allowing(principalMock).getAccountId();
                will(returnValue("accountName"));

                allowing(filterMock).filter(results, "accountName");
                will(returnValue(results));
            }
        });

        ServiceActionContext currentContext = new ServiceActionContext(request, principalMock);
        PagedSet<ActivityDTO> pagedResults = sut.execute(currentContext);
        assertEquals(3, pagedResults.getTotal());
        assertEquals(1, pagedResults.getPagedSet().size());
        assertSame(result1, pagedResults.getPagedSet().get(0));

        context.assertIsSatisfied();
    }


    /**
     * Fake for SearchStreamMapper.
     */
    public class SearchStreamMapperFake extends SearchActivitiesMapper
    {
        /**
         * The request object passed into execute().
         */
        private StreamSearchRequest request;

        /**
         * Results to return on execute.
         */
        private List<ActivityDTO> results;

        /**
         * Constructor.
         *
         * @param inResults
         *            the results to return on execute
         */
        public SearchStreamMapperFake(final List<ActivityDTO> inResults)
        {
            results = inResults;
        }

        /**
         * Overriden execute method to capture the request object.
         *
         * @param inRequest
         *            the request
         * @return the results passed into constructor
         */
        @Override
        public List<ActivityDTO> execute(final StreamSearchRequest inRequest)
        {
            request = inRequest;
            return results;
        }

        /**
         * @return the request
         */
        public StreamSearchRequest getRequest()
        {
            return request;
        }
    }
}
