/*
 * Copyright (c) 2009-2010 Lockheed Martin Corporation
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.GetRelatedOrganizationIdsByPersonId;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.cache.testhelpers.SimpleMemoryCache;
import org.eurekastreams.server.persistence.strategies.PersonQueryStrategy;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test fixture for PersonCacheLoader.
 */
public class PersonCacheLoaderTest extends MapperTest
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
    private PersonCacheLoader personCacheLoader;

    /**
     * Cache fed into the loader.
     */
    private Cache cache;

    /**
     * Strategy to query for person model views.
     */
    @Autowired
    private PersonQueryStrategy personQueryStrategy;

    /**
     * Mapper to get related org ids for people.
     */
    private GetRelatedOrganizationIdsByPersonId
    // line break
    getRelatedOrganizationIdsByPersonIdMapper = new GetRelatedOrganizationIdsByPersonId();

    /**
     * Mapper to remove a person from cache.
     */
    private RemovePersonFromCacheMapper removePersonFromCacheMapper;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        getRelatedOrganizationIdsByPersonIdMapper.setEntityManager(getEntityManager());

        cache = new SimpleMemoryCache();
        removePersonFromCacheMapper = context.mock(RemovePersonFromCacheMapper.class);
        personCacheLoader = new PersonCacheLoader(personQueryStrategy, getRelatedOrganizationIdsByPersonIdMapper,
                removePersonFromCacheMapper);
        personCacheLoader.setEntityManager(getEntityManager());
        personCacheLoader.setCache(cache);

        Person.setEntityCacheUpdater(null);
        getEntityManager().clear();
    }

    /**
     * Tear-down method.
     */
    @After
    public void tearDown()
    {
        Person.setEntityCacheUpdater(null);
        getEntityManager().clear();
    }

    /**
     * Test initialize.
     */
    @Test
    public void testInitialize()
    {
        final long fordpId = 42L;
        personCacheLoader.initialize();

        PersonModelView ford = (PersonModelView) cache.get(CacheKeys.PERSON_BY_ID + fordpId);
        assertEquals("fordp", ford.getAccountId());
        assertEquals(2, ford.getRelatedOrganizationIds().size());
        assertTrue(ford.getRelatedOrganizationIds().contains(5L));
        assertTrue(ford.getRelatedOrganizationIds().contains(6L));

        assertEquals(fordpId, cache.get(CacheKeys.PERSON_BY_ACCOUNT_ID + "fordp"));
        assertEquals(fordpId, cache.get(CacheKeys.PERSON_BY_OPEN_SOCIAL_ID + "2d359911-0977-418a-9490-57e8252b1a42"));
        assertEquals(2, (cache.getList(CacheKeys.FOLLOWERS_BY_PERSON + "99")).size());
        assertEquals(2, (cache.getList(CacheKeys.PEOPLE_FOLLOWED_BY_PERSON + "98")).size());
    }

    /**
     * Test onPostUpdate.
     */
    @Test
    public void testOnPostUpdate()
    {
        final long smithersId = 98L;
        personCacheLoader.initialize();

        // make sure the person is in cache
        assertNotNull(cache.get(CacheKeys.PERSON_BY_ID + smithersId));
        assertNotNull(cache.getList(CacheKeys.FOLLOWERS_BY_PERSON + smithersId));

        // update the person
        final Person smithersPerson = getEntityManager().find(Person.class, smithersId);
        smithersPerson.setAvatarId("HeyNowNiceAvatarId");

        context.checking(new Expectations()
        {
            {
                oneOf(removePersonFromCacheMapper).execute(smithersPerson);
            }
        });

        // update the cache
        personCacheLoader.onPostUpdate(smithersPerson);

        // but not for followers
        assertNotNull(cache.getList(CacheKeys.FOLLOWERS_BY_PERSON + smithersId));

        context.assertIsSatisfied();
    }

    /**
     * Test onPostPersist, which does nothing.
     */
    @Test
    public void testOnPostPersist()
    {
        final Long fordId = 42L;
        personCacheLoader.onPostPersist(getEntityManager().find(Person.class, fordId));
    }
}
