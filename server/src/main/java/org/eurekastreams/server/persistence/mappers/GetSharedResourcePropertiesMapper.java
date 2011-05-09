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
package org.eurekastreams.server.persistence.mappers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.action.request.SharedResourceRequest;
import org.eurekastreams.server.domain.stream.StreamScope;
import org.eurekastreams.server.search.modelview.SharedResourceDTO;

/**
 * Mapper to get the properties of a shared resource, including shared and liked counts and 4 people from each list.
 * This combines a few mappers.
 */
public class GetSharedResourcePropertiesMapper extends BaseArgDomainMapper<SharedResourceRequest, SharedResourceDTO>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Mapper to get a stream scope by scope type and unique key.
     */
    private DomainMapper<String, StreamScope> getResourceStreamScopeByKeyMapper;

    /**
     * Mapper that gets the ids of people that liked a shared resource.
     */
    private DomainMapper<SharedResourceRequest, List<Long>> getPeopleThatSharedResourceMapper;

    /**
     * Mapper that gets the ids of people that shared a shared resource.
     */
    private DomainMapper<SharedResourceRequest, List<Long>> getPeopleThatLikedResourceMapper;

    /**
     * Constructor.
     * 
     * @param inGetResourceStreamScopeByKeyMapper
     *            Mapper to get a stream scope by scope type and unique key.
     * @param inGetPeopleThatSharedResourceMapper
     *            Mapper that gets the ids of people that liked a shared resource.
     * @param inGetPeopleThatLikedResourceMapper
     *            Mapper that gets the ids of people that shared a shared resource.
     */
    public GetSharedResourcePropertiesMapper(
            final DomainMapper<String, StreamScope> inGetResourceStreamScopeByKeyMapper,
            final DomainMapper<SharedResourceRequest, List<Long>> inGetPeopleThatSharedResourceMapper,
            final DomainMapper<SharedResourceRequest, List<Long>> inGetPeopleThatLikedResourceMapper)
    {
        getResourceStreamScopeByKeyMapper = inGetResourceStreamScopeByKeyMapper;
        getPeopleThatSharedResourceMapper = inGetPeopleThatSharedResourceMapper;
        getPeopleThatLikedResourceMapper = inGetPeopleThatLikedResourceMapper;
    }

    /**
     * Return the SharedResourceDTO from the input request.
     * 
     * @param inRequest
     *            the request
     * @return the shared resource dto
     */
    @Override
    public SharedResourceDTO execute(final SharedResourceRequest inRequest)
    {
        SharedResourceDTO dto = new SharedResourceDTO();
        dto.setKey(inRequest.getUniqueKey());

        log.info("Looking for the stream scope for shared resource with uniqueKey " + inRequest.getUniqueKey());

        // either null or a stream scope id
        StreamScope sharedResourceStreamScope = getResourceStreamScopeByKeyMapper.execute(inRequest.getUniqueKey());

        // if the stream scope doesn't exist, then this resource doesn't either
        if (sharedResourceStreamScope == null)
        {
            // not found - if the shared resource existed, it would have a stream scope, so we can stop looking through
            // the other tables now
            log.info("Couldn't find the stream scope for shared resource with unique key " + inRequest.getUniqueKey()
                    + " - must not exist.  Cache as such.");

            dto.setStreamScopeId(null);
            dto.setIsLiked(false);
            return dto;
        }
        dto.setStreamScopeId(sharedResourceStreamScope.getId());

        // since we know the destination SharedResource id, we can get the likers and sharers much quicker
        inRequest.setSharedResourceId(sharedResourceStreamScope.getDestinationEntityId());

        log.info("Found the shared resource for unique key " + inRequest.getUniqueKey()
                + " - looking for lists of people ids that liked and shared it.");

        List<Long> sharedPersonIds = getPeopleThatSharedResourceMapper.execute(inRequest);
        List<Long> likedPersonIds = getPeopleThatLikedResourceMapper.execute(inRequest);

        if (sharedPersonIds == null)
        {
            sharedPersonIds = new ArrayList<Long>();
        }
        dto.setLikerPersonIds(likedPersonIds);

        if (likedPersonIds == null)
        {
            likedPersonIds = new ArrayList<Long>();
        }
        dto.setSharerPersonIds(sharedPersonIds);
        return dto;
    }
}
