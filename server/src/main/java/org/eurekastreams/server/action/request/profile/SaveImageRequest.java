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
package org.eurekastreams.server.action.request.profile;

import java.io.Serializable;

import org.apache.commons.fileupload.FileItem;

/**
 *Request object for saving an image.
 *
 */
public class SaveImageRequest implements Serializable
{

    /**
     * Serial version id.
     */
    private static final long serialVersionUID = -2136293107515756981L;

    /**
     * Entity id.
     */
    private Long entityId = null;

    /**
     * Image id.
     */
    private String imageId = null;

    /**
     * The file itself.
     */
    private FileItem fileItem;

    /**
     * Constructor (serialization).
     */
    @SuppressWarnings("unused")
    private SaveImageRequest()
    {
        // no-op
    }

    /**
     * Constructor.
     *
     * @param inFileItem the file item
     * @param inEntityId
     *            Entity id.
     * @param inImageId
     *            Avatar id.
     */
    public SaveImageRequest(final FileItem inFileItem, final Long inEntityId, final String inImageId)
    {
        fileItem = inFileItem;
        entityId = inEntityId;
        imageId = inImageId;
    }

    /**
     * @return the entityId
     */
    public Long getEntityId()
    {
        return entityId;
    }

    /**
     * @param inEntityId
     *            the entityId to set
     */
    public void setEntityId(final Long inEntityId)
    {
        entityId = inEntityId;
    }

    /**
     * @return the imageId
     */
    public String getImageId()
    {
        return imageId;
    }

    /**
     * @param inImageId
     *            the imageId to set
     */
    public void setImageId(final String inImageId)
    {
        imageId = inImageId;
    }

    /**
     * File item.
     * @return the file item.
     */
    public FileItem getFileItem()
    {
        return fileItem;
    }

}
