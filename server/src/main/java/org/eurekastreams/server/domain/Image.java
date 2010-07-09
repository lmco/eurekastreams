/*
 * Copyright (c) 2010 Lockheed Martin Corporation
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

import javax.persistence.Column;
import javax.persistence.Entity;

import org.eurekastreams.commons.model.DomainEntity;

/**
 * Images in the database.
 *
 */
@SuppressWarnings("serial")
@Entity
public class Image extends DomainEntity implements Serializable
{
    /**
     * The image identifier is a random string that helps to identify the image.
     * We don't use the database ID as we are able to "generate" 3 image IDs from
     * one static random string by prepending "n", "o", and "s" for normal,
     * original, and small versions.
     */
    @Column(nullable = false)
    private String imageIdentifier;
    /**
     * The image bytes.
     */
    @Column(nullable = false)
    private byte[] imageBlob;

    /**
     * Default constructor.
     */
    private Image()
    {

    }
    /**
     * Constructor with identifier and blob.
     * @param inImageIdentifier the identifier.
     * @param inImageBlob the blob.
     */
    public Image(final String inImageIdentifier, final byte[] inImageBlob)
    {
        imageIdentifier = inImageIdentifier;
        imageBlob = inImageBlob;
    }

    /**
     * Gets the image identifier.
     * @return the identifier.
     */
    public String getImageIdentifier()
    {
        return imageIdentifier;
    }

    /**
     * Sets the image identifier.
     * @param inImageIdentifier the identifier.
     */
    public void setImageIdentifier(final String inImageIdentifier)
    {
        imageIdentifier = inImageIdentifier;
    }

    /**
     * Gets the image blob.
     * @return the image blob.
     */
    public byte[] getImageBlob()
    {
        return imageBlob;
    }

    /**
     * Sets the image blob.
     * @param inImageBlob the image blob.
     */
    public void setImageBlob(final byte[] inImageBlob)
    {
        imageBlob = inImageBlob;
    }
}
