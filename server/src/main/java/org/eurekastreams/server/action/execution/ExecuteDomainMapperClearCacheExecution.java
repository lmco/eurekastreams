/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
import java.util.Collections;

import org.eurekastreams.commons.actions.TaskHandlerExecutionStrategy;
import org.eurekastreams.commons.actions.context.ActionContext;
import org.eurekastreams.commons.actions.context.PrincipalActionContext;
import org.eurekastreams.commons.actions.context.TaskHandlerActionContext;
import org.eurekastreams.commons.server.UserActionRequest;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.Transformer;

/**
 * Executes a configured {@link DomainMapper} with provided params then deletes from cache.
 */
public class ExecuteDomainMapperClearCacheExecution implements TaskHandlerExecutionStrategy<PrincipalActionContext>
{
    /** Strategy to supply mapper parameters from the action context. */
    private final Transformer<ActionContext, Serializable> parameterSupplier;

    /** {@link DomainMapper}. */
    private final DomainMapper<Serializable, Serializable> domainMapper;

    /** The cache. */
    private final Cache cache;

    /** Prefix for building the cache key. */
    private final String cacheKeyPrefix;

    /** Strategy to supply the cache key suffix from the action context. */
    private final Transformer<ActionContext, Serializable> cacheKeyParameterSupplier;

    /**
     * Constructor.
     *
     * @param inParameterSupplier
     *            Strategy to supply mapper parameters from the action context.
     * @param inDomainMapper
     *            {@link DomainMapper}.
     * @param inCache
     *            Cache.
     * @param inCacheKeyPrefix
     *            Prefix for building the cache key.
     * @param inCacheKeyParameterSupplier
     *            Strategy to supply the cache key suffix from the action context.
     */
    public ExecuteDomainMapperClearCacheExecution(final Transformer<ActionContext, Serializable> inParameterSupplier,
            final DomainMapper<Serializable, Serializable> inDomainMapper, final Cache inCache,
            final String inCacheKeyPrefix, final Transformer<ActionContext, Serializable> inCacheKeyParameterSupplier)
    {
        parameterSupplier = inParameterSupplier;
        domainMapper = inDomainMapper;
        cache = inCache;
        cacheKeyPrefix = inCacheKeyPrefix;
        cacheKeyParameterSupplier = inCacheKeyParameterSupplier;
    }

    /**
     * Executes a configured {@link DomainMapper} with provided params then deletes from cache.
     *
     * @param inActionContext
     *            ActionContext.
     * @return {@link DomainMapper} results.
     */
    @Override
    public Serializable execute(final TaskHandlerActionContext<PrincipalActionContext> inActionContext)
    {
        PrincipalActionContext realContext = inActionContext.getActionContext();

        Serializable param = parameterSupplier.transform(realContext);
        Serializable result = domainMapper.execute(param);

        // clear cache for the updated item
        String key = cacheKeyPrefix + cacheKeyParameterSupplier.transform(realContext);
        cache.delete(key);
        inActionContext.getUserActionRequests().add(
                new UserActionRequest("deleteCacheKeysAction", null, (Serializable) Collections.singletonList(key)));

        return result;
    }
}
