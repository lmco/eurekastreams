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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.feed.RefreshFeedRequest;
import org.eurekastreams.server.domain.stream.plugins.Feed;
import org.eurekastreams.server.persistence.mappers.GetRefreshableFeedsMapper;
import org.eurekastreams.server.persistence.mappers.SetRefreshableFeedsAsPending;
import org.eurekastreams.server.persistence.mappers.requests.CurrentDateInMinutesRequest;

/**
 * Feed refresh task. Get the feeds that need to be updated. Pop an action on the queue. Set them as pending.
 *
 */
public class RefreshFeedsExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * For converting milliseconds to minutes.
     */
    private static final int MILLISECONDSINMINUTE = 60000;
    /**
     * Mapper to get the feeds.
     */
    private GetRefreshableFeedsMapper getFeedsMapper;
    /**
     * Mapper to set the feeds as pending.
     */
    private SetRefreshableFeedsAsPending setFeedsAsPendingMapper;


    /**
     * Default constructor.
     */
    public RefreshFeedsExecution()
    {
    }

    /**
     * Default constructor.
     *
     * @param inGetFeedsMapper
     *            getter.
     * @param inSetFeedsAsPendingMapper
     *            setter.
     */
    public RefreshFeedsExecution(final GetRefreshableFeedsMapper inGetFeedsMapper,
            final SetRefreshableFeedsAsPending inSetFeedsAsPendingMapper)
    {
        getFeedsMapper = inGetFeedsMapper;
        setFeedsAsPendingMapper = inSetFeedsAsPendingMapper;
    }

    /**
     * {@inheritDoc}.
     *
     * Grab all the feeds, set them as pending, and fire off an async job. to refresh each one.
     *
     */

    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        CurrentDateInMinutesRequest request = new CurrentDateInMinutesRequest(new Date().getTime()
                / (MILLISECONDSINMINUTE));

        List<Feed> feeds = getFeedsMapper.execute(request);
        List<Long> feedIds = new ArrayList<Long>();
        for (Feed feed : feeds)
        {
            feedIds.add(feed.getId());
        }
        setFeedsAsPendingMapper.execute(request);

        for (Long id : feedIds)
        {
             inActionContext.getUserActionRequests().add(
                        new UserActionRequest("refreshFeedAction", null, new RefreshFeedRequest(id)));
        }

        return null;
    }
}
