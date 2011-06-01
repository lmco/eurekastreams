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

/**
 * DTO object for MembershipCriteria.
 */
public class MembershipCriteriaDTO implements Serializable
{
    /**
     * Serial version id.
     */
    private static final long serialVersionUID = -2874513974555151098L;

    /**
     * MembershipCriteria id.
     */
    private Long id;

    /**
     * The criteria.
     */
    private String criteria;

    /**
     * GalleryTabTemplate id.
     */
    private Long galleryTabTemplateId;

    /**
     * GalleryTabTemplate name.
     */
    private String galleryTabTemplateName;

    /**
     * Theme id.
     */
    private Long themeId;

    /**
     * Theme name.
     */
    private String themeName;

    /**
     * Constructor.
     */
    public MembershipCriteriaDTO()
    {
        // no-op.
    }

    /**
     * @param inId
     *            The MembershipCriteria id.
     * @param inCriteria
     *            The criteria.
     * @param inGalleryTabTemplateId
     *            GalleryTabTemplate id.
     * @param inGalleryTabTemplateName
     *            GalleryTabTemplate name.
     * @param inThemeId
     *            Theme id.
     * @param inThemeName
     *            Theme name.
     */
    public MembershipCriteriaDTO(final Long inId, final String inCriteria, final Long inGalleryTabTemplateId,
            final String inGalleryTabTemplateName, final Long inThemeId, final String inThemeName)
    {
        id = inId;
        criteria = inCriteria;
        galleryTabTemplateId = inGalleryTabTemplateId;
        galleryTabTemplateName = inGalleryTabTemplateName;
        themeId = inThemeId;
        themeName = inThemeName;

    }

    /**
     * @return the criteria
     */
    public String getCriteria()
    {
        return criteria;
    }

    /**
     * @param inCriteria
     *            the criteria to set
     */
    public void setCriteria(final String inCriteria)
    {
        criteria = inCriteria;
    }

    /**
     * @return the galleryTabTemplateId
     */
    public Long getGalleryTabTemplateId()
    {
        return galleryTabTemplateId;
    }

    /**
     * @param inGalleryTabTemplateId
     *            the galleryTabTemplateId to set
     */
    public void setGalleryTabTemplateId(final Long inGalleryTabTemplateId)
    {
        galleryTabTemplateId = inGalleryTabTemplateId;
    }

    /**
     * @return the galleryTabTemplateName
     */
    public String getGalleryTabTemplateName()
    {
        return galleryTabTemplateName;
    }

    /**
     * @param inGalleryTabTemplateName
     *            the galleryTabTemplateName to set
     */
    public void setGalleryTabTemplateName(final String inGalleryTabTemplateName)
    {
        galleryTabTemplateName = inGalleryTabTemplateName;
    }

    /**
     * @return the themeId
     */
    public Long getThemeId()
    {
        return themeId;
    }

    /**
     * @param inThemeId
     *            the themeId to set
     */
    public void setThemeId(final Long inThemeId)
    {
        themeId = inThemeId;
    }

    /**
     * @return the themeName
     */
    public String getThemeName()
    {
        return themeName;
    }

    /**
     * @param inThemeName
     *            the themeName to set
     */
    public void setThemeName(final String inThemeName)
    {
        themeName = inThemeName;
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

}
