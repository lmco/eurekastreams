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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.ApplicationAlertNotification;
import org.eurekastreams.server.persistence.mappers.db.GetApplicationAlertsByUserId;

/**
 * This strategy gets all application alerts for a given user up to a configured max number.
 */
public class GetApplicationAlertsExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Mapper to get application alerts.
     */
    private GetApplicationAlertsByUserId alertMapper;

    /**
     * Max items to return.
     */
    private int count;

    /**
     * Constructor.
     *
     * @param inAlertMapper
     *            the alert mapper to set.
     * @param inCount
     *            the count to set.
     */
    public GetApplicationAlertsExecution(final GetApplicationAlertsByUserId inAlertMapper, final int inCount)
    {
        alertMapper = inAlertMapper;
        count = inCount;
    }

    /**
     * {@inheritDoc} This method calls a mapper to retrieve all application alerts for the current user (up to a
     * specifiec max count).
     */
    @Override
    @SuppressWarnings("unchecked")
    public Serializable execute(final PrincipalActionContext inActionContext)
    {
        long userId = inActionContext.getPrincipal().getId();
        List<ApplicationAlertNotification> results = alertMapper.execute(userId, count);

        if (log.isTraceEnabled())
        {
            log.trace("Found" + results.size() + " alerts for user " + userId);
        }

        return new ArrayList(results);
    }
}
