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
package org.eurekastreams.server.action.execution.stream;

import java.io.Serializable;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.stream.SetActivityStarRequest;
import org.eurekastreams.server.action.request.stream.SetActivityStarRequest.StarActionType;
import org.eurekastreams.server.domain.stream.StarredActivity;
import org.eurekastreams.server.persistence.mappers.DeleteStarredActivity;
import org.eurekastreams.server.persistence.mappers.InsertStarredActivity;

/**
 * Action to add or remove star on activity for current user.
 *
 */
public class SetActivityStarExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Mapper for adding star.
     */
    private InsertStarredActivity insertStarredActivity;

    /**
     * Mapper for removing star.
     */
    private DeleteStarredActivity deleteStarredActivity;

    /**
     * Constructor.
     * @param inInsertStarredActivity Mapper for starring an activity.
     * @param inDeleteStarredActivity Mapper for unstarring an activity.
     */
    public SetActivityStarExecution(final InsertStarredActivity inInsertStarredActivity,
            final DeleteStarredActivity inDeleteStarredActivity)
    {
        insertStarredActivity = inInsertStarredActivity;
        deleteStarredActivity = inDeleteStarredActivity;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        SetActivityStarRequest request = (SetActivityStarRequest) inActionContext.getParams();
        StarredActivity starActivityData = new StarredActivity(
                inActionContext.getPrincipal().getId(),
                request.getActivityId());

        return (request.getStarActionType() == StarActionType.ADD_STAR)
            ? insertStarredActivity.execute(starActivityData)
            : deleteStarredActivity.execute(starActivityData);
    }

}
