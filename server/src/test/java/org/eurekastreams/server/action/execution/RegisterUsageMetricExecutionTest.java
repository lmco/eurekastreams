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
package org.eurekastreams.server.action.execution;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;

import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.date.DayOfWeekStrategy;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.UsageMetric;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.eurekastreams.server.search.modelview.UsageMetricDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test for RegisterUserMetricExecution.
 * 
 */
public class RegisterUsageMetricExecutionTest
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
     * Mocked isntance of the {@link Principal}.
     */
    private final Principal principalMock = context.mock(Principal.class);

    /**
     * Mocked instance of the {@link PrincipalActionContext}.
     */
    private final PrincipalActionContext principalActionContextMock = context.mock(PrincipalActionContext.class);

    /**
     * Mocked instance of the {@link TaskHandlerActionContext}.
     */
    private final TaskHandlerActionContext taskHandlerActionContextMock = context.mock(TaskHandlerActionContext.class);

    /**
     * Mapper to get a person stream scope id.
     */
    private DomainMapper<String, Long> personStreamScopeIdMapper = context.mock(DomainMapper.class,
            "personStreamScopeIdMapper");

    /**
     * Mapper to get a group stream scope id.
     */
    private DomainMapper<String, Long> groupStreamScopeIdMapper = context.mock(DomainMapper.class,
            "groupStreamScopeIdMapper");

    /**
     * Mocked instance of the UsageMetricDTO.
     */
    private final UsageMetricDTO um = context.mock(UsageMetricDTO.class);

    /**
     * Strategy to determine if a date is a weekday.
     */
    private final DayOfWeekStrategy dayOfWeekStrategy = context.mock(DayOfWeekStrategy.class);

    /**
     * System under test.
     */
    private RegisterUsageMetricExecution sut = new RegisterUsageMetricExecution(personStreamScopeIdMapper,
            groupStreamScopeIdMapper, dayOfWeekStrategy);

    /**
     * Test performing the action on a group stream view.
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    public final void textPerformActionForGroupStreamView() throws Exception
    {
        final ArrayList<UserActionRequest> uar = new ArrayList<UserActionRequest>();
        final Long groupStreamScopeId = 232L;

        context.checking(new Expectations()
        {
            {
                oneOf(dayOfWeekStrategy).isWeekday(with(any(Date.class)));
                will(returnValue(true));

                allowing(taskHandlerActionContextMock).getActionContext();
                will(returnValue(principalActionContextMock));

                oneOf(principalActionContextMock).getPrincipal();
                will(returnValue(principalMock));

                oneOf(principalActionContextMock).getParams();
                will(returnValue(um));

                allowing(um).isStreamView();
                will(returnValue(true));

                allowing(um).isPageView();
                will(returnValue(true));

                allowing(um).getMetricDetails();
                will(returnValue("{\"query\":{\"recipient\":[{\"type\":\"GROUP\", \"name\":\"woot\"}], "
                        + "\"sortBy\":\"date\"}}"));

                allowing(groupStreamScopeIdMapper).execute("woot");
                will(returnValue(groupStreamScopeId));

                oneOf(principalMock).getId();
                will(returnValue(1L));

                oneOf(principalMock).getAccountId();
                will(returnValue("accountId"));

                allowing(taskHandlerActionContextMock).getUserActionRequests();
                will(returnValue(uar));
            }
        });

        sut.execute(taskHandlerActionContextMock);

        assertEquals(1, uar.size());

        // make sure the stream scope id of the group was found
        assertEquals(groupStreamScopeId, ((UsageMetric) (((PersistenceRequest) uar.get(0).getParams()))
                .getDomainEnity()).getStreamViewStreamScopeId());

        context.assertIsSatisfied();
    }

    /**
     * Test performing the action on a person stream view.
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    public final void textPerformActionForPersonStreamView() throws Exception
    {
        final ArrayList<UserActionRequest> uar = new ArrayList<UserActionRequest>();
        final Long presonStreamScopeId = 999L;

        context.checking(new Expectations()
        {
            {
                oneOf(dayOfWeekStrategy).isWeekday(with(any(Date.class)));
                will(returnValue(true));

                allowing(taskHandlerActionContextMock).getActionContext();
                will(returnValue(principalActionContextMock));

                oneOf(principalActionContextMock).getPrincipal();
                will(returnValue(principalMock));

                oneOf(principalActionContextMock).getParams();
                will(returnValue(um));

                allowing(um).isStreamView();
                will(returnValue(true));

                allowing(um).isPageView();
                will(returnValue(true));

                allowing(um).getMetricDetails();
                will(returnValue("{\"query\":{\"recipient\":[{\"type\":\"PERSON\", \"name\":\"wooter\"}], "
                        + "\"sortBy\":\"date\"}}"));

                allowing(personStreamScopeIdMapper).execute("wooter");
                will(returnValue(presonStreamScopeId));

                oneOf(principalMock).getId();
                will(returnValue(1L));

                oneOf(principalMock).getAccountId();
                will(returnValue("accountId"));

                allowing(taskHandlerActionContextMock).getUserActionRequests();
                will(returnValue(uar));
            }
        });

        sut.execute(taskHandlerActionContextMock);

        assertEquals(1, uar.size());

        // make sure the stream scope id of the group was found
        assertEquals(presonStreamScopeId, ((UsageMetric) (((PersistenceRequest) uar.get(0).getParams()))
                .getDomainEnity()).getStreamViewStreamScopeId());

        context.assertIsSatisfied();
    }

    /**
     * Test performing the action with bad JSON - invalid resource type.
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    public final void textPerformActionForBadStreamJSONInvalidType() throws Exception
    {
        runTestWithJSON("{\"query\":{\"recipient\":[{\"type\":\"RESOURCE\", \"name\":\"wooter\"}], "
                + "\"sortBy\":\"date\"}}");
    }

    /**
     * Test performing the action with bad JSON - invalid recipient type.
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    public final void textPerformActionForBadStreamJSONBadType() throws Exception
    {
        runTestWithJSON("{\"query\":{\"recipient\":[{\"type\":\"SFSDF\", \"name\":\"wooter\"}], \"sortBy\":\"date\"}}");
    }

    /**
     * Test performing the action with bad JSON - missing "query".
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    public final void textPerformActionForBadStreamJSONMissingQuery() throws Exception
    {
        runTestWithJSON("{\"foo\":{\"recipient\":[{\"type\":\"PERSON\", \"name\":\"wooter\"}], \"sortBy\":\"date\"}}");
    }

    /**
     * Test performing the action with bad JSON - missing "recipient".
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    public final void textPerformActionForBadStreamJSONMissingRecipient() throws Exception
    {
        runTestWithJSON("{\"query\":{\"foo\":[{\"type\":\"PERSON\", \"name\":\"wooter\"}], \"sortBy\":\"date\"}}");
    }

    /**
     * Test performing the action with bad JSON - missing "type".
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    public final void textPerformActionForBadStreamJSONMissingType() throws Exception
    {
        runTestWithJSON("{\"query\":{\"recipient\":[{\"foo\":\"PERSON\", \"name\":\"wooter\"}], \"sortBy\":\"date\"}}");
    }

    /**
     * Test performing the action with bad JSON - missing "name".
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    public final void textPerformActionForBadStreamJSONMissingName() throws Exception
    {
        runTestWithJSON("{\"query\":{\"recipient\":[{\"type\":\"PERSON\", \"foo\":\"wooter\"}], \"sortBy\":\"date\"}}");
    }

    /**
     * Test performing the action with bad JSON - multiple recipients.
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    public final void textPerformActionForBadStreamJSONMultipleRecipients() throws Exception
    {
        runTestWithJSON("{\"query\":{\"recipient\":[{\"type\":\"PERSON\", \"name\":\"caldwelw\"},{\"type\":\"GROUP\", "
                + "\"name\":\"privategroup\"}], \"sortBy\":\"date\"}}");
    }

    /**
     * Test performing the action with null JSON.
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    public final void textPerformActionForBadStreamJSONNullJSON() throws Exception
    {
        runTestWithJSON(null);
    }

    /**
     * Test performing the action with invalid JSON.
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    public final void textPerformActionForBadStreamJSONInvalidJSON1() throws Exception
    {
        runTestWithJSON("{dksljfsjdf");
    }

    /**
     * Test performing the action with invalid JSON.
     * 
     * @throws Exception
     *             not expected.
     */
    @Test
    public final void textPerformActionForBadStreamJSONInvalidJSON2() throws Exception
    {
        runTestWithJSON("sdlkfj");
    }

    /**
     * Test performing the action on a weekend.
     */
    @Test
    public final void textPerformActionOnWeekend()
    {
        final ArrayList<UserActionRequest> uar = new ArrayList<UserActionRequest>();

        context.checking(new Expectations()
        {
            {
                oneOf(dayOfWeekStrategy).isWeekday(with(any(Date.class)));
                will(returnValue(false));
            }
        });

        sut.execute(taskHandlerActionContextMock);

        context.assertIsSatisfied();
    }

    /**
     * Run the test with the input json.
     * 
     * @param json
     *            the JSON to test
     */
    private void runTestWithJSON(final String json)
    {
        final ArrayList<UserActionRequest> uar = new ArrayList<UserActionRequest>();

        context.checking(new Expectations()
        {
            {
                oneOf(dayOfWeekStrategy).isWeekday(with(any(Date.class)));
                will(returnValue(true));

                allowing(taskHandlerActionContextMock).getActionContext();
                will(returnValue(principalActionContextMock));

                oneOf(principalActionContextMock).getPrincipal();
                will(returnValue(principalMock));

                oneOf(principalActionContextMock).getParams();
                will(returnValue(um));

                allowing(um).isStreamView();
                will(returnValue(true));

                allowing(um).isPageView();
                will(returnValue(true));

                allowing(um).getMetricDetails();
                will(returnValue(json));

                oneOf(principalMock).getId();
                will(returnValue(1L));

                oneOf(principalMock).getAccountId();
                will(returnValue("accountId"));

                allowing(taskHandlerActionContextMock).getUserActionRequests();
                will(returnValue(uar));
            }
        });

        sut.execute(taskHandlerActionContextMock);

        Assert.assertEquals(1, uar.size());

        // make sure the stream scope id of the group was found
        Assert.assertNull(((UsageMetric) (((PersistenceRequest) uar.get(0).getParams())).getDomainEnity())
                .getStreamViewStreamScopeId());

        context.assertIsSatisfied();
    }
}
