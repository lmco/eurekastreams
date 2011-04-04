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
package org.eurekastreams.server.action.execution;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.Principal;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.domain.UsageMetric;
import org.eurekastreams.server.persistence.mappers.requests.PersistenceRequest;
import org.eurekastreams.server.search.modelview.UsageMetricDTO;

/**
 * Action to create UserMetric entity and queue up action to persist it.
 * 
 */
public class RegisterUserMetricExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /**
     * Local logger instance.
     */
    private final Log logger = LogFactory.make();

    /**
     * Create UserMetric entity and queue up action to persist it.
     * 
     * @param inActionContext
     *            ActionContext.
     * @return null;
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
    {
        UsageMetricDTO umdto = (UsageMetricDTO) inActionContext.getActionContext().getParams();
        Principal principal = inActionContext.getActionContext().getPrincipal();

        UsageMetric um = new UsageMetric(principal.getId(), umdto.isPageView(), umdto.isStreamView(), new Date());

        logger.trace("Registering metric for user: " + principal.getAccountId() + " StreamView:" + umdto.isStreamView()
                + " PageView:" + umdto.isPageView() + " MetricDetails: " + umdto.getMetricDetails());

        inActionContext.getUserActionRequests().add(
                new UserActionRequest("persistUserMetricAsyncAction", null, new PersistenceRequest<UsageMetric>(um)));
        return null;
    }
}
