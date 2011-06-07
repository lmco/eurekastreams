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
package org.eurekastreams.web.client.ui.common.notification.dialog;

import org.eurekastreams.server.domain.EntityType;

import com.google.gwt.user.client.ui.HasText;

/**
 * Identifies a source.
 */
class Source
{
    /** The string-based unique identifier of the source. */
    private final String uniqueId;

    /** The type of the source. */
    private final EntityType entityType;

    /** The name to display for the source. */
    private String displayName;

    /** Unread count. */
    private int unreadCount;

    /** Parent "source" for cascading count decrements. */
    private final Source parent;

    /** Widget to display the source. */
    private HasText widget;

    /**
     * Constructor.
     *
     * @param inEntityType
     *            The type of the source.
     * @param inUniqueId
     *            The string-based unique identifier of the source.
     * @param inDisplayName
     *            The name to display for the source.
     * @param inParent
     *            Parent "source".
     */
    public Source(final EntityType inEntityType, final String inUniqueId, final String inDisplayName,
            final Source inParent)
    {
        entityType = inEntityType;
        uniqueId = inUniqueId;
        displayName = inDisplayName;
        parent = inParent;
    }

    /**
     * increment Unread Count.
     */
    public void incrementUnreadCount()
    {
        unreadCount++;
    }

    /**
     * decrement Unread Count.
     */
    public void decrementUnreadCount()
    {
        unreadCount--;
    }

    /**
     * @return the uniqueId
     */
    public String getUniqueId()
    {
        return uniqueId;
    }

    /**
     * @return the entityType
     */
    public EntityType getEntityType()
    {
        return entityType;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName()
    {
        return displayName;
    }


    /**
     * @return the unreadCount
     */
    public int getUnreadCount()
    {
        return unreadCount;
    }

    /**
     * @param inUnreadCount the unreadCount to set
     */
    public void setUnreadCount(final int inUnreadCount)
    {
        unreadCount = inUnreadCount;
    }

    /**
     * @return the parent
     */
    public Source getParent()
    {
        return parent;
    }

    /**
     * @return the widget
     */
    public HasText getWidget()
    {
        return widget;
    }

    /**
     * @param inWidget
     *            the widget to set
     */
    public void setWidget(final HasText inWidget)
    {
        widget = inWidget;
    }

    /**
     * @param inDisplayName
     *            the displayName to set
     */
    public void setDisplayName(final String inDisplayName)
    {
        displayName = inDisplayName;
    }
}