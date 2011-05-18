/*
 * Copyright (c) 2011 Lockheed Martin Corporation
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

import java.util.List;

import org.eurekastreams.server.domain.dto.GalleryTabTemplateDTO;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for GalleryTabTemplateMapper.
 * 
 */
public class GalleryTabTemplateMapperTest extends DomainEntityMapperTest
{
    /**
     * System under test.
     */
    private GalleryTabTemplateMapper sut = new GalleryTabTemplateMapper(null);

    /**
     * Set up.
     */
    @Before
    public void setup()
    {
        sut.setEntityManager(getEntityManager());
    }

    /**
     * Test.
     */
    @Test
    public void testFindSortedByRecent()
    {
        List<GalleryTabTemplateDTO> result = sut.findSortedByRecent(0, 5).getPagedSet();
        assertEquals(2, result.size());
        assertEquals("2", result.get(0).getId().toString());
        assertEquals("1", result.get(1).getId().toString());
    }

    /**
     * Test.
     */
    @Test
    public void testFindForCategorySortedByRecent()
    {
        List<GalleryTabTemplateDTO> result = sut.findForCategorySortedByRecent("tc1", 0, 5).getPagedSet();
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId().toString());
    }

    /**
     * Test.
     */
    @Test
    public void testFindSortedByPopular()
    {
        // null out galleryTabTemplate ids and then set only 1
        getEntityManager().createQuery("update TabTemplate set galleryTabTemplateId = null").executeUpdate();
        getEntityManager().createQuery("update TabTemplate set galleryTabTemplateId = 2 where id = 1097")
                .executeUpdate();

        // verify that it's correct.
        List<GalleryTabTemplateDTO> result = sut.findSortedByPopularity(0, 5).getPagedSet();
        assertEquals(2, result.size());
        assertEquals("2", result.get(0).getId().toString());
        assertEquals("1", result.get(1).getId().toString());

        // now do same, but swap galleryTabTemplate ids to verify it wasn't just default db ordering.
        getEntityManager().createQuery("update TabTemplate set galleryTabTemplateId = null").executeUpdate();
        getEntityManager().createQuery("update TabTemplate set galleryTabTemplateId = 1 where id = 1097")
                .executeUpdate();

        result = sut.findSortedByPopularity(0, 5).getPagedSet();
        assertEquals(2, result.size());
        assertEquals("1", result.get(0).getId().toString());
        assertEquals("2", result.get(1).getId().toString());
    }

    /**
     * Test.
     */
    @Test
    public void testFindForCategorySortedByPopular()
    {
        List<GalleryTabTemplateDTO> result = sut.findForCategorySortedByPopularity("tc1", 0, 5).getPagedSet();
        assertEquals(1, result.size());
        assertEquals("1", result.get(0).getId().toString());
    }

    /**
     * Test.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetDomainEntityName()
    {
        sut.getDomainEntityName();
    }

    /**
     * Test.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testFindByUrl()
    {
        sut.findByUrl(null);
    }

    /**
     * Test.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testInsert()
    {
        sut.insert(null);
    }

    /**
     * Test.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testRefresh()
    {
        sut.refresh(null);
    }

    /**
     * Test.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testFindByIdInt()
    {
        sut.findById(5);
    }

    /**
     * Test.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testFindByIdLong()
    {
        sut.findById(5L);
    }

}
