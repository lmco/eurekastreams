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
package org.eurekastreams.web.client.ui.common.avatar;

import org.eurekastreams.server.domain.AvatarEntity;
import org.eurekastreams.server.domain.AvatarUrlGenerator;
import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;

/**
 * Please use this if you want to represent an avatar on the screen. It has the benefit of looking up the appropriate
 * URL for you as well as displaying it according to the current avatar fashion.
 */
public class AvatarWidget extends Composite
{
    /**
     * The size of the avatar.
     */
    public enum Size
    {
        /** 75px. */
        Normal,
        /** 50px. */
        Small,
        /** 25px. */
        VerySmall
    }

    /**
     * The background the avatar will be living on.
     */
    public enum Background
    {
        /** #FFFFFF. */
        White,
        /** #CCCCCC. */
        Gray
    }

    /** The image. */
    private Image image = new Image();

    /** Avatar size. */
    private Size imageSize;

    /**
     * Constructor: Doesn't specify whose avatar, for setting dynamically later.
     * 
     * @param size
     *            the avatar size.
     */
    public AvatarWidget(final Size size)
    {
        imageSize = size;
        image.addStyleName(StaticResourceBundle.INSTANCE.coreCss().avatarImage());
        
        if (size.toString().toLowerCase().equals("small"))
        {
            image.addStyleName(StaticResourceBundle.INSTANCE.coreCss().avatarImageSmall());            
        }
        if (size.toString().toLowerCase().equals("verysmall"))
        {
            image.addStyleName(StaticResourceBundle.INSTANCE.coreCss().avatarImageVerySmall());            
        }
        if (size.toString().toLowerCase().equals("normal"))
        {
            image.addStyleName(StaticResourceBundle.INSTANCE.coreCss().avatarImageNormal());            
        }
        
        initWidget(image);
    }

    /**
     * Creates an avatar for the given entity.
     * 
     * @param entity
     *            the entity that has the avatar.
     * @param type
     *            the entity type.
     * @param size
     *            the avatar size.
     */
    public AvatarWidget(final AvatarEntity entity, final EntityType type, final Size size)
    {
        this(entity.getId(), entity.getAvatarId(), type, size);
    }

    /**
     * Creates an avatar for the given entity.
     * 
     * @param entityId
     *            the entity ID.
     * @param avatarId
     *            the ID of the avatar.
     * @param type
     *            the entity type.
     * @param size
     *            the avatar size.
     */
    public AvatarWidget(final long entityId, final String avatarId, final EntityType type, final Size size)
    {
        this(size);
        setAvatar(entityId, avatarId, type);
    }

    /**
     * Creates an avatar for the given entity.
     * 
     * @param entityId
     *            the entity ID.
     * @param avatarId
     *            the ID of the avatar.
     * @param type
     *            the entity type.
     * @param size
     *            the avatar size.
     * @param title
     *            the title.
     */
    public AvatarWidget(final long entityId, final String avatarId, final EntityType type, final Size size,
            final String title)
    {
        this(size);
        setAvatar(entityId, avatarId, type, title);
    }

    /**
     * Creates an avatar for the given entity.
     * 
     * @param entity
     *            the entity that has the avatar.
     * @param type
     *            the entity type.
     * @param size
     *            the avatar size.
     * @param title
     *            the title.
     */
    public AvatarWidget(final AvatarEntity entity, final EntityType type, final Size size, final String title)
    {
        this(entity.getId(), entity.getAvatarId(), type, size, title);
    }

    /**
     * Constructor: Doesn't specify whose avatar, for setting dynamically later.
     * 
     * @param size
     *            the avatar size.
     * @param bg
     *            the background color.
     */
    @Deprecated
    public AvatarWidget(final Size size, final Background bg)
    {
        this(size);
    }

    /**
     * Creates an avatar for the given entity.
     * 
     * @param entity
     *            the entity that has the avatar.
     * @param type
     *            the entity type.
     * @param size
     *            the avatar size.
     * @param bg
     *            the background color.
     */
    @Deprecated
    public AvatarWidget(final AvatarEntity entity, final EntityType type, final Size size, final Background bg)
    {
        this(entity.getId(), entity.getAvatarId(), type, size);
    }

    /**
     * Creates an avatar for the given entity.
     * 
     * @param entityId
     *            the entity ID.
     * @param avatarId
     *            the ID of the avatar.
     * @param type
     *            the entity type.
     * @param size
     *            the avatar size.
     * @param bg
     *            the background color.
     */
    @Deprecated
    public AvatarWidget(final long entityId, final String avatarId, final EntityType type, final Size size,
            final Background bg)
    {
        this(size);
        setAvatar(entityId, avatarId, type);
    }

    /**
     * Sets the avatar to be for the given entity (for dynamically changing it).
     * 
     * @param entityId
     *            the entity ID.
     * @param avatarId
     *            the ID of the avatar.
     * @param type
     *            the entity type.
     */
    public void setAvatar(final long entityId, final String avatarId, final EntityType type)
    {
        setAvatar(entityId, avatarId, type, "");
    }

    /**
     * Sets the avatar to be for the given entity (for dynamically changing it).
     * 
     * @param entityId
     *            the entity ID.
     * @param avatarId
     *            the ID of the avatar.
     * @param type
     *            the entity type.
     * @param title
     *            the title.
     */
    public void setAvatar(final long entityId, final String avatarId, final EntityType type, final String title)
    {
        AvatarUrlGenerator urlGen = new AvatarUrlGenerator(type);
        String imageUrl = imageSize.equals(Size.Normal) ? urlGen.getNormalAvatarUrl(entityId, avatarId) : urlGen
                .getSmallAvatarUrl(entityId, avatarId);
        image.setUrl(imageUrl);
        if (title.length() > 0)
        {
            image.setTitle(title);
        }
    }
}
