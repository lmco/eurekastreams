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
package org.eurekastreams.server.domain;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eurekastreams.server.domain.stream.StreamScope;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for DomainGroup.
 */
public class DomainGroupTest
{
    /**
     * Subject under test.
     */
    private DomainGroup sut;

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
     * StreamScope mock.
     */
    private final StreamScope streamScopeMock = context.mock(StreamScope.class);

    /**
     * Set up the SUT.
     */
    @Before
    public void setup()
    {
        sut = new DomainGroup();
    }

    /**
     * Test the getters and setters.
     */
    @Test
    public void testGettersAndSetters()
    {
        sut.setId(9L);
        assertEquals(9L, sut.getId());
        assertEquals(9L, sut.getEntityId());

        sut.setStreamScope(streamScopeMock);
        assertEquals(streamScopeMock, sut.getStreamScope());

        sut.setAvatarCropSize(4);
        assertEquals((Integer) 4, sut.getAvatarCropSize());

        sut.setAvatarCropY(4);
        assertEquals((Integer) 4, sut.getAvatarCropY());

        sut.setAvatarCropX(2);
        assertEquals((Integer) 2, sut.getAvatarCropX());

        sut.setAvatarId("ID");
        assertEquals("ID", sut.getAvatarId());

        sut.setName("name");
        assertEquals("name", sut.getName());
        assertEquals("name", sut.getDisplayName());

        sut.setName(null);
        assertEquals("", sut.getName());

        sut.setShortName("shorTName");
        assertEquals("shortname", sut.getShortName());

        sut.setShortName(null);
        assertEquals("", sut.getShortName());

        sut.setDescription("Description");
        assertEquals("Description", sut.getDescription());

        sut.setPublicGroup(true);
        assertTrue(sut.isPublicGroup());

        sut.setPending(true);
        assertTrue(sut.isPending());

        sut.setPending(false);
        assertFalse(sut.isPending());

        sut.setStreamPostable(false);
        assertFalse(sut.isStreamPostable());

        sut.setCommentable(false);
        assertFalse(sut.isCommentable());

        Person creator = context.mock(Person.class);
        sut.setCreatedBy(creator);
        assertEquals(creator, sut.getCreatedBy());

        assertEquals(EntityType.GROUP, sut.getEntityType());

        sut.setStickyActivityId(7L);
        assertEquals((Long) 7L, sut.getStickyActivityId());
    }

    /**
     * Test the coordinator methods.
     */
    @Test
    public void testCoordinators()
    {
        Person p1 = new Person("p1", "f", "m", "l", "p");
        Person p2 = new Person("p2", "f", "m", "l", "p");

        sut.addCoordinator(p1);
        sut.addCoordinator(p2);

        Set<Person> coordinators = sut.getCoordinators();
        assertEquals(2, coordinators.size());

        assertTrue(sut.isCoordinator("p1"));
        assertTrue(sut.isCoordinator("p2"));
        assertFalse(sut.isCoordinator("p3"));

        Set<Person> noCoordinators = new HashSet<Person>();
        sut.setCoordinators(noCoordinators);
        assertEquals(0, sut.getCoordinators().size());
    }

    /**
     * Test get/set of capabilities.
     */
    @Test
    public void testGetSetCapabilities()
    {
        final BackgroundItem backgroundItemMock = context.mock(BackgroundItem.class);

        // verify that get on null collection returns empty list, not null;
        assertNotNull(sut.getCapabilities());

        // verify that get returns what set sets.
        ArrayList<BackgroundItem> testList = new ArrayList<BackgroundItem>(1);
        testList.add(backgroundItemMock);

        sut.setCapabilities(testList);
        assertEquals("get not returning same list as set assigned", testList, sut.getCapabilities());
    }

    /**
     * Test the name/shortname constructor.
     */
    @Test
    public void testConstructor()
    {
        Person p1 = new Person("p1", "f", "m", "l", "p");
        final String name = "Foo Bar";
        final String shortName = "foo";
        DomainGroup dg = new DomainGroup(name, "FoO", p1);
        assertEquals(name, dg.getName());
        assertEquals(shortName, dg.getShortName());
    }

    /**
     * Test the hashcode.
     */
    @Test
    public void testHashCodeAndEquals()
    {
        Person p1 = new Person("p1", "f", "m", "l", "p");
        final String name = "Foo Bar";
        final String shortName = "foo";
        final long id = 3932L;
        DomainGroup dg1 = new DomainGroup(name, shortName, p1);
        dg1.setId(id);

        DomainGroup dg2 = new DomainGroup(name, shortName, p1);
        dg2.setId(id);

        assertEquals(dg1.hashCode(), dg2.hashCode());
        assertTrue(dg1.equals(dg2));
        assertFalse(dg1.equals(5L));
    }

    /**
     * Test the following methods.
     */
    @Test
    public void testFollowing()
    {
        int count = 5;
        int actual = sut.getFollowersCount();
        assertEquals(0, actual);

        sut.setFollowersCount(count);

        actual = sut.getFollowersCount();

        assertEquals(count, actual);
    }

    /**
     * Test the updates methods.
     */
    @Test
    public void testUpdates()
    {
        sut.setUpdatesCount(3);
        assertEquals(3, sut.getUpdatesCount());
    }

    /**
     * Test the banner methods.
     */
    @Test
    public void testBanner()
    {
        String expectedId = "bannerId";

        sut.setBannerId(expectedId);
        assertEquals(expectedId, sut.getBannerId());
    }

    /**
     * Test the datAdded property.
     */
    @Test
    public void testDateAdded()
    {
        Date date = new Date();
        sut.setDateAdded(date);
        assertSame(date, sut.getDateAdded());
    }
}
