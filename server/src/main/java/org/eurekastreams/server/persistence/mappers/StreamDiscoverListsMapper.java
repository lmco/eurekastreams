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

import org.apache.commons.logging.Log;
import org.eurekastreams.commons.logging.LogFactory;
import org.eurekastreams.server.domain.dto.FeaturedStreamDTO;
import org.eurekastreams.server.domain.dto.StreamDTO;
import org.eurekastreams.server.domain.dto.StreamDiscoverListsDTO;
import org.eurekastreams.server.domain.dto.SublistWithResultCount;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.eurekastreams.server.service.actions.strategies.RepopulateTempWeekdaysSinceDateStrategy;

/**
 * Mapper to piece together the results of different mappers to feed the streams discovery page.
 */
public class StreamDiscoverListsMapper extends BaseArgDomainMapper<Serializable, StreamDiscoverListsDTO>
{
    /**
     * Logger.
     */
    private Log log = LogFactory.make();

    /**
     * Mapper to retrieve featured stream DTOs.
     */
    private DomainMapper<MapperRequest< ? >, List<FeaturedStreamDTO>> featuredStreamDTOMapper;

    /**
     * Mapper to retrieve the most active stream DTOs.
     */
    private DomainMapper<Serializable, SublistWithResultCount<StreamDTO>> mostActiveStreamsMapper;

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
     * Strategy to repopulate the TempWeekdaysSinceDate table.
     */
    private final RepopulateTempWeekdaysSinceDateStrategy repopulateTempWeekdaysSinceDateStrategy;

    /**
     * Number of days to generate weekday count data for.
     */
    private final long numberOfDaysOfWeekdayCountDataToGenerate;

    /**
     * Constructor.
     * 
     * @param inFeaturedStreamDTOMapper
     *            mapper to retrieve featured stream DTOs.
     * @param inMostActiveStreamsMapper
     *            mapper to retrieve the most active stream DTOs.
     * @param inMostViewedStreamsMapper
     *            mapper to retrieve the most viewed stream DTOs.
     * @param inMostFollowedStreamsMapper
     *            mapper to retrieve the most followed stream DTOs.
     * @param inMostRecentStreamsMapper
     *            mapper to retrieve the most recent stream DTOs.
     * @param inRepopulateTempWeekdaysSinceDateStrategy
     *            strategy to repopulate the TempWeekdaysSinceDate table
     * @param inNumberOfDaysOfWeekdayCountDataToGenerate
     *            number of days to generate weekday count data for.
     */
    public StreamDiscoverListsMapper(
            final DomainMapper<MapperRequest< ? >, List<FeaturedStreamDTO>> inFeaturedStreamDTOMapper,
            final DomainMapper<Serializable, SublistWithResultCount<StreamDTO>> inMostActiveStreamsMapper,
            final DomainMapper<Serializable, List<StreamDTO>> inMostViewedStreamsMapper,
            final DomainMapper<Serializable, List<StreamDTO>> inMostFollowedStreamsMapper,
            final DomainMapper<Serializable, List<StreamDTO>> inMostRecentStreamsMapper,
            final RepopulateTempWeekdaysSinceDateStrategy inRepopulateTempWeekdaysSinceDateStrategy,
            final long inNumberOfDaysOfWeekdayCountDataToGenerate)
    {
        featuredStreamDTOMapper = inFeaturedStreamDTOMapper;
        mostActiveStreamsMapper = inMostActiveStreamsMapper;
        mostViewedStreamsMapper = inMostViewedStreamsMapper;
        mostFollowedStreamsMapper = inMostFollowedStreamsMapper;
        mostRecentStreamsMapper = inMostRecentStreamsMapper;
        repopulateTempWeekdaysSinceDateStrategy = inRepopulateTempWeekdaysSinceDateStrategy;
        numberOfDaysOfWeekdayCountDataToGenerate = inNumberOfDaysOfWeekdayCountDataToGenerate;
    }

    /**
     * Build a base StreamDiscoverListsDTO that applies to all of Eureka, nothing specific for a specific user.
     * 
     * @param inRequest
     *            (ignored)
     * @return a base StreamDiscoverListsDTO that applies to all of Eureka, nothing specific for a specific user.
     */
    @Override
    public StreamDiscoverListsDTO execute(final Serializable inRequest)
    {
        log.info("Beginning to generate Stream Discovery lists for all users.");

        log.info("Regenerating weekday count temp data for " + numberOfDaysOfWeekdayCountDataToGenerate + " days");
        repopulateTempWeekdaysSinceDateStrategy.execute(numberOfDaysOfWeekdayCountDataToGenerate);

        StreamDiscoverListsDTO result = new StreamDiscoverListsDTO();

        log.info("Generating the list of featured streams");
        result.setFeaturedStreams(featuredStreamDTOMapper.execute(null));

        log.info("Generating the list of most active streams");
        result.setMostActiveStreams(mostActiveStreamsMapper.execute(null));

        log.info("Generating the list of most viewed streams.");
        result.setMostViewedStreams(mostViewedStreamsMapper.execute(null));

        log.info("Generating the list of most followed streams.");
        result.setMostFollowedStreams(mostFollowedStreamsMapper.execute(null));

        log.info("Generating the list of most recent streams.");
        result.setMostRecentStreams(mostRecentStreamsMapper.execute(null));

        log.info("Finished generating Stream Discovery lists for all users.");
        return result;
    }
}
