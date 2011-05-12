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
package org.eurekastreams.server.action.execution.notification.translator;

import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the flagged activty event translator.
 */
// TODO Make this a an integration test.
public class FlagTranslatorTest
{
    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: mapper. */
    private DomainMapper<List<Long>, List<ActivityDTO>> activitiesMapper = context.mock(DomainMapper.class);

    /** Fixture: mapper. */
    private DomainMapper<Serializable, List<Long>> systemAdminIdsMapper = context.mock(DomainMapper.class,
            "systemAdminIdsMapper");

    /** SUT. */
    private FlagTranslator sut;

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new FlagTranslator(activitiesMapper, systemAdminIdsMapper);
    }

    /**
     * Tests translating.
     */
    @Test
    public void testTranslate()
    {
        StreamEntityDTO stream = new StreamEntityDTO();
        stream.setType(EntityType.PERSON);
        stream.setDestinationEntityId(4L);
        final ActivityDTO activity = new ActivityDTO();
        activity.setRecipientParentOrgId(2L);
        activity.setDestinationStream(stream);

        final List<Long> admins = new ArrayList<Long>();
        admins.add(5L);
        context.checking(new Expectations()
        {
            {
                allowing(activitiesMapper).execute(Arrays.asList(3L));
                will(returnValue(Arrays.asList(activity)));
                allowing(systemAdminIdsMapper).execute(null);
                will(returnValue(admins));
            }
        });

        Collection<NotificationDTO> results = sut.translate(1L, 0L, 3L);
        context.assertIsSatisfied();

        assertEquals(1, results.size());
        NotificationDTO notif = results.iterator().next();
        assertEquals((Long) 5L, notif.getRecipientIds().get(0));
    }
}
