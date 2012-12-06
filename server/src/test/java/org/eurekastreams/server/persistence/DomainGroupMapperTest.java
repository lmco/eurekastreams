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
package org.eurekastreams.server.persistence;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import javax.persistence.NoResultException;

import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.DomainGroupEntity;
import org.eurekastreams.server.domain.Followable;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.Person;
import org.eurekastreams.server.persistence.mappers.cache.Cache;
import org.eurekastreams.server.persistence.mappers.cache.DomainGroupCacheLoader;
import org.eurekastreams.server.persistence.mappers.cache.testhelpers.SimpleMemoryCache;
import org.eurekastreams.server.persistence.strategies.DomainGroupQueryStrategy;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Test for JpaGroupMapper.
 */
public class DomainGroupMapperTest extends DomainEntityMapperTest
{
    /**
     * cache fed into the cache loaders.
     */
    private Cache cache;

    /**
     * The subject under test.
     */
    @Autowired
    private DomainGroupMapper jpaGroupMapper;

    /**
     * Used to load up people for FollowMapper tests.
     */
    @Autowired
    private PersonMapper jpaPersonMapper;

    /**
     * Group Cache Loader.
     */
    private DomainGroupCacheLoader groupCacheLoader;

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
     * Setup.
     */
    @Before
    public void setup()
    {
        DomainGroup.setEntityCacheUpdater(null);
        cache = new SimpleMemoryCache();

        groupCacheLoader = new DomainGroupCacheLoader(new DomainGroupQueryStrategy());

        groupCacheLoader.setEntityManager(getEntityManager());

        groupCacheLoader.setCache(cache);

        groupCacheLoader.initialize();
    }

    /**
     * Teardown method.
     */
    @After
    public void teardown()
    {
        DomainGroup.setEntityCacheUpdater(null);
        cache = null;
    }

    /**
     * Test group lookup when the searched-for group does not exist in the database.
     */
    @Test
    public void testFindByShortNameWithNoSuchRecord()
    {
        DomainGroupEntity actual = jpaGroupMapper.findByShortName("nosuchgroup");

        assertNull(actual);
    }

    /**
     * Test group lookup.
     */
    @Test
    public void testFindByShortName()
    {
        DomainGroupEntity actual = jpaGroupMapper.findByShortName("group1");

        assertNotNull(actual);
        assertEquals("E Group 1 Name", actual.getName());
    }

    /**
     * Test a simple getter.
     */
    @Test
    public void testGetDomainEntityName()
    {
        assertEquals("DomainGroup", jpaGroupMapper.getDomainEntityName());
    }

    /**
     * Test getFollowing().
     */
    @Test
    public void testGetFollowing()
    {
        final long followedGroupId = 1L;
        PagedSet<Followable> followedGroups = jpaGroupMapper.getFollowing("mrburns", 0, 9);
        assertEquals(1, followedGroups.getTotal());
        assertEquals(0, followedGroups.getFromIndex());
        assertEquals(0, followedGroups.getToIndex());

        List<Followable> groups = followedGroups.getPagedSet();
        assertEquals(1, groups.size());
        assertEquals(followedGroupId, groups.get(0).getId());
    }

    /**
     * Test add follower.
     */
    @Test
    public void testAddFollower()
    {
        DomainGroup group = jpaGroupMapper.findByShortName("group1");

        Person fordp2 = jpaPersonMapper.findByAccountId("fordp2");
        Person csagan = jpaPersonMapper.findByAccountId("csagan");

        // Verify initial state
        assertFalse(jpaGroupMapper.isFollowing("fordp2", "group1"));
        assertFalse(jpaGroupMapper.isFollowing("csagan", "group1"));
        assertEquals(0, fordp2.getFollowingCount());
        assertEquals(0, fordp2.getGroupCount());
        assertEquals(0, csagan.getFollowingCount());
        assertEquals(1, csagan.getGroupCount());

        // ford wants to follow the group
        jpaGroupMapper.addFollower(fordp2.getId(), group.getId());

        getEntityManager().clear();

        fordp2 = jpaPersonMapper.findByAccountId("fordp2");
        csagan = jpaPersonMapper.findByAccountId("csagan");

        // verify new state
        assertTrue(jpaGroupMapper.isFollowing("fordp2", "group1"));
        // Test case insensitivity (everything should be lower cased by the mapper).
        assertTrue(jpaGroupMapper.isFollowing("fordp2", "Group1"));
        assertFalse(jpaGroupMapper.isFollowing("csagan", "group1"));
        assertEquals(0, fordp2.getFollowingCount());
        assertEquals(1, fordp2.getGroupCount());
        assertEquals(0, csagan.getFollowingCount());
        assertEquals(1, csagan.getGroupCount());

        // csagan wants to follow the group
        jpaGroupMapper.addFollower(csagan.getId(), group.getId());

        getEntityManager().clear();

        fordp2 = jpaPersonMapper.findByAccountId("fordp2");
        csagan = jpaPersonMapper.findByAccountId("csagan");

        // verify new state
        assertTrue(jpaGroupMapper.isFollowing("fordp2", "group1"));
        assertTrue(jpaGroupMapper.isFollowing("csagan", "group1"));
        // and verify csagan's counts after the change
        assertEquals(0, csagan.getFollowingCount());
        assertEquals(2, csagan.getGroupCount());
    }

    /**
     * Test addFollower when the person's already following the group.
     */
    @Test
    public void testAddFollowerWhenAlreadyFollowing()
    {
        final long personId = 99L;
        final long groupId = 1L;

        Person p = jpaPersonMapper.findById(personId);
        DomainGroup g = jpaGroupMapper.findById(groupId);

        int initialGroupsCount = p.getGroupCount();
        int initialFollowersCount = g.getFollowersCount();

        // invoke SUT
        jpaGroupMapper.addFollower(p.getId(), g.getId());

        // clear the entity manager, reload the entities, and assert the counts haven't changed
        getEntityManager().clear();

        p = jpaPersonMapper.findById(personId);
        g = jpaGroupMapper.findById(groupId);

        assertEquals(initialGroupsCount, p.getGroupCount());
        assertEquals(initialFollowersCount, g.getFollowersCount());
    }

    /**
     * Test whether the input user is a group coordinator - true.
     */
    @Test
    public void testInputUserIsGroupCoordinator()
    {
        final long personId = 99L;
        final long groupId = 1L;

        Person p = jpaPersonMapper.findById(personId);
        DomainGroup g = jpaGroupMapper.findById(groupId);

        if (!jpaGroupMapper.isInputUserGroupCoordinator(personId, groupId))
        {
            g.addCoordinator(p);
        }

        boolean result = jpaGroupMapper.isInputUserGroupCoordinator(personId, groupId);

        assertEquals(true, result);
    }

    /**
     * Test whether the input user is a group coordinator - false.
     */
    @Test
    public void testInputUserIsNotGroupCoordinator()
    {
        final long personId = 99L;
        final long groupId = 1L;

        Person p = jpaPersonMapper.findById(personId);
        DomainGroup g = jpaGroupMapper.findById(groupId);

        if (jpaGroupMapper.isInputUserGroupCoordinator(personId, groupId))
        {
            jpaGroupMapper.removeGroupCoordinator(personId, groupId);
        }

        boolean result = jpaGroupMapper.isInputUserGroupCoordinator(personId, groupId);

        assertEquals(false, result);
    }

    /**
     * Test the Group Coordinator count.
     */
    @Test
    public void testGetGroupCoordinatorCount()
    {
        final long personId = 99L;
        final long groupId = 1L;

        Person p = jpaPersonMapper.findById(personId);
        DomainGroup g = jpaGroupMapper.findById(groupId);

        int numberOfGroupCoordinators = g.getCoordinators().size();
        int result = jpaGroupMapper.getGroupCoordinatorCount(groupId);

        assertEquals(numberOfGroupCoordinators, result);
    }

    /**
     * Test remove a group coordinator.
     */
    @Test
    public void testRemoveGroupCoordinator()
    {
        final long personId = 99L;
        final long groupId = 1L;

        Person p = jpaPersonMapper.findById(personId);
        DomainGroup g = jpaGroupMapper.findById(groupId);

        int numberOfGroupCoordinatorsBeforeRemoval = jpaGroupMapper.getGroupCoordinatorCount(groupId);

        Object[] groupCoordinators = g.getCoordinators().toArray();

        long firstGroupCoordinatorId = ((Person) groupCoordinators[0]).getEntityId();

        jpaGroupMapper.removeGroupCoordinator(firstGroupCoordinatorId, groupId);

        int numberOfGroupCoordinatorsAfterRemoval = jpaGroupMapper.getGroupCoordinatorCount(groupId);

        assertEquals(numberOfGroupCoordinatorsBeforeRemoval - 1, numberOfGroupCoordinatorsAfterRemoval);
    }

    /**
     * Test whether a group is private - true.
     */
    @Test
    public void testIsGroupPrivate()
    {
        final long personId = 99L;
        final long groupId = 1L;

        Person p = jpaPersonMapper.findById(personId);
        DomainGroup g = jpaGroupMapper.findById(groupId);

        if (g.isPublicGroup())
        {
            g.setPublicGroup(false);
        }

        boolean result = jpaGroupMapper.isGroupPrivate(groupId);

        assertEquals(true, result);
    }

    /**
     * Test whether a group is private - false.
     */
    @Test
    public void testIsGroupNotPrivate()
    {
        final long personId = 99L;
        final long groupId = 1L;

        Person p = jpaPersonMapper.findById(personId);
        DomainGroup g = jpaGroupMapper.findById(groupId);

        if (!g.isPublicGroup())
        {
            g.setPublicGroup(true);
        }

        boolean result = jpaGroupMapper.isGroupPrivate(groupId);

        assertEquals(false, result);
    }

    /**
     * Test remove follower.
     */
    @Test
    @Transactional
    public void testRemoveFollower()
    {
        DomainGroup group = jpaGroupMapper.findByShortName("group1");
        Person burns = jpaPersonMapper.findByAccountId("mrburns");

        assertEquals(0, burns.getFollowingCount());
        assertEquals(1, burns.getGroupCount());
        assertEquals(3, group.getFollowersCount());
        assertTrue(jpaGroupMapper.isFollowing("mrburns", "group1"));

        jpaGroupMapper.removeFollower(burns.getId(), group.getId());
        getEntityManager().clear();
        group = jpaGroupMapper.findByShortName("group1");
        burns = jpaPersonMapper.findByAccountId("mrburns");

        assertEquals(0, burns.getFollowingCount());
        assertEquals(0, burns.getGroupCount());
        assertEquals(2, group.getFollowersCount());
        assertFalse(jpaGroupMapper.isFollowing("mrburns", "group1"));
    }

    /**
     * Test get followers. Dataset.xml has one follower for the group, so we should start with 1 and then see 3.
     */
    @Test
    public void testGetFollowers()
    {
        final int maxFollowers = 10;

        DomainGroup group = jpaGroupMapper.findByShortName("group1");
        Person fordp2 = jpaPersonMapper.findByAccountId("fordp2");
        Person csagan = jpaPersonMapper.findByAccountId("csagan");

        PagedSet<Person> followers = jpaGroupMapper.getFollowers("group1", 0, maxFollowers);

        assertEquals(3, followers.getTotal());

        jpaGroupMapper.addFollower(fordp2.getId(), group.getId());
        jpaGroupMapper.addFollower(csagan.getId(), group.getId());

        followers = jpaGroupMapper.getFollowers("group1", 0, maxFollowers);

        assertEquals(5, followers.getTotal());
    }

    /**
     * Test getFollowerAndCoordinatorPersonIds(DomainGroup).
     */
    @Test
    public void testGetFollowerAndCoordinatorPersonIds()
    {
        DomainGroup group = jpaGroupMapper.findById(1);
        Long[] ids = jpaGroupMapper.getFollowerAndCoordinatorPersonIds(group);
        List<Long> idsList = Arrays.asList(ids);
        assertEquals(3, idsList.size());
        final long burnsId = 99L;
        final long smithersId = 98L;
        final long fordId = 42L;
        assertTrue(idsList.contains(burnsId));
        assertTrue(idsList.contains(smithersId));
        assertTrue(idsList.contains(fordId));
    }

    /**
     * Test deleting a group.
     */
    @Test(expected = NoResultException.class)
    public void testDelete()
    {
        DomainGroup group = jpaGroupMapper.findById(2L);
        assertNotNull(group);

        jpaGroupMapper.deleteById(2L);

        jpaGroupMapper.findById(2);
    }
}
