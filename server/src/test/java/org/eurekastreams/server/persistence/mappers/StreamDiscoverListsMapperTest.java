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
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eurekastreams.server.domain.dto.FeaturedStreamDTO;
import org.eurekastreams.server.domain.dto.StreamDTO;
import org.eurekastreams.server.domain.dto.StreamDiscoverListsDTO;
import org.eurekastreams.server.domain.dto.SublistWithResultCount;
import org.eurekastreams.server.persistence.mappers.requests.MapperRequest;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.server.service.actions.strategies.RepopulateTempWeekdaysSinceDateStrategy;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;

/**
 * Test fixture for StreamDiscoverListsMapper.
 */
public class StreamDiscoverListsMapperTest
{
    /**
     * Context for mocking.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * Mapper to retrieve featured stream DTOs.
     */
    private DomainMapper<MapperRequest< ? >, List<FeaturedStreamDTO>> featuredStreamDTOMapper = context.mock(
            DomainMapper.class, "featuredStreamDTOMapper");

    /**
     * Mapper to retrieve the most active stream DTOs.
     */
    private DomainMapper<Serializable, SublistWithResultCount<StreamDTO>> mostActiveStreamsMapper = context.mock(
            DomainMapper.class, "mostActiveStreamsMapper");

    /**
     * Mapper to retrieve the most viewed stream DTOs.
     */
    private DomainMapper<Serializable, List<StreamDTO>> mostViewedStreamsMapper = context.mock(DomainMapper.class,
            "mostViewedStreamsMapper");

    /**
     * Mapper to retrieve the most followed stream DTOs.
     */
    private DomainMapper<Serializable, List<StreamDTO>> mostFollowedStreamsMapper = context.mock(DomainMapper.class,
            "mostFollowedStreamsMapper");

    /**
     * Mapper to retrieve the most recent stream DTOs.
     */
    private DomainMapper<Serializable, List<StreamDTO>> mostRecentStreamsMapper = context.mock(DomainMapper.class,
            "mostRecentStreamsMapper");

    /**
     * Strategy to repopulate the TempWeekdaysSinceDate table.
     */
    private final RepopulateTempWeekdaysSinceDateStrategy repopulateTempWeekdaysSinceDateStrategy = context
            .mock(RepopulateTempWeekdaysSinceDateStrategy.class);

    /**
     * Number of days to generate weekday count data for.
     */
    private final long numberOfDaysOfWeekdayCountDataToGenerate = 999;

    /**
     * System under test.
     */
    private StreamDiscoverListsMapper sut = new StreamDiscoverListsMapper(featuredStreamDTOMapper,
            mostActiveStreamsMapper, mostViewedStreamsMapper, mostFollowedStreamsMapper, mostRecentStreamsMapper,
            repopulateTempWeekdaysSinceDateStrategy, numberOfDaysOfWeekdayCountDataToGenerate);

    /**
     * Test execute.
     */
    @Test
    public void testExecute()
    {
        final FeaturedStreamDTO featuredDTO = new FeaturedStreamDTO();
        final StreamDTO mostActiveDTO = new PersonModelView();
        final Long mostActiveCount = 82L;
        final StreamDTO mostViewedDTO = new PersonModelView();
        final StreamDTO mostFollowedDTO = new PersonModelView();
        final StreamDTO mostRecentDTO = new PersonModelView();

        final List<FeaturedStreamDTO> featuredDTOs = new ArrayList<FeaturedStreamDTO>();
        featuredDTOs.add(featuredDTO);

        final SublistWithResultCount<StreamDTO> mostActiveDTOs = new SublistWithResultCount<StreamDTO>();
        mostActiveDTOs.setResultsSublist(new ArrayList<StreamDTO>());
        mostActiveDTOs.getResultsSublist().add(mostActiveDTO);
        mostActiveDTOs.setTotalResultsCount(mostActiveCount);

        final List<StreamDTO> mostViewedDTOs = new ArrayList<StreamDTO>();
        mostViewedDTOs.add(mostViewedDTO);

        final List<StreamDTO> mostFollowedDTOs = new ArrayList<StreamDTO>();
        mostFollowedDTOs.add(mostFollowedDTO);

        final List<StreamDTO> mostRecentDTOs = new ArrayList<StreamDTO>();
        mostRecentDTOs.add(mostRecentDTO);

        context.checking(new Expectations()
        {
            {
                oneOf(repopulateTempWeekdaysSinceDateStrategy).execute(numberOfDaysOfWeekdayCountDataToGenerate);

                oneOf(featuredStreamDTOMapper).execute(null);
                will(returnValue(featuredDTOs));

                oneOf(mostActiveStreamsMapper).execute(null);
                will(returnValue(mostActiveDTOs));

                oneOf(mostViewedStreamsMapper).execute(null);
                will(returnValue(mostViewedDTOs));

                oneOf(mostFollowedStreamsMapper).execute(null);
                will(returnValue(mostFollowedDTOs));

                oneOf(mostRecentStreamsMapper).execute(null);
                will(returnValue(mostRecentDTOs));
            }
        });

        StreamDiscoverListsDTO result = sut.execute(null);

        Assert.assertSame(featuredDTO, result.getFeaturedStreams().get(0));
        Assert.assertSame(mostActiveDTO, result.getMostActiveStreams().getResultsSublist().get(0));
        Assert.assertEquals(mostActiveCount, result.getMostActiveStreams().getTotalResultsCount());
        Assert.assertSame(mostViewedDTO, result.getMostViewedStreams().get(0));
        Assert.assertSame(mostFollowedDTO, result.getMostFollowedStreams().get(0));
        Assert.assertSame(mostRecentDTO, result.getMostRecentStreams().get(0));

        context.assertIsSatisfied();
    }

}
