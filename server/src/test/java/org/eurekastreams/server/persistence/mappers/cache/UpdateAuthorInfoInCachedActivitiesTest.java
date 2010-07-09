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
package org.eurekastreams.server.persistence.mappers.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.domain.stream.ActivityDTO;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test fixture for UpdateAuthorInfoInCachedActivities.
 */
public class UpdateAuthorInfoInCachedActivitiesTest
{
    /**
     * Context for building mock objects.
     */
    private final Mockery context = new JUnit4Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    /**
     * System under test.
     */
    private UpdateAuthorInfoInCachedActivities sut;

    /**
     * StreamEntityDTO updater #1.
     */
    private UpdateStreamEntityDTOFromPerson updater1;

    /**
     * StreamEntityDTO updater #2.
     */
    private UpdateStreamEntityDTOFromPerson updater2;

    /**
     * List of updaters.
     */
    private List<UpdateStreamEntityDTOFromPerson> authorStreamEntityDTOUpdaters;

    /**
     * Mocked activity.
     */
    private ActivityDTO activityDTO;

    /**
     * Mocked person.
     */
    private Person personWithUpdatedInfo;

    /**
     * Mocked person.
     */
    private Person randomPerson;

    /**
     * The person id of the person being updated.
     */
    private final Long updatedPersonId = 382L;

    /**
     * The person id of the person being updated.
     */
    private final Long randomPersonId = 382L;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        authorStreamEntityDTOUpdaters = new ArrayList<UpdateStreamEntityDTOFromPerson>();
        sut = new UpdateAuthorInfoInCachedActivities(2, authorStreamEntityDTOUpdaters);

        activityDTO = context.mock(ActivityDTO.class);
        personWithUpdatedInfo = context.mock(Person.class, "personWithUpdatedInfo");
        randomPerson = context.mock(Person.class, "randomPerson");
        updater1 = context.mock(UpdateStreamEntityDTOFromPerson.class, "updater1");
        updater2 = context.mock(UpdateStreamEntityDTOFromPerson.class, "updater2");

        context.checking(new Expectations()
        {
            {
                allowing(activityDTO).getId();
                will(returnValue(5L));

                allowing(personWithUpdatedInfo).getId();
                will(returnValue(updatedPersonId));

                allowing(randomPerson).getId();
                will(returnValue(randomPersonId));
            }
        });
    }

    /**
     * Test getCacheKeyPrefix().
     */
    @Test
    public void testGetCacheKeyPrefix()
    {
        assertEquals(CacheKeys.ACTIVITY_BY_ID, sut.getCacheKeyPrefix());
    }

    /**
     * Test updateCachedEntity with null actor and original actor.
     */
    @Test
    public void testUpdateCachedEntityWithPersonWithNullActorAndOriginalActor()
    {
        authorStreamEntityDTOUpdaters.add(updater1);
        authorStreamEntityDTOUpdaters.add(updater2);

        context.checking(new Expectations()
        {
            {
                allowing(activityDTO).getActor();
                will(returnValue(null));

                allowing(activityDTO).getOriginalActor();
                will(returnValue(null));
            }
        });

        // invoke sut
        boolean result = sut.updateCachedEntity(activityDTO, personWithUpdatedInfo);
        assertFalse(result);

        context.assertIsSatisfied();
    }

    /**
     * Test updateCachedEntity with null actor and original actor.
     */
    @Test
    public void testUpdateCachedEntityWithNonPersonActivities()
    {
        authorStreamEntityDTOUpdaters.add(updater1);
        authorStreamEntityDTOUpdaters.add(updater2);

        final StreamEntityDTO actor = new StreamEntityDTO();
        actor.setType(EntityType.APPLICATION);

        context.checking(new Expectations()
        {
            {
                allowing(activityDTO).getActor();
                will(returnValue(actor));

                allowing(activityDTO).getOriginalActor();
                will(returnValue(actor));
            }
        });

        // invoke sut
        boolean result = sut.updateCachedEntity(activityDTO, personWithUpdatedInfo);
        assertFalse(result);

        context.assertIsSatisfied();
    }

    /**
     * Test updateCachedEntity on an activity that the input person was not the actor or original actor.
     */
    @Test
    public void testUpdateCachedEntityWithUpdatedPersonNotActorOrOriginalActor()
    {
        authorStreamEntityDTOUpdaters.add(updater1);
        authorStreamEntityDTOUpdaters.add(updater2);

        final StreamEntityDTO randomPersonEntity = new StreamEntityDTO();
        randomPersonEntity.setType(EntityType.PERSON);

        context.checking(new Expectations()
        {
            {
                allowing(activityDTO).getActor();
                will(returnValue(randomPersonEntity));

                allowing(activityDTO).getOriginalActor();
                will(returnValue(randomPersonEntity));
            }
        });

        // invoke sut
        boolean result = sut.updateCachedEntity(activityDTO, personWithUpdatedInfo);
        assertFalse(result);

        context.assertIsSatisfied();
    }

    /**
     * Test updateCachedEntity on an activity that the input person was the actor and original actor, and there's no
     * updates.
     */
    @Test
    public void testUpdateCachedEntityWithUpdatedPersonActorAndOriginalActorAndNoChanges()
    {
        authorStreamEntityDTOUpdaters.add(updater1);
        authorStreamEntityDTOUpdaters.add(updater2);

        final StreamEntityDTO actorEntity = new StreamEntityDTO();
        actorEntity.setType(EntityType.PERSON);
        actorEntity.setId(updatedPersonId);

        final StreamEntityDTO originalActorEntity = new StreamEntityDTO();
        originalActorEntity.setType(EntityType.PERSON);
        originalActorEntity.setId(updatedPersonId);

        context.checking(new Expectations()
        {
            {
                allowing(activityDTO).getActor();
                will(returnValue(actorEntity));

                allowing(activityDTO).getOriginalActor();
                will(returnValue(originalActorEntity));

                oneOf(updater1).execute(actorEntity, personWithUpdatedInfo);
                will(returnValue(false));

                oneOf(updater2).execute(actorEntity, personWithUpdatedInfo);
                will(returnValue(false));

                oneOf(updater1).execute(originalActorEntity, personWithUpdatedInfo);
                will(returnValue(false));

                oneOf(updater2).execute(originalActorEntity, personWithUpdatedInfo);
                will(returnValue(false));
            }
        });

        // invoke sut
        boolean result = sut.updateCachedEntity(activityDTO, personWithUpdatedInfo);
        assertFalse(result);

        context.assertIsSatisfied();
    }

    /**
     * Test updateCachedEntity on an activity that the input person was the actor and original actor, and there's
     * updates.
     */
    @Test
    public void testUpdateCachedEntityWithUpdatedPersonActorAndOriginalActorAndAreChangesToOriginalActor1()
    {
        authorStreamEntityDTOUpdaters.add(updater1);
        authorStreamEntityDTOUpdaters.add(updater2);

        final StreamEntityDTO actorEntity = new StreamEntityDTO();
        actorEntity.setType(EntityType.PERSON);
        actorEntity.setId(updatedPersonId);

        final StreamEntityDTO originalActorEntity = new StreamEntityDTO();
        originalActorEntity.setType(EntityType.PERSON);
        originalActorEntity.setId(updatedPersonId);

        context.checking(new Expectations()
        {
            {
                allowing(activityDTO).getActor();
                will(returnValue(actorEntity));

                allowing(activityDTO).getOriginalActor();
                will(returnValue(originalActorEntity));

                oneOf(updater1).execute(actorEntity, personWithUpdatedInfo);
                will(returnValue(false));

                oneOf(updater2).execute(actorEntity, personWithUpdatedInfo);
                will(returnValue(true));

                oneOf(updater1).execute(originalActorEntity, personWithUpdatedInfo);
                will(returnValue(false));

                oneOf(updater2).execute(originalActorEntity, personWithUpdatedInfo);
                will(returnValue(false));
            }
        });

        // invoke sut
        boolean result = sut.updateCachedEntity(activityDTO, personWithUpdatedInfo);
        assertTrue(result);

        context.assertIsSatisfied();
    }

    /**
     * Test updateCachedEntity on an activity that the input person was the actor and original actor, and there's
     * updates.
     */
    @Test
    public void testUpdateCachedEntityWithUpdatedPersonActorAndOriginalActorAndAreChangesToOriginalActor2()
    {
        authorStreamEntityDTOUpdaters.add(updater1);
        authorStreamEntityDTOUpdaters.add(updater2);

        final StreamEntityDTO actorEntity = new StreamEntityDTO();
        actorEntity.setType(EntityType.PERSON);
        actorEntity.setId(updatedPersonId);

        final StreamEntityDTO originalActorEntity = new StreamEntityDTO();
        originalActorEntity.setType(EntityType.PERSON);
        originalActorEntity.setId(updatedPersonId);

        context.checking(new Expectations()
        {
            {
                allowing(activityDTO).getActor();
                will(returnValue(actorEntity));

                allowing(activityDTO).getOriginalActor();
                will(returnValue(originalActorEntity));

                oneOf(updater1).execute(actorEntity, personWithUpdatedInfo);
                will(returnValue(false));

                oneOf(updater2).execute(actorEntity, personWithUpdatedInfo);
                will(returnValue(false));

                oneOf(updater1).execute(originalActorEntity, personWithUpdatedInfo);
                will(returnValue(true));

                oneOf(updater2).execute(originalActorEntity, personWithUpdatedInfo);
                will(returnValue(false));
            }
        });

        // invoke sut
        boolean result = sut.updateCachedEntity(activityDTO, personWithUpdatedInfo);
        assertTrue(result);

        context.assertIsSatisfied();
    }
}
