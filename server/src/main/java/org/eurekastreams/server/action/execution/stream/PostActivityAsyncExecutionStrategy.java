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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.async.AsyncActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.stream.PostActivityRequest;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.persistence.mappers.FindByIdMapper;
import org.eurekastreams.server.persistence.mappers.requests.FindByIdRequest;
import org.eurekastreams.server.persistence.mappers.stream.PostCachedActivity;

/**
 * This class provides the Async ExecutionStrategy for the PostActivity action.
 *
 * Add the activity to cache, extract its hashtags, and store them in the database for each stream that will use them
 * for their popular hashtags.
 *
 */
public class PostActivityAsyncExecutionStrategy implements ExecutionStrategy<AsyncActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Mapper to perform cache post.
     */
    private final PostCachedActivity postCachedActivityMapper;

    /**
     * Mapper to find an activity by id.
     */
    private final FindByIdMapper<Activity> findByIdMapper;

    /**
     * Strategy to store hashtags for streams based on an activity.
     */
    private final StoreStreamHashTagsForActivityStrategy storeStreamHashTagStrategy;

    /**
     * Constructor for the PostActivityAsyncExecutionStrategy class.
     *
     * @param inPostCachedActivityMapper
     *            - instance of the {@link PostCachedActivity} mapper that will perform the cache updates.
     * @param inFindByIdMapper
     *            mapper to find an activity by id
     * @param inStoreStreamHashTagStrategy
     *            strategy to store activities to streams in the database
     */
    public PostActivityAsyncExecutionStrategy(final PostCachedActivity inPostCachedActivityMapper,
            final FindByIdMapper<Activity> inFindByIdMapper,
            final StoreStreamHashTagsForActivityStrategy inStoreStreamHashTagStrategy)
    {
        postCachedActivityMapper = inPostCachedActivityMapper;
        findByIdMapper = inFindByIdMapper;
        storeStreamHashTagStrategy = inStoreStreamHashTagStrategy;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public Serializable execute(final AsyncActionContext inActionContext) throws ExecutionException
    {
        ActivityDTO currentActivity = ((PostActivityRequest) inActionContext.getParams()).getActivityDTO();

        log.info("Updating caches for activity #" + currentActivity.getId());
        postCachedActivityMapper.execute(currentActivity);

        Activity activity = findByIdMapper.execute(new FindByIdRequest("Activity", currentActivity.getId()));
        if (activity != null)
        {
            log.info("Activity #" + currentActivity.getId() + " was found - no need to set its hashtags");
            storeStreamHashTagStrategy.execute(activity);

        }
        return null;
    }

}
