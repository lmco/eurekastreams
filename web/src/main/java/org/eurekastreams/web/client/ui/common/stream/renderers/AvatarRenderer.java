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
package org.eurekastreams.web.client.ui.common.stream.renderers;

import org.eurekastreams.server.domain.EntityType;
import org.eurekastreams.server.domain.stream.StreamEntityDTO;
import org.eurekastreams.web.client.ui.common.avatar.AvatarLinkPanel;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Background;
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;

import com.google.gwt.user.client.ui.Widget;

/**
 * Renders an avatar for a stream activity.
 *
 */
public class AvatarRenderer
{
    /**
     * Renders an avatar.
     * 
     * @param entity
     *            Entity whose avatar to render.
     * @return Widget displaying avatar.
     */
    public Widget render(final StreamEntityDTO entity)
    {
        return new AvatarLinkPanel(entity.getType(), entity.getUniqueIdentifier(), new AvatarWidget(entity.getId(),
                entity.getAvatarId(), entity.getType(), Size.Small, Background.White));
    }

    /**
     * Renders an avatar.
     *
     * @param id
     *            id of the user.
     * @param avatarId
     *            avatar id of the user.
     * @return the avatar panel.
     */
    public Widget render(final Long id, final String avatarId)
    {
        AvatarWidget avatar = new AvatarWidget(id, avatarId, EntityType.PERSON, Size.Small, Background.White);
        avatar.addStyleName("avatar");
        return avatar;
    }

    /**
     * Renders an avatar.
     *
     * @param id
     *            id of the user.
     * @param avatarId
     *            avatar id of the user.
     * @param type
     *            the type.
     * @return the avatar panel.
     */
    public Widget render(final Long id, final String avatarId, final EntityType type)
    {
        AvatarWidget avatar = new AvatarWidget(id, avatarId, type, Size.Small, Background.White);
        avatar.addStyleName("avatar");
        return avatar;
    }
}
