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
package org.eurekastreams.server.action.execution.notification;

import java.io.Serializable;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ClientPrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest;
import org.eurekastreams.server.action.request.notification.CreateNotificationsRequest.RequestType;
import org.eurekastreams.server.action.request.notification.PrebuiltNotificationsRequest;
import org.eurekastreams.server.action.request.notification.SendPrebuiltNotificationRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.search.modelview.PersonModelView;

/**
 * Action to inject a pre-built notification into the system.
 */
public class SendPrebuiltNotificationExecution implements TaskHandlerExecutionStrategy<ClientPrincipalActionContext>
{
    /** Mapper to get recipient id. */
    private final DomainMapper<String, Long> personIdMapper;

    /**
     * Constructor.
     *
     * @param inPersonIdMapper
     *            Mapper to get recipient id.
     */
    public SendPrebuiltNotificationExecution(final DomainMapper<String, Long> inPersonIdMapper)
    {
        personIdMapper = inPersonIdMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<ClientPrincipalActionContext> inWrapperContext)
    {
        final ClientPrincipalActionContext actionContext = inWrapperContext.getActionContext();

        String clientId = actionContext.getClientUniqueId();
        SendPrebuiltNotificationRequest params = (SendPrebuiltNotificationRequest) actionContext.getParams();

        PersonModelView recipient = (PersonModelView) actionContext.getState().get("recipient");
        long recipientId = recipient != null ? recipient.getId() : personIdMapper.execute(params
                .getRecipientAccountId());

        PrebuiltNotificationsRequest notifRequest = new PrebuiltNotificationsRequest(RequestType.EXTERNAL_PRE_BUILT,
                params.isHighPriority(), clientId, recipientId, params.getMessage(), params.getUrl());
        inWrapperContext.getUserActionRequests().add(
                new UserActionRequest(CreateNotificationsRequest.ACTION_NAME, null, notifRequest));

        return null;
    }
}
