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
package org.eurekastreams.web.client.ui.common.form.elements.avatar.strategies;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.web.client.events.ClearUploadedImageEvent.ImageType;
import org.eurekastreams.web.client.model.Deletable;

import com.google.gwt.user.client.ui.Widget;

/**
 * Image upload strategy to work with the generic image uploader.
 * 
 * @param <T>
 *            Type of entity.
 */
public interface ImageUploadStrategy<T>
{

    /**
     * Gets the delete action key.
     * @return the delete action key.
     */
    @SuppressWarnings("unchecked")
    Deletable getDeleteAction();
    /**
     * Gets the param to send to the delete action.
     * @return the param.
     */
    Long getDeleteParam();

    /**
     * Gets the id of the entity.
     *
     * @return the id.
     */
    Long getId();

    /**
     * Gets whether or not the strategy is resizable.
     *
     * @return the value.
     */
    Boolean isResizable();

    /**
     * Gets the image id.
     *
     * @return the image id.
     */
    String getImageId();

    /**
     * Gets the entity id associated with the image id.  This could be
     * different than the entity's id itself because the banner could be
     * of the parent org.
     * @return long entity id associated with the owner of the configured image id.
     */
    Long getImageEntityId();

    /**
     * Sets the X coord of the image crop.
     *
     * @param x
     *            the x coord.
     */
    void setX(Integer x);

    /**
     * Sets the y coord of the image crop.
     *
     * @param y
     *            the y coord.
     */
    void setY(Integer y);

    /**
     * sets the crop size.
     *
     * @param size
     *            the crop size.
     */
    void setCropSize(Integer size);

    /**
     * gets the crop X.
     *
     * @return the X.
     */
    Integer getX();

    /**
     * gets the crop Y.
     *
     * @return the crop y.
     */
    Integer getY();

    /**
     * Gets the crop size.
     *
     * @return the crop size.
     */
    Integer getCropSize();

    /**
     * Gets the image.
     *
     * @param imageId
     *            the image id.
     * @return the image.
     */
    Widget getImage(String imageId);

    /**
     * Gets the resize action key.
     * @return the resize action key.
     */
    String getResizeAction();
    /**
     * Get the entity type.
     * @return the entity type.
     */
    EntityType getEntityType();
    /**
     * Get the image type.
     * @return the image type.
     */
    ImageType getImageType();

    /**
     * Replace the entity in the strategy to reflect updates made.
     *
     * @param entity
     *            New entity.
     */
    void setEntity(T entity);
}
