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
package org.eurekastreams.server.action.execution.opensocial;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.execution.stream.ActivitySecurityTrimmer;
import org.eurekastreams.server.action.request.opensocial.GetUserActivitiesRequest;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByOpenSocialIds;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for GetUserActivitiesExecution.
 */
public class GetUserActivitiesExecutionTest
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
     * Local instance of the {@link BulkActivitiesMapper}.
     */
    private final DomainMapper<List<Long>, List<ActivityDTO>> bulkActivitiesMapper = context.mock(DomainMapper.class);

    /**
     * Local instance of the {@link GetPeopleByOpenSocialIds} mapper.
     */
    private final GetPeopleByOpenSocialIds getPeopleByOpenSocialIds = context.mock(GetPeopleByOpenSocialIds.class);

    /**
     * Local instance of the {@link ActivitySecurityTrimmer}.
     */
    private final ActivitySecurityTrimmer securityTrimmer = context.mock(ActivitySecurityTrimmer.class);

    /**
     * Max number of activities to fetch by open social id.
     */
    private final Long maxActivitiesToReturnByOpenSocialId = 8328L;

    /**
     * Test execute with no open social ids or activity ids.
     */
    @Test
    public void testExecuteNoRequest()
    {
        final PagedSet<ActivityDTO> activities = context.mock(PagedSet.class);
        final ExecutionStrategyFake executionStrategy = new ExecutionStrategyFake(activities);
        GetUserActivitiesExecution sut = new GetUserActivitiesExecution(bulkActivitiesMapper, getPeopleByOpenSocialIds,
                executionStrategy, maxActivitiesToReturnByOpenSocialId, securityTrimmer);

        final Principal principal = context.mock(Principal.class);
        final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

        final GetUserActivitiesRequest request = context.mock(GetUserActivitiesRequest.class);

        final List<Long> activityIds = new ArrayList<Long>();
        final Set<String> openSocialIds = new HashSet<String>();

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(actionContext).getParams();
                will(returnValue(request));

                // activity ids
                allowing(request).getActivityIds();
                will(returnValue(activityIds));

                // open social ids
                allowing(request).getOpenSocialIds();
                will(returnValue(openSocialIds));
            }
        });

        LinkedList<ActivityDTO> results = sut.execute(actionContext);
        assertEquals(0, results.size());

        context.assertIsSatisfied();
    }

    /**
     * Test execute with null open social ids or activity ids.
     */
    @Test
    public void testExecuteNullRequests()
    {
        final PagedSet<ActivityDTO> activities = context.mock(PagedSet.class);
        final ExecutionStrategyFake executionStrategy = new ExecutionStrategyFake(activities);
        GetUserActivitiesExecution sut = new GetUserActivitiesExecution(bulkActivitiesMapper, getPeopleByOpenSocialIds,
                executionStrategy, maxActivitiesToReturnByOpenSocialId, securityTrimmer);

        final Principal principal = context.mock(Principal.class);
        final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

        final GetUserActivitiesRequest request = context.mock(GetUserActivitiesRequest.class);

        final List<Long> activityIds = null;
        final Set<String> openSocialIds = null;

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(actionContext).getParams();
                will(returnValue(request));

                // activity ids
                allowing(request).getActivityIds();
                will(returnValue(activityIds));

                // open social ids
                allowing(request).getOpenSocialIds();
                will(returnValue(openSocialIds));
            }
        });

        LinkedList<ActivityDTO> results = sut.execute(actionContext);
        assertEquals(0, results.size());

        context.assertIsSatisfied();
    }

    /**
     * Test execute with open social ids, no activity ids.
     */
    @Test
    public void testExecuteOpenSocialIdsOnly()
    {
        final PagedSet<ActivityDTO> activities = context.mock(PagedSet.class);
        final List<ActivityDTO> activityList = new ArrayList<ActivityDTO>();
        ActivityDTO a1 = new ActivityDTO();
        ActivityDTO a2 = new ActivityDTO();
        activityList.add(a1);
        activityList.add(a2);

        final ExecutionStrategyFake executionStrategy = new ExecutionStrategyFake(activities);
        GetUserActivitiesExecution sut = new GetUserActivitiesExecution(bulkActivitiesMapper, getPeopleByOpenSocialIds,
                executionStrategy, maxActivitiesToReturnByOpenSocialId, securityTrimmer);

        final Principal principal = context.mock(Principal.class);
        final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

        final GetUserActivitiesRequest request = context.mock(GetUserActivitiesRequest.class);

        final List<Long> activityIds = new ArrayList<Long>();
        final Set<String> openSocialIds = new HashSet<String>();
        openSocialIds.add("grape");
        openSocialIds.add("potato");

        final List<PersonModelView> people = new ArrayList<PersonModelView>();
        PersonModelView p1 = new PersonModelView();
        PersonModelView p2 = new PersonModelView();
        people.add(p1);
        people.add(p2);

        p1.setAccountId("ACCT1");
        p2.setAccountId("ACCT2");

        String expectedJson = "{\"count\":" + maxActivitiesToReturnByOpenSocialId
                + ",\"query\":{\"recipient\":[{\"name\":\"ACCT1\",\"type\":\"PERSON\"},"
                + "{\"name\":\"ACCT2\",\"type\":\"PERSON\"}]}}";

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(actionContext).getParams();
                will(returnValue(request));

                // activity ids
                allowing(request).getActivityIds();
                will(returnValue(activityIds));

                // open social ids
                allowing(request).getOpenSocialIds();
                will(returnValue(openSocialIds));

                oneOf(getPeopleByOpenSocialIds).execute(with(any(ArrayList.class)));
                will(returnValue(people));

                oneOf(activities).getPagedSet();
                will(returnValue(activityList));
            }
        });

        List<ActivityDTO> results = sut.execute(actionContext);
        assertEquals(2, results.size());
        assertTrue(results.contains(a1));
        assertTrue(results.contains(a2));

        String receievedJson = (String) executionStrategy.getContextPassedIn().getParams();
        assertEquals(expectedJson, receievedJson);

        context.assertIsSatisfied();
    }

    /**
     * Test execute with open social ids, and an activity id that's already been found by destination stream.
     */
    @Test
    public void testExecuteActivityIdsOnly()
    {
        final Long act1Id = 827L;
        final Long act2Id = 82337L;

        final List<ActivityDTO> activityList = new ArrayList<ActivityDTO>();
        ActivityDTO a1 = new ActivityDTO();
        ActivityDTO a2 = new ActivityDTO();
        a1.setId(act1Id);
        a2.setId(act2Id);
        activityList.add(a1);
        activityList.add(a2);

        final ExecutionStrategyFake executionStrategy = new ExecutionStrategyFake(null);
        GetUserActivitiesExecution sut = new GetUserActivitiesExecution(bulkActivitiesMapper, getPeopleByOpenSocialIds,
                executionStrategy, maxActivitiesToReturnByOpenSocialId, securityTrimmer);

        final Principal principal = context.mock(Principal.class);
        final Long principalId = 1L;
        final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

        final GetUserActivitiesRequest request = context.mock(GetUserActivitiesRequest.class);

        final List<Long> activityIds = new ArrayList<Long>();
        activityIds.add(act1Id);
        activityIds.add(act2Id);

        // no open social ids
        final Set<String> openSocialIds = new HashSet<String>();

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(actionContext).getParams();
                will(returnValue(request));

                // activity ids
                allowing(request).getActivityIds();
                will(returnValue(activityIds));

                // open social ids
                allowing(request).getOpenSocialIds();
                will(returnValue(openSocialIds));
                
                oneOf(principal).getId();
                will(returnValue(principalId));
                
                oneOf(securityTrimmer).trim(activityIds, principalId);
                will(returnValue(activityIds));

                oneOf(bulkActivitiesMapper).execute(with(any(List.class)));
                will(returnValue(activityList));
            }
        });

        List<ActivityDTO> results = sut.execute(actionContext);
        assertEquals(2, results.size());
        assertTrue(results.contains(a1));
        assertTrue(results.contains(a2));

        // make sure not used
        assertEquals(null, executionStrategy.getContextPassedIn());

        context.assertIsSatisfied();
    }

    /**
     * Test execute with open social ids, and an activity id that's already been found by destination stream.
     */
    @Test
    public void testExecuteOpenSocialIdsAndActivityIdWhichWasAlreadyFoundByOpenSocialId()
    {
        final Long act1Id = 827L;
        final Long act2Id = 82337L;

        final PagedSet<ActivityDTO> activities = context.mock(PagedSet.class);
        final List<ActivityDTO> activityList = new ArrayList<ActivityDTO>();
        ActivityDTO a1 = new ActivityDTO();
        ActivityDTO a2 = new ActivityDTO();
        a1.setId(act1Id);
        a2.setId(act2Id);
        activityList.add(a1);
        activityList.add(a2);

        final ExecutionStrategyFake executionStrategy = new ExecutionStrategyFake(activities);
        GetUserActivitiesExecution sut = new GetUserActivitiesExecution(bulkActivitiesMapper, getPeopleByOpenSocialIds,
                executionStrategy, maxActivitiesToReturnByOpenSocialId, securityTrimmer);

        final Principal principal = context.mock(Principal.class);
        final Long principalId = 1L;
        final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

        final GetUserActivitiesRequest request = context.mock(GetUserActivitiesRequest.class);

        final List<Long> activityIds = new ArrayList<Long>();
        activityIds.add(act1Id);

        final Set<String> openSocialIds = new HashSet<String>();
        openSocialIds.add("grape");
        openSocialIds.add("potato");

        final List<PersonModelView> people = new ArrayList<PersonModelView>();
        PersonModelView p1 = new PersonModelView();
        PersonModelView p2 = new PersonModelView();
        people.add(p1);
        people.add(p2);

        p1.setAccountId("ACCT1");
        p2.setAccountId("ACCT2");

        String expectedJson = "{\"count\":" + maxActivitiesToReturnByOpenSocialId
                + ",\"query\":{\"recipient\":[{\"name\":\"ACCT1\",\"type\":\"PERSON\"},"
                + "{\"name\":\"ACCT2\",\"type\":\"PERSON\"}]}}";

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(actionContext).getParams();
                will(returnValue(request));

                // activity ids
                allowing(request).getActivityIds();
                will(returnValue(activityIds));

                // open social ids
                allowing(request).getOpenSocialIds();
                will(returnValue(openSocialIds));

                oneOf(getPeopleByOpenSocialIds).execute(with(any(ArrayList.class)));
                will(returnValue(people));                
                
                oneOf(principal).getId();
                will(returnValue(principalId));
                
                oneOf(securityTrimmer).trim(new ArrayList<Long>(), principalId);
                will(returnValue(new ArrayList<Long>()));

                oneOf(activities).getPagedSet();
                will(returnValue(activityList));
            }
        });

        List<ActivityDTO> results = sut.execute(actionContext);
        assertEquals(2, results.size());
        assertTrue(results.contains(a1));
        assertTrue(results.contains(a2));

        String receievedJson = (String) executionStrategy.getContextPassedIn().getParams();
        assertEquals(expectedJson, receievedJson);

        context.assertIsSatisfied();
    }

    /**
     * Test execute with open social ids, and an activity id that's already been found by destination stream.
     */
    @Test
    public void testExecuteOpenSocialIdsAndActivityId()
    {
        final Long act1Id = 827L;
        final Long act2Id = 82337L;

        final Long newActId = 8888L;
        final ActivityDTO newAct = new ActivityDTO();
        final List<ActivityDTO> responseById = new ArrayList<ActivityDTO>();
        responseById.add(newAct);

        final PagedSet<ActivityDTO> activities = context.mock(PagedSet.class);
        final List<ActivityDTO> activityList = new ArrayList<ActivityDTO>();
        ActivityDTO a1 = new ActivityDTO();
        ActivityDTO a2 = new ActivityDTO();
        a1.setId(act1Id);
        a2.setId(act2Id);
        activityList.add(a1);
        activityList.add(a2);

        final ExecutionStrategyFake executionStrategy = new ExecutionStrategyFake(activities);
        GetUserActivitiesExecution sut = new GetUserActivitiesExecution(bulkActivitiesMapper, getPeopleByOpenSocialIds,
                executionStrategy, maxActivitiesToReturnByOpenSocialId, securityTrimmer);

        final Principal principal = context.mock(Principal.class);
        final Long principalId = 1L;
        final PrincipalActionContext actionContext = context.mock(PrincipalActionContext.class);

        final GetUserActivitiesRequest request = context.mock(GetUserActivitiesRequest.class);

        final List<Long> activityIds = new ArrayList<Long>();
        activityIds.add(newActId);

        final Set<String> openSocialIds = new HashSet<String>();
        openSocialIds.add("grape");
        openSocialIds.add("potato");

        final List<PersonModelView> people = new ArrayList<PersonModelView>();
        PersonModelView p1 = new PersonModelView();
        PersonModelView p2 = new PersonModelView();
        people.add(p1);
        people.add(p2);

        p1.setAccountId("ACCT1");
        p2.setAccountId("ACCT2");

        String expectedJson = "{\"count\":" + maxActivitiesToReturnByOpenSocialId
                + ",\"query\":{\"recipient\":[{\"name\":\"ACCT1\",\"type\":\"PERSON\"},"
                + "{\"name\":\"ACCT2\",\"type\":\"PERSON\"}]}}";

        context.checking(new Expectations()
        {
            {
                allowing(actionContext).getPrincipal();
                will(returnValue(principal));

                oneOf(actionContext).getParams();
                will(returnValue(request));

                // activity ids
                allowing(request).getActivityIds();
                will(returnValue(activityIds));

                // open social ids
                allowing(request).getOpenSocialIds();
                will(returnValue(openSocialIds));

                oneOf(getPeopleByOpenSocialIds).execute(with(any(ArrayList.class)));
                will(returnValue(people));

                oneOf(bulkActivitiesMapper).execute(with(any(List.class)));
                will(returnValue(responseById));
                
                oneOf(principal).getId();
                will(returnValue(principalId));
                
                oneOf(securityTrimmer).trim(activityIds, principalId);
                will(returnValue(activityIds));
                
                oneOf(activities).getPagedSet();
                will(returnValue(activityList));
            }
        });

        List<ActivityDTO> results = sut.execute(actionContext);
        assertEquals(3, results.size());
        assertTrue(results.contains(a1));
        assertTrue(results.contains(a2));
        assertTrue(results.contains(newAct));

        String receievedJson = (String) executionStrategy.getContextPassedIn().getParams();
        assertEquals(expectedJson, receievedJson);

        context.assertIsSatisfied();
    }

    /**
     * Fake execution strategy.
     */
    private class ExecutionStrategyFake implements ExecutionStrategy<PrincipalActionContext>
    {
        /**
         * Result to return.
         */
        private Serializable result;

        /**
         * The action context passed into execute().
         */
        private PrincipalActionContext contextPassedIn;

        /**
         * Constructor.
         * 
         * @param inResult
         *            the result to return.
         */
        public ExecutionStrategyFake(final Serializable inResult)
        {
            result = inResult;
        }

        /**
         * Execute - return the result.
         * 
         * @param inActionContext
         *            the request
         * @return result that was passedinto constructor
         * @throws exception
         *             ... never
         */
        @Override
        public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
        {
            contextPassedIn = inActionContext;
            return result;
        }

        /**
         * Get the context passed into execute().
         * 
         * @return the context passed into execute
         */
        public PrincipalActionContext getContextPassedIn()
        {
            return contextPassedIn;
        }
    }
}
