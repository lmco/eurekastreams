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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.eurekastreams.commons.actions.ExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.action.request.stream.SetStreamOrderRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.stream.ReorderFollowedGroupIds;

/**
 * This execution strategy reorders the list followed groups for the current user and passes the reordered list to a
 * mapper that will persist the new order in the database. Additionally, it will update the hiddenlineindex for the
 * group list on the Person entity if necessary.
 */
public class SetGroupStreamsOrderExecution implements ExecutionStrategy<PrincipalActionContext>
{
    /**
     * Mapper to get the list of group ids that the user follows.
     */
    private DomainMapper<Long, List<Long>> groupIdsMapper;

    /**
     * Mapper that persists the reordered list to the db.
     */
    private ReorderFollowedGroupIds reorderMapper;

    /**
     * EntityManager to use for all ORM operations.
     */
    private EntityManager entityManager;

    /**
     * Cache instance to use for clearing the updated person's cache entry.
     */
    private Cache cache;

    /**
     * Set the entity manager to use for all ORM operations.
     * 
     * @param inEntityManager
     *            the EntityManager to use for all ORM operations.
     */
    @PersistenceContext
    public void setEntityManager(final EntityManager inEntityManager)
    {
        this.entityManager = inEntityManager;
    }

    /**
     * Constructor.
     * 
     * @param inGroupIdsMapper
     *            the group ids mapper to set.
     * @param inReorderMapper
     *            the reorder mapper to set.
     * @param inCache
     *            the cache.
     */
    public SetGroupStreamsOrderExecution(final DomainMapper<Long, List<Long>> inGroupIdsMapper,
            final ReorderFollowedGroupIds inReorderMapper, final Cache inCache)
    {
        groupIdsMapper = inGroupIdsMapper;
        reorderMapper = inReorderMapper;
        cache = inCache;
    }

    /**
     * {@inheritDoc} Using the injected mappers, this method performs the group stream list reordering for the current
     * uset.
     */
    @Override
    public Serializable execute(final PrincipalActionContext inActionContext) throws ExecutionException
    {
        SetStreamOrderRequest request = (SetStreamOrderRequest) inActionContext.getParams();
        final long userEntityId = inActionContext.getPrincipal().getId();

        List<Long> followedGroupIds = groupIdsMapper.execute(userEntityId);

        // Find the item to be moved
        int oldIndex = -1;

        for (int i = 0; i < followedGroupIds.size(); i++)
        {
            if (followedGroupIds.get(i).longValue() == request.getStreamId().longValue())
            {
                oldIndex = i;
            }
        }

        // Reorder the list
        followedGroupIds.remove(oldIndex);
        followedGroupIds.add(request.getNewIndex(), request.getStreamId());

        reorderMapper.execute(userEntityId, followedGroupIds);

        // TODO - this db code should be in a db mapper, not here in this action execution strategy
        // Update hidden line index
        String queryString = "update versioned Person set groupStreamHiddenLineIndex = :newIndex where id = :id";
        Query q = entityManager.createQuery(queryString).setParameter("newIndex",
                request.getHiddenLineIndex().intValue()).setParameter("id", userEntityId);
        q.executeUpdate();

        // Deletes the person from cache; will be added to cache the next time it is requested
        // Normally this is done by onPostUpdate in the PersonCacheLoader but the update query doesn't use the
        // person entity directly, which saves db calls.
        cache.delete(CacheKeys.PERSON_BY_ID + userEntityId);

        return Boolean.TRUE;
    }
}
