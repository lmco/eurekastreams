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
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.IndexEntity;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;

/**
 * Submit the activity to search index.
 * 
 */
public class IndexActivityByIdExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Activity entity mapper.
     */
    private FindByIdMapper<Activity> activityMapper;

    /**
     * {@link IndexEntity} mapper.
     */
    private IndexEntity<Activity> activityIndexer;

    /**
     * Constructor.
     * 
     * @param inActivityMapper
     *            Activity entity mapper.
     * @param inActivityIndexer
     *            {@link IndexEntity} mapper.
     */
    public IndexActivityByIdExecution(final FindByIdMapper<Activity> inActivityMapper,
            final IndexEntity<Activity> inActivityIndexer)
    {
        activityMapper = inActivityMapper;
        activityIndexer = inActivityIndexer;
    }

    /**
     * Submit the activity to search index if found in DB.
     * 
     * @param inActionContext
     *            {@link ActionContext}.
     * @return null.
     */
    @Override
    public Serializable execute(final ActionContext inActionContext)
    {
        Activity activity = activityMapper.execute(new FindByIdRequest("Activity", (Long) inActionContext.getParams()));
        if (activity != null)
        {
            activityIndexer.execute(activity);
        }

        return null;
    }

}
