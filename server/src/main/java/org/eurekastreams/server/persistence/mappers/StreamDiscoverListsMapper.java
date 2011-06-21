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
package org.eurekastreams.server.persistence.mappers;

import java.io.Serializable;
import java.util.List;

import org.eurekastreams.server.domain.dto.FeaturedStreamDTO;
import org.eurekastreams.server.domain.dto.StreamDTO;
import org.eurekastreams.server.domain.dto.SublistWithResultCount;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;

/**
 * Mapper to piece together the results of different mappers to feed the streams discovery page.
 */
public class StreamDiscoverListsMapper
{
    /**
     * Mapper to retrieve featured stream DTOs.
     */
    private DomainMapper<MapperRequest, List<FeaturedStreamDTO>> featuredStreamDTOMapper;

    /**
     * Mapper to retrieve the most active stream DTOs.
     */
    private DomainMapper<Serializable, SublistWithResultCount<Long>> mostActiveStreamsMapper;

    /**
     * Mapper to retrieve the most viewed stream DTOs.
     */
    private DomainMapper<Serializable, List<StreamDTO>> mostViewedStreamsMapper;

    /**
     * Mapper to retrieve the most followed stream DTOs.
     */
    private DomainMapper<Serializable, List<StreamDTO>> mostFollowedStreamsMapper;

    /**
     * Mapper to retrieve the most recent stream DTOs.
     */
    private DomainMapper<Serializable, List<StreamDTO>> mostRecentStreamsMapper;

    /**
     * Temporary method to avoid checkstyle complaint.
     */
    public void doNothing()
    {
        return;
    }

}
