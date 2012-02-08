/*
 * Copyright (c) 2010-2012 Lockheed Martin Corporation
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

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.stream.ChangeStreamActivitySubscriptionRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.ChangeStreamActivitySubscriptionMapperRequest;

/**
 * Execution strategy to set a person's activity notification subscription preference for a stream.
 */
public class ChangeStreamActivitySubscriptionExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /** Logger. */
    private final Log log = LogFactory.make();

    /** DAO to get a stream entity id by its unique ID. */
    private final DomainMapper<String, Long> entityIdFromUniqueIdDAO;

    /** DAO to change a person's activity notification preference for a specific stream. */
    private final DomainMapper<ChangeStreamActivitySubscriptionMapperRequest, Boolean> changeNotificationPreferenceDAO;

    /**
     * Constructor.
     *
     * @param inEntityIdFromUniqueIdDAO
     *            Mapper to get a stream entity id by its unique ID.
     * @param inChangeNotificationPreferenceDAO
     *            mapper to change a person's group new activity notification preference for a specific group.
     */
    public ChangeStreamActivitySubscriptionExecution(final DomainMapper<String, Long> inEntityIdFromUniqueIdDAO,
            final DomainMapper<ChangeStreamActivitySubscriptionMapperRequest, Boolean> //
            inChangeNotificationPreferenceDAO)
    {
        entityIdFromUniqueIdDAO = inEntityIdFromUniqueIdDAO;
        changeNotificationPreferenceDAO = inChangeNotificationPreferenceDAO;
    }

    /**
     * Set the current user's group new activity subscription.
     *
     * @param inActionContext
     *            the action context with the ChangeGroupActivitySubscriptionRequest
     * @return false on error/failure, else true
     * @throws ExecutionException
     *             on error
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        ChangeStreamActivitySubscriptionRequest request = (ChangeStreamActivitySubscriptionRequest) inActionContext
                .getParams();

        if (log.isInfoEnabled())
        {
            log.info("Setting group email notification "
                    + (request.getReceiveNewActivityNotifications() ? "on" : "off") + " for "
                    + inActionContext.getPrincipal().getAccountId() + " and stream entity with unique id "
                    + request.getStreamEntityUniqueId());
        }

        // get the stream entity id
        Long id = entityIdFromUniqueIdDAO.execute(request.getStreamEntityUniqueId());

        // update the user's preference
        changeNotificationPreferenceDAO.execute(new ChangeStreamActivitySubscriptionMapperRequest(inActionContext
                .getPrincipal().getId(), id, request.getReceiveNewActivityNotifications(), request
                .getCoordinatorOnlyNotifications()));

        return null;
    }
}
