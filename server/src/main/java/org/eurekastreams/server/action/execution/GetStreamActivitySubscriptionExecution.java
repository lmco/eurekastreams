/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.GetStreamActivitySubscriptionMapperRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Execution strategy to get a person's activity notification subscription preference for a followed stream.
 */
public class GetStreamActivitySubscriptionExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /** Local logger instance. */
    private final Logger log = LoggerFactory.getLogger(LogFactory.getClassName());

    /** DAO to get a stream entity id by its unique ID. */
    private final DomainMapper<String, Long> entityIdFromUniqueIdDAO;

    /** DAO to get a person's activity notification preference for a specific stream. */
    private final DomainMapper<GetStreamActivitySubscriptionMapperRequest, Boolean> getNotificationPreferenceDAO;

    /**
     * Constructor.
     *
     * @param inEntityIdFromUniqueIdDAO
     *            Mapper to get a stream entity id by its unique ID.
     * @param inGetNotificationPreferenceDAO
     *            DAO to get a person's activity notification preference for a specific stream.
     */
    public GetStreamActivitySubscriptionExecution(final DomainMapper<String, Long> inEntityIdFromUniqueIdDAO,
            final DomainMapper<GetStreamActivitySubscriptionMapperRequest, Boolean> //
            inGetNotificationPreferenceDAO)
    {
        entityIdFromUniqueIdDAO = inEntityIdFromUniqueIdDAO;
        getNotificationPreferenceDAO = inGetNotificationPreferenceDAO;
    }

    /**
     * Get a person's activity notification preference for a specific stream.
     * 
     * @param inActionContext
     *            the action context with the stream unique id.
     * @return If subscribed.
     * @throws ExecutionException
     *             on error
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        String uniqueId = (String) inActionContext.getParams();

        log.debug("Getting group email notification for {} and stream entity with unique id {}", inActionContext
                .getPrincipal().getAccountId(), uniqueId);

        // get the stream entity id
        Long id = entityIdFromUniqueIdDAO.execute(uniqueId);

        // A null 'id' indicates that no such group exists
        // for this id. As a result, it's acceptable to return false,
        // which means that the current user is not signed up for 
        // notifications of the current group, which does not exist.
        if (id == null)
        {
        	return false;
        }
        else
        {
        // get the user's preference
        return getNotificationPreferenceDAO.execute(new GetStreamActivitySubscriptionMapperRequest(inActionContext
                .getPrincipal().getId(), id));
        }
    }
}
