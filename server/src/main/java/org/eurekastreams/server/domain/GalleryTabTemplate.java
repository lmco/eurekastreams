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
package org.eurekastreams.server.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.eurekastreams.commons.model.DomainEntity;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.validator.Length;

/**
 * Represents a TabTemplate that has been added to gallery.
 * 
 */
@Entity
public class GalleryTabTemplate extends DomainEntity
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = -7759515836967572647L;

    /**
     * Max characters for description.
     */
    @Transient
    private final int maxDescriptionLength = 200;

    /**
     * GalleryTabTemplate creation date.
     */
    @Basic(optional = false)
    @Temporal(TemporalType.TIMESTAMP)
    @DateBridge(resolution = Resolution.SECOND)
    private Date created;

    /**
     * GalleryTabTemplate title.
     */
    @Basic(optional = false)
    private String title;

    /**
     * GalleryTabTemplate description.
     */
    @Basic(optional = false)
    @Length(min = 1, max = maxDescriptionLength, message = "Description supports up to " + maxDescriptionLength
            + " characters.")
    private String description;

    /**
     * The GalleryTabTemplate category.
     */
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoryId")
    @Basic(optional = false)
    private GalleryItemCategory category;

    /**
     * TabTemplate the GalleryTabTemplate is based on.
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinColumn(name = "tabTemplateId")
    @Basic(optional = false)
    private TabTemplate tabTemplate;

    /**
     * TabTemplates based on this GalleryTabTemplate.
     */
    @OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE })
    @JoinColumn(name = "galleryTabTemplateId")
    private List<TabTemplate> tabTemplates;

    /**
     * Required empty constructor.
     */
    public GalleryTabTemplate()
    {
        // no-op;
    }

    /**
     * Constructor.
     * 
     * @param inDescription
     *            Description.
     * @param inCategory
     *            Category.
     * @param inTabTemplate
     *            TabTemplate.
     */
    public GalleryTabTemplate(final String inDescription, final GalleryItemCategory inCategory,
            final TabTemplate inTabTemplate)
    {
        description = inDescription;
        category = inCategory;
        tabTemplate = inTabTemplate;
        title = tabTemplate.getTabName();
    }

    /**
     * sets the created attribute.
     */
    @PrePersist
    protected void onCreate()
    {
        created = new Date();
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
    public GalleryItemCategory getCategory()
    {
        return category;
    }

    /**
     * @param inCategory
     *            the category to set
     */
    public void setCategory(final GalleryItemCategory inCategory)
    {
        category = inCategory;
    }

    /**
     * @return the tabTemplate
     */
    public TabTemplate getTabTemplate()
    {
        return tabTemplate;
    }

    /**
     * @param inTabTemplate
     *            the tabTemplate to set
     */
    public void setTabTemplate(final TabTemplate inTabTemplate)
    {
        tabTemplate = inTabTemplate;
    }

    /**
     * @return the tabTemplates
     */
    public List<TabTemplate> getTabTemplates()
    {
        return tabTemplates;
    }

    /**
     * @param inTabTemplates
     *            the tabTemplates to set
     */
    public void setTabTemplates(final List<TabTemplate> inTabTemplates)
    {
        tabTemplates = inTabTemplates;
    }

}
