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
package org.eurekastreams.server.domain.dto;

import java.io.Serializable;
import java.util.Date;

import org.eurekastreams.server.domain.GalleryItemType;

/**
 * DTO for GalleryTabTemplate.
 */
public class GalleryTabTemplateDTO implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = -7026883827840753403L;

    /**
     * Id.
     */
    private Long id;

    /**
     * GalleryTabTemplate title.
     */
    private String title;

    /**
     * GalleryTabTemplate description.
     */
    private String description;

    /**
     * The GalleryTabTemplate category.
     */
    private GalleryItemCategoryDTO category;

    /**
     * GalleryTabTemplate creation date.
     */
    private Date created;

    /**
     * Number of tab templates that were created from this galleryTabTemplate.
     */
    private Long childTabTemplateCount = 0L;

    /**
     * Constructor.
     */
    public GalleryTabTemplateDTO()
    {

    }

    /**
     * Constructor.
     * 
     * @param inId
     *            Id.
     * @param inCreated
     *            Created date.
     * @param inDescription
     *            Description.
     * @param inTitle
     *            Title.
     * @param inCategoryId
     *            Category id.
     * @param inCategoryGalleryItemType
     *            Category type.
     * @param inCategoryName
     *            Category name.
     */
    public GalleryTabTemplateDTO(final Long inId, final Date inCreated, final String inDescription,
            final String inTitle, final Long inCategoryId, final GalleryItemType inCategoryGalleryItemType,
            final String inCategoryName)
    {
        id = inId;
        created = inCreated;
        description = inDescription;
        title = inTitle;
        category = new GalleryItemCategoryDTO(inCategoryId, inCategoryGalleryItemType, inCategoryName);
    }

    /**
     * @return the id
     */
    public Long getId()
    {
        return id;
    }

    /**
     * @param inId
     *            the id to set
     */
    public void setId(final Long inId)
    {
        id = inId;
    }

    /**
     * @return the title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * @param inTitle
     *            the title to set
     */
    public void setTitle(final String inTitle)
    {
        title = inTitle;
    }

    /**
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param inDescription
     *            the description to set
     */
    public void setDescription(final String inDescription)
    {
        description = inDescription;
    }

    /**
     * @return the category
     */
    public GalleryItemCategoryDTO getCategory()
    {
        return category;
    }

    /**
     * @param inCategory
     *            the category to set
     */
    public void setCategory(final GalleryItemCategoryDTO inCategory)
    {
        category = inCategory;
    }

    /**
     * @return the created
     */
    public Date getCreated()
    {
        return created;
    }

    /**
     * @param inCreated
     *            the created to set
     */
    public void setCreated(final Date inCreated)
    {
        created = inCreated;
    }

    /**
     * @return the childTabTemplateCount
     */
    public Long getChildTabTemplateCount()
    {
        return childTabTemplateCount;
    }

    /**
     * @param inChildTabTemplateCount
     *            the childTabTemplateCount to set
     */
    public void setChildTabTemplateCount(final Long inChildTabTemplateCount)
    {
        childTabTemplateCount = inChildTabTemplateCount;
    }

}
