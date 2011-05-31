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

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.eurekastreams.commons.model.DomainEntity;

/**
 * Membership criteria.
 */
@Entity
public class MembershipCriteria extends DomainEntity implements Serializable
{
    /**
     * Serial version uid.
     */
    private static final long serialVersionUID = -6648003009881047137L;

    /**
     * The criteria.
     */
    @Basic(optional = false)
    private String criteria = "";

    /**
     * This field will maintain a link to the optional corresponding galleryTabTemplate.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @Basic
    @JoinColumn(name = "galleryTabTemplateId")
    private GalleryTabTemplate galleryTabTemplate;

    /**
     * This field will maintain a link to the optional corresponding galleryTabTemplate.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @Basic
    @JoinColumn(name = "themeId")
    private Theme theme;

    /**
     * Constructor, used for serialization.
     */
    public MembershipCriteria()
    {
    }

    /**
     * Constructor.
     * 
     * @param inCriteria
     *            The criteria.
     */
    public MembershipCriteria(final String inCriteria)
    {
        criteria = inCriteria;
    }

    /**
     * Set the criteria.
     * 
     * @param inCriteria
     *            the criteria.
     */
    public void setCriteria(final String inCriteria)
    {
        criteria = inCriteria;
    }

    /**
     * Get the criteria.
     * 
     * @return the criteria.
     */
    public String getCriteria()
    {
        return criteria;
    }

    /**
     * @return the galleryTabTemplate
     */
    public GalleryTabTemplate getGalleryTabTemplate()
    {
        return galleryTabTemplate;
    }

    /**
     * @param inGalleryTabTemplate
     *            the galleryTabTemplate to set
     */
    public void setGalleryTabTemplate(final GalleryTabTemplate inGalleryTabTemplate)
    {
        galleryTabTemplate = inGalleryTabTemplate;
    }

    /**
     * @return the theme
     */
    public Theme getTheme()
    {
        return theme;
    }

    /**
     * @param inTheme
     *            the theme to set
     */
    public void setTheme(final Theme inTheme)
    {
        theme = inTheme;
    }
}
