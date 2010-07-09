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
 * This class represents an instance of a background item.
 */
@SuppressWarnings("serial")
@Entity
public class BackgroundItem extends DomainEntity implements Serializable
{
    /**
     * Public constructor.
     */
    public BackgroundItem()
    {
    }

    /**
     * Default constructor responsible for assembling the background item.
     *
     * @param inName
     *            the name
     * @param inBackgroundType
     *            the background type
     */
    public BackgroundItem(final String inName, final BackgroundItemType inBackgroundType)
    {
        name = inName;
        backgroundType = inBackgroundType;
    }

    /**
     * Enumeration for background types.
     *
     */
    /**
     * Max characters for tab name.
     */
    @Transient
    public static final int MAX_BACKGROUND_ITEM_NAME_LENGTH = 50;

    /**
     * Background type.
     */
    @Enumerated(EnumType.STRING)
    private BackgroundItemType backgroundType = BackgroundItemType.NOT_SET;

    /**
     * Store the value of the BackgroundItemName.
     */
    @Basic(optional = false)
    @Length(min = 1, max = MAX_BACKGROUND_ITEM_NAME_LENGTH, message = "BackgroundItem name must be between 1 and "
            + MAX_BACKGROUND_ITEM_NAME_LENGTH + " characters.")
    private String name;

    /**
     * Get the background type.
     *
     * @return the BackgroundType
     */
    public BackgroundItemType getBackgroundType()
    {
        return backgroundType;
    }

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
     * This is used in conjunction with CollectionFormat.
     *
     * @return the name.
     */
    @Override
    public String toString()
    {
        return name;
    }

    /**
     * Setter for serialization.
     *
     * @param inBackgroundType
     *            the backgroundType to set
     */
    private void setBackgroundType(final BackgroundItemType inBackgroundType)
    {
        backgroundType = inBackgroundType;
    }

    /**
     * Setter for serialization.
     *
     * @param inName
     *            the name to set
     */
    private void setName(final String inName)
    {
        name = inName;
    }

}
