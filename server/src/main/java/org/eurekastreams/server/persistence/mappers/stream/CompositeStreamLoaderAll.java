/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.stream;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.cache.CacheKeys;

/**
 * CompositeStreamLoader implementation for "Everyone" CompositeStream type.
 * 
 */
public class CompositeStreamLoaderAll extends BaseCompositeStreamLoader
{

    /**
     * Returns restrictions hashtable to be used in returning activityId list from datastore.
     * 
     * @param inCompositeStream
     *            the CompositeStream.
     * @param inUserId
     *            the user.
     * 
     * @return restrictions hashtable to be used in returning activityId list from datastore.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected Hashtable<RestrictionType, HashSet> getActivityRestrictions(final StreamView inCompositeStream,
            final long inUserId)
    {
        // no restrictions, this is for all activities.
        return null;
    }

    /**
     * Get list of activity ids for given compositeStream and user from cache, if present, or null if not.
     * 
     * @param inCompositeStream
     *            The CompositeStream.
     * @param inUserId
     *            The user id.
     * @return List of activity ids for given compositeStream and user from cache, if present, or null if not.
     */
    @SuppressWarnings("unchecked")
    @Override
    protected List<Long> getIdListFromCache(final StreamView inCompositeStream, final long inUserId)
    {
        return getCache().getList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + inCompositeStream.getId());
    }

    /**
     * Sets the list of activity ids to cache for given CompositeStream and user.
     * 
     * @param inActivityIds
     *            The list of activity ids.
     * @param inCompositeStream
     *            The CompositeStream.
     * @param inUserId
     *            The user id.
     */
    @Override
    protected void setIdListToCache(final List<Long> inActivityIds, final StreamView inCompositeStream,
            final long inUserId)
    {
        getCache().setList(CacheKeys.ACTIVITIES_BY_COMPOSITE_STREAM + inCompositeStream.getId(), inActivityIds);
    }

}
