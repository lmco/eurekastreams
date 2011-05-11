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

import org.eurekastreams.commons.hibernate.QueryOptimizer;
import org.eurekastreams.server.domain.GalleryTabTemplate;
import org.eurekastreams.server.domain.PagedSet;

/**
 * GalleryTabTemplate Mapper.
 * 
 */
public class GalleryTabTemplateMapper extends DomainEntityMapper<GalleryTabTemplate> implements
        GalleryItemMapper<GalleryTabTemplate>
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
        // TODO Auto-generated constructor stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getDomainEntityName()
    {
        return "GalleryTabTemplate";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(final GalleryTabTemplate inGalleryItem)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GalleryTabTemplate findByUrl(final String inGalleryItemUrl)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PagedSet<GalleryTabTemplate> findForCategorySortedByPopularity(final String inCategory, final int inStart,
            final int inEnd)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PagedSet<GalleryTabTemplate> findForCategorySortedByRecent(final String inCategory, final int inStart,
            final int inEnd)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PagedSet<GalleryTabTemplate> findSortedByPopularity(final int inStart, final int inEnd)
    {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PagedSet<GalleryTabTemplate> findSortedByRecent(final int inStart, final int inEnd)
    {
        throw new UnsupportedOperationException();
    }
}
