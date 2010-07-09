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

import java.util.List;

import javax.persistence.Query;

import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.GalleryItemCategory;
import org.eurekastreams.server.domain.GalleryItemType;

/**
 * This class provides the mapper functionality for GalleryItemCategory entities.
 */
public class GalleryItemCategoryMapper extends DomainEntityMapper<GalleryItemCategory>
{
    /**
     * Constructor.
     *
     * @param inQueryOptimizer
     *            the QueryOptimizer to use for specialized functions.
     */
    public GalleryItemCategoryMapper(final QueryOptimizer inQueryOptimizer)
    {
        super(inQueryOptimizer);
    }

    /**
     * Get the domain entity name for the generic query operations.
     *
     * @return the domain entity name for the gadget query operations.
     */
    @Override
    protected String getDomainEntityName()
    {
        return "GalleryItemCategory";
    }

    /**
     * Find by name.
     *
     * @param inGalleryItemType
     *            the type of the gallery item
     * @param inName
     *            the name of the category to search for
     * @return the category name.
     */
    @SuppressWarnings("unchecked")
    public GalleryItemCategory findByName(final GalleryItemType inGalleryItemType, final String inName)
    {
        Query q = getEntityManager().createQuery("from GalleryItemCategory where "
                + "galleryItemType = :galleryItemType and name = :inName")
            .setParameter("galleryItemType", inGalleryItemType)
            .setParameter("inName", inName);

        List results = q.getResultList();

        return (results.size() == 0) ? null : (GalleryItemCategory) results.get(0);
    }

    /**
     * Find the GalleryItem Categories.
     *
     * @param inGalleryItemType
     *            the type of gallery item (e.g., gadget, theme)
     * @return list of GalleryItemCategory
     */
    @SuppressWarnings("unchecked")
    public List<GalleryItemCategory> findGalleryItemCategories(final GalleryItemType inGalleryItemType)
    {
        Query q = getEntityManager().createQuery(
                "from GalleryItemCategory" + " where galleryItemType = :galleryItemType")
        .setParameter("galleryItemType", inGalleryItemType);

        return q.getResultList();
    }
}
