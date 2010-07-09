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

/**
 * Request object for resizing an avatar image.
 * 
 */
public class ResizeAvatarRequest implements Serializable
{
    /**
     * Serialization version Id.
     */
    private static final long serialVersionUID = -5008813036135082586L;

    /**
     * x coordinate.
     */
    private Integer x = null;

    /**
     * y coordinate.
     */
    private Integer y = null;

    /**
     * Size.
     */
    private Integer size = null;

    /**
     * If version of avatars should be refreshed.
     */
    private Boolean refreshFiles = false;

    /**
     * Id of entity whose avatar is being resized.
     */
    private Long entityId = null;

    /**
     * Constructor (serialization).
     */
    @SuppressWarnings("unused")
    private ResizeAvatarRequest()
    {
        // no-op
    }

    /**
     * Constructor.
     * 
     * @param inX
     *            x coordinate.
     * @param inY
     *            y cooridnate.
     * @param inSize
     *            size.
     * @param inRefreshFiles
     *            If version of avatars should be refreshed.
     * @param inEntityId
     *            Id of entity whose avatar is being resized.
     */
    public ResizeAvatarRequest(final Integer inX, final Integer inY, final Integer inSize,
            final Boolean inRefreshFiles, final Long inEntityId)
    {
        x = inX;
        y = inY;
        size = inSize;
        refreshFiles = inRefreshFiles;
        entityId = inEntityId;
    }

    /**
     * @return the x
     */
    public Integer getX()
    {
        return x;
    }

    /**
     * @param inX
     *            the x to set
     */
    public void setX(final Integer inX)
    {
        x = inX;
    }

    /**
     * @return the y
     */
    public Integer getY()
    {
        return y;
    }

    /**
     * @param inY
     *            the y to set
     */
    public void setY(final Integer inY)
    {
        y = inY;
    }

    /**
     * @return the size
     */
    public Integer getSize()
    {
        return size;
    }

    /**
     * @param inSize
     *            the size to set
     */
    public void setSize(final Integer inSize)
    {
        size = inSize;
    }

    /**
     * @return the refreshFiles
     */
    public Boolean getRefreshFiles()
    {
        return refreshFiles;
    }

    /**
     * @param inRefreshFiles
     *            the refreshFiles to set
     */
    public void setRefreshFiles(final Boolean inRefreshFiles)
    {
        refreshFiles = inRefreshFiles;
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

}
