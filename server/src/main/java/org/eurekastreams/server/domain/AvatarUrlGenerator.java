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


/**
 * Generates URLs for images. Should be renamed to ImageUrlGenerator.
 */
public class AvatarUrlGenerator
{
    /**
     * Default avatar image.
     */
    private final String defaultPersonAvatar = "/style/images/noPhoto75.png";

    /**
     * Default small avatar image.
     */
    private final String defaultPersonAvatarSmall = "/style/images/noPhoto50.png";

    /**
     * Default avatar image.
     */
    private final String defaultGroupAvatar = "/style/images/noPhoto75-group.png";

    /**
     * Default small avatar image.
     */
    private final String defaultGroupAvatarSmall = "/style/images/noPhoto50-group.png";

    /**
     * Default avatar image.
     */
    private final String defaultOrgAvatar = "/style/images/noPhoto75-org.png";

    /**
     * Default small avatar image.
     */
    private final String defaultOrgAvatarSmall = "/style/images/noPhoto50-org.png";

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

        if (entityType == EntityType.GROUP)
        {
            defaultAvatar = defaultGroupAvatar;
            defaultAvatarSmall = defaultGroupAvatarSmall;
        }
        else if (entityType == EntityType.ORGANIZATION)
        {
            defaultAvatar = defaultOrgAvatar;
            defaultAvatarSmall = defaultOrgAvatarSmall;
        }
        else
        {
            // Using Person as the catch-all, just in case.
            defaultAvatar = defaultPersonAvatar;
            defaultAvatarSmall = defaultPersonAvatarSmall;
        }
    }

    /**
     * Generates a url.
     *
     * @param entityId
     *            the id of the entity.
     * @param avatarId
     *            the id of the image.
     * @param prefix
     *            the prefix.
     * @return the url.
     */
    private String generateUrl(final Long entityId, final String avatarId, final String prefix)
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
        sb.append(getImageDirectory(entityId));
        sb.append("?img=");
        sb.append(prefix);
        sb.append(avatarId);
        return sb.toString();
    }

    /**
     * gets the image directory.
     *
     * @param id
     *            the entity id.
     * @return the image director.
     */
    public String getImageDirectory(final Long id)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("/eurekastreams/photos");

        return sb.toString();
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
        return generateUrl(entityId, avatarId, "o");
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
        return generateUrl(entityId, avatarId, "n");
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
        return generateUrl(entityId, avatarId, "s");
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

        //TODO: passing null in for the org id because it isn't needed anymore.  This signature needs to be cleaned up.
        return generateUrl(null, bannerId, "n");
    }
}
