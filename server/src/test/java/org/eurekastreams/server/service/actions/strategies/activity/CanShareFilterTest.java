/*
 * Copyright (c) 2009-2011 Lockheed Martin Corporation
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

package org.eurekastreams.server.service.actions.strategies.activity;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByShortNames;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test share Filter.
 */
public class CanShareFilterTest
{
    /**
     * Mocking context.
     */
    private final JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * List of activities.
     */
    private List<ActivityDTO> activities;

    /**
     * System under test.
     */
    private CanShareFilter sut;

    /**
     * GroupMapper Mock.
     */
    private GetDomainGroupsByShortNames gMapperMock = context.mock(GetDomainGroupsByShortNames.class);

    /**
     * GroupMock.
     */
    private DomainGroupModelView groupMock = context.mock(DomainGroupModelView.class);

    /**
     * ActivityDTO mock.
     */
    private ActivityDTO activityMock = context.mock(ActivityDTO.class);

    /**
     * StreamEntityDTO mock.
     */
    private StreamEntityDTO destinationStream = context.mock(StreamEntityDTO.class);

    /**
     * Unique id for destinationStream.
     */
    private String destinationStreamUniqueId = "destinationstreamuniqueid";

    /**
     * account for user.
     */
    private PersonModelView personAccount = new PersonModelView();

    /**
     * Setup test fixtures.
     */
    @Before
    public void setUp()
    {
        activities = new ArrayList<ActivityDTO>(1);
        activities.add(activityMock);

        sut = new CanShareFilter(gMapperMock);
    }

    /**
     * Test filter method.
     */
    @Test
    public void testFilterWithPersonStream()
    {

        context.checking(new Expectations()
        {
            {
                allowing(activityMock).getDestinationStream();
                will(returnValue(destinationStream));

                allowing(destinationStream).getType();
                will(returnValue(EntityType.PERSON));

                allowing(activityMock).setShareable(true);
            }
        });

        sut.filter(activities, personAccount);
        context.assertIsSatisfied();
    }

    /**
     * Test filter method.
     */
    @Test
    public void testFilterWithGroupStreamShareable()
    {
        final List<String> streamUniqueIds = new ArrayList<String>(1);
        streamUniqueIds.add(destinationStreamUniqueId);

        final List<DomainGroupModelView> domainGroupModelViews = new ArrayList<DomainGroupModelView>(1);
        domainGroupModelViews.add(groupMock);

        context.checking(new Expectations()
        {
            {
                allowing(activityMock).getDestinationStream();
                will(returnValue(destinationStream));

                allowing(destinationStream).getType();
                will(returnValue(EntityType.GROUP));

                allowing(destinationStream).getUniqueIdentifier();
                will(returnValue(destinationStreamUniqueId));

                oneOf(gMapperMock).execute(streamUniqueIds);
                will(returnValue(domainGroupModelViews));

                allowing(groupMock).getShortName();
                will(returnValue(destinationStreamUniqueId));

                allowing(groupMock).isPublic();
                will(returnValue(true));

                allowing(activityMock).setShareable(true);
            }
        });

        sut.filter(activities, personAccount);
        context.assertIsSatisfied();
    }

    /**
     * Test filter method.
     */
    @Test
    public void testFilterWithGroupStreamNotShareable()
    {
        final List<String> streamUniqueIds = new ArrayList<String>(1);
        streamUniqueIds.add(destinationStreamUniqueId);

        final List<DomainGroupModelView> domainGroupModelViews = new ArrayList<DomainGroupModelView>(1);
        domainGroupModelViews.add(groupMock);

        context.checking(new Expectations()
        {
            {
                allowing(activityMock).getDestinationStream();
                will(returnValue(destinationStream));

                allowing(destinationStream).getType();
                will(returnValue(EntityType.GROUP));

                allowing(destinationStream).getUniqueIdentifier();
                will(returnValue(destinationStreamUniqueId));

                oneOf(gMapperMock).execute(streamUniqueIds);
                will(returnValue(domainGroupModelViews));

                allowing(groupMock).getShortName();
                will(returnValue(destinationStreamUniqueId));

                allowing(groupMock).isPublic();
                will(returnValue(false));

                allowing(activityMock).setShareable(false);
            }
        });

        sut.filter(activities, personAccount);
        context.assertIsSatisfied();
    }

    /**
     * Test filter method.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testFilterWithBadType()
    {
        final List<String> streamUniqueIds = new ArrayList<String>(1);
        streamUniqueIds.add(destinationStreamUniqueId);

        final List<DomainGroupModelView> domainGroupModelViews = new ArrayList<DomainGroupModelView>(1);
        domainGroupModelViews.add(groupMock);

        context.checking(new Expectations()
        {
            {
                allowing(activityMock).getDestinationStream();
                will(returnValue(destinationStream));

                allowing(destinationStream).getType();
                will(returnValue(EntityType.APPLICATION));
            }
        });

        sut.filter(activities, personAccount);
        context.assertIsSatisfied();
    }
}
