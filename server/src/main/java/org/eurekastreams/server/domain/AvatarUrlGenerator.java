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
package org.eurekastreams.server.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * Generates URLs for images. Should be renamed to ImageUrlGenerator.
 */
public class AvatarUrlGenerator
{
    /** Fallback normal size avatar. */
    private static final String FALLBACK_NORMAL_AVATAR = "/style/images/noPhoto75-system.png";

    /** Fallback small size avatar. */
    private static final String FALLBACK_SMALL_AVATAR = "/style/images/noPhoto50-system.png";

    /** Default avatars - normal size. */
    private static final Map<EntityType, String> DEFAULT_NORMAL_AVATARS_BY_TYPE = new HashMap<EntityType, String>()
    {
        {
            put(EntityType.PERSON, "/style/images/noPhoto75.png");
            put(EntityType.GROUP, "/style/images/noPhoto75-group.png");
            put(EntityType.APPLICATION, "/style/images/noPhoto75-app.png");
            put(EntityType.NOTSET, "/style/images/noPhoto75-system.png");
            put(null, FALLBACK_NORMAL_AVATAR);
        }
    };

    /** Default avatars - small size. */
    private static final Map<EntityType, String> DEFAULT_SMALL_AVATARS_BY_TYPE = new HashMap<EntityType, String>()
    {
        {
            put(EntityType.PERSON, "/style/images/noPhoto50.png");
            put(EntityType.GROUP, "/style/images/noPhoto50-group.png");
            put(EntityType.APPLICATION, "/style/images/noPhoto50-app.png");
            put(EntityType.NOTSET, "/style/images/noPhoto50-system.png");
            put(null, FALLBACK_SMALL_AVATAR);
        }
    };

    /**
     * the default avatar for the specified type.
     */
    private String defaultAvatar;

    /**
     * the default small avatar for the specified type.
     */
    private String defaultAvatarSmall;

    /**
     * Default banner image.
     */
    private final String defaultBanner = "/style/images/profile-banner-default.png";

    /**
     * The type of entity that we're getting the avatar for.
     */
    private EntityType entityType = EntityType.NOTSET;

    /**
     * Default constructor.
     *
     * @param inEntityType
     *            the type of the entity whose avatar will be displayed
     */
    public AvatarUrlGenerator(final EntityType inEntityType)
    {
        entityType = inEntityType;

        defaultAvatar = DEFAULT_NORMAL_AVATARS_BY_TYPE.get(inEntityType);
        if (defaultAvatar == null)
        {
            defaultAvatar = DEFAULT_NORMAL_AVATARS_BY_TYPE.get(null);
        }

        defaultAvatarSmall = DEFAULT_SMALL_AVATARS_BY_TYPE.get(inEntityType);
        if (defaultAvatarSmall == null)
        {
            defaultAvatarSmall = DEFAULT_SMALL_AVATARS_BY_TYPE.get(null);
        }
    }

    /**
     * Generates a url.
     * @param avatarId
     *            the id of the image.
     * @param prefix
     *            the prefix.
     * @return the url.
     */
    private String generateUrl(final String avatarId, final String prefix)
    {
        if (avatarId == null)
        {
            if (prefix == "s")
            {
                return defaultAvatarSmall;
            }
            else
            {
                return defaultAvatar;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append(getImageDirectory());
        sb.append("?img=");
        sb.append(prefix);
        sb.append(avatarId);
        return sb.toString();
    }

    /**
     * gets the image directory.
     *
     * @return the image directory.
     */
    public String getImageDirectory()
    {
        return "/eurekastreams/photos";
    }

    /**
     * Gets the original avatar url.
     *
     * @param entityId
     *            the entity id.
     * @param avatarId
     *            the avatar id.
     * @return the avatar url.
     */
    public String getOriginalAvatarUrl(final Long entityId, final String avatarId)
    {
        return generateUrl(avatarId, "o");
    }

    /**
     * Gets the normal avatar url.
     *
     * @param entityId
     *            the entity id.
     * @param avatarId
     *            the avatar id.
     * @return the url.
     */
    public String getNormalAvatarUrl(final Long entityId, final String avatarId)
    {
        return generateUrl(avatarId, "n");
    }

    /**
     * Gets the small avatar url.
     *
     * @param entityId
     *            the entity id.
     * @param avatarId
     *            the avatar id.
     * @return the url.
     */
    public String getSmallAvatarUrl(final Long entityId, final String avatarId)
    {
        return generateUrl(avatarId, "s");
    }

    /**
     * Gets the banner url.
     *
     * @param bannerId
     *            the banner id.
     * @return the banner url.
     */
    public String getBannerUrl(final String bannerId)
    {
        if (bannerId == null)
        {
            return defaultBanner;
        }

        // TODO: passing null in for the org id because it isn't needed anymore. This signature needs to be cleaned up.
        return generateUrl(bannerId, "n");
    }
}
