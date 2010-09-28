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
package org.eurekastreams.server.action.execution.notification;

import static junit.framework.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.NotificationDTO;
import org.eurekastreams.server.domain.NotificationType;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.BaseObjectType;
import org.eurekastreams.server.persistence.mappers.DomainMapper;
import org.eurekastreams.server.persistence.mappers.stream.GetDomainGroupsByIds;
import org.eurekastreams.server.persistence.mappers.stream.GetOrganizationsByIds;
import org.eurekastreams.server.persistence.mappers.stream.GetPeopleByIds;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.server.search.modelview.OrganizationModelView;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;



/**
 * Tests NotificationPopulator.
 */
public class NotificationPopulatorTest
{
    /** Test data. */
    private static final long ACTOR_ID = 2222L;

    /** Test data. */
    private static final String PERSON_NAME = "John Doe";

    /** Test data. */
    private static final String PERSON_ACCOUNT_ID = "jdoe";

    /** Test data. */
    private static final long DESTINATION_ID = 2332L;



    /** Used for mocking objects. */
    private JUnit4Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /** Fixture: For getting person info. */
    private GetPeopleByIds personMapper = context.mock(GetPeopleByIds.class);

    /** Fixture: For getting group info. */
    private GetDomainGroupsByIds groupMapper = context.mock(GetDomainGroupsByIds.class);

    /** For getting org details. */
    private GetOrganizationsByIds orgMapper = context.mock(GetOrganizationsByIds.class);

    /** Fixture: For getting activity info. */
    private DomainMapper<List<Long>, List<ActivityDTO>>  activityMapper = context.mock(DomainMapper.class);

    /** Fixture: person. */
    private PersonModelView person = new PersonModelView();


    /** Fixture: notification. */
    private NotificationDTO notification;


    /** SUT. */
    private NotificationPopulator sut;

    /**
     * Constructor.
     */
    public NotificationPopulatorTest()
    {
        person.setAccountId(PERSON_ACCOUNT_ID);
        person.setDisplayName(PERSON_NAME);
    }

    /**
     * Setup before each test.
     */
    @Before
    public void setUp()
    {
        sut = new NotificationPopulator(personMapper, groupMapper, orgMapper, activityMapper);
        notification = new NotificationDTO(Collections.EMPTY_LIST, NotificationType.COMMENT_TO_COMMENTED_POST, 0L);
    }

    /**
     * Tests populating actor.
     */
    @Test
    public void testPopulateActor()
    {
        notification.setActorId(ACTOR_ID);

        context.checking(new Expectations()
        {
            {
                allowing(personMapper).execute(ACTOR_ID);
                will(returnValue(person));
            }
        });

        sut.populate(notification);

        context.assertIsSatisfied();
        assertEquals(PERSON_ACCOUNT_ID, notification.getActorAccountId());
        assertEquals(PERSON_NAME, notification.getActorName());
    }

    /**
     * Tests populating activity.
     */
    @Test
    public void testPopulateActivity()
    {
        notification.setActivityId(9L);

        final ActivityDTO activity = new ActivityDTO();
        activity.setBaseObjectType(BaseObjectType.NOTE);

        context.checking(new Expectations()
        {
            {
                allowing(activityMapper).execute(Arrays.asList(9L));
                will(returnValue(Arrays.asList(activity)));
            }
        });

        sut.populate(notification);

        context.assertIsSatisfied();
        assertEquals(BaseObjectType.NOTE, notification.getActivityType());
    }

    /**
     * Tests populating destination.
     */
    @Test
    public void testPopulateDestinationPerson()
    {
        notification.setDestination(DESTINATION_ID, EntityType.PERSON);

        context.checking(new Expectations()
        {
            {
                allowing(personMapper).execute(DESTINATION_ID);
                will(returnValue(person));
            }
        });

        sut.populate(notification);

        context.assertIsSatisfied();
        assertEquals(PERSON_ACCOUNT_ID, notification.getDestinationUniqueId());
        assertEquals(PERSON_NAME, notification.getDestinationName());
    }

    /**
     * Tests populating destination.
     */
    @Test
    public void testPopulateDestinationGroup()
    {
        notification.setDestination(DESTINATION_ID, EntityType.GROUP);

        final DomainGroupModelView group = new DomainGroupModelView();
        group.setShortName("mygroup");
        group.setName("My Group");

        context.checking(new Expectations()
        {
            {
                allowing(groupMapper).execute(DESTINATION_ID);
                will(returnValue(group));
            }
        });

        sut.populate(notification);

        context.assertIsSatisfied();
        assertEquals("mygroup", notification.getDestinationUniqueId());
        assertEquals("My Group", notification.getDestinationName());
    }

    /**
     * Tests populating destination.
     */
    @Test
    public void testPopulateDestinationOrg()
    {
        notification.setDestination(DESTINATION_ID, EntityType.ORGANIZATION);

        final OrganizationModelView org = new OrganizationModelView();
        org.setShortName("myorg");
        org.setName("My Org");

        context.checking(new Expectations()
        {
            {
                allowing(orgMapper).execute(DESTINATION_ID);
                will(returnValue(org));
            }
        });

        sut.populate(notification);

        context.assertIsSatisfied();
        assertEquals("myorg", notification.getDestinationUniqueId());
        assertEquals("My Org", notification.getDestinationName());
    }

    /**
     * Tests that it leaves already set parts alone.
     */
    @Test
    public void testPopulateLeaveAlone1()
    {
        notification.setActorId(1L);
        notification.setActorAccountId(".");

        notification.setActivityId(1L);
        notification.setActivityType(BaseObjectType.BOOKMARK);

        notification.setDestination(1L, EntityType.PERSON);
        notification.setDestinationUniqueId(".");

        context.assertIsSatisfied();
    }

    /**
     * Tests that it leaves already set parts alone.
     */
    @Test
    public void testPopulateLeaveAlone2()
    {
        notification.setActorId(1L);
        notification.setActorAccountId("");
        notification.setActorName(".");

        notification.setActivityId(1L);
        notification.setActivityType(BaseObjectType.BOOKMARK);

        notification.setDestination(1L, EntityType.PERSON);
        notification.setDestinationUniqueId("");
        notification.setDestinationName(".");

        context.assertIsSatisfied();
    }

}
