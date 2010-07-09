/*
 * Copyright (c) 2009 Lockheed Martin Corporation
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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Transient;

import org.eurekastreams.commons.model.DomainEntity;
import org.hibernate.validator.Length;

/**
 * This class represents an instance of a gadget category.
 */
@SuppressWarnings("serial")
@Entity
public class GalleryItemCategory extends DomainEntity implements Serializable
{
    /**
     * Public constructor.
     */
    public GalleryItemCategory()
    {
    }

    /**
     * Default constructor responsible for assembling the gadget category.
     * 
     * @param inName
     *            the name
     */
    public GalleryItemCategory(final String inName)
    {
        name = inName;
    }

    /**
     * Max characters for tab name.
     */
    @Transient
    private final int maxGadgetCategoryNameLength = 50;

    /**
     * Store the value of the GadgetCategoryName.
     */
    @Basic(optional = false)
    @Length(min = 1, max = maxGadgetCategoryNameLength, message = "GadgetCategory name must be between 1 and "
            + maxGadgetCategoryNameLength + " characters.")
    private String name;

    /**
     * Background type.
     */
    @Enumerated(EnumType.STRING)
    private GalleryItemType galleryItemType = GalleryItemType.NOT_SET;

    /**
     * Get the name.
     * 
     * @return the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the name.
     * 
     * @param inName
     *            the category name.
     */
    public void setName(final String inName)
    {
        name = inName;
    }

    /**
     * Get the name.
     * 
     * This is used in conjunction with CollectionFormat.
     * 
     * @return the name.
     */
    public String toString()
    {
        return name;
    }

}
