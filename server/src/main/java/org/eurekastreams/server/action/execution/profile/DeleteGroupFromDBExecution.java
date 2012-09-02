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
package org.eurekastreams.server.action.execution.profile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.DeleteFromSearchIndexRequest;
import org.eurekastreams.server.action.request.stream.DeleteIdsFromListsRequest;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.Activity;
import org.eurekastreams.server.domain.stream.StreamScope.ScopeType;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.db.DeleteAllFeedSubscriberByEntityTypeAndId;
import org.eurekastreams.server.persistence.mappers.db.DeleteGroup;
import org.eurekastreams.server.persistence.mappers.db.DeleteGroupActivity;
import org.eurekastreams.server.persistence.mappers.db.RemoveGroupFollowers;
import org.eurekastreams.server.persistence.mappers.requests.BulkActivityDeleteResponse;
import org.eurekastreams.server.persistence.mappers.requests.DeleteAllFeedSubscriberByEntityTypeAndIdRequest;
import org.eurekastreams.server.persistence.mappers.requests.DeleteGroupResponse;

/**
 * Execution strategy for deleting a group and associated objects from database, and creating the UserActionRequests to
 * clean up cache and search index.
 * 
 */
public class DeleteGroupFromDBExecution implements TaskHandlerExecutionStrategy<ActionContext>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * {@link DeleteGroupActivity}.
     */
    private DeleteGroupActivity deleteGroupActivityDAO;

    /**
     * {@link RemoveGroupFollowers}.
     */
    private RemoveGroupFollowers removeGroupFollowersDAO;

    /**
     * {@link DeleteGroup}.
     */
    private DeleteGroup deleteGroupDAO;

    /**
     * {@link DeleteAllFeedSubscriberByEntityTypeAndId}.
     */
    private DeleteAllFeedSubscriberByEntityTypeAndId deleteGroupFeedSubscriptions;

    /**
     * Max cache list size.
     */
    private final int maxCacheListSize;

    /**
     * Constructor.
     * 
     * @param inDeleteGroupActivityDAO
     *            {@link DeleteGroupActivity}.
     * @param inRemoveGroupFollowersDAO
     *            {@link RemoveGroupFollowers}.
     * @param inDeleteGroupDAO
     *            {@link DeleteGroup}.
     * @param inDeleteGroupFeedSubscriptions
     *            {@link DeleteAllFeedSubscriberByEntityTypeAndId}.
     * @param inMaxCacheListSize
     *            Max size for cache list.
     */
    public DeleteGroupFromDBExecution(final DeleteGroupActivity inDeleteGroupActivityDAO,
            final RemoveGroupFollowers inRemoveGroupFollowersDAO, final DeleteGroup inDeleteGroupDAO,
            final DeleteAllFeedSubscriberByEntityTypeAndId inDeleteGroupFeedSubscriptions, final int inMaxCacheListSize)
    {
        deleteGroupActivityDAO = inDeleteGroupActivityDAO;
        removeGroupFollowersDAO = inRemoveGroupFollowersDAO;
        deleteGroupDAO = inDeleteGroupDAO;
        deleteGroupFeedSubscriptions = inDeleteGroupFeedSubscriptions;
        maxCacheListSize = inMaxCacheListSize;
    }

    /**
     * Deleting a group and associated objects from database.
     * 
     * @param inActionContext
     *            {@link TaskHandlerActionContext}.
     * @return True if successful.
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        // ================= Database deletes =======================
        Long groupId = (Long) inActionContext.getActionContext().getParams();

        Long startDB = null;
        if (log.isInfoEnabled())
        {
            startDB = System.currentTimeMillis();
        }

        Long startTime = null;
        Long endTime = null;
        if (log.isDebugEnabled())
        {
            log.debug("Deleting activities for groupid:" + groupId);
            startTime = System.currentTimeMillis();
        }
        // Delete comments/activities/starred activity associations
        BulkActivityDeleteResponse deleteActivityResponse = deleteGroupActivityDAO.execute(groupId);
        if (log.isDebugEnabled())
        {
            endTime = System.currentTimeMillis();
            log.debug("Time to delete activities/comments: " + (endTime - startTime));

            log.debug("Removing followers for groupid:" + groupId);
            startTime = endTime;
        }
        // Remove user/group follower associations (reset order indexes) and adjust groupsCount for followers.
        List<Long> followerIds = removeGroupFollowersDAO.execute(groupId);
        if (log.isDebugEnabled())
        {
            endTime = System.currentTimeMillis();
            log.debug("Time to remove followers from group: " + (endTime - startTime));

            log.debug("Removing group subscriptions for groupid:" + groupId);
            startTime = endTime;
        }
        // Remove feed subscriptions for group.
        deleteGroupFeedSubscriptions.execute(new DeleteAllFeedSubscriberByEntityTypeAndIdRequest(groupId,
                EntityType.GROUP));
        if (log.isDebugEnabled())
        {
            endTime = System.currentTimeMillis();
            log.debug("Time to remove group subscriptions: " + (endTime - startTime));

            log.debug("Removing group and associated objects for groupid:" + groupId);
            startTime = endTime;
        }
        // Remove the group itself and adjust parent org stats recursively
        DeleteGroupResponse deleteGroupResponse = deleteGroupDAO.execute(groupId);
        if (log.isDebugEnabled())
        {
            endTime = System.currentTimeMillis();
            log.debug("Time to remove group and associated objects: " + (endTime - startTime));
        }

        Long endDB = null;
        if (log.isInfoEnabled())
        {
            endDB = System.currentTimeMillis();
        }

        // ================= Cache update task generation =======================
       
	Long startAsync = null;
        if (log.isInfoEnabled())
        {
            startAsync = System.currentTimeMillis();
        }

        int startSize = 0;
        int endSize = 0;

        if (log.isDebugEnabled())
        {
            startSize = inActionContext.getUserActionRequests().size();
        }
        // purge fixed set of cache keys.
        generateSingleDeleteKeyFromCacheTask(getKeysToPurgeFromCache(deleteGroupResponse), inActionContext);

        if (log.isDebugEnabled())
        {
            endSize = inActionContext.getUserActionRequests().size();
            log.debug("Tasks for purge fixed set of cache keys: " + (endSize - startSize));
        }

        startSize = endSize;
        // create tasks (1/key) for removing group id from CacheKeys.GROUPS_FOLLOWED_BY_PERSON lists
        generateRemoveIdsFromListTasks(createKeys(CacheKeys.GROUPS_FOLLOWED_BY_PERSON, followerIds), Collections
                .singletonList(deleteGroupResponse.getGroupId()), inActionContext);

        if (log.isDebugEnabled())
        {
            endSize = inActionContext.getUserActionRequests().size();
            log.debug("Tasks for remove group id from GROUPS_FOLLOWED_BY_PERSON lists: " + (endSize - startSize));
        }

        startSize = endSize;
        // create task for removing activities in group's stream from users' starred activity lists.
        generateRemoveIdsFromStarredActivityListTasks(deleteActivityResponse, inActionContext);

        if (log.isDebugEnabled())
        {
            endSize = inActionContext.getUserActionRequests().size();
            log.debug("Tasks for remove groups activity ids from starred activity lists: " + (endSize - startSize));
        }

        // get list of activity ids to remove from cache lists, no need to go beyond maxCacheListSize
        List<Long> cachedActivityIds = deleteActivityResponse.getActivityIds().size() > maxCacheListSize // \n
        ? deleteActivityResponse.getActivityIds().subList(0, maxCacheListSize - 1)
                : deleteActivityResponse.getActivityIds();

        startSize = endSize;

        if (log.isDebugEnabled())
        {
            endSize = inActionContext.getUserActionRequests().size();
            log.debug("Tasks for remove activity ids from parent orgs of the deleted group: " + (endSize - startSize));
        }

        // create task for removing activities in group's stream from everyone stream
        generateRemoveIdsFromListTasks(Collections.singletonList(CacheKeys.EVERYONE_ACTIVITY_IDS), cachedActivityIds,
                inActionContext);

        startSize = endSize;
        // purge group from search index.
        generateDeleteFromSearchIndexTasks(DomainGroup.class, Collections.singletonList(deleteGroupResponse
                .getGroupId()), inActionContext);

        if (log.isDebugEnabled())
        {
            endSize = inActionContext.getUserActionRequests().size();
            log.debug("Tasks for purge group from search index: " + (endSize - startSize));
        }

        startSize = endSize;
        // purge ALL activities from search index.
        generateDeleteFromSearchIndexTasks(Activity.class, deleteActivityResponse.getActivityIds(), inActionContext);

        if (log.isDebugEnabled())
        {
            endSize = inActionContext.getUserActionRequests().size();
            log.debug("Tasks for purge ALL activities from search index: " + (endSize - startSize));
        }

        startSize = endSize;
        // remove ALL activities from cache (low priority should we even do this?)
        generateIndividualDeleteKeyFromCacheTasks(new HashSet<String>(createKeys(CacheKeys.ACTIVITY_BY_ID,
                deleteActivityResponse.getActivityIds())), inActionContext);

        if (log.isDebugEnabled())
        {
            endSize = inActionContext.getUserActionRequests().size();
            log.debug("Tasks for remove ALL activities from cache: " + (endSize - startSize));
        }

        startSize = endSize;
        // remove ALL comments from cache (low priority should we even do this?)
        generateIndividualDeleteKeyFromCacheTasks(new HashSet<String>(createKeys(CacheKeys.COMMENT_BY_ID,
                deleteActivityResponse.getCommentIds())), inActionContext);

        if (log.isDebugEnabled())
        {
            endSize = inActionContext.getUserActionRequests().size();
            log.debug("Tasks for remove ALL comments from cache: " + (endSize - startSize));
            log.debug("Total async tasks for delete: " + endSize);
        }

        if (log.isInfoEnabled())
        {
            long now = System.currentTimeMillis();
            String logMessage = "GroupId: " + groupId + " DB delete: " + (endDB - startDB) + " Async task generation: "
                    + (now - startAsync) + " Total time: " + (now - startDB);
            log.info(logMessage);
        }
        return Boolean.TRUE;
    }

    /**
     * Queues UserActionRequest for deleteActivitiesFromLists actions. Queues a task for each key passed in.
     * 
     * @param keys
     *            Cache keys to remove ids from
     * @param values
     *            The ids to remove from given lists.
     * @param inActionContext
     *            {@link TaskHandlerActionContext}.
     */
    private void generateRemoveIdsFromListTasks(final List<String> keys, final List<Long> values,
            final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        for (String key : keys)
        {
            // Put an action on the queue to delete the activities from the appropriate lists
            inActionContext.getUserActionRequests().add(
                    new UserActionRequest("deleteIdsFromLists", null, new DeleteIdsFromListsRequest(Collections
                            .singletonList(key), values)));
        }
    }

    /**
     * Queues UserActionRequest for deleteFromSearchIndex actions.
     * 
     * @param clazz
     *            Class of item to remove.
     * @param ids
     *            The ids to remove.
     * @param inActionContext
     *            {@link TaskHandlerActionContext}.
     */
    private void generateDeleteFromSearchIndexTasks(final Class< ? > clazz, final List<Long> ids,
            final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        // Put an action on the queue to delete the activities from search index
        inActionContext.getUserActionRequests()
                .add(
                        new UserActionRequest("deleteFromSearchIndexAction", null, new DeleteFromSearchIndexRequest(
                                clazz, ids)));
    }

    /**
     * Queues Single UserActionRequest for deleteKeysFromCache action.
     * 
     * @param keys
     *            keys to delete from cache.
     * @param inActionContext
     *            {@link TaskHandlerActionContext}.
     */
    private void generateSingleDeleteKeyFromCacheTask(final Set<String> keys,
            final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        inActionContext.getUserActionRequests().add(
                new UserActionRequest("deleteCacheKeysAction", null, (Serializable) keys));
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
            inActionContext.getUserActionRequests().add(
                    new UserActionRequest("deleteCacheKeysAction", null, new HashSet<String>(Collections
                            .singletonList(it.next()))));
        }
    }

    /**
     * Utility method for converting Map of personid/starred activity ids to tasks to update the users'
     * CacheKeys.STARRED_BY_PERSON_ID lists in cache.
     * 
     * @param inDeleteActivityResponse
     *            {@link BulkActivityDeleteResponse} containing Map.
     * @param inActionContext
     *            {@link TaskHandlerActionContext}.
     */
    private void generateRemoveIdsFromStarredActivityListTasks(
            final BulkActivityDeleteResponse inDeleteActivityResponse,
            final TaskHandlerActionContext<ActionContext> inActionContext)
    {
        Map<Long, Set<Long>> starMap = inDeleteActivityResponse.getPeopleWithStarredActivities();
        Set<Long> personIds = starMap.keySet();

        for (Long personId : personIds)
        {
            generateRemoveIdsFromListTasks(Collections.singletonList(CacheKeys.STARRED_BY_PERSON_ID + personId),
                    new ArrayList<Long>(starMap.get(personId)), inActionContext);
        }
    }

    /**
     * Generate keys to delete from cache as a result of deleting a group.
     * 
     * @param inRequest
     *            {@link DeleteGroupCacheUpdateRequest}.
     * @return Keys to delete from cache as a result of deleting a group.
     */
    private Set<String> getKeysToPurgeFromCache(final DeleteGroupResponse inRequest)
    {
        Set<String> keysToPurgeFromCache = new HashSet<String>();

        // remove group by id/shortname from cache.
        keysToPurgeFromCache.add(CacheKeys.GROUP_BY_SHORT_NAME + inRequest.getGroupShortName());
        keysToPurgeFromCache.add(CacheKeys.GROUP_BY_ID + inRequest.getGroupId());

        // remove follower person ids list for group
        keysToPurgeFromCache.add(CacheKeys.FOLLOWERS_BY_GROUP + inRequest.getGroupId());

        // remove coordinator ids list for group.
        keysToPurgeFromCache.add(CacheKeys.COORDINATOR_PERSON_IDS_BY_GROUP_ID + inRequest.getGroupId());

        // remove the group stream from cache
        keysToPurgeFromCache.add(CacheKeys.ENTITY_STREAM_BY_SCOPE_ID + inRequest.getStreamScopeId());

        // remove the group stream scope id by short name cache key
        keysToPurgeFromCache.add(CacheKeys.STREAM_SCOPE_ID_BY_GROUP_SHORT_NAME + inRequest.getGroupShortName());

        // remove popular hashtags cache entry for group.
        keysToPurgeFromCache.add(CacheKeys.POPULAR_HASH_TAGS_BY_STREAM_TYPE_AND_SHORT_NAME + ScopeType.GROUP + "-"
                + inRequest.getGroupShortName());

        return keysToPurgeFromCache;
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
    @SuppressWarnings("unchecked")
    private List<String> createKeys(final String keyRoot, final List inIds)
    {
        List keys = new ArrayList();

        for (int i = 0; i < inIds.size(); i++)
        {
            keys.add(keyRoot + inIds.get(i));
        }

        return keys;
    }
}
