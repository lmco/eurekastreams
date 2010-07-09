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
package org.eurekastreams.server.action.execution.notification;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.cache.GetCachedAlertCountByUserId;

/**
 * This strategy gets the current user's count of unread application alerts from cache.
 */
public class GetUnreadApplicationAlertCountExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Mapper to get application alert count.
     */
    private GetCachedAlertCountByUserId alertCountMapper;

    /**
     * Constructor.
     * 
     * @param inAlertCountMapper
     *            the alert count mapper to set.
     */
    public GetUnreadApplicationAlertCountExecution(final GetCachedAlertCountByUserId inAlertCountMapper)
    {
        alertCountMapper = inAlertCountMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext)
    {
        long userId = inActionContext.getPrincipal().getId();
        int count = alertCountMapper.execute(userId);

        if (log.isTraceEnabled())
        {
            log.trace("Found" + count + " unread alerts for user " + userId);
        }

        return count;
    }
}
