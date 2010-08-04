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
package org.eurekastreams.server.persistence.mappers.db;

import java.util.List;

import org.eurekastreams.server.persistence.mappers.BaseArgDomainMapper;
import org.eurekastreams.server.persistence.mappers.cache.FollowedActivityIdsLoader;

/**
 * Get the followed by activities for a person.
 *
 */
public class GetFollowedByActivitiesDbMapper extends BaseArgDomainMapper<Long, List<Long>>
{
    /**
     * Followed loader.
     */
    private FollowedActivityIdsLoader followedLoader;

    /**
     * The max request.
     */
    private int maxRequest;

    /**
     * Default constructor.
     * @param inFollowedLoader the loader.
     * @param inMaxRequest the max request.
     */
    public GetFollowedByActivitiesDbMapper(final FollowedActivityIdsLoader inFollowedLoader,
            final int inMaxRequest)
    {
        followedLoader = inFollowedLoader;
        maxRequest = inMaxRequest;
    }

    /**
     * Execute.
     * @param inRequest the person id.
     * @return the list of IDs.
     */
    @Override
    public List<Long> execute(final Long inRequest)
    {
        return followedLoader.getFollowedActivityIds(inRequest, maxRequest);
    }

}
