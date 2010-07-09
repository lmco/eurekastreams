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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.eurekastreams.server.domain.GalleryItemCategory;
import org.eurekastreams.server.domain.GalleryItemType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is responsible for testing the JPA Implementation of the GalleryItemCategory Mapper interface. The tests
 * contained in here ensure proper interaction with the database.
 */
public class GalleryItemCategoryMapperTest extends DomainEntityMapperTest
{
    /**
     * JpaGalleryItemCategoryMapper - system under test.
     */
    @Autowired
    private GalleryItemCategoryMapper jpaGalleryItemCategoryMapper;

    /**
     * Dataset org name.
     */
    private String name = "News"; // from dataset.xml

    /**
     * Test FindByName.
     */
    @Test
    public void testFindByNameSuccess()
    {
        final long expectedId = 4565; // from dataset.
        assertEquals(expectedId, jpaGalleryItemCategoryMapper.findByName(GalleryItemType.GADGET, name).getId());
    }

    /**
     * Test FindByName.
     */
    @Test
    public void testFindByNameFailure()
    {
        final long expectedId = 4565; // from dataset.
        assertEquals(expectedId, jpaGalleryItemCategoryMapper.findByName(GalleryItemType.GADGET, name).getId());

        assertNull("Object should not have been found, " + "expected null", jpaGalleryItemCategoryMapper.findByName(
                GalleryItemType.GADGET, "blahWhatever"));
    }

    /**
     * Test finding a person's GalleryItemCategory.
     */
    @Test
    public void testFindGalleryItemCategories()
    {
        List<GalleryItemCategory> gadgetCategories =
                jpaGalleryItemCategoryMapper.findGalleryItemCategories(GalleryItemType.GADGET);

        assertTrue("No GalleryItemCategories found", gadgetCategories.size() > 0);
    }

}
