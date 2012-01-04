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

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.feed.DeleteFeedSubscriptionRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Delete a feed subscriber action.
 * 
 */
public class DeleteFeedSubscriberExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Delete Mapper.
     */
    private DomainMapper<Long, Boolean> deleteMapper;

    /**
     * Default constructor.
     * 
     * @param inDeleteMapper
     *            the delete mapper.
     */
    public DeleteFeedSubscriberExecution(final DomainMapper<Long, Boolean> inDeleteMapper)
    {
        deleteMapper = inDeleteMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean execute(final ActionContext inActionContext) throws ExecutionException
    {
        DeleteFeedSubscriptionRequest request = (DeleteFeedSubscriptionRequest) inActionContext.getParams();
        return deleteMapper.execute(request.getFeedSubscriberId());
    }

}
