/*
 * Copyright (c) 2010-2012 Lockheed Martin Corporation
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
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.action.request.SharedResourceRequest;
import org.eurekastreams.server.action.request.stream.SetSharedResourceLikeRequest;
import org.eurekastreams.server.domain.stream.SharedResource;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;
import org.eurekastreams.server.persistence.mappers.requests.SetSharedResourceLikeMapperRequest;

/**
 * Execution strategy to set the liked/unliked status of a shared resource.
 */
public class SetSharedResourceLikeExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /**
     * Logger.
     */
    private final Log log = LogFactory.make();

    /**
     * Mapper to update the like/unlike status of a shared resource.
     */
    private final DomainMapper<SetSharedResourceLikeMapperRequest, Boolean> setLikedResourceStatusMapper;

    /**
     * Mapper to get or insert shared resources.
     */
    private final DomainMapper<SharedResourceRequest, SharedResource> findOrInsertSharedResourceMapper;

    /**
     * Cache - used to immediately delete the shared resource cache key - it'll then be queued up to prevent the race
     * condition.
     */
    private final Cache cache;

    /** Transforms a shared resource's unique key to a cache key suffix. */
    private final Transformer<String, String> sharedResourceCacheKeySuffixTransformer;

    /**
     * @param inSetLikedResourceStatusMapper
     *            the mapper to use to update the person's liked status of the shared resource
     * @param inFindOrInsertSharedResourceMapper
     *            mapper to get or insert shared resources
     * @param inCache
     *            the cache to use to remove the shared resource
     * @param inSharedResourceCacheKeySuffixTransformer
     *            Transforms a shared resource's unique key to a cache key suffix.
     */
    public SetSharedResourceLikeExecution(
            final DomainMapper<SetSharedResourceLikeMapperRequest, Boolean> inSetLikedResourceStatusMapper,
            final DomainMapper<SharedResourceRequest, SharedResource> inFindOrInsertSharedResourceMapper,
            final Cache inCache, final Transformer<String, String> inSharedResourceCacheKeySuffixTransformer)
    {
        setLikedResourceStatusMapper = inSetLikedResourceStatusMapper;
        findOrInsertSharedResourceMapper = inFindOrInsertSharedResourceMapper;
        cache = inCache;
        sharedResourceCacheKeySuffixTransformer = inSharedResourceCacheKeySuffixTransformer;
    }

    /**
     * Set the liked/unlked status of a shared resource for a person.
     *
     * @param inActionContext
     *            the action context.
     * @return true
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
    {
        SetSharedResourceLikeRequest request = (SetSharedResourceLikeRequest) inActionContext.getActionContext()
                .getParams();
        if (request == null || request.getUniqueKey() == null)
        {
            return new Boolean(false);
        }

        final String sharedResourceUniqueKey = request.getUniqueKey().toLowerCase();
        final Long personId = inActionContext.getActionContext().getPrincipal().getId();

        // find the shared resource
        SharedResource sr = findOrInsertSharedResourceMapper.execute(new SharedResourceRequest(
                sharedResourceUniqueKey, null));

        SetSharedResourceLikeMapperRequest mapperRequest = new SetSharedResourceLikeMapperRequest(personId, sr,
                request.getLikes());

        setLikedResourceStatusMapper.execute(mapperRequest);

        // clean up the cache
        String cacheKey = CacheKeys.SHARED_RESOURCE_BY_UNIQUE_KEY
                + sharedResourceCacheKeySuffixTransformer.transform(sharedResourceUniqueKey);

        // delete the cache immediately
        log.debug("Immediately deleting cache key while in transaction '" + cacheKey
                + "', then queuing it up for post-transaction cleanup to avoid race.");
        cache.delete(cacheKey);

        // queue up a cache delete for after this transaction is closed - to prevent race condition
        inActionContext.getUserActionRequests().add(
                new UserActionRequest("deleteCacheKeysAction", null, (Serializable) Collections.singleton(cacheKey)));

        return new Boolean(true);
    }
}
