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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.DeleteFromSearchIndexRequest;
import org.eurekastreams.server.action.request.stream.DeleteIdsFromListsRequest;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.db.DeleteActivities;
import org.eurekastreams.server.persistence.mappers.db.GetListsContainingActivities;
import org.eurekastreams.server.persistence.mappers.requests.BulkActivityDeleteResponse;

/**
 * This execution strategy is responsible for deleting a batch of activities based on a list of activity ids supplied.
 *
 * This execution strategy will handle deleting the activities directly from the db and cache and then offload cached
 * list updates to an async job.
 *
 */
public class DeleteActivitiesByIdsExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * Local instance of logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Mapper to get a list of cache keys that contain references to expired activities.
     */
    private GetListsContainingActivities listsMapper;

    /**
     * Mapper to remove expired activities from the database.
     */
    private DeleteActivities deleteMapper;

    /**
     * Constructor.
     *
     * @param inListsMapper
     *            the lists containing activity references mapper.
     * @param inDeleteMapper
     *            the delete mapper.
     */
    public DeleteActivitiesByIdsExecution(final GetListsContainingActivities inListsMapper,
            final DeleteActivities inDeleteMapper)
    {
        listsMapper = inListsMapper;
        deleteMapper = inDeleteMapper;
    }

    /**
     * Delete the activities from the database and submit an async task to remove the activities from the respective
     * cache locations. {@inheritDoc}.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        List<Long> activityIds = (List<Long>) inActionContext.getActionContext().getParams();

        if (log.isTraceEnabled())
        {
            log.trace("Calling DeleteMapper to delete " + activityIds.size() + " activities");
        }

        List<String> keys = listsMapper.execute(activityIds);

        // Deletes the activities and their comments from the database
        BulkActivityDeleteResponse response = deleteMapper.execute(activityIds);

        // Put an action on the queue to delete the activities from the appropriate lists
        if (log.isInfoEnabled())
        {
            log.info("Queuing UserActionRequest for removing expired activities from index (num of ids): "
                    + response.getActivityIds().size());
        }
        // Put an action on the queue to delete the activities from search index
        inActionContext.getUserActionRequests().add(
                new UserActionRequest("deleteFromSearchIndexAction", null, new DeleteFromSearchIndexRequest(
                        Activity.class, response.getActivityIds())));

        if (log.isInfoEnabled())
        {
            log.info("Queuing UserActionRequest for removing expired activity ids from lists cache keys: " + keys);
        }
        inActionContext.getUserActionRequests().add(
                new UserActionRequest("deleteIdsFromLists", null, new DeleteIdsFromListsRequest(keys, response
                        .getActivityIds())));

        if (log.isInfoEnabled())
        {
            log.info("Queuing UserActionRequests for removing expired activity ids from cache: "
                    + response.getActivityIds().size());
        }
        // remove ALL activities from cache (low priority should we even do this?)
        generateIndividualDeleteKeyFromCacheTasks(new HashSet<String>(createKeys(CacheKeys.ACTIVITY_BY_ID, response
                .getActivityIds())), inActionContext);

        if (log.isInfoEnabled())
        {
            log.info("Queuing UserActionRequests for removing expired activitys' comment ids from cache: "
                    + response.getCommentIds());
        }
        // remove ALL comments from cache (low priority should we even do this?)
        generateIndividualDeleteKeyFromCacheTasks(new HashSet<String>(createKeys(CacheKeys.COMMENT_BY_ID, response
                .getCommentIds())), inActionContext);

        return null;
    }

    /**
     * Queues UserActionRequest for deleteKeysFromCache action, one for each key.
     *
     * @param keys
     *            keys to delete from cache.
     * @param inActionContext
     *            {@link TaskHandlerActionContext}.
     */
    private void generateIndividualDeleteKeyFromCacheTasks(final Set<String> keys,
            final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        Iterator<String> it = keys.iterator();
        while (it.hasNext())
        {
            inActionContext.getUserActionRequests()
                    .add(
                            new UserActionRequest("deleteCacheKeysAction", null, new HashSet<String>(Arrays.asList(it
                                    .next()))));
        }
    }

    /**
     * Generate cacheKeys.
     *
     * @param keyRoot
     *            Root of key.
     * @param inIds
     *            Id for key
     * @return List of generated keys.
     */
    private List<String> createKeys(final String keyRoot, final List<Long> inIds)
    {
        List<String> keys = new ArrayList<String>();

        for (Long id : inIds)
        {
            keys.add(keyRoot + id);
        }

        return keys;
    }

}
