/*
 * Copyright (c) 2010-2011 Lockheed Martin Corporation
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
import org.eurekastreams.web.client.ui.common.avatar.AvatarWidget.Size;
import org.eurekastreams.web.client.ui.pages.master.StaticResourceBundle;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;

/**
 * Panel which displays an avatar, but does not make it link to anything.
 */
public class AvatarDisplayPanel extends Composite
{
    /**
     * Constructor.
     *
     * @param avatar
     *            Avatar image widget.
     */
    public AvatarDisplayPanel(final AvatarWidget avatar)
    {
        Panel main = new FlowPanel();
        main.addStyleName(StaticResourceBundle.INSTANCE.coreCss().avatar());
        main.add(avatar);
        initWidget(main);
    }

    /**
     * Constructor.
     *
     * @param entityId
     *            the entity ID.
     * @param avatarId
     *            the ID of the avatar.
     * @param entityType
     *            the entity type.
     * @param size
     *            the avatar size.
     */
    public AvatarDisplayPanel(final EntityType entityType, final long entityId, final String avatarId, final Size size)
    {
        this(new AvatarWidget(entityId, avatarId, entityType, size));
    }
}
