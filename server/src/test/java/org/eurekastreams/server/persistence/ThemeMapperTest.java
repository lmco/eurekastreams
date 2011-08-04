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
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

import org.eurekastreams.server.domain.GalleryItemCategory;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.Theme;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is responsible for testing the JPA Implementation of the Theme Mapper interface. The tests contained in
 * here ensure proper interaction with the database.
 */
public class ThemeMapperTest extends DomainEntityMapperTest
{
    /**
     * This field holds the test instance of jpaTabMapper.
     */
    @Autowired
    private ThemeMapper jpaThemeMapper;

    /**
     * Test the persistence of a Tab.
     */
    @Test
    public void testInsert()
    {
        Theme t = new Theme("url", "name", "desc", "css", UUID.randomUUID().toString(), "bannerId", "authorName",
                "vswatter@gmail.com");
        jpaThemeMapper.insert(t);
        assertTrue(t.getId() > 0);
    }

    /**
     * Test the DBUnit XML Dataset - Theme.
     */
    @Test
    public void testFindByIdDataset()
    {
        Theme theme = jpaThemeMapper.findById(testThemeId);

        assertEquals("Name does not match.", "Test Theme", theme.getName());
        assertEquals("Theme URL File does not match.", "http://www.eurekastreams.org/theme.xml", theme.getUrl());
        assertEquals("Theme UUID does not match.", "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", theme.getUUID());
    }

    /**
     * Test the findOrCreate() method using a theme that is in the database.
     */
    @Test
    public void findByUrlWithExistingUrl()
    {
        Theme theme = jpaThemeMapper.findByUrl("http://www.eurekastreams.org/theme.xml");

        assertNotNull("Did not find the theme", theme);
        assertEquals("Theme id does not match, got the wrong theme.", testThemeId, theme.getId());
    }

    /**
     * Test the findOrCreate() method using a theme that is in the database.
     */
    @Test
    public void findByUrlWithNonExistingUrl()
    {
        Theme theme = jpaThemeMapper.findByUrl("http://www.eurekastreams.org/nonexistenttheme.xml");

        assertEquals("found the theme , but it should not exist", null, theme);
    }

    /**
     * Utility to method to ensure a created theme was built and persisted correctly.
     * 
     * @param theme
     *            the created theme
     * @param themeURL
     *            the expected URL to the theme's XML definition
     * @param cssPath
     *            the expected URL to the theme's CSS
     * @param themeName
     *            the expected name for the theme
     * @throws MalformedURLException
     *             thrown if the CSS URL is not valid
     */
    private void verifyCreatedTheme(final Theme theme, final String themeURL, final String cssPath,
            final String themeName) throws MalformedURLException
    {
        // Make sure the theme was build correctly
        assertNotNull("Did not create the theme", theme);
        assertEquals("Theme URL does not match", themeURL, theme.getUrl());

        // assertEquals("Did not build the CSS path correctly", cssPath, theme.getCssFile());
        assertEquals("Did not set the name correctly", themeName, theme.getName());

        // Make sure the theme was persisted
        Theme foundTheme = jpaThemeMapper.findById(theme.getId());

        assertEquals("Created theme did not match looked-up theme", foundTheme, theme);
    }

    /**
     * Look for a theme known to exist (present in dataset.xml).
     */
    @Test
    public void findByUUIDWithKnownUUID()
    {
        Theme theme = jpaThemeMapper.findByUUID("f81d4fae-7dec-11d0-a765-00a0c91e6bf6");

        assertNotNull("Did not find the theme and did not throw exception", theme);
        assertEquals("Theme id does not match", testThemeId, theme.getId());
    }

    /**
     * Look for a theme known not to exist (not present in dataset.xml).
     */
    @Test(expected = NoResultException.class)
    public void findByUUIDWithUnknownUUID()
    {
        jpaThemeMapper.findByUUID("12345678-90abcde12-3456-7890abcde123");
    }

    /**
     * Look for the default theme.
     */
    @Test
    public void findDefault()
    {
        Theme theme = jpaThemeMapper.findDefault();

        assertNotNull("Did not find the default theme and did not throw exception", theme);
        assertEquals("Theme id does not match", testThemeId, theme.getId());
    }

    /**
     * Tests the findSortedThemesForCategory method.
     */
    @Test
    public void testFindThemesForCategorySortedByPopularity()
    {
        // verify that it returns results.
        PagedSet<Theme> results = jpaThemeMapper.findForCategorySortedByPopularity("City", 0, 1);

        assertEquals(2, results.getPagedSet().size());
        int firstNumberOfUsers = results.getPagedSet().get(0).getNumberOfUsers();
        int secondNumberOfUsers = results.getPagedSet().get(1).getNumberOfUsers();

        assertTrue("first theme does not have more users than second theme", firstNumberOfUsers > secondNumberOfUsers);
    }

    /**
     * Tests the findSortedThemesForCategory method.
     */
    @Test
    public void testFindThemesForCategorySortedByRecent()
    {
        // verify that it returns results.
        PagedSet<Theme> results = jpaThemeMapper.findForCategorySortedByRecent("City", 0, 1);
        assertEquals(2, results.getPagedSet().size());
        Date firstDate = results.getPagedSet().get(0).getCreatedDate();
        Date secondDate = results.getPagedSet().get(1).getCreatedDate();

        assertTrue(firstDate.after(secondDate));
    }

    /**
     * Tests that the findSortedThemesSortedByRecentForCategory method returns only gadget defs of the specified
     * category.
     */
    @Test
    public void testFindThemesForCategorySortedByRecentReturnsRightCategory()
    {
        // verify that it returns results.
        PagedSet<Theme> results = jpaThemeMapper.findForCategorySortedByRecent("City", 0, 1);
        assertEquals(2, results.getPagedSet().size());
        GalleryItemCategory firstCategory = results.getPagedSet().get(0).getCategory();
        GalleryItemCategory secondCategory = results.getPagedSet().get(1).getCategory();

        assertEquals(firstCategory.getName(), "City");
        assertEquals(secondCategory.getName(), "City");
    }

    /**
     * Tests that the findSortedThemesSortedByPopularityForCategory method returns only gadget defs of the specified
     * category.
     */
    @Test
    public void testFindThemesForCategorySortedByPopularityReturnsRightCategory()
    {
        // verify that it returns results.
        PagedSet<Theme> results = jpaThemeMapper.findForCategorySortedByPopularity("Seasonal", 0, 1);
        assertEquals(2, results.getPagedSet().size());
        GalleryItemCategory firstCategory = results.getPagedSet().get(0).getCategory();
        GalleryItemCategory secondCategory = results.getPagedSet().get(1).getCategory();

        assertEquals(firstCategory.getName(), "Seasonal");
        assertEquals(secondCategory.getName(), "Seasonal");
    }

    /**
     * Tests that the findSortedThemesSortedByPopularityForCategory method returns gadget defs of any category when
     * category parameter is empty.
     */
    @Test
    public void testFindThemesForCategorySortedByPopularityReturnsAll()
    {
        // verify that it returns results.
        PagedSet<Theme> results = jpaThemeMapper.findSortedByPopularity(0, 3);
        assertEquals(4, results.getPagedSet().size());
    }

    /**
     * Tests that the findSortedThemesSortedByPopularityForCategory method returns gadget defs of any category when
     * category parameter is empty.
     */
    @Test
    public void testFindThemesForCategorySortedByRecentReturnsAll()
    {
        // verify that it returns results.
        PagedSet<Theme> results = jpaThemeMapper.findSortedByRecent(0, 3);
        assertEquals(4, results.getPagedSet().size());
    }

    /**
     * Test deleting a theme.
     */
    @Test
    public void testDelete()
    {
        Theme theme = null;
        final long themeId = 103L;
        theme = jpaThemeMapper.findById(themeId);
        jpaThemeMapper.delete(theme);

        try
        {
            theme = jpaThemeMapper.findById(themeId);
        }
        catch (NoResultException e)
        {
            theme = null;
        }

        catch (EntityNotFoundException e)
        {
            theme = null;
        }

        assertTrue("The theme was not deleted", theme == null);
    }
}
