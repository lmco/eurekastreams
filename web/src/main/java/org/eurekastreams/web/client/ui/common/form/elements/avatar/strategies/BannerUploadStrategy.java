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

import org.eurekastreams.server.domain.AvatarUrlGenerator;
import org.eurekastreams.server.domain.Bannerable;
import org.eurekastreams.server.domain.DomainGroup;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.search.modelview.DomainGroupModelView;
import org.eurekastreams.web.client.events.ClearUploadedImageEvent;
import org.eurekastreams.web.client.events.Observer;
import org.eurekastreams.web.client.events.ClearUploadedImageEvent.ImageType;
import org.eurekastreams.web.client.events.data.BaseDataResponseEvent;
import org.eurekastreams.web.client.events.data.DeleteGroupBannerResponseEvent;
import org.eurekastreams.web.client.model.Deletable;
import org.eurekastreams.web.client.model.GroupBannerModel;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * Banner uploadStratagy.
 * 
 * @param <T>
 *            The Type of the Bannerable Entity.
 */
public class BannerUploadStrategy<T extends Bannerable> implements ImageUploadStrategy<T>
{
    /**
     * the entity.
     */
    private final T entity;

    /**
     * Enum for type of Entity.
     */
    private EntityType entityType;

    /**
     * Name of action to call for delete.
     */
    private Deletable deleteAction;

    /**
     * EntityId of the entity being bannered.
     */
    private final Long entityId;

    /**
     * Default constructor.
     * 
     * @param inEntity
     *            the entity.
     * @param inEntityId
     *            id of the entity to upload the banner for.
     */
    public BannerUploadStrategy(final T inEntity, final Long inEntityId)
    {
        // TODO:Once the profile pages are entirely split from the domain models, refactor this to use DTO's correctly.
        entity = inEntity;
        entityId = inEntityId;

        if (entity.getClass() == DomainGroup.class || entity.getClass() == DomainGroupModelView.class)
        {
            entityType = EntityType.GROUP;
            deleteAction = GroupBannerModel.getInstance();
        }

        Session.getInstance().getEventBus().addObservers(new Observer<BaseDataResponseEvent<Bannerable>>()
        {
            public void update(final BaseDataResponseEvent<Bannerable> arg1)
            {
                Session.getInstance().getEventBus().notifyObservers(
                        new ClearUploadedImageEvent(entityType, ImageType.BANNER, arg1.getResponse()));
            }
        }, DeleteGroupBannerResponseEvent.class);
    }

    /**
     * Gets the crop size.
     * 
     * @return the crop size.
     */
    public Integer getCropSize()
    {
        return null;
    }

    /**
     * Gets the id of the entity.
     * 
     * @return the id.
     */
    public Long getId()
    {
        return entityId;
    }

    /**
     * Gets the image id.
     * 
     * @return the image id.
     */
    public String getImageId()
    {
        return entity.getBannerId();
    }

    /**
     * gets the crop X.
     * 
     * @return the X.
     */
    public Integer getX()
    {
        return null;
    }

    /**
     * gets the crop Y.
     * 
     * @return the crop y.
     */
    public Integer getY()
    {
        return null;
    }

    /**
     * Gets whether or not the strategy is resizable.
     * 
     * @return the value.
     */
    public Boolean isResizable()
    {
        return Boolean.FALSE;
    }

    /**
     * sets the crop size.
     * 
     * @param size
     *            the crop size.
     */
    public void setCropSize(final Integer size)
    {
        // Not used because the banner is not resizable.
    }

    /**
     * Sets the X coord of the image crop.
     * 
     * @param x
     *            the x coord.
     */
    public void setX(final Integer x)
    {
        // Not used because the banner is not resizable.
    }

    /**
     * Sets the y coord of the image crop.
     * 
     * @param y
     *            the y coord.
     */
    public void setY(final Integer y)
    {
        // Not used because the banner is not resizable.
    }

    /**
     * Gets the image.
     * 
     * @param imageId
     *            the image id.
     * @return the image.
     */
    public FlowPanel getImage(final String imageId)
    {
        AvatarUrlGenerator urlGenerator = new AvatarUrlGenerator(entityType);
        FlowPanel imageContainer = new FlowPanel();
        imageContainer.addStyleName(StaticResourceBundle.INSTANCE.coreCss().banner());
        imageContainer.add(new Image(urlGenerator.getBannerUrl(imageId)));

        return imageContainer;
    }

    /**
     * Gets the params to send to the delete action.
     * 
     * @return the params.
     */
    public Long getDeleteParam()
    {
        return entityId;
    }

    /**
     * Gets the delete action key.
     * 
     * @return the delete action key.
     */
    public Deletable getDeleteAction()
    {
        return deleteAction;
    }

    /**
     * Gets the resize action key. There is no resize action for Banner,
     * 
     * @return Returns a blank String.
     */
    public String getResizeAction()
    {
        return "";
    }

    /**
     * Get the entity type.
     * 
     * @return the entity type.
     */
    public EntityType getEntityType()
    {
        return entityType;
    }

    /**
     * Get the image type.
     * 
     * @return the image type.
     */
    public ImageType getImageType()
    {
        return ImageType.BANNER;
    }

    /**
     * {@inheritDoc}.
     */
    public Long getImageEntityId()
    {
        return entity.getBannerEntityId();
    }

    /**
     * {@inheritDoc}
     */
    public void setEntity(final T inEntity)
    {
        throw new UnsupportedOperationException("Updating entity not applicable to banner uploading.");
    }
}
