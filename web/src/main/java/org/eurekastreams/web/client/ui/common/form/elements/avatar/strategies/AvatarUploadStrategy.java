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
package org.eurekastreams.web.client.ui.common.form.elements.avatar.strategies;

import org.eurekastreams.commons.exceptions.ExecutionException;
import org.eurekastreams.server.domain.AvatarEntity;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.web.client.events.ClearUploadedImageEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ClearUploadedImageEvent.ImageType;
import org.eurekastreams.web.client.events.data.DeletedGroupAvatarResponseEvent;
import org.eurekastreams.web.client.events.data.DeletedOrganizationAvatarResponseEvent;
import org.eurekastreams.web.client.events.data.DeletedPersonAvatarResponseEvent;
import org.eurekastreams.web.client.model.Deletable;
import org.eurekastreams.web.client.model.GroupAvatarModel;
import org.eurekastreams.web.client.model.OrganizationAvatarModel;
import org.eurekastreams.web.client.model.PersonAvatarModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget;

import com.google.gwt.user.client.ui.Widget;

/**
 * Generic Avatar Upload strategy.
 *
 * @param <T>
 *            The avatar entity type.
 */
public class AvatarUploadStrategy<T extends AvatarEntity> implements ImageUploadStrategy
{
    /**
     * The person.
     */
    private T entity;

    /**
     * The delete action.
     */
    @SuppressWarnings("unchecked")
    private Deletable deleteKey;

    /**
     * The resize action.
     */
    private String resizeKey;

    /**
     * The entity type.
     */
    private EntityType entityType;

    /**
     * Default constructor.
     *
     * @param inEntity
     *            the entity.
     * @param inResizeKey
     *            the resize action.
     * @param inEntityType
     *            the entity type.
     */
    public AvatarUploadStrategy(final T inEntity, final String inResizeKey, final EntityType inEntityType)
    {
        entity = inEntity;
        resizeKey = inResizeKey;
        entityType = inEntityType;
        switch (entityType)
        {
        case PERSON:
            deleteKey = PersonAvatarModel.getInstance();
            break;
        case GROUP:
            deleteKey = GroupAvatarModel.getInstance();
            break;
        case ORGANIZATION:
            deleteKey = OrganizationAvatarModel.getInstance();
            break;
        default:
            throw new ExecutionException("Entity type key invalid.");
        }

        Session.getInstance().getEventBus().addObservers(
                new Observer()
                {
                    public void update(final Object arg1)
                    {
                        Session.getInstance().getEventBus().notifyObservers(
                                new ClearUploadedImageEvent(entityType, ImageType.AVATAR));
                    }
                }, DeletedPersonAvatarResponseEvent.class, DeletedGroupAvatarResponseEvent.class,
                DeletedOrganizationAvatarResponseEvent.class);
    }

    /**
     * Gets the image id.
     *
     * @return the image id.
     */
    public String getImageId()
    {
        return entity.getAvatarId();
    }

    /**
     * Gets whether or not the strategy is resizable.
     *
     * @return the value.
     */
    public Boolean isResizable()
    {
        return Boolean.TRUE;
    }

    /**
     * sets the crop size.
     *
     * @param size
     *            the crop size.
     */
    public void setCropSize(final Integer size)
    {
        entity.setAvatarCropSize(size);
    }

    /**
     * Sets the X coord of the image crop.
     *
     * @param x
     *            the x coord.
     */
    public void setX(final Integer x)
    {
        entity.setAvatarCropX(x);
    }

    /**
     * Sets the y coord of the image crop.
     *
     * @param y
     *            the y coord.
     */
    public void setY(final Integer y)
    {
        entity.setAvatarCropY(y);
    }

    /**
     * Gets the id of the entity.
     *
     * @return the id.
     */
    public Long getId()
    {
        return entity.getId();
    }

    /**
     * Gets the crop size.
     *
     * @return the crop size.
     */
    public Integer getCropSize()
    {
        return entity.getAvatarCropSize();
    }

    /**
     * gets the crop X.
     *
     * @return the X.
     */
    public Integer getX()
    {
        return entity.getAvatarCropX();
    }

    /**
     * gets the crop Y.
     *
     * @return the crop y.
     */
    public Integer getY()
    {
        return entity.getAvatarCropY();
    }

    /**
     * Gets the image.
     *
     * @param imageId
     *            the image id.
     * @return the image.
     */
    public Widget getImage(final String imageId)
    {
        entity.setAvatarId(imageId);
        return new AvatarWidget(entity, entityType, AvatarWidget.Size.Normal, AvatarWidget.Background.White);
    }

    /**
     * Gets the param to send to the delete action.
     *
     * @return the params.
     */
    public Long getDeleteParam()
    {
        return entity.getId();
    }

    /**
     * Gets the delete action key.
     *
     * @return the delete action key.
     */
    @SuppressWarnings("unchecked")
    public Deletable getDeleteAction()
    {
        return deleteKey;
    }

    /**
     * Gets the delete action key.
     *
     * @return the delete action key.
     */
    public String getResizeAction()
    {
        return resizeKey;
    }

    /**
     * Get the entity type.
     * @return the entity type.
     */
    public EntityType getEntityType()
    {
        return entityType;
    }
    /**
     * Get the image type.
     * @return the image type.
     */
    public ImageType getImageType()
    {
        return ImageType.AVATAR;
    }

    /**
     * {@inheritDoc}.
     */
    public Long getImageEntityId()
    {
        return entity.getId();
    }

}
