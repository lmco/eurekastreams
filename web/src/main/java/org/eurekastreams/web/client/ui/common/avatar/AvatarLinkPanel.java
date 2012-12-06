/*
 * Copyright (c) 2010-2012 Lockheed Martin Corporation
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

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.Page;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.server.search.modelview.PersonModelView;
import org.eurekastreams.web.client.history.CreateUrlRequest;
import org.eurekastreams.web.client.ui.Session;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.InlineHyperlink;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Displays an avatar and makes it link to the proper item's profile page.
 */
public class AvatarLinkPanel extends Composite 
{
    /**
     * Factory function for creating avatars.
     *
     * @param entity
     *            Person for whom to create an avatar.
     * @param size
     *            Size.
     * @param allowBadge
     *            If badgeable.
     * @return Appropriate widget.
     */
    public static Widget create(final PersonModelView entity, final Size size, final boolean allowBadge)
    {
        AvatarWidget avatarWidget = new AvatarWidget(entity.getAvatarId(), EntityType.PERSON, size,
                entity.getDisplayName());
        if (entity.isAccountLocked())
        {
            avatarWidget.addStyleName(StaticResourceBundle.INSTANCE.coreCss().avatar());
            return avatarWidget;
        }
        else
        {
            return new AvatarLinkPanel(EntityType.PERSON, entity.getUniqueId(), size, avatarWidget, allowBadge);
        }
    }

    /**
     * Factory function for creating avatars.
     *
     * @param entity
     *            Person for whom to create an avatar.
     * @param size
     *            Size.
     * @return Appropriate widget.
     */
    public static Widget create(final PersonModelView entity, final Size size)
    {
        return create(entity, size, true);
    }

    /**
     * Factory function for creating avatars.
     *
     * @param entity
     *            Entity for whom to create an avatar.
     * @param size
     *            Size.
     * @param allowBadge
     *            If badgeable.
     * @return Appropriate widget.
     */
    public static Widget create(final StreamEntityDTO entity, final Size size, final boolean allowBadge)
    {
        AvatarWidget avatarWidget = new AvatarWidget(entity.getAvatarId(), entity.getEntityType(), size,
                entity.getDisplayName());
        if (entity.isActive())
        {
            return new AvatarLinkPanel(entity.getEntityType(), entity.getUniqueId(), size, avatarWidget, allowBadge);
        }
        else
        {
            avatarWidget.addStyleName(StaticResourceBundle.INSTANCE.coreCss().avatar());
            return avatarWidget;
        }
    }

    /**
     * Factory function for creating avatars.
     *
     * @param entity
     *            Entity for whom to create an avatar.
     * @param size
     *            Size.
     * @return Appropriate widget.
     */
    public static Widget create(final StreamEntityDTO entity, final Size size)
    {
        return create(entity, size, true);
    }

    /**
     * Constructor.
     *
     * @param entityType
     *            Type of entity the avatar belongs to.
     * @param entityUniqueId
     *            Short name / account id of entity the avatar belongs to.
     * @param size
     *            the avatar size.
     * @param avatar
     *            Avatar image widget.
     * @param allowBadge
     *            If overlay badges should be created.
     */
    private AvatarLinkPanel(final EntityType entityType,
            final String entityUniqueId, final Size size,
                final AvatarWidget avatar, final boolean allowBadge) 
    {
        Panel main = new FlowPanel();
        main.addStyleName(StaticResourceBundle.INSTANCE.coreCss().avatar());
        initWidget(main);

        Page page;
        switch (entityType) 
        {
        case PERSON:
            page = Page.PEOPLE;
            break;
        case GROUP:
            page = Page.GROUPS;
            break;
        default:
            // this should never happen
            return;
        }

        String linkUrl = Session.getInstance().generateUrl(
                new CreateUrlRequest(page, entityUniqueId));

        Hyperlink link = new InlineHyperlink("", linkUrl);
        main.add(link);
        // Manipulate HTML to put the
        // class, whose CSS includes a badge image,
        // within the anchor tag for the gwt-InlineHyperlink
        if (allowBadge) 
        {
            Widget badgeForGroupCoordinator = AvatarBadgeManager.getInstance()
                        .createOverlay(entityUniqueId, size);
            link.getElement().appendChild(badgeForGroupCoordinator
                    .getElement());
        }
        link.getElement().appendChild(avatar.getElement());
    }

    /**
     * Constructor.
     *
     * @param entityType
     *            the entity type.
     * @param entityUniqueId
     *            Short name / account id of entity the avatar belongs to.
     * @param avatarId
     *            the ID of the avatar.
     * @param size
     *            the avatar size.
     * @param title
     *            the titleText.
     */
    public AvatarLinkPanel(final EntityType entityType,
            final String entityUniqueId, final String avatarId,
                final Size size, final String title) 
    {
        this(entityType, entityUniqueId, size, new AvatarWidget(avatarId,
                entityType, size, title), true);
    }

    /**
     * Constructor.
     *
     * @param entityType
     *            the entity type.
     * @param entityUniqueId
     *            Short name / account id of entity the avatar belongs to.
     * @param avatarId
     *            the ID of the avatar.
     * @param size
     *            the avatar size.
     */
    public AvatarLinkPanel(final EntityType entityType,
            final String entityUniqueId, final String avatarId,
                final Size size) 
    {
        this(entityType, entityUniqueId, size, new AvatarWidget(avatarId,
                entityType, size), true);
    }

    /**
     * Constructor.
     *
     * @param entityType
     *            the entity type
     * @param entityUniqueId
     *            Short name / account id of entity the avatar belongs to.
     * @param avatarId
     *            the ID of the avatar.
     * @param size
     *            the avatar size.
     * @param allowBadge
     *            If overlay badges should be created.
     */
    public AvatarLinkPanel(final EntityType entityType,
            final String entityUniqueId, final String avatarId,
                final Size size, final boolean allowBadge) 
    {
        this(entityType, entityUniqueId, size, new AvatarWidget(avatarId,
                entityType, size), allowBadge);
    }
}
