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

import java.util.HashMap;

import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.PagedSet;
import org.eurekastreams.server.domain.dto.GalleryTabTemplateDTO;

/**
 * GalleryTabTemplate Mapper.
 * 
 */
public class GalleryTabTemplateMapper extends DomainEntityMapper<GalleryTabTemplateDTO> implements
        GalleryItemMapper<GalleryTabTemplateDTO>
{
    /**
     * Constructor.
     * 
     * @param inQueryOptimizer
     *            QueryOptimizer.
     */
    public GalleryTabTemplateMapper(final QueryOptimizer inQueryOptimizer)
    {
        super(inQueryOptimizer);
    }

    @Override
    protected String getDomainEntityName()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(final GalleryTabTemplateDTO inGalleryItem)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public GalleryTabTemplateDTO findByUrl(final String inGalleryItemUrl)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public PagedSet<GalleryTabTemplateDTO> findForCategorySortedByPopularity(final String inCategory,
            final int inStart, final int inEnd)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("category", inCategory);

        String q = "SELECT NEW org.eurekastreams.server.domain.dto.GalleryTabTemplateDTO "
                + "(id, created, description, title, category.id, category.galleryItemType, category.name) "
                + "FROM GalleryTabTemplate WHERE category.name = :category ORDER BY size(tabTemplates) DESC";

        return getPagedResults(inStart, inEnd, q, parameters);
    }

    @Override
    public PagedSet<GalleryTabTemplateDTO> findForCategorySortedByRecent(final String inCategory, final int inStart,
            final int inEnd)
    {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("category", inCategory);

        String q = "SELECT NEW org.eurekastreams.server.domain.dto.GalleryTabTemplateDTO "
                + "(id, created, description, title, category.id, category.galleryItemType, category.name) "
                + "FROM GalleryTabTemplate WHERE category.name = :category ORDER BY created DESC";

        return getPagedResults(inStart, inEnd, q, parameters);
    }

    @Override
    public PagedSet<GalleryTabTemplateDTO> findSortedByPopularity(final int inStart, final int inEnd)
    {
        String q = "SELECT NEW org.eurekastreams.server.domain.dto.GalleryTabTemplateDTO "
                + "(id, created, description, title, category.id, category.galleryItemType, category.name) "
                + "FROM GalleryTabTemplate ORDER BY size(tabTemplates) DESC";

        return getPagedResults(inStart, inEnd, q, new HashMap<String, Object>());
    }

    @Override
    public PagedSet<GalleryTabTemplateDTO> findSortedByRecent(final int inStart, final int inEnd)
    {
        String q = "SELECT NEW org.eurekastreams.server.domain.dto.GalleryTabTemplateDTO "
                + "(id, created, description, title, category.id, category.galleryItemType, category.name) "
                + "FROM GalleryTabTemplate ORDER BY created DESC";

        return getPagedResults(inStart, inEnd, q, new HashMap<String, Object>());
    }

    @Override
    public void insert(final GalleryTabTemplateDTO inDomainEntity)
    {
        throw new UnsupportedOperationException();

    }

    @Override
    public void refresh(final GalleryTabTemplateDTO inGalleryItem)
    {
        throw new UnsupportedOperationException();

    }

    @Override
    public GalleryTabTemplateDTO findById(final Long inGalleryItemId)
    {
        throw new UnsupportedOperationException();
    }
}
