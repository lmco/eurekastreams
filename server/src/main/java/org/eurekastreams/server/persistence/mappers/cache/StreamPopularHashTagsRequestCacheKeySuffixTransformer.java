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

import org.eurekastreams.server.persistence.mappers.requests.StreamPopularHashTagsRequest;

/**
 * Convert a StreamPopularHashTagsRequest to a cache key suffix.
 */
public class StreamPopularHashTagsRequestCacheKeySuffixTransformer implements
        CacheKeySuffixTransformer<StreamPopularHashTagsRequest>
{
    /**
     * Convert the input StreamPopularHashTagsRequest request to a cache key suffix.
     *
     * @param inRequest
     *            the StreamPopularHashTagsRequest to convert
     * @return the cache key for a stream popular hashtag - the scope type and unique key
     */
    @Override
    public String transform(final StreamPopularHashTagsRequest inRequest)
    {
        return inRequest.getStreamEntityScopeType() + "-" + inRequest.getStreamEntityUniqueKey();
    }
}
