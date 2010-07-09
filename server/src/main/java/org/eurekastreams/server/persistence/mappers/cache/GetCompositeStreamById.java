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
package org.eurekastreams.server.persistence.mappers.cache;

import org.eurekastreams.server.domain.stream.StreamView;
import org.eurekastreams.server.persistence.mappers.stream.CachedDomainMapper;

/**
 * Simple mapper for retrieving a StreamView from cache by id.
 *
 */
public class GetCompositeStreamById extends CachedDomainMapper
{
    /**
     * Retrieve a streamview by id from cache.
     * @param inStreamViewId - id of the stream view to retrieve.
     * @return - cached instance of the streamview object or null if not present.
     */
    public StreamView execute(final Long inStreamViewId)
    {
        return (StreamView) getCache().get(CacheKeys.COMPOSITE_STREAM_BY_ID + inStreamViewId);
    }
}
