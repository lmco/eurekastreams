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
package org.eurekastreams.server.action.execution.feed;

import java.util.LinkedList;
import java.util.List;

import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.client.ActionRequest;
import org.eurekastreams.server.domain.stream.plugins.Feed;
import org.eurekastreams.server.persistence.mappers.GetRefreshableFeedsMapper;
import org.eurekastreams.server.persistence.mappers.SetRefreshableFeedsAsPending;
import org.eurekastreams.server.persistence.mappers.requests.CurrentDateInMinutesRequest;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * FeedRefreshTask test.
 *
 */
public class RefreshFeedsExecutionTest
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
     * Get mapper mock.
     */
    private GetRefreshableFeedsMapper getFeedsMapper = context.mock(GetRefreshableFeedsMapper.class);
    /**
     * Set mapper mock.
     */
    private SetRefreshableFeedsAsPending setFeedsAsPendingMapper = context.mock(SetRefreshableFeedsAsPending.class);

    /**
     * Async action submitter mock.
     */
    private TaskHandlerActionContext actionContext = context.mock(TaskHandlerActionContext.class);

    /**
     * System under test.
     */
    private RefreshFeedsExecution sut =
    	new RefreshFeedsExecution(getFeedsMapper, setFeedsAsPendingMapper);

    /**
     * Execute test.
     *
     * @throws Exception
     *             the exception.
     */
    @Test
    public void execute() throws Exception
    {
        final List<Feed> feeds = new LinkedList<Feed>();
        feeds.add(new Feed());

        final List<ActionRequest> requests = new LinkedList<ActionRequest>();


        context.checking(new Expectations()
        {
            {
                oneOf(getFeedsMapper).execute(with(any(CurrentDateInMinutesRequest.class)));
                will(returnValue(feeds));

                oneOf(setFeedsAsPendingMapper).execute(with(any(CurrentDateInMinutesRequest.class)));

                oneOf(actionContext).getUserActionRequests();
                will(returnValue(requests));
            }
        });

        sut.execute(actionContext);

        context.assertIsSatisfied();
    }
}
