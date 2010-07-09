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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;

import org.eurekastreams.server.domain.Background;
import org.eurekastreams.server.domain.BackgroundItem;
import org.eurekastreams.server.domain.BackgroundItemType;
import org.eurekastreams.server.domain.Person;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is responsible for testing the JPA Implementation of the Background Mapper interface. The tests contained
 * in here ensure proper interaction with the database.
 */
public class BackgroundMapperTest extends DomainEntityMapperTest
{
    /**
     * JpaBackgroundMapper - system under test.
     */
    @Autowired
    private BackgroundMapper jpaBackgroundMapper;

    /**
     * Person mapper.
     */
    @Autowired
    private PersonMapper jpaPersonMapper;

    /**
     * Test inserting a background.
     */
    @Test
    public void testInsert()
    {
        final long personId = 142L;
        Person person = jpaPersonMapper.findById(personId);
        Background background = new Background(person);
        jpaBackgroundMapper.insert(background);
        long backgroundId = background.getId();
        jpaBackgroundMapper.getEntityManager().clear();

        assertTrue("Inserting a Background did not get a positive id.", jpaBackgroundMapper.findById(backgroundId)
                .getId() > 0);
    }

    /**
     * Test inserting a background.
     */
    @Test
    public void testDelete()
    {
        final long backgroundId = 1042L;
        Background background = jpaBackgroundMapper.findById(backgroundId);
        jpaBackgroundMapper.delete(background);

        try
        {
            background = jpaBackgroundMapper.findById(backgroundId);
        }
        catch (NoResultException e)
        {
            background = null;
        }

        assertNull("The background was not deleted", background);
    }

    /**
     * Test finding a person's background.
     */
    @Test
    public void testfindPersonBackground()
    {
        final String openSocialId = "2d359911-0977-418a-9490-57e8252b1a42";
        Background background = jpaBackgroundMapper.findPersonBackground(openSocialId);

        assertNotNull("No background found", background);
    }

    /**
     * Test finding a person's background.
     */
    @Test
    public void testfindPersonBackgroundItems()
    {
        final long personId = 142L;
        final String openSocialId = "2d359911-0977-418a-9490-57e8252b1142";
        Person person = jpaPersonMapper.findById(personId);
        Background background = new Background(person);

        jpaBackgroundMapper.insert(background);

        List<BackgroundItem> expectedBackgroundItems = new ArrayList<BackgroundItem>();
        expectedBackgroundItems.add(new BackgroundItem("sports", BackgroundItemType.INTEREST));
        expectedBackgroundItems.add(new BackgroundItem("music", BackgroundItemType.INTEREST));
        expectedBackgroundItems.add(new BackgroundItem("software", BackgroundItemType.INTEREST));
        background.setBackgroundItems(expectedBackgroundItems, BackgroundItemType.HONOR);

        jpaBackgroundMapper.getEntityManager().flush();
        jpaBackgroundMapper.getEntityManager().clear();

        background = jpaBackgroundMapper.findPersonBackground(openSocialId);

        assertNotNull("No background items found for person with id fordp", background
                .getBackgroundItems(BackgroundItemType.HONOR));

        // the list.toString() prints out the same thing though they are
        // different objects
        assertEquals(expectedBackgroundItems.toString(), background.getBackgroundItems(BackgroundItemType.HONOR)
                .toString());

    }

    /**
     * Test that null is returned when findPersonBackground() is called for a user that doesn't have a background.
     */
    @Test
    public void testFindPersonBackgroundWithNoData()
    {
        Background background = jpaBackgroundMapper.findPersonBackground("2d359911-0977-418a-9490-57e8252b1a98");

        assertNull(background);
    }

    /**
     * Test that findOrCreatePersonBackground() puts a Background in the database if one isn't there already.
     */
    @Test
    public void testFindOrCreatePersonBackground()
    {
        String openSocialId = "2d359911-0977-418a-9490-57e8252b1a98";

        Background background = jpaBackgroundMapper.findOrCreatePersonBackground(openSocialId);

        assertNotNull(background);

        Background confirmBg = jpaBackgroundMapper.findById(background.getId());

        assertEquals(background, confirmBg);
    }

    /**
     * Test that findOrCreatePersonBackground() loads the Background if it already exists.
     */
    @Test
    public void testFindOrCreatePersonBackgroundWithExistingBackground()
    {
        String openSocialId = "2d359911-0977-418a-9490-57e8252b1a42";

        Background background = jpaBackgroundMapper.findOrCreatePersonBackground(openSocialId);

        assertNotNull(background);

        Background confirmBg = jpaBackgroundMapper.findById(background.getId());

        assertEquals(background, confirmBg);
    }

    /**
     * Tests the findTopBackgroundItemsByType method.
     */
    @Test
    public void testFindTopBackgroundItemsByType()
    {
        // verify that it returns results.
        List<String> results = jpaBackgroundMapper.findBackgroundItemNamesByType(BackgroundItemType.SKILL, "skill", 5);
        assertEquals(3, results.size());

        // verify it's not case sensitive.
        results = jpaBackgroundMapper.findBackgroundItemNamesByType(BackgroundItemType.SKILL, "SkIlL", 5);
        assertEquals(3, results.size());

        // verify that it works for different type
        results = jpaBackgroundMapper.findBackgroundItemNamesByType(BackgroundItemType.HONOR, "honor", 5);
        assertEquals(3, results.size());

        // verify that it trims results
        results = jpaBackgroundMapper.findBackgroundItemNamesByType(BackgroundItemType.HONOR, "honor", 2);
        assertEquals(2, results.size());
    }

    /**
     * Tests the flush and index method.
     */
    @Test
    public void voidTestFlushAndIndex()
    {

        final long personId = 142L;
        final String openSocialId = "2d359911-0977-418a-9490-57e8252b1142";
        Person person = jpaPersonMapper.findById(personId);
        Background background = new Background(person);

        jpaBackgroundMapper.insert(background);

        List<BackgroundItem> expectedBackgroundItems = new ArrayList<BackgroundItem>();
        expectedBackgroundItems.add(new BackgroundItem("sports", BackgroundItemType.INTEREST));
        expectedBackgroundItems.add(new BackgroundItem("music", BackgroundItemType.INTEREST));
        expectedBackgroundItems.add(new BackgroundItem("software", BackgroundItemType.INTEREST));
        background.setBackgroundItems(expectedBackgroundItems, BackgroundItemType.HONOR);

        jpaBackgroundMapper.getEntityManager().flush();
        jpaBackgroundMapper.getEntityManager().clear();

        background = jpaBackgroundMapper.findPersonBackground(openSocialId);

        assertNotNull("No background items found for person with id fordp", background
                .getBackgroundItems(BackgroundItemType.HONOR));

        jpaBackgroundMapper.flush("2d359911-0977-418a-9490-57e8252b1142");

        // the list.toString() prints out the same thing though they are
        // different objects
        assertEquals(expectedBackgroundItems.toString(), background.getBackgroundItems(BackgroundItemType.HONOR)
                .toString());

    }
}
