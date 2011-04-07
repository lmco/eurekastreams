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
package org.eurekastreams.server.action.execution.stream;

import java.io.Serializable;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;

/**
 * Service action that calls mapper to hide resource activity, then queues up async action to complete the cache
 * updates.
 * 
 */
public class HideResourceActivityExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * Mapper to hide resource activity.
     */
    private DomainMapper<Long, Void> hideResourceActivityMapper;

    /**
     * Constructor.
     * 
     * @param inHideResourceActivityMapper
     *            Mapper to hide resource activity.
     */
    public HideResourceActivityExecution(final DomainMapper<Long, Void> inHideResourceActivityMapper)
    {
        hideResourceActivityMapper = inHideResourceActivityMapper;
    }

    /**
     * Calls mapper to hide resource activity, then queues up async action to complete the cache updates.
     * 
     * @param inActionContext
     *            action context.
     * @return true upon success.
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        Long activityId = (Long) inActionContext.getActionContext().getParams();

        hideResourceActivityMapper.execute(activityId);

        // submit request for additional cache updates due to activity hide.
        inActionContext.getUserActionRequests().add(
                new UserActionRequest("hideResourceActivityCacheUpdate", null, activityId));

        return true;
    }

}
