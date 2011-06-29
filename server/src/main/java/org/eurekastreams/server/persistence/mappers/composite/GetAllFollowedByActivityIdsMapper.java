/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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
package org.eurekastreams.server.persistence.mappers.composite;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.requests.GetAllFollowedByActivityIdsRequest;

/**
 * Composite mapper to get all activity ids for people/groups a user is following.
 * 
 */
public class GetAllFollowedByActivityIdsMapper implements
        DomainMapper<GetAllFollowedByActivityIdsRequest, List<List<Long>>>
{
    /**
     * Mapper to get activity ids for all people a user is following.
     */
    private DomainMapper<Long, List<Long>> getFollowedPeopleActivityIdsMapper;

    /**
     * Mapper to get activity ids for all groups a user is following.
     */
    private DomainMapper<List<Long>, List<List<Long>>> getFollowedGroupActivityIdsMapper;

    /**
     * Constructor.
     * 
     * @param inGetFollowedPeopleActivityIdsMapper
     *            Mapper to get activity ids for all people a user is following.
     * @param inGetFollowedGroupActivityIdsMapper
     *            Mapper to get activity ids for all groups a user is following.
     */
    public GetAllFollowedByActivityIdsMapper(final DomainMapper<Long, List<Long>> inGetFollowedPeopleActivityIdsMapper,
            final DomainMapper<List<Long>, List<List<Long>>> inGetFollowedGroupActivityIdsMapper)
    {
        getFollowedPeopleActivityIdsMapper = inGetFollowedPeopleActivityIdsMapper;
        getFollowedGroupActivityIdsMapper = inGetFollowedGroupActivityIdsMapper;
    }

    /**
     * get all activity ids for people/groups a user is following.
     * 
     * @param inRequest
     *            the GetAllFollowedByActivityIdsRequest request.
     * @return List of lists of activity ids.
     */
    @Override
    public List<List<Long>> execute(final GetAllFollowedByActivityIdsRequest inRequest)
    {
        List<List<Long>> results = new ArrayList<List<Long>>();

        results.add(getFollowedPeopleActivityIdsMapper.execute(inRequest.getUserId()));
        results.addAll(getFollowedGroupActivityIdsMapper.execute(inRequest.getGroupStreamIds()));

        return results;
    }
}
