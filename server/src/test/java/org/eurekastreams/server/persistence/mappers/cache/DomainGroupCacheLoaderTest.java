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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.persistence.mappers.MapperTest;
import org.eurekastreams.server.persistence.mappers.cache.testhelpers.SimpleMemoryCache;
import org.eurekastreams.server.persistence.strategies.DomainGroupQueryStrategy;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test fixture for DomainGroupCacheLoader.
 */
public class DomainGroupCacheLoaderTest extends MapperTest
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
    private DomainGroupCacheLoader domainGroupCacheLoader;

    /**
     * Cache fed into the domain group loader.
     */
    private Cache cache;

    /**
     * Strategy to get domain groups.
     */
    @Autowired
    private DomainGroupQueryStrategy domainGroupQueryStrategy;

    /**
     * Setup method.
     */
    @Before
    public void setup()
    {
        cache = new SimpleMemoryCache();
        domainGroupCacheLoader = new DomainGroupCacheLoader(domainGroupQueryStrategy);
        domainGroupCacheLoader.setEntityManager(getEntityManager());
        domainGroupCacheLoader.setCache(cache);

        DomainGroup.setEntityCacheUpdater(null);
        getEntityManager().clear();
    }

    /**
     * Tear-down method.
     */
    @After
    public void tearDown()
    {
        DomainGroup.setEntityCacheUpdater(null);
        getEntityManager().clear();
    }

    /**
     * Test initializing the cache.
     */
    @Test
    public void testInitializeGroupCache()
    {
        domainGroupCacheLoader.initialize();

        // check the group short name lookup:
        assertEquals(1L, cache.get(CacheKeys.GROUP_BY_SHORT_NAME + "group1"));

        // get the domain group by id
        DomainGroupModelView group = (DomainGroupModelView) cache.get(CacheKeys.GROUP_BY_ID + "1");

        // check the standard properties
        assertEquals(1L, group.getEntityId());
        assertEquals("E Group 1 Name", group.getName());
        assertEquals("group1", group.getShortName());
        assertTrue(group.isPublic());

        // check the followers:
        final long person1 = 98L;
        final long person2 = 99L;
        final long person3 = 42L;

        List<Long> groupFollowerIds = cache.getList(CacheKeys.FOLLOWERS_BY_GROUP + "1");
        assertEquals(3, groupFollowerIds.size());
        assertTrue(groupFollowerIds.contains(person1));
        assertTrue(groupFollowerIds.contains(person2));
        assertTrue(groupFollowerIds.contains(person3));

        // check the coordinators
        List<Long> groupCoordinatorIds = cache.getList(CacheKeys.COORDINATOR_PERSON_IDS_BY_GROUP_ID + "1");
        assertEquals(2, groupCoordinatorIds.size());
        assertTrue(groupCoordinatorIds.contains(person1));
        assertTrue(groupCoordinatorIds.contains(person3));
    }

    /**
     * Test that pending groups aren't loaded.
     */
    @Test
    public void testPendingGroupNotLoadedInCache()
    {
        domainGroupCacheLoader.initialize();

        // make sure the pending group isn't cached by short name
        assertNull(cache.get(CacheKeys.GROUP_BY_SHORT_NAME + "pendinggroup"));

        // make sure the pending group isn't cached by id
        assertNull(cache.get(CacheKeys.GROUP_BY_ID + "6"));
    }

    /**
     * Test storing a group in cache, then changing its visibility to private, private searchable, re-adding to cache
     * and checking.
     */
    @Test
    public void testOnPostUpdateRemovesFromCache()
    {
        // initialize cache
        domainGroupCacheLoader.initialize();

        // make sure the group is in cache
        assertNotNull(cache.get(CacheKeys.GROUP_BY_ID + "1"));
        assertNotNull(cache.get(CacheKeys.GROUP_BY_SHORT_NAME + "group1"));

        // update it
        final DomainGroup domainGroup = getEntityManager().find(DomainGroup.class, 1L);

        // invoke SUT
        domainGroupCacheLoader.onPostUpdate(domainGroup);

        // but should keep the lookup from ID -> short name, since it can't change
        assertNotNull(cache.get(CacheKeys.GROUP_BY_SHORT_NAME + "group1"));

        context.assertIsSatisfied();
    }

    /**
     * Test OnPostPersist, which currently does nothing.
     */
    @Test
    public void testOnPostPersist()
    {
        domainGroupCacheLoader.onPostPersist(getEntityManager().find(DomainGroup.class, 1L));
    }
}
