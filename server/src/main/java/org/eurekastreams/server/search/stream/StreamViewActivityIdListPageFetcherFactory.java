/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
package org.eurekastreams.server.search.stream;

import org.eurekastreams.server.persistence.mappers.stream.CompositeStreamActivityIdsMapper;

/**
 * Factory to get a PageFetcher&lt;Long&gt; of activity ids from a stream view.
 */
public class StreamViewActivityIdListPageFetcherFactory
{
    /**
     * Composite Stream Activity Mapper.
     */
    private CompositeStreamActivityIdsMapper compositeStreamActivityIdsMapper;

    /**
     * Build a PageFetcher that returns all of the ActivityIds in a StreamView.
     * 
     * @param inStreamViewId
     *            the StreamView id to fetch the activity ids for
     * @param currentUserId
     *            the ID of the person making the request
     * @return a PageFetcher that fetches the ActivityIds of a stream view
     */
    public PageFetcher<Long> buildPageFetcher(final long inStreamViewId, final Long currentUserId)
    {
        return new ListWrappingPageFetcher(compositeStreamActivityIdsMapper.execute(inStreamViewId, currentUserId));
    }

    /**
     * Set the compositeStreamActivityIdsMapper.
     * 
     * @param inCompositeStreamActivityIdsMapper
     *            the compositeStreamActivityIdsMapper to set
     */
    public void setCompositeStreamActivityIdsMapper(
            final CompositeStreamActivityIdsMapper inCompositeStreamActivityIdsMapper)
    {
        compositeStreamActivityIdsMapper = inCompositeStreamActivityIdsMapper;
    }
}
