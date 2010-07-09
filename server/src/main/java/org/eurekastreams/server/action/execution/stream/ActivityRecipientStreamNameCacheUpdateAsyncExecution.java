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
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.cache.UpdateDestinationStreamNameInCachedActivity;
import org.eurekastreams.server.persistence.mappers.db.GetActivityIdsPostedToStreamByUniqueKeyAndScopeType;
import org.eurekastreams.server.persistence.mappers.db.GetFieldFromTableByUniqueField;

/**
 * Execution strategy to update the destination stream name of all cached activities posted to a stream.
 */
public class ActivityRecipientStreamNameCacheUpdateAsyncExecution implements ExecutionStrategy<ActionContext>
{
    /**
     * Log.
     */
    private Log log = LogFactory.make();

    /**
     * Cache updater.
     */
    private UpdateDestinationStreamNameInCachedActivity cacheUpdater;

    /**
     * Mapper to get the activity ids posted to a stream.
     */
    private GetActivityIdsPostedToStreamByUniqueKeyAndScopeType activityIdMapper;

    /**
     * Mapper to get a Nameable by string id.
     */
    private GetFieldFromTableByUniqueField<String, String> getDisplayNameMapper;

    /**
     * The scope type to update.
     */
    private ScopeType streamScopeType;

    /**
     * Constructor.
     *
     * @param inStreamScopeType
     *            the stream scope type to update
     * @param inGetDisplayNameMapper
     *            the mapper to get the display name of the stream owner
     * @param inActivityIdMapper
     *            mapper to get the activity ids of all activities posted to a stream
     * @param inCacheUpdater
     *            activity cache updater
     */
    public ActivityRecipientStreamNameCacheUpdateAsyncExecution(final ScopeType inStreamScopeType,
            final GetFieldFromTableByUniqueField<String, String> inGetDisplayNameMapper,
            final GetActivityIdsPostedToStreamByUniqueKeyAndScopeType inActivityIdMapper,
            final UpdateDestinationStreamNameInCachedActivity inCacheUpdater)
    {
        streamScopeType = inStreamScopeType;
        getDisplayNameMapper = inGetDisplayNameMapper;
        activityIdMapper = inActivityIdMapper;
        cacheUpdater = inCacheUpdater;
    }

    /**
     * Update the recipient stream scope display name in cache for all activities posted to the stream with the input
     * short name.
     *
     * @param inActionContext
     *            the action context, with the stream short name as the parameter
     * @return true
     */
    @Override
    public Serializable execute(final ActionContext inActionContext)
    {
        String streamShortName = (String) inActionContext.getParams();

        if (log.isInfoEnabled())
        {
            log.info("Updating the recipient stream name for all activities posted to stream of type "
                    + streamScopeType + " with short name " + streamShortName);
        }

        String ownerDisplayName = getDisplayNameMapper.execute(streamShortName);

        if (log.isInfoEnabled())
        {
            log.info("Found the owner's display name: " + ownerDisplayName);
        }

        List<Long> activityIds = activityIdMapper.execute(streamScopeType, streamShortName);
        if (log.isInfoEnabled())
        {
            log.info("Found " + activityIds.size() + " activities to update.");
        }
        cacheUpdater.execute(activityIds, ownerDisplayName);
        return true;
    }
}
